package org.iatoki.judgels.michael.user;

import org.iatoki.judgels.michael.MichaelControllerUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.centerLayout;
import org.iatoki.judgels.michael.user.html.indexView;
import play.data.Form;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public final class UserController extends AbstractJudgelsController {

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
        }

        session().clear();
        session("username", "michael");
        return redirect(org.iatoki.judgels.michael.dashboard.routes.DashboardController.index());
    }

    public Result logout() {
        session().clear();
        return redirect("/");
    }

    private Result showLogin(Form<LoginForm> loginForm) {
        LazyHtml content = new LazyHtml(indexView.render(loginForm));
        content.appendLayout(c -> centerLayout.render(c));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Login");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }
}
