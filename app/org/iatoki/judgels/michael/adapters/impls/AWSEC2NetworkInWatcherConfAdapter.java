package org.iatoki.judgels.michael.adapters.impls;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.AWSEC2WatcherConf;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.adapters.MachineWatcherAdapter;

public final class AWSEC2NetworkInWatcherConfAdapter extends AbstractAWSEC2ConfAdapter {

    @Override
    public MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf) {
        return new AWSEC2NetworkInWatcherAdapter(machine, new Gson().fromJson(conf, AWSEC2WatcherConf.class));
    }
}
