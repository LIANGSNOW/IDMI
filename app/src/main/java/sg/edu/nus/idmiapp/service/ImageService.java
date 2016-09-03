package sg.edu.nus.idmiapp.service;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.idmiapp.dao.ImageDAO;

/**
 * Created by zz on 3/9/16.
 */
public interface ImageService {
    /*
    get image bytes by given the url
     */
    byte[] getImageBytes(String path) throws IOException;

    byte[] readStream(InputStream inputStream) throws Exception;

    /*
    Get image information by url
     */
    ArrayList<ImageDAO> getImageSets(String path) throws Exception;

    /*
    get images' bitmap
     */
    Bitmap[] getBitMaps(List<String> cachedFile, List<String> uncachedFile, String localCachePath) throws IOException;


}
