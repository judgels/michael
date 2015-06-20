package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessNotFoundException;
import org.iatoki.judgels.michael.MachineAccessType;

import java.util.List;

public interface MachineAccessService {

    Page<MachineAccess> pageMachineAccesses(String machineJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<MachineAccess> findByMachineJid(String machineJid);

    MachineAccess findByMachineAccessId(long machineAccessId) throws MachineAccessNotFoundException;

    <T> T getMachineAccessConf(long machineAccessId, Class<T> clazz) throws MachineAccessNotFoundException;

    void createMachineAccess(String machineJid, String name, MachineAccessType types, String conf);
}
