package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import sep490.g13.pms_be.entities.AttendanceLog;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.response.attendance.LogOfChildren;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;
import sep490.g13.pms_be.repository.AttendanceRepo;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepo attendanceRepository;

    @Autowired
    private ChildrenRepo childrenRepository;

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private ClassRepo classRepo;

    public AttendanceLog saveAttendanceLog(String childrenId, String userId) {
        Children children = childrenRepository.findById(childrenId).orElseThrow(() -> new IllegalArgumentException("Invalid children ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        AttendanceLog log = AttendanceLog.builder()
                .children(children)
                .build();

        return attendanceRepository.save(log);
    }

    public void createBaseLog(String classId, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Classes classes = classRepo.findById(classId).orElseThrow(() -> new IllegalArgumentException("Invalid class ID"));
        LocalDate today = LocalDate.now();
        boolean logExists = attendanceRepository.existsByAttendanceDate(today);

        if (logExists) {
            return;
        }
        List<LogOfChildren> childrenList = childrenRepository.findAllByClassId(classId);
        for (LogOfChildren childrenResponse : childrenList) {
            Children currentChildren = childrenRepository.findById(childrenResponse.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid children ID"));

            // Tạo log mới cho từng trẻ trong lớp
            AttendanceLog log = AttendanceLog.builder()
                    .children(currentChildren)
                    .classes(classes)
                    .attendanceDate(today)
                    .build();
            log.setCreatedBy(userId);
            attendanceRepository.save(log);
        }
    }

    public List<LogOfChildren> findAllByClassId(String classId) {
        return attendanceRepository.findAllByClassId(classId);
    }

}