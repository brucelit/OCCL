/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package org.processmining.ocel.occl;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.editor.MiddleWare;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.*;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mxgraph.swing.mxGraphComponent.DEFAULT_PAGESCALE;


public class GraphEditor extends BasicGraphEditor
{
	static MiddleWare mw = MiddleWare.getInstance();
	
	protected double pageScale = DEFAULT_PAGESCALE;

	protected PageFormat pageFormat = new PageFormat();

	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;
	
	protected static  List<String> objectTypes;
	
	protected static List<String>  timeSymbol = Arrays.asList(
			"",
			">=",
			">",
			"<=",
			"<");
	
	protected static List<String>  timeUnit = Arrays.asList(
			"",
			"seconds",
			"minutes",
			"hours",
			"days",
			"months",
			"years");
	
	//GraphEditor.class.getResource("/com/mxgraph/examples/swing/images/connector.gif");

	public GraphEditor()
	{
		this("mxGraph Editor", objectTypes, new CustomGraphComponent(new CustomGraph()));
	}
	
	public GraphEditor(List<String> objectTypes)
	{
		this("mxGraph Editor", objectTypes, new CustomGraphComponent(new CustomGraph()));
	}

	/**
	 * 
	 */
	public GraphEditor(String appTitle, List<String> objectTypes, mxGraphComponent component) {
		super(appTitle, objectTypes, component);
		final mxGraph graph = graphComponent.getGraph();

		// Creates the shapes palette
		EditorPalette objectPalette = insertPalette(mxResources.get("object"));

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		objectPalette.addListener(mxEvent.SELECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		// Adds some template cells for dropping into the graph
		objectPalette
				.addTemplate(
						"Single Object",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/entity1.png")),
						"image;verticalAlign=middle;align=center;image=/com/mxgraph/examples/swing/images/entity1.png", ((int) Math.round(pageFormat.getHeight() * pageScale /20)), ((int) Math.round(pageFormat.getHeight() * pageScale /20)), null, "objEntity");
		objectPalette
				.addTemplate(
						"Identifier",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/actid.png")),
						"image;image=/com/mxgraph/examples/swing/images/activity_id.png", ((int) Math.round(pageFormat.getHeight() * pageScale /20)), ((int) Math.round(pageFormat.getHeight() * pageScale /20)), null, "identifier");
		objectPalette
				.addTemplate(
						"Period",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/objPeriod.png")),
						"image;image=/com/mxgraph/examples/swing/images/objPeriod2.png", ((int) Math.round(pageFormat.getHeight() * pageScale /8)), ((int) Math.round(pageFormat.getHeight() * pageScale /15)), null, "objPeriod");
		objectPalette
				.addTemplate(
						"State",
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
//		objectPalette
//				.addTemplate(
//						"Time",
//						new ImageIcon(
//								GraphEditor.class
//										.getResource("/com/mxgraph/examples/swing/images/timer.png")),
//						"image;image=/com/mxgraph/examples/swing/images/time.png", 700, 70, null, "time");
	}
	/**
	 * 
	 */
	public static class CustomGraphComponent extends mxGraphComponent
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 * 
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph)
		{
			super(graph);

			// Sets switches typically used in an editor
			setPageVisible(true);
			setToolTips(true);
			getConnectionHandler().setCreateTarget(true);

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils.loadDocument(GraphEditor.class.getResource(
					"/com/mxgraph/examples/swing/resources/default-style.xml")
					.toString());
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
		}

		/**
		 * Overrides drop behaviour to set the cell style if the target
		 * is not a valid drop target and the cells are of the same
		 * type (eg. both vertices or both edges). 
		 */
		public Object[] importCells(Object[] cells, double dx, double dy,
				Object target, Point location)
		{
			if (target == null && cells.length == 1 && location != null)
			{
				target = getCellAt(location.x, location.y);
				if (target instanceof mxICell && cells[0] instanceof mxICell)
				{
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex()
							|| targetCell.isEdge() == dropCell.isEdge())
					{
						mxIGraphModel model = graph.getModel();
						model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);
						return null;
					}
				}
			}
			Object[] cellToAdd = super.importCells(cells, dx, dy, target, location);
			// add port to certain type of elements
			graph.getModel().beginUpdate();
			try {
				if (((mxCell) cellToAdd[0]).getType().equals("objState")) {
					// add port to the element
					ArrayList<mxCell> ports = getStatePorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.addCell(ports.get(1), cellToAdd[0]);
					graph.addCell(ports.get(2), cellToAdd[0]);
					graph.addCell(ports.get(3), cellToAdd[0]);
					mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(1).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(2).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(3).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortSemantic(ports.get(0).getId(), "before");
					mw.updatePortSemantic(ports.get(1).getId(), "after");
					mw.updatePortSemantic(ports.get(2).getId(), "during");
					mw.updatePortSemantic(ports.get(3).getId(), "during");
					((mxCell) cellToAdd[0]).setConnectable(false);
					
					// begin configure the object property in period state
					JDialog dialog = new JDialog();
					dialog.setModal(true);
					dialog.setTitle("Object state setting");
					dialog.setSize(new Dimension(400,350));
					dialog.setLocationRelativeTo(null);  // set to the center of the screen
					JTabbedPane tab = new JTabbedPane();
					Box vBox = Box.createVerticalBox();
					
					vBox.add(Box.createVerticalStrut(30));
					Box hBoxAgg = Box.createHorizontalBox();
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JLabel jbAgg = new JLabel("Object number:");
					hBoxAgg.add(jbAgg);
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JComboBox<String> jcb_symbol = new JComboBox<String>();
					for (String ts: timeSymbol) {
						jcb_symbol.addItem(ts);
					}
					jcb_symbol.addItem("=");
					hBoxAgg.add(jcb_symbol);
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JTextField jtf_value = new JTextField();
					hBoxAgg.add(jtf_value);
					jtf_value.setSize(new Dimension(30, 20));
					jtf_value.setMaximumSize(new Dimension(30, 20));
					jtf_value.setPreferredSize(new Dimension(30, 20));			
					jcb_symbol.setSize(new Dimension(80, 20));
					jcb_symbol.setMaximumSize(new Dimension(80, 20));
					jcb_symbol.setPreferredSize(new Dimension(80, 20));
					vBox.add(hBoxAgg);

					tab.addTab("General object", null, vBox, "Basic settings");

					Box vBox2 = Box.createVerticalBox();

					vBox2.add(Box.createVerticalStrut(30));
					Box hBoxAgg2 = Box.createHorizontalBox();
					hBoxAgg2.add(Box.createHorizontalStrut(10));
					JLabel jbAgg2 = new JLabel("Object number:");
					hBoxAgg2.add(jbAgg2);
					hBoxAgg2.add(Box.createHorizontalStrut(10));
					JComboBox<String> jcb_symbol2 = new JComboBox<String>();
					for (String ts: timeSymbol) {
						jcb_symbol2.addItem(ts);
					}
					jcb_symbol2.addItem("=");
					hBoxAgg2.add(jcb_symbol2);
					hBoxAgg2.add(Box.createHorizontalStrut(10));
					JTextField jtf_value2 = new JTextField();
					hBoxAgg2.add(jtf_value2);
					jtf_value2.setSize(new Dimension(30, 20));
					jtf_value2.setMaximumSize(new Dimension(30, 20));
					jtf_value2.setPreferredSize(new Dimension(30, 20));
					jcb_symbol2.setSize(new Dimension(80, 20));
					jcb_symbol2.setMaximumSize(new Dimension(80, 20));
					jcb_symbol2.setPreferredSize(new Dimension(80, 20));
					vBox2.add(hBoxAgg2);

					tab.addTab("Specific object", null, vBox2, "Advanced settings");

					Box vBox3 = Box.createVerticalBox();
					vBox3.add(tab);
					vBox3.add(Box.createVerticalStrut(20));					
					Box hBoxConfirm = Box.createHorizontalBox();
					JButton jbConfirm = new JButton("Confirm");
					jbConfirm.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							graph.getModel().beginUpdate();
							dialog.dispose();
							graph.getModel().endUpdate();
							graph.refresh();
						}
					});
					JButton jbCancel = new JButton("Cancel");
					jbCancel.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							graph.getModel().beginUpdate();
							graph.removeCells(cellToAdd);
							graph.getModel().endUpdate();
							graph.refresh();
							dialog.dispose();
						}
					});
					hBoxConfirm.add(jbConfirm);
					hBoxConfirm.add(Box.createHorizontalStrut(20));
					hBoxConfirm.add(jbCancel);

					hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);

					vBox3.add(hBoxConfirm);
					dialog.add(vBox3);
					dialog.setVisible(true);

					
					graph.getModel().endUpdate();
					graph.refresh();
					
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("identifier")) {
					// add port to the element
					ArrayList<mxCell> ports = getIdentifierPorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortSemantic(ports.get(0).getId(), "identifier");
					((mxCell) cellToAdd[0]).setConnectable(false);
					graph.getModel().endUpdate();
					graph.refresh();
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("objPeriod")) {

					// add port to the element
					ArrayList<mxCell> ports = getPeriodPorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.addCell(ports.get(1), cellToAdd[0]);
					graph.addCell(ports.get(2), cellToAdd[0]);
					graph.addCell(ports.get(3), cellToAdd[0]);
					mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(1).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(2).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(3).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortSemantic(ports.get(0).getId(), "before");
					mw.updatePortSemantic(ports.get(1).getId(), "after");
					mw.updatePortSemantic(ports.get(2).getId(), "during");
					mw.updatePortSemantic(ports.get(3).getId(), "during");
					((mxCell) cellToAdd[0]).setConnectable(false);
				
					// begin configure the object property in period element
					JDialog dialog = new JDialog();
					dialog.setModal(true);
					dialog.setTitle("Object period setting");
					dialog.setSize(new Dimension(400,350));
					dialog.setLocationRelativeTo(null);  // set to the center of the screen
					JTabbedPane tab = new JTabbedPane();
					Box vBox = Box.createVerticalBox();
					Box hBox1 = Box.createHorizontalBox();
					Box hBox2 = Box.createHorizontalBox();

					JRadioButton radioBtn01 = new JRadioButton("General object");
			        JRadioButton radioBtn02 = new JRadioButton("Specific object");
			        ButtonGroup btnGroup = new ButtonGroup();
			        
			        
			        btnGroup.add(radioBtn01);
			        btnGroup.add(radioBtn02);
			        
			        radioBtn01.setSelected(true);
			        hBox1.add(radioBtn01);
					hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);
					vBox.add(Box.createVerticalStrut(20));
				    hBox2.add(radioBtn02);
					hBox2.setAlignmentX(Component.CENTER_ALIGNMENT);
					vBox.add(hBox1);
					vBox.add(hBox2);
					
					vBox.add(Box.createVerticalStrut(30));
					Box hBoxAgg = Box.createHorizontalBox();
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JLabel jbAgg = new JLabel("Object number:");
					hBoxAgg.add(jbAgg);
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JComboBox<String> jcb_symbol = new JComboBox<String>();
					for (String ts: timeSymbol) {
						jcb_symbol.addItem(ts);
					}
					jcb_symbol.addItem("equal to");
					hBoxAgg.add(jcb_symbol);
					hBoxAgg.add(Box.createHorizontalStrut(10));
					JTextField jtf_value = new JTextField();
					hBoxAgg.add(jtf_value);
					jtf_value.setSize(new Dimension(30, 20));
					jtf_value.setMaximumSize(new Dimension(30, 20));
					jtf_value.setPreferredSize(new Dimension(30, 20));			
					jcb_symbol.setSize(new Dimension(150, 20));
					jcb_symbol.setMaximumSize(new Dimension(150, 20));
					jcb_symbol.setPreferredSize(new Dimension(150, 20));
					vBox.add(hBoxAgg);									
					tab.addTab("Basics", null, vBox, "Basic settings");								
					Box vBox3 = Box.createVerticalBox();
					vBox3.add(tab);
					vBox3.add(Box.createVerticalStrut(20));					
					Box hBoxConfirm = Box.createHorizontalBox();
					JButton jbConfirm = new JButton("Confirm");
					jbConfirm.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							graph.getModel().beginUpdate();
							dialog.dispose();
							graph.getModel().endUpdate();
							graph.refresh();
						}
					});
					JButton jbCancel = new JButton("Cancel");
					jbCancel.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							graph.getModel().beginUpdate();
							graph.removeCells(cellToAdd);
							graph.getModel().endUpdate();
							graph.refresh();
							dialog.dispose();
						}
					});
					hBoxConfirm.add(jbConfirm);
					hBoxConfirm.add(Box.createHorizontalStrut(20));
					hBoxConfirm.add(jbCancel);

					hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);

					vBox3.add(hBoxConfirm);
					dialog.add(vBox3);
					dialog.setVisible(true);

					
					graph.getModel().endUpdate();
					graph.refresh();
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("activity")) {

					if (!((mxCell) cellToAdd[0]).getParent().getValue().equals("Activity")){
						graph.removeCells(cellToAdd);
					}
					else {
						System.out.println(((mxCell) cellToAdd[0]).getId());
						// add port to the element
						ArrayList<mxCell> ports = getActivityPorts();
						graph.addCell(ports.get(0), cellToAdd[0]);
						graph.addCell(ports.get(1), cellToAdd[0]);
						graph.addCell(ports.get(2), cellToAdd[0]);
						graph.addCell(ports.get(3), cellToAdd[0]);
						mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
						mw.updatePortParent(ports.get(1).getId(), (mxCell) cellToAdd[0]);
						mw.updatePortParent(ports.get(2).getId(), (mxCell) cellToAdd[0]);
						mw.updatePortParent(ports.get(3).getId(), (mxCell) cellToAdd[0]);
						mw.updatePortSemantic(ports.get(0).getId(), "before");
						mw.updatePortSemantic(ports.get(1).getId(), "after");
						mw.updatePortSemantic(ports.get(2).getId(), "during");
						mw.updatePortSemantic(ports.get(3).getId(), "during");
						((mxCell) cellToAdd[0]).setConnectable(false);
						setDialogForActivity(cellToAdd);
						graph.getModel().endUpdate();
						graph.refresh();
					}
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("objEntity")) {
					ArrayList<mxCell> ports = getEntityPorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.addCell(ports.get(1), cellToAdd[0]);
					graph.addCell(ports.get(2), cellToAdd[0]);
					graph.addCell(ports.get(3), cellToAdd[0]);
					((mxCell) cellToAdd[0]).setConnectable(false);
					((mxCell) cellToAdd[0]).setValue(((mxCell) cellToAdd[0]).getParent().getValue());
					graph.getModel().endUpdate();
					graph.refresh();
				}
				else if (((mxCell) cellToAdd[0]).getType().equals("time")) {
					// add port to the element
					ArrayList<mxCell> ports = getTimePorts();
					graph.addCell(ports.get(0), cellToAdd[0]);
					graph.addCell(ports.get(1), cellToAdd[0]);
					mw.updatePortParent(ports.get(0).getId(), (mxCell) cellToAdd[0]);
					mw.updatePortParent(ports.get(1).getId(), (mxCell) cellToAdd[0]);
					((mxCell) cellToAdd[0]).setConnectable(false);
					graph.getModel().endUpdate();
					graph.refresh();
				}
			}
			catch (Exception exception){

			}
			finally {
				return cellToAdd;
			}
		}

		public static ArrayList<mxCell> getTimePorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(-0.9, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"rectangle","timeAxis");
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(0.9, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"rectangle","timeAxis");
			port2.setVertex(true);

			ports.add(port1);
			ports.add(port2);
			return ports;
		}

		public void setDialogForActivity(Object [] cellToAdd) throws IOException {
			JDialog dialog = new JDialog();
			dialog.setModal(true);
			dialog.setTitle("Activity setting");
			dialog.setSize(new Dimension(500,400));
			dialog.setLocationRelativeTo(null);  // set to the center of the screen

			JTabbedPane tab = new JTabbedPane();

			Box vBox = Box.createVerticalBox();
			Box hBox1 = Box.createHorizontalBox();
			Box hBox2 = Box.createHorizontalBox();

			JComboBox<String> jcb = new JComboBox<String>();
			jcb.addItem("");
			for (String eachActivity: mw.activityList) {
				jcb.addItem(eachActivity);
			}

			jcb.setSize(new Dimension(200, 20));
			jcb.setMaximumSize(new Dimension(200, 20));
			jcb.setPreferredSize(new Dimension(200, 20));
			JLabel jb1 = new JLabel("Activity name: ");
			hBox1.add(jb1);

			hBox1.add(jcb);
			hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);

			vBox.add(Box.createVerticalStrut(20));

			vBox.add(hBox1);

			hBox2.setAlignmentX(Component.CENTER_ALIGNMENT);
			vBox.add(Box.createVerticalStrut(30));
			vBox.add(hBox2);

			tab.addTab("Basics", null, vBox, "Basic settings");

			Box vBox2 = Box.createVerticalBox();

			Box hBox_waiting = Box.createHorizontalBox();
			Box hBox_service = Box.createHorizontalBox();
			Box hBox_soujourn = Box.createHorizontalBox();
			Box hBox_flow = Box.createHorizontalBox();


			JRadioButton radioBtn01 = new JRadioButton();
			JRadioButton radioBtn02 = new JRadioButton();
			JRadioButton radioBtn03 = new JRadioButton();
			JRadioButton radioBtn04 = new JRadioButton();
			JRadioButton radioBtn05 = new JRadioButton();

			ButtonGroup btnGroup = new ButtonGroup();
			btnGroup.add(radioBtn01);
			btnGroup.add(radioBtn02);
			btnGroup.add(radioBtn03);
			btnGroup.add(radioBtn04);
			btnGroup.add(radioBtn05);

			JComboBox<String> jcb_waiting_symbol = new JComboBox<String>();
			for (String ts: timeSymbol) {
				jcb_waiting_symbol.addItem(ts);
			}

			JComboBox<String> jcb_waiting_time = new JComboBox<String>();
			for (String unit: timeUnit) {
				jcb_waiting_time.addItem(unit);
			}


			jcb_waiting_symbol.setSize(new Dimension(100, 20));
			jcb_waiting_symbol.setMaximumSize(new Dimension(100, 20));
			jcb_waiting_symbol.setPreferredSize(new Dimension(100, 20));
			jcb_waiting_time.setSize(new Dimension(100, 20));
			jcb_waiting_time.setMaximumSize(new Dimension(100, 20));
			jcb_waiting_time.setPreferredSize(new Dimension(100, 20));
			JLabel jlb_waiting = new JLabel("Waiting time:");
			jlb_waiting.setToolTipText("The time duration of the waiting time for the current activity");

			JTextField jtf_waiting = new JTextField();
			jtf_waiting.setSize(new Dimension(100, 20));
			jtf_waiting.setMaximumSize(new Dimension(100, 20));
			jtf_waiting.setPreferredSize(new Dimension(100, 20));

			hBox_waiting.add(radioBtn01);
			hBox_waiting.add(jlb_waiting);
			hBox_waiting.add(jcb_waiting_symbol);
			hBox_waiting.add(jtf_waiting);
			hBox_waiting.add(jcb_waiting_time);

			JComboBox<String> jcb_service_symbol = new JComboBox<String>();
			for (String ts: timeSymbol) {
				jcb_service_symbol.addItem(ts);
			}

			JComboBox<String> jcb_service_time = new JComboBox<String>();
			for (String unit: timeUnit) {
				jcb_service_time.addItem(unit);
			}

