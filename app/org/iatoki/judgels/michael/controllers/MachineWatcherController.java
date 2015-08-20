package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.services.MachineService;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.adapters.MachineWatcherConfAdapter;
import org.iatoki.judgels.michael.MachineWatcherNotFoundException;
import org.iatoki.judgels.michael.services.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherType;
import org.iatoki.judgels.michael.MachineWatcherUtils;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.views.html.machine.watcher.listMachineWatchersView;
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
import java.util.List;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
@Named
public final class MachineWatcherController extends AbstractJudgelsController {

    private final MachineService machineService;
    private final MachineWatcherService machineWatcherService;

    @Inject
    public MachineWatcherController(MachineService machineService, MachineWatcherService machineWatcherService) {
        this.machineService = machineService;
        this.machineWatcherService = machineWatcherService;
    }

    @Transactional(readOnly = true)
    public Result viewMachineWatchers(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        List<MachineWatcherType> enabledWatchers = machineWatcherService.getEnabledWatchersByMachineJid(machine.getJid());
        List<MachineWatcherType> unenabledWatchers = Lists.newArrayList(MachineWatcherType.values());
        unenabledWatchers.removeAll(enabledWatchers);

        LazyHtml content = new LazyHtml(listMachineWatchersView.render(machine.getId(), enabledWatchers, unenabledWatchers));
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.watcher.list"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.watcher.list"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Watchers");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result activateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            throw new UnsupportedOperationException();
        }

        if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
        }

        MachineWatcherConfAdapter machineWatcherConfAdapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherType.valueOf(watcherType));
        if (machineWatcherConfAdapter == null) {
            throw new UnsupportedOperationException();
        }

        return showActivateMachineWatcher(machine, watcherType, machineWatcherConfAdapter.getConfHtml(machineWatcherConfAdapter.generateForm(), routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.activate")));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postActivateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            throw new UnsupportedOperationException();
        }

        if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
        }

        MachineWatcherConfAdapter machineWatcherConfAdapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherType.valueOf(watcherType));
        if (machineWatcherConfAdapter == null) {
            throw new UnsupportedOperationException();
        }

        Form machineWatcherConfForm = machineWatcherConfAdapter.bindFormFromRequest(request());
        if (formHasErrors(machineWatcherConfForm)) {
            return showActivateMachineWatcher(machine, watcherType, machineWatcherConfAdapter.getConfHtml(machineWatcherConfForm, routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.activate")));
        }

        String conf = machineWatcherConfAdapter.processRequestForm(machineWatcherConfForm);
        machineWatcherService.createMachineWatcher(machine.getJid(), MachineWatcherType.valueOf(watcherType), conf);

        return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result updateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            throw new UnsupportedOperationException();
        }

        if (!machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
        }

        MachineWatcherConfAdapter machineWatcherConfAdapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherType.valueOf(watcherType));
        if (machineWatcherConfAdapter == null) {
            throw new UnsupportedOperationException();
        }

        MachineWatcher machineWatcher = machineWatcherService.findMachineWatcherByMachineJidAndType(machine.getJid(), MachineWatcherType.valueOf(watcherType));
        return showUpdateMachineWatcher(machine, watcherType, machineWatcherConfAdapter.getConfHtml(machineWatcherConfAdapter.generateForm(machineWatcher.getConf()), routes.MachineWatcherController.postUpdateMachineWatcher(machine.getId(), machineWatcher.getId(), watcherType), Messages.get("machine.watcher.update")));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postUpdateMachineWatcher(long machineId, long machineWatcherId, String watcherType) throws MachineNotFoundException, MachineWatcherNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        MachineWatcher machineWatcher = machineWatcherService.findMachineWatcherById(machineWatcherId);

        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            throw new UnsupportedOperationException();
        }

        if (!machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
        }

        MachineWatcherConfAdapter machineWatcherConfAdapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherType.valueOf(watcherType));
        if (machineWatcherConfAdapter == null) {
            throw new UnsupportedOperationException();
        }

        Form machineWatcherConfForm = machineWatcherConfAdapter.bindFormFromRequest(request());
        if (formHasErrors(machineWatcherConfForm)) {
            return showUpdateMachineWatcher(machine, watcherType, machineWatcherConfAdapter.getConfHtml(machineWatcherConfForm, routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.update")));
        }

        if (!machine.getJid().equals(machineWatcher.getMachineJid())) {
            machineWatcherConfForm.reject("error.notMachineWatcher");
            return showUpdateMachineWatcher(machine, watcherType, machineWatcherConfAdapter.getConfHtml(machineWatcherConfForm, routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.update")));
        }

        String machineWatcherConf = machineWatcherConfAdapter.processRequestForm(machineWatcherConfForm);
        machineWatcherService.updateMachineWatcher(machineWatcher.getId(), machine.getJid(), MachineWatcherType.valueOf(watcherType), machineWatcherConf);

        return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
    }

    @Transactional
    public Result deactivateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findMachineById(machineId);
        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            throw new UnsupportedOperationException();
        }

        if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            machineWatcherService.removeMachineWatcher(machine.getJid(), MachineWatcherType.valueOf(watcherType));
        }
        return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
    }

    private Result showActivateMachineWatcher(Machine machine, String watcherType, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.watcher.activate"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.watcher.list"), routes.MachineWatcherController.viewMachineWatchers(machine.getId())),
              new InternalLink(Messages.get("machine.watcher.activate"), routes.MachineWatcherController.activateMachineWatcher(machine.getId(), watcherType))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Watchers");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private Result showUpdateMachineWatcher(Machine machine, String watcherType, Html html) {
        LazyHtml content = new LazyHtml(html);
        content.appendLayout(c -> headingLayout.render(Messages.get("machine.watcher.update"), c));
        appendTabLayout(content, machine);
        ControllerUtils.getInstance().appendSidebarLayout(content);
        ControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("machine.machines"), routes.MachineController.index()),
              new InternalLink(Messages.get("machine.watcher.list"), routes.MachineWatcherController.viewMachineWatchers(machine.getId())),
              new InternalLink(Messages.get("machine.watcher.update"), routes.MachineWatcherController.updateMachineWatcher(machine.getId(), watcherType))
        ));
        ControllerUtils.getInstance().appendTemplateLayout(content, "Machine - Watchers");

        return ControllerUtils.getInstance().lazyOk(content);
    }

    private void appendTabLayout(LazyHtml content, Machine machine) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
              new InternalLink(Messages.get("machine.update"), routes.MachineController.updateMachineGeneral(machine.getId())),
              new InternalLink(Messages.get("machine.access"), routes.MachineAccessController.viewMachineAccesses(machine.getId())),
              new InternalLink(Messages.get("machine.watcher"), routes.MachineWatcherController.viewMachineWatchers(machine.getId()))
        ), c));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("machine.machine") + " #" + machine.getId() + ": " + machine.getDisplayName(), new InternalLink(Messages.get("commons.enter"), routes.MachineController.viewMachine(machine.getId())), c));
    }
}
