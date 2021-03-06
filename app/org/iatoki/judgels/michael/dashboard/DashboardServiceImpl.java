package org.iatoki.judgels.michael.dashboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;

    @Inject
    public DashboardServiceImpl(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @Override
    public Page<Dashboard> getPageOfDashboards(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = dashboardDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<DashboardModel> dashboardModels = dashboardDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Dashboard> dashboards = Lists.transform(dashboardModels, m -> createDashboardFromModel(m));

        return new Page<>(dashboards, totalPages, pageIndex, pageSize);
    }

    @Override
    public Dashboard findDashboardById(long dashboardId) throws DashboardNotFoundException {
        DashboardModel dashboardModel = dashboardDao.findById(dashboardId);
        if (dashboardModel != null) {
            return createDashboardFromModel(dashboardModel);
        } else {
            throw new DashboardNotFoundException("Dashboard not found.");
        }
    }

    @Override
    public void createDashboard(String name, String description) {
        DashboardModel dashboardModel = new DashboardModel();
        dashboardModel.name = name;
        dashboardModel.description = description;

        dashboardDao.persist(dashboardModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateDashboard(long dashboardId, String name, String description) throws DashboardNotFoundException {
        DashboardModel dashboardModel = dashboardDao.findById(dashboardId);
        if (dashboardModel != null) {
            dashboardModel.name = name;
            dashboardModel.description = description;

            dashboardDao.edit(dashboardModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new DashboardNotFoundException("Dashboard not found.");
        }
    }

    private Dashboard createDashboardFromModel(DashboardModel dashboardModel) {
        return new Dashboard(dashboardModel.id, dashboardModel.jid, dashboardModel.name, dashboardModel.description);
    }
}
