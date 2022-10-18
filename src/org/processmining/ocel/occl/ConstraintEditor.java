package org.processmining.ocel.occl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.examples.swing.editor.MiddleWare;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.*;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;
import org.processmining.ocel.ocelobjects.OcelObject;
import org.processmining.ocel.utils.TypeFromValue;

@Plugin(name = "Object-centric Constraints Checker for OCEL",
returnLabels = { "Violations in OCEL" },
returnTypes = { ViolatedSet.class },
parameterLabels = { "Object-Centric Event Log" },
userAccessible = true)
public class ConstraintEditor {

	@PluginVariant(requiredParameterLabels = {0})
	@UITopiaVariant(affiliation = "PADS RWTH", author = "Tian", email = "tian.li@pads.rwth-aachen.de")
	public ViolatedSet CheckOCELConstraints(UIPluginContext context, OcelEventLog ocel) throws ParseException {

		MiddleWare mw = MiddleWare.getInstance();

		// get the occl configure panel
		ConstraintWizardStep wizStep = new ConstraintWizardStep(ocel);

		// enter the configure step
		List<ProMWizardStep<ConstraintsWizardParameters>> wizStepList = new ArrayList<>();

		wizStepList.add(wizStep);
		ListWizard<ConstraintsWizardParameters> listWizard = new ListWizard<>(wizStepList);
		ConstraintsWizardParameters parameters = ProMWizardDisplay.show(context, listWizard, new ConstraintsWizardParameters());


		String firstAct = mw.refParentValue;
		String secondAct = mw.targetParentValue;
		String orderRelation = mw.actOrderType;

		String checkType = "";
		if (mw.connType.equals("objStateToStateStatic")){
			checkType = "objToObjCoExistence";
		}
		else if (mw.connType.equals("actToActOrder")){
			checkType = "actOrder";
		}
		else if (mw.objNum.size() == 1){
			checkType = "singleObjAggregation";
		}
//		else if (!singleActRelation.equals(""))
		ViolatedSet v1;

		switch (checkType){
			case "actOrder":
				System.out.println("use this one to check");
				XLog log = flatten(ocel, mw.actRefObj);
				OCELViolationChecker violationChecker = new OCELViolationChecker(
						log,
						mw.actRefObj,
						mw.firstAct,
						mw.secondAct,
						mw.actToActOrder);
				v1 = violationChecker.checkOrderRelation(context);
				return v1;
			case "objToObjCoExistence":
				ObjToObjStaticChecker oosc;
				if (mw.objToObjStaticRelation.equals("not co-existence")){
					oosc = new ObjToObjStaticChecker(
							ocel,
							mw.refParentValue,
							mw.targetParentValue,
							mw.objToObjStaticRelation);}
				else if (mw.objToObjStaticRelation.equals("co-birth") ||
						mw.objToObjStaticRelation.equals("co-death") ||
						mw.objToObjStaticRelation.equals("not co-birth") ||
						mw.objToObjStaticRelation.equals("not co-death")){
					System.out.println(mw.refParentValue);
					System.out.println(mw.targetParentValue);
					XLog log1 = flatten(ocel, mw.refParentValue);
					XLog log2 = flatten(ocel, mw.targetParentValue);
					oosc = new ObjToObjStaticChecker(
							log1,
							log2,
							ocel,
							mw.refParentValue,
							mw.targetParentValue,
							mw.objToObjStaticRelation);
				}
				else if ((mw.objToObjStaticRelation.equals("co-existence") && mw.refAct.equals("")
						)){
					oosc = new ObjToObjStaticChecker(
							ocel,
							mw.refParentValue,
							mw.targetParentValue,
							mw.objNum.get(0),
							mw.objNum.get(1),
							mw.objToObjStaticRelation);
				}
				else{
					oosc = new ObjToObjStaticChecker(
							ocel,
							mw.refParentValue,
							mw.targetParentValue,
							mw.objNum.get(0),
							mw.objNum.get(1),
							mw.refAct,
							mw.objToObjStaticRelation);
				}
				return oosc.checkObjToObjStatic(context);

			case "singleObjAggregation":
				System.out.println("enter this plug in");
				SingleObjViolationChecker sovc;
				if (mw.refAct.equals("")){
					sovc = new SingleObjViolationChecker(
							ocel,
							mw.actRefObj,
							mw.objNum.get(0),
							"ObjectCountWithoutRefAct",
							mw.objCount
					);
				}
				else{
					sovc = new SingleObjViolationChecker(ocel,
							mw.actRefObj,
							mw.objNum.get(0),
							mw.refAct,
							"ObjectCountWithRefAct",
							mw.objCount
					);
				}
				return sovc.checkObjCount(context);

			default:
				ObjToObjOrderChecker o1 = new ObjToObjOrderChecker(
						ocel,
						"orders","items",
						20,
						20,
						"objStateToStateResponse",
						"greaterOrEqualTo");

				return o1.getObjOrderWithRefAct(context);
		}
	}

	public static XLog flatten(OcelEventLog ocel, String objectType) {
		XAttributeMap logAttributes = new XAttributeMapImpl();
		XLog log = new XLogImpl(logAttributes);
		for (OcelObject ocelObject : ocel.objectTypes.get(objectType).objects) {
			XAttributeMap traceAttributes = new XAttributeMapImpl();
			XAttribute caseId = new XAttributeLiteralImpl("concept:name", ocelObject.id);
			traceAttributes.put("concept:name", caseId);
			XTrace trace = new XTraceImpl(traceAttributes);
			for (OcelEvent ocelEvent : ocelObject.sortedRelatedEvents) {
				XAttributeMap eventAttributes = new XAttributeMapImpl();
				XAttribute eventId = new XAttributeLiteralImpl("event_id", ocelEvent.id);
				eventAttributes.put("event_id", eventId);
				XAttribute conceptName = new XAttributeLiteralImpl("concept:name", ocelEvent.activity);
				eventAttributes.put("concept:name", conceptName);
				XAttribute timeTimestamp = new XAttributeTimestampImpl("time:timestamp", ocelEvent.timestamp);
				eventAttributes.put("time:timestamp", timeTimestamp);
				for (String attribute : ocelEvent.attributes.keySet()) {
					Object attributeValue = ocelEvent.attributes.get(attribute);
					XAttribute xatt = TypeFromValue.getAttributeForValue(attribute, attributeValue);
					eventAttributes.put(attribute, xatt);
				}
				XEvent event = new XEventImpl(eventAttributes);
				trace.add(event);
			}
			log.add(trace);
		}

		return log;
	}




}
 