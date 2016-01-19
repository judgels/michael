package org.iatoki.judgels.michael.machine.watcher;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.machine.Machine;

public final class AWSEC2NetworkInWatcherConfAdapter extends AbstractAWSEC2ConfAdapter {

    @Override
    public MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf) {
        return new AWSEC2NetworkInWatcherAdapter(machine, new Gson().fromJson(conf, AWSEC2WatcherConf.class));
    }
}
