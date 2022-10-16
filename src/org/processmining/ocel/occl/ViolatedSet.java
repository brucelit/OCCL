package org.processmining.ocel.occl;

import java.util.ArrayList;

public class ViolatedSet {

    public ArrayList<ArrayList> violatedRules = new ArrayList<ArrayList>();

    public ViolatedSet(){

    }

    public void appendViolatedRule(String eventId,
                                   String activity,
                                   String refObj,
                                   String reason){
        ArrayList violatedRule = new ArrayList<>();
        violatedRule.add(eventId);
        violatedRule.add(activity);
        violatedRule.add(refObj);
        violatedRule.add(reason);
        violatedRules.add(violatedRule);
    }

}
