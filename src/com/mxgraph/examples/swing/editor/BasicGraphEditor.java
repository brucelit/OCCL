package com.mxgraph.examples.swing.editor;

import static com.mxgraph.swing.mxGraphComponent.DEFAULT_PAGESCALE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.util.rule.Checker;
import com.mxgraph.view.mxGraph;


public class BasicGraphEditor extends JPanel
{

	static MiddleWare mw = MiddleWare.getInstance();
	
	protected double pageScale = DEFAULT_PAGESCALE;

	protected PageFormat pageFormat = new PageFormat();

	protected List<String> objectTypes;


	/**
	 * 
	 */
	private static final long serialVersionUID = -6561623072112577140L;

	/**
	 * Adds required resources for i18n
	 */
	static
	{
		try
		{
			mxResources.add("com/mxgraph/examples/swing/resources/editor");
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	/**
	 * 
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * 
	 */
	protected mxGraphOutline graphOutline;

	/**
	 * 
	 */
	protected JTabbedPane libraryPane;

	/**
	 * 
	 */
	protected mxUndoManager undoManager;

	/**
	 * 
	 */
	protected String appTitle;

	/**
	 * 
	 */
	protected JLabel statusBar;

	/**
	 * 
	 */
	protected File currentFile;

	/**
	 * Flag indicating whether the current graph has been modified 
	 */
	protected boolean modified = false;

	/**
	 * 
	 */
	protected mxRubberband rubberband;

	/**
	 * 
	 */
	protected mxKeyboardHandler keyboardHandler;

	/**
	 * 
	 */
	protected mxIEventListener undoHandler = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			undoManager.undoableEditHappened((mxUndoableEdit) evt
					.getProperty("edit"));
		}
	};

	/**
	 * 
	 */
	protected mxIEventListener changeTracker = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
			setModified(true);
		}
	};

	/**
	 * 
	 */
	public BasicGraphEditor(String appTitle, List<String> objectTypes,  mxGraphComponent component)
	{
		// Stores and updates the frame title
		this.appTitle = appTitle;
		this.objectTypes = objectTypes;
		// Stores a reference to the graph and creates the command history
		graphComponent = component;
		final mxGraph graph = graphComponent.getGraph();
		undoManager = createUndoManager();

		// Do not change the scale and translation after files have been loaded
		graph.setResetViewOnRootChange(false);

		// Updates the modified flag if the graph model changes
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

		// Adds the command history to the model and view
		graph.getModel().addListener(mxEvent.UNDO, undoHandler);
		graph.getView().addListener(mxEvent.UNDO, undoHandler);

		Object parent = graph.getDefaultParent();

//		ArrayList<String> lst1 = new ArrayList<>();
//		lst1.add("item");
//		lst1.add("order");
//		lst1.add("package");

		Integer i = 0;
		Integer j = 100;
		
		while (i<objectTypes.size()) {
			// Print the elements of ArrayList
			graph.prepareContext(parent, j.toString(), objectTypes.get(i), (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
					* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
					* pageScale)/10)*i,
					(int) Math.round(pageFormat.getWidth() * pageScale *0.8),
					((int) Math.round(pageFormat.getHeight() * pageScale)/10),"swimlane;horizontal=false","objContainer");
			i++;
			j++;
		}
		graph.prepareContext(parent, "97","Activity", (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
				* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
				* pageScale)/10)*i,
				(int) Math.round(pageFormat.getWidth() * pageScale *0.8), ((int) Math.round(pageFormat.getHeight()
						* pageScale)/10),"actSwimlane;horizontal=false","actContainer");
		
		graph.prepareContext(parent, "98","Time", (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
				* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
				* pageScale)/10)*(i+1),
				(int) Math.round(pageFormat.getWidth() * pageScale *0.8), ((int) Math.round(pageFormat.getHeight()
						* pageScale)/15),"timeSwimlane;horizontal=false","timeContainer");

		Object timeLine = graph.insertVertex(((mxGraphModel)graph.getModel()).getCell("98"),"99", null,
				0, 20, (int) Math.round(pageFormat.getWidth() * pageScale *0.75), ((int) Math.round(pageFormat.getHeight()
						* pageScale)/30), "image;image=/com/mxgraph/examples/swing/images/time.png");

		((mxCell) timeLine).setConnectable(false);

		ArrayList<mxCell> ports = getTimelineRefTime();
		graph.addCell(ports.get(0),(mxCell) timeLine);
		graph.addCell(ports.get(1),(mxCell) timeLine);
		graph.addCell(ports.get(2),(mxCell) timeLine);
		graph.addCell(ports.get(3),(mxCell) timeLine);

		// Keeps the selection in sync with the command history
		mxIEventListener undoHandler = new mxIEventListener()
		{
			public void invoke(Object source, mxEventObject evt)
			{
				List<mxUndoableChange> changes = ((mxUndoableEdit) evt
						.getProperty("edit")).getChanges();
				graph.setSelectionCells(graph
						.getSelectionCellsForChanges(changes));
			}
		};

		undoManager.addListener(mxEvent.UNDO, undoHandler);
		undoManager.addListener(mxEvent.REDO, undoHandler);

		// Creates the graph outline component
