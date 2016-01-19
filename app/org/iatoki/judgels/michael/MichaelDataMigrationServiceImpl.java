package org.iatoki.judgels.michael;

import org.iatoki.judgels.play.migration.AbstractBaseDataMigrationServiceImpl;

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
