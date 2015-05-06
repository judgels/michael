package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

public interface DashboardService {

    Page<Dashboard> pageDashboards(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Dashboard findByDashboardId(long dashboardId) throws DashboardNotFoundException;

    void createDashboard(String name, String description);

    void updateDashboard(long dashboardId, String name, String description) throws DashboardNotFoundException;

}