//		graphOutline = new mxGraphOutline(graphComponent);
		Box vbox = Box.createVerticalBox();

		Box hbox1 = Box.createHorizontalBox();
		Box hbox2 = Box.createHorizontalBox();

		hbox1.add(Box.createHorizontalStrut(15));
		JButton jb1 = new JButton("Check");
		hbox1.add(jb1);
		hbox2.add(Box.createHorizontalStrut(15));
		JButton jb2 = new JButton("Reset");
		hbox2.add(jb2);
		// add checking method
		jb1.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {

				Checker c1 = new Checker();
				HashMap violations = c1.checkViolations();

			}
		});
		vbox.add(Box.createVerticalStrut(20));
		vbox.add(hbox1);

		// reset the whole graph
		vbox.add(Box.createVerticalStrut(20));
		jb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mw = MiddleWare.getInstance();
				graph.selectAll();
				graph.removeCells();
				graphComponent = component;
				final mxGraph graph = graphComponent.getGraph();
				undoManager = createUndoManager();

				// Do not change the scale and translation after files have been loaded
				graph.setResetViewOnRootChange(false);

				// Updates the modified flag if the graph model changes
				graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

				// Adds the command history to the model and view
				graph.getModel().addListener(mxEvent.UNDO, undoHandler);
				graph.getView().addListener(mxEvent.UNDO, undoHandler);

				Object parent = graph.getDefaultParent();

				ArrayList<String> lst1 = new ArrayList<>();
				lst1.add("item");
				lst1.add("order");
				lst1.add("package");

				Integer i = 0;
				Integer j = 100;
				while (i<objectTypes.size()) {
					// Print the elements of ArrayList
					graph.prepareContext(parent, j.toString(), lst1.get(i), (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
							* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
							* pageScale)/10)*i,
							(int) Math.round(pageFormat.getWidth() * pageScale *0.8),
							((int) Math.round(pageFormat.getHeight() * pageScale)/10),"swimlane;horizontal=false","objContainer");
					i++;
					j++;
				}
				graph.prepareContext(parent, "97","Activity", (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
						* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
						* pageScale)/10)*i,
						(int) Math.round(pageFormat.getWidth() * pageScale *0.8), ((int) Math.round(pageFormat.getHeight()
								* pageScale)/10),"actSwimlane;horizontal=false","actContainer");
				
				graph.prepareContext(parent, "98","Time", (int) Math.round(pageFormat.getWidth() * pageScale/11), ((int) Math.round(pageFormat.getHeight()
						* pageScale)/20)+((int) Math.round(pageFormat.getHeight()
						* pageScale)/10)*(i+1),
						(int) Math.round(pageFormat.getWidth() * pageScale *0.8), ((int) Math.round(pageFormat.getHeight()
								* pageScale)/15),"timeSwimlane;horizontal=false","timeContainer");

				Object timeLine = graph.insertVertex(((mxGraphModel)graph.getModel()).getCell("98"),"99", null,
						0, 20, (int) Math.round(pageFormat.getWidth() * pageScale *0.75), ((int) Math.round(pageFormat.getHeight()
								* pageScale)/30), "image;image=/com/mxgraph/examples/swing/images/time.png");

				((mxCell) timeLine).setConnectable(false);

				ArrayList<mxCell> ports = getTimelineRefTime();
				graph.addCell(ports.get(0),(mxCell) timeLine);
				graph.addCell(ports.get(1),(mxCell) timeLine);
				graph.addCell(ports.get(2),(mxCell) timeLine);
				graph.addCell(ports.get(3),(mxCell) timeLine);

				graph.refresh();
				graph.repaint();
			}
		});
		vbox.add(hbox2);


		jb1.setSize(new Dimension(20,20));
		// Creates the library pane that contains the tabs with the palettes
		libraryPane = new JTabbedPane();

		// Creates the inner split pane that contains the library with the
		// palettes and the graph outline on the left side of the window
		JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				libraryPane, vbox);
		inner.setDividerLocation(0.1);
		inner.setBorder(null);

		// Creates the outer split pane that contains the inner split pane and
		// the graph component on the right side of the window
