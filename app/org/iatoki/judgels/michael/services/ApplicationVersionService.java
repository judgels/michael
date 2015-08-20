package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;

import java.util.List;

public interface ApplicationVersionService {

    Page<ApplicationVersion> getPageOfApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<ApplicationVersion> getApplicationVersionByApplicationJid(String applicationJid);

    ApplicationVersion findApplicationVersionById(long applicationVersionId) throws ApplicationVersionNotFoundException;

    void createApplicationVersion(String applicationJid, String name);

    void removeApplicationVersion(long applicationVersionId) throws ApplicationVersionNotFoundException;

}
