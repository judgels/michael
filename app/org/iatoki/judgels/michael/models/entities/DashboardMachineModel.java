package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractModel;

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
