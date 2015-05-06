package org.iatoki.judgels.michael.controllers.apis;

import com.google.gson.Gson;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.michael.GraphMachineWatcherAdapter;
import org.iatoki.judgels.michael.Machine;
import org.iatoki.judgels.michael.MachineNotFoundException;
import org.iatoki.judgels.michael.MachineService;
import org.iatoki.judgels.michael.MachineWatcher;
import org.iatoki.judgels.michael.MachineWatcherConfFactory;
import org.iatoki.judgels.michael.MachineWatcherService;
import org.iatoki.judgels.michael.MachineWatcherTypes;
import org.iatoki.judgels.michael.MachineWatcherUtils;
import org.iatoki.judgels.michael.controllers.security.LoggedIn;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Date;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class MachineWatcherAPIController extends Controller {

    private final MachineService machineService;
    private final MachineWatcherService machineWatcherService;

    public MachineWatcherAPIController(MachineService machineService, MachineWatcherService machineWatcherService) {
        this.machineService = machineService;
        this.machineWatcherService = machineWatcherService;
    }

    public Result getDataPoints(long machineId, String watcherType) {
        try {
            Machine machine = machineService.findByMachineId(machineId);
            if (EnumUtils.isValidEnum(MachineWatcherTypes.class, watcherType)) {
                if (machineWatcherService.isWatcherEnabled(machine.getJid(), MachineWatcherTypes.valueOf(watcherType))) {
                    MachineWatcherConfFactory factory = MachineWatcherUtils.getMachineWatcherConfFactory(machine, MachineWatcherTypes.valueOf(watcherType));
                    if (factory != null) {
                        MachineWatcher machineWatcher = machineWatcherService.findByMachineJidAndWatcherType(machine.getJid(), MachineWatcherTypes.valueOf(watcherType));
                        GraphMachineWatcherAdapter machineWatcherAdapter = (GraphMachineWatcherAdapter)factory.createMachineWatcherAdapter(machine, machineWatcher.getConf());

                        DynamicForm form = Form.form().bindFromRequest();
                        long startTime = Long.parseLong(form.get("startTime"));
                        long endTime = Long.parseLong(form.get("endTime"));
                        return ok(new Gson().toJson(machineWatcherAdapter.getDataPoints(new Date(startTime), new Date(endTime), 60)));
                    } else {
                        return badRequest();
                    }
                } else {
                    return badRequest();
                }
            } else {
                return badRequest();
            }
        } catch (MachineNotFoundException e) {
            return badRequest();
        }

    }
}
