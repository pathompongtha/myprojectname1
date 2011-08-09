package android_programmers_guide.HelloWorldImage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class HelloWorldImage extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}