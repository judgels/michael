package org.iatoki.judgels.michael.services.impls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationNotFoundException;
import org.iatoki.judgels.michael.ApplicationType;
import org.iatoki.judgels.michael.models.daos.ApplicationDao;
import org.iatoki.judgels.michael.models.entities.ApplicationModel;
import org.iatoki.judgels.michael.services.ApplicationService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named("applicationService")
public final class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationDao applicationDao;

    @Inject
    public ApplicationServiceImpl(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    @Override
    public Page<Application> getPageOfApplications(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = applicationDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<ApplicationModel> applicationModels = applicationDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<Application> applications = Lists.transform(applicationModels, m -> createApplicationFromModel(m));

        return new Page<>(applications, totalPages, pageIndex, pageSize);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationDao.getAll().stream().map(a -> createApplicationFromModel(a)).collect(Collectors.toList());
    }

    @Override
    public boolean applicationExistsByJid(String applicationJid) {
        return applicationDao.existsByJid(applicationJid);
    }

    @Override
    public Application findByApplicationJid(String applicationJid) {
        return createApplicationFromModel(applicationDao.findByJid(applicationJid));
    }

    @Override
    public Application findByApplicationId(long applicationId) throws ApplicationNotFoundException {
        ApplicationModel applicationModel = applicationDao.findById(applicationId);
        if (applicationModel != null) {
            return createApplicationFromModel(applicationModel);
        } else {
            throw new ApplicationNotFoundException("Application not found.");
        }
    }

    @Override
    public void createApplication(String name, ApplicationType applicationTypes) {
        ApplicationModel applicationModel = new ApplicationModel();
        applicationModel.name = name;
        applicationModel.type = applicationTypes.name();

        applicationDao.persist(applicationModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateApplication(long applicationId, String name, ApplicationType applicationTypes) throws ApplicationNotFoundException {
        ApplicationModel applicationModel = applicationDao.findById(applicationId);
        if (applicationModel != null) {
            applicationModel.name = name;
            applicationModel.type = applicationTypes.name();

            applicationDao.edit(applicationModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new ApplicationNotFoundException("Application not found.");
        }
    }

    private Application createApplicationFromModel(ApplicationModel applicationModel) {
        return new Application(applicationModel.id, applicationModel.jid, applicationModel.name, ApplicationType.valueOf(applicationModel.type));
    }
}
