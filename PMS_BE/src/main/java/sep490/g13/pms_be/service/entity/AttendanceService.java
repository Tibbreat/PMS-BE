package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.AttendanceLog;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.repository.AttendanceRepo;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.UserRepo;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepo attendanceRepository;

    @Autowired
    private ChildrenRepo childrenRepository;

    @Autowired
    private UserRepo userRepository;

    public AttendanceLog saveAttendanceLog(String childrenId, String userId) {
        Children children = childrenRepository.findById(childrenId).orElseThrow(() -> new IllegalArgumentException("Invalid children ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        AttendanceLog log = AttendanceLog.builder()
                .children(children)
                .user(user)
                .build();

        return attendanceRepository.save(log);
    }
}