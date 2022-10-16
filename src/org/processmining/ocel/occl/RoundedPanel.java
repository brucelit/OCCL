package org.processmining.ocel.occl;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {
    protected int radius;
    protected int outerBorder;
    protected int innerBorder;
    protected Color borderColor;
    protected float borderWidth;

    public RoundedPanel(int aRadius, int anOuterBorder, int anInnerBorder) {
        this.borderColor = null;
        this.borderWidth = 0.0F;
        this.radius = aRadius;
        this.outerBorder = anOuterBorder;
        this.innerBorder = anInnerBorder;
        this.setOpaque(false);
        int borderSize = this.radius / 2 + this.outerBorder + this.innerBorder;
        this.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
        this.setOpaque(false);
    }

    public RoundedPanel(int aRadius) {
        this(aRadius, 0, 0);
    }

    public RoundedPanel() {
        this(5, 0, 0);
    }

    public void setBorder(Color borderColor, float borderWidth) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        if (this.isVisible() && this.isDisplayable()) {
            this.repaint();
        }

    }

    public void clearBorder() {
        this.setBorder((Color)null, 0.0F);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(this.getBackground());
        RoundRectangle2D rrect = new RoundRectangle2D.Double((double)this.outerBorder - 0.5, (double)this.outerBorder - 0.5, (double)(this.getWidth() - 2 * this.outerBorder), (double)(this.getHeight() - 2 * this.outerBorder), (double)this.radius, (double)this.radius);
        g2d.fill(rrect);
        if (this.borderColor != null) {
            g2d.setColor(this.borderColor);
            BasicStroke stroke = new BasicStroke(this.borderWidth, 1, 1);
            g2d.setStroke(stroke);
            g2d.draw(rrect);
        }

    }

    public static com.fluxicon.slickerbox.components.RoundedPanel enclose(JComponent comp, int radius, int outerBorder, int innerBorder) {
        com.fluxicon.slickerbox.components.RoundedPanel enclosure = new com.fluxicon.slickerbox.components.RoundedPanel(radius, outerBorder, innerBorder);
        enclosure.setLayout(new BorderLayout());
        enclosure.setBackground(comp.getBackground());
        enclosure.add(comp, "Center");
        return enclosure;
    }
}
