package Student_management_system.com.studentapp.service;

import Student_management_system.com.studentapp.model.Student;
import Student_management_system.com.studentapp.repository.StudentRepositoryContract;
import Student_management_system.com.studentapp.util.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class StudentService {
    private final StudentRepositoryContract repo;

    public StudentService(StudentRepositoryContract repo) {
        this.repo = repo;
    }

    public Student createStudent(String name, LocalDate dob, String email, String major) {
        Validator.requireNonBlank(name, "name");
        Validator.requireValidEmail(email);
        UUID id = UUID.randomUUID();
        Student s = new Student(id, name, dob, email, major);
        return repo.save(s);
    }

    public List<Student> listStudents() {
        return repo.findAll();
    }

    public List<Student> search(String q) {
        return repo.searchByName(q);
    }

    public void delete(UUID id) {
        repo.delete(id);
    }

    public Student updateEmail(UUID id, String email) {
        Validator.requireValidEmail(email);
        Student s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        s.setEmail(email);
        repo.save(s);
        return s;
    }
}