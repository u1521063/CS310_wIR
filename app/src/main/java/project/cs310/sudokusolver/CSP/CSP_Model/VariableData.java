package project.cs310.sudokusolver.CSP.CSP_Model;

// import java.util.HashSet;
import java.util.*;

public class VariableData {
    //variables will be stored in a hashmap
    //as such do not need an id. the key will be the id

    private HashSet<Integer> domain;
    private Integer assignedValue;

    //are all of these needed? can any be removed?
    //also might want to add null checks if null is okay

    public VariableData(HashSet<Integer> domain, Integer assignedValue) {
        this.domain = domain;
        this.assignedValue = assignedValue;
    }

    public VariableData(VariableData variableData) {
        this.domain = variableData.getDomain();
        this.assignedValue = variableData.getValue();
    }


    public VariableData(Integer assignedValue) {
        this.assignedValue = assignedValue;
        this.domain = new HashSet<>();
    }

    public VariableData(HashSet<Integer> domain) {
        this.domain = domain;
        this.assignedValue = null;
    }

    public Integer getValue() {
        return assignedValue;
    }

    public HashSet<Integer> getDomain() {
        return domain;
    }

    public int getDomainSize() {
        return domain.size();
    }


    public void updateDomain(HashSet<Integer> domain) {
        this.domain = domain;
    }

    public void updateDomain(Integer newDomain) {
        this.domain = new HashSet<>();
        domain.add(newDomain);
    }

    public void restrictDomain(ArrayList<Integer> toRemove) {
        domain.removeAll(toRemove);
    }

    public void addToDomain(ArrayList<Integer> toAdd) {
        domain.addAll(toAdd);
    }

    public void restrictDomain(Integer toRemove) {
        domain.remove(toRemove);
    }

    public void addToDomain(Integer toAdd) {
        domain.add(toAdd);
    }

    public void assignValue(Integer assignedValue) {
        this.assignedValue = assignedValue;
    }

    public void unassignValue() {
        //ehh is null a good thing seems a bit messy
        this.assignedValue = null;
    }

    //restrict variable domain again?? or should that be elsewhere?
}
