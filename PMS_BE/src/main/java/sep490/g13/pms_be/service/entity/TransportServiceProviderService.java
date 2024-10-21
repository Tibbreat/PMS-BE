package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.transportsupplier.AddTransportProviderRequest;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;


@Service
public class TransportServiceProviderService {

    @Autowired
    private TransportServiceProviderRepo transportServiceProviderRepo;

    public TransportServiceProvider add(AddTransportProviderRequest request) {
        TransportServiceProvider provider = new TransportServiceProvider();
        BeanUtils.copyProperties(request, provider);
        provider.setCreatedBy(request.getCreatedBy());
        provider.setIsActive(true);
        return transportServiceProviderRepo.save(provider);
    }

    public Page<TransportServiceProvider> getAllProvider(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transportServiceProviderRepo.getAllProvider(pageable);
    }

    public TransportServiceProvider getDetail(String providerId) {
        return transportServiceProviderRepo.findById(providerId).orElseThrow(() -> new DataNotFoundException("Provider not found"));
    }

}
