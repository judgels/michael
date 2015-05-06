package org.iatoki.judgels.michael;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.iatoki.judgels.commons.JudgelsProperties;
import org.iatoki.judgels.michael.controllers.ApplicationController;
import org.iatoki.judgels.michael.controllers.ApplicationVersionController;
import org.iatoki.judgels.michael.controllers.MachineWatcherController;
import org.iatoki.judgels.michael.controllers.OperationController;
import org.iatoki.judgels.michael.controllers.DashboardController;
import org.iatoki.judgels.michael.controllers.MachineAccessController;
import org.iatoki.judgels.michael.controllers.MachineController;
import org.iatoki.judgels.michael.controllers.UserController;
import org.iatoki.judgels.michael.controllers.apis.ApplicationVersionAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineAccessAPIController;
import org.iatoki.judgels.michael.controllers.apis.MachineWatcherAPIController;
import play.Application;
import play.mvc.Controller;

import java.util.Map;

public class Global extends org.iatoki.judgels.commons.Global {

    private final Map<Class<?>, Controller> controllersRegistry;

    public Global() {
        Config config = ConfigFactory.load();
        MichaelProperties.buildInstance(config);

        MichaelControllerFactory michaelControllerFactory = new DefaultMichaelControllerFactory(new DefaultMichaelServiceFactory(new HibernateMichaelDaoFactory()));

        controllersRegistry = ImmutableMap.<Class<?>, Controller> builder()
              .put(ApplicationController.class, michaelControllerFactory.createApplicationController())
              .put(ApplicationVersionController.class, michaelControllerFactory.createApplicationVersionController())
              .put(DashboardController.class, michaelControllerFactory.createDashboardController())
              .put(MachineAccessController.class, michaelControllerFactory.createMachineAccessController())
              .put(MachineController.class, michaelControllerFactory.createMachineController())
              .put(MachineWatcherController.class, michaelControllerFactory.createMachineWatcherController())
              .put(OperationController.class, michaelControllerFactory.createOperationController())
              .put(UserController.class, michaelControllerFactory.createUserController())
              .put(ApplicationVersionAPIController.class, michaelControllerFactory.createApplicationVersionApiController())
              .put(MachineAccessAPIController.class, michaelControllerFactory.createMachineAccessApiController())
              .put(MachineWatcherAPIController.class, michaelControllerFactory.createMachineWatcherApiController())
              .build();
    }

    @Override
    public void onStart(Application app) {
        org.iatoki.judgels.michael.BuildInfo$ buildInfo = org.iatoki.judgels.michael.BuildInfo$.MODULE$;
        JudgelsProperties.buildInstance(buildInfo.name(), buildInfo.version(), ConfigFactory.load());

        super.onStart(app);
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        if (!controllersRegistry.containsKey(controllerClass)) {
            return super.getControllerInstance(controllerClass);
        } else {
            return controllerClass.cast(controllersRegistry.get(controllerClass));
        }
    }

}