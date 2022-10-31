package com.hoangt3k56.dropbox.model;

public class File {
    private String nameFile;
    private String pathFile;

    public File(String nameFile, String pathFile) {
        this.nameFile = nameFile;
        this.pathFile = pathFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }


}
