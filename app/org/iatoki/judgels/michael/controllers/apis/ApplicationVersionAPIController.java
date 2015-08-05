package org.iatoki.judgels.michael.controllers.apis;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class ApplicationVersionAPIController extends Controller {

    private final ApplicationVersionService applicationVersionService;

    @Inject
    public ApplicationVersionAPIController(ApplicationVersionService applicationVersionService) {
        this.applicationVersionService = applicationVersionService;
    }

    @Transactional(readOnly = true)
    public Result versionList() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String applicationJid = form.get("applicationJid");
        if (applicationJid != null) {
            return ok(new Gson().toJson(applicationVersionService.findByApplicationJid(applicationJid)));
        } else {
            return badRequest();
        }
    }
}
