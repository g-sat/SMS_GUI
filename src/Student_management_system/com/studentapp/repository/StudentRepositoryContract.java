package Student_management_system.com.studentapp.repository;

import Student_management_system.com.studentapp.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepositoryContract {
    Student save(Student s);
    Optional<Student> findById(UUID id);
    List<Student> findAll();
    void delete(UUID id);
    List<Student> searchByName(String q);
}
