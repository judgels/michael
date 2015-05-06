package org.iatoki.judgels.michael.controllers;

import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.centerLayout;
import org.iatoki.judgels.michael.LoginForm;
import org.iatoki.judgels.michael.views.html.indexView;
import play.data.Form;
import play.mvc.Result;

public final class UserController extends BaseController {

    public Result index() {
        return redirect(routes.UserController.login());
    }

    public Result login() {
        Form<LoginForm> loginForm = Form.form(LoginForm.class);

        return showLogin(loginForm);
    }

    public Result postLogin() {
        Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return showLogin(loginForm);
        } else {
            session().clear();
            session("username", "michael");
            return redirect(routes.DashboardController.index());
        }
    }

    public Result logout() {
        session().clear();
        return redirect("/");
    }

    private Result showLogin(Form<LoginForm> form) {
        LazyHtml content = new LazyHtml(indexView.render(form));
        content.appendLayout(c -> centerLayout.render(c));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Login");

        return ControllerUtils.getInstance().lazyOk(content);
    }
}
