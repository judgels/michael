package org.iatoki.judgels.michael.controllers;

import org.iatoki.judgels.michael.controllers.ApplicationController;
import org.iatoki.judgels.michael.controllers.ApplicationVersionController;
import org.iatoki.judgels.michael.controllers.DashboardController;
import org.iatoki.judgels.michael.controllers.DashboardMachineController;
import org.iatoki.judgels.michael.controllers.MachineAccessController;
import org.iatoki.judgels.michael.controllers.MachineController;
import org.iatoki.judgels.michael.controllers.MachineWatcherController;
import org.iatoki.judgels.michael.controllers.OperationController;
import org.iatoki.judgels.michael.controllers.UserController;
import org.iatoki.judgels.michael.controllers.apis.ApplicationVersionAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineAccessAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineWatcherAPIController;

public interface MichaelControllerFactory {

    ApplicationController createApplicationController();

    ApplicationVersionController createApplicationVersionController();

    DashboardController createDashboardController();

    DashboardMachineController createDashboardMachineController();

    MachineAccessController createMachineAccessController();

    MachineController createMachineController();

    MachineWatcherController createMachineWatcherController();

    OperationController createOperationController();

    UserController createUserController();

    ApplicationVersionAPIController createApplicationVersionApiController();

    MachineAccessAPIController createMachineAccessApiController();

    MachineWatcherAPIController createMachineWatcherApiController();
}
