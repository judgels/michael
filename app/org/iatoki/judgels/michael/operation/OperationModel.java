package org.iatoki.judgels.michael.operation;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_operation")
@JidPrefix(value = "MIOP")
public class OperationModel extends AbstractJudgelsModel {

    public String name;

    public String type;

    @Column(columnDefinition = "TEXT")
    public String conf;
}
