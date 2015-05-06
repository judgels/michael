package org.iatoki.judgels.michael;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.models.dao.interfaces.ApplicationVersionDao;
import org.iatoki.judgels.michael.models.domains.ApplicationVersionModel;
import org.iatoki.judgels.michael.models.domains.ApplicationVersionModel_;

import java.util.List;
import java.util.stream.Collectors;

public final class ApplicationVersionServiceImpl implements ApplicationVersionService {

    private final ApplicationVersionDao applicationVersionDao;

    public ApplicationVersionServiceImpl(ApplicationVersionDao applicationVersionDao) {
        this.applicationVersionDao = applicationVersionDao;
    }

    @Override
    public Page<ApplicationVersion> pageApplicationVersions(String applicationJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = applicationVersionDao.countByFilters(filterString, ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), ImmutableMap.of());
        List<ApplicationVersionModel> applicationVersionModels = applicationVersionDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<ApplicationVersion> machineAccesses = Lists.transform(applicationVersionModels, m -> createApplicationVersionFromModel(m));

        return new Page<>(machineAccesses, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<ApplicationVersion> findByApplicationJid(String applicationJid) {
        List<ApplicationVersionModel> applicationVersionModels = applicationVersionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(ApplicationVersionModel_.applicationJid, applicationJid), ImmutableMap.of(), 0, -1);
        return applicationVersionModels.stream().map(m -> createApplicationVersionFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public ApplicationVersion findByApplicationVersionId(long applicationVersionId) throws ApplicationVersionNotFoundException {
        return createApplicationVersionFromModel(applicationVersionDao.findById(applicationVersionId));
    }

    @Override
    public void createApplicationVersion(String applicationJid, String name) {
        ApplicationVersionModel applicationVersionModel = new ApplicationVersionModel();
        applicationVersionModel.applicationJid = applicationJid;
        applicationVersionModel.name = name;

        applicationVersionDao.persist(applicationVersionModel, "michael", IdentityUtils.getIpAddress());
    }


    private ApplicationVersion createApplicationVersionFromModel(ApplicationVersionModel applicationVersionModel) {
        return new ApplicationVersion(applicationVersionModel.id, applicationVersionModel.applicationJid, applicationVersionModel.name);
    }
}
