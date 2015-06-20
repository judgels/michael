package org.iatoki.judgels.michael;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.controllers.forms.MachineAccessKeyConfForm;
import org.iatoki.judgels.michael.views.html.machine.access.keyMachineAccessConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public final class MachineAccessKeyConfAdapter implements MachineAccessConfAdapter {

    @Override
    public Form generateForm() {
        return Form.form(MachineAccessKeyConfForm.class);
    }

    @Override
    public Form generateForm(String conf, String name) {
        MachineAccessKeyConf accessKeyConf = new Gson().fromJson(conf, MachineAccessKeyConf.class);
        MachineAccessKeyConfForm accessKeyConfForm = new MachineAccessKeyConfForm();
        accessKeyConfForm.name = name;
        accessKeyConfForm.username = accessKeyConf.username;
        accessKeyConfForm.key = "*************";
        accessKeyConfForm.port = accessKeyConf.port;

        Form form = Form.form(MachineAccessKeyConfForm.class).fill(accessKeyConfForm);
        return form;
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return keyMachineAccessConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(Http.Request request) {
        return Form.form(MachineAccessKeyConfForm.class).bindFromRequest(request);
    }

    @Override
    public String getNameFromForm(Form form) {
        Form<MachineAccessKeyConfForm> realForm = (Form<MachineAccessKeyConfForm>)form;
        MachineAccessKeyConfForm accessKeyConfForm = realForm.get();

        return accessKeyConfForm.name;
    }

    @Override
    public String processRequestForm(Form form) {
        Form<MachineAccessKeyConfForm> realForm = (Form<MachineAccessKeyConfForm>)form;
        MachineAccessKeyConfForm accessKeyConfForm = realForm.get();
        MachineAccessKeyConf accessKeyConf = new MachineAccessKeyConf();
        accessKeyConf.username = accessKeyConfForm.username;
        accessKeyConf.key = accessKeyConfForm.key;
        accessKeyConf.port = accessKeyConfForm.port;

        return new Gson().toJson(accessKeyConf);
    }
}
