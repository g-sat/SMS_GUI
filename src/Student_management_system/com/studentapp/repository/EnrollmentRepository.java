package Student_management_system.com.studentapp.repository;

import Student_management_system.com.studentapp.model.Enrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class EnrollmentRepository {
    private final ConcurrentMap<UUID, Enrollment> store = new ConcurrentHashMap<>();

    public Enrollment save(Enrollment e) {
        store.put(e.getId(), e);
        return e;
    }

    public void delete(UUID id) {
        store.remove(id);
    }

    public List<Enrollment> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Enrollment> findByStudent(UUID studentId) {
        return store.values().stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Enrollment> findByCourse(UUID courseId) {
        return store.values().stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }
}
