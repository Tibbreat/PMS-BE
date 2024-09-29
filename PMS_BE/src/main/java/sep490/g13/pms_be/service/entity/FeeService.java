package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.Fee;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.FeeRepo;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class FeeService {
    @Autowired
    private FeeRepo feeRepo;

    @Autowired
    private ChildrenRepo childrenRepository;

    public Fee addFee(String feeTitle, Boolean isActive) {
        Fee newFee = new Fee();
        newFee.setFeeTitle(feeTitle);
        newFee.setIsActive(isActive);

        return feeRepo.save(newFee);
    }
    public Page<Fee> getList(Pageable pageable) {
        return feeRepo.findAll(pageable);
    }
    @Transactional
    public Fee changeFeeStatus(String feeId, Boolean newStatus) {
        Optional<Fee> optionalFee = feeRepo.findById(feeId);
        if (optionalFee.isPresent()) {
            Fee fee = optionalFee.get();
            fee.setIsActive(newStatus);
            return feeRepo.save(fee);
        } else {
            throw new IllegalArgumentException("Fee not found with ID: " + feeId);
        }
    }


}
