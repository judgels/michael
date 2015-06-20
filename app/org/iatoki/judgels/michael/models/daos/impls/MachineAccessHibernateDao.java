package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.MachineAccessDao;
import org.iatoki.judgels.michael.models.entities.MachineAccessModel;

public final class MachineAccessHibernateDao extends AbstractHibernateDao<Long, MachineAccessModel> implements MachineAccessDao {

    public MachineAccessHibernateDao() {
        super(MachineAccessModel.class);
    }
}
