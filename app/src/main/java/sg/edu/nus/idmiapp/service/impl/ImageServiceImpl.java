package sg.edu.nus.idmiapp.service.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
    public ArrayList getImageSets(String path) throws Exception {
        ArrayList<HashMap<String,String>> imageInfoArray = new ArrayList<>();
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
                HashMap<String, String> map = new HashMap<>();
                map.put("imageName",jsonObject.getString("imageName"));
                map.put("latitude",jsonObject.getString("latitude"));
                map.put("longitude",jsonObject.getString("longitude"));
                map.put("size", jsonObject.getString("size"));
                imageInfoArray.add(map);
            }
        }
        return imageInfoArray;
    }
}
