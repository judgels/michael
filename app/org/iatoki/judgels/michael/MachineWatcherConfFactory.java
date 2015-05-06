package org.iatoki.judgels.michael;

import play.data.Form;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

public interface MachineWatcherConfFactory {

    Form generateForm();

    Form generateForm(String conf);

    Html getConfHtml(Form form, Call target);

    Form bindFormFromRequest(Http.Request request);

    String proccessRequestForm(Form form);

    MachineWatcherAdapter createMachineWatcherAdapter(Machine machine, String conf);

}
