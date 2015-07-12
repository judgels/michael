package org.iatoki.judgels.michael;

import org.iatoki.judgels.michael.adapters.impls.AWSEC2CPUWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2DiskReadBytesWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2DiskReadOpsWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2DiskWriteBytesWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2DiskWriteOpsWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2NetworkInWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2NetworkOutWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.impls.AWSEC2StateWatcherConfAdapter;
import org.iatoki.judgels.michael.adapters.MachineWatcherConfAdapter;

public final class MachineWatcherUtils {

    public static MachineWatcherConfAdapter getMachineWatcherConfAdapter(Machine machine, MachineWatcherType watcherTypes) {
        MachineWatcherConfAdapter confAdapter = null;
        switch (machine.getType()) {
            case AWS_EC2: {
                confAdapter = getAWSEC2ConfAdapter(watcherTypes);
                break;
            }
            case BARE_METAL: {
                break;
            }
            default: break;
        }

        return confAdapter;
    }

    private static MachineWatcherConfAdapter getAWSEC2ConfAdapter(MachineWatcherType watcherTypes) {
        MachineWatcherConfAdapter confAdapter = null;
        switch (watcherTypes) {
            case CPU: {
                confAdapter = new AWSEC2CPUWatcherConfAdapter();
                break;
            }
            case DISK_READ_BYTES: {
                confAdapter = new AWSEC2DiskReadBytesWatcherConfAdapter();
                break;
            }
            case DISK_READ_OPS: {
                confAdapter = new AWSEC2DiskReadOpsWatcherConfAdapter();
                break;
            }
            case DISK_WRITE_BYTES: {
                confAdapter = new AWSEC2DiskWriteBytesWatcherConfAdapter();
                break;
            }
            case DISK_WRITE_OPS: {
                confAdapter = new AWSEC2DiskWriteOpsWatcherConfAdapter();
                break;
            }
            case NETWORK_IN: {
                confAdapter = new AWSEC2NetworkInWatcherConfAdapter();
                break;
            }
            case NETWORK_OUT: {
                confAdapter = new AWSEC2NetworkOutWatcherConfAdapter();
                break;
            }
            case STATE: {
                confAdapter = new AWSEC2StateWatcherConfAdapter();
                break;
            }
            default: break;
        }

        return confAdapter;
    }
}
