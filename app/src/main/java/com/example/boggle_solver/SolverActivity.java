// testing strings
// "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI" Debug strings
// debugging EDUUHEIOFTTSRBRMENNOEHIER

//doesn't handle sparse boards well (skipped sections)


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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SolverActivity extends AppCompatActivity {

    final boolean DEBUG = false;
    ArrayList<Integer> sectionHeaders = new ArrayList<>();
    int NEWLINES = 1; //constant for wordlist display offset
    final int MIN_LEN = 4; //sets an offset for the wordcount
    int LIMIT_SETTING = 4; //user-defined min word length
    final int ABS_MIN = 3; //absolute minimum word length

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //standard init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init wordList
        TextView wordList = findViewById(R.id.wordList);
        wordList.setMovementMethod(new ScrollingMovementMethod());

        //init toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    // Runs on button press and prints solution to boggle board
    public void submitSolve(View view) {

        //initialization
        TextView wordList = findViewById(R.id.wordList);
        StringBuilder output = new StringBuilder();
        sectionHeaders.clear();
        wordList.scrollTo(0,0);

        // Get input text
        EditText boggleInput =  findViewById(R.id.boggleInput);
        String message = boggleInput.getText().toString();


        //Error check input text
        double m_length = Math.sqrt(message.length());
        if( m_length != Math.floor(m_length)) {
            output.append("Please check that your board input is the right size!");
            wordList = findViewById(R.id.wordList);
            wordList.setText(output);
            return;
        } else if (m_length == 0 && !DEBUG){
            output.append("No board inputted!");
            wordList.setText(output);
            return;
        }
        String board = message.toUpperCase();


        //initialize user-settings
        SharedPreferences sP = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        int dictFilename = sP.getInt(getString(R.string.preference_file_key),R.raw.scrabble);

        sP = this.getSharedPreferences("MinPreferences", Context.MODE_PRIVATE);
        LIMIT_SETTING = sP.getInt("MinPreferences", 4);

        switch(dictFilename) {
            case R.raw.scrabble:
                output.append("Using Scrabble dictionary \n");
                break;
            case R.raw.yawl:
                output.append("Using Awesome Word List \n");
                break;
            default:
                output.append("Error\n");
        }

        //Import dictionary
        String[] dictionary;

        InputStream is = this.getResources().openRawResource(dictFilename);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> dict = new ArrayList<>();
        String data;
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
        //testing code
        if (DEBUG) {
            board = "EDUUHEIOFTTSRBRMENNOEHIER";
            m_length = Math.sqrt(board.length());
        }


        //initialize a Boggle solver with a board and a dictionary;
        Boggle boggle = new Boggle(board, LIMIT_SETTING);
        String boggleWords[] = boggle.solveBoggle(dictionary);

        for (int i = 0; i < board.length(); i++){

            output.append(board.charAt(i) + " ");
            if ((i % m_length) == (m_length-1)) output.append("\n");
        }
        output.append("\n");

        //initialize scoring and count variables
        int len = 0;
        int linNum = 0;
        int i = 1;
        int score = 0;


        ArrayList<Integer> scoring = new ArrayList<>(Arrays.asList(1,1,2,3,5,11));
        //sublist ensures proper scoring is used.
        scoring = new ArrayList<> (scoring.subList(LIMIT_SETTING - ABS_MIN, scoring.size()));

        String temp = ""; //temp to calculate the score before writing to output.
        int lastLinNum = 0;
        sectionHeaders.add(0);
        ArrayList<String> outputString = new ArrayList<>(0);

        //length of first line. Ensures the number of newlines is correct
        if(wordList.getWidth() <= 720) {
            NEWLINES = 0;
        }

        //create wordlist output
        for (String s : boggleWords){
            linNum++;
            if (s.length() > len) { //if we have a new set of m-length words
                len = s.length();

                if(temp != ""){
                    int offset = 0;

                    if(len==MIN_LEN+1) offset= -1;

                    int wordcount = linNum-lastLinNum+offset;
                    int value = 1;

                    if(i<scoring.size()-1){
                        value=scoring.get(i-2);
                    } else { //use last element for scoring large words
                        value=scoring.get(scoring.size()-1);
                    }
                    score += (wordcount*value);

                    outputString.add("Score: " + (wordcount) +"\n");
                    lastLinNum=linNum;
                    outputString.add(temp);
                    temp="";
                }




                outputString.add("\n" + len + "-length words \n");
                sectionHeaders.add((int) (linNum+m_length + (3*(i++)) - NEWLINES ));
            }
            temp += (s + "\n");
        }
        outputString.add("Score: " + (linNum-lastLinNum+1) +"\n");
        outputString.add(temp);

        output.append("Total Score: " + score + "\n");
//        System.out.println(out);
        wordList.setText(output.append(String.join("", outputString)));

    }

    //Opens option menu
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

    //Wrapper to move up a wordlist section
    public void upSection(View view){

        toSection(true);
    }

    //Wrapper to move down a wordlist section
    public void downSection(View view){

        toSection(false);
    }

    // Performs logic to move sections
    void toSection(boolean up) {

        TextView textView = findViewById(R.id.wordList);
        ArrayList<Integer> localSections = new ArrayList<>(sectionHeaders);
        if(up) {
            Collections.reverse(localSections);
        }

        int pos=0;
        for(int sections : localSections) {
            pos = textView.getLayout().getLineTop(sections);
            //down
            if(!up && pos > textView.getScrollY()) {
                break;

            } else if (up && pos < textView.getScrollY()){ //up
                break;
            }
        }
        textView.scrollTo(0,pos);
    }
}
