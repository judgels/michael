package org.iatoki.judgels.michael;

public final class OperationUtils {

    public static OperationAdapter getOperationAdapter(OperationType operationTypes) {
        OperationAdapter adapter = null;
        switch (operationTypes) {
            case ONE_MACHINE_EXEC: {
                adapter = new OperationOneMachineExecAdapter();
                break;
            }
            case ONE_MACHINE_ONE_APP_EXEC: {
                adapter = new OperationOneMachineOneAppExecAdapter();
                break;
            }
            case TWO_MACHINE_COPY: {
                adapter = new OperationTwoMachineCopyAdapter();
                break;
            }
            default: break;
        }

        return adapter;
    }
}
