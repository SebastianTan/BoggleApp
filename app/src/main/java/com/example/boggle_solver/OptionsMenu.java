package com.example.boggle_solver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class OptionsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_menu);

        Intent intent = getIntent();

        Toolbar optionsToolbar = (Toolbar) findViewById(R.id.optionsToolbar);
        optionsToolbar.inflateMenu(R.menu.back);
        setSupportActionBar(optionsToolbar);
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }
    public void dict1(View view){
        updateDict(1);
    }
    public void dict2(View view){
        updateDict(0);
    }

    public void updateDict(int i) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        switch(i) {
            case 1:
                editor.putInt(getString(R.string.preference_file_key), R.raw.yawl);
                break;
            default:
                editor.putInt(getString(R.string.preference_file_key), R.raw.scrabble);
        }
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}