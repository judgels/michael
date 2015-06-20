package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.Dashboard;
import org.iatoki.judgels.michael.DashboardNotFoundException;

public interface DashboardService {

    Page<Dashboard> pageDashboards(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Dashboard findByDashboardId(long dashboardId) throws DashboardNotFoundException;

    void createDashboard(String name, String description);

    void updateDashboard(long dashboardId, String name, String description) throws DashboardNotFoundException;

}
