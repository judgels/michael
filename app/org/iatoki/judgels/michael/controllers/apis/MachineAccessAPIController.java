package org.iatoki.judgels.michael.controllers.apis;

import com.google.gson.Gson;
import org.iatoki.judgels.michael.services.MachineAccessService;
import org.iatoki.judgels.michael.controllers.securities.LoggedIn;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Transactional
@Security.Authenticated(value = LoggedIn.class)
public final class MachineAccessAPIController extends Controller {

    private final MachineAccessService machineAccessService;

    public MachineAccessAPIController(MachineAccessService machineAccessService) {
        this.machineAccessService = machineAccessService;
    }

    public Result accessList() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String machineJid = form.get("machineJid");
        if (machineJid != null) {
            return ok(new Gson().toJson(machineAccessService.findByMachineJid(machineJid)));
        } else {
            return badRequest();
        }
    }
}
