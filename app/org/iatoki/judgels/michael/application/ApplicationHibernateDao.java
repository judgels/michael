package org.iatoki.judgels.michael.application;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import org.iatoki.judgels.michael.application.version.ApplicationDao;

import javax.inject.Singleton;

@Singleton
public final class ApplicationHibernateDao extends AbstractJudgelsHibernateDao<ApplicationModel> implements ApplicationDao {

    public ApplicationHibernateDao() {
        super(ApplicationModel.class);
    }
}
