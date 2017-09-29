package example.practise.kevin.androidvlctextureview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by app2 on 2017/2/6.
 */

public class TextureVlc extends TextureView implements TextureView.SurfaceTextureListener,IVLCVout.Callback{

    private Handler handler = new Handler();
    private ArrayList<Toast> msjsToast = new ArrayList<Toast>();

    public final static String TAG = "LibVLCAndroidSample/VideoActivity";

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    protected Context mContext;
    private String URL;
    private boolean check =  true;

    public TextureVlc(final Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public TextureVlc(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initVideoView();
    }

    public TextureVlc(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    /**
     * 設定URL
     * */
    public String setURL (String url){
        return  this.URL = url;
    }

    /**
     * setAudio
     * */
    public boolean setAudio (boolean setCheck){
        return  this.check = setCheck;
    }

    private void initVideoView() {
        setFocusable(false);
        setSurfaceTextureListener(this);
    }

    //show toast
    protected void showToast(String msg) {
        Toast t = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        t.show();
        msjsToast.add(t);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        MediaFormat format = new MediaFormat();
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        // Create LibVLC
        // TODO: make this more robust, and sync with audio demo

        //新增vlc comment line 內碼
        ArrayList<String> options = new ArrayList<>();
//            options.add("--subsdec-encoding <encoding>");
//            options.add("--aout=opensles");
        options.add("--no-audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity
        //no audio
        if(!check){
            options.add("--aout=none");
        }
        options.add("--no-sub-autodetect-file");
        options.add("--swscale-mode=0");
        options.add("--network-caching=60000");
        options.add("--avcodec-hw=any");
//        options.add("--rtsp-mcast");
//        options.add("--rtsp-kasenna");
        options.add("--rtsp-tcp");
        options.add("--no-skip-frames");
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        options.add("--http-continuous");
        options.add("--repeat");
        options.add("--loop");
        options.add("-R");
        options.add("--http-reconnect");
//            if (BuildConfig.DEBUG) {
//                options.add("-vvv"); // verbosity
//            }

        //new libVLC
        libvlc = new LibVLC(options);
//            libvlc = new LibVLC();

        // Create media player
        mMediaPlayer = new MediaPlayer(libvlc);
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        // Set up video output
        vout.setVideoView(this);
        vout.addCallback(this);
        if(vout.areViewsAttached())
        {
            vout.detachViews();
        }
        vout.attachViews();

        //new media container
        final Media m = new Media(libvlc, Uri.parse(URL));
        //set decoder message hide
        m.setHWDecoderEnabled(true, true);
        //add media comment line
        m.addOption(":network-caching=5000");
        m.addOption(":clock-jitter=400");
        m.addOption(":clock-synchro=500");

        //decoder all media type
        m.addOption(":codec=ALL");
        mMediaPlayer.setMedia(m);
        //play stream
        mMediaPlayer.play();
        //播放器監聽事件
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.EndReached:
                        showToast("The connection is interrupted, reconnected");
                        mMediaPlayer.setMedia(m);
                        //play stream
                        mMediaPlayer.play();
                        break;
                    case MediaPlayer.Event.Playing:
                        showToast("The connection is successful and starts playing");
                        break;
                    case MediaPlayer.Event.Paused:
                        break;
                    case MediaPlayer.Event.Stopped:
                        break;
                    case MediaPlayer.Event.Opening:
                        showToast("Connecting, please wait ...");
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        showToast("Connection failed, reconnected");
                        mMediaPlayer.setMedia(m);
                        //play stream
                        mMediaPlayer.play();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /*************
     * Surface
     *************/
    private void setSize(int width, int height) {
        int mVideoWidth = 0;
        int mVideoHeight = 0;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        //取得 screen size
        int w = this.getWidth();
        int h = this.getHeight();

        //判斷size是否錯誤
        if (w > h && w < h) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);
        // set display size

        //直接設定熒幕符合方格大小
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width =ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //set layout
//        lp.width = w;
        lp.height = h;
        //set layout
        this.setLayoutParams(lp);
        this.invalidate();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //set size
        setSize(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //remove
        releasePlayer();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void killAllToast(){
        for(Toast t:msjsToast){
            if(t!=null) {
                t.cancel();
            }
        }
        msjsToast.clear();
    }


    public void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        mMediaPlayer.setEventListener(null);
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();
        libvlc = null;
        killAllToast();
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;
        // store video size
        setSize(width, height);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
        Log.i("onSurfacesCreated", "onSurfacesCreated");
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        releasePlayer();
        Log.i("onSurfacesDestroyed", "onSurfacesDestroyed");
    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {
        Log.i("TAG", "@@");
        this.releasePlayer();
    }
}
