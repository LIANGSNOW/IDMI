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

public class ShowImageByLocation extends AppCompatActivity {

    Bitmap bitmap;
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_by_location);

        // the activity should get image name from the intent, and the image file name should not include the path
        this.bitmap = showImage(this.getIntent().getStringExtra("imageName"));
        mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
    }

    /*
    Handle message
     */
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case MSG_SUCCESS:
                    ImageView imageView = (ImageView) findViewById(R.id.imageByLocation);
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };

    /*
    show image in the ImageView
     */
    public Bitmap showImage(String imageNameWithUrl){
        String[] split = imageNameWithUrl.split("/");
        String imageName = split[split.length - 1];
        String imageNameWithPath = this.getApplicationContext().getFilesDir().getPath() + File.separator + imageName;
        File imageFile = new File(imageNameWithPath);
        if(!imageFile.exists()){
            return null;
        }
        return BitmapFactory.decodeFile(imageNameWithPath);
    }
}
