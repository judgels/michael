package org.iatoki.judgels.michael.machine.tag;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(MachineTagModel.class)
public abstract class MachineTagModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<MachineTagModel, Long> id;
    public static volatile SingularAttribute<MachineTagModel, String> machineJid;
    public static volatile SingularAttribute<MachineTagModel, String> tag;
}
