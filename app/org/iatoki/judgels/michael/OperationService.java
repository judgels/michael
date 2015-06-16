package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

public interface OperationService {

    Page<Operation> pageOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Operation findByOperationId(long operationId) throws OperationNotFoundException;

    void createOperation(String name, OperationType types, String conf);

    void updateOperation(long operationId, String name, OperationType types, String conf) throws OperationNotFoundException;
}
