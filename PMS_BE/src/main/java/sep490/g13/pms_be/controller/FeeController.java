package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.ChildrenFee;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.Fee;
import sep490.g13.pms_be.model.request.fee.AddFeeRequest;
import sep490.g13.pms_be.model.request.fee.UpdatePaymentStatusRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.ChildrenFeeService;
import sep490.g13.pms_be.service.entity.FeeService;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.DoubleStream.builder;

@RestController
@RequestMapping("/pms/fee")
public class FeeController {
    @Autowired
    private FeeService feeService;
    @Autowired
    private ChildrenFeeService childrenFeeService;


    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addFee(@RequestParam String feeTitle, @RequestParam Boolean isActive) {
        try{
            Fee addedFee = feeService.addFee(feeTitle, isActive);
            return ResponseEntity.ok(ResponseModel.<Fee>builder()
                    .message("Create Fee Successfully").data(addedFee).build())
                    ;
        }catch(Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message(e.getMessage())
                            .build());
        }

    }
    @GetMapping
    public ResponseEntity<PagedResponseModel<Fee>> getList(@RequestParam(defaultValue = "0") int page
                                            ) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Fee> feePage = feeService.getList(pageable);
        List<Fee> feeList = feePage.getContent();
        PagedResponseModel<Fee> pagedResponse = PagedResponseModel.<Fee>builder()
                .total(feePage.getTotalElements())
                .page(page)
                .size(size)
                .listData(feeList)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @PutMapping("/change-fee-status/{feeId}")
    public ResponseEntity<ResponseModel<Fee>> changeFeeStatus(@PathVariable String feeId, @RequestParam Boolean newStatus) {
        try {
            Fee updatedFee = feeService.changeFeeStatus(feeId, newStatus);
            return ResponseEntity.ok(ResponseModel.<Fee>builder().data(updatedFee).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PostMapping("/add-fee")
    public ResponseEntity<ResponseModel<ChildrenFee>> addFeeToStudent(@RequestBody AddFeeRequest request) {
        LocalDate parsedDueDate = request.getDueDate();
        ChildrenFee childrenFee = childrenFeeService.addFeeToStudent(request.getChildId(), request.getFeeId(), request.getAmount(), parsedDueDate);

        ResponseModel<ChildrenFee> response = ResponseModel.<ChildrenFee>builder()
                .message("Fee added successfully.")
                .data(childrenFee)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-payment-status")
    public ResponseEntity<ResponseModel<ChildrenFee>> updatePaymentStatus(@RequestBody UpdatePaymentStatusRequest request) {
        ChildrenFee updatedChildrenFee = childrenFeeService.updatePaymentStatus(request.getChildrenFeeId(), request.getIsPayed());

        ResponseModel<ChildrenFee> response = ResponseModel.<ChildrenFee>builder()
                .message("Payment status updated successfully.")
                .data(updatedChildrenFee)
                .build();

        return ResponseEntity.ok(response);
    }

}
