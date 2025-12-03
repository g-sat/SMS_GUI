package Student_management_system.com.studentapp.service;

import Student_management_system.com.studentapp.model.ReportSummary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    public List<ReportSummary> latestReports(int totalStudents, int totalCourses, int totalEnrollments) {
        List<ReportSummary> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        list.add(new ReportSummary("Enrollment Snapshot", "Total active enrollments", String.valueOf(totalEnrollments)));
        list.add(new ReportSummary("Student Count", "Registered learners", String.valueOf(totalStudents)));
        list.add(new ReportSummary("Courses", "Published courses", String.valueOf(totalCourses)));
        list.add(new ReportSummary("Generated", "Report date", fmt.format(LocalDate.now())));
        return list;
    }
}
