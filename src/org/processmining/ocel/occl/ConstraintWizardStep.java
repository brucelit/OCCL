package org.processmining.ocel.occl;

import javax.swing.JComponent;

import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.ocel.ocelobjects.OcelEventLog;

public class ConstraintWizardStep extends OCCLConfigPanel implements ProMWizardStep<ConstraintsWizardParameters> {

	private static final String TITLE = "Configure Object-centric Constraints";

	public ConstraintWizardStep(OcelEventLog ocelLog) {
		super(ocelLog);
	}

	// get selected object type
	public ConstraintsWizardParameters apply(ConstraintsWizardParameters model, JComponent component) {
		// TODO Auto-generated method stub
		if (canApply(model, component)) {
			ConstraintWizardStep step = (ConstraintWizardStep) component;
//			model.setObjectType((String)objectTypeList.getSelectedItem());
		}
		return model;
	}

	public boolean canApply(ConstraintsWizardParameters model, JComponent component) {
		// TODO Auto-generated method stub
		return component instanceof ConstraintWizardStep;
	}

	public JComponent getComponent(ConstraintsWizardParameters model) {
		// TODO Auto-generated method stub
		return this;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return TITLE;
	}
}
