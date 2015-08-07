package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.services.impls.MichaelDataMigrationServiceImpl;
import org.iatoki.judgels.play.AbstractGlobal;
import org.iatoki.judgels.play.services.BaseDataMigrationService;
import play.Application;

public final class Global extends AbstractGlobal {

    @Override
    public void onStart(Application application) {
        super.onStart(application);
    }

    @Override
    protected BaseDataMigrationService getDataMigrationService() {
        return new MichaelDataMigrationServiceImpl();
    }
}
