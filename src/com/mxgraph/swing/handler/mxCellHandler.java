/**
 * Copyright (c) 2008-2012, JGraph Ltd
 */
package com.mxgraph.swing.handler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

/**
 * @author Administrator
 * 
 */
public class mxCellHandler
{
	/**
	 * Reference to the enclosing graph component.
	 */
	protected mxGraphComponent graphComponent;

	/**
	 * Holds the cell state associated with this handler.
	 */
	protected mxCellState state;

	/**
	 * Holds the rectangles that define the handles.
	 */
	protected Rectangle[] handles;

	/**
	 * Specifies if the handles should be painted. Default is true.
	 */
	protected boolean handlesVisible = true;

	/**
	 * Holds the bounding box of the handler.
	 */
	protected transient Rectangle bounds;

	/**
	 * Holds the component that is used for preview.
	 */
	protected transient JComponent preview;

	/**
	 * Holds the start location of the mouse gesture.
	 */
	protected transient Point first;

	public String actSelected;


	/**
	 * Holds the index of the handle that was clicked.
	 */
	protected transient int index;

	/**
	 * Constructs a new cell handler for the given cell state.
	 * 
	 * @param graphComponent Enclosing graph component.
	 * @param state Cell state for which the handler is created.
	 */
	public mxCellHandler(mxGraphComponent graphComponent, mxCellState state)
	{
		this.graphComponent = graphComponent;
		refresh(state);
	}

	/**
	 * 
	 */
	public boolean isActive()
	{
		return first != null;
	}

