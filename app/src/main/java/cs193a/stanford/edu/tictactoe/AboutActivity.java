package cs193a.stanford.edu.tictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView headingMsg = (TextView)findViewById(R.id.headingMsg);
        TextView developer = (TextView)findViewById(R.id.developer);
        TextView developerName = (TextView)findViewById(R.id.developerName);
        TextView font = (TextView)findViewById(R.id.font);
        TextView fontName = (TextView)findViewById(R.id.fontName);
        TextView sound = (TextView)findViewById(R.id.soundSource);
        TextView soundName = (TextView)findViewById(R.id.soundSourceName);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");

        headingMsg.setTypeface(face);
        developer.setTypeface(face);
        developerName.setTypeface(face);
        font.setTypeface(face);
        fontName.setTypeface(face);
        sound.setTypeface(face);
        soundName.setTypeface(face);

        String startColor = "#fa709a";
        String endColor = "#fee140";
        headingMsg.getPaint().setShader(new LinearGradient(0,0,0,headingMsg.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));


        startColor = "#d4fc79";
        endColor = "#96e6a1";
        developer.getPaint().setShader(new LinearGradient(0,0,0,developer.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        developerName.getPaint().setShader(new LinearGradient(0,0,0,developerName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        font.getPaint().setShader(new LinearGradient(0,0,0,font.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        fontName.getPaint().setShader(new LinearGradient(0,0,0,fontName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));

        sound.getPaint().setShader(new LinearGradient(0,0,0,sound.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));
        soundName.getPaint().setShader(new LinearGradient(0,0,0,soundName.getLineHeight(), Color.parseColor(startColor),  Color.parseColor(endColor), Shader.TileMode.CLAMP));



        int x = 20;
        int headingSize=x;
        int subheadingSize = x+10;

        developer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingSize);
        developerName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subheadingSize);

        font.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingSize);
        fontName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subheadingSize);

        sound.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingSize);
        soundName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subheadingSize);


    }

}
