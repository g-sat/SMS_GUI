package Student_management_system.com.studentapp.ui;

import Student_management_system.com.studentapp.model.Student;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
	private final List<Student> data;
    private final String[] cols = {"ID", "Name", "DOB", "Email", "Major"};
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    public StudentTableModel(List<Student> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getId();
            case 1 -> s.getName();
            case 2 -> (s.getDob() == null) ? "" : s.getDob().format(fmt);
            case 3 -> s.getEmail();
            case 4 -> s.getMajor();
            default -> null;
        };
    }

    public Student getStudentAt(int row) {
        return data.get(row);
    }
}
