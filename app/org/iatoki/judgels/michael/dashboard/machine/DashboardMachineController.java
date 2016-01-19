package org.iatoki.judgels.michael.dashboard.machine;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.michael.MichaelControllerUtils;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.dashboard.Dashboard;
import org.iatoki.judgels.michael.dashboard.DashboardNotFoundException;
import org.iatoki.judgels.michael.dashboard.DashboardService;
import org.iatoki.judgels.michael.dashboard.machine.html.listCreateDashboardMachinesView;
import org.iatoki.judgels.michael.machine.Machine;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
public final class DashboardMachineController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final DashboardMachineService dashboardMachineService;
    private final DashboardService dashboardService;

    @Inject
    public DashboardMachineController(DashboardMachineService dashboardMachineService, DashboardService dashboardService) {
        this.dashboardMachineService = dashboardMachineService;
        this.dashboardService = dashboardService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewDashboardMachines(long dashboardId) throws DashboardNotFoundException {
        return listCreateDashboardMachines(dashboardId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listCreateDashboardMachines(long dashboardId, long page, String orderBy, String orderDir, String filterString) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        Form<DashboardMachineCreateForm> dashboardMachineCreateForm = Form.form(DashboardMachineCreateForm.class);
        Page<DashboardMachine> pageOfDashboardMachines = dashboardMachineService.getPageOfDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateDashboardMachines(dashboard, pageOfDashboardMachines, orderBy, orderDir, filterString, dashboardMachineCreateForm, dashboardMachineService.getMachinesNotInMachinesByDashboardJid(dashboard.getJid()));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateDashboardMachine(long dashboardId, long page, String orderBy, String orderDir, String filterString) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        Form<DashboardMachineCreateForm> dashboardMachineCreateForm = Form.form(DashboardMachineCreateForm.class).bindFromRequest();

        if (formHasErrors(dashboardMachineCreateForm)) {
            Page<DashboardMachine> pageOfDashboardMachines = dashboardMachineService.getPageOfDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateDashboardMachines(dashboard, pageOfDashboardMachines, orderBy, orderDir, filterString, dashboardMachineCreateForm, dashboardMachineService.getMachinesNotInMachinesByDashboardJid(dashboard.getJid()));
        } else {
            DashboardMachineCreateForm dashboardMachineCreateData = dashboardMachineCreateForm.get();
            if (!dashboardMachineService.dashboardMachineExists(dashboard.getJid(), dashboardMachineCreateData.machineJid)) {
                dashboardMachineService.createDashboardMachine(dashboard.getJid(), dashboardMachineCreateData.machineJid);

                return redirect(routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()));
            } else {
                dashboardMachineCreateForm.reject("error.dashboard.machine.machineIsAlreadyExist");
                Page<DashboardMachine> pageOfDashboardMachines = dashboardMachineService.getPageOfDashboardMachines(dashboard.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateDashboardMachines(dashboard, pageOfDashboardMachines, orderBy, orderDir, filterString, dashboardMachineCreateForm, dashboardMachineService.getMachinesNotInMachinesByDashboardJid(dashboard.getJid()));
            }
        }
    }

    @Transactional
    public Result removeDashboardMachine(long dashboardId, long dashboardMachineId) throws DashboardNotFoundException, DashboardMachineNotFoundException {
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        DashboardMachine dashboardMachine = dashboardMachineService.findDashboardMachineById(dashboardMachineId);
        if (!dashboard.getJid().equals(dashboardMachine.getDashboardJid())) {
            throw new UnsupportedOperationException();
        }

        dashboardMachineService.removeDashboardMachine(dashboardMachine.getId());

        return redirect(routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()));
    }

    private Result showListCreateDashboardMachines(Dashboard dashboard, Page<DashboardMachine> pageOfDashboardMachines, String orderBy, String orderDir, String filterString, Form<DashboardMachineCreateForm> dashboardMachineCreateForm, List<Machine> machines) {
        LazyHtml content = new LazyHtml(listCreateDashboardMachinesView.render(dashboard.getId(), pageOfDashboardMachines, orderBy, orderDir, filterString, dashboardMachineCreateForm, dashboardMachineService.getMachinesNotInMachinesByDashboardJid(dashboard.getJid())));
        content.appendLayout(c -> headingLayout.render(Messages.get("dashboard.machine.list"), c));
        appendTabLayout(content, dashboard);
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), org.iatoki.judgels.michael.dashboard.routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.machine.list"), routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()))
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Machines");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendTabLayout(LazyHtml content, Dashboard dashboard) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("dashboard.update"), org.iatoki.judgels.michael.dashboard.routes.DashboardController.updateDashboardGeneral(dashboard.getId())),
              new InternalLink(Messages.get("dashboard.machine"), routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), new InternalLink(Messages.get("commons.enter"), org.iatoki.judgels.michael.dashboard.routes.DashboardController.viewDashboard(dashboard.getId())), c));
    }
}
