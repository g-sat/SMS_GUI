package Student_management_system.com.studentapp.repository;

import Student_management_system.com.studentapp.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JdbcStudentRepository implements StudentRepositoryContract {
    private final String url;
    private final String user;
    private final String password;

    public JdbcStudentRepository(String url, String user, String password) {
        this.url = Objects.requireNonNull(url);
        this.user = Objects.requireNonNullElse(user, "");
        this.password = Objects.requireNonNullElse(password, "");
        ensureSchema();
    }

    private void ensureSchema() {
        try (Connection c = getConn()) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS students (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255) NOT NULL)");
            }
            addColumnIfMissing(c, "students", "uid", "VARCHAR(36) NOT NULL");
            addUniqueIfMissing(c, "students", "uid");
            addColumnIfMissing(c, "students", "dob", "INT NULL");
            addColumnIfMissing(c, "students", "email", "VARCHAR(255) NOT NULL");
            addColumnIfMissing(c, "students", "major", "VARCHAR(255) NOT NULL");
            // populate uid for any rows missing it
            try (PreparedStatement ps = c.prepareStatement("UPDATE students SET uid=? WHERE uid IS NULL OR uid=''")) {
                // generate per-row is tricky without looping; do a best-effort fill
            }
            // loop to fill missing uids
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT id FROM students WHERE uid IS NULL OR uid=''")) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    try (PreparedStatement ups = c.prepareStatement("UPDATE students SET uid=? WHERE id=?")) {
                        ups.setString(1, java.util.UUID.randomUUID().toString());
                        ups.setInt(2, id);
                        ups.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure schema", e);
        }
    }

    private void addUniqueIfMissing(Connection c, String table, String column) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.statistics WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME=? AND INDEX_NAME=?";
        try (PreparedStatement ps = c.prepareStatement(check)) {
            ps.setString(1, table);
            ps.setString(2, column + "_uniq");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int cnt = rs.getInt(1);
                if (cnt == 0) {
                    try (Statement st = c.createStatement()) {
                        st.executeUpdate("CREATE UNIQUE INDEX " + column + "_uniq ON " + table + "(" + column + ")");
                    }
                }
            }
        }
    }

    private Integer toIntDate(LocalDate d) {
        if (d == null) return null;
        int y = d.getYear();
        int m = d.getMonthValue();
        int day = d.getDayOfMonth();
        return y * 10000 + m * 100 + day;
    }

    private LocalDate fromIntDate(Integer v) {
        if (v == null || v == 0) return null;
        int y = v / 10000;
        int m = (v / 100) % 100;
        int d = v % 100;
        try {
            return LocalDate.of(y, m, d);
        } catch (Exception e) {
            return null;
        }
    }

    private void addColumnIfMissing(Connection c, String table, String column, String definition) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = c.prepareStatement(check)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int cnt = rs.getInt(1);
                if (cnt == 0) {
                    String alter = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition;
                    try (Statement st = c.createStatement()) {
                        st.executeUpdate(alter);
                    }
                }
            }
        }
    }

    private Connection getConn() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Student save(Student s) {
        String sqlInsert = "INSERT INTO students(uid, name, dob, email, major) VALUES (?,?,?,?,?)"
                + " ON DUPLICATE KEY UPDATE name=VALUES(name), dob=VALUES(dob), email=VALUES(email), major=VALUES(major)";
        try (Connection c = getConn(); PreparedStatement ps = c.prepareStatement(sqlInsert)) {
            ps.setString(1, s.getId().toString());
            ps.setString(2, s.getName());
            Integer d = toIntDate(s.getDob());
            if (d == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, d);
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getMajor());
            ps.executeUpdate();
            return s;
        } catch (SQLException e) {
            throw new RuntimeException("DB save failed (" + e.getSQLState() + "/" + e.getErrorCode() + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findById(UUID id) {
        String sql = "SELECT uid, name, dob, email, major FROM students WHERE uid=?";
        try (Connection c = getConn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB findById failed", e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT uid, name, dob, email, major FROM students ORDER BY name";
        try (Connection c = getConn(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Student> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB findAll failed (" + e.getSQLState() + "/" + e.getErrorCode() + "): " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM students WHERE uid=?";
        try (Connection c = getConn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB delete failed (" + e.getSQLState() + "/" + e.getErrorCode() + "): " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> searchByName(String q) {
        String sql = "SELECT uid, name, dob, email, major FROM students WHERE LOWER(name) LIKE ? ORDER BY name";
        try (Connection c = getConn(); PreparedStatement ps = c.prepareStatement(sql)) {
            String needle = (q == null || q.isBlank()) ? "%" : ("%" + q.trim().toLowerCase(Locale.ROOT) + "%");
            ps.setString(1, needle);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB search failed (" + e.getSQLState() + "/" + e.getErrorCode() + "): " + e.getMessage(), e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        java.util.UUID id = java.util.UUID.fromString(rs.getString("uid"));
        String name = rs.getString("name");
        Integer d = rs.getObject("dob") == null ? null : rs.getInt("dob");
        LocalDate dob = fromIntDate(d);
        String email = rs.getString("email");
        String major = rs.getString("major");
        return new Student(id, name, dob, email, major);
    }
}