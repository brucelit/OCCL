/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package com.mxgraph.examples.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

import com.mxgraph.model.*;
import org.w3c.dom.Document;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class GraphEditor extends BasicGraphEditor
{
	static MiddleWare mw = MiddleWare.getInstance();

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

	//GraphEditor.class.getResource("/com/mxgraph/examples/swing/images/connector.gif");

	public GraphEditor()
	{
		this("mxGraph Editor", new CustomGraphComponent(new CustomGraph()));
	}

	/**
	 * 
	 */
	public GraphEditor(String appTitle, mxGraphComponent component) {
		super(appTitle, component);
		final mxGraph graph = graphComponent.getGraph();
//		Object parent = graph.getDefaultParent();
//		graph.getModel().beginUpdate();
//
//		Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
//				30);
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
						"Entity",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/entity1.png")),
						"image;verticalAlign=middle;align=center;image=/com/mxgraph/examples/swing/images/entity1.png", 80, 80, null, "objEntity");
		objectPalette
				.addTemplate(
						"Identifier",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/actid.png")),
						"image;image=/com/mxgraph/examples/swing/images/activity_id.png", 80, 80, null, "identifier");

		objectPalette
				.addTemplate(
						"Period",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/objPeriod.png")),
						"image;image=/com/mxgraph/examples/swing/images/objPeriod2.png", 200, 80, null, "objPeriod");
		objectPalette
				.addTemplate(
						"State",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/objState.png")),
						"image;image=/com/mxgraph/examples/swing/images/objState.png", 80, 80, null, "objState");
		objectPalette
				.addTemplate(
						"Activity",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/activity1.png")),
						"image;verticalAlign=middle;image=/com/mxgraph/examples/swing/images/activity1.png", 160, 80,
						"", "activity");
		objectPalette
				.addTemplate(
						"Time",
						new ImageIcon(
								GraphEditor.class
										.getResource("/com/mxgraph/examples/swing/images/timer.png")),
						"image;image=/com/mxgraph/examples/swing/images/time.png", 700, 70, null, "time");
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
						JDialog dialog = new JDialog();
						dialog.setModal(true);
						dialog.setTitle("Activity Name");
						dialog.setSize(new Dimension(150, 150));
						dialog.setLocationRelativeTo(null);  // set to the center of the screen

						Box vBox = Box.createVerticalBox();
						Box hBox1 = Box.createHorizontalBox();
						Box hBox2 = Box.createHorizontalBox();

						JComboBox<String> jcb = new JComboBox<String>();
						jcb.addItem("lst1");
						jcb.addItem("lst2");
						jcb.addItem("lst3");
						jcb.addItem("lst4");
						jcb.addItem("lst5");
						jcb.addItem("lst6");
						jcb.setSize(new Dimension(100, 20));
						jcb.setMaximumSize(new Dimension(100, 20));
						jcb.setPreferredSize(new Dimension(100, 20));
						hBox1.add(jcb);
						hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);

						vBox.add(Box.createVerticalStrut(20));

						vBox.add(hBox1);
						JButton jbConfirm = new JButton("Confirm");
						jbConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								graph.getModel().beginUpdate();
								String actSelected = String.valueOf(jcb.getSelectedItem());
								dialog.dispose();
								((mxCell) cellToAdd[0]).setValue(actSelected);
								graph.getModel().endUpdate();
								graph.refresh();
							}
						});
						hBox2.add(jbConfirm);
						hBox2.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(30));
						vBox.add(hBox2);
						dialog.add(vBox);
						dialog.setVisible(true);

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
					"actRefPort","objIdPort", true);
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
					"beforePort;direction=west","beforePort", true);
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(0.99, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"afterPort;direction=east","afterPort", true);
			port2.setVertex(true);

			mxGeometry geo3 = new mxGeometry(0.52, 0, PORT_DIAMETER,
					PORT_DIAMETER);
			geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo3.setRelative(true);

			mxCell port3 = new mxCell(null, geo3,
					"refPort;perimter=ellipsePerimeter","actToObjRef", true);
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
			String tip = "<html>";
			mxGeometry geo = getModel().getGeometry(cell);
			mxCellState state = getView().getState(cell);

			if (getModel().isEdge(cell))
			{
				tip += "points={";

				if (geo != null)
				{
					List<mxPoint> points = geo.getPoints();

					if (points != null)
					{
						Iterator<mxPoint> it = points.iterator();

						while (it.hasNext())
						{
							mxPoint point = it.next();
							tip += "[x=" + numberFormat.format(point.getX())
									+ ",y=" + numberFormat.format(point.getY())
									+ "],";
						}

						tip = tip.substring(0, tip.length() - 1);
					}
				}

				tip += "}<br>";
				tip += "absPoints={";

				if (state != null)
				{

					for (int i = 0; i < state.getAbsolutePointCount(); i++)
					{
						mxPoint point = state.getAbsolutePoint(i);
						tip += "[x=" + numberFormat.format(point.getX())
								+ ",y=" + numberFormat.format(point.getY())
								+ "],";
					}

					tip = tip.substring(0, tip.length() - 1);
				}

				tip += "}";
			}
			else
			{
				tip += "geo=[";

				if (geo != null)
				{
					tip += "x=" + numberFormat.format(geo.getX()) + ",y="
							+ numberFormat.format(geo.getY()) + ",width="
							+ numberFormat.format(geo.getWidth()) + ",height="
							+ numberFormat.format(geo.getHeight());
				}

				tip += "]<br>";
				tip += "state=[";

				if (state != null)
				{
					tip += "x=" + numberFormat.format(state.getX()) + ",y="
							+ numberFormat.format(state.getY()) + ",width="
							+ numberFormat.format(state.getWidth())
							+ ",height="
							+ numberFormat.format(state.getHeight());
				}

				tip += "]";
			}

			mxPoint trans = getView().getTranslate();

			tip += "<br>scale=" + numberFormat.format(getView().getScale())
					+ ", translate=[x=" + numberFormat.format(trans.getX())
					+ ",y=" + numberFormat.format(trans.getY()) + "]";
			tip += "</html>";

			return tip;
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

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		GraphEditor editor = new GraphEditor();
		editor.createFrame().setVisible(true);

//		editor.createFrame(new EditorMenuBar(editor)).setVisible(true);
	}
}
