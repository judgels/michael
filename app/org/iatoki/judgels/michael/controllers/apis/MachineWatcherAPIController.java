package org.iatoki.judgels.michael.controllers.apis;

import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.michael.machine.Machine;
import org.iatoki.judgels.michael.machine.MachineNotFoundException;
import org.iatoki.judgels.michael.machine.watcher.MachineWatcher;
import org.iatoki.judgels.michael.machine.watcher.MachineWatcherType;
import org.iatoki.judgels.michael.machine.watcher.MachineWatcherUtils;
import org.iatoki.judgels.michael.machine.watcher.GraphMachineWatcherAdapter;
import org.iatoki.judgels.michael.machine.watcher.MachineWatcherConfAdapter;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.machine.MachineService;
import org.iatoki.judgels.michael.machine.watcher.MachineWatcherService;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
public final class MachineWatcherAPIController extends AbstractJudgelsAPIController {

    private final MachineService machineService;
    private final MachineWatcherService machineWatcherService;

    @Inject
    public MachineWatcherAPIController(MachineService machineService, MachineWatcherService machineWatcherService) {
        this.machineService = machineService;
        this.machineWatcherService = machineWatcherService;
    }

    @Transactional(readOnly = true)
    public Result getDataPoints(long machineId, String watcherType) {
        Machine machine;
        try {
            machine = machineService.findMachineById(machineId);
        } catch (MachineNotFoundException e) {
            return badRequest();
        }

        if (!EnumUtils.isValidEnum(MachineWatcherType.class, watcherType)) {
            return badRequest();
        }

        if (!machineWatcherService.isWatcherActivated(machine.getJid(), MachineWatcherType.valueOf(watcherType))) {
            return badRequest();
        }

        MachineWatcherConfAdapter factory = MachineWatcherUtils.getMachineWatcherConfAdapter(machine, MachineWatcherType.valueOf(watcherType));
        if (factory == null) {
            return badRequest();
        }

        MachineWatcher machineWatcher = machineWatcherService.findMachineWatcherByMachineJidAndType(machine.getJid(), MachineWatcherType.valueOf(watcherType));
        GraphMachineWatcherAdapter machineWatcherAdapter = (GraphMachineWatcherAdapter) factory.createMachineWatcherAdapter(machine, machineWatcher.getConf());

        DynamicForm form = Form.form().bindFromRequest();
        long startTime = Long.parseLong(form.get("startTime"));
        long endTime = Long.parseLong(form.get("endTime"));

        return ok(Json.toJson(machineWatcherAdapter.getDataPoints(new Date(startTime), new Date(endTime), 60)).toString());
    }
}
