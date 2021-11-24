package com.example.boggle_solver.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.boggle_solver.R;


public class OptionsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_menu);

        Toolbar optionsToolbar = findViewById(R.id.optionsToolbar);
        optionsToolbar.inflateMenu(R.menu.back);
        setSupportActionBar(optionsToolbar);


        int[] buttons = {
                R.id.dict1,
                R.id.dict2,
                R.id.button3word,
                R.id.button4word
        };

        Button button = findViewById(buttons[0]);
        button.setOnClickListener(this::dict1);

        button =  findViewById(buttons[1]);
        button.setOnClickListener(this::dict2);

        button = findViewById(buttons[2]);
        button.setOnClickListener(this::threeWord);

        button = findViewById(buttons[3]);
        button.setOnClickListener(this::fourWord);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //onclick methods



    //dictionaries
    public void dict1(View view){
        updateDict(1);
    }
    public void dict2(View view){
        updateDict(0);
    }

    //word buttons
    public void threeWord(View view) {
        updateMin(3);
    }

    public void fourWord (View view) {
        updateMin(4);

    }

    //shared pref functions which update values

    public void updateDict(int i) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        if (i == 1) {
            editor.putString(getString(R.string.dictPref), "yawl");
        } else {
            editor.putString(getString(R.string.dictPref), "scrabble");
        }
        editor.apply();
    }

    public void updateMin(int minWord) {
        SharedPreferences sharedPref = this.getSharedPreferences("MinPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("MinPreferences", minWord);
        editor.apply();
    }
}