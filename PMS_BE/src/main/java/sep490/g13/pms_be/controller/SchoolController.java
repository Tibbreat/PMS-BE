package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.School;
import sep490.g13.pms_be.model.request.school.AddSchoolRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.SchoolService;

@RestController
@RequestMapping("/pms/school")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addSchool(@RequestBody AddSchoolRequest request) {
        return ResponseEntity.ok(ResponseModel.<School>builder()
                .data(schoolService.saveSchool(request))
                .message("School added successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> getSchools(@RequestParam String principalId) {
        return ResponseEntity.ok(ResponseModel.<School>builder()
                .data(schoolService.getSchools(principalId))
                .message("Schools fetched successfully")
                .build());
    }
}
