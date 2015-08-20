package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationNotFoundException;
import org.iatoki.judgels.michael.ApplicationType;

import java.util.List;

public interface ApplicationService {

    Page<Application> getPageOfApplications(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<Application> getAllApplications();

    boolean applicationExistsByJid(String applicationJid);

    Application findByApplicationJid(String applicationJid);

    Application findByApplicationId(long applicationId) throws ApplicationNotFoundException;

    void createApplication(String name, ApplicationType applicationTypes);

    void updateApplication(long applicationId, String name, ApplicationType applicationTypes) throws ApplicationNotFoundException;
}
