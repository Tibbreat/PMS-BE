package sep490.g13.pms_be.service.entity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.repository.ChildrenFeeRepo;

import sep490.g13.pms_be.repository.ChildrenRepo;

import sep490.g13.pms_be.repository.FeeRepo;

@Service
public class ChildrenFeeService {

    @Autowired
    private ChildrenFeeRepo childrenFeeRepository;

    @Autowired
    private ChildrenRepo childrenRepository;

    @Autowired
    private FeeRepo feeRepository;


}