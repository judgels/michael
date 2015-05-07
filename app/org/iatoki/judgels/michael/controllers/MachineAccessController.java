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
import org.iatoki.judgels.michael.views.html.machines.accesses.listCreateMachineAccessesView;
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
        return listCreateMachineAccesses(machineId, 0, "id", "asc", "");
    }

    public Result listCreateMachineAccesses(long machineId, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
        Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateMachineAccesses(machine, currentPage, orderBy, orderDir, filterString, form);
    }

    @AddCSRFToken
    public Result createMachineAccess(long machineId, String machineAccessType, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);

        if (EnumUtils.isValidEnum(MachineAccessTypes.class, machineAccessType)) {
            MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessTypes.valueOf(machineAccessType));
            if (adapter != null) {
                return showCreateMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), org.iatoki.judgels.michael.controllers.routes.MachineAccessController.postCreateMachineAccess(machineId, machineAccessType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
            } else {
                Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
                form.reject("error.machine.access.undefined");
                Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateMachineAccesses(machine, currentPage, orderBy, orderDir, filterString, form);
            }
        } else {
            Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
            form.reject("error.machine.access.undefined");
            Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, currentPage, orderBy, orderDir, filterString, form);
        }
    }

    @RequireCSRFCheck
    public Result postCreateMachineAccess(long machineId, String machineAccessType, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);

        if (EnumUtils.isValidEnum(MachineAccessTypes.class, machineAccessType)) {
            MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessTypes.valueOf(machineAccessType));
            if (adapter != null) {
                Form form = adapter.bindFormFromRequest(request());
                if (form.hasErrors() || form.hasGlobalErrors()) {
                    return showCreateMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), org.iatoki.judgels.michael.controllers.routes.MachineAccessController.postCreateMachineAccess(machineId, machineAccessType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
                } else {
                    machineAccessService.createMachineAccess(machine.getJid(), adapter.getNameFromForm(form), MachineAccessTypes.valueOf(machineAccessType), adapter.processRequestForm(form));

                    return redirect(routes.MachineAccessController.viewMachineAccesses(machine.getId()));
                }
            } else {
                Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
                form.reject("error.machine.access.undefined");
                Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateMachineAccesses(machine, currentPage, orderBy, orderDir, filterString, form);
            }
        } else {
            Form<MachineAccessCreateForm> form = Form.form(MachineAccessCreateForm.class);
            form.reject("error.machine.access.undefined");
            Page<MachineAccess> currentPage = machineAccessService.pageMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, currentPage, orderBy, orderDir, filterString, form);
        }
    }

    private Result showListCreateMachineAccesses(Machine machine, Page<MachineAccess> currentPage, String orderBy, String orderDir, String filterString, Form<MachineAccessCreateForm> form) {
        LazyHtml content = new LazyHtml(listCreateMachineAccessesView.render(machine.getId(), currentPage, orderBy, orderDir, filterString, form));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.list"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Accesses");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateMachineAccess(Machine machine, String accessType, Html html, long page, String orderBy, String orderDir, String filterString) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.access.create"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.access.list"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.access.create"), routes.MachineAccessController.createMachineAccess(machine.getId(), accessType, page, orderBy, orderDir, filterString))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machines - Create");

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
