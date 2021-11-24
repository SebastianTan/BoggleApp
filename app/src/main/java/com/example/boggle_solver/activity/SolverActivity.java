    // testing strings
// "LILEROHTOPENZOIA" "LCTCWHTEOEIRBSHI" Debug strings
// debugging EDUUHEIOFTTSRBRMENNOEHIER

//doesn't handle sparse boards well (skipped sections)


package com.example.boggle_solver.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;

import com.example.boggle_solver.Boggle;
import com.example.boggle_solver.R;
import com.example.boggle_solver.util.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class SolverActivity extends AppCompatActivity {

        final boolean DEBUG = false;
        Map<Integer, ArrayList<Integer>> wordIds = new HashMap<>();
        int LIMIT_SETTING = 4; //user-defined min word length
        final int ABS_MIN = 3; //absolute minimum word length
        String board = new String(new char[LIMIT_SETTING*LIMIT_SETTING]).replace("\0","*");

        ArrayList<Integer> tiles = new ArrayList<>();


        @SuppressLint("ResourceType")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            //standard init
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //
            View parent = findViewById(R.id.parentConstraints);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });

            //init toolbar
            Toolbar myToolbar = findViewById(R.id.mainToolbar);
            myToolbar.inflateMenu(R.menu.menu);
            setSupportActionBar(myToolbar);

            //init spinner
            Spinner spinner = findViewById(R.id.dimSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.spinnerOps, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    String option = adapterView.getItemAtPosition(position).toString();
                    LIMIT_SETTING=Integer.parseInt(option.substring(0,1));
                    makeBoard();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.measure(spinner.getWidth(),spinner.getHeight());
            spinner.getLayoutParams().width = (int) (spinner.getMeasuredWidth() * 1.25);

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }


        // Runs on button press and prints solution to boggle board
        public void submitSolve(View view) {
            //initialization
            StringBuilder output = new StringBuilder();
            ConstraintLayout parentConstraints = findViewById(R.id.parentConstraints);


            // Get input text
//            EditText boggleInput = findViewById(R.id.lineInput);
//            String board = boggleInput.getText().toString();
            char [] boardChars = board.toCharArray();
            for(int i = 0; i < tiles.size() ; i++ ){
                TextView et = findViewById(tiles.get(i));
                String tileString = et.getText().toString();
                if(tileString.length() != 0)
                    boardChars[i] = tileString.charAt(0);
                else{
                    makeErrorView(getString(R.string.error_missing_letter));
                    return;
                }

            }
            board = String.valueOf(boardChars);


            //Error check input text
            board = board.toUpperCase();
            for (char c : board.toCharArray()) {
                if ((int) c < 'A' || (int) c > 'Z') {
                    makeErrorView(getString(R.string.error_invalid_char));
                    return;
                }
            }

            double boardDim = Math.sqrt(board.length());
            if (boardDim != Math.floor(boardDim)) {
                makeErrorView(getString(R.string.error_small_board));

                return;
            } else if (boardDim == 0 && !DEBUG) {
                makeErrorView("No board inputted!");
                return;
            }


            //initialize user-settings
            SharedPreferences sP = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String dictFilename = sP.getString(getString(R.string.dictPref), "scrabble");
            int dictIndex = R.raw.scrabble;
            sP = this.getSharedPreferences("MinPreferences", Context.MODE_PRIVATE);
            LIMIT_SETTING = sP.getInt("MinPreferences", 4);

            switch (dictFilename) {
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
                while ((data = br.readLine()) != null) {
                    dict.add(data);
                }
            } catch (Exception e) {
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

            output.append("\n");

            View sectionsScroller = findViewById(R.id.wordListWrap);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)sectionsScroller.getLayoutParams();
            lp.topMargin = 25;

            //initialize scoring and count variables
            int len = 0; //current len of words

            //Starts with score for 3-letter words
//        LinkedList<Integer> scoring = new LinkedList<>(Arrays.asList(11,5,3,2,1,1));

            int[] scoring = {1, 1, 2, 3, 5, 11};

            //temp to calculate the score before writing to output.

            //length of first line. Ensures the number of newlines is correct


            //create wordlist output
            int id = Constraints.LayoutParams.PARENT_ID;
            int currId = 0; //Careful

            LinearLayout wordScroller = findViewById(R.id.wordListLayout);
            wordScroller.removeAllViews();

            ConstraintSet constraintSet = new ConstraintSet();
            for (String s : boggleWords) {

                if (s.length() > len) {

                    //calculate score
                    len = s.length(); //current set of m-length words
                    int currScore;
                    if (len < 9) { //Max word length for scoring
                        currScore = scoring[len - ABS_MIN];
                    } else {
                        currScore = scoring[scoring.length - 1] + (len - 8) * 2;
                    }

                    //Generate TextView Header
                    TextView header = new TextView(this);
                    int headerId = View.generateViewId();
                    header.setId(headerId);
                    header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                    header.setBackgroundColor(0xFFA1A1A1);
                    header.setText(String.format("%d-letter words %d points", len, currScore * len));
                    header.setGravity(Gravity.CENTER);
                    header.setPadding(0, 10, 0, 10);

                    header.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    wordScroller.addView(header);

                    header.setOnClickListener(this::hideView);

                    constraintSet.clone(parentConstraints);
                    constraintSet.connect(header.getId(), ConstraintSet.TOP, id, ConstraintSet.TOP, 0);
                    constraintSet.connect(header.getId(), ConstraintSet.LEFT, id, ConstraintSet.LEFT, 0);
                    constraintSet.applyTo(parentConstraints);

                    wordIds.put(headerId, new ArrayList<>());
                    currId=headerId;

                    //Make Divider
                    id = makeDivider(wordScroller, headerId);
                }

                //Generate wordList Entry
                TextView sectionWords = new TextView(this);
                int wordEntryId = View.generateViewId();
                sectionWords.setId(wordEntryId);
                sectionWords.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
//                sectionWords.setBackgroundColor(0xFFA1A1A1);
                sectionWords.setGravity(Gravity.CENTER_VERTICAL);
                sectionWords.setText(s);
                sectionWords.setPadding(40, 8, 10, 8);

                sectionWords.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                wordScroller.addView(sectionWords);

                constraintSet.clone(parentConstraints);
                constraintSet.connect(sectionWords.getId(), ConstraintSet.TOP, id, ConstraintSet.BOTTOM, 0);
                constraintSet.connect(sectionWords.getId(), ConstraintSet.LEFT, id, ConstraintSet.LEFT, 0);
                constraintSet.applyTo(parentConstraints);

                wordIds.get(currId).add(wordEntryId);
                id = makeDivider(wordScroller, wordEntryId);
                wordIds.get(currId).add(id);


//            section.append(s + "\n");
            }
            getWordList();
        }

        void makeBoard(){

            int vert_id = R.id.dimSpinner;
            int horz_id = Constraints.LayoutParams.PARENT_ID;

            ConstraintLayout boardContainer = findViewById(R.id.boardContainer);
            boardContainer.removeAllViews();
            ConstraintSet constraintSet = new ConstraintSet();

            for (int i = 0; i < LIMIT_SETTING; i++) {
                int[]chainIds = new int[LIMIT_SETTING];
                for(int j = 0; j < LIMIT_SETTING; j++){

                    EditText cell = new EditText(this);
                    EditText lastCell = findViewById(horz_id);
                    int id = View.generateViewId();
                    chainIds[j]=id;
                    cell.setId(id);
                    tiles.add(id);

                    cell.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                        @Override
                        public void onFocusChange(View v, boolean hasFocus){
                            EditText e = (EditText)v;
                            if(e.getText().toString().length() == 1){
                                e.setCursorVisible(false);
                            }
                        }

                    });

                    if(lastCell != null) lastCell.setNextFocusDownId(id);
                    cell.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
                    cell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
                    cell.setHint(R.string.hintCell);
                    cell.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    cell.setBackgroundColor(getResources().getColor(R.color.gray));
                    cell.setPadding(10,10,10,10);
                    cell.setGravity(Gravity.CENTER);
                    boardContainer.addView(cell);
                    ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) cell.getLayoutParams();

                    lp.width = 0;
                    lp.height = ConstraintSet.WRAP_CONTENT;

                    constraintSet.clone(boardContainer);
                    constraintSet.setDimensionRatio(id, "W, 1:1");

                    constraintSet.connect(id, ConstraintSet.TOP, vert_id, ConstraintSet.BOTTOM, 10);

                    constraintSet.setMargin(id,ConstraintSet.START,10);

                    constraintSet.applyTo(boardContainer);
                    horz_id=id;

                    cell.requestLayout();
                    boardContainer.requestLayout();

                    cell.addOnLayoutChangeListener((v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas) -> {
                        int widthWas = rightWas - leftWas; // Right exclusive, left inclusive
                        if (widthWas < v.getWidth()) {

                            lp.width = v.getWidth();
                            lp.height = v.getWidth();

                        }
                    }
                    );
                }
                float[]weights = new float[LIMIT_SETTING];
                Arrays.fill(weights, (float) 1 / LIMIT_SETTING);
                constraintSet.clone(boardContainer);
                constraintSet.createHorizontalChain(
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                        chainIds,
                        weights,
                        ConstraintSet.CHAIN_PACKED

                        );
