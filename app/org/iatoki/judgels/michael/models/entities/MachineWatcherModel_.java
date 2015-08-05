package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(MachineWatcherModel.class)
public abstract class MachineWatcherModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<MachineWatcherModel, Long> id;
    public static volatile SingularAttribute<MachineWatcherModel, String> machineJid;
    public static volatile SingularAttribute<MachineWatcherModel, String> type;
    public static volatile SingularAttribute<MachineWatcherModel, String> conf;

}

