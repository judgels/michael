package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.BaseController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.DashboardMachine;
import org.iatoki.judgels.michael.controllers.forms.DashboardMachineCreateForm;
import org.iatoki.judgels.michael.DashboardMachineNotFoundException;
import org.iatoki.judgels.michael.services.DashboardMachineService;
import org.iatoki.judgels.michael.DashboardNotFoundException;
import org.iatoki.judgels.michael.services.DashboardService;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.dashboard.machine.listCreateDashboardMachinesView;
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
public final class DashboardMachineController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final DashboardService dashboardService;
    private final DashboardMachineService dashboardMachineService;

    @Inject
    public DashboardMachineController(DashboardService dashboardService, DashboardMachineService dashboardMachineService) {
        this.dashboardService = dashboardService;
        this.dashboardMachineService = dashboardMachineService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewDashboardMachines(long dashboardId) throws DashboardNotFoundException {
        return listCreateDashboardMachines(dashboardId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listCreateDashboardMachines(long dashboardId, long page, String orderBy, String orderDir, String filterString) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        Form<DashboardMachineCreateForm> form = Form.form(DashboardMachineCreateForm.class);
        Page<DashboardMachine> currentPage = dashboardMachineService.pageDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateDashboardMachines(dashboard, currentPage, page, orderBy, orderDir, filterString, form, dashboardMachineService.findAllNotIncludedMachinesByDashboardJid(dashboard.getJid()));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateDashboardMachine(long dashboardId, long page, String orderBy, String orderDir, String filterString) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        Form<DashboardMachineCreateForm> form = Form.form(DashboardMachineCreateForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            Page<DashboardMachine> currentPage = dashboardMachineService.pageDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateDashboardMachines(dashboard, currentPage, page, orderBy, orderDir, filterString, form, dashboardMachineService.findAllNotIncludedMachinesByDashboardJid(dashboard.getJid()));
        } else {
            DashboardMachineCreateForm createForm = form.get();
            if (!dashboardMachineService.existByDashboardJidAndMachineJid(dashboard.getJid(), createForm.machineJid)) {
                dashboardMachineService.createDashboardMachine(dashboard.getJid(), createForm.machineJid);

                return redirect(org.iatoki.judgels.michael.controllers.routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()));
            } else {
                form.reject("error.dashboard.machine.machineIsAlreadyExist");
                Page<DashboardMachine> currentPage = dashboardMachineService.pageDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateDashboardMachines(dashboard, currentPage, page, orderBy, orderDir, filterString, form, dashboardMachineService.findAllNotIncludedMachinesByDashboardJid(dashboard.getJid()));
            }
        }
    }

    @Transactional
    public Result removeDashboardMachine(long dashboardId, long dashboardMachineId) throws DashboardNotFoundException, DashboardMachineNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        DashboardMachine dashboardMachine = dashboardMachineService.findByDashboardMachineId(dashboardMachineId);
        if (dashboard.getJid().equals(dashboardMachine.getDashboardJid())) {
            dashboardMachineService.removeDashboardMachine(dashboardMachine.getId());

            return redirect(org.iatoki.judgels.michael.controllers.routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Result showListCreateDashboardMachines(Dashboard dashboard, Page<DashboardMachine> currentPage, long page, String orderBy, String orderDir, String filterString, Form<DashboardMachineCreateForm> form, List<Machine> machines) {
        LazyHtml content = new LazyHtml(listCreateDashboardMachinesView.render(dashboard.getId(), currentPage, orderBy, orderDir, filterString, form, dashboardMachineService.findAllNotIncludedMachinesByDashboardJid(dashboard.getJid())));
        content.appendLayout(c -> headingLayout.render(Messages.get("dashboard.machine.list"), c));
        appendTabLayout(content, dashboard);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.machine.list"), routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Machines");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private void appendTabLayout(LazyHtml content, Dashboard dashboard) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("dashboard.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId())),
              new InternalLink(Messages.get("dashboard.machine"), routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), new InternalLink(Messages.get("commons.enter"), routes.DashboardController.viewDashboard(dashboard.getId())), c));
    }
}
