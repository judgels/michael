package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.messageView;
import org.iatoki.judgels.michael.services.ApplicationService;
import org.iatoki.judgels.michael.services.ApplicationVersionService;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.Operation;
import org.iatoki.judgels.michael.OperationAdapter;
import org.iatoki.judgels.michael.controllers.forms.OperationCreateForm;
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

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class OperationController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final ApplicationService applicationService;
    private final ApplicationVersionService applicationVersionService;
    private final MachineService machineService;
    private final MachineAccessService machineAccessService;
    private final OperationService operationService;

    public OperationController(ApplicationService applicationService, ApplicationVersionService applicationVersionService, MachineService machineService, MachineAccessService machineAccessService, OperationService operationService) {
        this.applicationService = applicationService;
        this.applicationVersionService = applicationVersionService;
        this.machineService = machineService;
        this.machineAccessService = machineAccessService;
        this.operationService = operationService;
    }

    public Result index() {
        return listOperations(0, "id", "asc", "");
    }

    public Result listOperations(long page, String orderBy, String orderDir, String filterString) {
        Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
        Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

        return showListCreateOperation(currentPage, orderBy, orderDir, filterString, form);
    }

    @AddCSRFToken
    public Result createOperation(String operationType, long page, String orderBy, String orderDir, String filterString) {
        if (EnumUtils.isValidEnum(OperationType.class, operationType)) {
            OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operationType));
            if (adapter != null) {
                Form form = adapter.generateConfForm();
                return showCreateOperation(operationType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postCreateOperation(operationType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
            } else {
                Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
                form.reject("error.operation.undefined");
                Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateOperation(currentPage, orderBy, orderDir, filterString, form);
            }
        } else {
            Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
            form.reject("error.operation.undefined");
            Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(currentPage, orderBy, orderDir, filterString, form);
        }
    }

    @RequireCSRFCheck
    public Result postCreateOperation(String operationType, long page, String orderBy, String orderDir, String filterString) {
        if (EnumUtils.isValidEnum(OperationType.class, operationType)) {
            OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operationType));
            if (adapter != null) {
                Form form = adapter.bindConfFormFromRequest(request());
                if (form.hasErrors() || form.hasGlobalErrors()) {
                    return showCreateOperation(operationType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postCreateOperation(operationType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
                } else {
                    operationService.createOperation(adapter.getNameFromConfForm(form), OperationType.valueOf(operationType), adapter.processConfForm(form));

                    return redirect(routes.OperationController.index());
                }
            } else {
                Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
                form.reject("error.operation.undefined");
                Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateOperation(currentPage, orderBy, orderDir, filterString, form);
            }
        } else {
            Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
            form.reject("error.operation.undefined");
            Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateOperation(currentPage, orderBy, orderDir, filterString, form);
        }
    }

    @AddCSRFToken
    public Result updateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form form = adapter.generateConfForm(operation.getName(), operation.getConf());
        Html html = adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update"));
        return showUpdateOperation(operation, html);
    }

    @RequireCSRFCheck
    public Result postUpdateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form form = adapter.bindConfFormFromRequest(request());
        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showUpdateOperation(operation, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update")));
        } else {
            operationService.updateOperation(operation.getId(), adapter.getNameFromConfForm(form), OperationType.valueOf(operation.getType()), adapter.processConfForm(form));

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result runOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form form = adapter.generateRunForm();
        Html html = adapter.getRunHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postRunOperation(operation.getId()), Messages.get("operation.run"), machineService.findAll(), applicationService.findAll());
        return showRunOperation(operation, html);
    }

    @RequireCSRFCheck
    public Result postRunOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationType.valueOf(operation.getType()));

        Form form = adapter.bindRunFormFromRequest(request());
        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showRunOperation(operation, adapter.getRunHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postRunOperation(operation.getId()), Messages.get("operation.run"), machineService.findAll(), applicationService.findAll()));
        } else {
            if (adapter.runOperation(form, machineService, machineAccessService, applicationService, applicationVersionService, operation.getConf())) {
                return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationSuccess(operation.getId()));
            } else {
                return redirect(org.iatoki.judgels.michael.controllers.routes.OperationController.operationFail(operation.getId()));
            }
        }
    }

    private Result showListCreateOperation(Page<Operation> currentPage, String orderBy, String orderDir, String filterString, Form<OperationCreateForm> form) {
        LazyHtml content = new LazyHtml(listCreateOperationsView.render(currentPage, orderBy, orderDir, filterString, form));
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
        Operation operation = operationService.findByOperationId(operationId);

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
        Operation operation = operationService.findByOperationId(operationId);

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
