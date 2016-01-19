package org.iatoki.judgels.michael.machine.watcher;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(MachineWatcherHibernateDao.class)
public interface MachineWatcherDao extends Dao<Long, MachineWatcherModel> {

    boolean existsByMachineJidAndWatcherType(String machineJid, String watcherType);

    MachineWatcherModel findByMachineJidAndWatcherType(String machineJid, String watcherType);
}
