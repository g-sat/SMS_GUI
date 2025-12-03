package Student_management_system.com.studentapp.ui;

import Student_management_system.com.studentapp.model.*;
import Student_management_system.com.studentapp.repository.JdbcStudentRepository;
import Student_management_system.com.studentapp.repository.StudentRepositoryContract;
import Student_management_system.com.studentapp.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
	private final StudentService service;
    private final StudentRepositoryContract repo;

    private final JTextField nameField = new JTextField();
    private final JTextField dobField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField majorField = new JTextField();
    private final JTextField searchField = new JTextField();

    private JTable table;
    private StudentTableModel tableModel;
    private final JLabel statusLabel = new JLabel("Ready");

    private boolean darkMode = false;

    private static final class Palette {
        final Color bgApp;
        final Color bgCard;
        final Color textPrimary;
        final Color textSecondary;
        final Color border;
        final Color accent;
        final Color zebraOdd;
        final Color zebraEven;
        final Color headerBg;
        final Color headerFg;
        Palette(boolean dark) {
            if (dark) {
                bgApp = new Color(24,26,30);
                bgCard = new Color(32,34,38);
                textPrimary = new Color(230,230,230);
                textSecondary = new Color(170,170,170);
                border = new Color(70,70,70);
                accent = new Color(33,150,243);
                zebraEven = new Color(30,32,36);
                zebraOdd = new Color(38,40,44);
                headerBg = new Color(40,44,48);
                headerFg = new Color(220,220,220);
            } else {
                bgApp = new Color(245,246,250);
                bgCard = Color.WHITE;
                textPrimary = new Color(40,40,40);
                textSecondary = new Color(100,100,100);
                border = new Color(230,230,230);
                accent = new Color(33,150,243);
                zebraEven = Color.WHITE;
                zebraOdd = new Color(249,250,252);
                headerBg = new Color(245,245,245);
                headerFg = new Color(50,50,50);
            }
        }
    }
    private Palette palette = new Palette(false);

    public MainFrame() {
        super("Student Management");
        applyTheme(darkMode);
        String url = "jdbc:mysql://127.0.0.25:3306/mydb";
        String user = "root";
        String pass = "2221";
        repo = new JdbcStudentRepository(url, user, pass);
        service = new StudentService(repo);
        initUI();
        setMinimumSize(new Dimension(1000, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void applyTheme(boolean dark) {
        palette = new Palette(dark);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        UIManager.put("control", palette.bgApp);
        UIManager.put("text", palette.textPrimary);
        UIManager.put("Table.gridColor", dark ? new Color(55,55,55) : new Color(235,235,235));
        UIManager.put("Table.selectionBackground", palette.accent);
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(palette.bgApp);

        // App bar
        JComponent appBar = createAppBar();
        root.add(appBar, BorderLayout.NORTH);

        // Content split: table center, form right
        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(palette.bgApp);

        tableModel = new StudentTableModel(List.of());
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(palette.bgCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        content.add(scroll, BorderLayout.CENTER);

        JPanel formCard = createFormCard();
        content.add(formCard, BorderLayout.EAST);

        root.add(content, BorderLayout.CENTER);

        // Status bar
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(new EmptyBorder(8, 16, 8, 16));
        status.setBackground(palette.bgCard);
        statusLabel.setForeground(palette.textSecondary);
        status.add(statusLabel, BorderLayout.WEST);
        root.add(status, BorderLayout.SOUTH);

        setContentPane(root);
        refreshTable();
    }

    private JComponent createAppBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = darkMode ? new Color(25, 25, 28) : palette.accent;
                Color c2 = darkMode ? new Color(18, 18, 20) : new Color(25, 118, 210);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 72));

        JLabel title = new JLabel("Student Manager");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 8, 0, 0));
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 18));
        right.setOpaque(false);
        styleTextField(searchField, "Search by name");
        JButton searchBtn = createPrimaryButton("Search");
        searchBtn.addActionListener(this::onSearch);
        JToggleButton themeToggle = new JToggleButton(darkMode ? "Light mode" : "Dark mode");
        themeToggle.setSelected(darkMode);
        themeToggle.addItemListener(ev -> onToggleTheme(ev, themeToggle));
        right.add(searchField);
        right.add(searchBtn);
        right.add(themeToggle);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private void onToggleTheme(ItemEvent ev, JToggleButton toggle) {
        darkMode = ev.getStateChange() == ItemEvent.SELECTED;
        toggle.setText(darkMode ? "Light mode" : "Dark mode");
        applyTheme(darkMode);
        SwingUtilities.updateComponentTreeUI(this);
        // re-style dynamic components
        styleTable(table);
        styleTextField(nameField, "Full name");
        styleTextField(dobField, "DOB (yyyy-mm-dd)");
        styleTextField(emailField, "Email address");
        styleTextField(majorField, "Major");
        styleTextField(searchField, "Search by name");
        repaint();
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 0));
        card.setBackground(palette.bgCard);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.border),
                new EmptyBorder(16, 16, 16, 16)));
        JLabel formTitle = new JLabel("Add / Manage Student");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(palette.textPrimary);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitle.setBorder(new EmptyBorder(0,0,8,0));
        card.add(formTitle);

        styleTextField(nameField, "Full name");
        styleTextField(dobField, "DOB (yyyy-mm-dd)");
        styleTextField(emailField, "Email address");
        styleTextField(majorField, "Major");

        card.add(labeledField("Name", nameField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeledField("DOB", dobField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeledField("Email", emailField));
        card.add(Box.createVerticalStrut(10));
        card.add(labeledField("Major", majorField));
        card.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton add = createPrimaryButton("Add Student");
        add.addActionListener(this::onAdd);
        JButton delete = createSecondaryButton("Delete Selected");
        delete.addActionListener(this::onDelete);
        actions.add(add);
        actions.add(delete);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(actions);

        return card;
    }

    private void styleTable(JTable t) {
        t.setFillsViewportHeight(true);
        t.setRowHeight(30);
        t.setShowGrid(true);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(palette.headerBg);
        t.getTableHeader().setForeground(palette.headerFg);
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder(6,8,6,8));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? palette.zebraEven : palette.zebraOdd);
                    c.setForeground(palette.textPrimary);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(palette.textSecondary);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleTextField(JTextField tf, String placeholder) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.border),
                new EmptyBorder(10, 12, 10, 12)));
        tf.setBackground(palette.bgCard);
        tf.setForeground(palette.textPrimary);
        applyPlaceholder(tf, placeholder);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(palette.accent);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.accent),
                new EmptyBorder(8,16,8,16)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(palette.accent);
        b.setBackground(palette.bgCard);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.accent),
                new EmptyBorder(8,16,8,16)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void applyPlaceholder(JTextField tf, String placeholder) {
        Color hint = palette.textSecondary;
        if (tf.getText() == null || tf.getText().isBlank() || tf.getText().equals(placeholder)) {
            tf.setText(placeholder);
            tf.setForeground(hint);
        }
        for (FocusListener l : tf.getFocusListeners()) tf.removeFocusListener(l);
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(palette.textPrimary);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isBlank()) {
                    tf.setText(placeholder);
                    tf.setForeground(hint);
                }
            }
        });
    }

    private void onAdd(ActionEvent e) {
        try {
            String name = normalized(nameField);
            String dob = normalized(dobField);
            String email = normalized(emailField);
            String major = normalized(majorField);
            LocalDate ld = (dob == null || dob.isBlank()) ? null : LocalDate.parse(dob.trim());
            service.createStudent(name, ld, email, major);
            clearForm();
            refreshTable();
            setStatus("Added student: " + name);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Error: " + ex.getMessage());
        }
    }

    private void onDelete(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            setStatus("Select a row to delete");
            return;
        }
        Student s = tableModel.getStudentAt(row);
        service.delete(s.getId());
        refreshTable();
        setStatus("Deleted student: " + s.getName());
    }

    private void onSearch(ActionEvent e) {
        String q = normalized(searchField);
        List<Student> res = service.search(q);
        tableModel = new StudentTableModel(res);
        table.setModel(tableModel);
        setStatus(res.size() + " result(s) for '" + q + "'");
    }

    private void refreshTable() {
        var all = service.listStudents();
        tableModel = new StudentTableModel(all);
        table.setModel(tableModel);
    }

    private void clearForm() {
        nameField.setText("");
        dobField.setText("");
        emailField.setText("");
        majorField.setText("");
    }

    private String normalized(JTextField tf) {
        String s = tf.getText();
        if (s == null) return "";
        return s.equals("Search by name") || s.equals("Full name") || s.equals("DOB (yyyy-mm-dd)") || s.equals("Email address") || s.equals("Major") ? "" : s.trim();
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }
}
