package org.iatoki.judgels.michael.machine;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

import java.util.Collection;
import java.util.List;

@ImplementedBy(MachineHibernateDao.class)
public interface MachineDao extends JudgelsDao<MachineModel> {

    List<MachineModel> getMachinesNotInJids(Collection<String> machineJids);
}
