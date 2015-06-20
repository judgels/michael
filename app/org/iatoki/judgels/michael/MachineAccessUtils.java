package org.iatoki.judgels.michael;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.michael.services.MachineAccessService;

import java.io.File;
import java.io.IOException;

public final class MachineAccessUtils {

    public static MachineAccessConfAdapter getMachineAccessConfAdapter(MachineAccessType accessTypes) {
        MachineAccessConfAdapter confAdapter = null;
        switch (accessTypes) {
            case KEY: {
                confAdapter = new MachineAccessKeyConfAdapter();
                break;
            }
            case PASSWORD: {
                confAdapter = new MachineAccessPasswordConfAdapter();
                break;
            }
            default: break;
        }

        return confAdapter;
    }

    public static Session getMachineSession(MachineAccessService machineAccessService, JSch jSch, Machine machine, MachineAccess machineAccess) throws IOException, JSchException, MachineAccessNotFoundException {
        Session session = null;
        if (MachineAccessType.KEY.name().equals(machineAccess.getType())) {
            MachineAccessKeyConf conf = machineAccessService.getMachineAccessConf(machineAccess.getId(), MachineAccessKeyConf.class);
            File temp = File.createTempFile("tempkey", ".pem");
            FileUtils.writeStringToFile(temp, conf.key);
            temp.setReadOnly();

            jSch.addIdentity(temp.getCanonicalPath());
            session = jSch.getSession(conf.username, machine.getIpAddress(), conf.port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            temp.delete();
        } else if (MachineAccessType.PASSWORD.name().equals(machineAccess.getType())) {
            MachineAccessPasswordConf conf = machineAccessService.getMachineAccessConf(machineAccess.getId(), MachineAccessPasswordConf.class);

            session = jSch.getSession(conf.username, machine.getIpAddress(), conf.port);
            session.setPassword(conf.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
        }

        return session;
    }
}
