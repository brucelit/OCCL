package org.processmining.ocel.occl;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;

import java.util.*;

public class ObjToObjRelationChecker {

    private XLog firstLog;
    private XLog secondLog;

    private OcelEventLog ocel;
    private String firstRefObj;
    private String secondRefObj;
    private String relationType;

    public ViolatedSet vs = new ViolatedSet();
    public String reason;

    public  XTrace tempTrace;

    public ObjToObjRelationChecker() {

    }


    public ObjToObjRelationChecker(XLog firstLog,
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

    public ViolatedSet checkObjCount(UIPluginContext context) {
        switch (relationType) {
            case "coBirth":
                return getObjCoBirth(context);
            case "notCoBirth":
                return getObjNotCoBirth(context);
            case "coDeath":
                return getObjCoDeath(context);
            case "notCoDeath":
                return getObjNotCoDeath(context);
            default:
                return null;
        }
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
                System.out.println(reason);
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-death with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, secondEvtId.get(key), secondRefObj, reason);
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
                System.out.println(reason);
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (!firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-death with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, secondEvtId.get(key), secondRefObj, reason);
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

        System.out.println(firstEvtId);
        System.out.println(secondEvtId);


        for (String key : firstEvtId.keySet()) {
            if (secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-birth with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " co-birth with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, secondEvtId.get(key), secondRefObj, reason);
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

        System.out.println(firstEvtId);
        System.out.println(secondEvtId);


        for (String key : firstEvtId.keySet()) {
            if (!secondEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj, reason);
            }
            context.getProgress().inc();

        }

        for (String key : secondEvtId.keySet()) {
            if (!firstEvtId.containsKey(key)) {
                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
                System.out.println(reason);
                vs.appendViolatedRule(key, secondEvtId.get(key), secondRefObj, reason);
            }
            context.getProgress().inc();

        }

        return vs;
    }
//
//    private ViolatedSet getObjNotCoExist(UIPluginContext context) {
//        context.getProgress().setMinimum(0);
//        context.getProgress().setMaximum(2*(firstLog.size()+secondLog.size()));
//        context.getProgress().setCaption("Constructing hello worlds string");
//        context.getProgress().setIndeterminate(false);
//
//        HashMap<String,String> firstEvtId = new HashMap<>();
//        HashMap<String,String> secondEvtId = new HashMap<>();
//
//        // iterate each event in the first and second flattened log
//        // iterate each case in the log
//        for (int i = 0; i < firstLog.size(); i++) {
//            // get the current trace
//            tempTrace = firstLog.get(i);
//            // get the first event in the trace
//            firstEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
//            context.getProgress().inc();
//        }
//
//        for (int i = 0; i < secondLog.size(); i++) {
//            // get the current trace
//            tempTrace = secondLog.get(i);
//            // get the first event in the trace
//            secondEvtId.put(String.valueOf(tempTrace.get(0).getAttributes().get("event_id")), String.valueOf(tempTrace.get(0).getAttributes().get("concept:name")));
//            context.getProgress().inc();
//        }
//
//        System.out.println(firstEvtId);
//        System.out.println(secondEvtId);
//
//
//        for (String key : firstEvtId.keySet()) {
//            if (!secondEvtId.containsKey(key)) {
//                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
//                System.out.println(reason);
//                vs.appendViolatedRule(key, firstEvtId.get(key), firstRefObj, reason);
//            }
//            context.getProgress().inc();
//
//        }
//
//        for (String key : secondEvtId.keySet()) {
//            if (!firstEvtId.containsKey(key)) {
//                reason = "Object type of " + firstRefObj + " does not co-birth with object type of " + secondRefObj;
//                System.out.println(reason);
//                vs.appendViolatedRule(key, secondEvtId.get(key), secondRefObj, reason);
//            }
//            context.getProgress().inc();
//
//        }
//
//        return vs;
//    }
//
//    private ViolatedSet getObjCoExist(UIPluginContext context) {
//        return null;
//    }

}