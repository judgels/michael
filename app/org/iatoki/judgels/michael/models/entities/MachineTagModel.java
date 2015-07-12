package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.play.models.domains.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "michael_machine_tag")
public class MachineTagModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String machineJid;

    public String tag;

}
