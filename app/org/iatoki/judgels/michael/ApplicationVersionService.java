package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

import java.util.List;

public interface ApplicationVersionService {

    Page<ApplicationVersion> pageApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<ApplicationVersion> findByApplicationJid(String applicationJid);

    ApplicationVersion findByApplicationVersionId(long applicationVersionId) throws ApplicationVersionNotFoundException;

    void createApplicationVersion(String applicationJid, String name);

    void removeApplicationVersion(long applicationVersionId) throws ApplicationVersionNotFoundException;

}
