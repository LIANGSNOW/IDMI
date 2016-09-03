package sg.edu.nus.idmiapp.utils;

/**
 * Created by zz on 3/9/16.
 */
public interface Configure {

    String serverIP = "http://ec2-54-218-40-64.us-west-2.compute.amazonaws.com:8080";

    int expireTime = 30 * 60 * 60 * 24 * 7;   // expire time of the cache files
    long maximumCacheSize = 1024 * 1024 * 300; // maximum local image cache size

}

