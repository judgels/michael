package org.iatoki.judgels.michael;

import com.google.gson.Gson;

public final class AWSEC2DiskReadBytesWatcherConfAdapter extends AbstractAWSEC2ConfAdapter {

    @Override
    public MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf) {
        return new AWSEC2DiskReadBytesWatcherAdapter(machine, new Gson().fromJson(conf, AWSEC2WatcherConf.class));
    }
}
