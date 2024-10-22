package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.School;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.request.school.AddSchoolRequest;
import sep490.g13.pms_be.repository.SchoolRepo;
import sep490.g13.pms_be.repository.UserRepo;

@Service
public class SchoolService {
    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private UserRepo userRepo;

    public School saveSchool(AddSchoolRequest addSchoolRequest) {
        School school = new School();
        BeanUtils.copyProperties(addSchoolRequest, school);
        User principal = userRepo.findById(addSchoolRequest.getPrincipalId()).orElseThrow(() -> new RuntimeException("User not found"));
        school.setPrincipal(principal);
        return schoolRepo.save(school);
    }

    public School getSchools(String principalId) {
        return schoolRepo.findByPrincipalId(principalId);
    }

}
