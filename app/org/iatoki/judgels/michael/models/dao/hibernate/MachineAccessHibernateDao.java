package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineAccessDao;
import org.iatoki.judgels.michael.models.domains.MachineAccessModel;

public final class MachineAccessHibernateDao extends AbstractHibernateDao<Long, MachineAccessModel> implements MachineAccessDao {

    public MachineAccessHibernateDao() {
        super(MachineAccessModel.class);
    }
}
