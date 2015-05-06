package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.OperationDao;
import org.iatoki.judgels.michael.models.domains.OperationModel;

public final class OperationHibernateDao extends AbstractJudgelsHibernateDao<OperationModel> implements OperationDao {

    public OperationHibernateDao() {
        super(OperationModel.class);
    }
}
