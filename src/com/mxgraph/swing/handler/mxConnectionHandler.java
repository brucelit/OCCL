/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.handler;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import com.mxgraph.examples.swing.editor.MiddleWare;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphComponent.mxGraphControl;
import com.mxgraph.swing.util.mxMouseAdapter;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

/**
 * Connection handler creates new connections between cells. This control is used to display the connector
 * icon, while the preview is used to draw the line.
 * 
 * mxEvent.CONNECT fires between begin- and endUpdate in mouseReleased. The <code>cell</code>
 * property contains the inserted edge, the <code>event</code> and <code>target</code> 
 * properties contain the respective arguments that were passed to mouseReleased.
 */
public class mxConnectionHandler extends mxMouseAdapter
{

	MiddleWare mw = MiddleWare.getInstance();

	private String refElement;
	private String targetElement;

	private String refPortType;
	private String targetPortType;
	/**
	 *
	 */
	private String selectedConnType;

	private String refParentValue;
	private String targetParentValue;

	public enum connType{
		connTimeRef,  // time reference to timeline
		connBeforeAfter,  // every before/after connection
		connObjToActRef,  // object type as activity reference type
		connObjToActCard,  // object to activity cardinality type
		connObjToObjCard,  // object to object cardinality type
		connActToObjId // activity to object id type
	}

	protected static java.util.List<String> timeSymbol = Arrays.asList(
			"",
			">=",
			">",
			"<=",
			"<");

	protected static List<String> timeUnit = Arrays.asList(
			"",
			"seconds",
			"minutes",
			"hours",
			"days",
			"months",
			"years");
	/**
	 * 
	 */
	private static final long serialVersionUID = -2543899557644889853L;

	/**
	 * 
	 */
	public static Cursor CONNECT_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * Holds the event source.
	 */
	protected mxEventSource eventSource = new mxEventSource(this);

	/**
	 * 
	 */
	protected mxConnectPreview connectPreview;

	/**
	 * Specifies the icon to be used for creating new connections. If this is
	 * specified then it is used instead of the handle. Default is null.
	 */
	protected ImageIcon connectIcon = null;

	/**
	 * Specifies the size of the handle to be used for creating new
	 * connections. Default is mxConstants.CONNECT_HANDLE_SIZE. 
	 */
	protected int handleSize = mxConstants.CONNECT_HANDLE_SIZE;

	/**
	 * Specifies if a handle should be used for creating new connections. This
	 * is only used if no connectIcon is specified. If this is false, then the
	 * source cell will be highlighted when the mouse is over the hotspot given
	 * in the marker. Default is mxConstants.CONNECT_HANDLE_ENABLED.
	 */
	protected boolean handleEnabled = mxConstants.CONNECT_HANDLE_ENABLED;

	/**
	 * 
	 */
	protected boolean select = true;

	/**
	 * Specifies if the source should be cloned and used as a target if no
	 * target was selected. Default is false.
	 */
	protected boolean createTarget = false;

	/**
	 * Appearance and event handling order wrt subhandles.
	 */
	protected boolean keepOnTop = true;

	/**
	 * 
	 */
	protected boolean enabled = true;

	/**
	 * 
	 */
	protected transient Point first;

	/**
	 * 
	 */
	protected transient boolean active = false;

	/**
	 * 
	 */
	protected transient Rectangle bounds;

	/**
	 * 
	 */
	protected transient mxCellState source;

	/**
	 * 
	 */
	protected transient mxCellMarker marker;

	/**
	 * 
	 */
	protected transient String error;

