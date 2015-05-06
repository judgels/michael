package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineAccessDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineTagDao;
import org.iatoki.judgels.michael.models.domains.MachineAccessModel;
import org.iatoki.judgels.michael.models.domains.MachineTagModel;

public final class MachineTagHibernateDao extends AbstractHibernateDao<Long, MachineTagModel> implements MachineTagDao {

    public MachineTagHibernateDao() {
        super(MachineTagModel.class);
    }
}
