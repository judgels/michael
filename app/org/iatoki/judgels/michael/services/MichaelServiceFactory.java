package org.iatoki.judgels.michael.services;

public interface MichaelServiceFactory {

    ApplicationService createApplicationService();

    ApplicationVersionService createApplicationVersionService();

    DashboardService createDashboardService();

    DashboardMachineService createDashboardMachineService();

    MachineService createMachineService();

    MachineAccessService createMachineAccessService();

    MachineWatcherService createMachineWatcherService();

    OperationService createOperationService();
}
