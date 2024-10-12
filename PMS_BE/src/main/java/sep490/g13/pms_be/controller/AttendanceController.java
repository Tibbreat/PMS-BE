package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
