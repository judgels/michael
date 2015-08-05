package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.play.models.daos.impls.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.MachineAccessDao;
import org.iatoki.judgels.michael.models.entities.MachineAccessModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("machineAccessDao")
public final class MachineAccessHibernateDao extends AbstractHibernateDao<Long, MachineAccessModel> implements MachineAccessDao {

    public MachineAccessHibernateDao() {
        super(MachineAccessModel.class);
    }
}
