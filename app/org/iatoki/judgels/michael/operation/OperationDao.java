package org.iatoki.judgels.michael.operation;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(OperationHibernateDao.class)
public interface OperationDao extends JudgelsDao<OperationModel> {

}
