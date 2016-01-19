package org.iatoki.judgels.michael.controllers.securities;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public final class LoggedIn extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        if ((ctx.session().get("username") == null) || !"michael".equals(ctx.session().get("username"))) {
            return null;
        }

        return ctx.session().get("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(org.iatoki.judgels.michael.user.routes.UserController.login());
    }

}
