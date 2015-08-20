package org.iatoki.judgels.michael.services;

import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.michael.MachineAccess;
import org.iatoki.judgels.michael.MachineAccessNotFoundException;
import org.iatoki.judgels.michael.MachineAccessType;

import java.util.List;

public interface MachineAccessService {

    Page<MachineAccess> getPageOfMachineAccesses(String machineJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<MachineAccess> getMachineAccessesByMachineJid(String machineJid);

    MachineAccess findMachineAccessById(long machineAccessId) throws MachineAccessNotFoundException;

    <T> T getMachineAccessConfById(long machineAccessId, Class<T> clazz) throws MachineAccessNotFoundException;

    void createMachineAccess(String machineJid, String name, MachineAccessType types, String conf);
}
