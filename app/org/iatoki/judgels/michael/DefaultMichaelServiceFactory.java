package org.iatoki.judgels.michael;

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
