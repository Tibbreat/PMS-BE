package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.repository.FoodServiceProviderRepo;

@Service
public class FoodServiceProviderService {

    @Autowired
    private FoodServiceProviderRepo foodServiceProviderRepo;

    public FoodServiceProvider addProvider(FoodServiceProvider fsp) {
        return foodServiceProviderRepo.save(fsp);
    }

    public Page<FoodServiceProvider> getProvider(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return foodServiceProviderRepo.findByProvider(status, pageable);
    }
}

