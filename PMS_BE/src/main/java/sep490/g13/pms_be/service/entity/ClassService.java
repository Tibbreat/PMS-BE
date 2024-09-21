package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private UserRepo userRepo;

    public List<Classes> findAll(){
        return classRepo.findAll();
    }
    public Classes addClass(Classes c){
        // Check User co ton tai hay khong

        // Kiểm tra User (Manager) có tồn tại hay không
        Optional<User> managerOpt = userRepo.findById(c.getManager().getId());
        if (managerOpt.isEmpty()) {
            throw new RuntimeException("Manager không tồn tại");
        }

        // Kiểm tra danh sách giáo viên có tồn tại hay không (tuỳ thuộc vào logic yêu cầu)
        for (ClassTeacher teacher : c.getTeachers()) {
            Optional<User> teacherOpt = userRepo.findById(teacher.getTeacherId().getId());
            if (teacherOpt.isEmpty()) {
                throw new RuntimeException("Giáo viên không tồn tại: " + teacher.getTeacherId().getId());
            }
        }

        // Lưu lớp học vào database
        return classRepo.save(c);
    }

}
