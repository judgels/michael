package org.iatoki.judgels.michael.machine.tag;

import org.iatoki.judgels.play.model.AbstractHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class MachineTagHibernateDao extends AbstractHibernateDao<Long, MachineTagModel> implements MachineTagDao {

    public MachineTagHibernateDao() {
        super(MachineTagModel.class);
    }
}
