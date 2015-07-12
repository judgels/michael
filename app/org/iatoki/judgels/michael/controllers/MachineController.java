package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.BaseController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.controllers.forms.MachineUpsertForm;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherAdapter;
import org.iatoki.judgels.michael.MachineWatcherConfAdapter;
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
public final class MachineController extends BaseController {

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
        Page<Machine> currentPage = machineService.pageMachines(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listMachinesView.render(currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.list"), new InternalLink(Messages.get("commons.create"), routes.MachineController.createMachine()), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result viewMachine(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        List<MachineWatcher> machineWatchers = machineWatcherService.findAll(machine.getJid());
        ImmutableList.Builder<MachineWatcherAdapter> machineWatcherAdapterBuilder = ImmutableList.builder();
        if (machine.getType().equals(MachineType.AWS_EC2)) {
            for (MachineWatcher machineWatcher : machineWatchers) {
                MachineWatcherConfAdapter factory = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, machineWatcher.getType());
                if (factory != null) {
                    machineWatcherAdapterBuilder.add(factory.createMachineWatcherAdapter(machine, machineWatcher.getConf()));
                }
            }
        }

        LazyHtml content = new LazyHtml(viewMachineView.render(machine, machineWatcherAdapterBuilder.build()));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.update"), routes.MachineController.updateMachineGeneral(machine.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.view"), routes.MachineController.viewMachine(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - View");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createMachine() {
        Form<MachineUpsertForm> form = Form.form(MachineUpsertForm.class);

        return showCreateMachine(form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateMachine() {
        Form<MachineUpsertForm> form = Form.form(MachineUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateMachine(form);
        } else {
            MachineUpsertForm machineUpsertForm = form.get();
            machineService.createMachine(machineUpsertForm.instanceName, machineUpsertForm.displayName, machineUpsertForm.baseDir, MachineType.valueOf(machineUpsertForm.type), machineUpsertForm.ipAddress);

            return redirect(routes.MachineController.index());
        }
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateMachineGeneral(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        MachineUpsertForm machineUpsertForm = new MachineUpsertForm();
        machineUpsertForm.instanceName = machine.getInstanceName();
        machineUpsertForm.displayName = machine.getDisplayName();
        machineUpsertForm.baseDir = machine.getBaseDir();
        machineUpsertForm.type = machine.getType().name();
        machineUpsertForm.ipAddress = machine.getIpAddress();

        Form<MachineUpsertForm> form = Form.form(MachineUpsertForm.class).fill(machineUpsertForm);

        return showUpdateMachineGeneral(form, machine);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateMachineGeneral(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineUpsertForm> form = Form.form(MachineUpsertForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return showUpdateMachineGeneral(form, machine);
        } else {
            MachineUpsertForm machineUpsertForm = form.get();
            machineService.updateMachine(machine.getId(), machineUpsertForm.instanceName, machineUpsertForm.displayName, machineUpsertForm.baseDir, MachineType.valueOf(machineUpsertForm.type), machineUpsertForm.ipAddress);

            return redirect(routes.MachineController.index());
        }
    }

    private Result showCreateMachine(Form<MachineUpsertForm> form) {
        LazyHtml content = new LazyHtml(createMachineView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.create"), routes.MachineController.createMachine())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateMachineGeneral(Form<MachineUpsertForm> form, Machine machine) {
        LazyHtml content = new LazyHtml(updateMachineGeneralView.render(form, machine.getId()));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.watcher"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.enter"), routes.MachineController.viewMachine(machine.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }
}
