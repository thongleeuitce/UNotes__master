package com.lguipeng.notes.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.lguipeng.notes.R;
import com.lguipeng.notes.model.UNote;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Author: lgp
 * Date: 2014/12/31.
 */
public class FileUtils {

    public final static String SD_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String APP_DIR = SD_ROOT_DIR + File.separator +"SNotes" ;
    public final static String BACKUP_FILE_NAME = "notes.txt" ;

    @Inject @Singleton
    public FileUtils() {
    }

    private void makeSureAppDirCreated(){
        if (checkSdcardStatus()) {
            mkdir(APP_DIR);
        }else{
            NotesLog.e("sd card not ready");
        }
    }

    public void mkdir(String dir){
        if (TextUtils.isEmpty(dir))
            return;
        File dirFile = new File(dir);
        if (!dirFile.exists()){
            boolean res = dirFile.mkdir();
            if (!res) {
                NotesLog.e("make dir " + dir + " error!");
            }
        }
    }
    public boolean isFileExist(String filePath){
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     *
     * @param filename
     * @return true if create success
     */
    public boolean createFile(String filename) {
        makeSureAppDirCreated();
        return createFile(APP_DIR, filename);
    }

    public boolean createFile(String dir, String filename) {
        File dirFile = new File(dir);
        if (!dirFile.isDirectory())
            return false;
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File newFile = new File(dir + File.separator + filename);
        try {
            if (!newFile.exists())
                newFile.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param filename
     * @return true if delete success
     */
    public boolean deleteFile(String filename) {
        File deleteFile = new File(filename);
        if (deleteFile.exists()) {
            deleteFile.delete();
            return true;
        }else {
            return false;
        }
    }

    public boolean  writeSNotesFile(String content) {
        return writeFile(BACKUP_FILE_NAME, content, false);
    }

    public boolean  writeFile(String fileName, String content, boolean append) {
        makeSureAppDirCreated();
        return writeFile(APP_DIR, fileName, content, append);
    }

    public boolean  writeFile(String dir, String fileName, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        FileWriter fileWriter = null;
        try {
            String filePath = dir + File.separator + fileName;
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content + "\n");
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }
        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public boolean checkSdcardStatus() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean backupSNotes(Context context, List<UNote> notes) {
        createFile(BACKUP_FILE_NAME);
        StringBuilder sb = new StringBuilder();
        String title = context.getString(R.string.title);
        String content = context.getString(R.string.note_content);
        for (UNote note : notes){
            sb.append(title + ":" + note.getLabel() + "\n");
            sb.append(content + ":\n" + note.getContent() + "\n\n");
        }
        return writeSNotesFile(sb.toString());
    }
}
