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
            case STATE: {
                confAdapter = new AWSEC2StateWatcherConfAdapter();
                break;
            }
            default: break;
        }

        return confAdapter;
    }
}
