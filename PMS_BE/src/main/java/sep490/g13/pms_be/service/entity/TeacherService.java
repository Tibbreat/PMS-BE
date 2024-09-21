package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.repository.TeacherRepo;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepo teacherRepo;
    public List<ClassTeacher> findAllById(List<String> teacherId) {

        List<ClassTeacher> teacherList = teacherRepo.findAllById(teacherId);

        if (teacherId.size() != teacherId.size()) {
            throw new IllegalArgumentException("Some Teacher were not found in the database");
        }

        return teacherList;
    }
}
