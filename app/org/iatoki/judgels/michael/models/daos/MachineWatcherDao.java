package org.iatoki.judgels.michael.models.daos;

import org.iatoki.judgels.play.models.daos.interfaces.Dao;
import org.iatoki.judgels.michael.models.entities.MachineWatcherModel;

public interface MachineWatcherDao extends Dao<Long, MachineWatcherModel> {

    boolean existByMachineJidAndWatcherType(String machineJid, String watcherType);

    MachineWatcherModel findByMachineJidAndWatcherType(String machineJid, String watcherType);

}
