package sg.edu.nus.idmiapp.service.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.nus.idmiapp.dao.ImageDAO;
import sg.edu.nus.idmiapp.service.ImageService;

/**
 * Created by zz on 3/9/16.
 */
public class ImageServiceImpl implements ImageService{
    @Override
    public byte[] getImageBytes(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(5*1000);
        InputStream inputStream = conn.getInputStream();
        byte[] data = new byte[0];
        try {
            data = readStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }

    @Override
    public ArrayList<ImageDAO> getImageSets(String path) throws Exception {
        ArrayList<ImageDAO> imageInfoArray = new ArrayList<>();
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(15 * 1000);
        if (con.getResponseCode() == 200){
            InputStream is = con.getInputStream();
            byte[] data = readStream(is);
            JSONArray jsonArray = new JSONArray(new String(data));
            JSONObject jsonObject;
            for(int i = 0;i < jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                ImageDAO image = new ImageDAO();
                image.setImageNameWithCloudStorageURL(jsonObject.getString("imageName"));
                String temp[] = jsonObject.getString("imageName").split("/");
                image.setImageName(temp[temp.length - 1]);
                image.setLatitude(jsonObject.getString("latitude"));
                image.setLongitude(jsonObject.getString("longitude"));
                image.setSize(Integer.parseInt(jsonObject.getString("size")));
                imageInfoArray.add(image);
            }
        }
        return imageInfoArray;
    }

    public Bitmap[] getBitMaps(List<String> cachedFile, List<String> uncachedFile, String localCachePath) throws IOException {
        Bitmap[] bitmap = new Bitmap[cachedFile.size() + uncachedFile.size()];
        int count = 0;
        if(cachedFile.size() > 0){
            for(int i = 0; i < cachedFile.size(); i++){
                bitmap[count] = BitmapFactory.decodeFile(cachedFile.get(i));
                count++;
            }
        }
        if(uncachedFile.size() > 0){
            for(int i = 0; i < uncachedFile.size(); i++){
                byte[] data = getImageBytes(uncachedFile.get(i));
                bitmap[count] = BitmapFactory.decodeByteArray(data, 0, data.length);
                String[] temp = uncachedFile.get(i).split("/");
                File file = new File(localCachePath + "/" + temp[temp.length - 1]);
                FileOutputStream out = new FileOutputStream(file);
                bitmap[count].compress(Bitmap.CompressFormat.PNG, 90, out);
                count++;
                out.flush();
                out.close();
            }
        }
        return bitmap;
    }
}
