package sg.edu.nus.idmiapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import sg.edu.nus.idmiapp.R;
import sg.edu.nus.idmiapp.service.CacheService;
import sg.edu.nus.idmiapp.service.impl.CacheServiceImpl;
import sg.edu.nus.idmiapp.utils.UIMessage;

public class ShowImageByLocation extends AppCompatActivity {

    Bitmap bitmap;
    private CacheService cacheService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init service
        this.cacheService = new CacheServiceImpl();
        setContentView(R.layout.activity_show_image_by_location);

        // the activity should get image name from the intent, and the image file name should not include the path
        this.bitmap = this.cacheService.getImageFromLocalCache(this.getApplicationContext().getFilesDir().getPath(), this.getIntent().getStringExtra("imageName"));
        mHandler.obtainMessage(UIMessage.MSG_SUCCESS).sendToTarget();
    }

    /*
    Handle message
     */
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case UIMessage.MSG_SUCCESS:
                    if(null == bitmap)
                        return;
                    ImageView imageView = (ImageView) findViewById(R.id.imageByLocation);
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };


}
