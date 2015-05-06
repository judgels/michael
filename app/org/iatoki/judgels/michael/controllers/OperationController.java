package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.messageView;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationService;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;
import org.iatoki.judgels.michael.ApplicationVersionService;
import org.iatoki.judgels.michael.CopyOperationCommand;
import org.iatoki.judgels.michael.CopyOperationRunForm;
import org.iatoki.judgels.michael.CopyOperationTypes;
import org.iatoki.judgels.michael.CopyOperationUpsertForm;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessKeyConf;
import org.iatoki.judgels.michael.MachineAccessNotFoundException;
import org.iatoki.judgels.michael.MachineAccessPasswordConf;
import org.iatoki.judgels.michael.MachineAccessService;
import org.iatoki.judgels.michael.MachineAccesses;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.Operation;
import org.iatoki.judgels.michael.OperationNotFoundException;
import org.iatoki.judgels.michael.OperationService;
import org.iatoki.judgels.michael.SingleMachineOperationCommand;
import org.iatoki.judgels.michael.SingleMachineOperationRunForm;
import org.iatoki.judgels.michael.SingleMachineOperationTypes;
import org.iatoki.judgels.michael.SingleMachineOperationUpsertForm;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.operations.createCopyOperationView;
import org.iatoki.judgels.michael.views.html.operations.createOperationView;
import org.iatoki.judgels.michael.views.html.operations.createSingleMachineOperationView;
import org.iatoki.judgels.michael.views.html.operations.listOperationsView;
import org.iatoki.judgels.michael.views.html.operations.runCopyOperationView;
import org.iatoki.judgels.michael.views.html.operations.runSingleMachineOperationView;
import org.iatoki.judgels.michael.views.html.operations.updateCopyOperationView;
import org.iatoki.judgels.michael.views.html.operations.updateSingleMachineOperationView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class OperationController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;
    private final ApplicationVersionService applicationVersionService;
    private final MachineService machineService;
    private final MachineAccessService machineAccessService;
    private final OperationService operationService;

    public OperationController(ApplicationService applicationService, ApplicationVersionService applicationVersionService, MachineService machineService, MachineAccessService machineAccessService, OperationService operationService) {
        this.applicationService = applicationService;
        this.applicationVersionService = applicationVersionService;
        this.machineService = machineService;
        this.machineAccessService = machineAccessService;
        this.operationService = operationService;
    }

    public Result index() {
        return listOperations(0, "id", "asc", "");
    }

    public Result listOperations(long page, String orderBy, String orderDir, String filterString) {
        Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listOperationsView.render(currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("operation.list"), new InternalLink(Messages.get("commons.create"), routes.OperationController.createOperation()), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operations");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result createOperation() {
        LazyHtml content = new LazyHtml(createOperationView.render());
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @AddCSRFToken
    public Result createSingleMachineOperation() {
        Form<SingleMachineOperationUpsertForm> form = Form.form(SingleMachineOperationUpsertForm.class);

        return showCreateSingleMachineOperation(form);
    }

    @RequireCSRFCheck
    public Result postCreateSingleMachineOperation() {
        Form<SingleMachineOperationUpsertForm> form = Form.form(SingleMachineOperationUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateSingleMachineOperation(form);
        } else {
            SingleMachineOperationUpsertForm operationUpsertForm = form.get();
            operationService.createSingleMachineOperation(operationUpsertForm.name, SingleMachineOperationTypes.valueOf(operationUpsertForm.type), operationUpsertForm.command);

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result createCopyOperation() {
        Form<CopyOperationUpsertForm> form = Form.form(CopyOperationUpsertForm.class);

        return showCreateCopyOperation(form);
    }

    @RequireCSRFCheck
    public Result postCreateCopyOperation() {
        Form<CopyOperationUpsertForm> form = Form.form(CopyOperationUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateCopyOperation(form);
        } else {
            CopyOperationUpsertForm operationUpsertForm = form.get();
            operationService.createCopyOperation(operationUpsertForm.name, CopyOperationTypes.valueOf(operationUpsertForm.type), operationUpsertForm.file1, operationUpsertForm.file2);

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result updateSingleMachineOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        SingleMachineOperationUpsertForm operationUpsertForm = new SingleMachineOperationUpsertForm();
        SingleMachineOperationCommand command = operation.getCommand(SingleMachineOperationCommand.class);
        operationUpsertForm.name = operation.getName();
        operationUpsertForm.type = operation.getType();
        operationUpsertForm.command = command.getCommand();

        Form<SingleMachineOperationUpsertForm> form = Form.form(SingleMachineOperationUpsertForm.class).fill(operationUpsertForm);

        return showUpdateSingleMachineOperation(form, operation);
    }

    @RequireCSRFCheck
    public Result postUpdateSingleMachineOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        Form<SingleMachineOperationUpsertForm> form = Form.form(SingleMachineOperationUpsertForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return showUpdateSingleMachineOperation(form, operation);
        } else {
            SingleMachineOperationUpsertForm operationUpsertForm = form.get();
            operationService.updateSingleMachineOperation(operation.getId(), operationUpsertForm.name, SingleMachineOperationTypes.valueOf(operationUpsertForm.type), operationUpsertForm.command);

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result updateCopyOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        CopyOperationUpsertForm operationUpsertForm = new CopyOperationUpsertForm();
        CopyOperationCommand command = operation.getCommand(CopyOperationCommand.class);
        operationUpsertForm.name = operation.getName();
        operationUpsertForm.type = operation.getType();
        operationUpsertForm.file1 = command.getFile1();
        operationUpsertForm.file2 = command.getFile2();

        Form<CopyOperationUpsertForm> form = Form.form(CopyOperationUpsertForm.class).fill(operationUpsertForm);

        return showUpdateCopyOperation(form, operation);
    }

    @RequireCSRFCheck
    public Result postUpdateCopyOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        Form<CopyOperationUpsertForm> form = Form.form(CopyOperationUpsertForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return showUpdateCopyOperation(form, operation);
        } else {
            CopyOperationUpsertForm operationUpsertForm = form.get();
            operationService.updateCopyOperation(operation.getId(), operationUpsertForm.name, CopyOperationTypes.valueOf(operationUpsertForm.type), operationUpsertForm.file1, operationUpsertForm.file2);

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result runSingleMachineOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        List<Machine> machines = machineService.findAll();
        List<Application> applications = applicationService.findAll();

        Form<SingleMachineOperationRunForm> form = Form.form(SingleMachineOperationRunForm.class);

        return showRunSingleMachineOperation(form, operation, machines, applications);
    }

    @RequireCSRFCheck
    public Result postRunSingleMachineOperation(long operationId) throws OperationNotFoundException, ApplicationVersionNotFoundException, MachineAccessNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        Form<SingleMachineOperationRunForm> form = Form.form(SingleMachineOperationRunForm.class).bindFromRequest();

        if (form.hasErrors()) {
            List<Machine> machines = machineService.findAll();
            List<Application> applications = applicationService.findAll();
            return showRunSingleMachineOperation(form, operation, machines, applications);
        } else {
            SingleMachineOperationRunForm operationRunForm = form.get();
            if (applicationService.existByApplicationJid(operationRunForm.applicationJid)) {
                Application application = applicationService.findByApplicationJid(operationRunForm.applicationJid);
                ApplicationVersion applicationVersion = applicationVersionService.findByApplicationVersionId(operationRunForm.versionId);
                if (application.getJid().equals(applicationVersion.getApplicationJid())) {
                    if (machineService.existByMachineJid(operationRunForm.machineJid)) {
                        Machine machine = machineService.findByMachineJid(operationRunForm.machineJid);
                        MachineAccess machineAccess = machineAccessService.findByMachineAccessId(operationRunForm.accessId);
                        if (machine.getJid().equals(machineAccess.getMachineJid())) {
                            if (operation.getType().equals(SingleMachineOperationTypes.SSH.name())) {
                                try {
                                    JSch jSch = new JSch();
                                    Session session = getMachineSession(jSch, machine, machineAccess);

                                    if (session != null) {
                                        Channel channel = session.openChannel("shell");
                                        InputStream input = channel.getInputStream();
                                        PrintStream printStream = new PrintStream(channel.getOutputStream());

                                        channel.connect();

                                        SingleMachineOperationCommand command = operation.getCommand(SingleMachineOperationCommand.class);
                                        String commandsString = command.getCommand();
                                        commandsString = commandsString.replace("<[(APP_NAME)]>", application.getName());
                                        commandsString = commandsString.replace("<[(APP_VERSION)]>", applicationVersion.getName());
                                        String[] commands = commandsString.split("\n");
                                        for (String commandString : commands) {
                                            printStream.println(commandString);
                                        }
                                        printStream.println("exit");
                                        printStream.flush();

                                        StringBuilder sb = new StringBuilder();
                                        while (channel.isConnected()) {
                                            int readByte = input.read();
                                            sb.append((char) readByte);
                                        }

                                        System.out.println(sb.toString());
                                        printStream.close();
                                        channel.disconnect();
                                        session.disconnect();

                                        return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationSuccess(operation.getId()));
                                    } else {
                                        return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
                                    }
                                } catch (JSchException | IOException e) {
                                    return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
                                }
                            } else {
                                form.reject("error.operationNotSupported");
                                List<Machine> machines = machineService.findAll();
                                List<Application> applications = applicationService.findAll();
                                return showRunSingleMachineOperation(form, operation, machines, applications);
                            }
                        } else {
                            form.reject("error.accessNotExist");
                            List<Machine> machines = machineService.findAll();
                            List<Application> applications = applicationService.findAll();
                            return showRunSingleMachineOperation(form, operation, machines, applications);
                        }
                    } else {
                        form.reject("error.machineNotExist");
                        List<Machine> machines = machineService.findAll();
                        List<Application> applications = applicationService.findAll();
                        return showRunSingleMachineOperation(form, operation, machines, applications);
                    }
                } else {
                    form.reject("error.versionNotExist");
                    List<Machine> machines = machineService.findAll();
                    List<Application> applications = applicationService.findAll();
                    return showRunSingleMachineOperation(form, operation, machines, applications);
                }
            } else {
                form.reject("error.applicationNotExist");
                List<Machine> machines = machineService.findAll();
                List<Application> applications = applicationService.findAll();
                return showRunSingleMachineOperation(form, operation, machines, applications);
            }
        }
    }

    @AddCSRFToken
    public Result runCopyOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        List<Machine> machines = machineService.findAll();
        List<Application> applications = applicationService.findAll();
        Form<CopyOperationRunForm> form = Form.form(CopyOperationRunForm.class);

        return showRunCopyOperation(form, operation, machines, applications);
    }

    @RequireCSRFCheck
    public Result postRunCopyOperation(long operationId) throws OperationNotFoundException, ApplicationVersionNotFoundException, MachineAccessNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        Form<CopyOperationRunForm> form = Form.form(CopyOperationRunForm.class).bindFromRequest();

        if (form.hasErrors()) {
            List<Machine> machines = machineService.findAll();
            List<Application> applications = applicationService.findAll();
            return showRunCopyOperation(form, operation, machines, applications);
        } else {
            CopyOperationRunForm operationRunForm = form.get();
            if (applicationService.existByApplicationJid(operationRunForm.applicationJid)) {
                Application application = applicationService.findByApplicationJid(operationRunForm.applicationJid);
                ApplicationVersion applicationVersion = applicationVersionService.findByApplicationVersionId(operationRunForm.versionId);
                if (application.getJid().equals(applicationVersion.getApplicationJid())) {
                    if ((machineService.existByMachineJid(operationRunForm.machineJid1)) && ((machineService.existByMachineJid(operationRunForm.machineJid2)))) {
                        Machine machine1 = machineService.findByMachineJid(operationRunForm.machineJid1);
                        Machine machine2 = machineService.findByMachineJid(operationRunForm.machineJid2);
                        MachineAccess machineAccess1 = machineAccessService.findByMachineAccessId(operationRunForm.accessId1);
                        MachineAccess machineAccess2 = machineAccessService.findByMachineAccessId(operationRunForm.accessId2);
                        if ((machine1.getJid().equals(machineAccess1.getMachineJid())) && (machine2.getJid().equals(machineAccess2.getMachineJid()))) {
                            if (operation.getType().equals(CopyOperationTypes.SCP.name())) {
                                try {
                                    JSch jSch = new JSch();
                                    Session session1 = getMachineSession(jSch, machine1, machineAccess1);
                                    Session session2 = getMachineSession(jSch, machine2, machineAccess2);

                                    if ((session1 != null) && (session2 != null)) {
                                        CopyOperationCommand command = operation.getCommand(CopyOperationCommand.class);

                                        String filename1 = machine1.getBaseDir() + command.getFile1();
                                        filename1 = filename1.replace("<[(APP_NAME)]>", application.getName());
                                        filename1 = filename1.replace("<[(APP_VERSION)]>", applicationVersion.getName());
                                        String filename2 = machine2.getBaseDir() + command.getFile2();
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
                                        channel.put(new FileInputStream(tempFile), FilenameUtils.getBaseName(filename2));
                                        channel.disconnect();

                                        session1.disconnect();
                                        session2.disconnect();

                                        tempFile.delete();

                                        return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationSuccess(operation.getId()));
                                    } else {
                                        return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
                                    }
                                } catch (JSchException | SftpException | IOException e) {
                                    e.printStackTrace();
                                    return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
                                }
                            } else {
                                form.reject("error.operationNotSupported");
                                List<Machine> machines = machineService.findAll();
                                List<Application> applications = applicationService.findAll();
                                return showRunCopyOperation(form, operation, machines, applications);
                            }
                        } else {
                            form.reject("error.accessNotExist");
                            List<Machine> machines = machineService.findAll();
                            List<Application> applications = applicationService.findAll();
                            return showRunCopyOperation(form, operation, machines, applications);
                        }
                    } else {
                        form.reject("error.machineNotExist");
                        List<Machine> machines = machineService.findAll();
                        List<Application> applications = applicationService.findAll();
                        return showRunCopyOperation(form, operation, machines, applications);
                    }
                } else {
                    form.reject("error.versionNotExist");
                    List<Machine> machines = machineService.findAll();
                    List<Application> applications = applicationService.findAll();
                    return showRunCopyOperation(form, operation, machines, applications);
                }
            } else {
                form.reject("error.applicationNotExist");
                List<Machine> machines = machineService.findAll();
                List<Application> applications = applicationService.findAll();
                return showRunCopyOperation(form, operation, machines, applications);
            }
        }
    }

    public Result operationSuccess(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);

        LazyHtml content = new LazyHtml(messageView.render(Messages.get("operation.success")));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.result.success"), routes.OperationController.operationSuccess(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Result");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result operationFail(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);

        LazyHtml content = new LazyHtml(messageView.render(Messages.get("operation.fail")));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.result.success"), routes.OperationController.operationSuccess(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Result");
        return ControllerUtils.getInstance().lazyOk(content);

    }

    private Result showCreateSingleMachineOperation(Form<SingleMachineOperationUpsertForm> form) {
        LazyHtml content = new LazyHtml(createSingleMachineOperationView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation()),
              new InternalLink(Messages.get("operation.singleMachine.create"), routes.OperationController.createSingleMachineOperation())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateCopyOperation(Form<CopyOperationUpsertForm> form) {
        LazyHtml content = new LazyHtml(createCopyOperationView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation()),
              new InternalLink(Messages.get("operation.copy.create"), routes.OperationController.createSingleMachineOperation())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateSingleMachineOperation(Form<SingleMachineOperationUpsertForm> form, Operation operation) {
        LazyHtml content = new LazyHtml(updateSingleMachineOperationView.render(form, operation.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.update"), routes.OperationController.updateSingleMachineOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateCopyOperation(Form<CopyOperationUpsertForm> form, Operation operation) {
        LazyHtml content = new LazyHtml(updateCopyOperationView.render(form, operation.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.update"), routes.OperationController.updateSingleMachineOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showRunSingleMachineOperation(Form<SingleMachineOperationRunForm> form, Operation operation, List<Machine> machines, List<Application> applications) {
        LazyHtml content = new LazyHtml(runSingleMachineOperationView.render(form, operation.getId(), machines, applications));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.run"), routes.OperationController.runSingleMachineOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Run");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showRunCopyOperation(Form<CopyOperationRunForm> form, Operation operation, List<Machine> machines, List<Application> applications) {
        LazyHtml content = new LazyHtml(runCopyOperationView.render(form, operation.getId(), machines, applications));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.run"), routes.OperationController.runCopyOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Run");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Session getMachineSession(JSch jSch, Machine machine, MachineAccess machineAccess) throws IOException, JSchException, MachineAccessNotFoundException {
        Session session = null;
        if (MachineAccesses.KEY.name().equals(machineAccess.getType())) {
            MachineAccessKeyConf conf = machineAccessService.getMachineAccessConf(machineAccess.getId(), MachineAccessKeyConf.class);
            File temp = File.createTempFile("tempkey", ".pem");
            FileUtils.writeStringToFile(temp, conf.getKey());
            temp.setReadOnly();

            jSch.addIdentity(temp.getCanonicalPath());
            session = jSch.getSession(conf.getUsername(), machine.getIpAddress(), conf.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            temp.delete();
        } else if (MachineAccesses.PASSWORD.name().equals(machineAccess.getType())) {
            MachineAccessPasswordConf conf = machineAccessService.getMachineAccessConf(machineAccess.getId(), MachineAccessPasswordConf.class);

            session = jSch.getSession(conf.getUsername(), machine.getIpAddress(), conf.getPort());
            session.setPassword(conf.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
        }

        return session;
    }
}
