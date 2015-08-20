package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.adapters.MachineAccessConfAdapter;
import org.iatoki.judgels.michael.forms.MachineAccessCreateForm;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.MachineAccessType;
import org.iatoki.judgels.michael.MachineAccessUtils;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.machine.access.listCreateMachineAccessesView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class MachineAccessController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final MachineAccessService machineAccessService;
    private final MachineService machineService;

    @Inject
    public MachineAccessController(MachineAccessService machineAccessService, MachineService machineService) {
        this.machineAccessService = machineAccessService;
        this.machineService = machineService;
    }

    @Transactional(readOnly = true)
    public Result viewMachineAccesses(long machineId) throws MachineNotFoundException {
        return listCreateMachineAccesses(machineId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listCreateMachineAccesses(long machineId, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        Form<MachineAccessCreateForm> machineAccessCreateForm = Form.form(MachineAccessCreateForm.class);
        Page<MachineAccess> pageOfMachineAccesses = machineAccessService.getPageOfMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateMachineAccesses(machine, pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createMachineAccess(long machineId, String machineAccessType, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);

        if (!EnumUtils.isValidEnum(MachineAccessType.class, machineAccessType)) {
            Form<MachineAccessCreateForm> machineAccessCreateForm = Form.form(MachineAccessCreateForm.class);
            machineAccessCreateForm.reject("error.machine.access.undefined");
            Page<MachineAccess> pageOfMachineAccesses = machineAccessService.getPageOfMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm);
        }


        MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessType.valueOf(machineAccessType));
        if (adapter == null) {
            Form<MachineAccessCreateForm> machineAccessCreateForm = Form.form(MachineAccessCreateForm.class);
            machineAccessCreateForm.reject("error.machine.access.undefined");
            Page<MachineAccess> pageOfMachineAccesses = machineAccessService.getPageOfMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm);
        }

        return showCreateMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), routes.MachineAccessController.postCreateMachineAccess(machineId, machineAccessType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateMachineAccess(long machineId, String machineAccessType, long page, String orderBy, String orderDir, String filterString) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);

        if (!EnumUtils.isValidEnum(MachineAccessType.class, machineAccessType)) {
            Form<MachineAccessCreateForm> machineAccessCreateForm = Form.form(MachineAccessCreateForm.class);
            machineAccessCreateForm.reject("error.machine.access.undefined");
            Page<MachineAccess> pageOfMachineAccesses = machineAccessService.getPageOfMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm);
        }

        MachineAccessConfAdapter adapter = MachineAccessUtils.getMachineAccessConfAdapter(MachineAccessType.valueOf(machineAccessType));
        if (adapter == null) {
            Form<MachineAccessCreateForm> machineAccessCreateForm = Form.form(MachineAccessCreateForm.class);
            machineAccessCreateForm.reject("error.machine.access.undefined");
            Page<MachineAccess> pageOfMachineAccesses = machineAccessService.getPageOfMachineAccesses(machine.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateMachineAccesses(machine, pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm);
        }

        Form machineAccessConfForm = adapter.bindFormFromRequest(request());
        if (formHasErrors(machineAccessConfForm)) {
            return showCreateMachineAccess(machine, machineAccessType, adapter.getConfHtml(adapter.generateForm(), routes.MachineAccessController.postCreateMachineAccess(machineId, machineAccessType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
        }

        machineAccessService.createMachineAccess(machine.getJid(), adapter.getNameFromForm(machineAccessConfForm), MachineAccessType.valueOf(machineAccessType), adapter.processRequestForm(machineAccessConfForm));

        return redirect(routes.MachineAccessController.viewMachineAccesses(machine.getId()));
    }

    private Result showListCreateMachineAccesses(Machine machine, Page<MachineAccess> pageOfMachineAccesses, String orderBy, String orderDir, String filterString, Form<MachineAccessCreateForm> machineAccessCreateForm) {
        LazyHtml content = new LazyHtml(listCreateMachineAccessesView.render(machine.getId(), pageOfMachineAccesses, orderBy, orderDir, filterString, machineAccessCreateForm));
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
