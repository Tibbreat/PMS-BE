package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.ChildrenFee;
import sep490.g13.pms_be.entities.Fee;
import sep490.g13.pms_be.model.request.fee.AddFeeToChildrenRequest;
import sep490.g13.pms_be.model.request.fee.UpdatePaymentStatusRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.ChildrenFeeService;
import sep490.g13.pms_be.service.entity.FeeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pms/fee")
public class FeeController {
    @Autowired
    private FeeService feeService;
    @Autowired
    private ChildrenFeeService childrenFeeService;




}
