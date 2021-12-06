package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportSwimmingPoolRepository;
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
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportSwimmingPoolService(SportSwimmingPoolRepository sportSwimmingPoolRepository,
                                    SportObjectMapper sportObjectMapper,
                                    Logger logger,
                                    RentEquipmentRepository rentEquipmentRepository) {
        this.sportSwimmingPoolRepository = sportSwimmingPoolRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SportSwimmingPool> findAll() {
        return sportSwimmingPoolRepository.findAll().stream()
                .map(sportObjectMapper::map)
                .map(SportSwimmingPool.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<SportSwimmingPool> findById(Integer courtId) {
        return sportSwimmingPoolRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::map)
                .map(SportSwimmingPool.class::cast);
    }

    @Transactional
    public SportSwimmingPool save(SportSwimmingPool sportSwimmingPool) {
        SportSwimmingPoolEntity entity = (SportSwimmingPoolEntity) sportObjectMapper.map(sportSwimmingPool);
        sportSwimmingPoolRepository.persist(entity);
        return (SportSwimmingPool) sportObjectMapper.map(entity);
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
        sportSwimmingPoolRepository.persist(entity);
        return (SportSwimmingPool) sportObjectMapper.map(entity);
    }

    public SportSwimmingPool putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportSwimmingPoolEntity sportSwimmingPoolToUpdate = sportSwimmingPoolRepository.findById(sportObjectId);
        sportSwimmingPoolToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        sportSwimmingPoolRepository.persistAndFlush(sportSwimmingPoolToUpdate);
        return (SportSwimmingPool) sportObjectMapper.map(sportSwimmingPoolToUpdate);
    }

}
