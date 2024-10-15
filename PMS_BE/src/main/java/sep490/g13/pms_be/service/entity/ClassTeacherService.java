package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.response.user.TeacherOfClassResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.ClassTeacherRepo;
import sep490.g13.pms_be.repository.UserRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ClassTeacherService {
    @Autowired
    private ClassTeacherRepo classTeacherRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ClassRepo classesRepo;
    @Autowired
    private ClassRepo classRepo;

    public void addTeacherIntoClass(String classId, List<String> teacherIds) {
        Optional<Classes> schoolClassOpt = classesRepo.findById(classId);

        if (schoolClassOpt.isPresent()) {
            Classes schoolClass = schoolClassOpt.get();

            for (String teacherId : teacherIds) {
                Optional<User> teacherOpt = userRepo.findById(teacherId);

                if (teacherOpt.isPresent()) {
                    User teacher = teacherOpt.get();
                    ClassTeacher classTeacher = new ClassTeacher();
                    classTeacher.setSchoolClasses(schoolClass);
                    classTeacher.setTeacherId(teacher);

                    classTeacherRepo.save(classTeacher);
                }
            }
        }
    }

    public Page<Classes> getClassByTeacherId(String teacherId, int size, int page){
        Pageable pageable = PageRequest.of(page, size);
        return classTeacherRepo.findClassesByTeacherId(teacherId, pageable);
    }


}
