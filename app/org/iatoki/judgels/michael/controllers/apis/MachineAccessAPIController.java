package org.iatoki.judgels.michael.controllers.apis;

import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import org.iatoki.judgels.michael.machine.access.MachineAccessService;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Security.Authenticated(value = LoggedIn.class)
@Singleton
public final class MachineAccessAPIController extends AbstractJudgelsAPIController {

    private final MachineAccessService machineAccessService;

    @Inject
    public MachineAccessAPIController(MachineAccessService machineAccessService) {
        this.machineAccessService = machineAccessService;
    }

    @Transactional(readOnly = true)
    public Result accessList() {
        DynamicForm dForm = DynamicForm.form().bindFromRequest();
        String machineJid = dForm.get("machineJid");
        if (machineJid == null) {
            return badRequest();
        }

        return ok(Json.toJson(machineAccessService.getMachineAccessesByMachineJid(machineJid)).toString());
    }
}
