package com.mxgraph.shape;

import java.awt.*;
import java.awt.geom.GeneralPath;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

public class mxArrowShape extends mxBasicShape
{

	/**
	 *
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{

		double scale = canvas.getScale();
		mxPoint p0 = state.getAbsolutePoint(0);
		mxPoint pe = state.getAbsolutePoint(state.getAbsolutePointCount() - 1);

		// Geometry of arrow
		double spacing = mxConstants.ARROW_SPACING * scale;
		double width = mxConstants.ARROW_WIDTH * scale;
		double arrow = mxConstants.ARROW_SIZE * scale;

		double dx = pe.getX() - p0.getX();
		double dy = pe.getY() - p0.getY();
		double dist = Math.sqrt(dx * dx + dy * dy);
		double length = dist - 2 * spacing - arrow;

		// Computes the norm and the inverse norm
		double nx = dx / dist;
		double ny = dy / dist;
		double basex = length * nx;
		double basey = length * ny;
		double floorx = width * ny / 3;
		double floory = -width * nx / 3;

		// Computes points
		double p0x = p0.getX() - floorx / 2 + spacing * nx;
		double p0y = p0.getY() - floory / 2 + spacing * ny;
		double p1x = p0x + floorx;
		double p1y = p0y + floory;
		double p2x = p1x + basex;
		double p2y = p1y + basey;
		double p3x = p2x + floorx;
		double p3y = p2y + floory;
		// p4 not required
		double p5x = p3x - 3 * floorx;
		double p5y = p3y - 3 * floory;

		Polygon poly = new Polygon();
		poly.addPoint((int) Math.round(p0x), (int) Math.round(p0y));
		poly.addPoint((int) Math.round(p0x), (int) Math.round(p0y+floory));
		poly.addPoint((int) Math.round(p3x), (int) Math.round(p0y+floory));
		poly.addPoint((int) Math.round(p3x), (int) Math.round(p0y+3 * floory));
		poly.addPoint((int) Math.round(pe.getX() - spacing * nx), (int) Math
				.round((p1y+p0y)/2));
		poly.addPoint((int) Math.round(p3x), (int) Math.round(p0y-2*floory));
		poly.addPoint((int) Math.round(p3x), (int) Math.round(p0y));

		return poly;
	}

}