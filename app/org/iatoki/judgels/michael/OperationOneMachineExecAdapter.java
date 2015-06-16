package org.iatoki.judgels.michael;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.michael.views.html.operations.oneMachineExecOperationConfView;
import org.iatoki.judgels.michael.views.html.operations.oneMachineExecOperationView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public final class OperationOneMachineExecAdapter implements OperationAdapter {

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
        Form<OperationOneMachineExecConfForm> realForm = (Form<OperationOneMachineExecConfForm>)form;
        OperationOneMachineExecConfForm confForm = realForm.get();

        return confForm.name;
    }

    @Override
    public String processConfForm(Form form) {
        Form<OperationOneMachineExecConfForm> realForm = (Form<OperationOneMachineExecConfForm>)form;
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
        return oneMachineExecOperationView.render(form, target, submitLabel, machineList, applicationList);
    }

    @Override
    public Form bindRunFormFromRequest(Http.Request request) {
        return Form.form(OperationOneMachineExecForm.class).bindFromRequest(request);
    }

    @Override
    public boolean runOperation(Form form, MachineService machineService, MachineAccessService machineAccessService, ApplicationService applicationService, ApplicationVersionService applicationVersionService, String conf) {
        Form<OperationOneMachineExecForm> realForm = (Form<OperationOneMachineExecForm>)form;
        OperationOneMachineExecForm execForm = realForm.get();

        OperationOneMachineExecConf execConf = new Gson().fromJson(conf, OperationOneMachineExecConf.class);
        if (EnumUtils.isValidEnum(OperationExecTerminationType.class, execConf.terminationType)) {
            try {
                if (machineService.existByMachineJid(execForm.machineJid)) {
                    Machine machine = machineService.findByMachineJid(execForm.machineJid);
                    MachineAccess machineAccess = machineAccessService.findByMachineAccessId(execForm.accessId);

                    if (machine.getJid().equals(machineAccess.getMachineJid())) {
                        JSch jSch = new JSch();
                        Session session = MachineAccessUtils.getMachineSession(machineAccessService, jSch, machine, machineAccess);

                        if (session != null) {
                            try {
                                Channel channel = session.openChannel("shell");
                                try {
                                    InputStream input = channel.getInputStream();
                                    PrintStream printStream = new PrintStream(channel.getOutputStream());

                                    channel.connect();

                                    String commandsString = execConf.command;
                                    commandsString = commandsString.replace("<[(BASE_DIR)]>", machine.getBaseDir());
                                    String[] commands = commandsString.split("\n");
                                    for (String commandString : commands) {
                                        printStream.println(commandString);
                                    }
                                    printStream.flush();
                                    switch (OperationExecTerminationType.valueOf(execConf.terminationType)) {
                                        case AVAILABLE_TERMINATION_KEY: {
                                            final String terminationKey = execConf.terminationValue;
                                            printStream.println(terminationKey);
                                            printStream.flush();
                                            StringBuilder sb = new StringBuilder();
                                            boolean end = false;
                                            while (!end) {
                                                int readByte = input.read();
                                                sb.append((char) readByte);
                                                if ((readByte == (int) '#') && (sb.toString().equals(terminationKey))) {
                                                    end = true;
                                                }
                                                if (sb.length() == terminationKey.length()) {
                                                    sb.deleteCharAt(0);
                                                }
                                            }
                                            break;
                                        }
                                        case GENERATED_TERMINATION_KEY: {
                                            final String terminationKey = "#$JUDGELS_END_SHELL$#";
                                            printStream.println(terminationKey);
                                            printStream.flush();
                                            StringBuilder sb = new StringBuilder();
                                            boolean end = false;
                                            while (!end) {
                                                int readByte = input.read();
                                                sb.append((char) readByte);
                                                if ((readByte == (int) '#') && (sb.toString().equals(terminationKey))) {
                                                    end = true;
                                                }
                                                if (sb.length() == terminationKey.length()) {
                                                    sb.deleteCharAt(0);
                                                }
                                            }
                                            break;
                                        }
                                        case EXIT_TERMINATION_KEY: {
                                            printStream.println("exit");
                                            printStream.flush();
                                            while (channel.isConnected()) ;
                                            break;
                                        }
                                        case TIME_TERMINATION: {
                                            try {
                                                Thread.sleep(Long.valueOf(execConf.terminationValue));
                                            } catch (InterruptedException e) {
                                                return false;
                                            }
                                            break;
                                        }
                                        default: return false;
                                    }

                                    printStream.close();
                                } finally {
                                    channel.disconnect();
                                }
                            } finally {
                                session.disconnect();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (MachineAccessNotFoundException | JSchException | IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