//		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
//				graphComponent);
		JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, libraryPane, graphComponent);
		outer.setDividerLocation(70);
//		outer.setDividerSize(1);
		outer.setBorder(null);

		// Creates the status bar
		statusBar = createStatusBar();

		// Display some useful information about repaint events
		installRepaintListener();

		// Puts everything together
		setLayout(new BorderLayout());
		add(outer, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
//		installToolBar();

		// Installs rubberband selection and handling for some special
		// keystrokes such as F2, Control-C, -V, X, A etc.
		installHandlers();
		installListeners();
		updateTitle();
	}

	public static ArrayList<mxCell> getTimelineRefTime(){
		int PORT_DIAMETER = 10;

		int PORT_RADIUS = PORT_DIAMETER / 2;

		ArrayList<mxCell> ports = new ArrayList<>();

		mxGeometry geo1 = new mxGeometry(0.1, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);

		mxCell port1 = new mxCell(null, geo1,
				"timeRefPort","timeRef", true);
		port1.setVertex(true);

		mxGeometry geo2 = new mxGeometry(0.4, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo2.setRelative(true);

		mxCell port2 = new mxCell(null, geo2,
				"timeRefPort","timeRef", true);
		port2.setVertex(true);

		mxGeometry geo3 = new mxGeometry(0.6, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo3.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo3.setRelative(true);

		mxCell port3 = new mxCell(null, geo3,
				"timeRefPort","timeRef", true);
		port3.setVertex(true);

		mxGeometry geo4 = new mxGeometry(0.9, 0.5, PORT_DIAMETER,
				PORT_DIAMETER);
		geo4.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo4.setRelative(true);

		mxCell port4 = new mxCell(null, geo4,
				"timeRefPort","timeRef", true);
		port4.setVertex(true);
		ports.add(port1);
		ports.add(port2);
		ports.add(port3);
		ports.add(port4);

		return ports;
	}

	/**
	 * 
	 */
	protected mxUndoManager createUndoManager()
	{
		return new mxUndoManager();
	}

	/**
	 * 
	 */
	protected void installHandlers()
	{
		rubberband = new mxRubberband(graphComponent);
		keyboardHandler = new EditorKeyboardHandler(graphComponent);
	}

	/**
	 * ·
	 */
	protected void installToolBar()
	{
		add(new EditorToolBar(this, JToolBar.HORIZONTAL), BorderLayout.NORTH);
	}

	/**
	 * 
	 */
	protected JLabel createStatusBar()
	{
		JLabel statusBar = new JLabel(mxResources.get("ready"));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		return statusBar;
	}

	/**
	 * 
	 */
	protected void installRepaintListener()
	{
		graphComponent.getGraph().addListener(mxEvent.REPAINT,
				new mxIEventListener()
				{
					public void invoke(Object source, mxEventObject evt)
					{
						String buffer = (graphComponent.getTripleBuffer() != null) ? ""
								: " (unbuffered)";
						mxRectangle dirty = (mxRectangle) evt
								.getProperty("region");

						if (dirty == null)
						{
							status("Repaint all" + buffer);
						}
						else
						{
							status("Repaint: x=" + (int) (dirty.getX()) + " y="
									+ (int) (dirty.getY()) + " w="
									+ (int) (dirty.getWidth()) + " h="
									+ (int) (dirty.getHeight()) + buffer);
						}
					}
				});
	}

	/**
	 * 
	 */
	public EditorPalette insertPalette(String title)
	{
		final EditorPalette palette = new EditorPalette();
//		final JPanel scrollPane = new JPanel(palette);
//		palette.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		palette.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		libraryPane.add(title, palette);
		libraryPane.setSize(720,40);
		// Updates the widths of the palettes if the container size changes
		palette.addComponentListener(new ComponentAdapter()
		{
			/**
			 * 
			 */
			public void componentResized(ComponentEvent e)
			{
				int w = palette.getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
	}

	/**
	 * 
	 */
	protected void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() < 0)
		{
			graphComponent.zoomIn();
		}
		else
		{
			graphComponent.zoomOut();
		}

		status(mxResources.get("scale") + ": "
				+ (int) (100 * graphComponent.getGraph().getView().getScale())
				+ "%");
	}

	/**
	 * 
	 */
	protected void showOutlinePopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(
				mxResources.get("magnifyPage"));
		item.setSelected(graphOutline.isFitPage());

		item.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setFitPage(!graphOutline.isFitPage());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(
				mxResources.get("showLabels"));
		item2.setSelected(graphOutline.isDrawLabels());

		item2.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
				graphOutline.repaint();
			}
		});

		JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(
				mxResources.get("buffering"));
		item3.setSelected(graphOutline.isTripleBuffered());

		item3.addActionListener(new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
				graphOutline.repaint();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(item);
		menu.add(item2);
		menu.add(item3);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	/**
	 * 
	 */
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		EditorPopupMenu menu = new EditorPopupMenu(BasicGraphEditor.this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}

	/**
	 * 
	 */
	protected void mouseLocationChanged(MouseEvent e)
	{
		status(e.getX() + ", " + e.getY());
	}

	/**
	 * 
	 */
	protected void installListeners()
	{
		// Installs mouse wheel listener for zooming
		MouseWheelListener wheelTracker = new MouseWheelListener()
		{
			/**
			 * 
			 */
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				if (e.getSource() instanceof mxGraphOutline
						|| e.isControlDown())
				{
					BasicGraphEditor.this.mouseWheelMoved(e);
				}
			}

		};

		// Handles mouse wheel events in the outline and graph component
//		graphOutline.addMouseWheelListener(wheelTracker);
		graphComponent.addMouseWheelListener(wheelTracker);

		// Installs the popup menu in the outline
//		graphOutline.addMouseListener(new MouseAdapter()
//		{
//
//			/**
//			 *
//			 */
//			public void mousePressed(MouseEvent e)
//			{
//				// Handles context menu on the Mac where the trigger is on mousepressed
//				mouseReleased(e);
//			}
//
//			/**
//			 *
//			 */
//			public void mouseReleased(MouseEvent e)
//			{
//				if (e.isPopupTrigger())
//				{
//					showOutlinePopupMenu(e);
//				}
//			}
//
//		});

		// Installs the popup menu in the graph component
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			/**
			 * 
			 */
			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			/**
			 * 
			 */
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
				}
			}

		});

		// Installs a mouse motion listener to display the mouse location
		graphComponent.getGraphControl().addMouseMotionListener(
				new MouseMotionListener()
				{

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
					 */
					public void mouseDragged(MouseEvent e)
					{
						mouseLocationChanged(e);
					}

					/*
					 * (non-Javadoc)
					 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
					 */
					public void mouseMoved(MouseEvent e)
					{
						mouseDragged(e);
					}

				});
	}

	/**
	 * 
	 */
	public void setCurrentFile(File file)
	{
		File oldValue = currentFile;
		currentFile = file;

		firePropertyChange("currentFile", oldValue, file);

		if (oldValue != file)
		{
			updateTitle();
		}
	}

	/**
	 * 
	 */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/**
	 * 
	 * @param modified
	 */
	public void setModified(boolean modified)
	{
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified)
		{
			updateTitle();
		}
	}

	/**
	 * 
	 * @return whether or not the current graph has been modified
	 */
	public boolean isModified()
	{
		return modified;
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphComponent;
	}

	/**
	 * 
	 */
	public mxGraphOutline getGraphOutline()
	{
		return graphOutline;
	}
	
	/**
	 * 
	 */
	public JTabbedPane getLibraryPane()
	{
		return libraryPane;
	}

	/**
	 * 
	 */
	public mxUndoManager getUndoManager()
	{
		return undoManager;
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name
	 */
	public Action bind(String name, final Action action)
	{
		return bind(name, action, null);
	}

	/**
	 * 
	 * @param name
	 * @param action
	 * @return a new Action bound to the specified string name and icon
	 */
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl)
	{
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				BasicGraphEditor.class.getResource(iconUrl)) : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(getGraphComponent(), e
						.getID(), e.getActionCommand()));
			}
		};
		
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		return newAction;
	}

	/**
	 * 
	 * @param msg
	 */
	public void status(String msg)
	{
		statusBar.setText(msg);
	}

	/**
	 * 
	 */
	public void updateTitle()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			String title = (currentFile != null) ? currentFile
					.getAbsolutePath() : mxResources.get("newDiagram");

			if (modified)
			{
				title += "*";
			}

			frame.setTitle(title + " - " + appTitle);
		}
	}

	/**
	 * 
	 */
	public void about()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			EditorAboutFrame about = new EditorAboutFrame(frame);
			about.setModal(true);

			// Centers inside the application frame
			int x = frame.getX() + (frame.getWidth() - about.getWidth()) / 2;
			int y = frame.getY() + (frame.getHeight() - about.getHeight()) / 2;
			about.setLocation(x, y);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	/**
	 * 
	 */
	public void exit()
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			frame.dispose();
		}
	}

	/**
	 * 
	 */
	public void setLookAndFeel(String clazz)
	{
		JFrame frame = (JFrame) SwingUtilities.windowForComponent(this);

		if (frame != null)
		{
			try
			{
				UIManager.setLookAndFeel(clazz);
				SwingUtilities.updateComponentTreeUI(frame);

				// Needs to assign the key bindings again
				keyboardHandler = new EditorKeyboardHandler(graphComponent);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public JFrame createFrame()
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200, 200);
		return frame;
	}

	/**
	 * 
	 */
	public JFrame createFrame(JMenuBar menuBar)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setSize(1000, 1000);

		// Updates the frame title
		updateTitle();

		return frame;
	}

	/**
	 * Creates an action that executes the specified layout.
	 * 
	 * @param key Key to be used for getting the label from mxResources and also
	 * to create the layout instance for the commercial graph editor example.
	 * @return an action that executes the specified layout
	 */
	@SuppressWarnings("serial")
	public Action graphLayout(final String key, boolean animate)
	{
		final mxIGraphLayout layout = createLayout(key, animate);

		if (layout != null)
		{
			return new AbstractAction(mxResources.get(key))
			{
				public void actionPerformed(ActionEvent e)
				{
					final mxGraph graph = graphComponent.getGraph();
					Object cell = graph.getSelectionCell();

					if (cell == null
							|| graph.getModel().getChildCount(cell) == 0)
					{
						cell = graph.getDefaultParent();
					}

					graph.getModel().beginUpdate();
					try
					{
						long t0 = System.currentTimeMillis();
						layout.execute(cell);
						status("Layout: " + (System.currentTimeMillis() - t0)
								+ " ms");
					}
					finally
					{
						mxMorphing morph = new mxMorphing(graphComponent, 20,
								1.2, 20);

						morph.addListener(mxEvent.DONE, new mxIEventListener()
						{

							public void invoke(Object sender, mxEventObject evt)
							{
								graph.getModel().endUpdate();
							}

						});

						morph.startAnimation();
					}

				}

			};
		}
		else
		{
			return new AbstractAction(mxResources.get(key))
			{

				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noLayout"));
				}

			};
		}
	}

	/**
	 * Creates a layout instance for the given identifier.
	 */
	protected mxIGraphLayout createLayout(String ident, boolean animate)
	{
		mxIGraphLayout layout = null;

		if (ident != null)
		{
			mxGraph graph = graphComponent.getGraph();

			if (ident.equals("verticalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph);
			}
			else if (ident.equals("horizontalHierarchical"))
			{
				layout = new mxHierarchicalLayout(graph, JLabel.WEST);
			}
			else if (ident.equals("verticalTree"))
			{
				layout = new mxCompactTreeLayout(graph, false);
			}
			else if (ident.equals("horizontalTree"))
			{
				layout = new mxCompactTreeLayout(graph, true);
			}
			else if (ident.equals("parallelEdges"))
			{
				layout = new mxParallelEdgeLayout(graph);
			}
			else if (ident.equals("placeEdgeLabels"))
			{
				layout = new mxEdgeLabelLayout(graph);
			}
			else if (ident.equals("organicLayout"))
			{
				layout = new mxOrganicLayout(graph);
			}
			if (ident.equals("verticalPartition"))
			{
				layout = new mxPartitionLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalPartition"))
			{
				layout = new mxPartitionLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("verticalStack"))
			{
				layout = new mxStackLayout(graph, false)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("horizontalStack"))
			{
				layout = new mxStackLayout(graph, true)
				{
					/**
					 * Overrides the empty implementation to return the size of the
					 * graph control.
					 */
					public mxRectangle getContainerSize()
					{
						return graphComponent.getLayoutAreaSize();
					}
				};
			}
			else if (ident.equals("circleLayout"))
			{
				layout = new mxCircleLayout(graph);
			}
		}

		return layout;
	}

}
