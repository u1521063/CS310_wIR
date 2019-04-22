package project.cs310.sudokusolver.CSP.CSP_Model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
/*
Variable of the CSP takes part in one or multiple Constraints. A Constraint involves some subset of the variables of a
CSP and specifies the allowable combinations of values for that subset. The Constraint class provides the means to determine
whether a constraint is consistent and satisfied given the set of dependent variables.

The Constraint class provides the means to determine whether a constraint is consistent and satisfied given the set of dependent variables.
*/


//the constraint interface is very simple and simply allows for consistency checks etc
//might have to remove getConstrainedVariables() = eg what if a complicated constraint that actually has mult sets getConstraints - leave for now

public interface Constraint {

    /*TO DO MAKE IN INTELLIJ SO CAN MAKE JAVADOCS*/

    /* Determine is the constraint is consistent with the given state of the set of variables
     */
    boolean isConsistent(HashMap<String, VariableData> variables);

    ArrayList<String> getUnassignedConstrainedVars(ArrayList<String> unassignedVars);

    ArrayList<String> getUnassignedConstrainedVars(CSPAssignment state);

    /* Returns the set of variables affected by the constraint (as not all will be)
     */
    HashSet<String> getConstrainedVariables();

    void addConstrainedVar(String varId);
}
