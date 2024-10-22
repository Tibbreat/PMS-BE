package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.AttendanceLog;
import sep490.g13.pms_be.model.request.log.AttendanceLogRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.AttendanceService;

@RestController
@RequestMapping("/pms/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<ResponseModel<?>> checkin(@RequestBody AttendanceLogRequest request) {

        return ResponseEntity.ok(ResponseModel.<AttendanceLog>builder()
                        .message("Checkin success")
                        .data(attendanceService.saveAttendanceLog(request.getChildrenId(), request.getUserId()))
                .build());

    }

    @PostMapping("/base-log")
    public ResponseEntity<ResponseModel<?>> baseLog(@RequestParam String classId, @RequestParam String userId) {
        attendanceService.createBaseLog(classId, userId);
        return ResponseEntity.ok(ResponseModel.builder()
                .message("Base log created")
                .data(null)
                .build());
    }

    @GetMapping("/class-log")
    public ResponseEntity<ResponseModel<?>> classLog(@RequestParam String classId) {
        return ResponseEntity.ok(ResponseModel.builder()
                .message("Class log")
                .data(attendanceService.findAllByClassId(classId))
                .build());
    }
}
