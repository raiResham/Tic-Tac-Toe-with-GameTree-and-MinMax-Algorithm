package cs193a.stanford.edu.tictactoe;

import android.app.Activity;
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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LevelActivity extends Activity {

    private SharedPreferences sp;
    private Typeface face;
    private String level ="";
    private TextView currentLevel = null;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        // Create typeface object
        face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");
        Button easyBtn = (Button)findViewById(R.id.easyBtn);
        Button hardBtn = (Button)findViewById(R.id.hardBtn);
        TextView headingName = (TextView)findViewById(R.id.headingName);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Get currently set level
        level = sp.getString("level", "easy");

        currentLevel = (TextView)findViewById(R.id.currentLevel);
        currentLevel.setTypeface(face);

        // Set typeface
        easyBtn.setTypeface(face);
        hardBtn.setTypeface(face);
        headingName.setTypeface(face);

        String startColor = "#d4fc79";
        String endColor = "#96e6a1";

        easyBtn.getPaint().setShader(new LinearGradient(0,0,0,easyBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
        hardBtn.getPaint().setShader(new LinearGradient(0,0,0,hardBtn.getLineHeight(), Color.parseColor("#ff758c"),  Color.parseColor("#ff7eb3"),Shader.TileMode.CLAMP));
    //    hardBtn.getPaint().setShader(new LinearGradient(0,0,0,hardBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));
   //     currentLevel.getPaint().setShader(new LinearGradient(0,0,0,currentLevel.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor),Shader.TileMode.CLAMP));

        startColor = "#fa709a";
        endColor = "#fee140";
        headingName.getPaint().setShader(new LinearGradient(0,0,0,headingName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        // Set level
        if(level.equals("easy")){
            currentLevel.setText("Current Level : Easy");
            currentLevel.getPaint().setShader(new LinearGradient(0,0,0,currentLevel.getLineHeight(), Color.parseColor("#d4fc79"),  Color.parseColor("#96e6a1"), Shader.TileMode.CLAMP));
        }else{
            currentLevel.setText("Current Level : Hard");
            currentLevel.getPaint().setShader(new LinearGradient(0,0,0,currentLevel.getLineHeight(), Color.parseColor("#ff758c"),  Color.parseColor("#ff7eb3"),Shader.TileMode.CLAMP));
        }
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // level - "easy" was selected
    public void easyClick(View view) {
        playClickSound();
        SharedPreferences.Editor ed = sp.edit();
        //set level to easy
        ed.putString("level", "easy");
        ed.commit();
        currentLevel.setText("Current Level : Easy");
        currentLevel.getPaint().setShader(new LinearGradient(0,0,0,currentLevel.getLineHeight(), Color.parseColor("#d4fc79"),  Color.parseColor("#96e6a1"), Shader.TileMode.CLAMP));
     }

    // level - "easy" was selected
    public void hardClick(View view) {
        playClickSound();
        SharedPreferences.Editor ed = sp.edit();
        //set level to hard
        ed.putString("level", "hard");
        ed.commit();
        currentLevel.setText("Current Level : Hard");
        currentLevel.getPaint().setShader(new LinearGradient(0,0,0,currentLevel.getLineHeight(), Color.parseColor("#ff758c"),  Color.parseColor("#ff7eb3"),Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
