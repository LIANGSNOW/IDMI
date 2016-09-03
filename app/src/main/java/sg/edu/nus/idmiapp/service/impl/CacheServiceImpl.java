package sg.edu.nus.idmiapp.service.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import sg.edu.nus.idmiapp.service.CacheService;
import sg.edu.nus.idmiapp.service.ImageService;
import sg.edu.nus.idmiapp.utils.Configure;

/**
 * Created by zz on 3/9/16.
 */
public class CacheServiceImpl implements CacheService{
    @Override
    public boolean delCacheFile(String path, int expireTime) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                long lastModiefy = temp.lastModified();
                long current = new Date().getTime();
                if (current - lastModiefy > (expireTime)) {
                    temp.delete();
                }
            }
        }
        return flag;
    }

    @Override
    public long enquiryFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + enquiryFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public Bitmap getImageFromLocalCache(String localCachePath, String imageNameWithUrl){
        String[] split = imageNameWithUrl.split("/");
        String imageName = split[split.length - 1];
        String imageNameWithPath = localCachePath + File.separator + imageName;
        File imageFile = new File(imageNameWithPath);
        if(!imageFile.exists()){
            return null;
        }
        return BitmapFactory.decodeFile(imageNameWithPath);
    }

    public void clearCacheOnStart(String localCachePath){
        int tempExpireTime = Configure.expireTime;
        while(enquiryFolderSize(new File(localCachePath)) > Configure.maximumCacheSize){
            delCacheFile(localCachePath, tempExpireTime);
            if(tempExpireTime > 0){
                tempExpireTime = tempExpireTime - 60 * 60 * 24;
            }
        }
    }
}
