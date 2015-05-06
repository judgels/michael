package org.iatoki.judgels.michael;

public final class MachineWatcherUtils {

    public static MachineWatcherConfFactory getMachineWatcherConfFactory(Machine machine, MachineWatcherTypes watcherTypes) {
        MachineWatcherConfFactory confFactory = null;
        switch (machine.getType()) {
            case AWS_EC2:
                confFactory = getAWSEC2ConfFactory(watcherTypes);
                break;
            case LOCAL:
                break;
            default: break;
        }

        return confFactory;
    }

    private static MachineWatcherConfFactory getAWSEC2ConfFactory(MachineWatcherTypes watcherTypes) {
        MachineWatcherConfFactory confFactory = null;
        switch (watcherTypes) {
            case CPU:
                confFactory = new AWSEC2CPUWatcherConfFactory();
                break;
            case STATE:
                confFactory = new AWSEC2StateWatcherConfFactory();
                break;
            default: break;
        }

        return confFactory;
    }
}
