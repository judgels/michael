package org.iatoki.judgels.michael.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.iatoki.judgels.commons.JudgelsProperties;
import org.iatoki.judgels.michael.MichaelProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "org.iatoki.judgels.michael.models.daos",
        "org.iatoki.judgels.michael.services",
})
public class PersistenceConfig {

    @Bean
    public JudgelsProperties judgelsProperties() {
        org.iatoki.judgels.michael.BuildInfo$ buildInfo = org.iatoki.judgels.michael.BuildInfo$.MODULE$;
        JudgelsProperties.buildInstance(buildInfo.name(), buildInfo.version(), ConfigFactory.load());
        return JudgelsProperties.getInstance();
    }

    @Bean
    public MichaelProperties michaelProperties() {
        Config config = ConfigFactory.load();
        MichaelProperties.buildInstance(config);
        return MichaelProperties.getInstance();
    }
}
