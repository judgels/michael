package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.play.models.daos.impls.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.OperationDao;
import org.iatoki.judgels.michael.models.entities.OperationModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("operationDao")
public final class OperationHibernateDao extends AbstractJudgelsHibernateDao<OperationModel> implements OperationDao {

    public OperationHibernateDao() {
        super(OperationModel.class);
    }
}
