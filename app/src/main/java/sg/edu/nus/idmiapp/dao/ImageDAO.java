package sg.edu.nus.idmiapp.dao;

import java.io.Serializable;

/**
 * Created by zz on 3/9/16.
 */
public class ImageDAO implements Serializable {

    private String imageName;
    private String latitude;
    private String longitude;
    private String imageNameWithCloudStorageURL;
    private int size;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageNameWithCloudStorageURL() {
        return imageNameWithCloudStorageURL;
    }

    public void setImageNameWithCloudStorageURL(String imageNameWithcloudStorageURL) {
        this.imageNameWithCloudStorageURL = imageNameWithcloudStorageURL;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
