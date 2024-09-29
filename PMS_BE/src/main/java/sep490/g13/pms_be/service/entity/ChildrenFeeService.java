package sep490.g13.pms_be.service.entity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.ChildrenFee;
import sep490.g13.pms_be.entities.Fee;
import sep490.g13.pms_be.repository.ChildrenFeeRepo;

import sep490.g13.pms_be.repository.ChildrenRepo;

import sep490.g13.pms_be.repository.FeeRepo;


import java.time.LocalDate;
import java.util.Optional;

@Service
public class ChildrenFeeService {

    @Autowired
    private ChildrenFeeRepo childrenFeeRepository;

    @Autowired
    private ChildrenRepo childrenRepository;

    @Autowired
    private FeeRepo feeRepository;

    public ChildrenFee addFeeToStudent(String childId, String feeId, String amount, LocalDate dueDate) {
        Optional<Children> optionalChild = childrenRepository.findById(childId);
        Optional<Fee> optionalFee = feeRepository.findById(feeId);

        if (optionalChild.isPresent() && optionalFee.isPresent()) {
            Children child = optionalChild.get();
            Fee fee = optionalFee.get();

            ChildrenFee childrenFee = new ChildrenFee();
            childrenFee.setChildren(child);
            childrenFee.setFee(fee);
            childrenFee.setAmount(amount);
            childrenFee.setDueDate(dueDate);
            childrenFee.setIsPayed(false);  // Default chưa thanh toán

            return childrenFeeRepository.save(childrenFee);
        } else {
            throw new IllegalArgumentException("Child or Fee not found");
        }
    }
    public ChildrenFee updatePaymentStatus(String childrenFeeId, Boolean isPayed) {
        Optional<ChildrenFee> optionalChildrenFee = childrenFeeRepository.findById(childrenFeeId);

        if (optionalChildrenFee.isPresent()) {
            ChildrenFee childrenFee = optionalChildrenFee.get();
            childrenFee.setIsPayed(isPayed);
            return childrenFeeRepository.save(childrenFee);
        } else {
            throw new IllegalArgumentException("ChildrenFee not found with ID: " + childrenFeeId);
        }
    }
}