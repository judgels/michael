package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.DashboardService;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.DashboardNotFoundException;
import org.iatoki.judgels.michael.DashboardUpsertForm;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.dashboards.createDashboardView;
import org.iatoki.judgels.michael.views.html.dashboards.listDashboardsView;
import org.iatoki.judgels.michael.views.html.dashboards.updateDashboardGeneralView;
import org.iatoki.judgels.michael.views.html.dashboards.viewDashboardView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class DashboardController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final DashboardService dashboardService;
    private final MachineService machineService;

    public DashboardController(DashboardService dashboardService, MachineService machineService) {
        this.dashboardService = dashboardService;
        this.machineService = machineService;
    }

    public Result index() {
        return listDashboards(0, "id", "asc", "");
    }

    public Result listDashboards(long page, String orderBy, String orderDir, String filterString) {
        Page<Dashboard> currentPage = dashboardService.pageDashboards(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listDashboardsView.render(currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.list"), new InternalLink(Messages.get("commons.create"), routes.DashboardController.createDashboard()), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboards");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result viewDashboard(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        LazyHtml content = new LazyHtml(viewDashboardView.render(dashboard));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), new InternalLink(Messages.get("commons.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.view"), routes.DashboardController.viewDashboard(dashboard.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - View");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @AddCSRFToken
    public Result createDashboard() {
        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class);

        return showCreateDashboard(form);
    }

    @RequireCSRFCheck
    public Result postCreateDashboard() {
        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateDashboard(form);
        } else {
            DashboardUpsertForm dashboardUpsertForm = form.get();
            dashboardService.createDashboard(dashboardUpsertForm.name, dashboardUpsertForm.description);

            return redirect(routes.DashboardController.index());
        }
    }

    @AddCSRFToken
    public Result updateDashboardGeneral(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        DashboardUpsertForm dashboardUpsertForm = new DashboardUpsertForm();
        dashboardUpsertForm.name = dashboard.getName();
        dashboardUpsertForm.description = dashboard.getDescription();

        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class).fill(dashboardUpsertForm);

        return showUpdateDashboardGeneral(form, dashboard);
    }

    @RequireCSRFCheck
    public Result postUpdateDashboardGeneral(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return showUpdateDashboardGeneral(form, dashboard);
        } else {
            DashboardUpsertForm dashboardUpsertForm = form.get();
            dashboardService.updateDashboard(dashboard.getId(), dashboardUpsertForm.name, dashboardUpsertForm.description);

            return redirect(routes.DashboardController.index());
        }
    }

    private Result showCreateDashboard(Form<DashboardUpsertForm> form) {
        LazyHtml content = new LazyHtml(createDashboardView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("dashboard.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.create"), routes.DashboardController.createDashboard())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateDashboardGeneral(Form<DashboardUpsertForm> form, Dashboard dashboard) {
        LazyHtml content = new LazyHtml(updateDashboardGeneralView.render(form, dashboard.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }
}
