package example.practise.kevin.androidvlctextureview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class StreamActivity extends AppCompatActivity {

    TextureVlc textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String URL = "rtsp://173.224.209.43/zc";
        initLayout(URL);
    }


    private void initLayout(String url) {

        //new VLC
        textureView = new TextureVlc(this);
        textureView.setAudio(true);
        textureView.setURL(url);
        RelativeLayout.LayoutParams textureView_params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textureView_params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textureView.setLayoutParams(textureView_params);

        //outer layout
        RelativeLayout outer = new RelativeLayout(this);
        outer.setId(R.id.outer);
        RelativeLayout.LayoutParams outer_params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //outer_params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        outer.addView(textureView);
        outer.setBackgroundColor(Color.BLACK);
        setContentView(outer, outer_params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textureView != null) textureView.releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textureView != null) textureView.releasePlayer();
    }
}
