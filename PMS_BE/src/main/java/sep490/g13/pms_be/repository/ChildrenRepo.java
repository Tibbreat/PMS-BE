package sep490.g13.pms_be.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Children;
import org.springframework.data.domain.Pageable;
import sep490.g13.pms_be.model.response.attendance.LogOfChildren;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildrenRepo extends JpaRepository<Children, String> {

    @Query("SELECT new sep490.g13.pms_be.model.response.children.ChildrenListResponse(" +
            "ch.id, ch.childName, ch.childBirthDate, sc.id,sc.className, ch.imageUrl, ch.gender, " +
            "(SELECT rlFather.parentId.fullName FROM Relationship rlFather WHERE rlFather.childrenId.id = ch.id AND rlFather.relationship = 'Father'), " +
            "(SELECT rlMother.parentId.fullName FROM Relationship rlMother WHERE rlMother.childrenId.id = ch.id AND rlMother.relationship = 'Mother')) " +
            "FROM Children ch " +
            "LEFT JOIN ch.schoolClass sc " +
            "WHERE (:classId IS NULL OR sc.id = :classId) " +
            "AND (:childName IS NULL OR ch.childName LIKE CONCAT('%', :childName, '%')) " +
            "ORDER BY ch.childName")
    Page<ChildrenListResponse> findChildrenByFilter(
            @Param("classId") String classId,
            @Param("childName") String childName,
            Pageable pageable);

    @Query("SELECT new sep490.g13.pms_be.model.response.attendance.LogOfChildren(" +
            "al.children.id, al.children.childName, al.children.imageUrl, " +
            "al.checkinTime, al.checkoutTime, al.note) " +
            "FROM AttendanceLog al WHERE al.classes.id = :classId")
    List<LogOfChildren> findAllByClassId(String classId);


    @Query("SELECT new sep490.g13.pms_be.model.response.children.ChildrenDetailResponse(" +
            "ch.childName, ch.childBirthDate, ch.childAddress, ch.isRegisteredForTransport, ch.isRegisteredForBoarding, ch.imageUrl, ch.birthAddress, " +
            "ch.nationality, ch.religion, ch.gender, " +
            "father.parentId.id, father.parentId.fullName, father.parentId.phone, " +
            "mother.parentId.id, mother.parentId.fullName, mother.parentId.phone) " +
            "FROM Children ch " +
            "LEFT JOIN Relationship father ON father.childrenId.id = ch.id AND father.relationship = 'Father' " +
            "LEFT JOIN Relationship mother ON mother.childrenId.id = ch.id AND mother.relationship = 'Mother' " +
            "WHERE ch.id = :childrenId")
    ChildrenDetailResponse findChildrenDetailById(@Param("childrenId") String childrenId);

    @Modifying
    @Query("UPDATE Children ch SET ch.isRegisteredForTransport = :status WHERE ch.id = :childrenId")
    int updateTransportServiceStatus(@Param("childrenId") String childrenId, @Param("status") Boolean status);

    @Modifying
    @Query("UPDATE Children ch SET ch.isRegisteredForBoarding = :status WHERE ch.id = :childrenId")
    int updateBoardingServiceStatus(@Param("childrenId") String childrenId, @Param("status") Boolean status);

}
