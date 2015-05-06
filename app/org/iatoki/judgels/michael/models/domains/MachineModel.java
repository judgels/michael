package org.iatoki.judgels.michael.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_machine")
@JidPrefix("MIMA")
public class MachineModel extends AbstractJudgelsModel {

    public String instanceName;

    public String displayName;

    public String baseDir;

    public String type;

    public String ipAddress;
}
