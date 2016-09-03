package sg.edu.nus.idmiapp.service;

import java.io.File;

/**
 * Created by zz on 3/9/16.
 */
public interface CacheService {

    /*
    delete the local cache file by path and expiry time
     */
    boolean delCacheFile(String path, int expireTime);

    long enquiryFolderSize(File file);
}
