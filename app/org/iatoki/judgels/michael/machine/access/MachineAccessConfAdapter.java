package org.iatoki.judgels.michael.machine.access;

import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public interface MachineAccessConfAdapter {

    Form generateForm();

    Form generateForm(String conf, String name);

    Html getConfHtml(Form form, Call target, String submitLabel);

    Form bindFormFromRequest(Http.Request request);

    String getNameFromForm(Form form);

    String processRequestForm(Form form);
}
