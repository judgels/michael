package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationNotFoundException;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.ApplicationType;
import org.iatoki.judgels.michael.controllers.forms.ApplicationUpsertForm;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.application.createApplicationView;
import org.iatoki.judgels.michael.views.html.application.listApplicationsView;
import org.iatoki.judgels.michael.views.html.application.updateApplicationGeneralView;
import org.iatoki.judgels.michael.views.html.application.viewApplicationView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class ApplicationController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public Result index() {
        return listApplications(0, "id", "asc", "");
    }

    public Result listApplications(long page, String orderBy, String orderDir, String filterString) {
        Page<Application> currentPage = applicationService.pageApplications(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listApplicationsView.render(currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("application.list"), new InternalLink(Messages.get("commons.create"), routes.ApplicationController.createApplication()), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Applications");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result viewApplication(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        LazyHtml content = new LazyHtml(viewApplicationView.render(application));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), new InternalLink(Messages.get("commons.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.view"), routes.ApplicationController.viewApplication(application.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Application - View");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @AddCSRFToken
    public Result createApplication() {
        Form<ApplicationUpsertForm> form = Form.form(ApplicationUpsertForm.class);

        return showCreateApplication(form);
    }

    @RequireCSRFCheck
    public Result postCreateApplication() {
        Form<ApplicationUpsertForm> form = Form.form(ApplicationUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateApplication(form);
        } else {
            ApplicationUpsertForm applicationUpsertForm = form.get();
            applicationService.createApplication(applicationUpsertForm.name, ApplicationType.valueOf(applicationUpsertForm.type));

            return redirect(routes.ApplicationController.index());
        }
    }

    @AddCSRFToken
    public Result updateApplicationGeneral(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        ApplicationUpsertForm applicationUpsertForm = new ApplicationUpsertForm();
        applicationUpsertForm.name = application.getName();
        applicationUpsertForm.type = application.getType().name();

        Form<ApplicationUpsertForm> form = Form.form(ApplicationUpsertForm.class).fill(applicationUpsertForm);

        return showUpdateApplicationGeneral(form, application);
    }

    @RequireCSRFCheck
    public Result postUpdateApplicationGeneral(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        Form<ApplicationUpsertForm> form = Form.form(ApplicationUpsertForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return showUpdateApplicationGeneral(form, application);
        } else {
            ApplicationUpsertForm applicationUpsertForm = form.get();
            applicationService.updateApplication(application.getId(), applicationUpsertForm.name, ApplicationType.valueOf(applicationUpsertForm.type));

            return redirect(routes.ApplicationController.index());
        }
    }

    private Result showCreateApplication(Form<ApplicationUpsertForm> form) {
        LazyHtml content = new LazyHtml(createApplicationView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.create"), routes.ApplicationController.createApplication())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Application - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateApplicationGeneral(Form<ApplicationUpsertForm> form, Application application) {
        LazyHtml content = new LazyHtml(updateApplicationGeneralView.render(form, application.getId()));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())),
              new InternalLink(Messages.get("application.version"), routes.ApplicationVersionController.viewApplicationVersions(application.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Application - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }
}
