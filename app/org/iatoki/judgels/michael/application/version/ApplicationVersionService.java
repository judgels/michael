package org.iatoki.judgels.michael.application.version;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;

@ImplementedBy(ApplicationVersionServiceImpl.class)
public interface ApplicationVersionService {

    Page<ApplicationVersion> getPageOfApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<ApplicationVersion> getApplicationVersionByApplicationJid(String applicationJid);

    ApplicationVersion findApplicationVersionById(long applicationVersionId) throws ApplicationVersionNotFoundException;

    void createApplicationVersion(String applicationJid, String name);

    void removeApplicationVersion(long applicationVersionId) throws ApplicationVersionNotFoundException;

}
