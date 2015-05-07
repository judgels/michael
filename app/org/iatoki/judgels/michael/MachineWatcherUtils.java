package org.iatoki.judgels.michael;

public final class MachineWatcherUtils {

    public static MachineWatcherConfAdapter getMachineWatcherConfAdapter(Machine machine, MachineWatcherTypes watcherTypes) {
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

    private static MachineWatcherConfAdapter getAWSEC2ConfAdapter(MachineWatcherTypes watcherTypes) {
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
