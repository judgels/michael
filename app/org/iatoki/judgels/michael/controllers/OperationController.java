package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.messageView;
import org.iatoki.judgels.michael.ApplicationService;
import org.iatoki.judgels.michael.ApplicationVersionService;
import org.iatoki.judgels.michael.MachineAccessCreateForm;
import org.iatoki.judgels.michael.MachineAccessService;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.Operation;
import org.iatoki.judgels.michael.OperationAdapter;
import org.iatoki.judgels.michael.OperationCreateForm;
import org.iatoki.judgels.michael.OperationNotFoundException;
import org.iatoki.judgels.michael.OperationService;
import org.iatoki.judgels.michael.OperationTypes;
import org.iatoki.judgels.michael.OperationUtils;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.operations.createOperationView;
import org.iatoki.judgels.michael.views.html.operations.listOperationsView;
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
        Page<Operation> currentPage = operationService.pageOperations(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listOperationsView.render(currentPage, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("operation.list"), new InternalLink(Messages.get("commons.create"), routes.OperationController.createOperation()), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operations");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    public Result createOperation() {
        Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);

        return showCreateOperation(form);
    }

    @AddCSRFToken
    public Result createDefinedOperation(String operationType) {
        if (EnumUtils.isValidEnum(OperationTypes.class, operationType)) {
            OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operationType));
            if (adapter != null) {
                Form form = adapter.generateConfForm();
                return showCreateDefinedOperation(operationType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postCreateDefinedOperation(operationType), Messages.get("commons.create")));
            } else {
                Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
                form.reject("error.operation.undefined");
                return showCreateOperation(form);
            }
        } else {
            Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
            form.reject("error.operation.undefined");
            return showCreateOperation(form);
        }
    }

    @RequireCSRFCheck
    public Result postCreateDefinedOperation(String operationType) {
        if (EnumUtils.isValidEnum(OperationTypes.class, operationType)) {
            OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operationType));
            if (adapter != null) {
                Form form = adapter.bindConfFormFromRequest(request());
                if (form.hasErrors() || form.hasGlobalErrors()) {
                    return showCreateDefinedOperation(operationType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postCreateDefinedOperation(operationType), Messages.get("commons.create")));
                } else {
                    operationService.createOperation(adapter.getNameFromConfForm(form), OperationTypes.valueOf(operationType), adapter.processConfForm(form));

                    return redirect(routes.OperationController.index());
                }
            } else {
                Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
                form.reject("error.operation.undefined");
                return showCreateOperation(form);
            }
        } else {
            Form<OperationCreateForm> form = Form.form(OperationCreateForm.class);
            form.reject("error.operation.undefined");
            return showCreateOperation(form);
        }
    }

    @AddCSRFToken
    public Result updateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operation.getType()));

        Form form = adapter.generateConfForm(operation.getName(), operation.getConf());
        Html html = adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update"));
        return showUpdateOperation(operation, html);
    }

    @RequireCSRFCheck
    public Result postUpdateOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operation.getType()));

        Form form = adapter.bindConfFormFromRequest(request());
        if (form.hasErrors() || form.hasGlobalErrors()) {
            return showUpdateOperation(operation, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postUpdateOperation(operation.getId()), Messages.get("commons.update")));
        } else {
            operationService.updateOperation(operation.getId(), adapter.getNameFromConfForm(form), OperationTypes.valueOf(operation.getType()), adapter.processConfForm(form));

            return redirect(routes.OperationController.index());
        }
    }

    @AddCSRFToken
    public Result runOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operation.getType()));

        Form form = adapter.generateRunForm();
        Html html = adapter.getRunHtml(form, org.iatoki.judgels.michael.controllers.routes.OperationController.postRunOperation(operation.getId()), Messages.get("operation.run"), machineService.findAll(), applicationService.findAll());
        return showRunOperation(operation, html);
    }

    @RequireCSRFCheck
    public Result postRunOperation(long operationId) throws OperationNotFoundException {
        Operation operation = operationService.findByOperationId(operationId);
        OperationAdapter adapter = OperationUtils.getOperationAdapter(OperationTypes.valueOf(operation.getType()));

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

    private Result showCreateOperation(Form<OperationCreateForm> form) {
        LazyHtml content = new LazyHtml(createOperationView.render(form));
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation())
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateDefinedOperation(String operationType, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("operation.create"), c));
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index()),
              new InternalLink(Messages.get("operation.create"), routes.OperationController.createOperation()),
              new InternalLink(Messages.get("operation.createDefined"), routes.OperationController.createDefinedOperation(operationType))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Operation - Create Defined");
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
