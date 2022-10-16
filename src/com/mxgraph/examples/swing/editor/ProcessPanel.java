package com.mxgraph.examples.swing.editor;

import static com.mxgraph.swing.mxGraphComponent.DEFAULT_PAGESCALE;

import java.awt.Dimension;
import java.awt.print.PageFormat;

import javax.swing.BoxLayout;

import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.components.RoundedPanel;

/* 
 * @author mwesterg

 */
public class ProcessPanel extends RoundedPanel {

	
	protected double pageScale = DEFAULT_PAGESCALE;
	protected PageFormat pageFormat = new PageFormat();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param title
	 */
	public ProcessPanel(final String title) {
		super(0, 0, 0);
//		setMinimumSize(new Dimension(1000, 1500));
//		setMaximumSize(new Dimension(1000, 1500));
		setPreferredSize(new Dimension(1000, 1500));
//		setMinimumSize(new Dimension((int) Math.round(pageFormat.getWidth() * pageScale), (int) Math.round(pageFormat.getHeight()
//				* pageScale)));
//		setMaximumSize(new Dimension((int) Math.round(pageFormat.getWidth() * pageScale), (int) Math.round(pageFormat.getHeight()
//				* pageScale)));
//		setPreferredSize(new Dimension((int) Math.round(pageFormat.getWidth() * pageScale), (int) Math.round(pageFormat.getHeight()
//				* pageScale)));
		setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

}
