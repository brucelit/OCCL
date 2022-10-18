package org.processmining.ocel.occl;

import jnr.ffi.annotations.In;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;

import java.util.*;

public class ObjToObjStaticChecker {

    private XLog firstLog;
    private XLog secondLog;

    private OcelEventLog ocel;
    private String firstRefObj;
    private String secondRefObj;
    private String relationType;
    private String refAct = "";

    private Integer firstRefObjNum;

    private Integer secondRefObjNum;


    public ViolatedSet vs = new ViolatedSet();
    public String reason;

    public  XTrace tempTrace;

    public Integer firstRefObjNumInEvt;
    public Integer secondRefObjNumInEvt;

    public ObjToObjStaticChecker() {

    }

    // for not-coexist
    public ObjToObjStaticChecker(OcelEventLog ocel,
                                 String firstRefObj,
                                 String secondRefObj,
                                 String relationType) {
        this.ocel = ocel;
        this.firstRefObj = firstRefObj;
        this.secondRefObj = secondRefObj;
        this.relationType = relationType;
    }

    // for co-birth / co-death
    public ObjToObjStaticChecker(XLog firstLog,
                                 XLog secondLog,
                                 OcelEventLog ocel,
                                 String firstRefObj,
                                 String secondRefObj,
                                 String relationType) {
        this.firstLog = firstLog;
        this.secondLog = secondLog;
        this.ocel = ocel;
        this.firstRefObj = firstRefObj;
        this.secondRefObj = secondRefObj;
        this.relationType = relationType;
    }

    // for coexist without refact
    public ObjToObjStaticChecker(OcelEventLog ocel,
                                 String firstRefObj,
                                 String secondRefObj,
                                 Integer firstRefObjNum,
                                 Integer secondRefObjNum,
                                 String relationType) {
        this.ocel = ocel;
        this.firstRefObj = firstRefObj;
        this.secondRefObj = secondRefObj;
        this.firstRefObjNum = firstRefObjNum;
        this.secondRefObjNum = secondRefObjNum;
        this.relationType = relationType;
    }

    // for coexist with refact
    public ObjToObjStaticChecker(OcelEventLog ocel,
                                 String firstRefObj,
                                 String secondRefObj,
                                 Integer firstRefObjNum,
                                 Integer secondRefObjNum,
                                 String refAct,
                                 String relationType) {
        this.ocel = ocel;
        this.firstRefObj = firstRefObj;
        this.secondRefObj = secondRefObj;
        this.firstRefObjNum = firstRefObjNum;
        this.secondRefObjNum = secondRefObjNum;
        this.refAct = refAct;
        this.relationType = relationType;
    }

    public ViolatedSet checkObjToObjStatic(UIPluginContext context) {
        switch (relationType) {
            case "not co-existence":
                return getObjNotCoExistence(context);
            case "co-birth":
                return getObjCoBirth(context);
            case "not co-birth":
                return getObjNotCoBirth(context);
            case "co-death":
                return getObjCoDeath(context);
            case "not co-death":
                return getObjNotCoDeath(context);
            case "co-existence":
                return getObjCoExistence(context);
            default:
                return null;
        }
    }

    public ViolatedSet getObjCoExistence(UIPluginContext context) {
       if (refAct.equals("")){
           return getObjCoExistenceWithoutRefAct(context);
       }
       else{
           return getObjCoExistenceWithRefAct(context);
       }
    }

