package org.iatoki.judgels.michael.models.dao.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.Dao;
import org.iatoki.judgels.michael.models.domains.MachineWatcherModel;

public interface MachineWatcherDao extends Dao<Long, MachineWatcherModel> {

    boolean existByMachineJidAndWatcherType(String machineJid, String watcherType);

    MachineWatcherModel findByMachineJidAndWatcherType(String machineJid, String watcherType);

}
