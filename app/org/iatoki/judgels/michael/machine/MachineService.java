package org.iatoki.judgels.michael.machine;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;

@ImplementedBy(MachineServiceImpl.class)
public interface MachineService {

    List<Machine> getAllMachines();

    Page<Machine> getPageOfMachines(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    boolean machineExistsByJid(String machineJid);

    Machine findMachineByJid(String machineJid);

    Machine findMachineById(long machineId) throws MachineNotFoundException;

    void createMachine(String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress);

    void updateMachine(long machineId, String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress) throws MachineNotFoundException;
}
