package org.iatoki.judgels.michael.services.impls;

import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.services.MachineWatcherService;
import org.iatoki.judgels.michael.models.daos.MichaelDaoFactory;
import org.iatoki.judgels.michael.services.MichaelServiceFactory;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.services.DashboardMachineService;
import org.iatoki.judgels.michael.services.DashboardService;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.services.OperationService;

public final class DefaultMichaelServiceFactory implements MichaelServiceFactory {

    private final MichaelDaoFactory michaelDaoFactory;

    public DefaultMichaelServiceFactory(MichaelDaoFactory michaelDaoFactory) {
        this.michaelDaoFactory = michaelDaoFactory;
    }

    @Override
    public ApplicationService createApplicationService() {
        return new ApplicationServiceImpl(michaelDaoFactory.createApplicationDao());
    }

    @Override
    public ApplicationVersionService createApplicationVersionService() {
        return new ApplicationVersionServiceImpl(michaelDaoFactory.createApplicationVersionDao());
    }

    @Override
    public DashboardService createDashboardService() {
        return new DashboardServiceImpl(michaelDaoFactory.createDashboardDao(), michaelDaoFactory.createDashboardMachineDao());
    }

    @Override
    public DashboardMachineService createDashboardMachineService() {
        return new DashboardMachineServiceImpl(michaelDaoFactory.createDashboardMachineDao(), michaelDaoFactory.createMachineDao());
    }

    @Override
    public MachineService createMachineService() {
        return new MachineServiceImpl(michaelDaoFactory.createMachineDao());
    }

    @Override
    public MachineAccessService createMachineAccessService() {
        return new MachineAccessServiceImpl(michaelDaoFactory.createMachineAccessDao());
    }

    @Override
    public MachineWatcherService createMachineWatcherService() {
        return new MachineWatcherServiceImpl(michaelDaoFactory.createMachineWatcherDao());
    }

    @Override
    public OperationService createOperationService() {
        return new OperationServiceImpl(michaelDaoFactory.createOperationDao());
    }
}
