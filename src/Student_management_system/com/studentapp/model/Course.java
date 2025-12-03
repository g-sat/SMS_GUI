package Student_management_system.com.studentapp.model;

import java.util.Objects;
import java.util.UUID;

public final class Course {
    private final UUID id;
    private String code;
    private String title;
    private String instructor;
    private String schedule;
    private int capacity;
    private String category;

    public Course(UUID id, String code, String title, String instructor, String schedule, int capacity, String category) {
        this.id = Objects.requireNonNull(id, "id");
        setCode(code);
        setTitle(title);
        setInstructor(instructor);
        setSchedule(schedule);
        setCapacity(capacity);
        setCategory(category);
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Course code is required");
        this.code = code.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Course title is required");
        this.title = title.trim();
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        if (instructor == null || instructor.isBlank()) throw new IllegalArgumentException("Instructor is required");
        this.instructor = instructor.trim();
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        if (schedule == null || schedule.isBlank()) throw new IllegalArgumentException("Schedule is required");
        this.schedule = schedule.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 5) throw new IllegalArgumentException("Capacity must be at least 5");
        this.capacity = capacity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category == null || category.isBlank()) ? "General" : category.trim();
    }

    @Override
    public String toString() {
        return code + " • " + title;
    }
}
