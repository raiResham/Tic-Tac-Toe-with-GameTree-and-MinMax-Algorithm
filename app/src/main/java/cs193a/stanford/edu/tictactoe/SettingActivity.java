package cs193a.stanford.edu.tictactoe;

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
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends Activity {
    private SharedPreferences sp;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Create typeface object
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");
        Button btn_sound = (Button)findViewById(R.id.btn_sound);
        Button levelBtn = (Button)findViewById(R.id.levelBtn);
        Button turnBtn =(Button)findViewById(R.id.turnBtn);
        TextView headingName = (TextView)findViewById(R.id.headingName);

        // Set font style to text and button
        btn_sound.setTypeface(face);
        levelBtn.setTypeface(face);
        turnBtn.setTypeface(face);
        headingName.setTypeface(face);

        String startColor = "#d4fc79";
        String endColor = "#96e6a1";

        btn_sound.getPaint().setShader(new LinearGradient(0,0,0,btn_sound.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        levelBtn.getPaint().setShader(new LinearGradient(0,0,0,levelBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        turnBtn.getPaint().setShader(new LinearGradient(0,0,0,turnBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        startColor = "#fa709a";
        endColor = "#fee140";
        headingName.getPaint().setShader(new LinearGradient(0,0,0,headingName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));


        // Get shared preference
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Check value of mute in shared preference
        if (sp.getBoolean("mute", false))
            ((Button) findViewById(R.id.btn_sound)).setText("SOUND");
        else{
            ((Button) findViewById(R.id.btn_sound)).setText("MUTE");
        }
        soundSetting();
    }

    public void levelClicked(View view) {
        playClickSound();
        Intent intent = new Intent(this, LevelActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void soundClicked(View view) {
        SharedPreferences.Editor ed = sp.edit();
        // sound
        if (sp.getBoolean("mute", false)) {
            // mute if false means sound is on
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
            ed.putBoolean("mute", false);
            ((Button) findViewById(R.id.btn_sound)).setText("MUTE");

        } else {
            // mute is true so no sound
            ed.putBoolean("mute", true);
            ((Button) findViewById(R.id.btn_sound)).setText("SOUND");
        }
        ed.commit();
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void turnClicked(View view) {
        playClickSound();
        Intent intent = new Intent(this, TurnActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        // Check value of mute in shared preference, sp.
        if (!sp.getBoolean("mute", false)){
            // Play click sound as mute is false.
            soundPool.play(soundClick, 1, 1, 0, 0, 1);
        }
    }
}
