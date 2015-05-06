package org.iatoki.judgels.michael.models.domains;

import org.iatoki.judgels.commons.models.JidPrefix;
import org.iatoki.judgels.commons.models.domains.AbstractJudgelsModel;
import org.iatoki.judgels.commons.models.domains.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "michael_dashboard_machine")
public class DashboardMachineModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String dashboardJid;

    public String machineJid;

}
