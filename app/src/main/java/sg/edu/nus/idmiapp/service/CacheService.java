package sg.edu.nus.idmiapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zz on 3/9/16.
 */
public interface CacheService {

    /*
    delete the local cache file by path and expiry time
     */
    boolean delCacheFile(String path, int expireTime);

    long enquiryFolderSize(File file);

    /*
    show image in the ImageView
     */
    public Bitmap getImageFromLocalCache(String localCachePath, String imageNameWithUrl);

    /*
    check the local cache on activity start
     */
    void clearCacheOnStart(String localCachePath);
}