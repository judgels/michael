package org.iatoki.judgels.michael.models.daos.impls;

import org.iatoki.judgels.commons.models.daos.hibernate.AbstractHibernateDao;
import org.iatoki.judgels.michael.models.daos.MachineWatcherDao;
import org.iatoki.judgels.michael.models.entities.MachineWatcherModel;
import org.iatoki.judgels.michael.models.entities.MachineWatcherModel_;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public final class MachineWatcherHibernateDao extends AbstractHibernateDao<Long, MachineWatcherModel> implements MachineWatcherDao {

    public MachineWatcherHibernateDao() {
        super(MachineWatcherModel.class);
    }

    @Override
    public boolean existByMachineJidAndWatcherType(String machineJid, String watcherType) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MachineWatcherModel> root = query.from(MachineWatcherModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(MachineWatcherModel_.machineJid), machineJid), cb.equal(root.get(MachineWatcherModel_.type), watcherType)));

        return JPA.em().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public MachineWatcherModel findByMachineJidAndWatcherType(String machineJid, String watcherType) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<MachineWatcherModel> query = cb.createQuery(MachineWatcherModel.class);
        Root<MachineWatcherModel> root = query.from(MachineWatcherModel.class);

        query.where(cb.and(cb.equal(root.get(MachineWatcherModel_.machineJid), machineJid), cb.equal(root.get(MachineWatcherModel_.type), watcherType)));

        return JPA.em().createQuery(query).getSingleResult();
    }
}
