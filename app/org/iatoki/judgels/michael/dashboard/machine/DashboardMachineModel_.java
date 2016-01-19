package org.iatoki.judgels.michael.dashboard.machine;

import org.iatoki.judgels.play.model.AbstractModel_;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DashboardMachineModel.class)
public abstract class DashboardMachineModel_ extends AbstractModel_ {

    public static volatile SingularAttribute<DashboardMachineModel, Long> id;
    public static volatile SingularAttribute<DashboardMachineModel, String> dashboardJid;
    public static volatile SingularAttribute<DashboardMachineModel, String> machineJid;
}
