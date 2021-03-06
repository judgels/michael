package org.iatoki.judgels.michael.machine.access;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;

@ImplementedBy(MachineAccessServiceImpl.class)
public interface MachineAccessService {

    Page<MachineAccess> getPageOfMachineAccesses(String machineJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    List<MachineAccess> getMachineAccessesByMachineJid(String machineJid);

    MachineAccess findMachineAccessById(long machineAccessId) throws MachineAccessNotFoundException;

    <T> T getMachineAccessConfById(long machineAccessId, Class<T> clazz) throws MachineAccessNotFoundException;

    void createMachineAccess(String machineJid, String name, MachineAccessType types, String conf);
}
