package org.iatoki.judgels.michael.machine;

import org.iatoki.judgels.play.model.AbstractJudgelsModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(MachineModel.class)
public abstract class MachineModel_ extends AbstractJudgelsModel_ {

    public static volatile SingularAttribute<MachineModel, String> instanceName;
    public static volatile SingularAttribute<MachineModel, String> displayName;
    public static volatile SingularAttribute<MachineModel, String> baseDir;
    public static volatile SingularAttribute<MachineModel, String> type;
    public static volatile SingularAttribute<MachineModel, String> ipAddress;
}
