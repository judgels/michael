package org.iatoki.judgels.michael.services.impls;

import org.iatoki.judgels.play.services.impls.AbstractBaseDataMigrationServiceImpl;

import java.sql.SQLException;

public final class MichaelDataMigrationServiceImpl extends AbstractBaseDataMigrationServiceImpl {

    @Override
    protected void onUpgrade(long databaseVersion, long codeDatabaseVersion) throws SQLException {

    }

    @Override
    public long getCodeDataVersion() {
        return 1;
    }
}
