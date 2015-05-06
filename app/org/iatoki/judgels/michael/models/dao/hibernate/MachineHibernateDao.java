package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.MachineDao;
import org.iatoki.judgels.michael.models.domains.MachineModel;

public final class MachineHibernateDao extends AbstractJudgelsHibernateDao<MachineModel> implements MachineDao {

    public MachineHibernateDao() {
        super(MachineModel.class);
    }
}
