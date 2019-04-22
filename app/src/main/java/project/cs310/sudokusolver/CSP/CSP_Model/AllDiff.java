package project.cs310.sudokusolver.CSP.CSP_Model;

// import java.util.Collections;
// import java.util.Set;
import java.util.*;

public class AllDiff implements Constraint {

    //QUESTION: IS THIS THE RIGHT PLACE TO PYUT THE NAKED TRIPLES ETC OR NO? IS THAT IN CONSTRAINT PROP?
    //problem how to check if only the ids

    private final HashSet<String> varIds;

    public AllDiff(HashSet<String> varIds) {
        this.varIds = varIds;
    }

    public AllDiff() {
        this.varIds = new HashSet<>();
    }

    @Override
    public void addConstrainedVar(String varId) {
        this.varIds.add(varId);
    }


    //unmodifiableSet
    @Override
    public HashSet<String> getConstrainedVariables() {
        return varIds;
    }

    @Override
    public ArrayList<String> getUnassignedConstrainedVars(CSPAssignment state) {
        ArrayList<String> resultingVars = new ArrayList<>(state.getUnassignedVars());
        resultingVars.retainAll(varIds);
        return resultingVars;
    }

    @Override
    public ArrayList<String> getUnassignedConstrainedVars(ArrayList<String> unassignedVars) {
        ArrayList<String> resultingVars = new ArrayList<>(unassignedVars);
        resultingVars.retainAll(varIds);
        return resultingVars;
    }

    //check that there is no double value (eg 4 twice)
    public boolean noDuplicates(HashMap<String, VariableData> variables) {
        HashSet<Integer> checkSet = new HashSet<Integer>();
        for (String var : varIds) {
            VariableData data = variables.get(var);
            if (null != data.getValue()) {
                if (!checkSet.add(data.getValue())) return false;
            }
        }
        return true;
    }

    //use override whenever overriding abstract interface methods
    @Override
    public boolean isConsistent(HashMap<String, VariableData> variables) {
        return noDuplicates(variables);
    }
}
