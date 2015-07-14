package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.services.impls.MichaelDataMigrationServiceImpl;
import play.Application;

public final class Global extends org.iatoki.judgels.play.Global {

    public Global() {
        super(new MichaelDataMigrationServiceImpl());
    }

    @Override
    public void onStart(Application application) {
        super.onStart(application);
    }
}