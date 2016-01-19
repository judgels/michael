package org.iatoki.judgels.michael.operation;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

@ImplementedBy(OperationServiceImpl.class)
public interface OperationService {

    Page<Operation> getPageOfOperations(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Operation findOperationById(long operationId) throws OperationNotFoundException;

    void createOperation(String name, OperationType types, String conf);

    void updateOperation(long operationId, String name, OperationType types, String conf) throws OperationNotFoundException;
}
