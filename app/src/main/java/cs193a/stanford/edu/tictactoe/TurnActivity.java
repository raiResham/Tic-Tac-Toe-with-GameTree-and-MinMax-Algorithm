package cs193a.stanford.edu.tictactoe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TurnActivity extends Activity {

    private int turn;
    private SharedPreferences sp;
    private final static int X_PIECE = 1; // Human
    private final static int O_PIECE = 2; // Computer
    private Typeface face = null;
    private TextView txtView = null;
    private SoundPool soundPool;
    private int soundClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn);

        // get preference manager
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        // get value of turn
        turn = sp.getInt("turn",X_PIECE); // By default Human gets to place fist piece

        // create typeface
        face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");

        txtView =(TextView)findViewById(R.id.turnId);
        // set typeface
        txtView.setTypeface(face);
        // set text
        if(turn == X_PIECE){
            // human gets first chance to place his/her piece
            txtView.setText("First Turn : Human");
        }else{
            // computer gets its first chance
            txtView.setText("First Turn : Computer");
        }

        TextView headingName = (TextView)findViewById(R.id.headingName);
        Button humanBtn = (Button)findViewById(R.id.humanBtn);
        Button computerBtn = (Button)findViewById(R.id.computerBtn);

        headingName.setTypeface(face);
        humanBtn.setTypeface(face);
        computerBtn.setTypeface(face);

        String startColor = "#d4fc79";
        String endColor = "#96e6a1";

        humanBtn.getPaint().setShader(new LinearGradient(0,0,0,humanBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        computerBtn.getPaint().setShader(new LinearGradient(0,0,0,computerBtn.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        txtView.getPaint().setShader(new LinearGradient(0,0,0,txtView.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        startColor = "#fa709a";
        endColor = "#fee140";
        headingName.getPaint().setShader(new LinearGradient(0,0,0,headingName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        soundSetting();
    }

    public void humanClicked(View view) {
        playClickSound();
        SharedPreferences.Editor ed = sp.edit();
        //set turn to HUMAN
        ed.putInt("turn", X_PIECE);
        ed.commit();
        txtView.setText("First Turn : Human");
    }

    public void computerClicked(View view) {
        playClickSound();
        SharedPreferences.Editor ed = sp.edit();
        //set turn to COMPUTER
        ed.putInt("turn", O_PIECE);
        ed.commit();
        txtView.setText("First Turn : Computer");
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
}
