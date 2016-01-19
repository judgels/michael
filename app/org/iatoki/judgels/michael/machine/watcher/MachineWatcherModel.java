package org.iatoki.judgels.michael.machine.watcher;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "michael_machine_watcher")
public class MachineWatcherModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String machineJid;

    public String type;

    @Column(columnDefinition = "TEXT")
    public String conf;
}
