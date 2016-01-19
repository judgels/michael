package org.iatoki.judgels.michael.machine.watcher;

import play.twirl.api.Html;

public interface MachineWatcherAdapter {

    Html renderWatcher();

    MachineWatcherType getType();
}
