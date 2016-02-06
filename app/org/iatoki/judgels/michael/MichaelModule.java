package org.iatoki.judgels.michael;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.iatoki.judgels.play.JudgelsPlayProperties;
import org.iatoki.judgels.play.general.GeneralName;
import org.iatoki.judgels.play.general.GeneralVersion;
import org.iatoki.judgels.play.migration.JudgelsDataMigrator;

public class MichaelModule extends AbstractModule {

    @Override
    public void configure() {
        org.iatoki.judgels.michael.BuildInfo$ buildInfo = org.iatoki.judgels.michael.BuildInfo$.MODULE$;

        bindConstant().annotatedWith(GeneralName.class).to(buildInfo.name());
        bindConstant().annotatedWith(GeneralVersion.class).to(buildInfo.version());

        // <DEPRECATED>
        Config config = ConfigFactory.load();
        JudgelsPlayProperties.buildInstance(buildInfo.name(), buildInfo.version(), config);
        MichaelProperties.buildInstance(config);
        // </DEPRECATED>

        bind(JudgelsDataMigrator.class).to(MichaelDataMigrator.class);
    }
}
