package org.iatoki.judgels.michael.dashboard;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "michael_dashboard")
@JidPrefix("MIDA")
public class DashboardModel extends AbstractJudgelsModel {

    public String name;

    public String description;
}
