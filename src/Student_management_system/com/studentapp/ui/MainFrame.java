package Student_management_system.com.studentapp.ui;

import Student_management_system.com.studentapp.model.Course;
import Student_management_system.com.studentapp.model.Enrollment;
import Student_management_system.com.studentapp.model.ReportSummary;
import Student_management_system.com.studentapp.model.Student;
import Student_management_system.com.studentapp.repository.JdbcStudentRepository;
import Student_management_system.com.studentapp.repository.StudentRepositoryContract;
import Student_management_system.com.studentapp.service.CourseService;
import Student_management_system.com.studentapp.service.EnrollmentService;
import Student_management_system.com.studentapp.service.ReportService;
import Student_management_system.com.studentapp.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String CARD_DASHBOARD = "Dashboard";
    private static final String CARD_STUDENTS = "Students";
    private static final String CARD_COURSES = "Courses";
    private static final String CARD_ENROLL = "Enrollment";
    private static final String CARD_REPORTS = "Reports";
    private static final String CARD_SETTINGS = "Settings";

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final ReportService reportService;

    private boolean darkMode = false;
    private Palette palette = new Palette(false);

    private final JTextField searchField = new JTextField();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JPanel cards = new JPanel(new CardLayout());

    private final DashboardPanel dashboardPanel;
    private final StudentsPanel studentsPanel;
    private final CoursesPanel coursesPanel;
    private final EnrollmentPanel enrollmentPanel;
    private final ReportsPanel reportsPanel;
    private final SettingsPanel settingsPanel;

    public MainFrame() {
        super("Student ERP");
        applyTheme(darkMode);
        StudentRepositoryContract repo = new JdbcStudentRepository("jdbc:mysql://127.0.0.25:3306/mydb", "root", "2221");
        this.studentService = new StudentService(repo);
        this.courseService = new CourseService(new Student_management_system.com.studentapp.repository.CourseRepository());
        this.enrollmentService = new EnrollmentService(new Student_management_system.com.studentapp.repository.EnrollmentRepository());
        this.reportService = new ReportService();
        this.dashboardPanel = new DashboardPanel();
        this.studentsPanel = new StudentsPanel();
        this.coursesPanel = new CoursesPanel();
        this.enrollmentPanel = new EnrollmentPanel();
        this.reportsPanel = new ReportsPanel();
        this.settingsPanel = new SettingsPanel();
        initUI();
        setMinimumSize(new Dimension(1400, 840));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        WallpaperPanel root = new WallpaperPanel();
        root.setLayout(new BorderLayout());

        root.add(createAppBar(), BorderLayout.NORTH);
        RoundedPanel shell = new RoundedPanel(new BorderLayout());
        shell.setBorder(new EmptyBorder(16, 16, 16, 16));
        shell.add(createSidebar(), BorderLayout.WEST);
        shell.add(buildCards(), BorderLayout.CENTER);
        root.add(shell, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setBorder(new EmptyBorder(8, 24, 16, 24));
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);
        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
        showCard(CARD_DASHBOARD);
        refreshAllModules();
    }

    private JComponent createAppBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = darkMode ? new Color(28, 30, 35) : palette.accent;
                Color c2 = darkMode ? new Color(18, 18, 20) : new Color(25, 118, 210);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setBorder(new EmptyBorder(10, 24, 10, 24));
        JLabel title = new JLabel("Student ERP Console");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        actions.setOpaque(false);
        styleTextField(searchField, "Quick search");
        searchField.setColumns(18);
        JButton searchBtn = createPrimaryButton("Search");
        searchBtn.addActionListener(this::onSearch);
        JToggleButton themeToggle = new JToggleButton(darkMode ? "Light mode" : "Dark mode");
        themeToggle.setSelected(darkMode);
        themeToggle.addItemListener(e -> setTheme(e.getStateChange() == ItemEvent.SELECTED));
        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(themeToggle);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private JPanel createSidebar() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(24, 16, 24, 24));

        JLabel label = new JLabel("Command Center");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(label);
        nav.add(Box.createVerticalStrut(16));

        nav.add(navButton("Dashboard", CARD_DASHBOARD, UIManager.getIcon("FileView.computerIcon")));
        nav.add(navButton("Students", CARD_STUDENTS, UIManager.getIcon("FileView.listViewIcon")));
        nav.add(navButton("Courses", CARD_COURSES, UIManager.getIcon("FileChooser.detailsViewIcon")));
        nav.add(navButton("Enrollment", CARD_ENROLL, UIManager.getIcon("FileChooser.newFolderIcon")));
        nav.add(navButton("Reports", CARD_REPORTS, UIManager.getIcon("FileView.hardDriveIcon")));
        nav.add(Box.createVerticalStrut(12));
        nav.add(navButton("Settings", CARD_SETTINGS, UIManager.getIcon("OptionPane.informationIcon")));
        return nav;
    }

    private JButton navButton(String text, String card, Icon icon) {
        NavButton button = new NavButton(text, icon, new Color(255, 255, 255, 40), Color.WHITE, new Color(255, 255, 255, 90));
        button.addActionListener(e -> {
            showCard(card);
            switch (card) {
                case CARD_DASHBOARD -> dashboardPanel.refresh();
                case CARD_STUDENTS -> studentsPanel.refresh();
                case CARD_COURSES -> coursesPanel.refresh();
                case CARD_ENROLL -> enrollmentPanel.refresh();
                case CARD_REPORTS -> reportsPanel.refresh();
                case CARD_SETTINGS -> settingsPanel.refresh();
                default -> {
                }
            }
            setStatus("Viewing: " + card);
        });
        return button;
    }

    private JPanel buildCards() {
        cards.setOpaque(false);
        cards.add(dashboardPanel, CARD_DASHBOARD);
        cards.add(studentsPanel, CARD_STUDENTS);
        cards.add(coursesPanel, CARD_COURSES);
        cards.add(enrollmentPanel, CARD_ENROLL);
        cards.add(reportsPanel, CARD_REPORTS);
        cards.add(settingsPanel, CARD_SETTINGS);
        return cards;
    }

    private void showCard(String card) {
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, card);
    }

    private void onSearch(ActionEvent e) {
        String query = normalized(searchField, "Quick search");
        showCard(CARD_STUDENTS);
        int matches = studentsPanel.search(query);
        setStatus(matches + " result(s) for '" + (query == null ? "" : query) + "'");
    }

    private void refreshAllModules() {
        dashboardPanel.refresh();
        studentsPanel.refresh();
        coursesPanel.refresh();
        enrollmentPanel.refresh();
        reportsPanel.refresh();
    }

    private void setTheme(boolean dark) {
        if (this.darkMode == dark) return;
        this.darkMode = dark;
        applyTheme(dark);
        SwingUtilities.updateComponentTreeUI(this);
        styleTextField(searchField, "Quick search");
        dashboardPanel.applyTheme();
        studentsPanel.applyTheme();
        coursesPanel.applyTheme();
        enrollmentPanel.applyTheme();
        reportsPanel.applyTheme();
        settingsPanel.applyTheme();
        repaint();
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
        UIManager.put("control", palette.bgCard);
        UIManager.put("text", palette.textPrimary);
        UIManager.put("nimbusLightBackground", palette.bgCard);
        UIManager.put("Table.selectionBackground", palette.accent);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", dark ? new Color(60, 60, 60) : new Color(225, 225, 225));
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private String normalized(JTextField field, String placeholder) {
        if (field.getText() == null) return "";
        String value = field.getText();
        return (value.equals(placeholder)) ? "" : value.trim();
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

    private void styleInputField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.border),
                new EmptyBorder(8, 10, 8, 10)));
        tf.setBackground(palette.bgCard);
        tf.setForeground(palette.textPrimary);
    }

    private void applyPlaceholder(JTextField tf, String placeholder) {
        Color hint = palette.textSecondary;
        if (tf.getText() == null || tf.getText().isBlank() || tf.getText().equals(placeholder)) {
            tf.setText(placeholder);
            tf.setForeground(hint);
        }
        for (FocusListener listener : tf.getFocusListeners()) {
            tf.removeFocusListener(listener);
        }
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

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(palette.accent);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.accent),
                new EmptyBorder(8, 18, 8, 18)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(palette.accent);
        button.setBackground(palette.bgCard);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(palette.accent),
                new EmptyBorder(8, 18, 8, 18)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(palette.headerBg);
        table.getTableHeader().setForeground(palette.headerFg);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? palette.zebraEven : palette.zebraOdd);
                    component.setForeground(palette.textPrimary);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return component;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private Color translucent(float alpha) {
        return new Color(255, 255, 255, Math.round(255 * alpha));
    }

    private void setStatusFromError(Exception ex) {
        setStatus("Error: " + ex.getMessage());
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    // === Nested Panels ===
    private final class DashboardPanel extends JPanel {
        private final MetricCard studentMetric = new MetricCard("Students", () -> String.valueOf(studentService.listStudents().size()), translucent(0.5f), Color.WHITE, palette.accent);
        private final MetricCard courseMetric = new MetricCard("Courses", () -> String.valueOf(courseService.all().size()), translucent(0.5f), Color.WHITE, palette.accent);
        private final MetricCard enrollmentMetric = new MetricCard("Enrollments", () -> String.valueOf(enrollmentService.all().size()), translucent(0.5f), Color.WHITE, palette.accent);
        private final GradientPanel hero = new GradientPanel(new Color(41, 128, 185), new Color(123, 31, 162));

        DashboardPanel() {
            setOpaque(false);
            setLayout(new BorderLayout(16, 16));
            JPanel metrics = new JPanel(new GridLayout(1, 3, 16, 16));
            metrics.setOpaque(false);
            metrics.add(studentMetric);
            metrics.add(courseMetric);
            metrics.add(enrollmentMetric);
            add(metrics, BorderLayout.NORTH);

            hero.setLayout(new BorderLayout());
            JLabel headline = new JLabel("Unified academic workspace");
            headline.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headline.setForeground(Color.WHITE);
            JLabel subtitle = new JLabel("Navigate modules on the left to manage the entire student lifecycle.");
            subtitle.setForeground(new Color(255, 255, 255, 200));
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setBorder(new EmptyBorder(32, 32, 32, 32));
            text.add(headline);
            text.add(Box.createVerticalStrut(8));
            text.add(subtitle);
            hero.add(text, BorderLayout.CENTER);
            add(hero, BorderLayout.CENTER);
        }

        void refresh() {
            studentMetric.refreshValue();
            courseMetric.refreshValue();
            enrollmentMetric.refreshValue();
        }

        void applyTheme() {
            studentMetric.setBackground(translucent(0.5f));
            courseMetric.setBackground(translucent(0.5f));
            enrollmentMetric.setBackground(translucent(0.5f));
        }
    }

    private final class StudentsPanel extends JPanel {
        private JTable table;
        private StudentTableModel model;
        private final JTextField nameField = new JTextField();
        private final JTextField dobField = new JTextField();
        private final JTextField emailField = new JTextField();
        private final JTextField majorField = new JTextField();
        private JPanel formCard;
        private JScrollPane tableScroll;

        StudentsPanel() {
            setOpaque(false);
            setLayout(new BorderLayout(16, 16));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            model = new StudentTableModel(List.of());
            table = new JTable(model);
            styleTable(table);
            tableScroll = new JScrollPane(table);
            tableScroll.setBorder(BorderFactory.createEmptyBorder());
            tableScroll.getViewport().setBackground(palette.bgCard);
            add(tableScroll, BorderLayout.CENTER);
            formCard = buildForm();
            add(formCard, BorderLayout.EAST);
            refresh();
        }

        private JPanel buildForm() {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setPreferredSize(new Dimension(380, 0));
            card.setBackground(palette.bgCard);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(palette.border),
                    new EmptyBorder(16, 16, 16, 16)));
            JLabel title = new JLabel("Add / Manage Students");
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setForeground(palette.textPrimary);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(title);
            card.add(Box.createVerticalStrut(12));

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

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            buttons.setOpaque(false);
            JButton addBtn = createPrimaryButton("Save Student");
            addBtn.addActionListener(e -> addStudent());
            JButton deleteBtn = createSecondaryButton("Delete Selected");
            deleteBtn.addActionListener(e -> deleteStudent());
            buttons.add(addBtn);
            buttons.add(deleteBtn);
            card.add(buttons);
            return card;
        }

        private JPanel labeledField(String label, JTextField field) {
            JPanel wrapper = new JPanel(new BorderLayout(4, 4));
            wrapper.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setForeground(palette.textSecondary);
            wrapper.add(l, BorderLayout.NORTH);
            wrapper.add(field, BorderLayout.CENTER);
            return wrapper;
        }

        private void addStudent() {
            try {
                String name = normalized(nameField, "Full name");
                String dob = normalized(dobField, "DOB (yyyy-mm-dd)");
                String email = normalized(emailField, "Email address");
                String major = normalized(majorField, "Major");
                LocalDate parsedDob = dob.isBlank() ? null : LocalDate.parse(dob.trim());
                studentService.createStudent(name, parsedDob, email, major);
                clearForm();
                refresh();
                setStatus("Student saved successfully");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
                setStatusFromError(ex);
            }
        }

        private void deleteStudent() {
            int row = table.getSelectedRow();
            if (row < 0) {
                setStatus("Select a record to delete");
                return;
            }
            Student student = model.getStudentAt(row);
            studentService.delete(student.getId());
            refresh();
            setStatus("Deleted student: " + student.getName());
        }

        private void clearForm() {
            nameField.setText("Full name");
            dobField.setText("DOB (yyyy-mm-dd)");
            emailField.setText("Email address");
            majorField.setText("Major");
            nameField.setForeground(palette.textSecondary);
            dobField.setForeground(palette.textSecondary);
            emailField.setForeground(palette.textSecondary);
            majorField.setForeground(palette.textSecondary);
        }

        void refresh() {
            loadTable(studentService.listStudents());
        }

        int search(String query) {
            if (query == null || query.isBlank()) {
                refresh();
                return model.getRowCount();
            }
            loadTable(studentService.search(query));
            return model.getRowCount();
        }

        private void loadTable(List<Student> data) {
            model = new StudentTableModel(data);
            table.setModel(model);
            styleTable(table);
        }

        void applyTheme() {
            formCard.setBackground(palette.bgCard);
            formCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(palette.border),
                    new EmptyBorder(16, 16, 16, 16)));
            styleTextField(nameField, "Full name");
            styleTextField(dobField, "DOB (yyyy-mm-dd)");
            styleTextField(emailField, "Email address");
            styleTextField(majorField, "Major");
            tableScroll.getViewport().setBackground(palette.bgCard);
            styleTable(table);
        }
    }

    private final class CoursesPanel extends JPanel {
        private final DefaultListModel<Course> model = new DefaultListModel<>();
        private final JList<Course> list = new JList<>(model);
        private final JTextField codeField = new JTextField();
        private final JTextField titleField = new JTextField();
        private final JTextField instructorField = new JTextField();
        private final JTextField scheduleField = new JTextField();
        private final JTextField capacityField = new JTextField();
        private final JTextField categoryField = new JTextField();
        private JPanel form;

        CoursesPanel() {
            setOpaque(false);
            setLayout(new BorderLayout(16, 16));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            list.setCellRenderer((ListCellRenderer<Course>) (lst, value, index, isSelected, cellHasFocus) -> {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setBackground(isSelected ? palette.accent : translucent(0.45f));
                JLabel lbl = new JLabel(value.getCode() + " • " + value.getTitle());
                lbl.setBorder(new EmptyBorder(8, 12, 8, 12));
                lbl.setForeground(isSelected ? Color.WHITE : palette.textPrimary);
                panel.add(lbl, BorderLayout.CENTER);
                return panel;
            });
            JScrollPane scroll = new JScrollPane(list);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            add(scroll, BorderLayout.CENTER);
            form = buildForm();
            add(form, BorderLayout.SOUTH);
            refresh();
        }

        private JPanel buildForm() {
            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            panel.setOpaque(false);
            addField(panel, "Code", codeField);
            addField(panel, "Title", titleField);
            addField(panel, "Instructor", instructorField);
            addField(panel, "Schedule", scheduleField);
            addField(panel, "Capacity", capacityField);
            addField(panel, "Category", categoryField);
            JButton createBtn = createPrimaryButton("Create course");
            createBtn.addActionListener(e -> addCourse());
            JButton deleteBtn = createSecondaryButton("Delete selected");
            deleteBtn.addActionListener(e -> deleteCourse());
            panel.add(createBtn);
            panel.add(deleteBtn);
            return panel;
        }

        private void addField(JPanel panel, String label, JTextField field) {
            JLabel lbl = new JLabel(label);
            lbl.setForeground(Color.WHITE);
            styleInputField(field);
            panel.add(lbl);
            panel.add(field);
        }

        private void addCourse() {
            try {
                int cap = Integer.parseInt(capacityField.getText().trim());
                courseService.create(
                        codeField.getText().trim(),
                        titleField.getText().trim(),
                        instructorField.getText().trim(),
                        scheduleField.getText().trim(),
                        cap,
                        categoryField.getText().trim());
                clear();
                refresh();
                setStatus("Course created");
            } catch (Exception ex) {
                setStatusFromError(ex);
            }
        }

        private void deleteCourse() {
            Course course = list.getSelectedValue();
            if (course == null) {
                setStatus("Select a course first");
                return;
            }
            courseService.delete(course.getId());
            refresh();
            setStatus("Course removed: " + course.getCode());
        }

        private void clear() {
            codeField.setText("");
            titleField.setText("");
            instructorField.setText("");
            scheduleField.setText("");
            capacityField.setText("");
            categoryField.setText("");
        }

        void refresh() {
            model.clear();
            courseService.all().forEach(model::addElement);
        }

        void applyTheme() {
            list.repaint();
            styleInputField(codeField);
            styleInputField(titleField);
            styleInputField(instructorField);
            styleInputField(scheduleField);
            styleInputField(capacityField);
            styleInputField(categoryField);
        }
    }

    private final class EnrollmentPanel extends JPanel {
        private final DefaultListModel<Enrollment> model = new DefaultListModel<>();
        private final JList<Enrollment> list = new JList<>(model);
        private final JTextField studentIdField = new JTextField();
        private final JTextField courseIdField = new JTextField();

        EnrollmentPanel() {
            setOpaque(false);
            setLayout(new BorderLayout(16, 16));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            list.setCellRenderer((ListCellRenderer<Enrollment>) (lst, value, index, isSelected, cellHasFocus) -> {
                JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setBackground(isSelected ? palette.accent : translucent(0.35f));
                JLabel lbl = new JLabel(value.getId() + " • S=" + value.getStudentId() + " • C=" + value.getCourseId() + " • " + value.getStatus());
                lbl.setForeground(isSelected ? Color.WHITE : palette.textPrimary);
                lbl.setBorder(new EmptyBorder(8, 12, 8, 12));
                panel.add(lbl, BorderLayout.CENTER);
                return panel;
            });
            JScrollPane scroll = new JScrollPane(list);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            add(scroll, BorderLayout.CENTER);

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setOpaque(false);
            addField(form, "Student UID", studentIdField);
            addField(form, "Course UID", courseIdField);
            JButton enrollBtn = createPrimaryButton("Enroll");
            enrollBtn.addActionListener(e -> enroll());
            JButton dropBtn = createSecondaryButton("Drop selected");
            dropBtn.addActionListener(e -> drop());
            form.add(enrollBtn);
            form.add(dropBtn);
            add(form, BorderLayout.SOUTH);
            refresh();
        }

        private void addField(JPanel panel, String label, JTextField field) {
            JLabel lbl = new JLabel(label);
            lbl.setForeground(Color.WHITE);
            styleInputField(field);
            panel.add(lbl);
            panel.add(field);
        }

        private void enroll() {
            try {
                UUID studentId = UUID.fromString(studentIdField.getText().trim());
                UUID courseId = UUID.fromString(courseIdField.getText().trim());
                enrollmentService.enroll(studentId, courseId);
                refresh();
                setStatus("Enrollment created");
            } catch (Exception ex) {
                setStatusFromError(ex);
            }
        }

        private void drop() {
            Enrollment selection = list.getSelectedValue();
            if (selection == null) {
                setStatus("Select an enrollment entry");
                return;
            }
            enrollmentService.drop(selection.getId());
            refresh();
            setStatus("Enrollment dropped");
        }

        void refresh() {
            model.clear();
            enrollmentService.all().forEach(model::addElement);
        }

        void applyTheme() {
            list.repaint();
            styleInputField(studentIdField);
            styleInputField(courseIdField);
        }
    }

    private final class ReportsPanel extends JPanel {
        private final DefaultListModel<String> model = new DefaultListModel<>();
        private final JList<String> list = new JList<>(model);

        ReportsPanel() {
            setOpaque(false);
            setLayout(new BorderLayout(16, 16));
            setBorder(new EmptyBorder(16, 16, 16, 16));
            list.setBackground(translucent(0.45f));
            list.setForeground(Color.WHITE);
            list.setFont(new Font("Consolas", Font.PLAIN, 14));
            add(new JScrollPane(list), BorderLayout.CENTER);
            JButton refresh = createPrimaryButton("Generate snapshot");
            refresh.addActionListener(e -> refresh());
            add(refresh, BorderLayout.SOUTH);
            refresh();
        }

        void refresh() {
            model.clear();
            List<ReportSummary> summaries = reportService.latestReports(
                    studentService.listStudents().size(),
                    courseService.all().size(),
                    enrollmentService.all().size());
            summaries.forEach(summary -> model.addElement(summary.title() + " -> " + summary.value() + " (" + summary.description() + ")"));
        }

        void applyTheme() {
            list.setBackground(translucent(0.45f));
            list.repaint();
        }
    }

    private final class SettingsPanel extends JPanel {
        private final JCheckBox darkToggle = new JCheckBox("Enable dark mode");

        SettingsPanel() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(32, 32, 32, 32));
            JLabel title = new JLabel("Preferences");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(title);
            add(Box.createVerticalStrut(16));
            darkToggle.setOpaque(false);
            darkToggle.setForeground(Color.WHITE);
            darkToggle.setSelected(darkMode);
            darkToggle.addItemListener(e -> setTheme(e.getStateChange() == ItemEvent.SELECTED));
            darkToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(darkToggle);
            add(Box.createVerticalStrut(16));
            JButton refreshBtn = createSecondaryButton("Refresh modules");
            refreshBtn.addActionListener(e -> {
                refreshAllModules();
                setStatus("Modules refreshed");
            });
            refreshBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(refreshBtn);
        }

        void refresh() {
            darkToggle.setSelected(darkMode);
        }

        void applyTheme() {
            darkToggle.setForeground(Color.WHITE);
        }
    }

    private final class Palette {
        final Color bgCard;
        final Color border;
        final Color textPrimary;
        final Color textSecondary;
        final Color accent;
        final Color zebraEven;
        final Color zebraOdd;
        final Color headerBg;
        final Color headerFg;

        Palette(boolean dark) {
            if (dark) {
                bgCard = new Color(32, 34, 38);
                border = new Color(70, 70, 70);
                textPrimary = new Color(235, 235, 235);
                textSecondary = new Color(170, 170, 170);
                accent = new Color(100, 181, 246);
                zebraEven = new Color(38, 40, 44);
                zebraOdd = new Color(45, 47, 51);
                headerBg = new Color(36, 38, 44);
                headerFg = Color.WHITE;
            } else {
                bgCard = Color.WHITE;
                border = new Color(225, 225, 225);
                textPrimary = new Color(40, 40, 40);
                textSecondary = new Color(110, 110, 110);
                accent = new Color(33, 150, 243);
                zebraEven = Color.WHITE;
                zebraOdd = new Color(248, 249, 251);
                headerBg = new Color(245, 245, 245);
                headerFg = new Color(40, 40, 40);
            }
        }
    }

    private class WallpaperPanel extends JPanel {
        private BufferedImage texture;

        WallpaperPanel() {
            try {
                java.net.URL url = MainFrame.class.getResource("/wallpaper.jpg");
                if (url != null) {
                    Image image = Toolkit.getDefaultToolkit().createImage(url);
                    MediaTracker tracker = new MediaTracker(this);
                    tracker.addImage(image, 0);
                    tracker.waitForID(0);
                    if (image.getWidth(this) > 0 && image.getHeight(this) > 0) {
                        texture = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = texture.createGraphics();
                        g2.drawImage(image, 0, 0, null);
                        g2.dispose();
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } catch (Exception ignored) {
                texture = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (texture != null) {
                for (int x = 0; x < getWidth(); x += texture.getWidth()) {
                    for (int y = 0; y < getHeight(); y += texture.getHeight()) {
                        g2.drawImage(texture, x, y, null);
                    }
                }
            } else {
                g2.setPaint(new GradientPaint(0, 0, new Color(15, 32, 39), getWidth(), getHeight(), new Color(32, 58, 67)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }
    }

    private final class RoundedPanel extends JPanel {
        RoundedPanel(LayoutManager manager) {
            super(manager);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, darkMode ? 150 : 90));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
