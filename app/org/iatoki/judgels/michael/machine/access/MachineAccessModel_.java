package org.iatoki.judgels.michael.machine.access;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(MachineAccessModel.class)
public abstract class MachineAccessModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<MachineAccessModel, Long> id;
    public static volatile SingularAttribute<MachineAccessModel, String> machineJid;
    public static volatile SingularAttribute<MachineAccessModel, String> name;
    public static volatile SingularAttribute<MachineAccessModel, String> type;
    public static volatile SingularAttribute<MachineAccessModel, String> conf;
}
