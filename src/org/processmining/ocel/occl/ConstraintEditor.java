package org.processmining.ocel.occl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mxgraph.examples.swing.editor.MiddleWare;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.*;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.ocel.flattening.FlatteningWizardParameters;
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
	public ViolatedSet CheckOCELConstraints(UIPluginContext context, OcelEventLog ocel) {

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
		if (!orderRelation.equals(""))
		{
			checkType = "actOrder";
		}
//		else if (!singleActRelation.equals(""))

		switch (checkType){
			case "actOrder":
				XLog log = flatten(ocel, mw.actRefObj);
				OCELViolationChecker violationChecker = new OCELViolationChecker(log,"items", firstAct, secondAct, orderRelation);
				ViolatedSet v1 = violationChecker.getDirectAfterRelation(context);
				return v1;
				// return the violation object as return
			//			case "unary response":
//				return getUnaryResponseRelation(context);
			default:
				ObjViolationChecker objViolationChecker = new ObjViolationChecker(ocel,"items", 1, "pick item", "ObjectCountWithRefAct");
				ViolatedSet v2 = objViolationChecker.getObjCountWithRefAct(context);
				return v2;
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
 