package org.iatoki.judgels.michael;

import play.twirl.api.Html;

public interface MachineWatcherAdapter {

    Html renderWatcher();

    MachineWatcherType getType();

}
