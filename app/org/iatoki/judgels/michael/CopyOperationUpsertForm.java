package org.iatoki.judgels.michael;

import play.data.validation.Constraints;

public final class CopyOperationUpsertForm {

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String type;

    @Constraints.Required
    public String file1;

    @Constraints.Required
    public String file2;
}
