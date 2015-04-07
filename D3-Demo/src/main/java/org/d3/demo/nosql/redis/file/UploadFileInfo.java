package org.d3.demo.nosql.redis.file;

/**
 * Created by tanj on 2014/9/16.
 */
public class UploadFileInfo {
    private String fileName;
    private String lastModified;
    private String length;

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
