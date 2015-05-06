package org.iatoki.judgels.michael;

import com.amazonaws.regions.Regions;
import com.google.gson.Gson;
import org.iatoki.judgels.michael.views.html.machines.watchers.awsEC2ConfView;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Http;
import play.twirl.api.Html;

public abstract class AbstractAWSEC2ConfFactory implements MachineWatcherConfFactory {

    @Override
    public Form generateForm() {
        return Form.form(AWSEC2WatcherConfForm.class);
    }

    @Override
    public Form generateForm(String conf) {
        AWSEC2WatcherConf awsec2WatcherConf = new Gson().fromJson(conf, AWSEC2WatcherConf.class);
        AWSEC2WatcherConfForm awsec2WatcherConfForm = new AWSEC2WatcherConfForm();
        awsec2WatcherConfForm.useKeyCredential = awsec2WatcherConf.useKeyCredential;
        awsec2WatcherConfForm.accessKey = "*************";
        awsec2WatcherConfForm.secretKey = "*************";
        awsec2WatcherConfForm.regionId = awsec2WatcherConf.regionId;

        Form form = Form.form(AWSEC2WatcherConfForm.class).fill(awsec2WatcherConfForm);
        return form;
    }

    @Override
    public Html getConfHtml(Form form, Call target) {
        return awsEC2ConfView.render(form, target);
    }

    @Override
    public Form bindFormFromRequest(Http.Request request) {
        return Form.form(AWSEC2WatcherConfForm.class).bindFromRequest(request);
    }

    @Override
    public String proccessRequestForm(Form form) {
        Form<AWSEC2WatcherConfForm> realForm = (Form<AWSEC2WatcherConfForm>)form;
        AWSEC2WatcherConfForm watcherConfForm = realForm.get();
        AWSEC2WatcherConf awsec2WatcherConf = new AWSEC2WatcherConf();
        awsec2WatcherConf.regionId = Regions.valueOf(watcherConfForm.regionId).name();
        awsec2WatcherConf.accessKey = watcherConfForm.accessKey;
        awsec2WatcherConf.secretKey = watcherConfForm.secretKey;
        awsec2WatcherConf.useKeyCredential = watcherConfForm.useKeyCredential;

        return new Gson().toJson(awsec2WatcherConf);
    }
}
