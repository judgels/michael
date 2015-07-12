package org.iatoki.judgels.michael.config;

import org.iatoki.judgels.play.config.JudgelsAbstractModule;

public class MichaelModule extends JudgelsAbstractModule {

    @Override
    protected String getDaosImplPackage() {
        return "org.iatoki.judgels.michael.models.daos.impls";
    }

    @Override
    protected String getServicesImplPackage() {
        return "org.iatoki.judgels.michael.services.impls";
    }
}
