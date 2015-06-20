package org.iatoki.judgels.michael;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FilenameUtils;
import org.iatoki.judgels.michael.controllers.forms.OperationTwoMachineCopyConfForm;
import org.iatoki.judgels.michael.controllers.forms.OperationTwoMachineCopyForm;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.views.html.operation.twoMachineCopyOperationConfView;
import org.iatoki.judgels.michael.views.html.operation.twoMachineCopyOperationView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public final class OperationTwoMachineCopyAdapter implements OperationAdapter {

    @Override
    public Form generateConfForm() {
        return Form.form(OperationTwoMachineCopyConfForm.class);
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return twoMachineCopyOperationConfView.render(form, target, submitLabel);
    }

    @Override
    public Form generateConfForm(String name, String conf) {
        OperationTwoMachineCopyConf copyConf = new Gson().fromJson(conf, OperationTwoMachineCopyConf.class);
        OperationTwoMachineCopyConfForm confForm = new OperationTwoMachineCopyConfForm();
        confForm.name = name;
        confForm.sourceFile = copyConf.sourceFile;
        confForm.targetFile = copyConf.targetFile;

        Form<OperationTwoMachineCopyConfForm> form = Form.form(OperationTwoMachineCopyConfForm.class).fill(confForm);
        return form;
    }

    @Override
    public Form bindConfFormFromRequest(Http.Request request) {
        return Form.form(OperationTwoMachineCopyConfForm.class).bindFromRequest(request);
    }

    @Override
    public String getNameFromConfForm(Form form) {
        Form<OperationTwoMachineCopyConfForm> realForm = (Form<OperationTwoMachineCopyConfForm>)form;
        OperationTwoMachineCopyConfForm confForm = realForm.get();

        return confForm.name;
    }

    @Override
    public String processConfForm(Form form) {
        Form<OperationTwoMachineCopyConfForm> realForm = (Form<OperationTwoMachineCopyConfForm>)form;
        OperationTwoMachineCopyConfForm confForm = realForm.get();
        OperationTwoMachineCopyConf conf = new OperationTwoMachineCopyConf();
        conf.sourceFile = confForm.sourceFile;
        conf.targetFile = confForm.targetFile;

        return new Gson().toJson(conf);
    }

    @Override
    public Form generateRunForm() {
        return Form.form(OperationTwoMachineCopyForm.class);
    }

    @Override
    public Html getRunHtml(Form form, Call target, String submitLabel, List<Machine> machineList, List<Application> applicationList) {
        return twoMachineCopyOperationView.render(form, target, submitLabel, machineList, applicationList);
    }

    @Override
    public Form bindRunFormFromRequest(Http.Request request) {
        return Form.form(OperationTwoMachineCopyForm.class).bindFromRequest(request);
    }

    @Override
    public boolean runOperation(Form form, MachineService machineService, MachineAccessService machineAccessService, ApplicationService applicationService, ApplicationVersionService applicationVersionService, String conf) {
        Form<OperationTwoMachineCopyForm> realForm = (Form<OperationTwoMachineCopyForm>)form;
        OperationTwoMachineCopyForm copyForm = realForm.get();

        OperationTwoMachineCopyConf copyConf = new Gson().fromJson(conf, OperationTwoMachineCopyConf.class);
        try {
            if (applicationService.existByApplicationJid(copyForm.applicationJid)) {
                Application application = applicationService.findByApplicationJid(copyForm.applicationJid);
                ApplicationVersion applicationVersion = applicationVersionService.findByApplicationVersionId(copyForm.versionId);
                if (application.getJid().equals(applicationVersion.getApplicationJid())) {
                    if (machineService.existByMachineJid(copyForm.machineJid1)) {
                        Machine machine1 = machineService.findByMachineJid(copyForm.machineJid1);
                        MachineAccess machineAccess1 = machineAccessService.findByMachineAccessId(copyForm.accessId1);

                        if (machine1.getJid().equals(machineAccess1.getMachineJid())) {
                            if (machineService.existByMachineJid(copyForm.machineJid2)) {
                                Machine machine2 = machineService.findByMachineJid(copyForm.machineJid2);
                                MachineAccess machineAccess2 = machineAccessService.findByMachineAccessId(copyForm.accessId2);

                                if (machine2.getJid().equals(machineAccess2.getMachineJid())) {
                                    JSch jSch = new JSch();
                                    Session session1 = MachineAccessUtils.getMachineSession(machineAccessService, jSch, machine1, machineAccess1);
                                    Session session2 = MachineAccessUtils.getMachineSession(machineAccessService, jSch, machine2, machineAccess2);

                                    if ((session1 != null) && (session2 != null)) {
                                        String filename1 = machine1.getBaseDir() + copyConf.sourceFile;
                                        filename1 = filename1.replace("<[(APP_NAME)]>", application.getName());
                                        filename1 = filename1.replace("<[(APP_VERSION)]>", applicationVersion.getName());
                                        String filename2 = machine2.getBaseDir() + copyConf.targetFile;
                                        filename2 = filename2.replace("<[(APP_NAME)]>", application.getName());
                                        filename2 = filename2.replace("<[(APP_VERSION)]>", applicationVersion.getName());
                                        File tempFile = File.createTempFile(FilenameUtils.getBaseName(filename2), FilenameUtils.getExtension(filename2));

                                        ChannelSftp channel = (ChannelSftp) session1.openChannel("sftp");
                                        channel.connect();
                                        channel.cd(FilenameUtils.getFullPathNoEndSeparator(filename1));
                                        channel.get(FilenameUtils.getName(filename1), new FileOutputStream(tempFile));
                                        channel.disconnect();

                                        channel = (ChannelSftp) session2.openChannel("sftp");
                                        channel.connect();
                                        channel.cd(FilenameUtils.getFullPathNoEndSeparator(filename2));
                                        channel.put(new FileInputStream(tempFile), FilenameUtils.getName(filename2));
                                        channel.disconnect();

                                        session1.disconnect();
                                        session2.disconnect();

                                        tempFile.delete();

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
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (ApplicationVersionNotFoundException | MachineAccessNotFoundException | JSchException | SftpException | IOException e) {
            return false;
        }
    }
}
