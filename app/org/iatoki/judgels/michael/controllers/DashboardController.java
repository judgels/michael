package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.services.DashboardMachineService;
import org.iatoki.judgels.michael.DashboardNotFoundException;
import org.iatoki.judgels.michael.services.DashboardService;
import org.iatoki.judgels.michael.forms.DashboardUpsertForm;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineType;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.adapters.MachineWatcherAdapter;
import org.iatoki.judgels.michael.adapters.MachineWatcherConfAdapter;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class DashboardController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final DashboardMachineService dashboardMachineService;
    private final DashboardService dashboardService;
    private final MachineWatcherService machineWatcherService;

    @Inject
    public DashboardController(DashboardMachineService dashboardMachineService, DashboardService dashboardService, MachineWatcherService machineWatcherService) {
        this.dashboardMachineService = dashboardMachineService;
        this.dashboardService = dashboardService;
        this.machineWatcherService = machineWatcherService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listDashboards(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listDashboards(long page, String orderBy, String orderDir, String filterString) {
        Page<Dashboard> pageOfDashboards = dashboardService.getPageOfDashboards(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listDashboardsView.render(pageOfDashboards, orderBy, orderDir, filterString));
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
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        List<Machine> machinesInDashboard = dashboardMachineService.getMachinesInDashboardByDashboardJid(dashboard.getJid());
        Map<MachineWatcherType, List<MachineWatcherAdapter>> watcherAdapters = Maps.newHashMap();
        for (Machine machine : machinesInDashboard) {
            List<MachineWatcher> machineWatchers = machineWatcherService.getAllMachineWatchers(machine.getJid());
            if (machine.getType().equals(MachineType.AWS_EC2)) {
                for (MachineWatcher machineWatcher : machineWatchers) {
                    MachineWatcherConfAdapter factory = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, machineWatcher.getType());
                    if (factory != null) {
                        if (!watcherAdapters.containsKey(machineWatcher.getType())) {
                            watcherAdapters.put(machineWatcher.getType(), Lists.newArrayList());
                        }
                        List<MachineWatcherAdapter> adapters = watcherAdapters.get(machineWatcher.getType());
                        adapters.add(factory.createMachineWatcherAdapter(machine, machineWatcher.getConf()));
                    }
                }
            }
        }

        LazyHtml content = new LazyHtml(viewDashboardView.render(dashboard, watcherAdapters));
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
        Form<DashboardUpsertForm> dashboardUpsertForm = Form.form(DashboardUpsertForm.class);

        return showCreateDashboard(dashboardUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateDashboard() {
        Form<DashboardUpsertForm> dashboardUpsertForm = Form.form(DashboardUpsertForm.class).bindFromRequest();

        if (formHasErrors(dashboardUpsertForm)) {
            return showCreateDashboard(dashboardUpsertForm);
        }

        DashboardUpsertForm dashboardUpsertData = dashboardUpsertForm.get();
        dashboardService.createDashboard(dashboardUpsertData.name, dashboardUpsertData.description);

        return redirect(routes.DashboardController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateDashboardGeneral(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        DashboardUpsertForm dashboardUpsertData = new DashboardUpsertForm();
        dashboardUpsertData.name = dashboard.getName();
        dashboardUpsertData.description = dashboard.getDescription();

        Form<DashboardUpsertForm> dashboardUpsertForm = Form.form(DashboardUpsertForm.class).fill(dashboardUpsertData);

        return showUpdateDashboardGeneral(dashboardUpsertForm, dashboard);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateDashboardGeneral(long dashboardId) throws DashboardNotFoundException {
        Dashboard dashboard = dashboardService.findDashboardById(dashboardId);
        Form<DashboardUpsertForm> dashboardUpsertForm = Form.form(DashboardUpsertForm.class).bindFromRequest();

        if (formHasErrors(dashboardUpsertForm)) {
            return showUpdateDashboardGeneral(dashboardUpsertForm, dashboard);
        }

        DashboardUpsertForm dashboardUpsertData = dashboardUpsertForm.get();
        dashboardService.updateDashboard(dashboard.getId(), dashboardUpsertData.name, dashboardUpsertData.description);

        return redirect(routes.DashboardController.index());
    }

    private Result showCreateDashboard(Form<DashboardUpsertForm> dashboardUpsertForm) {
        LazyHtml content = new LazyHtml(createDashboardView.render(dashboardUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("dashboard.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("dashboard.create"), routes.DashboardController.createDashboard())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Dashboard - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateDashboardGeneral(Form<DashboardUpsertForm> dashboardUpsertForm, Dashboard dashboard) {
        LazyHtml content = new LazyHtml(updateDashboardGeneralView.render(dashboardUpsertForm, dashboard.getId()));
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
