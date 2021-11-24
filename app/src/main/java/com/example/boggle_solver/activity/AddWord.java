package com.example.boggle_solver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.boggle_solver.R;
import com.example.boggle_solver.util.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class AddWord extends AppCompatActivity {

    final String filename = "custom.txt";
    Set<String> words = new HashSet<>();
    int lastId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.wordToolbar);
        toolbar.inflateMenu(R.menu.back);
        setSupportActionBar(toolbar);

        View addWord = findViewById(R.id.confirmWordInput);
        //add word to the modal.
        addWord.setOnClickListener(this::addWord);
        lastId = R.id.confirmWordInput;

        try {
            InputStream inputStream = this.openFileInput(filename);
            if(inputStream != null) {
                InputStreamReader iSR = new InputStreamReader(inputStream);
                BufferedReader bR = new BufferedReader(iSR);

                String word;
                while((word = bR.readLine()) != null) {
                    words.add(word);
                    addWordView(word);
                }
            }
        } catch (FileNotFoundException e){
            System.err.println("fnf");
        } catch(IOException e){
            System.err.println("IO");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back){
            Utils.changeActivity(this, SolverActivity.class);
            return true;
        }
        return false;
    }
    void addWord(View v){
        EditText et = findViewById(R.id.addWordInput);
        String word = et.getText().toString();
        if(words.contains(word) || word.length() < 1){
            Utils.makeAlert("Please input a valid word!", this);
            return;
        }
        try {
            OutputStreamWriter fos = new OutputStreamWriter(this.openFileOutput("custom.txt", Context.MODE_APPEND));
            fos.write(String.format("%s \n", word));
            fos.close();
        } catch (IOException e){
            System.err.println("File write failed");
        }
        et.setText("");
        addWordView(word);
    }

    void addWordView(String word) {

        //word display
        ConstraintSet cs = new ConstraintSet();
        ConstraintLayout parent = findViewById(R.id.addWordParent);
        TextView customWord = new TextView(this);
        int currId = View.generateViewId();
        customWord.setId(currId);
        customWord.setText(word);
        customWord.setTextSize(30);
        customWord.setBackgroundResource(R.drawable.cus_back);
        customWord.setGravity(Gravity.LEFT);
        customWord.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        parent.addView(customWord);

        customWord.setPadding(40,8,12,8);
        cs.clone(parent);
        cs.connect(currId,ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT);
        cs.connect(currId,ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT);
        cs.connect(currId,ConstraintSet.TOP,lastId,ConstraintSet.BOTTOM);
        cs.applyTo(parent);
        lastId=currId;

        //text button to close
        TextView closeCustom = new TextView(this);
        int buttonId = View.generateViewId();
        closeCustom.setId(buttonId);
        closeCustom.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        closeCustom.setText("X");
        closeCustom.setTextSize(20);
        closeCustom.setOnClickListener(this::deleteWord);
        parent.addView(closeCustom);
        cs.clone(parent);
        cs.connect(buttonId, ConstraintSet.RIGHT, currId, ConstraintSet.RIGHT,60);
        cs.connect(buttonId, ConstraintSet.TOP, currId, ConstraintSet.TOP);
        cs.connect(buttonId, ConstraintSet.BOTTOM, currId, ConstraintSet.BOTTOM);
//        cs.constrainDefaultWidth(buttonId, ConstraintSet.MATCH_CONSTRAINT_WRAP);
//        cs.constrainedWidth(buttonId, true);
        cs.applyTo(parent);
    }

    void deleteWord(View v){
        StringBuilder out = new StringBuilder();
        TextView button = (TextView) v;
        ConstraintLayout.LayoutParams cl = (ConstraintLayout.LayoutParams)button.getLayoutParams();
        button.setVisibility(View.GONE);

        TextView customWordView = findViewById(cl.topToTop);
        try {
            InputStream inputStream = this.openFileInput(filename);
            if(inputStream != null) {
                InputStreamReader iSR = new InputStreamReader(inputStream);
                BufferedReader bR = new BufferedReader(iSR);

                String word;
                while((word = bR.readLine()) != null) {
                    if(!word.equals(customWordView.getText().toString())) {
                        out.append(word+"\n");
                    }
                }

                OutputStreamWriter fos = new OutputStreamWriter(this.openFileOutput("custom.txt", Context.MODE_PRIVATE));
                fos.write(out.toString());
                fos.close();
            }
        } catch (FileNotFoundException e){
            System.err.println("fnf");
        } catch(IOException e){
            System.err.println("IO");
        }
        customWordView.setVisibility(View.GONE);
    }
}