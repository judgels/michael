package org.iatoki.judgels.michael.machine.watcher;

import org.iatoki.judgels.michael.machine.Machine;
import play.data.Form;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

public interface MachineWatcherConfAdapter {

    Form generateForm();

    Form generateForm(String conf);

    Html getConfHtml(Form form, Call target, String submitLabel);

    Form bindFormFromRequest(Http.Request request);

    String processRequestForm(Form form);

    MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf);
}
