package org.iatoki.judgels.michael.application.version;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ApplicationVersionModel.class)
public abstract class ApplicationVersionModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<ApplicationVersionModel, Long> id;
    public static volatile SingularAttribute<ApplicationVersionModel, String> applicationJid;
    public static volatile SingularAttribute<ApplicationVersionModel, String> name;
}

