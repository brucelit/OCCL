package org.processmining.ocel.occl;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;

import java.util.Map;
import java.util.Set;

public class ObjViolationChecker {

    private OcelEventLog ocel;
    private String refObj;
    private String refAct;
    private Integer objNum;

    private String objCountRule="";
    public ViolatedSet vs = new ViolatedSet();


    public ObjViolationChecker() {
    }

    public ObjViolationChecker(OcelEventLog ocel,
                               String refObj,
                               Integer objNum,
                               String objCountRule) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.objNum = objNum;
        this.objCountRule = objCountRule;
    }

    public ObjViolationChecker(OcelEventLog ocel,
                               String refObj,
                               Integer objNum,
                               String refAct,
                               String objCountRule) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.objNum = objNum;
        this.refAct = refAct;
        this.objCountRule = objCountRule;
    }


    public ViolatedSet checkOrderRelation(UIPluginContext context) {
        switch (objCountRule) {
            case "ObjectCountWithoutRefAct":
                return getObjCountWithoutRefAct(context);
            case "ObjectCountWithRefAct":
                return getObjCountWithRefAct(context);
            default:
                return null;
        }
    }

    private ViolatedSet getObjCountWithoutRefAct(UIPluginContext context) {

        return null;
    }

    public ViolatedSet getObjCountWithRefAct(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(ocel.events.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each case in the log
        for (Map.Entry<String, OcelEvent> set :
                ocel.events.entrySet()) {
            if (set.getValue().activity.equals(refAct)) {

                Set<OcelObject> objSet = set.getValue().relatedObjects;

                    // Printing all elements of a Map
                for (OcelObject eachObj : objSet) {

                }
            }
        }
        return vs;
    }
}