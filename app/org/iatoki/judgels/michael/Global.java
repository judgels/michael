package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.config.ControllerConfig;
import org.iatoki.judgels.michael.config.PersistenceConfig;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.Application;

import java.util.Optional;

public final class Global extends org.iatoki.judgels.commons.Global {

    private ApplicationContext applicationContext;

    @Override
    public void onStart(Application application) {
        applicationContext = new AnnotationConfigApplicationContext(PersistenceConfig.class, ControllerConfig.class);
        super.onStart(application);
    }

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        return getContextBean(controllerClass).orElse(super.getControllerInstance(controllerClass));
    }

    private <A> Optional<A> getContextBean(Class<A> controllerClass) throws Exception {
        if (applicationContext == null) {
            throw new Exception("Application Context not Initialized");
        } else {
            try {
                return Optional.of(applicationContext.getBean(controllerClass));
            } catch (NoSuchBeanDefinitionException ex) {
                return Optional.empty();
            }
        }
    }
}