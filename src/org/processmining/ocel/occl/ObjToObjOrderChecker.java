package org.processmining.ocel.occl;

import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.*;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.utils.TypeFromValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjToObjOrderChecker {

    private OcelEventLog ocel;
    private String refObj;

    private String targObj;
    private String refAct;
    private Integer refobjNum;
    private Integer tarObjNum;

    private Integer objMatchNum;
    private String objCount;
    private String objOrderRule="";
    public ViolatedSet vs = new ViolatedSet();
    public String reason = "";

    public ObjToObjOrderChecker() {
    }

    public ObjToObjOrderChecker(OcelEventLog ocel,
                                String refObj,
                                String targObj,
                                String objOrderRule,
                                String objCount) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.targObj = targObj;
        this.objOrderRule = objOrderRule;
        this.objCount = objCount;
    }

    public ObjToObjOrderChecker(OcelEventLog ocel,
                                String refObj,
                                String targObj,
                                Integer refobjNum,
                                Integer tarObjNum,
                                String objOrderRule,
                                String objCount) {
        this.ocel = ocel;
        this.refObj = refObj;
        this.targObj = targObj;
        this.refobjNum = refobjNum;
        this.tarObjNum = tarObjNum;
        this.objOrderRule = objOrderRule;
        this.objCount = objCount;
    }



    public ViolatedSet getObjOrderWithRefAct(UIPluginContext context) {
        switch (objOrderRule){
            case "objStateToStateResponse":
                vs = getObjResponseRelation(context);
//            case "objStateToStatePrecedence":
//                vs = getObjPrecedenceRelation(context);
        }
        return vs;
    }

//    public ViolatedSet getObjPrecedenceRelation(UIPluginContext context) {
//        switch (objCount){
//            case "smallerThan":
//                vs = getSmallerThanObjCount(context);
//            case "smallerOrEqualTo":
//                vs = getSmallerOrEqualToObjCount(context);
//            case "equalTo":
//                vs = getEqualToObjCount(context);
//            case "greaterThan":
//                vs = getGreaterThanObjCount(context);
//            case "greaterOrEqualTo":
//                vs = getGreaterOrEqualToObjCount(context);
//        }
//        return vs;
//    }

    public ViolatedSet getObjResponseRelation(UIPluginContext context) {
        switch (objCount){
            case "smallerThan":
                vs = getSmallerThanObjResponseRelation(context);
            case "smallerOrEqualTo":
                vs = getSmallerOrEqualToObjResponseRelation(context);
            case "equalTo":
                vs = getSmallerThanObjResponseRelation(context);
            case "greaterThan":
                vs = geGreaterThanObjResponseRelation(context);
            case "greaterOrEqualTo":
                vs = getGreaterOrEqualToObjResponseRelation(context);
        }
        return vs;
    }

    private ViolatedSet getGreaterOrEqualToObjResponseRelation(UIPluginContext context) {
        XAttributeMap logAttributes = new XAttributeMapImpl();
        XLog log = new XLogImpl(logAttributes);
        for (OcelObject ocelObject : ocel.objectTypes.get(refObj).objects) {
            XAttributeMap traceAttributes = new XAttributeMapImpl();
            XAttribute caseId = new XAttributeLiteralImpl("concept:name", ocelObject.id);
            traceAttributes.put("concept:name", caseId);
            XTrace trace = new XTraceImpl(traceAttributes);
            for (OcelEvent ocelEvent : ocelObject.sortedRelatedEvents) {
                XAttributeMap eventAttributes = new XAttributeMapImpl();
                XAttribute eventId = new XAttributeLiteralImpl("event_id", ocelEvent.id);
                XAttribute conceptName = new XAttributeLiteralImpl("concept:name", ocelEvent.activity);
                XAttribute timeTimestamp = new XAttributeTimestampImpl("time:timestamp", ocelEvent.timestamp);
                Set<String> objTypeSet = new HashSet<>();
                for (OcelObject s : ocelEvent.relatedObjects) {
                    objTypeSet.add(s.objectType.name);
                }
                if (objTypeSet.contains(targObj)){
                    vs.appendViolatedRule(eventId.toString(), conceptName.toString(), refObj, "");

                }
            }
            log.add(trace);
        }

        return vs;
    }

    private ViolatedSet geGreaterThanObjResponseRelation(UIPluginContext context) {

        return null;

    }

    private ViolatedSet getSmallerOrEqualToObjResponseRelation(UIPluginContext context) {

        return null;

    }

    private ViolatedSet getSmallerThanObjResponseRelation(UIPluginContext context) {

        return null;

    }

//


}