package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;
import org.iatoki.judgels.michael.models.daos.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.entities.ApplicationVersionModel;
import org.iatoki.judgels.michael.models.entities.ApplicationVersionModel_;
import org.iatoki.judgels.michael.services.ApplicationVersionService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named("applicationVersionService")
public final class ApplicationVersionServiceImpl implements ApplicationVersionService {

    private final ApplicationVersionDao applicationVersionDao;

    @Inject
    public ApplicationVersionServiceImpl(ApplicationVersionDao applicationVersionDao) {
        this.applicationVersionDao = applicationVersionDao;
    }

    @Override
    public Page<ApplicationVersion> getPageOfApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = applicationVersionDao.countByFilters(filterString, ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), ImmutableMap.of());
        List<ApplicationVersionModel> applicationVersionModels = applicationVersionDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), pageIndex * pageSize, pageSize);

        List<ApplicationVersion> machineAccesses = Lists.transform(applicationVersionModels, m -> createApplicationVersionFromModel(m));

        return new Page<>(machineAccesses, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<ApplicationVersion> getApplicationVersionByApplicationJid(String applicationJid) {
        List<ApplicationVersionModel> applicationVersionModels = applicationVersionDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), 0, -1);
        return applicationVersionModels.stream().map(m -> createApplicationVersionFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public ApplicationVersion findApplicationVersionById(long applicationVersionId) throws ApplicationVersionNotFoundException {
        ApplicationVersionModel applicationVersionModel = applicationVersionDao.findById(applicationVersionId);
        if (applicationVersionModel != null) {
            return createApplicationVersionFromModel(applicationVersionModel);
        } else {
            throw new ApplicationVersionNotFoundException("Application Version not found.");
        }
    }

    @Override
    public void createApplicationVersion(String applicationJid, String name) {
        ApplicationVersionModel applicationVersionModel = new ApplicationVersionModel();
        applicationVersionModel.applicationJid = applicationJid;
        applicationVersionModel.name = name;

        applicationVersionDao.persist(applicationVersionModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void removeApplicationVersion(long applicationVersionId) throws ApplicationVersionNotFoundException {
        ApplicationVersionModel applicationVersionModel = applicationVersionDao.findById(applicationVersionId);
        if (applicationVersionModel != null) {
            applicationVersionDao.remove(applicationVersionModel);
        } else {
            throw new ApplicationVersionNotFoundException("Application Version not found.");
        }
    }

    private ApplicationVersion createApplicationVersionFromModel(ApplicationVersionModel applicationVersionModel) {
        return new ApplicationVersion(applicationVersionModel.id, applicationVersionModel.applicationJid, applicationVersionModel.name);
    }
}
