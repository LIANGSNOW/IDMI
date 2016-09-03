package sg.edu.nus.idmiapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
    ArrayList getImageSets(String path) throws Exception;
}
