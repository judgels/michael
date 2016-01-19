package org.iatoki.judgels.michael.dashboard;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(DashboardServiceImpl.class)
public interface DashboardService {

    Page<Dashboard> getPageOfDashboards(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Dashboard findDashboardById(long dashboardId) throws DashboardNotFoundException;

    void createDashboard(String name, String description);

    void updateDashboard(long dashboardId, String name, String description) throws DashboardNotFoundException;

}