	/**
	 * Refreshes the cell handler.
	 */
	public void refresh(mxCellState state)
	{
		this.state = state;
		handles = createHandles();
		mxGraph graph = graphComponent.getGraph();
		mxRectangle tmp = graph.getBoundingBox(state.getCell());

		if (tmp != null)
		{
			bounds = tmp.getRectangle();

			if (handles != null)
			{
				for (int i = 0; i < handles.length; i++)
				{
					if (isHandleVisible(i))
					{
						bounds.add(handles[i]);
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	public mxGraphComponent getGraphComponent()
	{
		return graphComponent;
	}

	/**
	 * Returns the cell state that is associated with this handler.
	 */
	public mxCellState getState()
	{
		return state;
	}

	/**
	 * Returns the index of the current handle.
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Returns the bounding box of this handler.
	 */
	public Rectangle getBounds()
	{
		return bounds;
	}

	/**
	 * Returns true if the label is movable.
	 */
	public boolean isLabelMovable()
	{
		mxGraph graph = graphComponent.getGraph();
		String label = graph.getLabel(state.getCell());

		return graph.isLabelMovable(state.getCell()) && label != null
				&& label.length() > 0;
	}

	/**
	 * Returns true if the handles should be painted.
	 */
	public boolean isHandlesVisible()
	{
		return handlesVisible;
	}

	/**
	 * Specifies if the handles should be painted.
	 */
	public void setHandlesVisible(boolean handlesVisible)
	{
		this.handlesVisible = handlesVisible;
	}

	/**
	 * Returns true if the given index is the index of the last handle.
	 */
	public boolean isLabel(int index)
	{
		return index == getHandleCount() - 1;
	}

	/**
	 * Creates the rectangles that define the handles.
	 */
	protected Rectangle[] createHandles()
	{
		return null;
	}

	/**
	 * Returns the number of handles in this handler.
	 */
	protected int getHandleCount()
	{
		return (handles != null) ? handles.length : 0;
	}

	/**
	 * Hook for subclassers to return tooltip texts for certain points on the
	 * handle.
	 */
	public String getToolTipText(MouseEvent e)
	{
		return null;
	}

	/**
	 * Returns the index of the handle at the given location.
	 * 
	 * @param x X-coordinate of the location.
	 * @param y Y-coordinate of the location.
	 * @return Returns the handle index for the given location.
	 */
	public int getIndexAt(int x, int y)
	{
		if (handles != null && isHandlesVisible())
		{
			int tol = graphComponent.getTolerance();
			Rectangle rect = new Rectangle(x - tol / 2, y - tol / 2, tol, tol);

			for (int i = handles.length - 1; i >= 0; i--)
			{
				if (isHandleVisible(i) && handles[i].intersects(rect))
				{
					return i;
				}
			}
		}

		return -1;
	}


	/**
	 * Processes the given event.
	 */
	public void mousePressed(MouseEvent e)
	{
		try {

			// ????????????cell
			if (!e.isConsumed() && e.getClickCount() == 2) {
				mxGraph graph = graphComponent.getGraph();
				System.out.println(graph);
				graph.getModel().beginUpdate();

				try {
					mxCell c1 = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
					actSelected = (String) c1.getValue();

					String cellId = c1.getId();
					if (c1.getType().equals("activity")){
						JDialog dialog = new JDialog();
						dialog.setModal(true);
						dialog.setTitle("Activity");
						dialog.setSize(new Dimension(150,150));
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
						jcb.setSize(new Dimension(100,20));
						jcb.setMaximumSize(new Dimension(100,20));
						jcb.setPreferredSize(new Dimension(100,20));
						hBox1.add(jcb);
						hBox1.setAlignmentX(Component.CENTER_ALIGNMENT);
						vBox.add(Box.createVerticalStrut(20));
						vBox.add(hBox1);
						JButton jbConfirm = new JButton("Confirm");
						jbConfirm.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String actSelected = String.valueOf(jcb.getSelectedItem());
								dialog.dispose();
								((mxCell)((mxGraphModel)graph.getModel()).getCell(cellId)).setValue(actSelected);
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
					System.out.println(c1.getValue());


				} catch (Exception exception) {

				}

			}
		}
		catch (Exception exception){

		}
	}

	/**
	 * Processes the given event.
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (!e.isConsumed() && handles != null)
		{
			int index = getIndexAt(e.getX(), e.getY());

			if (index >= 0 && isHandleEnabled(index))
			{
				Cursor cursor = getCursor(e, index);

				if (cursor != null)
				{
					graphComponent.getGraphControl().setCursor(cursor);
					e.consume();
				}
				else
				{
					graphComponent.getGraphControl().setCursor(
							new Cursor(Cursor.HAND_CURSOR));
				}
			}
		}
	}

	/**
	 * Processes the given event.
	 */
	public void mouseDragged(MouseEvent e)
	{
		// empty
	}

	/**
	 * Processes the given event.
	 */
	public void mouseReleased(MouseEvent e)
	{
		reset();
	}

	/**
	 * Starts handling a gesture at the given handle index.
	 */
	public void start(MouseEvent e, int index)
	{
		this.index = index;
		first = e.getPoint();
		preview = createPreview();

		if (preview != null)
		{
			graphComponent.getGraphControl().add(preview, 0);
		}
	}

	/**
	 * Returns true if the given event should be ignored.
	 */
	protected boolean isIgnoredEvent(MouseEvent e)
	{
		return graphComponent.isEditEvent(e);
	}

	/**
	 * Creates the preview for this handler.
	 */
	protected JComponent createPreview()
	{
		return null;
	}

	/**
	 * Resets the state of the handler and removes the preview.
	 */
	public void reset()
	{
		if (preview != null)
		{
			preview.setVisible(false);
			preview.getParent().remove(preview);
			preview = null;
		}

		first = null;
	}

	/**
	 * Returns the cursor for the given event and handle.
	 */
	protected Cursor getCursor(MouseEvent e, int index)
	{
		return null;
	}

	/**
	 * Paints the visible handles of this handler.
	 */
	public void paint(Graphics g)
	{
		if (handles != null && isHandlesVisible())
		{
			for (int i = 0; i < handles.length; i++)
			{
				if (isHandleVisible(i)
						&& g.hitClip(handles[i].x, handles[i].y,
								handles[i].width, handles[i].height))
				{
					g.setColor(getHandleFillColor(i));
					g.fillRect(handles[i].x, handles[i].y, handles[i].width,
							handles[i].height);

					g.setColor(getHandleBorderColor(i));
					g.drawRect(handles[i].x, handles[i].y,
							handles[i].width - 1, handles[i].height - 1);
				}
			}
		}
	}

	/**
	 * Returns the color used to draw the selection border. This implementation
	 * returns null.
	 */
	public Color getSelectionColor()
	{
		return null;
	}

	/**
	 * Returns the stroke used to draw the selection border. This implementation
	 * returns null.
	 */
	public Stroke getSelectionStroke()
	{
		return null;
	}

	/**
	 * Returns true if the handle at the specified index is enabled.
	 */
	protected boolean isHandleEnabled(int index)
	{
		return true;
	}

	/**
	 * Returns true if the handle at the specified index is visible.
	 */
	protected boolean isHandleVisible(int index)
	{
		return !isLabel(index) || isLabelMovable();
	}

	/**
	 * Returns the color to be used to fill the handle at the specified index.
	 */
	protected Color getHandleFillColor(int index)
	{
		if (isLabel(index))
		{
			return mxSwingConstants.LABEL_HANDLE_FILLCOLOR;
		}

		return mxSwingConstants.HANDLE_FILLCOLOR;
	}

	/**
	 * Returns the border color of the handle at the specified index.
	 */
	protected Color getHandleBorderColor(int index)
	{
		return mxSwingConstants.HANDLE_BORDERCOLOR;
	}
	
	/**
	 * Invoked when the handler is no longer used. This is an empty
	 * hook for subclassers.
	 */
	protected void destroy()
	{
		// nop
	}

}
