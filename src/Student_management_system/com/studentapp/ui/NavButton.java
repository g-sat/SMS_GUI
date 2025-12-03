package Student_management_system.com.studentapp.ui;

import javax.swing.*;
import java.awt.*;

final class NavButton extends JButton {
    private static final long serialVersionUID = 1L;

    NavButton(String text, Icon icon, Color background, Color foreground, Color hoverBackground) {
        super(text, icon);
        setHorizontalAlignment(LEFT);
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setForeground(foreground);
        setBackground(background);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        setMinimumSize(new Dimension(0, 48));
        setPreferredSize(new Dimension(0, 48));
        addChangeListener(e -> {
            if (getModel().isRollover()) {
                setBackground(hoverBackground);
            } else {
                setBackground(background);
            }
        });
    }
}
