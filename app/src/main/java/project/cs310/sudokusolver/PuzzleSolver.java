package project.cs310.sudokusolver;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//import org.opencv.android.OpenCVLoader;

public class PuzzleSolver extends AppCompatActivity {

    private TableLayout mTableLayout;
    private Board mBoard;
    private Button mClearBoardButton;
    private Button mFullySolveButton_FC;
    private Button mFullySolveButton_BT;
    private Button genButton;
    private Button photoButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    private boolean ignoreNextText; //decides whether to skip textWatcher
    private Uri uri;
    private static String filename;

    final static int DEFAULT_BOARD_SIZE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (!OpenCVLoader.initDebug()) {
//            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
//        } else {
//            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
//        }


        setContentView(R.layout.activity_puzzle_solver);


        mBoard = new Board(DEFAULT_BOARD_SIZE);

        mTableLayout = (TableLayout) findViewById(R.id.board_table);
        createTable(DEFAULT_BOARD_SIZE);

        ignoreNextText = false;

        mClearBoardButton = (Button) findViewById(R.id.clear_puzzle_button);
        mClearBoardButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTexts();
                mBoard.deleteData();
                if (!mFullySolveButton_FC.isEnabled())
                    mFullySolveButton_FC.setEnabled(true);
                if (!mFullySolveButton_BT.isEnabled())
                    mFullySolveButton_BT.setEnabled(true);

            }
        });

        photoButton = (Button) findViewById(R.id.take_photo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            photoButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        genButton = (Button) findViewById(R.id.gen_puzzle_button);
        genButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTexts();
                mBoard.deleteData();
                if (!mFullySolveButton_FC.isEnabled())
                    mFullySolveButton_FC.setEnabled(true);
                if (!mFullySolveButton_BT.isEnabled())
                    mFullySolveButton_BT.setEnabled(true);

                mBoard.genPuzzle();

                TableEntryEditText tempET;
                int size = mBoard.getSize();
                ignoreNextText = true;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        tempET = (TableEntryEditText) findViewById(i * size + j);
                        if (tempET != null && mBoard.getData(i * size + j) != 0) {
                            tempET.setText(String.format("%s", mBoard.getData(i * size + j)));
                            tempET.disableEditability();
                        }
                    }
                }
                ignoreNextText = false;
            }
        });

        mFullySolveButton_FC = (Button) findViewById(R.id.fully_solve_button_fc);
        mFullySolveButton_FC.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoard.solvePuzzleFC()) {
                    TableEntryEditText tempET;
                    int size = mBoard.getSize();
                    ignoreNextText = true;
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            tempET = (TableEntryEditText) findViewById(i * size + j);
                            if (tempET != null) {
                                tempET.setText(String.format("%s", mBoard.getSolvedData(i * size + j)));
                                tempET.disableEditability();
                            }
                        }
                    }
                    ignoreNextText = false;
                    mFullySolveButton_FC.setEnabled(false);
                    mFullySolveButton_BT.setEnabled(false);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Puzzle could not be solved", Toast.LENGTH_SHORT).show();
                }

                //InputMethodManager imm = (InputMethodManager) PuzzleSolver.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                //if (imm.isAcceptingText())
                //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //hide keyboard if its open - VERY BUGGY
            }
        });

        mFullySolveButton_BT = (Button) findViewById(R.id.fully_solve_button_b);
        mFullySolveButton_BT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoard.solvePuzzleBT()) {
                    TableEntryEditText tempET;
                    int size = mBoard.getSize();
                    ignoreNextText = true;
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            tempET = (TableEntryEditText) findViewById(i * size + j);
                            if (tempET != null) {
                                tempET.setText(String.format("%s", mBoard.getSolvedData(i * size + j)));
                                tempET.disableEditability();
                            }
                        }
                    }
                    ignoreNextText = false;
                    mFullySolveButton_BT.setEnabled(false);
                    mFullySolveButton_FC.setEnabled(false);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Puzzle could not be solved", Toast.LENGTH_SHORT).show();
                }

                //InputMethodManager imm = (InputMethodManager) PuzzleSolver.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                //if (imm.isAcceptingText())
                //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); //hide keyboard if its open - VERY BUGGY
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                photoButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputMediaFile();

        uri = FileProvider.getUriForFile(PuzzleSolver.this, BuildConfig.APPLICATION_ID + ".provider",file);
        intentPicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


//        Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + "android.support.FILE_PROVIDER_PATHS", createImageFile());
//
//        intentPicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intentPicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentPicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

//            clearEditTexts();
//            mBoard.deleteData();
//            if (!mFullySolveButton_FC.isEnabled())
//                mFullySolveButton_FC.setEnabled(true);
//            if (!mFullySolveButton_BT.isEnabled())
//                mFullySolveButton_BT.setEnabled(true);
//

            mBoard.imageRec(filename);

