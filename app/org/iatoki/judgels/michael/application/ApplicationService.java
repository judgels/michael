package org.iatoki.judgels.michael.application;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;

@ImplementedBy(ApplicationServiceImpl.class)
public interface ApplicationService {

    Page<Application> getPageOfApplications(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Application> getAllApplications();

    boolean applicationExistsByJid(String applicationJid);

    Application findByApplicationJid(String applicationJid);

    Application findByApplicationId(long applicationId) throws ApplicationNotFoundException;

    void createApplication(String name, ApplicationType applicationTypes);

    void updateApplication(long applicationId, String name, ApplicationType applicationTypes) throws ApplicationNotFoundException;
}
