package org.iatoki.judgels.michael.machine;

import org.iatoki.judgels.play.model.AbstractJudgelsHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

@Singleton
public final class MachineHibernateDao extends AbstractJudgelsHibernateDao<MachineModel> implements MachineDao {

    public MachineHibernateDao() {
        super(MachineModel.class);
    }

    @Override
    public List<MachineModel> getMachinesNotInJids(Collection<String> machineJids) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<MachineModel> query = cb.createQuery(MachineModel.class);
        Root<MachineModel> root = query.from(MachineModel.class);

        if (machineJids.size() > 0) {
            query.where(cb.not(root.get(MachineModel_.jid).in(machineJids)));
        }

        return JPA.em().createQuery(query).getResultList();
    }
}

