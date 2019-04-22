package project.cs310.sudokusolver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import project.cs310.sudokusolver.CSP.CSP_Model.CSPAssignment;
import project.cs310.sudokusolver.CSP.CSP_Model.SudokuAssignment;
import project.cs310.sudokusolver.CSP.CSP_Model.VariableData;
import project.cs310.sudokusolver.CSP.CSP_Solver.CSPSolver;
//import project.cs310.sudokusolver.Image_Rec.DigitExtractor;
import project.cs310.sudokusolver.Sudoku_Generator.Sudoku_Gen;

public class Board {

    private UUID mId;
    private int size;
    private int data[][];
    private int solvedData[][];
    private boolean hasBeenSolved;
    private boolean solvable;
    private ArrayList<Coordinate> entries;
    private ArrayList<Coordinate> invalidEntries;

    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int x_loc, int y_loc) {
            x = x_loc;
            y = y_loc;
        }
    }

    public Board(int size) {
        this(UUID.randomUUID(), size);
    }

    public Board(UUID id, int size) {
        mId = id;
        this.size = size;
        data = new int[size][size];
        solvedData = new int[size][size];
        hasBeenSolved = false;
        solvable = true;
        entries = new ArrayList<>();
        invalidEntries = new ArrayList<>();
    }

    public int[][] getSolvedData() {
        return solvedData;
    }

    public void setData(int x, int y, int value) {
        data[x][y] = value;


        boolean duplicateFound = false;
        for (Coordinate c : entries) {
            if (c.x == x && c.y == y) {
                duplicateFound = true;
                break;
            }
        }

        if (!duplicateFound)
            entries.add(new Coordinate(x, y));

        Log.v("Board-checkEntry", "value " + value + " entered at index [" + x + "," + y + "]");
    }


    public void setData(int index, int value) {
        int x = index % size;
        int y = index / size;

        data[x][y] = value;


        boolean duplicateFound = false;
        for (Coordinate c : entries) {
            if (c.x == x && c.y == y) {
                duplicateFound = true;
                break;
            }
        }

        if (!duplicateFound)
            entries.add(new Coordinate(x, y));

        Log.v("Board-checkEntry", "value " + value + " entered at index [" + x + "," + y + "]");
    }

    public void deleteSingleData(int index) {
        int x = index % size;
        int y = index / size;
        int valueToDelete = data[x][y];

        for (int i = entries.size()-1; i >= 0; i--) {
            if (entries.get(i).x == x && entries.get(i).y == y) {
                Log.v("Board-deleteData", "removed " + data[x][y] + " at [" + x + "," + y + "]   entry size: " + entries.size());
                entries.remove(i);
                break;
            }
        }

        for (int j = invalidEntries.size()-1; j >= 0; j--) {
            if (invalidEntries.get(j).x == x && invalidEntries.get(j).y == y) {
                invalidEntries.remove(j);
                break;
            }
        }

        data[x][y] = 0;

        //checkData(x, y, valueToDelete);

        hasBeenSolved = false;
    }

    public void imageRec(String absPath) {
        try {
//            DigitExtractor.imageRec(absPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void genPuzzle() {
        data = Sudoku_Gen.getSudoku();
    }

    public boolean solvePuzzleFC() {
        CSPSolver solver = new CSPSolver(createSudokuAssignment());
        return isSolved(solver.solveFC());
    }

    public boolean solvePuzzleBT() {
        CSPSolver solver = new CSPSolver(createSudokuAssignment());
        return isSolved(solver.solveBT());
    }

    public SudokuAssignment createSudokuAssignment() {
        for (int i = 0; i < size; i++)
            System.arraycopy(data[i], 0, solvedData[i], 0, size);

        //EDITS//

        ArrayList<String> unassignedVars = new ArrayList<>();
        HashMap<String, VariableData> variableMap = new HashMap<>();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                String key = r+""+c; //messy?
                Integer value = solvedData[r][c];
                HashSet<Integer> domain = new HashSet<Integer>();
                if (solvedData[r][c] == 0) {
                    unassignedVars.add(key);
                    value = null;
                    Collections.addAll(domain, 1, 2, 3, 4, 5, 6, 7, 8, 9);
                } else {
                    domain.add(value);
                }
                VariableData varData = new VariableData(domain, value);
                variableMap.put(key, varData);
            }
        }


        return new SudokuAssignment(variableMap, unassignedVars);
    }

    public boolean isSolved(CSPAssignment solved) {
        if(solvable && null != solved && solved.isSolved()) { //recursive method that solves puzzle in solvedData
            hasBeenSolved = true;

            if (null != solved && solved.isSolved()) {
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        String key = r + "" + c;
                        solvedData[r][c] = solved.getVariables().get(key).getValue();
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    //checks that value entered at index has no repeats in its column, row, or group
    public int checkData(int index) {
        int x = computeX(index);
        int y = computeY(index);
        int value = data[x][y];

        if (value != 0) {
            if (colDuplicate(x, value, data)) {
                duplicateFound(x, y);
                if (boxDuplicate(x, y, value, data)) {
                    duplicateFound(x, y);
                    return 4;
                }
                return 1;
            }

            if (rowDuplicate(y, value, data)) {
                duplicateFound(x, y);
                if (boxDuplicate(x, y, value, data)) {
                    duplicateFound(x, y);
                    return 5;
                }
                return 2;
            }

            if (boxDuplicate(x, y, value, data)) {
                duplicateFound(x, y);
                return 3;
            }
        }

        solvable = true; //probably not right, should check whether invalid entries is empty or just when something is deleted
        // if all row,col,and box duplicates return false for the deleted
        //then remove it from the invalidentries, if invalid entries is 0 then it is solvable
        return 0;
    }

    private void duplicateFound(int x, int y) {
        solvable = false;
        invalidEntries.add(new Coordinate(x, y));
        Log.v("board-checkData", "invalid entry added for " + x + "," + y + " and size is " + entries.size());
    }

    private boolean rowDuplicate(int y, int num, int[][] array) {
        boolean duplicateCounter = false;
        for (int x = 0; x < size; x++) {
            if (array[x][y] == num) {
                if (duplicateCounter) {
                    Log.e("DuplicateChecking", "row duplicate found!");
                    return true;
                }
                duplicateCounter = true;
            }
        }
        return false;
    }

    private boolean colDuplicate(int x, int num, int[][] array) {
        boolean duplicateCounter = false;
        for (int y = 0; y < size; y++) {
            if (array[x][y] == num) {
                if (duplicateCounter) {
                    Log.e("DuplicateChecking", "col duplicate found!");
                    return true;
                }
                duplicateCounter = true;
            }
        }
        return false;
    }

    private boolean boxDuplicate(int start_x, int start_y, int num, int[][] array) {
        start_x -= start_x % 3;
        start_y -= start_y % 3;
        boolean duplicateCounter = false;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (array[x + start_x][y + start_y] == num) {
                    if (duplicateCounter) {
                        int xx = start_x + x;
                        int yy = start_y + y;
                        Log.e("DuplicateChecking", "box duplicate found at " + xx + "," + yy);
                        return true;
                    }
                    duplicateCounter = true;
                }
            }
        }
        return false;
    }



    public void deleteData() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                data[i][j] = 0;
            }
        }
        solvable = true;
    }

    public int computeX(int index) {
        return index % size;
    }

    public int computeY(int index) {
        return index / size;
    }

    public int getData(int index) {
        return data[index % size][index / size];
    }

    public int getSolvedData(int index) {
        return solvedData[index % size][index / size];
    }

    public int getSize() {
        return size;
    }

    public UUID getId() {
        return mId;
    }

    public boolean getSolvedStatus() {
        return hasBeenSolved;
    }
}

//references: http://programmers.stackexchange.com/questions/212808/treating-a-1d-data-structure-as-2d-grid
//              http://www.geeksforgeeks.org/backtracking-set-7-suduku/
