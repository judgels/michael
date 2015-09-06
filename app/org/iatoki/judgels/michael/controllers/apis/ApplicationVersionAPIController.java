package org.iatoki.judgels.michael.controllers.apis;

import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
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
        DynamicForm dForm = DynamicForm.form().bindFromRequest();
        String applicationJid = dForm.get("applicationJid");
        if (applicationJid == null) {
            return badRequest();
        }

        return ok(Json.toJson(applicationVersionService.getApplicationVersionByApplicationJid(applicationJid)).toString());
    }
}
