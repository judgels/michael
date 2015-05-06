package org.iatoki.judgels.michael;

public class CopyOperationCommand {

    private final String file1;
    private final String file2;

    public CopyOperationCommand(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    public String getFile1() {
        return file1;
    }

    public String getFile2() {
        return file2;
    }
}
