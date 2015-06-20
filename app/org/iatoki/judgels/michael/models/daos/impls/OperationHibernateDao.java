package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.OperationDao;
import org.iatoki.judgels.michael.models.entities.OperationModel;

public final class OperationHibernateDao extends AbstractJudgelsHibernateDao<OperationModel> implements OperationDao {

    public OperationHibernateDao() {
        super(OperationModel.class);
    }
}
