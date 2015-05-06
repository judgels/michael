package org.iatoki.judgels.michael.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_dashboard")
@JidPrefix("MIDA")
public class DashboardModel extends AbstractJudgelsModel {

    public String name;

    public String description;

}
