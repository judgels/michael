package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractJudgelsModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OperationModel.class)
public abstract class OperationModel_ extends AbstractJudgelsModel_ {

    public static volatile SingularAttribute<OperationModel, String> name;
    public static volatile SingularAttribute<OperationModel, String> type;
    public static volatile SingularAttribute<OperationModel, String> conf;
}
