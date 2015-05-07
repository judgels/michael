package org.iatoki.judgels.michael.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.commons.InternalLink;
import org.iatoki.judgels.commons.LazyHtml;
import org.iatoki.judgels.commons.controllers.BaseController;
import org.iatoki.judgels.commons.views.html.layouts.headingLayout;
import org.iatoki.judgels.commons.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.commons.views.html.layouts.tabLayout;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherConfAdapter;
import org.iatoki.judgels.michael.MachineWatcherNotFoundException;
import org.iatoki.judgels.michael.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherTypes;
import org.iatoki.judgels.michael.MachineWatcherUtils;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import org.iatoki.judgels.michael.views.html.machines.watchers.listMachineWatchersView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;

import java.util.List;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class MachineWatcherController extends BaseController {

    private static final long PAGE_SIZE = 20;

    private final MachineService machineService;
    private final MachineWatcherService machineWatcherService;

    public MachineWatcherController(MachineService machineService, MachineWatcherService machineWatcherService) {
        this.machineService = machineService;
        this.machineWatcherService = machineWatcherService;
    }

    public Result viewMachineWatchers(long machineId) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        List<MachineWatcherTypes> enabledWatchers = machineWatcherService.findEnabledWatcherByMachineJid(machine.getJid());
        List<MachineWatcherTypes> unenabledWatchers = Lists.newArrayList(MachineWatcherTypes.values());
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

    @AddCSRFToken
    public Result activateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
            if (!machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                MachineWatcherConfAdapter adapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherTypes.valueOf(watcherType));
                if (adapter != null) {
                    return showActivateMachineWatcher(machine, watcherType, adapter.getConfHtml(adapter.generateForm(), org.iatoki.judgels.michael.controllers.routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.activate")));
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @RequireCSRFCheck
    public Result postActivateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
            if (!machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                MachineWatcherConfAdapter adapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherTypes.valueOf(watcherType));
                if (adapter != null) {
                    Form form = adapter.bindFormFromRequest(request());
                    if (form.hasErrors() || form.hasGlobalErrors()) {
                        return showActivateMachineWatcher(machine, watcherType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.activate")));
                    } else {
                        String conf = adapter.processRequestForm(form);
                        machineWatcherService.createWatcher(machine.getJid(), MachineWatcherTypes.valueOf(watcherType), conf);

                        return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @AddCSRFToken
    public Result updateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
            if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                MachineWatcherConfAdapter adapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherTypes.valueOf(watcherType));
                if (adapter != null) {
                    MachineWatcher machineWatcher = machineWatcherService.findByMachineJidAndWatcherType(machine.getJid(), MachineWatcherTypes.valueOf(watcherType));
                    return showUpdateMachineWatcher(machine, watcherType, adapter.getConfHtml(adapter.generateForm(machineWatcher.getConf()), org.iatoki.judgels.michael.controllers.routes.MachineWatcherController.postUpdateMachineWatcher(machine.getId(), machineWatcher.getId(), watcherType), Messages.get("machine.watcher.update")));
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @RequireCSRFCheck
    public Result postUpdateMachineWatcher(long machineId, long machineWatcherId, String watcherType) throws MachineNotFoundException, MachineWatcherNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        MachineWatcher machineWatcher = machineWatcherService.findByWatcherId(machineWatcherId);
        if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
            if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                MachineWatcherConfAdapter adapter = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherTypes.valueOf(watcherType));
                if (adapter != null) {
                    Form form = adapter.bindFormFromRequest(request());
                    if (form.hasErrors() || form.hasGlobalErrors()) {
                        return showUpdateMachineWatcher(machine, watcherType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.update")));
                    } else {
                        if (machine.getJid().equals(machineWatcher.getMachineJid())) {
                            String conf = adapter.processRequestForm(form);
                            machineWatcherService.updateWatcher(machineWatcher.getId(), machine.getJid(), MachineWatcherTypes.valueOf(watcherType), conf);

                            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
                        } else {
                            form.reject("error.notMachineWatcher");
                            return showUpdateMachineWatcher(machine, watcherType, adapter.getConfHtml(form, org.iatoki.judgels.michael.controllers.routes.MachineWatcherController.postActivateMachineWatcher(machine.getId(), watcherType), Messages.get("machine.watcher.update")));
                        }
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Result deactivateMachineWatcher(long machineId, String watcherType) throws MachineNotFoundException {
        Machine machine = machineService.findByMachineId(machineId);
        if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
            if (machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                machineWatcherService.removeWatcher(machine.getJid(), MachineWatcherTypes.valueOf(watcherType));
            }
            return redirect(routes.MachineWatcherController.viewMachineWatchers(machine.getId()));
        } else {
            throw new UnsupportedOperationException();
        }
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
