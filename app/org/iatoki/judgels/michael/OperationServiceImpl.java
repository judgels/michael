package org.iatoki.judgels.michael;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.models.dao.interfaces.OperationDao;
import org.iatoki.judgels.michael.models.domains.OperationModel;

import java.util.List;

public final class OperationServiceImpl implements OperationService {

    private final OperationDao operationDao;

    public OperationServiceImpl(OperationDao operationDao) {
        this.operationDao = operationDao;
    }

    @Override
    public Page<Operation> pageOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = operationDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<OperationModel> operationModels = operationDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        List<Operation> operations = Lists.transform(operationModels, m -> createOperationFromModel(m));

        return new Page<>(operations, totalPages, pageIndex, pageSize);
    }

    @Override
    public Operation findByOperationId(long operationId) throws OperationNotFoundException {
        OperationModel operationModel = operationDao.findById(operationId);
        if (operationModel != null) {
            return createOperationFromModel(operationModel);
        } else {
            throw new OperationNotFoundException("Operation not found.");
        }
    }

    @Override
    public void createSingleMachineOperation(String name, SingleMachineOperationTypes operationTypes, String command) {
        SingleMachineOperationCommand operationCommand = new SingleMachineOperationCommand(command);
        OperationModel operationModel = new OperationModel();
        operationModel.name = name;
        operationModel.type = operationTypes.name();
        operationModel.command = new Gson().toJson(operationCommand);

        operationDao.persist(operationModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateSingleMachineOperation(long operationId, String name, SingleMachineOperationTypes operationTypes, String command) throws OperationNotFoundException {
        OperationModel operationModel = operationDao.findById(operationId);
        if (operationModel != null) {
            SingleMachineOperationCommand operationCommand = new SingleMachineOperationCommand(command);
            operationModel.name = name;
            operationModel.type = operationTypes.name();
            operationModel.command = new Gson().toJson(operationCommand);

            operationDao.edit(operationModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new OperationNotFoundException("Operation not found.");
        }
    }

    @Override
    public void createCopyOperation(String name, CopyOperationTypes operationTypes, String file1, String file2) {
        CopyOperationCommand operationCommand = new CopyOperationCommand(file1, file2);
        OperationModel operationModel = new OperationModel();
        operationModel.name = name;
        operationModel.type = operationTypes.name();
        operationModel.command = new Gson().toJson(operationCommand);

        operationDao.persist(operationModel, "michael", IdentityUtils.getIpAddress());
    }

    @Override
    public void updateCopyOperation(long operationId, String name, CopyOperationTypes operationTypes, String file1, String file2) throws OperationNotFoundException {
        OperationModel operationModel = operationDao.findById(operationId);
        if (operationModel != null) {
            CopyOperationCommand operationCommand = new CopyOperationCommand(file1, file2);
            operationModel.name = name;
            operationModel.type = operationTypes.name();
            operationModel.command = new Gson().toJson(operationCommand);

            operationDao.edit(operationModel, "michael", IdentityUtils.getIpAddress());
        } else {
            throw new OperationNotFoundException("Operation not found.");
        }
    }

    private Operation createOperationFromModel(OperationModel operationModel) {
        return new Operation(operationModel.id, operationModel.jid, operationModel.name, operationModel.type, operationModel.command);
    }
}
