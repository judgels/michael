package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

public interface OperationService {

    Page<Operation> pageOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Operation findByOperationId(long operationId) throws OperationNotFoundException;

    void createSingleMachineOperation(String name, SingleMachineOperationTypes operationTypes, String command);

    void updateSingleMachineOperation(long operationId, String name, SingleMachineOperationTypes operationTypes, String command) throws OperationNotFoundException;

    void createCopyOperation(String name, CopyOperationTypes operationTypes, String file1, String file2);

    void updateCopyOperation(long operationId, String name, CopyOperationTypes operationTypes, String file1, String file2) throws OperationNotFoundException;
}
