package Student_management_system.com.studentapp.ui;

import javax.swing.*;
import java.awt.*;

final class GradientPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Color c1;
    private final Color c2;

    GradientPanel(Color c1, Color c2) {
        this.c1 = c1;
        this.c2 = c2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
        g2.dispose();
        super.paintComponent(g);
    }
}
