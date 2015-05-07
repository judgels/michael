package org.iatoki.judgels.michael.models.dao.interfaces;

import org.iatoki.judgels.commons.models.daos.interfaces.JudgelsDao;
import org.iatoki.judgels.michael.models.domains.MachineModel;

import java.util.Collection;
import java.util.List;

public interface MachineDao extends JudgelsDao<MachineModel> {

    List<MachineModel> findMachinesNotInMachineJids(Collection<String> machineJids);
}
