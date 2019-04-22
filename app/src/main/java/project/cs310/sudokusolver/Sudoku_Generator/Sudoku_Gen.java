package project.cs310.sudokusolver.Sudoku_Generator;

import project.cs310.sudokusolver.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sudoku_Gen {
    private static final int RANDOM = 0;
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int TRICKY = 3;
    private static final int FIENDISH = 4;
    private static final int DIABOLICAL = 5;


    public static boolean checker(int[][] bs, int i, ArrayList<Integer> ar) {
        ArrayList<Integer> temp_ar = new ArrayList<Integer>();
        boolean check1 = true;          //For returning true for good Sudoku
        for(int e=0;e<9;e++){
            bs[i][e] = ar.get(e);
        }
        for(int t=0;t<9;t++){
            for(int e=0;e<=i;e++){

                temp_ar.add(e, bs[e][t]);
            }
            Set<Integer> temp_set = new HashSet<Integer>(temp_ar);
            if(temp_set.size()<temp_ar.size()){
                check1 = false; break;                                      //Fuck OFF
            }
            else {
                temp_ar.clear();
                temp_set.clear();
            }
        }
        return check1;
    }

    public static boolean checkPath(int[][] bs, int i) {
        //boolean check_cP = false;
        ArrayList<Integer> temp_cP = new ArrayList<>();
        Set<Integer> temp_setcP;
        boolean denoter = true;
        while(i==3){
            for(int k =0;k<i;k++ ){
                for(int e=0;e<3;e++){
                    temp_cP.add(e, bs[k][e]);
                }
            }
            temp_setcP = new HashSet<>(temp_cP);
            if(temp_cP.size()>temp_setcP.size()){
                denoter = false;break;

            }
            else {
                temp_cP.clear();
                temp_setcP.clear();

                for(int k =0;k<i;k++ ){
                    for(int e=3;e<6;e++){
                        temp_cP.add(bs[k][e]);
                    }
                }
                temp_setcP = new HashSet<Integer>(temp_cP);
                if(temp_cP.size()>temp_setcP.size()){
                    denoter = false;break;

                }
                else {
                    break;
                }
            }
        }
        while(i==6){

            for(int k =3;k<i;k++ ){
                for(int e=0;e<3;e++){
                    temp_cP.add(e, bs[k][e]);
                }
            }
            temp_setcP = new HashSet<Integer>(temp_cP);
            if(temp_cP.size()>temp_setcP.size()){
                denoter = false;break;

            }
            else {
                temp_cP.clear();
                temp_setcP.clear();

                for(int k =3;k<i;k++ ){
                    for(int e=3;e<6;e++){
                        temp_cP.add(bs[k][e]);
                    }
                }
                temp_setcP = new HashSet<Integer>(temp_cP);
                if(temp_cP.size()>temp_setcP.size()){
                    denoter = false;break;

                }
                else {
                    break;
                }
            }

        }
        return denoter;
    }

    public static int[][] partialSudoGen(int difficulty) {
        int[][] partialSolution = sudoGen();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (Math.random() < 0.66 ) {
                    partialSolution[r][c] = 0;
                }
            }
        }

        return partialSolution;
    }

    public static int[][] partialSudoGen() {
        int[][] partialSolution = sudoGen();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (Math.random() < 0.66 ) {
                    partialSolution[r][c] = 0;
                }
            }
        }

        return partialSolution;
    }

    public static int[][] sudoGen() {
        Board board = new Board(9);
        ArrayList<Integer> values = new ArrayList<>();

        for(int i=1;i<10;i++){
            values.add(i);
        }
        Collections.shuffle(values);

        for(int i=0;i<1;i++){
            board.setData(i,0,values.get(i));
        }
        board.solvePuzzleFC();
        return board.getSolvedData();
    }



    public static int[][] getSudoku() {
        return partialSudoGen();
    }

    public static int[][] genEasySudoku() {
        return partialSudoGen(EASY);
    }

    public static int[][] genMediumSudoku() {
        return partialSudoGen(MEDIUM);
    }

    public static int[][] genTrickySudoku() {
        return partialSudoGen(TRICKY);
    }

    public static int[][] genFiendishSudoku() {
        return partialSudoGen(FIENDISH);
    }

    public static int[][] genDiabolicalSudoku() {
        return partialSudoGen(DIABOLICAL);
    }


}
