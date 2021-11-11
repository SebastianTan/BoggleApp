    // testing strings
// "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI" Debug strings
// debugging EDUUHEIOFTTSRBRMENNOEHIER

//doesn't handle sparse boards well (skipped sections)


package com.example.boggle_solver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
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
import java.util.Collections;
import java.util.List;

public class SolverActivity extends AppCompatActivity {

    final boolean DEBUG = true;
    ArrayList<Integer> sectionHeaders = new ArrayList<>();
    int lineConst = -3; //constant for wordlist display offset
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
        String board = boggleInput.getText().toString();


        //Error check input text

        board = board.toUpperCase();
        for(char c: board.toCharArray()) {
            if((int)c < 'A' || (int)c > 'Z' ){
                output.append("Please input letters!");
                wordList.setText(output);
                return ;
            }
        }

        double boardDim = Math.sqrt(board.length());
        if( boardDim != Math.floor(boardDim)) {
            output.append("Please input a square board! You have only inputted ").append(board.length()).append(" letters.");
            wordList = findViewById(R.id.wordList);
            wordList.setText(output);
            return;
        } else if (boardDim == 0 && !DEBUG){
            output.append("No board inputted!");
            wordList.setText(output);
            return;
        }




        //initialize user-settings
        SharedPreferences sP = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        String dictFilename = sP.getString(getString(R.string.dictPref),"scrabble");
        int dictIndex = R.raw.scrabble;
        sP = this.getSharedPreferences("MinPreferences", Context.MODE_PRIVATE);
        LIMIT_SETTING = sP.getInt("MinPreferences", 4);

        switch(dictFilename) {
            case "scrabble":
                output.append("Using Scrabble dictionary \n");
                break;
            case "yawl":
                dictIndex = R.raw.yawl;
                output.append("Using Awesome Word List \n");
                break;
            default:
                output.append("Error\n");
        }

        //Import dictionary
        String[] dictionary;

        InputStream is = this.getResources().openRawResource(dictIndex);
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
        dictionary = dict.toArray(new String[0]);

        // "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI" Debug strings
        //testing code
        if (DEBUG) {
            board = "EDUUHEIOFTTSRBRMENNOEHIER";
            boardDim = Math.sqrt(board.length());
        }


        //initialize a Boggle solver with a board and a dictionary;
        Boggle boggle = new Boggle(board, LIMIT_SETTING);
        String[] boggleWords = boggle.solveBoggle(dictionary);

        for (int i = 0; i < board.length(); i++){

            output.append(board.charAt(i)).append(" ");
            if ((i % boardDim) == (boardDim -1)) output.append("\n");
        }
        output.append("\n");

        //initialize scoring and count variables
        int len = 0; //current len of words
        int linNum = 0; //# of lines
        int scoreIndex = 0;
        int totalScore = 0;

        //Starts with score for 3-letter words
//        LinkedList<Integer> scoring = new LinkedList<>(Arrays.asList(11,5,3,2,1,1));

        int[] scoring = {1,1,2,3,5,11};

        //temp to calculate the score before writing to output.
        sectionHeaders.add(0);
        ArrayList<String> outputString = new ArrayList<>(0);

        //length of first line. Ensures the number of newlines is correct
        if(wordList.getWidth() <= 720) {
            lineConst = 0;
        }

        //create wordlist output
        for (String s : boggleWords){
            linNum++;
            if (s.length() > len) { //if we have a new set of m-length words
                len = s.length(); //current set of m-length words
                int wordCount = boggle.getCount(len);
                int currScore;
                if(len < 9){ //Max word length for scoring
                    currScore=scoring[len-ABS_MIN];
                } else {
                    currScore=scoring[scoring.length-1]+(len-8)*2;
                }
                currScore *= wordCount;

                outputString.add("\n" + len + "-length words \n");
                outputString.add("Score: " + (currScore) +"\n");
                sectionHeaders.add((int) (linNum+ boardDim + (3*(scoreIndex++)) - lineConst));


//                if(temp != ""){
//                    int offset = 0;
//
//                    if(len==MIN_LEN+1) offset= -1;
//
//                    int wordcount = linNum-lastLinNum+offset;
//                    int value = 1;
//
//                    if(i<scoring.size()-1){
//                        value=scoring.get(i-2);
//                    } else { //use last element for scoring large words
//                        value=scoring.get(scoring.size()-1);
//                    }
//                    totalScore += (wordcount*value);
//
//                    outputString.add("Score: " + (wordcount) +"\n");
//                    lastLinNum=linNum;
//                    outputString.add(temp);
//                    temp="";
//                }
            }
            outputString.add(s + "\n");
        }
//        outputString.add("Score: " + (linNum-lastLinNum+1) +"\n");
//        outputString.add(temp);

        output.append("Total Score: ").append(totalScore).append("\n");
        String out = output.append(String.join("", outputString)).toString();
        wordList.setText(out);

        getWordList();
    }

    //Opens option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openOptions();
            return true;
        }
        return false;
    }
    void getWordList(){
        TextView wordList = findViewById(R.id.wordList);
        String searchable = wordList.getText().toString();
        System.out.print(searchable);
        String searchKey = "BODE";
        if(searchable.contains(searchKey)){
            int index = searchable.indexOf(searchKey);
            int lineNumber = wordList.getLayout().getLineForOffset(index);
            SpannableString newStr = new SpannableString(searchable);
            newStr.setSpan(new BackgroundColorSpan(Color.RED),index,index+searchKey.length(),0);
            wordList.setText(newStr);

//            wordList.scrollTo(0,wordList.getLayout().getLineTop(lineNumber+3));
        }




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
