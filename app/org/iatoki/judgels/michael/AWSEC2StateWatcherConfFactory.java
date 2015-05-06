package org.iatoki.judgels.michael;

import com.google.gson.Gson;

public final class AWSEC2StateWatcherConfFactory extends AbstractAWSEC2ConfFactory {

    @Override
    public MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf) {
        return new AWSEC2StateWatcherAdapter(machine, new Gson().fromJson(conf, AWSEC2WatcherConf.class));
    }
}
