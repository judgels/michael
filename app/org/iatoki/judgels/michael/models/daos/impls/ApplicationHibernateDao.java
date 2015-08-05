package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.play.models.daos.impls.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.models.daos.ApplicationDao;
import org.iatoki.judgels.michael.models.entities.ApplicationModel;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("applicationDao")
public final class ApplicationHibernateDao extends AbstractJudgelsHibernateDao<ApplicationModel> implements ApplicationDao {

    public ApplicationHibernateDao() {
        super(ApplicationModel.class);
    }
}
