package org.iatoki.judgels.michael.adapters.impls;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessNotFoundException;
import org.iatoki.judgels.michael.MachineAccessUtils;
import org.iatoki.judgels.michael.OperationExecTerminationType;
import org.iatoki.judgels.michael.OperationOneMachineExecConf;
import org.iatoki.judgels.michael.adapters.OperationAdapter;
import org.iatoki.judgels.michael.forms.OperationOneMachineExecConfForm;
import org.iatoki.judgels.michael.forms.OperationOneMachineExecForm;
import org.iatoki.judgels.michael.forms.OperationOneMachineOneAppExecForm;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.views.html.operation.oneMachineExecOperationConfView;
import org.iatoki.judgels.michael.views.html.operation.oneMachineOneAppExecOperationView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public final class OperationOneMachineOneAppExecAdapter implements OperationAdapter {

    @Override
    public Form generateConfForm() {
        return Form.form(OperationOneMachineExecConfForm.class);
    }

    @Override
    public Form generateConfForm(String name, String conf) {
        OperationOneMachineExecConf execConf = new Gson().fromJson(conf, OperationOneMachineExecConf.class);
        OperationOneMachineExecConfForm confForm = new OperationOneMachineExecConfForm();
        confForm.name = name;
        confForm.command = execConf.command;
        confForm.terminationType = execConf.terminationType;
        confForm.terminationValue = execConf.terminationValue;

        Form<OperationOneMachineExecConfForm> form = Form.form(OperationOneMachineExecConfForm.class).fill(confForm);
        return form;
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return oneMachineExecOperationConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindConfFormFromRequest(Http.Request request) {
        return Form.form(OperationOneMachineExecConfForm.class).bindFromRequest(request);
    }

    @Override
    public String getNameFromConfForm(Form form) {
        Form<OperationOneMachineExecConfForm> realForm = (Form<OperationOneMachineExecConfForm>) form;
        OperationOneMachineExecConfForm confForm = realForm.get();

        return confForm.name;
    }

    @Override
    public String processConfForm(Form form) {
        Form<OperationOneMachineExecConfForm> realForm = (Form<OperationOneMachineExecConfForm>) form;
        OperationOneMachineExecConfForm confForm = realForm.get();
        OperationOneMachineExecConf conf = new OperationOneMachineExecConf();
        conf.command = confForm.command;
        conf.terminationType = confForm.terminationType;
        conf.terminationValue = confForm.terminationValue;

        return new Gson().toJson(conf);
    }

    @Override
    public Form generateRunForm() {
        return Form.form(OperationOneMachineExecForm.class);
    }

    @Override
    public Html getRunHtml(Form form, Call target, String submitLabel, List<Machine> machineList, List<Application> applicationList) {
        return oneMachineOneAppExecOperationView.render(form, target, submitLabel, machineList, applicationList);
    }

    @Override
    public Form bindRunFormFromRequest(Http.Request request) {
        return Form.form(OperationOneMachineOneAppExecForm.class).bindFromRequest(request);
    }

    @Override
    public boolean runOperation(Form form, MachineService machineService, MachineAccessService machineAccessService, ApplicationService applicationService, ApplicationVersionService applicationVersionService, String conf) {
        Form<OperationOneMachineOneAppExecForm> realForm = (Form<OperationOneMachineOneAppExecForm>) form;
        OperationOneMachineOneAppExecForm execForm = realForm.get();

        OperationOneMachineExecConf execConf = new Gson().fromJson(conf, OperationOneMachineExecConf.class);
        if (EnumUtils.isValidEnum(OperationExecTerminationType.class, execConf.terminationType)) {
            return false;
        }

        if (!applicationService.applicationExistsByJid(execForm.applicationJid)) {
            return false;
        }

        Application application = applicationService.findByApplicationJid(execForm.applicationJid);
        ApplicationVersion applicationVersion;
        try {
            applicationVersion = applicationVersionService.findApplicationVersionById(execForm.versionId);
        } catch (ApplicationVersionNotFoundException e) {
            return false;
        }
        if (!application.getJid().equals(applicationVersion.getApplicationJid())) {
            return false;
        }

        if (machineService.machineExistsByJid(execForm.machineJid)) {
            return false;
        }

        Machine machine = machineService.findMachineByJid(execForm.machineJid);
        MachineAccess machineAccess;
        try {
            machineAccess = machineAccessService.findMachineAccessById(execForm.accessId);
        } catch (MachineAccessNotFoundException e) {
            return false;
        }

        if (!machine.getJid().equals(machineAccess.getMachineJid())) {
            return false;
        }

        JSch jSch = new JSch();
        Session session;
        try {
            session = MachineAccessUtils.getMachineSession(machineAccessService, jSch, machine, machineAccess);
        } catch (MachineAccessNotFoundException | JSchException | IOException e) {
            return false;
        }

        if (session == null) {
            return false;
        }

        try {
            Channel channel = session.openChannel("shell");
            try {
                InputStream input = channel.getInputStream();
                PrintStream printStream = new PrintStream(channel.getOutputStream());

                channel.connect();

                String commandsString = execConf.command;
                commandsString = commandsString.replace("<[(BASE_DIR)]>", machine.getBaseDir());
                commandsString = commandsString.replace("<[(APP_NAME)]>", application.getName());
                commandsString = commandsString.replace("<[(APP_VERSION)]>", applicationVersion.getName());
                String[] commands = commandsString.split("\n");
                for (String commandString : commands) {
                    printStream.println(commandString);
                }
                printStream.flush();
                switch (OperationExecTerminationType.valueOf(execConf.terminationType)) {
                    case AVAILABLE_TERMINATION_KEY:
                        final String terminationValue = execConf.terminationValue;
                        printStream.println(terminationValue);
                        printStream.flush();
                        StringBuilder sb = new StringBuilder();
                        boolean end = false;
                        while (!end) {
                            int readByte = input.read();
                            sb.append((char) readByte);
                            if ((readByte == (int) '#') && (sb.toString().equals(terminationValue))) {
                                end = true;
                            }
                            if (sb.length() == terminationValue.length()) {
                                sb.deleteCharAt(0);
                            }
                        }
                        break;
                    case GENERATED_TERMINATION_KEY:
                        final String terminationKey = "#$JUDGELS_END_SHELL$#";
                        printStream.println(terminationKey);
                        printStream.flush();
                        StringBuilder sb2 = new StringBuilder();
                        boolean end2 = false;
                        while (!end2) {
                            int readByte = input.read();
                            sb2.append((char) readByte);
                            if ((readByte == (int) '#') && (sb2.toString().equals(terminationKey))) {
                                end2 = true;
                            }
                            if (sb2.length() == terminationKey.length()) {
                                sb2.deleteCharAt(0);
                            }
                        }
                        break;
                    case EXIT_TERMINATION_KEY:
                        printStream.println("exit");
                        printStream.flush();
                        while (channel.isConnected()) {
                        }
                        break;
                    case TIME_TERMINATION:
                        try {
                            Thread.sleep(Long.valueOf(execConf.terminationValue));
                        } catch (InterruptedException e) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                }

                printStream.close();
            } finally {
                channel.disconnect();
            }
        } catch (JSchException | IOException e) {
            return false;
        } finally {
            session.disconnect();
        }
        return true;
    }
}
