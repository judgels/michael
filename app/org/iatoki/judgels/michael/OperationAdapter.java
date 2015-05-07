package org.iatoki.judgels.michael;

import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

import java.util.List;

public interface OperationAdapter {

    Form generateConfForm();

    Form generateConfForm(String name, String conf);

    Html getConfHtml(Form form, Call target, String submitLabel);

    Form bindConfFormFromRequest(Http.Request request);

    String getNameFromConfForm(Form form);

    String processConfForm(Form form);

    Form generateRunForm();

    Html getRunHtml(Form form, Call target, String submitLabel, List<Machine> machineList, List<Application> applicationList);

    Form bindRunFormFromRequest(Http.Request request);

    boolean runOperation(Form form, MachineService machineService, MachineAccessService machineAccessService, ApplicationService applicationService, ApplicationVersionService applicationVersionService, String conf);

}
