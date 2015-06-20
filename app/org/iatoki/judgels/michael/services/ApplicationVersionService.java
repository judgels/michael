package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;

import java.util.List;

public interface ApplicationVersionService {

    Page<ApplicationVersion> pageApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<ApplicationVersion> findByApplicationJid(String applicationJid);

    ApplicationVersion findByApplicationVersionId(long applicationVersionId) throws ApplicationVersionNotFoundException;

    void createApplicationVersion(String applicationJid, String name);

    void removeApplicationVersion(long applicationVersionId) throws ApplicationVersionNotFoundException;

}
