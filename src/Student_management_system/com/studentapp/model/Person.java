package Student_management_system.com.studentapp.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public abstract class Person {
    private final UUID id;
    private String name;
    private LocalDate dob;

    protected Person(UUID id, String name, LocalDate dob) {
        this.id = Objects.requireNonNull(id, "id");
        setName(name);
        setDob(dob);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name.trim();
    }

    public LocalDate getDob() {
        return dob;
    }

    public final void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
