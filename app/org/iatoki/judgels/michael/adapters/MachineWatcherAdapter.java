package org.iatoki.judgels.michael.adapters;

import org.iatoki.judgels.michael.MachineWatcherType;
import play.twirl.api.Html;

public interface MachineWatcherAdapter {

    Html renderWatcher();

    MachineWatcherType getType();

}
