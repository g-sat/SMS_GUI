package Student_management_system.com.studentapp.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Enrollment {
    private final UUID id;
    private final UUID studentId;
    private final UUID courseId;
    private final LocalDate enrolledOn;
    private String status;

    public Enrollment(UUID id, UUID studentId, UUID courseId, LocalDate enrolledOn, String status) {
        this.id = Objects.requireNonNull(id, "id");
        this.studentId = Objects.requireNonNull(studentId, "studentId");
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.enrolledOn = Objects.requireNonNullElse(enrolledOn, LocalDate.now());
        setStatus(status);
    }

    public UUID getId() {
        return id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public LocalDate getEnrolledOn() {
        return enrolledOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status is required");
        this.status = status.trim();
    }
}
