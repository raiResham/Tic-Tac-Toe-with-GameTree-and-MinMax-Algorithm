package cs193a.stanford.edu.tictactoe;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static android.widget.GridLayout.OnClickListener;

// This is the main entry point of the app
public class MainActivity extends Activity {

    private SoundPool soundPool;
    private int soundClick;
    private int soundWin;
    private int soundResetClick;
    private int board[][] = new int[3][3];
    private final static int EMPTY_PIECE = -1;
    private final static int X_PIECE = 1;   // Human piece
    private final static int O_PIECE = 2;   // Computer piece
    private int turn = X_PIECE; // X_PIECE is default as first piece. It is also piece of human opponent in multi-player.
    private int numOfPiecesPlaced = 0;
    private final static int X_PIECE_WON = -10000;
    private final static int O_PIECE_WON = 10000;
    private final static int DRAW = 0;
    private String winningPattern = "";
    private final static int CONTINUE = -1;
    private boolean gameOver = false;
    private Button cells[][] = new Button[3][3];
    private String mode = ""; // "single" or "multi"
    private int maxdepth = 20;
    private ArrayList<String> best_moves = new ArrayList<>();
    private String level = "";
    private Typeface face;
    private SharedPreferences sp;
    private int firstTurn;
    private ProgressBar progressBar;
    private Best best = new Best();
    private boolean doingBackgroundTask;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        face = Typeface.createFromAsset(getAssets(), "fonts/SLANT.ttf");
        TextView turnMsg = (TextView) findViewById(R.id.turnMsg);
        turnMsg.setTypeface(face);

        TextView levelView = (TextView) findViewById(R.id.levelId);
        levelView.setTypeface(face);

        progressBar = findViewById(R.id.progressbar);

        TextView modeView = (TextView) findViewById(R.id.modeId);
        modeView.setTypeface(face);

        //set font to reset button
        Button resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setTypeface(face);

        levelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        modeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);


        // get default preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Helps edit data in shared preference
        SharedPreferences.Editor ed = sp.edit();

        //get level info either "easy" or "hard"
        level = sp.getString("level", "easy");

        // Extract intent
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        //Show mode "Single or Multi-player"
        if (mode.equals("single")) {
            modeView.setText("Mode : Single");
        } else {
            modeView.setText("Mode : Multiplayer");
        }

        if (mode.equals("single")) {
            firstTurn = turn = sp.getInt("turn", X_PIECE);
            if (level.equals("easy")) {
                levelView.getPaint().setShader(new LinearGradient(0, 0, 0, levelView.getLineHeight(), Color.parseColor("#96e6a1"), Color.parseColor("#96e6a1"), Shader.TileMode.CLAMP));
                levelView.setText("Level : Easy");
            } else {
                levelView.getPaint().setShader(new LinearGradient(0, 0, 0, levelView.getLineHeight(), Color.parseColor("#ff758c"), Color.parseColor("#ff7eb3"), Shader.TileMode.CLAMP));
                levelView.setText("Level : Hard");
            }
        }
        soundSetting();
        init();
    }

    private void init() {
        // Fill tic-tac-toe board with EMPTY_PIECE value. Not visible to user
        int emptyPiece = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY_PIECE;
            }
        }

        // Create 9 image cells where users can click to place their pieces. Visible to user.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button cell = new Button(this);
                cell.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                ((ViewGroup) findViewById(R.id.grid)).addView(cell);
                // Add cell to imageView
                cells[i][j] = cell;
                cell.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cellClicked(v);
                    }
                });
                cell.setBackgroundResource(R.drawable.circle);
                cell.setTag(String.valueOf("(" + i + ", " + j + ")"));
                cell.setTypeface(face);
                cell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 80);
                // Setting cell size and margin in gridlayout
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                cell.getLayoutParams().width = (int) px;
                cell.getLayoutParams().height = (int) px;
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) cell.getLayoutParams();
                params.setMargins(10, 10, 10, 10); // left, top, right, bottom
            }
        }
        if (turn == X_PIECE) {
            setTurnMessage("X's turn!");
            if (mode.equals("single")) {
                setTurnMessage("Your turn!");
            }
            setTurnColor('X');
        } else {
            setTurnMessage("O's turn!");
            setTurnColor('O');
        }

        start();
    }

    private void setTurnMessage(String msg) {
        TextView txtView = (TextView) findViewById(R.id.turnMsg);
        txtView.setText(msg);
    }

    private void setTurnColor(char turn) {
        TextView txtView = (TextView) findViewById(R.id.turnMsg);
        if (turn == 'X') {
            txtView.setTextColor(Color.parseColor("#145A32"));// old color : #9fedd7
        } else if (turn == 'O') {
            txtView.setTextColor(Color.parseColor("#E74C3C"));// old color : #f3749
        } else {
            txtView.setTextColor(Color.parseColor("#00ffd4"));
        }
    }

    private void start() {
        checkComputerTurn();
    }

    private void soundSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    //   .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        soundClick = soundPool.load(this, R.raw.snd_click, 1);
        soundWin = soundPool.load(this, R.raw.snd_win, 1);
        soundResetClick = soundPool.load(this, R.raw.button_click, 1);
    }

    private void cellClicked(View v) {
        if (!gameOver) {
            // Handle cell click
            if (v.isEnabled()) {
                Button cell = (Button) v;
                cell.setEnabled(false);
                String tag = (String) v.getTag();
                // Get the coordinate of the clicked cell
                int i = Character.getNumericValue(tag.charAt(1));
                int j = Character.getNumericValue(tag.charAt(4));
                // register the click in board array
                board[i][j] = turn;
                if (turn == X_PIECE) {
                    cell.setText("X");
                    cell.setTextColor(Color.parseColor("#145A32")); // #9fedd7
                } else {
                    cell.setText("O");
                    cell.setTextColor(Color.parseColor("#E74C3C")); // #f37498
                }
                numOfPiecesPlaced++;
                swapPlayer();

                // Animate towards x - direction
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "scaleX", .9f);
                animX.setRepeatCount(1);
                animX.setRepeatMode(ObjectAnimator.REVERSE);

                // Animate towards y - direction
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "scaleY", .9f);
                animY.setRepeatCount(1);
                animY.setRepeatMode(ObjectAnimator.REVERSE);

                // Combine/Orchestrate both x and y direction animation using AnimatorSet object
                AnimatorSet animSetXY = new AnimatorSet();
                animSetXY.playTogether(animX, animY);
                animSetXY.start();

                if (!sp.getBoolean("mute", false)) {
                    // Play click sound
                    soundPool.play(soundClick, 1, 1, 0, 0, 1);
                }

                // Check the status of game such as win or continue or draw.
                int res = evaluate();
                if (res == X_PIECE_WON) {
                    setTurnMessage("X won!");
                    if (mode.equals("single")) {
                        setTurnMessage("You Won!");
                    }
                    setTurnColor('X');
                    gameOver = true;
                    animate("win");
                }
                if (res == O_PIECE_WON) {
                    setTurnMessage("O won!");
                    if (mode.equals("single")) {
                        setTurnMessage("I Won!");
                    }
                    setTurnColor('O');
                    gameOver = true;
                    animate("win");
                }
                if (res == DRAW) {
                    setTurnMessage("Draw!");
                    setTurnColor('A');
                    gameOver = true;
                    animate("draw");
                }
                checkComputerTurn();
            }
        }
    }

    private void checkComputerTurn() {
        if (mode.equals("single") && level.equals("easy") && (turn == O_PIECE) && gameOver == false) {
            //   System.out.println("help");
            // level is set to easy
            Best best = randomMove();
            cellClicked(cells[best.i][best.j]);
        } else if (mode.equals("single") && level.equals("hard") && (turn == O_PIECE) && gameOver == false) {

            // Disable click in cells as it turn of computer
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cells[i][j].setEnabled(false);
                }
            }


            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);
            // level is set to hard
            // Check the player mode and computer turn


            //         Best best = chooseMove(O_PIECE, maxdepth);


            doingBackgroundTask = true;
            new ChooseMove().execute();


        }
    }


    private class ChooseMove extends AsyncTask<Void, Void, Best> {

        @Override
        protected Best doInBackground(Void... voids) {
            Log.e("ChooseMove", "doInBackground");
            Best best = chooseMove(O_PIECE, maxdepth);
            return best;
        }

        @Override
        protected void onPostExecute(Best finalBest) {
            super.onPostExecute(finalBest);
            best.i = finalBest.i;
            best.j = finalBest.j;
            Log.e("Choosemove", finalBest.i + " ," + finalBest.j);
            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            Log.e("MainActivity", best.i + ", " + best.j);


            int row = best.i;
            int col = best.j;
            cells[row][col].setEnabled(true);

            cellClicked(cells[row][col]);

            // Enable empty cells
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY_PIECE)
                        cells[i][j].setEnabled(true);
                }
            }

            doingBackgroundTask = false;

        }

        public Best chooseMove(int side, int depth) {
            Best myBest = new Best();
            Best reply = null;
            int result = evaluate();

            if (result == X_PIECE_WON || result == O_PIECE_WON) {
                myBest.score = result;
                return myBest;
            }

            if (draw()) {
                // We set score to zero when draw occurs.
                myBest.score = 0;
                return myBest;
            }

            if (side == O_PIECE) {
                // Set unbelievable worst value so computer is forced to take at least one move no matter if its bad
                myBest.score = -1000000;
            } else {
                // Set unbelievable worst value so Human is forced to take at least one move no matter if its bad
                myBest.score = 1000000;
            }

            // Look for possible moves for AI
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (isValidMove(i, j)) {
                        doMove(i, j, side);
                        reply = chooseMove((side == O_PIECE) ? X_PIECE : O_PIECE, depth - 1);
                        undoMove(i, j);

                        if (depth == maxdepth) {
                            if (side == O_PIECE) {
                                System.out.println(i + " " + j + " score :" + reply.score);
                                if (reply.score > myBest.score) {
                                    // clear previous best_moves as new best score is found
                                    best_moves.clear();
                                    // add position in best_moves arraylist
                                    best_moves.add(String.valueOf(i + "" + j + reply.score));
                                } else if (reply.score == myBest.score) {
                                    // add position in best_moves arraylist
                                    // all moves in best_moves arraylist are equally good
                                    best_moves.add(String.valueOf(i + "" + j + reply.score));
                                }
                            }
                        }
                        if (side == O_PIECE && reply.score > myBest.score) {
                            // Better move found for AI
                            myBest.score = reply.score;
                            myBest.i = i;
                            myBest.j = j;
                        } else if (side == X_PIECE && reply.score < myBest.score) {
                            // Human found better move
                            myBest.score = reply.score;
                            myBest.i = i;
                            myBest.j = j;
                        }

                    }
                }
            }
            // Randomly select among equally good moves from best_moves arraylist of String
            if (depth == maxdepth) {
                Random r = new Random();
                int index = r.nextInt(best_moves.size());
                myBest.i = Character.getNumericValue((best_moves.get(index)).charAt(0));
                myBest.j = Character.getNumericValue((best_moves.get(index)).charAt(1));
                best_moves.clear();
            }
            return myBest;
        }


    }

    // Computer makes a random move
    public Best randomMove() {
        ArrayList<String> emptyCells = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY_PIECE)
                    emptyCells.add(String.valueOf(i + "" + j));
            }
        }

        // Randomly choose among empty cell
        int randIndex = -1;
        Random rand = new Random();
        randIndex = rand.nextInt(emptyCells.size());
        int i = Character.getNumericValue(emptyCells.get(randIndex).charAt(0));
        int j = Character.getNumericValue(emptyCells.get(randIndex).charAt(1));
        Best myBest = new Best(i, j);
        return myBest;
    }

    private void animate(String msg) {

        // Disable resetButton for 2 sec
        findViewById(R.id.resetBtn).setEnabled(false);
        //reset board after 2 sec
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Enable reset button
                findViewById(R.id.resetBtn).setEnabled(true);
                resetClicked(null);
            }
        }, 2000);

        ArrayList<Animator> anim_list = new ArrayList<Animator>();
        if (msg.equals("win")) {
            if (!sp.getBoolean("mute", false)) {
                soundPool.play(soundWin, 1f, 1f, 0, 0, 1);
            }
            int index = 0;
            for (int i = 0; i < 3; i++) {
                int x = Character.getNumericValue(winningPattern.charAt(index++));
                int y = Character.getNumericValue(winningPattern.charAt(index++));
                ObjectAnimator animX = ObjectAnimator.ofFloat(cells[x][y], "scaleX", .9f);
                animX.setRepeatCount(3);
                animX.setRepeatMode(ObjectAnimator.REVERSE);
                anim_list.add(animX);
                ObjectAnimator animY = ObjectAnimator.ofFloat(cells[x][y], "scaleY", .9f);
                animY.setRepeatCount(3);
                animY.setRepeatMode(ObjectAnimator.REVERSE);
                anim_list.add(animY);
            }
        }

        TextView textView = (TextView) findViewById(R.id.turnMsg);
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "scaleX", 2.5f);
        animX.setRepeatCount(3);
        animX.setRepeatMode(ObjectAnimator.REVERSE);
        anim_list.add(animX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(textView, "scaleY", 1.5f);
        animY.setRepeatCount(3);
        animY.setRepeatMode(ObjectAnimator.REVERSE);
        anim_list.add(animY);
        // Animate cells and message
        AnimatorSet anim = new AnimatorSet();
        anim.setDuration(700);
        anim.playTogether(anim_list);
        anim.start();
    }

    // returns 1 if X_PIECE won,
    //         2 if O_PIECE has won
    //         0 if draw
    //         else -1
    public int evaluate() {
        // Check horizontally
        if (board[0][0] == board[0][1] && board[0][1] == board[0][2]) {
            if (board[0][0] == X_PIECE || board[0][0] == O_PIECE) {
                winningPattern = "000102";
            }
            if (board[0][0] == X_PIECE) return X_PIECE_WON;
            if (board[0][0] == O_PIECE) return O_PIECE_WON;
        }
        if (board[1][0] == board[1][1] && board[1][1] == board[1][2]) {
            if (board[1][0] == X_PIECE || board[1][0] == O_PIECE) {
                winningPattern = "101112";
            }
            if (board[1][0] == X_PIECE) return X_PIECE_WON;
            if (board[1][0] == O_PIECE) return O_PIECE_WON;
        }
        if (board[2][0] == board[2][1] && board[2][1] == board[2][2]) {
            if (board[2][0] == X_PIECE || board[2][0] == O_PIECE) {
                winningPattern = "202122";
            }
            if (board[2][0] == X_PIECE) return X_PIECE_WON;
            if (board[2][0] == O_PIECE) return O_PIECE_WON;
        }

        // Check vertically
        if (board[0][0] == board[1][0] && board[1][0] == board[2][0]) {
            if (board[0][0] == X_PIECE || board[0][0] == O_PIECE) {
                winningPattern = "001020";
            }
            if (board[0][0] == X_PIECE) return X_PIECE_WON;
            if (board[0][0] == O_PIECE) return O_PIECE_WON;
        }
        if (board[0][1] == board[1][1] && board[1][1] == board[2][1]) {
            if (board[0][1] == X_PIECE || board[0][1] == O_PIECE) {
                winningPattern = "011121";
            }
            if (board[0][1] == X_PIECE) return X_PIECE_WON;
            if (board[0][1] == O_PIECE) return O_PIECE_WON;
        }
        if (board[0][2] == board[1][2] && board[1][2] == board[2][2]) {
            if (board[0][2] == X_PIECE || board[0][2] == O_PIECE) {
                winningPattern = "021222";
            }
            if (board[0][2] == X_PIECE) return X_PIECE_WON;
            if (board[0][2] == O_PIECE) return O_PIECE_WON;
        }

        // Check diagonally
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == X_PIECE || board[0][0] == O_PIECE) {
                winningPattern = "001122";
            }
            if (board[0][0] == X_PIECE) return X_PIECE_WON;
            if (board[0][0] == O_PIECE) return O_PIECE_WON;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == X_PIECE || board[1][1] == O_PIECE) {
                winningPattern = "021120";
            }
            if (board[0][2] == X_PIECE) return X_PIECE_WON;
            if (board[0][2] == O_PIECE) return O_PIECE_WON;
        }

        // Nobody has won.
        // Check for draw
        if (numOfPiecesPlaced == 9)
            return DRAW;

        // Continue playing.
        return CONTINUE;
    }

