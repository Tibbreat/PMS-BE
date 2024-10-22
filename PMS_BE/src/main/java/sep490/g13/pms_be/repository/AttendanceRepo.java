package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.AttendanceLog;
import sep490.g13.pms_be.model.response.attendance.LogOfChildren;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<AttendanceLog, String> {
    @Query("SELECT COUNT(al) > 0 FROM AttendanceLog al WHERE al.attendanceDate = :date")
    boolean existsByAttendanceDate(LocalDate date);

    @Query("SELECT new sep490.g13.pms_be.model.response.attendance.LogOfChildren(" +
            "al.children.id, al.children.childName, al.children.imageUrl, " +
            "al.checkinTime, al.checkoutTime, al.note) " +
            "FROM AttendanceLog al WHERE al.classes.id = :classId")
    List<LogOfChildren> findAllByClassId(String classId);
}