//            TableEntryEditText tempET;
//            int size = mBoard.getSize();
//            ignoreNextText = true;
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    tempET = (TableEntryEditText) findViewById(i * size + j);
//                    if (tempET != null && mBoard.getData(i * size + j) != 0) {
//                        tempET.setText(String.format("%s", mBoard.getData(i * size + j)));
//                        tempET.disableEditability();
//                    }
//                }
//            }
//            ignoreNextText = false;
        }
    }


    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SudokuSolverImages");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("SudokuSolverImages", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        filename = "IMG_"+ timeStamp + ".jpg";

        return new File(mediaStorageDir.getPath() + File.separator +
                filename);
    }


    private void clearEditTexts() {
        ignoreNextText = true;
        for (int i = 0; i < mTableLayout.getChildCount(); i++) { //loops through all rows in tableLayout
            TableRow tempTR = (TableRow) mTableLayout.getChildAt(i); //sets a temp TableRow
            for (int j = 0; j < tempTR.getChildCount(); j++) { //loops through all editTexts in current TableRow
                TableEntryEditText et = (TableEntryEditText) tempTR.getChildAt(j);
                et.enableEditability();
                et.setText(""); //clears the editText
                et.setInvalidEntry(false);
            }
        }
        ignoreNextText = false;
    }


    @Override
    protected void onSaveInstanceState(Bundle SavedInstanceState) {
        super.onSaveInstanceState(SavedInstanceState);

        String boardData;
    }

    private void checkEntry(int editTextId) {
        TableEntryEditText tempET;
        int startPos;

        switch (mBoard.checkData(editTextId)) {
            case 1:
                //set col background for elements to red
                for (int i = mBoard.computeX(editTextId); i < mBoard.getSize() * mBoard.getSize(); i+=9) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }
                break;
            case 2:
                //set row background for elements to red
                startPos = mBoard.computeY(editTextId) * mBoard.getSize();
                for (int i = startPos; i < startPos + 9; i++) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }
                break;
            case 3:
                //set box background for elements to red
                startPos = (editTextId - (editTextId % 3)) - ((editTextId / 9) % 3 * 9);
                for (int y = startPos; y < 27 + startPos; y += 9) {
                    for (int loc = y; loc < y + 3; loc++) {
                        tempET = (TableEntryEditText) findViewById(loc);
                        if (tempET != null)
                            tempET.setInvalidEntry(true);
                        //tempET.setBackgroundResource(R.drawable.invalid_cell);
                    }
                }

                break;

            case 4:
                //set col background for elements to red
                for (int i = mBoard.computeX(editTextId); i < mBoard.getSize() * mBoard.getSize(); i+=9) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }

                //set box background for elements to red
                startPos = (editTextId - (editTextId % 3)) - ((editTextId / 9) % 3 * 9);
                for (int y = startPos; y < 27 + startPos; y += 9) {
                    for (int loc = y; loc < y + 3; loc++) {
                        tempET = (TableEntryEditText) findViewById(loc);
                        if (tempET != null)
                            tempET.setInvalidEntry(true);
                        //tempET.setBackgroundResource(R.drawable.invalid_cell);
                    }
                }

                break;

            case 5:
                //set row background for elements to red
                startPos = mBoard.computeY(editTextId) * mBoard.getSize();
                for (int i = startPos; i < startPos + 9; i++) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }

                //set box background for elements to red
                startPos = (editTextId - (editTextId % 3)) - ((editTextId / 9) % 3 * 9);
                for (int y = startPos; y < 27 + startPos; y += 9) {
                    for (int loc = y; loc < y + 3; loc++) {
                        tempET = (TableEntryEditText) findViewById(loc);
                        if (tempET != null)
                            tempET.setInvalidEntry(true);
                        //tempET.setBackgroundResource(R.drawable.invalid_cell);
                    }
                }

                break;

            case 0:
                //remove element from list and set background to normal
                for (int i = mBoard.computeX(editTextId); i < mBoard.getSize() * mBoard.getSize(); i+=9) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(false);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }

                startPos = mBoard.computeY(editTextId) * mBoard.getSize();
                for (int i = startPos; i < startPos + 9; i++) {
                    tempET = (TableEntryEditText) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(false);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }

                startPos = (editTextId - (editTextId % 3)) - ((editTextId / 9) % 3 * 9);
                for (int y = startPos; y < 27 + startPos; y += 9) {
                    for (int loc = y; loc < y + 3; loc++) {
                        tempET = (TableEntryEditText) findViewById(loc);
                        if (tempET != null)
                            tempET.setInvalidEntry(false);
                        //tempET.setBackgroundResource(R.drawable.invalid_cell);
                    }
                }


                break;
        }
    }


    private void createTable(final int size) {

        for (int i = 0; i < size; i++) {

            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams( new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)); //sets width and height
            tableRow.setId(i + 100); //sets id between 100 and 100 + boardSize

            for (int j = 0; j < size; j++) {


                final TableEntryEditText editText = new TableEntryEditText(this);
                editText.setId(i * size + j); //sets id between 0 and 80

                mTableLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        editText.setHeight(mTableLayout.getWidth() / 9);
                    }
                });

                editText.addTextChangedListener(new TextWatcher() { //add custom textwatcher class that accesses edittext parent and such and changes background id's of all edititexts
                    //will need a way to check if certain set of edittexts background has been set and change it if changes

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(ignoreNextText)
                            return;

                        if (editText.length() > 1) {
                            String newInput = s.toString().substring(s.length() - 1); //grabs the last value entered
                            editText.setText(newInput); //sets the text to the newInput
                            editText.setSelection(editText.length()); //sets cursor to end of editText
                            checkEntry(editText.getId());
                        }
                        else if (editText.length() == 1 && s.length() == 1) {
                            mBoard.setData(editText.getId(), Integer.parseInt(s.toString())); //sets board data when something entered
                            checkEntry(editText.getId());
                        }
                        else if (count == 0 && editText.length() == 0) {
                            int id = editText.getId();
                            mBoard.deleteSingleData(id);
                            checkEntry(id);
                            Log.v("onTextChanged", "deleting data at index " + editText.getId());
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                tableRow.addView(editText);
            }
            mTableLayout.addView(tableRow); //add the row to the table
        }
    }
}