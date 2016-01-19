package org.iatoki.judgels.michael.operation;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class OperationHibernateDao extends AbstractJudgelsHibernateDao<OperationModel> implements OperationDao {

    public OperationHibernateDao() {
        super(OperationModel.class);
    }
}
