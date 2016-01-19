package org.iatoki.judgels.michael.machine.access;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class MachineAccessHibernateDao extends AbstractHibernateDao<Long, MachineAccessModel> implements MachineAccessDao {

    public MachineAccessHibernateDao() {
        super(MachineAccessModel.class);
    }
}