//    private Best chooseMove(int side, int depth){
//        Best myBest = new Best();
//        Best reply = null;
//        int result = evaluate();
//
//        if(result == X_PIECE_WON || result == O_PIECE_WON){
//          myBest.score = result;
//          return myBest;
//        }
//
//        if(draw()){
//            // We set score to zero when draw occurs.
//            myBest.score = 0;
//            return myBest;
//        }
//
//        if(side == O_PIECE){
//            // Set unbelievable worst value so computer is forced to take at least one move no matter if its bad
//            myBest.score = -1000000;
//        }else{
//           // Set unbelievable worst value so Human is forced to take at least one move no matter if its bad
//            myBest.score = 1000000;
//        }
//
//        // Look for possible moves for AI
//        for(int i = 0; i < 3; i++){
//             for(int j = 0; j < 3; j++){
//                if(isValidMove(i,j)){
//                    doMove(i, j, side);
//                    reply = chooseMove((side==O_PIECE)?X_PIECE:O_PIECE, depth - 1);
//                    undoMove(i,j);
//
//                    if(depth == maxdepth){
//                       if(side == O_PIECE ){
//                           System.out.println(i+ " "+j+" score :"+reply.score);
//                           if(reply.score > myBest.score){
//                               // clear previous best_moves as new best score is found
//                               best_moves.clear();
//                               // add position in best_moves arraylist
//                               best_moves.add(String.valueOf(i+""+j+reply.score));
//                           }else if(reply.score == myBest.score){
//                               // add position in best_moves arraylist
//                               // all moves in best_moves arraylist are equally good
//                               best_moves.add(String.valueOf(i+""+j+reply.score));
//                           }
//                       }
//                    }
//                    if(side == O_PIECE && reply.score > myBest.score){
//                        // Better move found for AI
//                        myBest.score = reply.score;
//                        myBest.i = i;
//                        myBest.j = j;
//                    }else if( side == X_PIECE && reply.score < myBest.score){
//                        // Human found better move
//                        myBest.score = reply.score;
//                        myBest.i = i;
//                        myBest.j = j;
//                    }
//
//                }
//            }
//        }
//        // Randomly select among equally good moves from best_moves arraylist of String
//       if(depth == maxdepth){
//           Random r = new Random();
//           int index = r.nextInt(best_moves.size());
//           myBest.i = Character.getNumericValue((best_moves.get(index)).charAt(0));
//           myBest.j = Character.getNumericValue((best_moves.get(index)).charAt(1));
//           best_moves.clear();
//       }
//        return myBest;
//    }

    private void doMove(int i, int j, int side) {
        board[i][j] = side;
        numOfPiecesPlaced++;
    }

    private void undoMove(int i, int j) {
        board[i][j] = EMPTY_PIECE;
        numOfPiecesPlaced--;
    }

    private boolean isValidMove(int i, int j) {
        if (board[i][j] == EMPTY_PIECE) return true;
        return false;
    }

    private boolean draw() {
        if (numOfPiecesPlaced == 9) return true;
        return false;
    }

    public void swapPlayer() {
        if (turn == X_PIECE) {
            turn = O_PIECE;
            setTurnMessage("O's turn!");
            setTurnColor('O');
        } else {
            turn = X_PIECE;
            setTurnMessage("X's turn!");
            if (mode.equals("single"))
                setTurnMessage("Your turn!");
            setTurnColor('X');
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;

//        As per documentation,
//        Remove any pending posts of callbacks
//        and sent messages whose obj is token. If token is null, all callbacks and messages will be removed.
        handler.removeCallbacksAndMessages(null);
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

    public void resetClicked(View view) {
        if (!doingBackgroundTask) {
            if (!sp.getBoolean("mute", false)) {
                // Play click sound
                soundPool.play(soundResetClick, 1, 1, 0, 0, 1);
            }
            // Clear internal board or Fill it with EMPTY_PIECE
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = EMPTY_PIECE;
                }
            }

            // Clear Button cell and enable click
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cells[i][j].setText("");
                    cells[i][j].setEnabled(true);
                }
            }

            // reset values
            if (mode.equals("single")) {
                if (firstTurn == X_PIECE) {
                    turn = X_PIECE;
                    setTurnMessage("Your turn!");
                    setTurnColor('X');
                } else {
                    turn = O_PIECE;
                    setTurnMessage("O's turn!");
                    setTurnColor('O');
                }
            } else {
                turn = X_PIECE;
                setTurnMessage("X's turn!");
                setTurnColor('X');
            }

            numOfPiecesPlaced = 0;
            gameOver = false;
            start();
        }

    }
}
