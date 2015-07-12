package org.iatoki.judgels.michael.models.daos;

import org.iatoki.judgels.play.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.michael.models.entities.MachineModel;

import java.util.Collection;
import java.util.List;

public interface MachineDao extends JudgelsDao<MachineModel> {

    List<MachineModel> findMachinesNotInMachineJids(Collection<String> machineJids);
}
