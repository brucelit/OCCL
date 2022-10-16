package org.processmining.ocel.occl;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class ConstraintsWizardParameters extends PluginParametersImpl {
	public String objectType;
	
	public ConstraintsWizardParameters() {
		
	}
	
	public ConstraintsWizardParameters(String objectType) {
		this.objectType = objectType;
	}
	
	public String getObjectType() {
		return this.objectType;
	}
	
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
