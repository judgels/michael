package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.controllers.AbstractControllerUtils;
import org.iatoki.judgels.commons.views.html.layouts.menusView;
import org.iatoki.judgels.commons.views.html.layouts.sidebarLayout;
import play.i18n.Messages;

public final class ControllerUtils extends AbstractControllerUtils {

    private static final ControllerUtils INSTANCE = new ControllerUtils();

    private ControllerUtils() {
    }

    @Override
    public void appendSidebarLayout(LazyHtml content) {
        LazyHtml sidebarContent = new LazyHtml(menusView.render(ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index())
        )));
        content.appendLayout(c -> sidebarLayout.render(sidebarContent.render(), c));
    }

    static final ControllerUtils getInstance() {
        return INSTANCE;
    }
}
