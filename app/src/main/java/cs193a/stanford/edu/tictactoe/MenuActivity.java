package cs193a.stanford.edu.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity {

    SharedPreferences sp;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Create typeface object
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");
        Button singleBtn = findViewById(R.id.singleBtn);
        Button multiBtn = findViewById(R.id.multiBtn);
        Button settingBtn = findViewById(R.id.settingBtn);
        TextView gameName = findViewById(R.id.gameName);
        Button aboutBtn = findViewById(R.id.aboutBtn);

        // Set typeface
        singleBtn.setTypeface(face);
        multiBtn.setTypeface(face);
        settingBtn.setTypeface(face);
        gameName.setTypeface(face);
        aboutBtn.setTypeface(face);

        String startColor = "#d4fc79";
        String endColor = "#96e6a1";
        /*
        String startColor = "#ff9a9e";
        String endColor = "#fad0c4";
*/
        singleBtn.getPaint().setShader(new LinearGradient(0,0,0,singleBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
        multiBtn.getPaint().setShader(new LinearGradient(0,0,0,multiBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
        settingBtn.getPaint().setShader(new LinearGradient(0,0,0,settingBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
        aboutBtn.getPaint().setShader(new LinearGradient(0,0,0,aboutBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));

/*
      //  1
        String startColor = "#ff9a9e";
        String endColor = "#fad0c4";
*/
/*
        String startColor = "#d4fc79";
        String endColor = "#96e6a1";
        */
/*
        String startColor = "#84fab0";
        String endColor = "#8fd3f4";
*/



/*
        String startColor = "#2575fc";
        String endColor = "#6a11cb";

*/

        startColor = "#fa709a";
        endColor = "#fee140";
        gameName.getPaint().setShader(new LinearGradient(0,0,0,gameName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
        // get default preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        soundSetting();
    }

    private void soundSetting(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    //   .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        soundClick = soundPool.load(this, R.raw.button_click,  1);
    }

    public void playClickSound(){
        // play click sound if mute is false
        if (!sp.getBoolean("mute", false)){
            // Play click sound
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
        }
    }
    public void singlePlayerSelected(View view) {
        playClickSound();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "single");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void multiPlayerSelected(View view) {
        playClickSound();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "multi");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void settingsClicked(View view) {
        playClickSound();
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void aboutClicked(View view) {
        playClickSound();
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
