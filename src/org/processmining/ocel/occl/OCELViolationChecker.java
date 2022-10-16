package org.processmining.ocel.occl;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;

public class OCELViolationChecker {


    private XLog log;
    private String refObj;
    private String firstAct;
    private String secondAct;
    private String orderRelation;

    public ViolatedSet vs = new ViolatedSet();

    public OCELViolationChecker() {
    }

    public OCELViolationChecker(XLog log,
                                String refObj,
                                String firstAct,
                                String secondAct,
                                String orderRelation) {
        this.log = log;
        this.refObj = refObj;
        this.firstAct = firstAct;
        this.secondAct = secondAct;
        this.orderRelation = orderRelation;
    }


    public ViolatedSet checkOrderRelation(UIPluginContext context){
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        switch (orderRelation){
            case "direct after":
                return getDirectAfterRelation(context);
            case "response":
                return getResponseRelation(context);
            case "unary response":
                return getUnaryResponseRelation(context);
            case "not direct after":
                return getNotDirectAfterRelation(context);
            case "not unary response":
                return getNotResponseRelation(context);
            default:
                return null;
        }
    }

    public ViolatedSet getDirectAfterRelation(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each case in the log
        for (int i = 0; i < log.size(); i++) {
            // get the current trace
            XTrace tempTrace = log.get(i);
            // iterate each event in the trace
            for (int j = 0; j < tempTrace.size() - 1; j++) {

                // if the first activity exist, we check the next activity
                if (tempTrace.get(j).getAttributes().get("concept:name").toString().equals(firstAct)) {
                    if (!tempTrace.get(j + 1).getAttributes().get("concept:name").toString().equals(secondAct)) {
                        // add to the violated set
                        String eventId = tempTrace.get(j).getAttributes().get("event_id").toString();
                        String reason = "The next activity of " + firstAct + " is not " + secondAct;
                        vs.appendViolatedRule(eventId, firstAct, refObj, reason);
                    }
                }
            }
            if (tempTrace.get(tempTrace.size() - 1).getAttributes().get("concept:name").toString().equals(firstAct)) {
                String reason = "Activity " + firstAct + " is the final activity";
                String eventId = tempTrace.get(tempTrace.size() - 1).getAttributes().get("event_id").toString();
                vs.appendViolatedRule(eventId, firstAct, refObj, reason);
            }
            System.out.println(vs.violatedRules);
            context.getProgress().inc();
        }
        return vs;
    }

    // Get the response relation
    public ViolatedSet getResponseRelation(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        for (int i = 0; i < log.size(); i++) {
            // get the current trace
            XTrace tempTrace = log.get(i);
            // iterate each event in the trace

            for (int j = 0; j < tempTrace.size() - 1; j++) {
                boolean responseFlag = false;

                // if the first activity exist, we check the second activity in the rule
                if (tempTrace.get(j).getAttributes().get("concept:name").toString().equals(firstAct)) {

                    // get the id of the first activity
                    String eventId = tempTrace.get(j).getAttributes().get("event_id").toString();

                    for(int k=j+1; k<tempTrace.size(); k++){
                       if (tempTrace.get(k).getAttributes().get("concept:name").toString().equals(secondAct)) {
                           // if the second activity exists, set the response flag to true
                           responseFlag = true;
                           break;
                       }
                    }
                    if (!responseFlag) {
                        String reason = "The response rule of "+
                                firstAct+" - "+
                                secondAct+
                                " is not satisfied";
                        vs.appendViolatedRule(eventId, firstAct, refObj, reason);
                    }
                }
            }
            System.out.println(vs.violatedRules);
        }
        return vs;
    }

    public ViolatedSet getUnaryResponseRelation(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        for (int i = 0; i < log.size(); i++) {
            // get the current trace
            XTrace tempTrace = log.get(i);
            // iterate each event in the trace

            for (int j = 0; j < tempTrace.size() - 1; j++) {
                int responseCount = 0;

                // if the first activity exist, we check the second activity in the rule
                if (tempTrace.get(j).getAttributes().get("concept:name").toString().equals(firstAct)) {

                    // get the id of the first activity
                    String eventId = tempTrace.get(j).getAttributes().get("event_id").toString();

                    for(int k=j+1; k<tempTrace.size(); k++){
                        if (tempTrace.get(k).getAttributes().get("concept:name").toString().equals(secondAct)) {
                            // if the second activity exists, set the response flag to true
                            responseCount += 1;
                            break;
                        }
                    }
                    if (responseCount>1) {
                        String reason = "The unary response rule of "+
                                firstAct+" - "+
                                secondAct+
                                " is not satisfied";
                        vs.appendViolatedRule(eventId, firstAct, refObj, reason);
                    }
                }
            }
            System.out.println(vs.violatedRules);
        }
        return vs;
    }

    public ViolatedSet getNotDirectAfterRelation(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);

        // iterate each case in the log
        for (int i = 0; i < log.size(); i++) {
            // get the current trace
            XTrace tempTrace = log.get(i);
            // iterate each event in the trace
            for (int j = 0; j < tempTrace.size() - 1; j++) {

                // if the first activity exist, we check the next activity
                if (tempTrace.get(j).getAttributes().get("concept:name").toString().equals(firstAct)) {
                    if (tempTrace.get(j + 1).getAttributes().get("concept:name").toString().equals(secondAct)) {
                        // add to the violated set
                        String eventId = tempTrace.get(j).getAttributes().get("event_id").toString();
                        String reason = "Activity" + secondAct+" should not be directly after activity "+ firstAct;
                        vs.appendViolatedRule(eventId, secondAct, refObj, reason);
                    }
                }
            }
            System.out.println(vs.violatedRules);
        }
        return vs;
    }

    public ViolatedSet getNotResponseRelation(UIPluginContext context) {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);
        return null;
    }

}
