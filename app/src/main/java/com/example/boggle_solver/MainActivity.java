package com.example.boggle_solver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    final boolean DEBUG = false;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    static final int scrollSpeed = 100;
    // Thread running code

    long startTime = 0;
    int solutionPos = 0;
    float solutionDelay = 10;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;


            TextView textView = findViewById(R.id.textView2);
            int tvHeight = (int) (textView.getLineCount()*textView.getTextSize());
//            String curr_text = textView.getText().toString();
//            curr_text = String.format("%d:%02d\n", minutes, seconds) + curr_text;
//            textView.setText(curr_text);

            //contentOffset to potentially get the full scroll size
            if (tvHeight < scrollSpeed){
                timerHandler.removeCallbacks(timerRunnable);
                return;
            }
            if (millis > 2800) {
                solutionDelay = 1;
                solutionPos += 18*1.5;
            }
            else if (millis > 2250){
                solutionDelay = 4;
                solutionPos += 4;
            }
            else if (millis > 750) {
                solutionDelay = 6;
                solutionPos += 3;
            }
            else {
                solutionPos += 1;
            }

            textView.scrollTo(0,solutionPos);

            if(solutionPos > tvHeight) {
                textView.setGravity(Gravity.BOTTOM);
                //textView.scrollTo(0,tvHeight);
                timerHandler.removeCallbacks(timerRunnable);
                return;
            }

            timerHandler.postDelayed(this, (long)solutionDelay);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textview
        TextView textView = findViewById(R.id.textView2);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
    // Runs on button press
    public void sendMessage(View view) {

        //Reset autoscrolling
        solutionDelay = 10;
        solutionPos = 0;

        String output = ""; //button output text;

        // Get input text
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
//        output += (message + "\n\n"); //debug
        TextView textView = findViewById(R.id.textView2);
        textView.scrollTo(0,0);
        textView.setGravity(-1);
        //Error check input text
        double m_length = Math.sqrt(message.length());
        if( m_length != Math.floor(m_length)) {
            output = "Please check that your board input is the right size!";

            textView.setText(output);
            return;
        }


        String dictionary[] = {};

        StringBuffer buf = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.scrabble);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> dict = new ArrayList<String>();
        String data = null;
        try {
            while ((data = br.readLine()) != null){
                dict.add(data);
            }
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        dictionary = dict.toArray(new String[dict.size()]);




        // "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI" Debug strings
        String board = message.toUpperCase();

        //testing code
        if (DEBUG){
            board = "EDUUHEIOFTTSRBRMENNOEHIER";
            m_length = Math.sqrt(board.length());
        }

        //initialize a Boggle solver with a board and a dictionary;
        Boggle boggle = new Boggle(board);
        String boggleWords[] = boggle.solveBoggle(dictionary);

        for (int i = 0; i < board.length(); i++){
            output+= (board.charAt(i) + " ");
            if ((i % m_length) == (m_length-1)) output += "\n";
        }

        output +="\n";
        for (String s : boggleWords){
            output += (s + "\n");
        }
        textView.setText(output);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

    }
}