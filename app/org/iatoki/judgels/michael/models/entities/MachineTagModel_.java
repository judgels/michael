package org.iatoki.judgels.michael.models.entities;

import org.iatoki.judgels.play.models.entities.AbstractModel_;

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

