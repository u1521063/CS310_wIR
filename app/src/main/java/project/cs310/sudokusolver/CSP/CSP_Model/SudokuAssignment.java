package project.cs310.sudokusolver.CSP.CSP_Model;

import java.util.*;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.HashMap;

public class SudokuAssignment extends CSPAssignment {
    private static int boardSize;
    private HashSet<Constraint> allDiffConstraints;

    public void addConstraint(String key, Constraint constraint) {
        if (constraintMap.containsKey(key)) {
            ArrayList<Constraint> constraintList = constraintMap.get(key);
            constraintList.add(constraint);
            constraintMap.put(key, constraintList);
        } else {
            constraintMap.put(key, new ArrayList<Constraint>(Arrays.asList(constraint)));
        }
    }

    public HashSet<Constraint> constraints() {
        allDiffConstraints = new HashSet<>();
        allDiffConstraints.addAll(constraintsOnRows());
        allDiffConstraints.addAll(constraintsOnColumns());
        allDiffConstraints.addAll(constraintsOnGrids());
        return allDiffConstraints;
    }

    private HashSet<Constraint> constraintsOnRows() {
        HashSet<Constraint> rowConstraints = new HashSet<>();
        for (int row = 0; row < boardSize; row++) {
            Constraint  constraint = new AllDiff();
            for (int col = 0; col < boardSize; col++) {
                String key = row+""+col;
                constraint.addConstrainedVar(key);
                addConstraint(key, constraint);
            }
            rowConstraints.add(constraint);
        }
        return rowConstraints;
    }

    private HashSet<Constraint> constraintsOnColumns() {
        HashSet<Constraint> colConstraints = new HashSet<>();
        for (int col = 0; col < boardSize; col++) {
            Constraint  constraint = new AllDiff();
            for (int row = 0; row < boardSize; row++) {
                String key = row+""+col;
                constraint.addConstrainedVar(key);
                addConstraint(key, constraint);
            }

            colConstraints.add(constraint);
        }
        return colConstraints;
    }

    private Set<Constraint> constraintsOnGrids() {
        HashSet<Constraint> gridConstraints = new HashSet<>();
        for (int gridRow = 0; gridRow < 3; gridRow++) {
            for (int gridCol = 0; gridCol < 3; gridCol++) {
                Constraint  constraint = new AllDiff();
                for (int rowIndex = gridRow * 3; rowIndex < gridRow * 3 + 3; rowIndex++) {
                    for (int colIndex = gridCol * 3; colIndex < gridCol * 3 + 3; colIndex++) {
                        String key = rowIndex+""+colIndex;
                        constraint.addConstrainedVar(key);
                        addConstraint(key, constraint);
                    }
                }
                gridConstraints.add(constraint);
            }
        }
        return gridConstraints;
    }


    public SudokuAssignment(int boardSize, HashMap<String, VariableData> variables, ArrayList<String> unassignedVars) {
        this.boardSize = boardSize;
        this.variables = variables;
        this.unassignedVars = unassignedVars;
        this.constraintMap = new HashMap<>();
        this.constraints = constraints();
    }


    public SudokuAssignment(HashMap<String, VariableData> variables, ArrayList<String> unassignedVars) {
        this.boardSize = 9;
        this.variables = variables;
        this.unassignedVars = unassignedVars;
        this.constraintMap = new HashMap<>();
        this.constraints = constraints();
    }
}
