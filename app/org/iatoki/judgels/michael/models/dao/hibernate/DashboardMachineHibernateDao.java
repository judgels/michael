package org.iatoki.judgels.michael.models.dao.hibernate;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.dao.interfaces.DashboardMachineDao;
import org.iatoki.judgels.michael.models.domains.DashboardMachineModel;
import org.iatoki.judgels.michael.models.domains.DashboardMachineModel_;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public final class DashboardMachineHibernateDao extends AbstractHibernateDao<Long, DashboardMachineModel> implements DashboardMachineDao {

    public DashboardMachineHibernateDao() {
        super(DashboardMachineModel.class);
    }

    @Override
    public boolean existByDashboardJidAndMachineJid(String dashboardJid, String machineJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<DashboardMachineModel> root = query.from(DashboardMachineModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(DashboardMachineModel_.dashboardJid), dashboardJid)), cb.equal(root.get(DashboardMachineModel_.machineJid), machineJid));

        return JPA.em().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public List<String> findMachineJidsByDashboardJid(String dashboardJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<DashboardMachineModel> root = query.from(DashboardMachineModel.class);

        query.select(root.get(DashboardMachineModel_.machineJid)).where(cb.equal(root.get(DashboardMachineModel_.dashboardJid), dashboardJid));

        return JPA.em().createQuery(query).getResultList();
    }
}
