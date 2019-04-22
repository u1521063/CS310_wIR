package project.cs310.sudokusolver.CSP.CSP_Solver;

import project.cs310.sudokusolver.CSP.CSP_Model.*;
import java.util.*;

public class Backtracker {
    private CSPAssignment state;

    public Backtracker(CSPAssignment state) {
        this.state = state;
    }

    private String pickVariable() {
        return state.getUnassignedVar();
    }

    private String MD(ArrayList<String> Ids) {
        int curMaxDegree = -100;
        String maxId = "";
        for (String varId: Ids) {
            int degree = state.getVarDegree(varId);
            if (degree > curMaxDegree) {
                curMaxDegree = degree;
                maxId = varId;
            }
        }
        return maxId;
    }

    private String MRV() {
        int curMinDomain = 100;
        ArrayList<String> curMaxIds = new ArrayList<>();
        for (String var: state.getUnassignedVars()) {
            int size = state.getVariables().get(var).getDomainSize();
            if (curMinDomain > size) {
                curMinDomain = size;
                curMaxIds.clear();
                curMaxIds.add(var);
            } else if (curMinDomain == size) {
                curMaxIds.add(var);
            }
        }

        if (curMaxIds.size() > 1) {
            return MD(curMaxIds);
        } else if (curMaxIds.size() == 0) {
            //ISSUE? WE'RE DONE??? HMM HOW TO HANDLE
            return "";
        } else {
            return curMaxIds.get(0);
        }
    }

    private boolean simpleBacktrackingMRV() {
        if (state.isComplete())
            return true;
        String varId = MRV();
        VariableData varData = state.getVariableData(varId);
        for(Integer val : varData.getDomain()){
            varData.assignValue(val);
            state.getVariables().put(varId, varData);
            state.removeAssignedVar(varId);
            if (state.isConsistent()) {
                if (simpleBacktrackingMRV()) {
                    return true;
                }
            }
            varData.unassignValue();
            state.addUnassignedVar(varId);
            state.getVariables().put(varId, varData);
        }
        return false;
    }

    private boolean simpleBacktrackingFC_MRV() {
        if (state.isComplete())
            return true;
        String varId = MRV();
        VariableData varData = state.getVariableData(varId);
        for(Integer val : varData.getDomain()){
            varData.assignValue(val);
            state.getVariables().put(varId, varData);
            state.removeAssignedVar(varId);
            if (state.isConsistent() && ForwardCheck(varId, val) && simpleBacktrackingFC_MRV()) {
                return true;
            }
            varData.unassignValue();
            state.addUnassignedVar(varId);
            state.getVariables().put(varId, varData);
            restoreDomain(varId, val);
        }
        return false;
    }



    private boolean simpleBacktrackingFC() {
        if (state.isComplete())
            return true;
        String varId = pickVariable();
        VariableData varData = state.getVariableData(varId);
        for(Integer val : varData.getDomain()){
            varData.assignValue(val);
            state.getVariables().put(varId, varData);
            state.removeAssignedVar(varId);
            if (state.isConsistent() && ForwardCheck(varId, val) && simpleBacktrackingFC()) {
                return true;
            }
            varData.unassignValue();
            state.addUnassignedVar(varId);
            state.getVariables().put(varId, varData);
            restoreDomain(varId, val);
        }
        return false;
    }

    private boolean simpleBacktracking() {
        if (state.isComplete())
            return true;
        String varId = pickVariable();
        VariableData varData = state.getVariableData(varId);
        for(Integer val : varData.getDomain()){
            varData.assignValue(val);
            state.getVariables().put(varId, varData);
            state.removeAssignedVar(varId);
            if (state.isConsistent()) {
                if (simpleBacktracking()) {
                    return true;
                }
            }
            varData.unassignValue();
            state.addUnassignedVar(varId);
            state.getVariables().put(varId, varData);
        }
        return false;
    }


    public CSPAssignment solveFC() {
        if (InitialPrune_FC()) {
            if (simpleBacktrackingFC_MRV()) {
                return state;
            }
        } else {
            System.out.println("initial prune failed");
        }
        return null; //seems like bad practise - imp diff
    }

    public CSPAssignment solveBT() {
        if (InitialPrune_FC()) {
            if (simpleBacktrackingMRV()) {
                return state;
            }
        } else {
            System.out.println("initial prune failed");
        }
        return null; //seems like bad practise - imp diff
    }

    private boolean ForwardCheck(String assignedId, Integer value) {
        for (String neighbourId: state.getVarsAffected(assignedId)) {
            VariableData neighbourData =  new VariableData(state.getVariables().get(neighbourId));
            neighbourData.restrictDomain(value);
            if (neighbourData.getDomainSize() == 0) {
                return false;
            }
        }
        return true;
    }

    private void restoreDomain(String assignedId, Integer value) {
        for (String neighbourId: state.getVarsAffected(assignedId)) {
            state.getVariables().get(neighbourId).addToDomain(value);
        }
    }


    private boolean InitialPrune_FC() {
        HashSet<String> assignedVars = new HashSet<>(state.getVariables().keySet());
        assignedVars.removeAll(state.getUnassignedVars());
        for (String assignedId : assignedVars) {
            if (!ForwardCheck(assignedId, state.getVariables().get(assignedId).getValue())) return false;
        }
        return true;
    }


    //TODO: Consider newly assigned items
    private boolean InitialPrune() {
        HashSet<String> assignedVars = new HashSet<>(state.getVariables().keySet());
        assignedVars.removeAll(state.getUnassignedVars());
//    HashSet<String> newAssigned = new HashSet<>();
        for (String assignedId : assignedVars) {
            for (String neighbourId: state.getVarsAffected(assignedId)) {
                if (!assignedVars.contains(neighbourId)) {
                    VariableData neighbourData = state.getVariableData(neighbourId);
                    neighbourData.restrictDomain(state.getVariableData(assignedId).getValue());
//          if (neighbourData.getDomainSize() == 1) {
//            neighbourData.assignValue(neighbourData.getDomain().get(0));
//            state.getVariables().put(neighbourId, neighbourData);
//            state.removeAssignedVar(neighbourId);
//
//            //maybe need to forward check
//
//
//            newAssigned.add(neighbourId);
//            System.out.println(neighbourId);
//            System.out.println("thign");
//            System.out.println(state.getUnassignedVars().size());
//
//
                    if (neighbourData.getDomainSize() == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //all values not just one? so maybe wrong.
    private void pruneNeighbourDomains(String varId, Integer val) {
        for (String neighbour: state.getVarsAffected(varId)) {
            state.getVariableData(neighbour).restrictDomain(val);
        }
    }

}