//                constraintSet.connect(horz_id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                constraintSet.applyTo(boardContainer);
                vert_id=horz_id;
            }

//            for(int i = 0; i < numChildren; i++) boardContainer.removeViewAt(0);
        }
        public void clearBoard(View v) {
            tiles.forEach((viewId) -> {
                TextView view = findViewById(viewId);
                view.setText(null);
            });
        }


        void hideView(View view) {
            for(int id : wordIds.get(view.getId())){
                View v = findViewById(id);
                if ((v.getVisibility() == View.GONE)) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.GONE);
                }
            }

        }

        int makeDivider(LinearLayout l, int lastId) {
            View divider = new View(this);
            int retId = View.generateViewId();
            divider.setId(retId);
            divider.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    5
            ));
            divider.setBackgroundColor(getResources().getColor(R.color.black));
            l.addView(divider);

            ConstraintLayout parentConstraints = findViewById(R.id.parentConstraints);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(parentConstraints);
            constraintSet.connect(retId, ConstraintSet.TOP, lastId, ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(parentConstraints);
            return retId;
        }

        //Opens option menu
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_settings) {
                Utils.changeActivity(this,OptionsMenu.class);
                return true;
            }
            if(item.getItemId() == R.id.add_word) {
                Utils.changeActivity(this, AddWord.class);
                                return true;
            }
            return false;
        }

        void makeErrorView(String s){
            LinearLayout wll = findViewById(R.id.wordListLayout);
            wll.removeAllViews();
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setTextSize(24);
            wll.addView(tv);
        }

        void getWordList() {
//    //            TextView wordList = findViewById(R.id.wordList);
//    //            String searchable = wordList.getText().toString();
//    //            System.out.print(searchable);
//    //            String searchKey = "BODE";
//    //            if (searchable.contains(searchKey)) {
//    //                int index = searchable.indexOf(searchKey);
//    //                SpannableString newStr = new SpannableString(searchable);
//    //                newStr.setSpan(new BackgroundColorSpan(Color.RED), index, index + searchKey.length(), 0);
//    //                wordList.setText(newStr);
//
//    //            wordList.scrollTo(0,wordList.getLayout().getLineTop(lineNumber+3));
//            }


        }

        // Navigation

    }

