package org.iatoki.judgels.michael.forms;

import play.data.validation.Constraints;

public final class AWSEC2WatcherConfForm {

    @Constraints.Required
    public boolean useKeyCredential;

    public String accessKey;

    public String secretKey;

    @Constraints.Required
    public String regionId;
}
