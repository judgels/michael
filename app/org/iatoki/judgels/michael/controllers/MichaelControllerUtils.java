package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsControllerUtils;
import org.iatoki.judgels.play.views.html.layouts.contentLayout;
import org.iatoki.judgels.play.views.html.layouts.menusView;
import org.iatoki.judgels.play.views.html.layouts.sidebarLayout;
import play.i18n.Messages;

public final class MichaelControllerUtils extends AbstractJudgelsControllerUtils {

    private static final MichaelControllerUtils INSTANCE = new MichaelControllerUtils();

    private MichaelControllerUtils() {
    }

    @Override
    public void appendSidebarLayout(LazyHtml content) {
        content.appendLayout(c -> contentLayout.render(c));

        LazyHtml sidebarContent = new LazyHtml(menusView.render(ImmutableList.of(
              new InternalLink(Messages.get("application.applications"), routes.ApplicationController.index()),
              new InternalLink(Messages.get("dashboard.dashboards"), routes.DashboardController.index()),
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("operation.operations"), routes.OperationController.index())
        )));
        content.appendLayout(c -> sidebarLayout.render(sidebarContent.render(), c));
    }

    static MichaelControllerUtils getInstance() {
        return INSTANCE;
    }
}
