package Student_management_system.com.studentapp.repository;

import Student_management_system.com.studentapp.model.Student;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StudentRepository implements StudentRepositoryContract {
    private final Map<UUID, Student> store = new ConcurrentHashMap<>();

    public Student save(Student s) {
        store.put(s.getId(), s);
        return s;
    }

    public Optional<Student> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Student> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(UUID id) {
        store.remove(id);
    }

    public List<Student> searchByName(String q) {
        if (q == null || q.isBlank()) return findAll();
        String low = q.trim().toLowerCase(Locale.ROOT);
        return store.values().stream()
                .filter(s -> s.getName().toLowerCase(Locale.ROOT).contains(low))
                .collect(Collectors.toList());
    }
}