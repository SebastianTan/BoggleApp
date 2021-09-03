package com.example.boggle_solver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final boolean DEBUG = false;
    ArrayList<Integer> sectionHeaders = new ArrayList<Integer>();
    int scroll = 0;
    final int NEWLINES = 1;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.wordList);
        textView.setMovementMethod(new ScrollingMovementMethod());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    // Runs on button press
    public void sendMessage(View view) {


        TextView textView = findViewById(R.id.wordList);
        textView.scrollTo(0,0);

        String output = ""; //button output text;

        // Get input text
        EditText editText = (EditText) findViewById(R.id.boggleInput);
        String message = editText.getText().toString();
//        output += (message + "\n\n"); //DEBUG

        //Error check input text
        double m_length = Math.sqrt(message.length());
        if( m_length != Math.floor(m_length)) {
            output = "Please check that your board input is the right size!";
            textView = findViewById(R.id.wordList);
            textView.setText(output);
            return;
        }

        SharedPreferences sP = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        int dictFilename = sP.getInt(getString(R.string.preference_file_key),R.raw.scrabble);

        switch(dictFilename) {
            case R.raw.scrabble:
                output += "Using Scrabble dictionary \n";
                break;
            case R.raw.yawl:
                output += "Using Awesome Word List \n";
                break;
            default:
                output += "Error\n";


        }

        String dictionary[] = {};

        StringBuffer buf = new StringBuffer();
        InputStream is = this.getResources().openRawResource(dictFilename);
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
        if (DEBUG) {
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
        int len = 0;
        int linNum = 0;
        int i = 1;
        sectionHeaders.add(0);
        for (String s : boggleWords){
            linNum++;
            if (s.length() > len) {
                len = s.length();
                output += ("\n" + len + "-length words \n");
                sectionHeaders.add((int) (linNum+m_length + (2*(i++)) - NEWLINES ));
            }
            output += (s + "\n");
        }
        textView = findViewById(R.id.wordList);
        textView.setText(output);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()) {
            case R.id.action_settings:
                openOptions();
                return true;

        }
        return false;
    }



    // Navigation
    void openOptions() {
        Intent intent = new Intent(this, OptionsMenu.class);
        startActivity(intent);

    }
    public void upSection(View view){
        toSection(true);
    }
    public void downSection(View view){
        toSection(false);
    }

    void toSection(boolean up) {
        if (up) {
            scroll--;
        } else {
            scroll++;
        }
        if (scroll < 0 ) {
            scroll = 0;
        } else if (scroll > sectionHeaders.size() - 1){
            scroll = sectionHeaders.size() - 1;
        } else {
            TextView textView = findViewById(R.id.wordList);
            int pos = textView.getLayout().getLineTop(sectionHeaders.get(scroll));
            textView.scrollTo(0,pos);
        }
    }
}
