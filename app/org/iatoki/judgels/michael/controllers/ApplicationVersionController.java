package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.ApplicationVersionNotFoundException;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.Application;
import org.iatoki.judgels.michael.ApplicationVersion;
import org.iatoki.judgels.michael.controllers.forms.ApplicationVersionUpsertForm;
import org.iatoki.judgels.michael.ApplicationNotFoundException;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.application.version.createApplicationVersionView;
import org.iatoki.judgels.michael.views.html.application.version.listApplicationVersionsView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(value = LoggedIn.class)
public final class ApplicationVersionController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;
    private final ApplicationVersionService applicationVersionService;

    public ApplicationVersionController(ApplicationService applicationService, ApplicationVersionService applicationVersionService) {
        this.applicationService = applicationService;
        this.applicationVersionService = applicationVersionService;
    }

    @Transactional(readOnly = true)
    public Result viewApplicationVersions(long applicationId) throws ApplicationNotFoundException {
        return listApplicationVersions(applicationId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listApplicationVersions(long applicationId, long page, String orderBy, String orderDir, String filterString) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        Page<ApplicationVersion> currentPage = applicationVersionService.pageApplicationVersions(application.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listApplicationVersionsView.render(application.getId(), currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("application.version.list"), new InternalLink(Messages.get("commons.create"), routes.ApplicationVersionController.createApplicationVersion(application.getId())), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())),
              new InternalLink(Messages.get("application.version"), routes.ApplicationVersionController.viewApplicationVersions(application.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.version.list"), routes.ApplicationVersionController.viewApplicationVersions(application.getId())),
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Applications");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createApplicationVersion(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        Form<ApplicationVersionUpsertForm> form = Form.form(ApplicationVersionUpsertForm.class);

        return showCreateApplicationVersion(application, form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateApplicationVersion(long applicationId) throws ApplicationNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        Form<ApplicationVersionUpsertForm> form = Form.form(ApplicationVersionUpsertForm.class).bindFromRequest();

        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showCreateApplicationVersion(application, form);
        } else {
            ApplicationVersionUpsertForm applicationVersionUpsertForm = form.get();
            applicationVersionService.createApplicationVersion(application.getJid(), applicationVersionUpsertForm.name);

            return redirect(routes.ApplicationVersionController.viewApplicationVersions(application.getId()));
        }
    }

    @Transactional
    public Result removeApplicationVersion(long applicationId, long applicationVersionId) throws ApplicationNotFoundException, ApplicationVersionNotFoundException {
        Application application = applicationService.findByApplicationId(applicationId);
        ApplicationVersion applicationVersion = applicationVersionService.findByApplicationVersionId(applicationVersionId);
        if (application.getJid().equals(applicationVersion.getApplicationJid())) {
            applicationVersionService.removeApplicationVersion(applicationVersionId);

            return redirect(routes.ApplicationVersionController.viewApplicationVersions(application.getId()));
        } else {
            return badRequest();
        }
    }

    private Result showCreateApplicationVersion(Application application, Form<ApplicationVersionUpsertForm> form) {
        LazyHtml content = new LazyHtml(createApplicationVersionView.render(application.getId(), form));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.version.create"), c));
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("application.update"), routes.ApplicationController.updateApplicationGeneral(application.getId())),
              new InternalLink(Messages.get("application.version"), routes.ApplicationVersionController.viewApplicationVersions(application.getId()))
        ), c));
        content.appendLayout(c -> headingLayout.render(Messages.get("application.application") + " #" + application.getId() + ": " + application.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("application.version.create"), routes.ApplicationVersionController.createApplicationVersion(application.getId())),
              new InternalLink(Messages.get("application.version.list"), routes.ApplicationVersionController.viewApplicationVersions(application.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Applications");

        return ControllerUtils.getInstance().lazyOk(content);
    }

}
