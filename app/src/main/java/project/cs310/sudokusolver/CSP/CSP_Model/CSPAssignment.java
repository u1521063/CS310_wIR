package project.cs310.sudokusolver.CSP.CSP_Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

abstract public class CSPAssignment {
    protected HashMap<String, VariableData> variables; //with domain for each variable and value in VariableData
    protected HashSet<Constraint> constraints;
    protected HashMap<String, ArrayList<Constraint>> constraintMap;
    protected ArrayList<String> unassignedVars;

    public VariableData getVariableData(String id) {
        return variables.get(id); //returns collection not vardata so?
    }

    public ArrayList<String> getUnassignedVars() {
        return unassignedVars;
    }


    public void setVariables(HashMap<String, VariableData> variables) {
        this.variables = variables;
    }

    public HashSet<String> getVarsAffected(String varId) {
        HashSet<String> affectedVars = new HashSet<String>();
        for (Constraint c : constraintMap.get(varId)) {
            affectedVars.addAll(c.getUnassignedConstrainedVars(unassignedVars));
        }
        affectedVars.remove(varId);
        return affectedVars;

    }

    public int getVarDegree(String varId) {
        return getVarsAffected(varId).size();
    }

    public String getUnassignedVar() {
        return unassignedVars.get(0);
    }

    public void removeAssignedVar(String varId) {
        unassignedVars.remove(varId);
    }

    public void addUnassignedVar(String varId) {
        if (!unassignedVars.contains(varId)) {
            unassignedVars.add(varId);
        }
    }


    public HashSet<Constraint> getConstraints() {
        return constraints;
    }

    public HashMap<String, VariableData> getVariables() {
        return variables;
    }


    //all vars assigned
    public boolean isComplete() {
        return unassignedVars.isEmpty();
    }

    //An assignment that does not violate any constraint
    public boolean isConsistent() {
        for (Constraint c : constraints) {
            if (!c.isConsistent(variables)) {
                return false;
            }
        }
        return true;
    }

    //consistent and complete
    public boolean isSolved() {
        if (null == variables) {
            return false;
        } else {
            if (isConsistent() && isComplete()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
