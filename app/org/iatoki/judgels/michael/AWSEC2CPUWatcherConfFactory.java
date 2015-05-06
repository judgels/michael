package org.iatoki.judgels.michael;

import com.google.gson.Gson;

public final class AWSEC2CPUWatcherConfFactory extends AbstractAWSEC2ConfFactory {

    @Override
    public MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf) {
        return new AWSEC2CPUWatcherAdapter(machine, new Gson().fromJson(conf, AWSEC2WatcherConf.class));
    }
}
