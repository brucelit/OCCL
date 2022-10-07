package org.processmining.ocel.occl;

import javax.swing.JComponent;

import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.ocel.ocelobjects.OcelEventLog;

public class ConstraintWizardStep extends OCCLConfigPanel implements ProMWizardStep<FlatteningWizardParameters> {
	OcelEventLog ocelLog;
	
	private static final String TITLE = "Configure Object-centric Constraints";
	
	
//	private final ProMComboBox<String> objectTypeList;
//	private final ProMTextField objectTypeList2;

	public ConstraintWizardStep(OcelEventLog ocelLog) {
		super(TITLE, ocelLog);

	}

	// get selected object type
	public FlatteningWizardParameters apply(FlatteningWizardParameters model, JComponent component) {
		// TODO Auto-generated method stub
		if (canApply(model, component)) {
			ConstraintWizardStep step = (ConstraintWizardStep) component;
//			model.setObjectType((String)objectTypeList.getSelectedItem());
		}
		return model;
	}

	public boolean canApply(FlatteningWizardParameters model, JComponent component) {
		// TODO Auto-generated method stub
		return component instanceof ConstraintWizardStep;
	}

	public JComponent getComponent(FlatteningWizardParameters model) {
		// TODO Auto-generated method stub
		return this;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return TITLE;
	}
}
