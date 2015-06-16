package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

import java.util.List;

public interface ApplicationService {

    Page<Application> pageApplications(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Application> findAll();

    boolean existByApplicationJid(String applicationJid);

    Application findByApplicationJid(String applicationJid);

    Application findByApplicationId(long applicationId) throws ApplicationNotFoundException;

    void createApplication(String name, ApplicationType applicationTypes);

    void updateApplication(long applicationId, String name, ApplicationType applicationTypes) throws ApplicationNotFoundException;
}
