package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessConfAdapter;
import org.iatoki.judgels.michael.MachineAccessCreateForm;
import org.iatoki.judgels.michael.MachineAccessService;
import org.iatoki.judgels.michael.MachineAccessTypes;
import org.iatoki.judgels.michael.MachineAccessUtils;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.machines.accesses.createMachineAccessView;
import org.iatoki.judgels.michael.views.html.machines.accesses.listMachineAccessesView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;

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
        appendTabLayout(content, machine);
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
        Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);

        return showCreateMachineAccess(machine, form);
    }

    @AddCSRFToken
    public Result createDefinedMachineAccess(long machineId, String machineAccessType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);

        if (EnumUtils.isValidEnum(MachineAccessTypes.class, machineAccessType)) {
            MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessTypes.valueOf(machineAccessType));
            if (adapter != null) {
                return showCreateDefinedMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), org.iatoki.judgels.michael.controllers.routes.MachineAccessController.postCreateDefinedMachineAccess(machineId, machineAccessType), Messages.get("commons.create")));
            } else {
                Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
                form.reject("error.machine.access.undefined");
                return showCreateMachineAccess(machine, form);
            }
        } else {
            Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
            form.reject("error.machine.access.undefined");
            return showCreateMachineAccess(machine, form);
        }
    }

    @RequireCSRFCheck
    public Result postCreateDefinedMachineAccess(long machineId, String machineAccessType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);

        if (EnumUtils.isValidEnum(MachineAccessTypes.class, machineAccessType)) {
            MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessTypes.valueOf(machineAccessType));
            if (adapter != null) {
                Form form = adapter.bindFormFromRequest(request());
                if (form.hasErrors() || form.hasGlobalErrors()) {
                    return showCreateDefinedMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), org.iatoki.judgels.michael.controllers.routes.MachineAccessController.postCreateDefinedMachineAccess(machineId, machineAccessType), Messages.get("commons.create")));
                } else {
                    machineAccessService.createMachineAccess(machine.getJid(), adapter.getNameFromForm(form), MachineAccessTypes.valueOf(machineAccessType), adapter.processRequestForm(form));

                    return redirect(routes.MachineAccessController.viewMachineAccesses(machine.getId()));
                }
            } else {
                Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
                form.reject("error.machine.access.undefined");
                return showCreateMachineAccess(machine, form);
            }
        } else {
            Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
            form.reject("error.machine.access.undefined");
            return showCreateMachineAccess(machine, form);
        }
    }

    private Result showCreateMachineAccess(Machine machine, Form<MachineAccessCreateForm> form) {
        LazyHtml content = new LazyHtml(createMachineAccessView.render(machine.getId(), form));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.create"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.create"), routes.MachineAccessController.createMachineAccess(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines - Create");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateDefinedMachineAccess(Machine machine, String accessType, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.createDefined"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.create"), routes.MachineAccessController.createMachineAccess(machine.getId())),
              new InternalLink(Messages.get("machine.access.createDefined"), routes.MachineAccessController.createDefinedMachineAccess(machine.getId(), accessType))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines - Create Defined");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private void appendTabLayout(LazyHtml content, Machine machine) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.watcher"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.enter"), routes.MachineController.viewMachine(machine.getId())), c));
    }
}
