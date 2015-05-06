package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessService;
import org.iatoki.judgels.michael.MachineAccessUpsertKeyForm;
import org.iatoki.judgels.michael.MachineAccessUpsertPasswordForm;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.machines.accesses.createKeyMachineAccessView;
import org.iatoki.judgels.michael.views.html.machines.accesses.createMachineAccessView;
import org.iatoki.judgels.michael.views.html.machines.accesses.createPasswordMachineAccessView;
import org.iatoki.judgels.michael.views.html.machines.accesses.listMachineAccessesView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class MachineAccessController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final MachineService machineService;
    private final MachineAccessService machineAccessService;

    public MachineAccessController(MachineService machineService, MachineAccessService machineAccessService) {
        this.machineService = machineService;
        this.machineAccessService = machineAccessService;
    }

    public Result viewMachineAccesses(long machineId) throws MachineNotFoundException {
        return listMachineAccesses(machineId, 0, "id", "asc", "");
    }

    public Result listMachineAccesses(long machineId, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listMachineAccessesView.render(machine.getId(), currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.access.list"), new InternalLink(Messages.get("commons.create"), routes.MachineAccessController.createMachineAccess(machine.getId())), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.watcher"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Accesses");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result createMachineAccess(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);

        LazyHtml content = new LazyHtml(createMachineAccessView.render(machineId));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.create"), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.create"), routes.MachineAccessController.createMachineAccess(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @AddCSRFToken
    public Result createKeyMachineAccess(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineAccessUpsertKeyForm> form = Form.form(MachineAccessUpsertKeyForm.class);

        return showCreateKeyMachineAccess(machine, form);
    }

    @RequireCSRFCheck
    public Result postCreateKeyMachineAccess(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineAccessUpsertKeyForm> form = Form.form(MachineAccessUpsertKeyForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateKeyMachineAccess(machine, form);
        } else {
            MachineAccessUpsertKeyForm machineAccessUpsertKeyForm = form.get();
            machineAccessService.createKeyMachineAccess(machine.getJid(), machineAccessUpsertKeyForm.name, machineAccessUpsertKeyForm.username, machineAccessUpsertKeyForm.key, machineAccessUpsertKeyForm.port);

            return redirect(routes.MachineAccessController.viewMachineAccesses(machine.getId()));
        }
    }

    @AddCSRFToken
    public Result createPasswordMachineAccess(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineAccessUpsertPasswordForm> form = Form.form(MachineAccessUpsertPasswordForm.class);

        return showCreatePasswordMachineAccess(machine, form);
    }

    @RequireCSRFCheck
    public Result postCreatePasswordMachineAccess(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineAccessUpsertPasswordForm> form = Form.form(MachineAccessUpsertPasswordForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreatePasswordMachineAccess(machine, form);
        } else {
            MachineAccessUpsertPasswordForm machineAccessUpsertPasswordForm = form.get();
            machineAccessService.createPasswordMachineAccess(machine.getJid(), machineAccessUpsertPasswordForm.name, machineAccessUpsertPasswordForm.username, machineAccessUpsertPasswordForm.password, machineAccessUpsertPasswordForm.port);

            return redirect(routes.MachineAccessController.viewMachineAccesses(machine.getId()));
        }
    }

    private Result showCreateKeyMachineAccess(Machine machine, Form<MachineAccessUpsertKeyForm> form) {
        LazyHtml content = new LazyHtml(createKeyMachineAccessView.render(machine.getId(), form));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.key.create"), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.key.create"), routes.MachineAccessController.createKeyMachineAccess(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreatePasswordMachineAccess(Machine machine, Form<MachineAccessUpsertPasswordForm> form) {
        LazyHtml content = new LazyHtml(createPasswordMachineAccessView.render(machine.getId(), form));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.password.create"), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.password.create"), routes.MachineAccessController.createPasswordMachineAccess(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines");

        return ControllerUtils.getInstance().lazyOk(content);
    }
}