//			Image img = ImageIO.read(getClass().getResource("/com/mxgraph/examples/swing/images/question.jpg"));
//			JButton jb_service = new JButton(new ImageIcon(img));
//			jb_service.setSize(new Dimension(10, 10));
//			jb_service.setMaximumSize(new Dimension(10, 10));
//			jb_service.setPreferredSize(new Dimension(10, 10));

			jcb_service_symbol.setSize(new Dimension(100, 20));
			jcb_service_symbol.setMaximumSize(new Dimension(100, 20));
			jcb_service_symbol.setPreferredSize(new Dimension(100, 20));
			jcb_service_time.setSize(new Dimension(100, 20));
			jcb_service_time.setMaximumSize(new Dimension(100, 20));
			jcb_service_time.setPreferredSize(new Dimension(100, 20));
			JLabel jlb_service = new JLabel("Service time:");
			jlb_service.setToolTipText("The time duration of the current activity");
			JTextField jtf_service = new JTextField();
			jtf_service.setSize(new Dimension(100, 20));
			jtf_service.setMaximumSize(new Dimension(100, 20));
			jtf_service.setPreferredSize(new Dimension(100, 20));

			hBox_service.add(radioBtn02);
			hBox_service.add(jlb_service);
			hBox_service.add(jcb_service_symbol);
			hBox_service.add(jtf_service);
			hBox_service.add(jcb_service_time);

			JComboBox<String> jcb_soujourn_symbol = new JComboBox<String>();
			for (String ts: timeSymbol) {
				jcb_soujourn_symbol.addItem(ts);
			}

			JComboBox<String> jcb_soujourn_time = new JComboBox<String>();
			for (String unit: timeUnit) {
				jcb_soujourn_time.addItem(unit);
			}

			jcb_soujourn_symbol.setSize(new Dimension(80, 20));
			jcb_soujourn_symbol.setMaximumSize(new Dimension(80, 20));
			jcb_soujourn_symbol.setPreferredSize(new Dimension(80, 20));
			jcb_soujourn_time.setSize(new Dimension(100, 20));
			jcb_soujourn_time.setMaximumSize(new Dimension(100, 20));
			jcb_soujourn_time.setPreferredSize(new Dimension(100, 20));
			JLabel jlb_soujourn = new JLabel("Soujourn time:");
			jlb_soujourn.setToolTipText("The time duration from the end of previous activity to current activity");
			JTextField jtf_soujourn = new JTextField();
			jtf_soujourn.setSize(new Dimension(100, 20));
			jtf_soujourn.setMaximumSize(new Dimension(100, 20));
			jtf_soujourn.setPreferredSize(new Dimension(100, 20));

			hBox_soujourn.add(radioBtn03);
			hBox_soujourn.add(jlb_soujourn);
			hBox_soujourn.add(jcb_soujourn_symbol);
			hBox_soujourn.add(jtf_soujourn);
			hBox_soujourn.add(jcb_soujourn_time);


			JComboBox<String> jcb_flow_symbol = new JComboBox<String>();
			for (String ts: timeSymbol) {
				jcb_flow_symbol.addItem(ts);
			}

			JComboBox<String> jcb_flow_time = new JComboBox<String>();
			for (String unit: timeUnit) {
				jcb_flow_time.addItem(unit);
			}

			jcb_flow_symbol.setSize(new Dimension(100, 20));
			jcb_flow_symbol.setMaximumSize(new Dimension(100, 20));
			jcb_flow_symbol.setPreferredSize(new Dimension(100, 20));
			jcb_flow_time.setSize(new Dimension(100, 20));
			jcb_flow_time.setMaximumSize(new Dimension(100, 20));
			jcb_flow_time.setPreferredSize(new Dimension(100, 20));
			JLabel jlb_flow = new JLabel("Flow time:");
			jlb_flow.setToolTipText("The time duration from the end of current activity to the next activity");
			JTextField jtf_flow = new JTextField();
			jtf_flow.setSize(new Dimension(100, 20));
			jtf_flow.setMaximumSize(new Dimension(100, 20));
			jtf_flow.setPreferredSize(new Dimension(100, 20));

			hBox_flow.add(radioBtn04);
			hBox_flow.add(jlb_flow);
			hBox_flow.add(jcb_flow_symbol);
			hBox_flow.add(jtf_flow);
			hBox_flow.add(jcb_flow_time);

			Box hBox_count = Box.createHorizontalBox();
			JComboBox<String> jcb_count_symbol = new JComboBox<String>();
			for (String ts: timeSymbol) {
				jcb_count_symbol.addItem(ts);
			}
			jcb_count_symbol.addItem("=");

			jcb_count_symbol.setSize(new Dimension(100, 20));
			jcb_count_symbol.setMaximumSize(new Dimension(100, 20));
			jcb_count_symbol.setPreferredSize(new Dimension(100, 20));
			JLabel jlb_count = new JLabel("Activity count:");
			jlb_count.setToolTipText("The number of activity in the trace.");
			JTextField jtf_count = new JTextField();
			jtf_count.setSize(new Dimension(100, 20));
			jtf_count.setMaximumSize(new Dimension(100, 20));
			jtf_count.setPreferredSize(new Dimension(100, 20));
			hBox_count.add(radioBtn05);

			hBox_count.add(jlb_count);
			hBox_count.add(jcb_count_symbol);
			hBox_count.add(jtf_count);


			vBox2.add(Box.createVerticalStrut(10));
			vBox2.add(hBox_waiting);
			vBox2.add(Box.createVerticalStrut(30));
			vBox2.add(hBox_service);
			vBox2.add(Box.createVerticalStrut(30));
			vBox2.add(hBox_soujourn);
			vBox2.add(Box.createVerticalStrut(30));
			vBox2.add(hBox_flow);
			vBox2.add(Box.createVerticalStrut(30));
			vBox2.add(hBox_count);

			vBox2.setSize(new Dimension(480, 350));
			vBox2.setMaximumSize(new Dimension(480, 400));
			vBox2.setPreferredSize(new Dimension(480, 400));



			tab.addTab("Advanced", null, vBox2, "Advanced settings");

			Box vBox3 = Box.createVerticalBox();
			vBox3.add(tab);
			vBox3.add(Box.createVerticalStrut(20));
			vBox3.add(tab);

			Box hBoxConfirm = Box.createHorizontalBox();
			JButton jbConfirm = new JButton("Confirm");
			jbConfirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (jcb.getSelectedIndex() == 0){
						return;
					}
					graph.getModel().beginUpdate();
					String actSelected = String.valueOf(jcb.getSelectedItem());
					dialog.dispose();
					((mxCell) cellToAdd[0]).setValue(actSelected);
					graph.getModel().endUpdate();
					graph.refresh();
				}
			});
			JButton jbCancel = new JButton("Cancel");
			jbCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					graph.getModel().beginUpdate();
					graph.removeCells(cellToAdd);
					graph.getModel().endUpdate();
					graph.refresh();
					dialog.dispose();

				}
			});
			hBoxConfirm.add(jbConfirm);
			hBoxConfirm.add(Box.createHorizontalStrut(20));
			hBoxConfirm.add(jbCancel);

			hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);

			vBox3.add(hBoxConfirm);
			dialog.add(vBox3);
			dialog.setVisible(true);
		}

		public static ArrayList<mxCell> getStatePorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"beforePort;direction=west","beforePort", true);
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(1.0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"afterPort;direction=east","afterPort", true);
			port2.setVertex(true);

			mxGeometry geo3 = new mxGeometry(0.5, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo3.setRelative(true);

			mxCell port3 = new mxCell(null, geo3,
					"refPort;perimter=ellipsePerimeter","stateRef", true);
			port3.setVertex(true);

			mxGeometry geo4 = new mxGeometry(0.5, 1, PORT_DIAMETER,
					PORT_DIAMETER);
			geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo4.setRelative(true);

			mxCell port4 = new mxCell(null, geo4,
					"timeRefPort","stateDuring", true);
			port4.setVertex(true);
			ports.add(port1);
			ports.add(port2);
			ports.add(port3);
			ports.add(port4);

			return ports;
		}


		public static ArrayList<mxCell> getIdentifierPorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(0.5, 1, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);
			mxCell port1 = new mxCell(null, geo1,
					"actRefPort","portActRef", true);
			port1.setVertex(true);
			ports.add(port1);

			return ports;
		}

		public static ArrayList<mxCell> getActivityPorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(0.22, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"beforePort;direction=west","portTypeActBefore", true);
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(0.99, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"afterPort;direction=east","portTypeActAfter", true);
			port2.setVertex(true);

			mxGeometry geo3 = new mxGeometry(0.52, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo3.setRelative(true);

			mxCell port3 = new mxCell(null, geo3,
					"refPort;perimter=ellipsePerimeter","portActRef", true);
			port3.setVertex(true);

			mxGeometry geo4 = new mxGeometry(0.52, 0.95, PORT_DIAMETER,
					PORT_DIAMETER);
			geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo4.setRelative(true);

			mxCell port4 = new mxCell(null, geo4,
					"timeRefPort","actTime", true);
			port4.setVertex(true);
			ports.add(port1);
			ports.add(port2);
			ports.add(port3);
			ports.add(port4);

			return ports;
		}


		public static ArrayList<mxCell> getPeriodPorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"beforePort;direction=west","beforePort", true);
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(0.95, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"afterPort;direction=east", "afterPort", true);
			port2.setVertex(true);

			mxGeometry geo3 = new mxGeometry(0.9, 0.95, PORT_DIAMETER,
					PORT_DIAMETER);
			geo3.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geo3.setRelative(true);

			mxCell port3 = new mxCell(null, geo3,
					"timeRefPort;timeColor","startTime", true);
			port3.setVertex(true);

			mxGeometry geo4 = new mxGeometry(0.1, 0.95, PORT_DIAMETER,
					PORT_DIAMETER);
			geo4.setOffset(new mxPoint(-PORT_RADIUS, 0));
			geo4.setRelative(true);

			mxCell port4 = new mxCell(null, geo4,
					"timeRefPort;timeColor","endTime", true);
			port4.setVertex(true);
			ports.add(port1);
			ports.add(port2);
			ports.add(port3);
			ports.add(port4);

			return ports;
		}


		public static ArrayList<mxCell> getEntityPorts(){
			int PORT_DIAMETER = 10;

			int PORT_RADIUS = PORT_DIAMETER / 2;

			ArrayList<mxCell> ports = new ArrayList<>();

			mxGeometry geo1 = new mxGeometry(0.5, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"refPort","objRef", true);
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(0.5, 1, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"refPort","objRef", true);
			port2.setVertex(true);

			mxGeometry geo3 = new mxGeometry(0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo3.setRelative(true);

			mxCell port3 = new mxCell(null, geo3,
					"refPort","objRef", true);
			port3.setVertex(true);

			mxGeometry geo4 = new mxGeometry(1,0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo4.setRelative(true);

			mxCell port4 = new mxCell(null, geo4,
					"refPort","objRef", true);
			port4.setVertex(true);

			ports.add(port1);
			ports.add(port2);
			ports.add(port3);
			ports.add(port4);
			return ports;
		}

	}

	/**
	 * A graph that creates new edges from a given template edge.
	 */
	public static class CustomGraph extends mxGraph
	{
		/**
		 * Holds the edge to be used as a template for inserting new edges.
		 */
		protected Object edgeTemplate;

		/**
		 * Custom graph that defines the alternate edge style to be used when
		 * the middle control point of edges is double clicked (flipped).
		 */
		public CustomGraph()
		{
			setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
		}

		/**
		 * Sets the edge template to be used to inserting edges.
		 */
		public void setEdgeTemplate(Object template)
		{
			edgeTemplate = template;
		}

		/**
		 * Prints out some useful information about the cell in the tooltip.
		 */
		public String getToolTipForCell(Object cell)
		{
//			String tip = "<html>";
//			mxGeometry geo = getModel().getGeometry(cell);
//			mxCellState state = getView().getState(cell);
//
//			if (getModel().isEdge(cell))
//			{
//				tip += "points={";
//
//				if (geo != null)
//				{
//					List<mxPoint> points = geo.getPoints();
//
//					if (points != null)
//					{
//						Iterator<mxPoint> it = points.iterator();
//
//						while (it.hasNext())
//						{
//							mxPoint point = it.next();
//							tip += "[x=" + numberFormat.format(point.getX())
//									+ ",y=" + numberFormat.format(point.getY())
//									+ "],";
//						}
//
//						tip = tip.substring(0, tip.length() - 1);
//					}
//				}
//
//				tip += "}<br>";
//				tip += "absPoints={";
//
//				if (state != null)
//				{
//
//					for (int i = 0; i < state.getAbsolutePointCount(); i++)
//					{
//						mxPoint point = state.getAbsolutePoint(i);
//						tip += "[x=" + numberFormat.format(point.getX())
//								+ ",y=" + numberFormat.format(point.getY())
//								+ "],";
//					}
//
//					tip = tip.substring(0, tip.length() - 1);
//				}
//
//				tip += "}";
//			}
//			else
//			{
//				tip += "geo=[";
//
//				if (geo != null)
//				{
//					tip += "x=" + numberFormat.format(geo.getX()) + ",y="
//							+ numberFormat.format(geo.getY()) + ",width="
//							+ numberFormat.format(geo.getWidth()) + ",height="
//							+ numberFormat.format(geo.getHeight());
//				}
//
//				tip += "]<br>";
//				tip += "state=[";
//
//				if (state != null)
//				{
//					tip += "x=" + numberFormat.format(state.getX()) + ",y="
//							+ numberFormat.format(state.getY()) + ",width="
//							+ numberFormat.format(state.getWidth())
//							+ ",height="
//							+ numberFormat.format(state.getHeight());
//				}
//
//				tip += "]";
//			}
//
//			mxPoint trans = getView().getTranslate();
//
//			tip += "<br>scale=" + numberFormat.format(getView().getScale())
//					+ ", translate=[x=" + numberFormat.format(trans.getX())
//					+ ",y=" + numberFormat.format(trans.getY()) + "]";
//			tip += "</html>";

			return ((mxCell)cell).getValue().toString();
		}

		/**
		 * Overrides the method to use the currently selected edge template for
		 * new edges.
		 * 
		 * @param graph
		 * @param parent
		 * @param id
		 * @param value
		 * @param source
		 * @param target
		 * @param style
		 * @return
		 */
		public Object createEdge(Object parent, String id, Object value,
				Object source, Object target, String style)
		{
			if (edgeTemplate != null)
			{
				mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
				edge.setId(id);

				return edge;
			}

			return super.createEdge(parent, id, value, source, target, style);
		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.WHITE;
		mxConstants.W3C_SHADOWCOLOR = "#000000";

		GraphEditor editor = new GraphEditor();
		editor.createFrame().setVisible(true);

//		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}
}
