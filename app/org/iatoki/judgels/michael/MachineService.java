package org.iatoki.judgels.michael;

import org.iatoki.judgels.commons.Page;

import java.util.List;

public interface MachineService {

    List<Machine> findAll();

    Page<Machine> pageMachines(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    boolean existByMachineJid(String machineJid);

    Machine findByMachineJid(String machineJid);

    Machine findByMachineId(long machineId) throws MachineNotFoundException;

    void createMachine(String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress);

    void updateMachine(long machineId, String instanceName, String displayName, String baseDir, MachineType machineTypes, String ipAddress) throws MachineNotFoundException;
}
