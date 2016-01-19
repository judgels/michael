package org.iatoki.judgels.michael.machine;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

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
