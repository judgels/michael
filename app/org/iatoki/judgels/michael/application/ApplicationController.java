package org.iatoki.judgels.michael.application;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.michael.application.html.createApplicationView;
import org.iatoki.judgels.michael.application.html.listApplicationsView;
import org.iatoki.judgels.michael.application.html.updateApplicationGeneralView;
import org.iatoki.judgels.michael.application.html.viewApplicationView;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.MichaelControllerUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
public final class ApplicationController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;

    @Inject
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listApplications(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listApplications(long page, String orderBy, String orderDir, String filterString) {
        Page<Application> pageOfApplications = applicationService.getPageOfApplications(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listApplicationsView.render(pageOfApplications, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("application.list"), new InternalLink(Messages.get("commons.create"), routes.ApplicationController.createApplication()), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index())
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Applications");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result viewApplication(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);

        LazyHtml content = new LazyHtml(viewApplicationView.render(application));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), new InternalLink(Messages.get("commons.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.view"), routes.ApplicationController.viewApplication(application.getId()))
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Application - View");

        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createApplication() {
        Form<ApplicationUpsertForm> applicationUpsertForm = Form.form(ApplicationUpsertForm.class);

        return showCreateApplication(applicationUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateApplication() {
        Form<ApplicationUpsertForm> applicationUpsertForm = Form.form(ApplicationUpsertForm.class).bindFromRequest();

        if (formHasErrors(applicationUpsertForm)) {
            return showCreateApplication(applicationUpsertForm);
        }

        ApplicationUpsertForm applicationUpsertData = applicationUpsertForm.get();
        applicationService.createApplication(applicationUpsertData.name, ApplicationType.valueOf(applicationUpsertData.type));

        return redirect(routes.ApplicationController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateApplicationGeneral(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        ApplicationUpsertForm applicationUpsertData = new ApplicationUpsertForm();
        applicationUpsertData.name = application.getName();
        applicationUpsertData.type = application.getType().name();

        Form<ApplicationUpsertForm> applicationUpsertForm = Form.form(ApplicationUpsertForm.class).fill(applicationUpsertData);

        return showUpdateApplicationGeneral(applicationUpsertForm, application);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateApplicationGeneral(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        Form<ApplicationUpsertForm> applicationUpsertForm = Form.form(ApplicationUpsertForm.class).bindFromRequest();

        if (formHasErrors(applicationUpsertForm)) {
            return showUpdateApplicationGeneral(applicationUpsertForm, application);
        }

        ApplicationUpsertForm applicationUpsertData = applicationUpsertForm.get();
        applicationService.updateApplication(application.getId(), applicationUpsertData.name, ApplicationType.valueOf(applicationUpsertData.type));

        return redirect(routes.ApplicationController.index());
    }

    private Result showCreateApplication(Form<ApplicationUpsertForm> applicationUpsertForm) {
        LazyHtml content = new LazyHtml(createApplicationView.render(applicationUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.create"), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.create"), routes.ApplicationController.createApplication())
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Application - Create");
        return MichaelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateApplicationGeneral(Form<ApplicationUpsertForm> applicationUpsertForm, Application application) {
        LazyHtml content = new LazyHtml(updateApplicationGeneralView.render(applicationUpsertForm, application.getId()));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())),
              new InternalLink(Messages.get("application.version"), org.iatoki.judgels.michael.application.version.routes.ApplicationVersionController.viewApplicationVersions(application.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), c));
        MichaelControllerUtils.getInstance().appendSidebarLayout(content);
        MichaelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId()))
        ));
        MichaelControllerUtils.getInstance().appendTemplateLayout(content, "Application - Update");
        return MichaelControllerUtils.getInstance().lazyOk(content);
    }
}
