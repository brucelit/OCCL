package org.processmining.ocel.occl;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class SingleActChecker {
    private XLog log;
    private String refObj;
    private String refAct;
    private Long waitingTime;
    private Long serviceTime;
    private Long soujournTime;

    private String reason;

    public ViolatedSet vs;

    public SingleActChecker() {
    }

    public SingleActChecker(XLog log,
                            String refAct) {
        this.log = log;
        this.refAct = refAct;
    }


    public SingleActChecker(XLog log,
                            String refObj,
                            String refAct,
                            Long waitingTime,
                            Long serviceTime,
                            Long soujournTime) {
        this.log = log;
        this.refObj = refObj;
        this.refAct = refAct;
        this.waitingTime = waitingTime;
        this.serviceTime = serviceTime;
        this.soujournTime = soujournTime;
    }


    public ViolatedSet checkWaitingTimeForAct(UIPluginContext context) throws ParseException {
        context.getProgress().setMinimum(0);
        context.getProgress().setMaximum(log.size());
        context.getProgress().setCaption("Constructing hello worlds string");
        context.getProgress().setIndeterminate(false);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        ViolatedSet vs = new ViolatedSet();

        // iterate each case in the log
        for (int i = 0; i < log.size(); i++) {
            // get the current trace
            XTrace tempTrace = log.get(i);

            reason = "The waiting time for activity "+refAct +" is ";
            // iterate each event in the trace
            for (int j = 1; j < tempTrace.size(); j++) {

                // if the first activity exist, we check the next activity
                if (tempTrace.get(j).getAttributes().get("concept:name").toString().equals(refAct)) {
                    // add to the violated set
                    String at1 =  tempTrace.get(j - 1).getAttributes().get("time:timestamp").toString();
                    String at2 =  tempTrace.get(j).getAttributes().get("time:timestamp").toString();
                    Long timeGap = df.parse(at2).getTime() - df.parse(at1).getTime();
                    if (timeGap > 10){
                        reason = reason+timeGap;
                        vs.appendViolatedRule(tempTrace.get(j).getAttributes().get("event_id").toString(),refAct,refObj, reason);
                    }
                }
            }
            context.getProgress().inc();
            }
        return vs;
    }

}
