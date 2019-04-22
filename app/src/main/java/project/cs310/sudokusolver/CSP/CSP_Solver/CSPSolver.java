package project.cs310.sudokusolver.CSP.CSP_Solver;

import project.cs310.sudokusolver.CSP.CSP_Model.CSPAssignment;

public class CSPSolver {
    private CSPAssignment state;
    // private ConstraintPropagator propagator;
    private Backtracker backtrackor;

    public CSPSolver(CSPAssignment state) {
        this.state = state;
        //do I want to read the state in? is that a good idea? think about it
        // this.propagator = new ConstraintPropagator(state);
        this.backtrackor = new Backtracker(state);

    }


    public CSPAssignment solveBT() {
        state = backtrackor.solveBT();
        if (null != state && state.isSolved()) {
            return state;
        } else {
            return null;
        }
    }

    public CSPAssignment solveFC() {
        state = backtrackor.solveFC();
        if (null != state && state.isSolved()) {
            return state;
        } else {
            return null;
        }
    }
}