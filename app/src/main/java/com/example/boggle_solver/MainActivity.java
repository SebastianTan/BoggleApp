package com.example.boggle_solver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
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

        String output = ""; //button output text;

        // Get input text
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
//        output += (message + "\n\n"); //debug

        //Error check input text
        double m_length = Math.sqrt(message.length());
        if( m_length != Math.floor(m_length)) {
            output = "Please check that your board input is the right size!";
            TextView textView = findViewById(R.id.textView2);
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




        // "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI"
        // "
        String board = message.toUpperCase();

        //testing code
//        String board = "EDUUHEIOFTTSRBRMENNOEHIER";
//        m_length = Math.sqrt(board.length());

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
        TextView textView = findViewById(R.id.textView2);
        textView.setText(output);

    }
}