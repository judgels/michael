package org.iatoki.judgels.michael;

import org.iatoki.judgels.play.migration.AbstractJudgelsDataMigrator;
import org.iatoki.judgels.play.migration.DataVersionDao;

import javax.inject.Inject;
import java.sql.SQLException;

public final class MichaelDataMigrator extends AbstractJudgelsDataMigrator {

    @Inject
    public MichaelDataMigrator(DataVersionDao dataVersionDao) {
        super(dataVersionDao);
    }

    @Override
    protected void migrate(long currentDataVersion) throws SQLException {

    }

    @Override
    public long getLatestDataVersion() {
        return 1;
    }
}
