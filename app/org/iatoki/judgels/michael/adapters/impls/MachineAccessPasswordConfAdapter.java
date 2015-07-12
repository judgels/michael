package org.iatoki.judgels.michael.adapters.impls;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.MachineAccessPasswordConf;
import org.iatoki.judgels.michael.adapters.MachineAccessConfAdapter;
import org.iatoki.judgels.michael.forms.MachineAccessPasswordConfForm;
import org.iatoki.judgels.michael.views.html.machine.access.passwordMachineAccessConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public final class MachineAccessPasswordConfAdapter implements MachineAccessConfAdapter {

    @Override
    public Form generateForm() {
        return Form.form(MachineAccessPasswordConfForm.class);
    }

    @Override
    public Form generateForm(String conf, String name) {
        MachineAccessPasswordConf accessPasswordConf = new Gson().fromJson(conf, MachineAccessPasswordConf.class);
        MachineAccessPasswordConfForm accessPasswordConfForm = new MachineAccessPasswordConfForm();
        accessPasswordConfForm.name = name;
        accessPasswordConfForm.username = accessPasswordConf.username;
        accessPasswordConfForm.password = "*************";
        accessPasswordConfForm.port = accessPasswordConf.port;

        Form form = Form.form(MachineAccessPasswordConfForm.class).fill(accessPasswordConfForm);
        return form;
    }

    @Override
    public Html getConfHtml(Form form, Call target, String submitLabel) {
        return passwordMachineAccessConfView.render(form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(Http.Request request) {
        return Form.form(MachineAccessPasswordConfForm.class).bindFromRequest(request);
    }

    @Override
    public String getNameFromForm(Form form) {
        Form<MachineAccessPasswordConfForm> realForm = (Form<MachineAccessPasswordConfForm>)form;
        MachineAccessPasswordConfForm accessPasswordConfForm = realForm.get();

        return accessPasswordConfForm.name;
    }

    @Override
    public String processRequestForm(Form form) {
        Form<MachineAccessPasswordConfForm> realForm = (Form<MachineAccessPasswordConfForm>)form;
        MachineAccessPasswordConfForm accessPasswordConfForm = realForm.get();
        MachineAccessPasswordConf accessPasswordConf = new MachineAccessPasswordConf();
        accessPasswordConf.username = accessPasswordConfForm.username;
        accessPasswordConf.password = accessPasswordConfForm.password;
        accessPasswordConf.port = accessPasswordConfForm.port;

        return new Gson().toJson(accessPasswordConf);
    }
}
