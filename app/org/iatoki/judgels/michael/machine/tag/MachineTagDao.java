package org.iatoki.judgels.michael.machine.tag;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(MachineTagHibernateDao.class)
public interface MachineTagDao extends Dao<Long, MachineTagModel> {

}
