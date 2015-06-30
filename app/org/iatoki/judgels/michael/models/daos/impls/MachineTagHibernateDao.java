package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.MachineTagDao;
import org.iatoki.judgels.michael.models.entities.MachineTagModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("machineTagDao")
public final class MachineTagHibernateDao extends AbstractHibernateDao<Long, MachineTagModel> implements MachineTagDao {

    public MachineTagHibernateDao() {
        super(MachineTagModel.class);
    }
}
