package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.messageView;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.Operation;
import org.iatoki.judgels.michael.adapters.OperationAdapter;
import org.iatoki.judgels.michael.forms.OperationCreateForm;
import org.iatoki.judgels.michael.OperationNotFoundException;
import org.iatoki.judgels.michael.services.OperationService;
import org.iatoki.judgels.michael.OperationType;
import org.iatoki.judgels.michael.OperationUtils;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.operation.listCreateOperationsView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class OperationController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;
    private final ApplicationVersionService applicationVersionService;
    private final MachineAccessService machineAccessService;
    private final MachineService machineService;
    private final OperationService operationService;

    @Inject
    public OperationController(ApplicationService applicationService, ApplicationVersionService applicationVersionService, MachineAccessService machineAccessService, MachineService machineService, OperationService operationService) {
        this.applicationService = applicationService;
        this.applicationVersionService = applicationVersionService;
        this.machineAccessService = machineAccessService;
        this.machineService = machineService;
        this.operationService = operationService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listOperations(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listOperations(long page, String orderBy, String orderDir, String filterString) {
        Form<OperationCreateForm> operationCreateForm = Form.form(OperationCreateForm.class);
        Page<Operation> pageOfOperations = operationService.getPageOfOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateOperation(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createOperation(String operationType, long page, String orderBy, String orderDir, String filterString) {
        if (!EnumUtils.isValidEnum(OperationType.class, operationType)) {
            Form<OperationCreateForm> operationCreateForm = Form.form(OperationCreateForm.class);
            operationCreateForm.reject("error.operation.undefined");
            Page<Operation> pageOfOperations = operationService.getPageOfOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm);
        }
        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operationType));
        if (operationAdapter == null) {
            Form<OperationCreateForm> operationCreateForm = Form.form(OperationCreateForm.class);
            operationCreateForm.reject("error.operation.undefined");
            Page<Operation> pageOfOperations = operationService.getPageOfOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm);
        }

        Form operationConfForm = operationAdapter.generateConfForm();
        return showCreateOperation(operationType, operationAdapter.getConfHtml(operationConfForm, routes.OperationController.postCreateOperation(operationType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateOperation(String operationType, long page, String orderBy, String orderDir, String filterString) {
        if (!EnumUtils.isValidEnum(OperationType.class, operationType)) {
            Form<OperationCreateForm> operationCreateForm = Form.form(OperationCreateForm.class);
            operationCreateForm.reject("error.operation.undefined");
            Page<Operation> pageOfOperations = operationService.getPageOfOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm);
        }

        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operationType));
        if (operationAdapter == null) {
            Form<OperationCreateForm> operationCreateForm = Form.form(OperationCreateForm.class);
            operationCreateForm.reject("error.operation.undefined");
            Page<Operation> pageOfOperations = operationService.getPageOfOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm);
        }

        Form operationConfForm = operationAdapter.bindConfFormFromRequest(request());
        if (formHasErrors(operationConfForm)) {
            return showCreateOperation(operationType, operationAdapter.getConfHtml(operationConfForm, routes.OperationController.postCreateOperation(operationType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
        }

        operationService.createOperation(operationAdapter.getNameFromConfForm(operationConfForm), OperationType.valueOf(operationType), operationAdapter.processConfForm(operationConfForm));

        return redirect(routes.OperationController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);
        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form operationConfForm = operationAdapter.generateConfForm(operation.getName(), operation.getConf());
        Html html = operationAdapter.getConfHtml(operationConfForm, routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update"));
        return showUpdateOperation(operation, html);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);
        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form operationConfForm = operationAdapter.bindConfFormFromRequest(request());
        if (formHasErrors(operationConfForm)) {
            return showUpdateOperation(operation, operationAdapter.getConfHtml(operationConfForm, routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update")));
        }

        operationService.updateOperation(operation.getId(), operationAdapter.getNameFromConfForm(operationConfForm), OperationType.valueOf(operation.getType()), operationAdapter.processConfForm(operationConfForm));

        return redirect(routes.OperationController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result runOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);
        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form operationRunForm = operationAdapter.generateRunForm();
        Html html = operationAdapter.getRunHtml(operationRunForm, routes.OperationController.postRunOperation(operation.getId()), Messages.get("operation.run"), machineService.getAllMachines(), applicationService.getAllApplications());
        return showRunOperation(operation, html);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postRunOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);
        OperationAdapter operationAdapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form operationRunForm = operationAdapter.bindRunFormFromRequest(request());
        if (formHasErrors(operationRunForm)) {
            return showRunOperation(operation, operationAdapter.getRunHtml(operationRunForm, org.iatoki.judgels.michael.controllers.routes.OperationController.postRunOperation(operation.getId()), Messages.get("operation.run"), machineService.getAllMachines(), applicationService.getAllApplications()));
        }

        if (!operationAdapter.runOperation(operationRunForm, machineService, machineAccessService, applicationService, applicationVersionService, operation.getConf())) {
            return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
        }

        return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationSuccess(operation.getId()));
    }

    private Result showListCreateOperation(Page<Operation> pageOfOperations, String orderBy, String orderDir, String filterString, Form<OperationCreateForm> operationCreateForm) {
        LazyHtml content = new LazyHtml(listCreateOperationsView.render(pageOfOperations, orderBy, orderDir, filterString, operationCreateForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.list"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operations");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateOperation(String operationType, Html html, long page, String orderBy, String orderDir, String filterString) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation(operationType, page, orderBy, orderDir, filterString))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateOperation(Operation operation, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.update"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.update"), routes.OperationController.updateOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Update");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showRunOperation(Operation operation, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.run"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.run"), routes.OperationController.runOperation(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Run");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result operationSuccess(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);

        LazyHtml content = new LazyHtml(messageView.render(Messages.get("operation.success")));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.result.success"), routes.OperationController.operationSuccess(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Result");
        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result operationFail(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findOperationById(operationId);

        LazyHtml content = new LazyHtml(messageView.render(Messages.get("operation.fail")));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.operation") + " #" + operation.getId() + ": " + operation.getName(), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.result.success"), routes.OperationController.operationSuccess(operation.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Result");
        return ControllerUtils.getInstance().lazyOk(content);
    }
}