	/**
	 * 
	 */
	protected transient mxIEventListener resetHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			reset();
		}
	};

	/**
	 * 
	 * @param graphComponent
	 */
	public mxConnectionHandler(mxGraphComponent graphComponent)
	{
		this.graphComponent = graphComponent;

		// Installs the paint handler
		graphComponent.addListener(mxEvent.AFTER_PAINT, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Graphics g = (Graphics) evt.getProperty("g");
				paint(g);
			}
		});

		connectPreview = createConnectPreview();

		mxGraphControl graphControl = graphComponent.getGraphControl();
		graphControl.addMouseListener(this);
		graphControl.addMouseMotionListener(this);

		// Installs the graph listeners and keeps them in sync
		addGraphListeners(graphComponent.getGraph());

		graphComponent.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("graph"))
				{
					removeGraphListeners((mxGraph) evt.getOldValue());
					addGraphListeners((mxGraph) evt.getNewValue());
				}
			}
		});

		marker = new mxCellMarker(graphComponent)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 103433247310526381L;

			// Overrides to return cell at location only if valid (so that
			// there is no highlight for invalid cells that have no error
			// message when the mouse is released)
			protected Object getCell(MouseEvent e)
			{
				Object cell = super.getCell(e);

				if (isConnecting())
				{
					if (source != null)
					{
						error = validateConnection(source.getCell(), cell);

						if (error != null && error.length() == 0)
						{
							cell = null;

							// Enables create target inside groups
							if (createTarget)
							{
								error = null;
							}
						}
					}
				}
				else if (!isValidSource(cell))
				{
					cell = null;
				}

				return cell;
			}

			// Sets the highlight color according to isValidConnection
			protected boolean isValidState(mxCellState state)
			{
				if (isConnecting())
				{
					return error == null;
				}
				else
				{
					return super.isValidState(state);
				}
			}

			// Overrides to use marker color only in highlight mode or for
			// target selection
			protected Color getMarkerColor(MouseEvent e, mxCellState state,
					boolean isValid)
			{
				return (isHighlighting() || isConnecting()) ? super
						.getMarkerColor(e, state, isValid) : null;
			}

			// Overrides to use hotspot only for source selection otherwise
			// intersects always returns true when over a cell
			protected boolean intersects(mxCellState state, MouseEvent e)
			{
				if (!isHighlighting() || isConnecting())
				{
					return true;
				}

				return super.intersects(state, e);
			}
		};

		marker.setHotspotEnabled(true);
	}

	/**
	 * Installs the listeners to update the handles after any changes.
	 */
	protected void addGraphListeners(mxGraph graph)
	{
		// LATER: Install change listener for graph model, view
		if (graph != null)
		{
			mxGraphView view = graph.getView();
			view.addListener(mxEvent.SCALE, resetHandler);
			view.addListener(mxEvent.TRANSLATE, resetHandler);
			view.addListener(mxEvent.SCALE_AND_TRANSLATE, resetHandler);

			graph.getModel().addListener(mxEvent.CHANGE, resetHandler);
		}
	}

	/**
	 * Removes all installed listeners.
	 */
	protected void removeGraphListeners(mxGraph graph)
	{
		if (graph != null)
		{
			mxGraphView view = graph.getView();
			view.removeListener(resetHandler, mxEvent.SCALE);
			view.removeListener(resetHandler, mxEvent.TRANSLATE);
			view.removeListener(resetHandler, mxEvent.SCALE_AND_TRANSLATE);

			graph.getModel().removeListener(resetHandler, mxEvent.CHANGE);
		}
	}

	/**
	 * 
	 */
	protected mxConnectPreview createConnectPreview()
	{
		return new mxConnectPreview(graphComponent);
	}

	/**
	 * 
	 */
	public mxConnectPreview getConnectPreview()
	{
		return connectPreview;
	}

	/**
	 * 
	 */
	public void setConnectPreview(mxConnectPreview value)
	{
		connectPreview = value;
	}

	/**
	 * Returns true if the source terminal has been clicked and a new
	 * connection is currently being previewed.
	 */
	public boolean isConnecting()
	{
		return connectPreview.isActive();
	}

	/**
	 * 
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Returns true if no connectIcon is specified and handleEnabled is false.
	 */
	public boolean isHighlighting()
	{
		return connectIcon == null && !handleEnabled;
	}

	/**
	 * 
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean value)
	{
		enabled = value;
	}

	/**
	 * 
	 */
	public boolean isKeepOnTop()
	{
		return keepOnTop;
	}

	/**
	 * 
	 */
	public void setKeepOnTop(boolean value)
	{
		keepOnTop = value;
	}

	/**
	 * 
	 */
	public void setConnectIcon(ImageIcon value)
	{
		connectIcon = value;
	}

	/**
	 * 
	 */
	public ImageIcon getConnecIcon()
	{
		return connectIcon;
	}

	/**
	 * 
	 */
	public void setHandleEnabled(boolean value)
	{
		handleEnabled = value;
	}

	/**
	 * 
	 */
	public boolean isHandleEnabled()
	{
		return handleEnabled;
	}

	/**
	 * 
	 */
	public void setHandleSize(int value)
	{
		handleSize = value;
	}

	/**
	 * 
	 */
	public int getHandleSize()
	{
		return handleSize;
	}

	/**
	 * 
	 */
	public mxCellMarker getMarker()
	{
		return marker;
	}

	/**
	 * 
	 */
	public void setMarker(mxCellMarker value)
	{
		marker = value;
	}

	/**
	 * 
	 */
	public void setCreateTarget(boolean value)
	{
		createTarget = value;
	}

	/**
	 * 
	 */
	public boolean isCreateTarget()
	{
		return createTarget;
	}

	/**
	 * 
	 */
	public void setSelect(boolean value)
	{
		select = value;
	}

	/**
	 * 
	 */
	public boolean isSelect()
	{
		return select;
	}

	/**
	 * 
	 */
	public void reset()
	{
		connectPreview.stop(false);
		setBounds(null);
		marker.reset();
		active = false;
		source = null;
		first = null;
		error = null;
	}

	/**
	 * 
	 */
	public Object createTargetVertex(MouseEvent e, Object source)
	{
		mxGraph graph = graphComponent.getGraph();
		Object clone = graph.cloneCells(new Object[] { source })[0];
		mxIGraphModel model = graph.getModel();
		mxGeometry geo = model.getGeometry(clone);

		if (geo != null)
		{
			mxPoint point = graphComponent.getPointForEvent(e);
			geo.setX(graph.snap(point.getX() - geo.getWidth() / 2));
			geo.setY(graph.snap(point.getY() - geo.getHeight() / 2));
		}

		return clone;
	}

	/**
	 * 
	 */
	public boolean isValidSource(Object cell)
	{
		return graphComponent.getGraph().isValidSource(cell);
	}

	/**
	 * Returns true. The call to mxGraph.isValidTarget is implicit by calling
	 * mxGraph.getEdgeValidationError in validateConnection. This is an
	 * additional hook for disabling certain targets in this specific handler.
	 */
	public boolean isValidTarget(Object cell)
	{
		return true;
	}

	/**
	 * Returns the error message or an empty string if the connection for the
	 * given source target pair is not valid. Otherwise it returns null.
	 */
	public String validateConnection(Object source, Object target)
	{
		if (target == null && createTarget)
		{
			return null;
		}

		if (!isValidTarget(target))
		{
			return "";
		}

		return graphComponent.getGraph().getEdgeValidationError(
				connectPreview.getPreviewState().getCell(), source, target);
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!graphComponent.isForceMarqueeEvent(e)
				&& !graphComponent.isPanningEvent(e)
				&& !e.isPopupTrigger()
				&& graphComponent.isEnabled()
				&& isEnabled()
				&& !e.isConsumed()
				&& ((isHighlighting() && marker.hasValidState()) || (!isHighlighting()
						&& bounds != null && bounds.contains(e.getPoint()))))
		{
			start(e, marker.getValidState());
			e.consume();
		}
	}

	/**
	 * 
	 */
	public void start(MouseEvent e, mxCellState state)
	{
		try {
			mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(),e.getY());
			refParentValue = (String) c1.getParent().getValue();

			refPortType = c1.getType();
			System.out.println(c1 + "\n conn start:" + c1.getId());
			first = e.getPoint();
			connectPreview.start(e, state, "");
			if (!mw.getPortParent().containsKey(c1.getId())) {
				return;
			}
			mw.setSrcPortId(c1.getId());
			refElement = ((mxCell) (c1.getParent())).getType();
			mw.setSrcPortParentId(c1.getParent().getId());

		}
		catch (Exception exception){

		}
		finally {

		}

	}

	/**
	 * 
	 */
	public void mouseMoved(MouseEvent e)
	{
		try {

			mouseDragged(e);

			if (isHighlighting() && !marker.hasValidState()) {
				source = null;
			}

			if (!isHighlighting() && source != null) {
				int imgWidth = handleSize;
				int imgHeight = handleSize;

				if (connectIcon != null) {
					imgWidth = connectIcon.getIconWidth();
					imgHeight = connectIcon.getIconHeight();
				}

				int x = (int) source.getCenterX() - imgWidth / 2;
				int y = (int) source.getCenterY() - imgHeight / 2;

				if (graphComponent.getGraph().isSwimlane(source.getCell())) {
					mxRectangle size = graphComponent.getGraph().getStartSize(
							source.getCell());

					if (size.getWidth() > 0) {
						x = (int) (source.getX() + size.getWidth() / 2 - imgWidth / 2);
					} else {
						y = (int) (source.getY() + size.getHeight() / 2 - imgHeight / 2);
					}
				}

				setBounds(new Rectangle(x, y, imgWidth, imgHeight));
			} else {
				setBounds(null);
			}

			if (source != null && (bounds == null || bounds.contains(e.getPoint()))) {
				graphComponent.getGraphControl().setCursor(CONNECT_CURSOR);
				e.consume();
			}
		}
		catch (Exception exception){

		}
	}

	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!e.isConsumed() && graphComponent.isEnabled() && isEnabled())
		{
			// Activates the handler
			if (!active && first != null)
			{
				double dx = Math.abs(first.getX() - e.getX());
				double dy = Math.abs(first.getY() - e.getY());
				int tol = graphComponent.getTolerance();
				
				if (dx > tol || dy > tol)
				{
					active = true;
				}
			}
			
			if (e.getButton() == 0 || (isActive() && connectPreview.isActive()))
			{
				mxCellState state = marker.process(e);
	
				if (connectPreview.isActive())
				{
					connectPreview.update(e, marker.getValidState(), e.getX(),
							e.getY());
					setBounds(null);
					e.consume();
				}
				else
				{
					source = state;
				}
			}
		}
	}

	/**
	 *
	 */
	public void mouseReleased(MouseEvent e)
	{

		mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
		if (!c1.isPort()){
			return;
		}
		targetPortType = c1.getType();
		System.out.println(targetPortType+refPortType);
		targetParentValue = (String) c1.getParent().getValue();

		if (isActive())
		{

			if (error != null)
			{
				if (error.length() > 0)
				{
					JOptionPane.showMessageDialog(graphComponent, error);
				}
			}
			else if (first != null)
			{
				mxGraph graph = graphComponent.getGraph();
				double dx = first.getX() - e.getX();
				double dy = first.getY() - e.getY();

				if (connectPreview.isActive()
						&& (marker.hasValidState() || isCreateTarget() || graph
						.isAllowDanglingEdges()))
				{
					graph.getModel().beginUpdate();

					try
					{
						Object dropTarget = null;

						if (!marker.hasValidState() && isCreateTarget())
						{
							Object vertex = createTargetVertex(e, source.getCell());
							dropTarget = graph.getDropTarget(
									new Object[] { vertex }, e.getPoint(),
									graphComponent.getCellAt(e.getX(), e.getY()));

							if (vertex != null)
							{
								// Disables edges as drop targets if the target cell was created
								if (dropTarget == null
										|| !graph.getModel().isEdge(dropTarget))
								{
									mxCellState pstate = graph.getView().getState(
											dropTarget);

									if (pstate != null)
									{
										mxGeometry geo = graph.getModel()
												.getGeometry(vertex);

										mxPoint origin = pstate.getOrigin();
										geo.setX(geo.getX() - origin.getX());
										geo.setY(geo.getY() - origin.getY());
									}
								}
								else
								{
									dropTarget = graph.getDefaultParent();
								}

								graph.addCells(new Object[] { vertex }, dropTarget);
							}

							// FIXME: Here we pre-create the state for the vertex to be
							// inserted in order to invoke update in the connectPreview.
							// This means we have a cell state which should be created
							// after the model.update, so this should be fixed.
							mxCellState targetState = graph.getView().getState(
									vertex, true);
							connectPreview.update(e, targetState, e.getX(),
									e.getY());
						}

						Object cell = connectPreview.stop(
								graphComponent.isSignificant(dx, dy), e);
						if (cell != null)
						{
							graphComponent.getGraph().setSelectionCell(cell);
							if (targetPortType.equals("refObjForAct") || refPortType.equals("refObjForAct")){					// set edge style
									connType ct = connType.connObjToActRef;
									((mxCell) cell).setStyle("doubleEdge");
								}
							else if (targetPortType.equals("timeRef") || refPortType.equals("timeRef")){					// set edge style
								connType ct = connType.connTimeRef;
								((mxCell) cell).setStyle("timeRefEdge");
							}
							else if (targetPortType.equals("objRef") || refPortType.equals("objRef")){					// set edge style
								connType ct = connType.connObjToObjCard;
								((mxCell) cell).setStyle("doubleEdge");
							}

							// this is the before/after connection type
							else if ((targetPortType.equals("portTypeActBefore") && refPortType.equals("portTypeActAfter"))
							|| (targetPortType.equals("portTypeActAfter") && refPortType.equals("portTypeActBefore"))){
								connType ct = connType.connBeforeAfter;
								((mxCell) cell).setStyle("beforeAfterEdge");
							}

							// this is the activity to object id type connection type
							else if ((targetPortType.equals("actToObjRef") && refPortType.equals("objIdPort"))
									|| (targetPortType.equals("objIdPort") && refPortType.equals("actToObjRef"))){
								connType ct = connType.connActToObjId;
								((mxCell) cell).setStyle("doubleEdge");
								((mxCell) cell).setType("connActToObjId");
							}
							else if (refPortType.equals("portActRef") && targetPortType.equals("portActRef")) {
								connType ct = connType.connActToObjId;
								((mxCell) cell).setStyle("doubleEdge");
								((mxCell) cell).setType("connActToObjId");
							}
							else {
								return;
							}
							eventSource.fireEvent(new mxEventObject(
									mxEvent.CONNECT, "cell", cell, "event", e,
									"target", dropTarget));
						}

						e.consume();

						if (refPortType.equals("portActRef") && targetPortType.equals("portActRef")) {
							// set up a dialog for the connection
							mw.actRefObj = (String) c1.getParent().getParent().getValue();
						}

						// obj to obj state ordering
						else if ((refPortType.equals("portTypeObjBefore") && targetPortType.equals("portTypeObjAfter"))
								|| refPortType.equals("portTypeObjAfter") && targetPortType.equals("portTypeObjBefore")) {
							// set up a dialog for the activity ordering relation
							mw.refParentValue = refParentValue;
							mw.targetParentValue = targetParentValue;
							mw.connType = "objStateToStateOrder";
							setDialogForObjToObjOrder(refParentValue, targetParentValue);
						}

						// act to act state ordering
						else if ((refPortType.equals("portTypeActBefore") && targetPortType.equals("portTypeActAfter"))
								|| refPortType.equals("portTypeActAfter") && targetPortType.equals("portTypeActBefore")) {
							// set up a dialog for the activity ordering relation
							mw.refParentValue = refParentValue;
							mw.targetParentValue = targetParentValue;
							mw.connType = "actToActOrder";
							setDialogForActToActOrder(refParentValue, targetParentValue);
						}

						// act to obj state ordering
						else if ((refPortType.equals("portTypeActBefore") && targetPortType.equals("portTypeObjAfter"))
								|| refPortType.equals("portTypeActAfter") && targetPortType.equals("portTypeObjBefore")) {
							// set up a dialog for the activity ordering relation
							mw.refParentValue = refParentValue;
							mw.targetParentValue = targetParentValue;
							mw.connType = "actToObjStateOrder";
							setDialogForActToActOrder(refParentValue, targetParentValue);
						}

						// act to obj period ordering
//						else if ((refPortType.equals("portTypeActBefore") && targetPortType.equals("portTypeObjAfter"))
//								|| refPortType.equals("portTypeActAfter") && targetPortType.equals("portTypeObjBefore")) {
//							// set up a dialog for the activity ordering relation
//							mw.refParentValue = refParentValue;
//							mw.targetParentValue = targetParentValue;
//							mw.connType = "actToObjStateOrder";
//							setDialogForActToActOrder(refParentValue, targetParentValue);
//						}
						else{
							return;
						}
					}
					finally
					{

						graph.getModel().endUpdate();
						graph.refresh();

					}
				}
			}
		}

		reset();
	}

	/**
	 * activity to activity order
	 */
	public void setDialogForActToActOrder(String refObj, String targetObj){
		JDialog dialog = new JDialog();
		dialog.setTitle("Activity order setting");
		dialog.setSize(new Dimension(500,250));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		JTabbedPane tab = new JTabbedPane();
		Box vBox = Box.createVerticalBox();
		Box infoBox = Box.createHorizontalBox();

		JButton jb1 = new JButton(refObj);
		jb1.setEnabled(false);
		JButton jb2 = new JButton(targetObj);
		jb2.setEnabled(false);
		Box hBox1 = Box.createHorizontalBox();

		JComboBox<String> jcb = new JComboBox<String>();
		jcb.addItem("direct after");
		jcb.addItem("response");
		jcb.addItem("unary response");
		jcb.addItem("----------------");
		jcb.addItem("not direct after");
		jcb.addItem("not response");
		jcb.setSize(new Dimension(120,20));
		jcb.setMaximumSize(new Dimension(120,20));
		jcb.setPreferredSize(new Dimension(120,20));

		hBox1.add(jcb);
		hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);

		// info box that contain two object
		infoBox.add(jb1);

		JLabel jl1 = new JLabel("———————————");
		jl1.setSize(new Dimension(140,20));
		jl1.setMinimumSize(new Dimension(140,20));
		jl1.setMaximumSize(new Dimension(140,20));
		jl1.setPreferredSize(new Dimension(140,20));
		infoBox.add(jl1);
		infoBox.add(jb2);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				mw.objStateToStateRelation = (String) jcb.getSelectedItem();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(infoBox);
		tab.addTab("Basics", null, vBox, "Basic setting");

		Box vBox2 = Box.createVerticalBox();

		JComboBox<String> jcb_waiting_symbol = new JComboBox<String>();
		for (String ts: timeSymbol) {
			jcb_waiting_symbol.addItem(ts);
		}
		jcb_waiting_symbol.addItem("=");

		JComboBox<String> jcb_waiting_time = new JComboBox<String>();
		for (String unit: timeUnit) {
			jcb_waiting_time.addItem(unit);
		}

		Box hBox_waiting = Box.createHorizontalBox();
		jcb_waiting_symbol.setSize(new Dimension(50, 20));
		jcb_waiting_symbol.setMaximumSize(new Dimension(50, 20));
		jcb_waiting_symbol.setPreferredSize(new Dimension(50, 20));
		jcb_waiting_time.setSize(new Dimension(70, 20));
		jcb_waiting_time.setMaximumSize(new Dimension(70, 20));
		jcb_waiting_time.setPreferredSize(new Dimension(70, 20));
		JLabel jlb_waiting = new JLabel("time gap:");
		JTextField jtf_waiting = new JTextField();
		jtf_waiting.setSize(new Dimension(30, 20));
		jtf_waiting.setMaximumSize(new Dimension(30, 20));
		jtf_waiting.setPreferredSize(new Dimension(30, 20));
		hBox_waiting.add(jlb_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_symbol);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_time);
		vBox2.add(Box.createVerticalStrut(20));
		vBox2.add(hBox_waiting);

		tab.addTab("Advanced", null, vBox2, "Advanced setting");
		Box vBox3 = Box.createVerticalBox();

		vBox3.add(tab);
		Box hBoxConfirm = Box.createHorizontalBox();
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.add(Box.createHorizontalStrut(20));
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox3.add(hBoxConfirm);
		// add vBox3 to dialog
		dialog.add(vBox3);
	}

	public void setDialogForActToObjOrder(String refObj, String targetObj){
		JDialog dialog = new JDialog();
		dialog.setTitle("Activity-Object order setting");
		dialog.setSize(new Dimension(500,250));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		JTabbedPane tab = new JTabbedPane();
		Box vBox = Box.createVerticalBox();
		Box infoBox = Box.createHorizontalBox();

		JButton jb1 = new JButton(refObj);
		jb1.setEnabled(false);
		JButton jb2 = new JButton(targetObj);
		jb2.setEnabled(false);
		Box hBox1 = Box.createHorizontalBox();

		JComboBox<String> jcb = new JComboBox<String>();
		jcb.addItem("response");
		jcb.addItem("unary response");
		jcb.addItem("----------------");
		jcb.addItem("not direct after");
		jcb.addItem("not response");
		jcb.setSize(new Dimension(120,20));
		jcb.setMaximumSize(new Dimension(120,20));
		jcb.setPreferredSize(new Dimension(120,20));

		hBox1.add(jcb);
		hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);

		// info box that contain two object
		infoBox.add(jb1);

		JLabel jl1 = new JLabel("———————————");
		jl1.setSize(new Dimension(140,20));
		jl1.setMinimumSize(new Dimension(140,20));
		jl1.setMaximumSize(new Dimension(140,20));
		jl1.setPreferredSize(new Dimension(140,20));
		infoBox.add(jl1);
		infoBox.add(jb2);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				mw.objStateToStateRelation = (String) jcb.getSelectedItem();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(infoBox);
		tab.addTab("Basics", null, vBox, "Basic setting");

		Box vBox2 = Box.createVerticalBox();

		JComboBox<String> jcb_waiting_symbol = new JComboBox<String>();
		for (String ts: timeSymbol) {
			jcb_waiting_symbol.addItem(ts);
		}
		jcb_waiting_symbol.addItem("=");

		JComboBox<String> jcb_waiting_time = new JComboBox<String>();
		for (String unit: timeUnit) {
			jcb_waiting_time.addItem(unit);
		}

		Box hBox_waiting = Box.createHorizontalBox();
		jcb_waiting_symbol.setSize(new Dimension(50, 20));
		jcb_waiting_symbol.setMaximumSize(new Dimension(50, 20));
		jcb_waiting_symbol.setPreferredSize(new Dimension(50, 20));
		jcb_waiting_time.setSize(new Dimension(70, 20));
		jcb_waiting_time.setMaximumSize(new Dimension(70, 20));
		jcb_waiting_time.setPreferredSize(new Dimension(70, 20));
		JLabel jlb_waiting = new JLabel("time gap:");
		JTextField jtf_waiting = new JTextField();
		jtf_waiting.setSize(new Dimension(30, 20));
		jtf_waiting.setMaximumSize(new Dimension(30, 20));
		jtf_waiting.setPreferredSize(new Dimension(30, 20));
		hBox_waiting.add(jlb_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_symbol);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_time);
		vBox2.add(Box.createVerticalStrut(20));
		vBox2.add(hBox_waiting);

		tab.addTab("Advanced", null, vBox2, "Advanced setting");
		Box vBox3 = Box.createVerticalBox();

		vBox3.add(tab);
		Box hBoxConfirm = Box.createHorizontalBox();
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.add(Box.createHorizontalStrut(20));
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox3.add(hBoxConfirm);
		// add vBox3 to dialog
		dialog.add(vBox3);
	}

	public void setDialogForObjToObjOrder(String refObj, String targetObj){
		JDialog dialog = new JDialog();
		dialog.setTitle("Object order setting");
		dialog.setSize(new Dimension(500,250));
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);  // set to the center of the screen

		JTabbedPane tab = new JTabbedPane();
		Box vBox = Box.createVerticalBox();
		Box infoBox = Box.createHorizontalBox();

		JButton jb1 = new JButton(refObj);
		jb1.setEnabled(false);
		JButton jb2 = new JButton(targetObj);
		jb2.setEnabled(false);
		Box hBox1 = Box.createHorizontalBox();

		JComboBox<String> jcb = new JComboBox<String>();
		jcb.addItem("response");
		jcb.addItem("precedence");
		jcb.addItem("----------------");
		jcb.addItem("not response");
		jcb.addItem("not precedence");
		jcb.setSize(new Dimension(120,20));
		jcb.setMaximumSize(new Dimension(120,20));
		jcb.setPreferredSize(new Dimension(120,20));

		hBox1.add(jcb);
		hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);

		// info box that contain two object
		infoBox.add(jb1);

		JLabel jl1 = new JLabel("———————————");
		jl1.setSize(new Dimension(140,20));
		jl1.setMinimumSize(new Dimension(140,20));
		jl1.setMaximumSize(new Dimension(140,20));
		jl1.setPreferredSize(new Dimension(140,20));
		infoBox.add(jl1);
		infoBox.add(jb2);

		// add a confirm button
		JButton jbConfirm = new JButton("Confirm");
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				mw.actOrderType = (String) jcb.getSelectedItem();
			}
		});

		// add a cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox1);
		vBox.add(infoBox);
		tab.addTab("Basics", null, vBox, "Basic setting");

		Box vBox2 = Box.createVerticalBox();

		JComboBox<String> jcb_waiting_symbol = new JComboBox<String>();
		for (String ts: timeSymbol) {
			jcb_waiting_symbol.addItem(ts);
		}
		jcb_waiting_symbol.addItem("=");

		JComboBox<String> jcb_waiting_time = new JComboBox<String>();
		for (String unit: timeUnit) {
			jcb_waiting_time.addItem(unit);
		}

		Box hBox_waiting = Box.createHorizontalBox();
		jcb_waiting_symbol.setSize(new Dimension(50, 20));
		jcb_waiting_symbol.setMaximumSize(new Dimension(50, 20));
		jcb_waiting_symbol.setPreferredSize(new Dimension(50, 20));
		jcb_waiting_time.setSize(new Dimension(70, 20));
		jcb_waiting_time.setMaximumSize(new Dimension(70, 20));
		jcb_waiting_time.setPreferredSize(new Dimension(70, 20));
		JLabel jlb_waiting = new JLabel("time gap:");
		JTextField jtf_waiting = new JTextField();
		jtf_waiting.setSize(new Dimension(30, 20));
		jtf_waiting.setMaximumSize(new Dimension(30, 20));
		jtf_waiting.setPreferredSize(new Dimension(30, 20));
		hBox_waiting.add(jlb_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_symbol);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jtf_waiting);
		hBox_waiting.add(Box.createHorizontalStrut(5));
		hBox_waiting.add(jcb_waiting_time);
		vBox2.add(Box.createVerticalStrut(20));
		vBox2.add(hBox_waiting);

		tab.addTab("Advanced", null, vBox2, "Advanced setting");
		Box vBox3 = Box.createVerticalBox();

		vBox3.add(tab);
		Box hBoxConfirm = Box.createHorizontalBox();
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.add(Box.createHorizontalStrut(20));
		hBoxConfirm.add(jbConfirm);
		hBoxConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
		vBox3.add(hBoxConfirm);
		// add vBox3 to dialog
		dialog.add(vBox3);
	}

	/**
	 * 
	 */
	public void setBounds(Rectangle value)
	{
		if ((bounds == null && value != null)
				|| (bounds != null && value == null)
				|| (bounds != null && value != null && !bounds.equals(value)))
		{
			Rectangle tmp = bounds;

			if (tmp != null)
			{
				if (value != null)
				{
					tmp.add(value);
				}
			}
			else
			{
				tmp = value;
			}

			bounds = value;

			if (tmp != null)
			{
				graphComponent.getGraphControl().repaint(tmp);
			}
		}
	}

	/**
	 * Adds the given event listener.
	 */
	public void addListener(String eventName, mxIEventListener listener)
	{
		eventSource.addListener(eventName, listener);
	}

	/**
	 * Removes the given event listener.
	 */
	public void removeListener(mxIEventListener listener)
	{
		eventSource.removeListener(listener);
	}

	/**
	 * Removes the given event listener for the specified event name.
	 */
	public void removeListener(mxIEventListener listener, String eventName)
	{
		eventSource.removeListener(listener, eventName);
	}

	/**
	 * 
	 */
	public void paint(Graphics g)
	{
		if (bounds != null)
		{
			if (connectIcon != null)
			{
				g.drawImage(connectIcon.getImage(), bounds.x, bounds.y,
						bounds.width, bounds.height, null);
			}
			else if (handleEnabled)
			{
				g.setColor(Color.BLACK);
				g.draw3DRect(bounds.x, bounds.y, bounds.width - 1,
						bounds.height - 1, true);
				g.setColor(Color.GREEN);
				g.fill3DRect(bounds.x + 1, bounds.y + 1, bounds.width - 2,
						bounds.height - 2, true);
				g.setColor(Color.BLUE);
				g.drawRect(bounds.x + bounds.width / 2 - 1, bounds.y
						+ bounds.height / 2 - 1, 1, 1);
			}
		}
	}

}
