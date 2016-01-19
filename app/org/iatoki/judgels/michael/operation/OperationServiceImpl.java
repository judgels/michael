package org.iatoki.judgels.michael.operation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class OperationServiceImpl implements OperationService {

    private final OperationDao operationDao;

    @Inject
    public OperationServiceImpl(OperationDao operationDao) {
        this.operationDao = operationDao;
    }

    @Override
    public Page<Operation> getPageOfOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = operationDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<OperationModel> operationModels = operationDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Operation> operations = Lists.transform(operationModels, m -> createOperationFromModel(m));

        return new Page<>(operations, totalPages, pageIndex, pageSize);
    }

    @Override
    public Operation findOperationById(long operationId) throws OperationNotFoundException {
        OperationModel operationModel = operationDao.findById(operationId);
        if (operationModel != null) {
            return createOperationFromModel(operationModel);
        } else {
            throw new OperationNotFoundException("Operation not found.");
        }
    }

    @Override
    public void createOperation(String name, OperationType types, String conf) {
        OperationModel operationModel = new OperationModel();
        operationModel.name = name;
        operationModel.type = types.name();
        operationModel.conf = conf;

        operationDao.persist(operationModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateOperation(long operationId, String name, OperationType types, String conf) throws OperationNotFoundException {
        OperationModel operationModel = operationDao.findById(operationId);
        if (operationModel != null) {
            operationModel.name = name;
            operationModel.type = types.name();
            operationModel.conf = conf;

            operationDao.edit(operationModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new OperationNotFoundException("Operation not found.");
        }
    }

    private Operation createOperationFromModel(OperationModel operationModel) {
        return new Operation(operationModel.id, operationModel.jid, operationModel.name, operationModel.type, operationModel.conf);
    }
}
