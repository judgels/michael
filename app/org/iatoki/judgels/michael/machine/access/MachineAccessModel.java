package org.iatoki.judgels.michael.machine.access;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "michael_machine_access")
public class MachineAccessModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String machineJid;

    public String name;

    public String type;

    @Column(columnDefinition = "TEXT")
    public String conf;
}
