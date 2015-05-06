package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.controllers.ApplicationController;
import org.iatoki.judgels.michael.controllers.ApplicationVersionController;
import org.iatoki.judgels.michael.controllers.DashboardController;
import org.iatoki.judgels.michael.controllers.MachineAccessController;
import org.iatoki.judgels.michael.controllers.MachineController;
import org.iatoki.judgels.michael.controllers.MachineWatcherController;
import org.iatoki.judgels.michael.controllers.OperationController;
import org.iatoki.judgels.michael.controllers.UserController;
import org.iatoki.judgels.michael.controllers.apis.ApplicationVersionAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineAccessAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineWatcherAPIController;

public final class DefaultMichaelControllerFactory implements MichaelControllerFactory {

    private final MichaelServiceFactory michaelServiceFactory;

    public DefaultMichaelControllerFactory(MichaelServiceFactory michaelServiceFactory) {
        this.michaelServiceFactory = michaelServiceFactory;
    }

    @Override
    public ApplicationController createApplicationController() {
        return new ApplicationController(michaelServiceFactory.createApplicationService());
    }

    @Override
    public ApplicationVersionController createApplicationVersionController() {
        return new ApplicationVersionController(michaelServiceFactory.createApplicationService(), michaelServiceFactory.createApplicationVersionService());
    }

    @Override
    public DashboardController createDashboardController() {
        return new DashboardController(michaelServiceFactory.createDashboardService(), michaelServiceFactory.createMachineService());
    }

    @Override
    public MachineAccessController createMachineAccessController() {
        return new MachineAccessController(michaelServiceFactory.createMachineService(), michaelServiceFactory.createMachineAccessService());
    }

    @Override
    public MachineController createMachineController() {
        return new MachineController(michaelServiceFactory.createMachineService(), michaelServiceFactory.createMachineWatcherService());
    }

    @Override
    public MachineWatcherController createMachineWatcherController() {
        return new MachineWatcherController(michaelServiceFactory.createMachineService(), michaelServiceFactory.createMachineWatcherService());
    }

    @Override
    public OperationController createOperationController() {
        return new OperationController(michaelServiceFactory.createApplicationService(), michaelServiceFactory.createApplicationVersionService(), michaelServiceFactory.createMachineService(), michaelServiceFactory.createMachineAccessService(), michaelServiceFactory.createOperationService());
    }

    @Override
    public UserController createUserController() {
        return new UserController();
    }

    @Override
    public ApplicationVersionAPIController createApplicationVersionApiController() {
        return new ApplicationVersionAPIController(michaelServiceFactory.createApplicationVersionService());
    }

    @Override
    public MachineAccessAPIController createMachineAccessApiController() {
        return new MachineAccessAPIController(michaelServiceFactory.createMachineAccessService());
    }

    @Override
    public MachineWatcherAPIController createMachineWatcherApiController() {
        return new MachineWatcherAPIController(michaelServiceFactory.createMachineService(), michaelServiceFactory.createMachineWatcherService());
    }
}
