package org.processmining.ocel.occl;

import static com.mxgraph.swing.mxGraphComponent.DEFAULT_PAGESCALE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMPasswordField;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.editor.MiddleWare;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

/**
 * 
 */
public class OCCLConfigPanel extends RoundedPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected double pageScale = DEFAULT_PAGESCALE;

	protected PageFormat pageFormat = new PageFormat();

	protected static List<String> objectTypes;

	protected Set<String> activityList = new HashSet<>();

	
	private OcelEventLog ocelLog;
	
	private boolean first = true;
	
	static MiddleWare mw = MiddleWare.getInstance();

//	private final JPanel properties;

	public OCCLConfigPanel(final OcelEventLog ocelLog) {
		super(0,0,0);
		setMinimumSize(new Dimension(250, 150));
		setMaximumSize(new Dimension(250, 150));
		setPreferredSize(new Dimension(250, 150));
		setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.ocelLog = ocelLog;
		
		for (OcelEvent evt:ocelLog.events.values()){
			activityList.add(evt.activity);
		}
		
		mw.setActivityList(activityList);
		objectTypes = new ArrayList<String>(this.ocelLog.objectTypes.keySet());

		GraphEditor ge = new GraphEditor(objectTypes);
		ge.createFrame();
		EditorPalette ep = PaletteEditor(ge.getGraphComponent().getGraph());
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		add(ep);
		add(ge);
	}
	
	
	public EditorPalette PaletteEditor(mxGraph graph) {
		EditorPalette objectPalette = new EditorPalette();
		
		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		objectPalette.addListener(mxEvent.SELECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((GraphEditor.CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}
		});

		// Adds some template cells for dropping into the graph
		objectPalette
				.addTemplate(
						"Identifier",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/actid.png")),
						"image;image=/com/mxgraph/examples/swing/images/activity_id.png", ((int) Math.round(pageFormat.getHeight() * pageScale /20)), ((int) Math.round(pageFormat.getHeight() * pageScale /20)), null, "identifier");
		objectPalette
				.addTemplate(
						"Object period",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/objPeriod.png")),
						"image;image=/com/mxgraph/examples/swing/images/objPeriod2.png", ((int) Math.round(pageFormat.getHeight() * pageScale /8)), ((int) Math.round(pageFormat.getHeight() * pageScale /15)), null, "objPeriod");
		objectPalette
				.addTemplate(
						"Object state",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/objState.png")),
						"image;image=/com/mxgraph/examples/swing/images/objState.png", ((int) Math.round(pageFormat.getHeight() * pageScale /20)), ((int) Math.round(pageFormat.getHeight() * pageScale /20)), null, "objState");
		objectPalette
		.addTemplate(
				"Activity",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/activity1.png")),
				"image;verticalAlign=middle;image=/com/mxgraph/examples/swing/images/activity1.png", ((int) Math.round(pageFormat.getHeight() * pageScale /10)), ((int) Math.round(pageFormat.getHeight() * pageScale /20)),
				"", "activity");
		return objectPalette;
	}
	


	/**
	 * @param name
	 * @return
	 */
	public JCheckBox addCheckBox(final String name) {
		return addCheckBox(name, false);
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public JCheckBox addCheckBox(final String name, final boolean value) {
		final JCheckBox checkBox = SlickerFactory.instance().createCheckBox(null, value);
		return addProperty(name, checkBox);
	}

	/**
	 * @param name
	 * @param values
	 * @return
	 */
	public <E> ProMComboBox<E> addComboBox(final String name, final E[] values) {
		return addProperty(name, new ProMComboBox<E>(values));
	}

	/**
	 * @param name
	 * @param values
	 * @return
	 */
	public <E> ProMComboBox<E> addComboBox(final String name, final Iterable<E> values) {
		return addProperty(name, new ProMComboBox<E>(values));
	}

	/**
	 * @param <T>
	 * @param name
	 * @param component
	 * @return
	 */
	public <T extends JComponent> T addProperty(final String name, final T component) {
		if (!first) {
//			properties.add(Box.createVerticalStrut(3));
		} else {
			first = false;
		}
//		properties.add(packInfo(name, component));
		return component;
	}

	/**
	 * @param name
	 * @return
	 */
	public ProMTextField addTextField(final String name) {
		return addTextField(name, "");
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public ProMTextField addTextField(final String name, final String value) {
		final ProMTextField component = new ProMTextField();
		component.setText(value);
		return addProperty(name, component);
	}

	private Component findComponent(final Component component) {
		if (component instanceof AbstractButton) {
			return component;
		}
		if (component instanceof JTextComponent) {
			return component;
		}
		if (component instanceof Container) {
			for (final Component child : ((Container) component).getComponents()) {
				final Component result = findComponent(child);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private void installHighlighter(final Component component, final RoundedPanel target) {
		component.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 240));
				target.repaint();
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 160));
				target.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) { /* ignore */
			}
		});
		if (component instanceof Container) {
			for (final Component child : ((Container) component).getComponents()) {
				installHighlighter(child, target);
			}
		}
	}

	protected RoundedPanel packInfo(final String name, final JComponent component) {
		final RoundedPanel packed = new RoundedPanel(10, 0, 0);
		packed.setBackground(new Color(60, 60, 60, 160));
		final RoundedPanel target = packed;
		final Component actualComponent = findComponent(component);
		packed.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) {
				if (actualComponent != null) {
					if (actualComponent instanceof AbstractButton) {
						final AbstractButton button = (AbstractButton) actualComponent;
						button.doClick();
					}
					if (actualComponent instanceof JTextComponent) {
						final JTextComponent text = (JTextComponent) actualComponent;
						if (text.isEnabled() && text.isEditable()) {
							text.selectAll();
						}
						text.grabFocus();
					}
				}
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 240));
				target.repaint();
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				target.setBackground(new Color(60, 60, 60, 160));
				target.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) { /* ignore */
			}
		});
		installHighlighter(component, target);
		packed.setLayout(new BoxLayout(packed, BoxLayout.X_AXIS));
		final JLabel nameLabel = new JLabel(name);
		nameLabel.setOpaque(false);
		nameLabel.setForeground(WidgetColors.TEXT_COLOR);
		nameLabel.setFont(nameLabel.getFont().deriveFont(12f));
		nameLabel.setMinimumSize(new Dimension(1500, 2000));
		nameLabel.setMaximumSize(new Dimension(1500, 1000));
		nameLabel.setPreferredSize(new Dimension(1500, 3000));

		packed.add(Box.createHorizontalStrut(5));
		packed.add(nameLabel);
		packed.add(Box.createHorizontalGlue());
		packed.add(component);
		packed.add(Box.createHorizontalStrut(5));
		packed.revalidate();
		return packed;
	}

	/**
	 * Adds a new password field
	 * 
	 * @param name
	 *            Name for the password field
	 * @return The new password field
	 */
	public ProMPasswordField addPasswordInputField(final String name) {
		return addPasswordInputField(name, "");
	}

	/**
	 * Adds a new password field
	 * 
	 * @param name
	 *            Name for the password field
	 * @param value
	 *            Value for the password field
	 * @return The new password field
	 */
	public ProMPasswordField addPasswordInputField(final String name, final String value) {
		final ProMPasswordField component = new ProMPasswordField();
		component.setText(value);
		return addProperty(name, component);
	}

}
