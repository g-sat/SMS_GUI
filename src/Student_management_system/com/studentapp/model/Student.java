package Student_management_system.com.studentapp.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Student extends Person {
    private String email;
    private String major;

    public Student(UUID id, String name, LocalDate dob, String email, String major) {
        super(Objects.requireNonNull(id, "id"), name, dob);
        setEmail(email);
        setMajor(major);
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        this.email = email.trim();
    }

    public String getMajor() {
        return major;
    }

    public final void setMajor(String major) {
        this.major = (major == null) ? "Undeclared" : major.trim();
    }
}
