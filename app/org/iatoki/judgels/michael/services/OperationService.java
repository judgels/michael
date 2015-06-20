package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.Operation;
import org.iatoki.judgels.michael.OperationNotFoundException;
import org.iatoki.judgels.michael.OperationType;

public interface OperationService {

    Page<Operation> pageOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Operation findByOperationId(long operationId) throws OperationNotFoundException;

    void createOperation(String name, OperationType types, String conf);

    void updateOperation(long operationId, String name, OperationType types, String conf) throws OperationNotFoundException;
}
