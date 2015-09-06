package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.forms.MachineUpsertForm;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.adapters.MachineWatcherAdapter;
import org.iatoki.judgels.michael.adapters.MachineWatcherConfAdapter;
import org.iatoki.judgels.michael.services.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherUtils;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.machine.createMachineView;
import org.iatoki.judgels.michael.views.html.machine.listMachinesView;
import org.iatoki.judgels.michael.views.html.machine.updateMachineGeneralView;
import org.iatoki.judgels.michael.views.html.machine.viewMachineView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class MachineController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final MachineService machineService;
    private final MachineWatcherService machineWatcherService;

    @Inject
    public MachineController(MachineService machineService, MachineWatcherService machineWatcherService) {
        this.machineService = machineService;
        this.machineWatcherService = machineWatcherService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listMachines(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listMachines(long page, String orderBy, String orderDir, String filterString) {
        Page<Machine> pageOfMachines = machineService.getPageOfMachines(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listMachinesView.render(pageOfMachines, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.list"), new InternalLink(Messages.get("commons.create"), routes.MachineController.createMachine()), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index())
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Machines");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result viewMachine(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        List<MachineWatcher> machineWatchers = machineWatcherService.getAllMachineWatchers(machine.getJid());
        ImmutableList.Builder<MachineWatcherAdapter> machineWatcherAdaptersBuilder = ImmutableList.builder();
        if (machine.getType().equals(MachineType.AWS_EC2)) {
            for (MachineWatcher machineWatcher : machineWatchers) {
                MachineWatcherConfAdapter factory = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, machineWatcher.getType());
                if (factory != null) {
                    machineWatcherAdaptersBuilder.add(factory.createMachineWatcherAdapter(machine, machineWatcher.getConf()));
                }
            }
        }

        LazyHtml content = new LazyHtml(viewMachineView.render(machine, machineWatcherAdaptersBuilder.build()));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.update"), routes.MachineController.updateMachineGeneral(machine.getId())), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.view"), routes.MachineController.viewMachine(machine.getId()))
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Machine - View");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createMachine() {
        Form<MachineUpsertForm> machineUpsertForm = Form.form(MachineUpsertForm.class);

        return showCreateMachine(machineUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateMachine() {
        Form<MachineUpsertForm> machineUpsertForm = Form.form(MachineUpsertForm.class).bindFromRequest();

        if (formHasErrors(machineUpsertForm)) {
            return showCreateMachine(machineUpsertForm);
        }

        MachineUpsertForm machineUpsertData = machineUpsertForm.get();
        machineService.createMachine(machineUpsertData.instanceName, machineUpsertData.displayName, machineUpsertData.baseDir, MachineType.valueOf(machineUpsertData.type), machineUpsertData.ipAddress);

        return redirect(routes.MachineController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateMachineGeneral(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        MachineUpsertForm machineUpsertData = new MachineUpsertForm();
        machineUpsertData.instanceName = machine.getInstanceName();
        machineUpsertData.displayName = machine.getDisplayName();
        machineUpsertData.baseDir = machine.getBaseDir();
        machineUpsertData.type = machine.getType().name();
        machineUpsertData.ipAddress = machine.getIpAddress();

        Form<MachineUpsertForm> machineUpsertForm = Form.form(MachineUpsertForm.class).fill(machineUpsertData);

        return showUpdateMachineGeneral(machineUpsertForm, machine);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateMachineGeneral(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        Form<MachineUpsertForm> machineUpsertForm = Form.form(MachineUpsertForm.class).bindFromRequest();

        if (formHasErrors(machineUpsertForm)) {
            return showUpdateMachineGeneral(machineUpsertForm, machine);
        }

        MachineUpsertForm machineUpsertData = machineUpsertForm.get();
        machineService.updateMachine(machine.getId(), machineUpsertData.instanceName, machineUpsertData.displayName, machineUpsertData.baseDir, MachineType.valueOf(machineUpsertData.type), machineUpsertData.ipAddress);

        return redirect(routes.MachineController.index());
    }

    private Result showCreateMachine(Form<MachineUpsertForm> machineUpsertForm) {
        LazyHtml content = new LazyHtml(createMachineView.render(machineUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.create"), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.create"), routes.MachineController.createMachine())
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Create");
        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateMachineGeneral(Form<MachineUpsertForm> machineUpsertForm, Machine machine) {
        LazyHtml content = new LazyHtml(updateMachineGeneralView.render(machineUpsertForm, machine.getId()));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.watcher"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.enter"), routes.MachineController.viewMachine(machine.getId())), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId()))
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Update");
        return MichaelControllerUtils.getInstance().lazyOk(content);
    }
}
