package com.croshe.android.base.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件实体类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 */
public class FileEntity extends BaseEntity {
    private String key;
    private String fileName;
    private String filePath;
    private String fileType;
    private String fileContent;
    private String fileDateTime;
    private Integer duration;
    private String thumbPath;
    private Boolean isLock = true;

    public static FileEntity objectFromData(String str) {

        return new Gson().fromJson(str, FileEntity.class);
    }

    public static List<FileEntity> arrayFileEntityFromData(String str) {

        Type listType = new TypeToken<ArrayList<FileEntity>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileDateTime() {
        return fileDateTime;
    }

    public void setFileDateTime(String fileDateTime) {
        this.fileDateTime = fileDateTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public Boolean isLock() {
        return isLock;
    }

    public void setLock(Boolean lock) {
        isLock = lock;
    }

    public String getThumbPath() {
        if (StringUtils.isEmpty(thumbPath)) {
            return getFilePath();
        }
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {

        this.thumbPath = thumbPath;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getFileUrl() {
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }
        return getServerMainUrl() + filePath;
    }

    public String getThumbFileUrl() {
        if (StringUtils.isEmpty(thumbPath)) {
            return getFileUrl();
        }
        return getServerMainUrl() + thumbPath;
    }
    


    public Boolean isImage() {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        if (Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$", filePath.toLowerCase())) {
            return true;
        }
        return false;
    }


    public Boolean isGif() {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        return filePath.toLowerCase().endsWith(".gif");
    }


    public Boolean isVideo() {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        return filePath.toLowerCase().endsWith(".mp4");
    }
}
