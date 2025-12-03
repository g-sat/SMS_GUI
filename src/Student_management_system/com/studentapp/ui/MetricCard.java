package Student_management_system.com.studentapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

final class MetricCard extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Supplier<String> supplier;
    private final JLabel valueLbl;

    MetricCard(String title, Supplier<String> valueSupplier, Color bg, Color fg, Color accent) {
        this.supplier = valueSupplier;
        setLayout(new BorderLayout());
        setBackground(bg);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(accent);
        valueLbl = new JLabel();
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLbl.setForeground(fg);
        add(titleLbl, BorderLayout.NORTH);
        add(valueLbl, BorderLayout.CENTER);
        refreshValue();
    }

    void refreshValue() {
        valueLbl.setText(supplier.get());
    }
}