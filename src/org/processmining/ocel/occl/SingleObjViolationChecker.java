package org.processmining.ocel.occl;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;

import java.util.Map;
import java.util.Set;

public class SingleObjViolationChecker {

    private OcelEventLog ocel;
    private String refObj;
    private String refAct;
    private Integer objNum;
    private Integer objMatchNum;
    private String objCount;
    private String objCountRule="";
    public ViolatedSet vs = new ViolatedSet();
    public String reason = "";

    public SingleObjViolationChecker() {
    }

    public SingleObjViolationChecker(OcelEventLog ocel,
                                     String refObj,
                                     Integer objNum,
                                     String objCountRule,
                                     String objCount) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.objNum = objNum;
        this.objCountRule = objCountRule;
        this.objCount = objCount;
    }

    public SingleObjViolationChecker(OcelEventLog ocel,
                                     String refObj,
                                     Integer objNum,
                                     String refAct,
                                     String objCountRule,
                                     String objCount) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.objNum = objNum;
        this.refAct = refAct;
        this.objCountRule = objCountRule;
        this.objCount = objCount;
    }


    public ViolatedSet checkObjCount(UIPluginContext context) {
        switch (objCountRule) {
            case "ObjectCountWithoutRefAct":
                return getObjCountWithoutRefAct(context);
            case "ObjectCountWithRefAct":
                return getObjCountWithRefAct(context);
            default:
                return null;
        }
    }

    private ViolatedSet getObjCountWithRefAct(UIPluginContext context) {
        switch (objCount){
            case "smallerThan":
                vs = getSmallerThanObjCount(context, refAct);
            case "smallerOrEqualTo":
                vs = getSmallerOrEqualToObjCount(context, refAct);
            case "equalTo":
                vs = getEqualToObjCount(context,refAct);
            case "greaterThan":
                vs = getGreaterThanObjCount(context,refAct);
            case "greaterOrEqualTo":
                vs = getGreaterOrEqualToObjCount(context,refAct);
        }
        return vs;
    }

    public ViolatedSet getObjCountWithoutRefAct(UIPluginContext context) {
        switch (objCount){
            case "smallerThan":
                vs = getSmallerThanObjCount(context);
            case "smallerOrEqualTo":
                vs = getSmallerOrEqualToObjCount(context);
            case "equalTo":
                vs = getEqualToObjCount(context);
            case "greaterThan":
                vs = getGreaterThanObjCount(context);
            case "greaterOrEqualTo":
                vs = getGreaterOrEqualToObjCount(context);
        }
        return vs;
    }

    public ViolatedSet getSmallerThanObjCount(UIPluginContext context, String refAct) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum >= objNum){

                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ "(greater than "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }
        }
        return vs;
    }

    public ViolatedSet getSmallerOrEqualToObjCount(UIPluginContext context, String refAct) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum > objNum){

                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ " (greater than "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }
        }
        return vs;
    }

    public ViolatedSet getEqualToObjCount(UIPluginContext context, String refAct) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum != objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ "(not equal to "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }
        }
        return vs;
    }

    public ViolatedSet getGreaterThanObjCount(UIPluginContext context, String refAct) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum <= objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ " (smaller than "+objNum+")";
                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }
        }
        return vs;
    }

    public ViolatedSet getGreaterOrEqualToObjCount(UIPluginContext context, String refAct) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        String reason = "The actual number of object for type "+ refObj + " is smaller than setting.";
        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum < objNum){
                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }
        }
        return vs;
    }

    public ViolatedSet getSmallerThanObjCount(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum >= objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ " (greater than "+objNum+")";
                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }
        }
        return vs;
    }

    public ViolatedSet getSmallerOrEqualToObjCount(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum > objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ " (greater than "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);

            }
        }
        return vs;
    }

    public ViolatedSet getEqualToObjCount(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        String reason = "The actual number of object for type "+ refObj + " is not equal to the setting.";
        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum != objNum){
                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }

        return vs;
    }

    public ViolatedSet getGreaterThanObjCount(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum <= objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ "( smaller than "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }

        return vs;
    }

    public ViolatedSet getGreaterOrEqualToObjCount(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
                objMatchNum = 0;
                // get the set of the objects
                Set<OcelObject> objSet = set.getValue().relatedObjects;

                // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {
                    if (eachObj.objectType.name.equals(refObj)){
                        objMatchNum += 1;
                    }
                }

                // see if the matching number is greater or equal to the target number
                if (objMatchNum < objNum){
                    reason = "The actual number of object for type "+ refObj + " is " +objMatchNum+ " (smaller than "+objNum+")";

                    vs.appendViolatedRule(set.getKey(), refAct, refObj, reason);
                }

            }

        return vs;
    }
}