package org.iatoki.judgels.michael.application;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_application")
@JidPrefix("MIAP")
public class ApplicationModel extends AbstractJudgelsModel {

    public String name;

    public String type;
}
