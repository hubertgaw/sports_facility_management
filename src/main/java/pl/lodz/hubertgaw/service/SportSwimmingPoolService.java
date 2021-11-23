package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.SportSwimmingPoolMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportSwimmingPoolRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SportSwimmingPoolService {
    private final SportSwimmingPoolRepository sportSwimmingPoolRepository;
    private final SportSwimmingPoolMapper sportSwimmingPoolMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportSwimmingPoolService(SportSwimmingPoolRepository sportSwimmingPoolRepository,
                                    SportSwimmingPoolMapper sportSwimmingPoolMapper,
                                    Logger logger,
                                    RentEquipmentRepository rentEquipmentRepository) {
        this.sportSwimmingPoolRepository = sportSwimmingPoolRepository;
        this.sportSwimmingPoolMapper = sportSwimmingPoolMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SportSwimmingPool> findAll() {
        return sportSwimmingPoolRepository.findAll().stream()
                .map(sportSwimmingPoolMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<SportSwimmingPool> findById(Integer courtId) {
        return sportSwimmingPoolRepository.findByIdOptional(courtId).map(sportSwimmingPoolMapper::toDomain);
    }

    @Transactional
    public SportSwimmingPool save(SportSwimmingPool sportSwimmingPool) {
        SportSwimmingPoolEntity entity = sportSwimmingPoolMapper.toEntity(sportSwimmingPool);
        sportSwimmingPoolRepository.persist(entity);
        return sportSwimmingPoolMapper.toDomain(entity);
    }

    @Transactional
    public SportSwimmingPool update(SportSwimmingPool sportSwimmingPool) {
        if (sportSwimmingPool.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<SportSwimmingPoolEntity> optional = sportSwimmingPoolRepository.findByIdOptional(sportSwimmingPool.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", sportSwimmingPool.getId()));
        }
        SportSwimmingPoolEntity entity = optional.get();
        entity.setFullPrice(sportSwimmingPool.getFullPrice());
        entity.setName(sportSwimmingPool.getName());
        entity.setTrackPrice(sportSwimmingPool.getTrackPrice());
        entity.setTracksNumber(sportSwimmingPool.getTracksNumber());
//        entity.set(sportSwimmingPool.getFullPrice());
//        entity.setRentEquipment(sportSwimmingPool.getRentEquipments());
        sportSwimmingPoolRepository.persist(entity);
        return sportSwimmingPoolMapper.toDomain(entity);
    }

    public SportSwimmingPool putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportSwimmingPoolEntity sportSwimmingPoolToUpdate = sportSwimmingPoolRepository.findById(sportObjectId);
        sportSwimmingPoolToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        sportSwimmingPoolRepository.persist(sportSwimmingPoolToUpdate);
        return sportSwimmingPoolMapper.toDomain(sportSwimmingPoolToUpdate);
    }

}
