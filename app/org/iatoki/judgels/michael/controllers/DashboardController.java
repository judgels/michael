package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.services.DashboardMachineService;
import org.iatoki.judgels.michael.DashboardNotFoundException;
import org.iatoki.judgels.michael.services.DashboardService;
import org.iatoki.judgels.michael.controllers.forms.DashboardUpsertForm;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherAdapter;
import org.iatoki.judgels.michael.MachineWatcherConfAdapter;
import org.iatoki.judgels.michael.services.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherType;
import org.iatoki.judgels.michael.MachineWatcherUtils;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.dashboard.createDashboardView;
import org.iatoki.judgels.michael.views.html.dashboard.listDashboardsView;
import org.iatoki.judgels.michael.views.html.dashboard.updateDashboardGeneralView;
import org.iatoki.judgels.michael.views.html.dashboard.viewDashboardView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;
import java.util.Map;

@Security.Authenticated(value = LoggedIn.class)
public final class DashboardController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final DashboardService dashboardService;
    private final DashboardMachineService dashboardMachineService;
    private final MachineWatcherService machineWatcherService;

    public DashboardController(DashboardService dashboardService, DashboardMachineService dashboardMachineService, MachineWatcherService machineWatcherService) {
        this.dashboardService = dashboardService;
        this.dashboardMachineService = dashboardMachineService;
        this.machineWatcherService = machineWatcherService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listDashboards(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Result viewDashboard(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        List<Machine> machineList = dashboardMachineService.findAllIncludedMachinesByDashboardJid(dashboard.getJid());
        Map<MachineWatcherType, List<MachineWatcherAdapter>> adapterMap = Maps.newHashMap();
        for (Machine machine : machineList) {
            List<MachineWatcher> machineWatchers = machineWatcherService.findAll(machine.getJid());
            if (machine.getType().equals(MachineType.AWS_EC2)) {
                for (MachineWatcher machineWatcher : machineWatchers) {
                    MachineWatcherConfAdapter factory = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, machineWatcher.getType());
                    if (factory != null) {
                        if (!adapterMap.containsKey(machineWatcher.getType())) {
                            adapterMap.put(machineWatcher.getType(), Lists.newArrayList());
                        }
                        List<MachineWatcherAdapter> adapters = adapterMap.get(machineWatcher.getType());
                        adapters.add(factory.createMachineWatcherAdapter(machine, machineWatcher.getConf()));
                    }
                }
            }
        }

        LazyHtml content = new LazyHtml(viewDashboardView.render(dashboard, adapterMap));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), new InternalLink(Messages.get("commons.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.view"), routes.DashboardController.viewDashboard(dashboard.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - View");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createDashboard() {
        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class);

        return showCreateDashboard(form);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateDashboardGeneral(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findByDashboardId(dashboardId);
        DashboardUpsertForm dashboardUpsertForm = new DashboardUpsertForm();
        dashboardUpsertForm.name = dashboard.getName();
        dashboardUpsertForm.description = dashboard.getDescription();

        Form<DashboardUpsertForm> form = Form.form(DashboardUpsertForm.class).fill(dashboardUpsertForm);

        return showUpdateDashboardGeneral(form, dashboard);
    }

    @Transactional
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
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("dashboard.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId())),
              new InternalLink(Messages.get("dashboard.machine"), routes.DashboardMachineController.viewDashboardMachines(dashboard.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("dashboard.dashboard") + " #" + dashboard.getId() + ": " + dashboard.getName(), new InternalLink(Messages.get("commons.enter"), routes.DashboardController.viewDashboard(dashboard.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.update"), routes.DashboardController.updateDashboardGeneral(dashboard.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }
}