    private ViolatedSet getObjCoExistenceWithRefAct(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

            firstRefObjNumInEvt = 0 ;
            secondRefObjNumInEvt = 0;

            // get the set of the objects
            Set<OcelObject> objSet = set.getValue().relatedObjects;

            if(set.getValue().activity.equals(refAct)) {
                // Printing all elements of a Map
                    for (OcelObject eachObj : objSet) {

                        if (eachObj.objectType.name.equals(firstRefObj)) {
                            firstRefObjNumInEvt += 1;
                        }
                        if (eachObj.objectType.name.equals(secondRefObj)) {
                            secondRefObjNumInEvt += 1;
                        }
                    }
                    // see if the matching number is greater or equal to the target number
                    if (firstRefObjNum / secondRefObjNum != firstRefObjNumInEvt / secondRefObjNumInEvt) {

                        reason = "The cardinality for the object type " +
                                firstRefObj + " and type " + secondRefObj + " is " +firstRefObjNumInEvt +".."+secondRefObjNumInEvt+ " (should be "+ firstRefObjNum +".."+secondRefObjNum+")";

                        vs.appendViolatedRule(set.getKey(), set.getValue().activity, firstRefObj + ", " + secondRefObj, reason);
                    }
                }

            }
        return vs;
    }

    private ViolatedSet getObjCoExistenceWithoutRefAct(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {

            firstRefObjNumInEvt = 0 ;
            secondRefObjNumInEvt = 0;

                // get the set of the objects
            Set<OcelObject> objSet = set.getValue().relatedObjects;

            // Printing all elements of a Map
            for (OcelObject eachObj : objSet) {
                if (eachObj.objectType.name.equals(firstRefObj)){
                    firstRefObjNumInEvt += 1;
                }
                if (eachObj.objectType.name.equals(secondRefObj)){
                    secondRefObjNumInEvt += 1;
                }
            }

            // see if the matching number is greater or equal to the target number
            if (firstRefObjNum/secondRefObjNum != firstRefObjNumInEvt/secondRefObjNumInEvt){
                reason = "The cardinality for the object type " +
                        firstRefObj + " and type " + secondRefObj + " is " +firstRefObjNumInEvt +".."+secondRefObjNumInEvt+ " (should be "+ firstRefObjNum +".."+secondRefObjNum+")";

                vs.appendViolatedRule(set.getKey(), set.getValue().activity, firstRefObj +", "+secondRefObj, reason);
            }

        }

        return vs;
    }

    private ViolatedSet getObjNotCoExistence(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        Boolean f1;
        Boolean f2;
        // iterate each event in the ocel
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            Set<OcelObject> s1 = set.getValue().relatedObjects;
            f1 = false;
            f2 = false;
            for (OcelObject o1:s1){
                if (o1.objectType.name.equals("firstRefObj")){
                    f1 = true;
                }
                if (o1.objectType.name.equals("secondRefObj")){
                    f2 = true;
                }
            }
            if (!f1 || !f2){
                reason = "Object type of " + firstRefObj + " co-exist with object type of " + secondRefObj;
                vs.appendViolatedRule(set.getKey(), set.getValue().activity, firstRefObj +", "+secondRefObj, reason);

            }
        }
        return vs;
    }

    private ViolatedSet getObjNotCoDeath(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(2*(firstLog.size()+secondLog.size()));
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        HashMap<String,String> firstEvtId = new HashMap<>();
        HashMap<String,String> secondEvtId = new HashMap<>();

        // iterate each event in the first and second flattened log
        // iterate each case in the log
        for (int i = 0; i < firstLog.size(); i++) {
            // get the current trace
            tempTrace = firstLog.get(i);
            // get the first event in the trace
            firstEvtId.put(String.valueOf(tempTrace.get(tempTrace.size()-1).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }

        for (int i = 0; i < secondLog.size(); i++) {
            // get the current trace
            tempTrace = secondLog.get(i);
            // get the first event in the trace
            secondEvtId.put(String.valueOf(tempTrace.get(tempTrace.size()-1).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }


        for (String key : firstEvtId.keySet()) {
            if (secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-death with object type of " + secondRefObj;
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-death with object type of " + secondRefObj;
                vs.appendViolatedRule(key, secondEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        return vs;
    }

    private ViolatedSet getObjCoDeath(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(2*(firstLog.size()+secondLog.size()));
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        HashMap<String,String> firstEvtId = new HashMap<>();
        HashMap<String,String> secondEvtId = new HashMap<>();

        // iterate each event in the first and second flattened log
        // iterate each case in the log
        for (int i = 0; i < firstLog.size(); i++) {
            // get the current trace
            tempTrace = firstLog.get(i);
            // get the first event in the trace
            firstEvtId.put(String.valueOf(tempTrace.get(tempTrace.size()-1).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }

        for (int i = 0; i < secondLog.size(); i++) {
            // get the current trace
            tempTrace = secondLog.get(i);
            // get the first event in the trace
            secondEvtId.put(String.valueOf(tempTrace.get(tempTrace.size()-1).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }


        for (String key : firstEvtId.keySet()) {
            if (!secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-death with object type of " + secondRefObj;

                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (!firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-death with object type of " + secondRefObj;
                vs.appendViolatedRule(key, secondEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        return vs;
    }

    private ViolatedSet getObjNotCoBirth(UIPluginContext context) {

        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(2*(firstLog.size()+secondLog.size()));
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        HashMap<String,String> firstEvtId = new HashMap<>();
        HashMap<String,String> secondEvtId = new HashMap<>();

        // iterate each event in the first and second flattened log
        // iterate each case in the log
        for (int i = 0; i < firstLog.size(); i++) {
            // get the current trace
            tempTrace = firstLog.get(i);
            // get the first event in the trace
            firstEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }

        for (int i = 0; i < secondLog.size(); i++) {
            // get the current trace
            tempTrace = secondLog.get(i);
            // get the first event in the trace
            secondEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }



        for (String key : firstEvtId.keySet()) {
            if (secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-birth with object type of " + secondRefObj;
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-birth with object type of " + secondRefObj;
                vs.appendViolatedRule(key, secondEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        return vs;
    }

    public ViolatedSet getObjCoBirth(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(2*(firstLog.size()+secondLog.size()));
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        HashMap<String,String> firstEvtId = new HashMap<>();
        HashMap<String,String> secondEvtId = new HashMap<>();

        // iterate each event in the first and second flattened log
        // iterate each case in the log
        for (int i = 0; i < firstLog.size(); i++) {
            // get the current trace
            tempTrace = firstLog.get(i);
            // get the first event in the trace
            firstEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }

        for (int i = 0; i < secondLog.size(); i++) {
            // get the current trace
            tempTrace = secondLog.get(i);
            // get the first event in the trace
            secondEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
            context.getProgress().inc();
        }


        for (String key : firstEvtId.keySet()) {
            if (!secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (!firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
                vs.appendViolatedRule(key, secondEvtId.get(key), firstRefObj +", "+secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        return vs;
    }


}