package org.iatoki.judgels.michael.machine.access;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(MachineAccessHibernateDao.class)
public interface MachineAccessDao extends Dao<Long, MachineAccessModel> {

}
