package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_application")
@JidPrefix("MIAP")
public class ApplicationModel extends AbstractJudgelsModel {

    public String name;

    public String type;

}
