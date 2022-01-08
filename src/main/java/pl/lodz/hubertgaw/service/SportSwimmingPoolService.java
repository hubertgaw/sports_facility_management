package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportSwimmingPoolRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.SportSwimmingPoolException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

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
    private final ServiceUtils serviceUtils;

    public SportSwimmingPoolService(SportSwimmingPoolRepository sportSwimmingPoolRepository,
                                    SportObjectMapper sportObjectMapper,
                                    Logger logger,
                                    RentEquipmentRepository rentEquipmentRepository,
                                    ServiceUtils serviceUtils) {
        this.sportSwimmingPoolRepository = sportSwimmingPoolRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<SportSwimmingPool> findAll() {
        return sportSwimmingPoolRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportSwimmingPool.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<SportSwimmingPool> findById(Integer courtId) {
        return sportSwimmingPoolRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::toDomain)
                .map(SportSwimmingPool.class::cast);
    }

    @Transactional
    public SportSwimmingPool save(SportSwimmingPool sportSwimmingPool) {
        if (serviceUtils.compareSportObjectNameWithExisting(sportSwimmingPool.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        SportSwimmingPoolEntity entity = (SportSwimmingPoolEntity) sportObjectMapper.toEntity(sportSwimmingPool);
        sportSwimmingPoolRepository.persist(entity);
        return (SportSwimmingPool) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SportSwimmingPool update(SportSwimmingPool sportSwimmingPool) {
        if (sportSwimmingPool.getId() == null) {
            throw SportSwimmingPoolException.sportSwimmingPoolEmptyIdException();
        }
        SportSwimmingPoolEntity entity = sportSwimmingPoolRepository.findByIdOptional(sportSwimmingPool.getId())
                .orElseThrow(SportSwimmingPoolException::sportSwimmingPoolNotFoundException);
        if (serviceUtils.compareSportObjectNameWithExisting(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        entity.setFullPrice(sportSwimmingPool.getFullPrice());
        entity.setName(sportSwimmingPool.getName());
        entity.setTrackPrice(sportSwimmingPool.getTrackPrice());
        entity.setTracksNumber(sportSwimmingPool.getTracksNumber());
        sportSwimmingPoolRepository.persist(entity);
        return (SportSwimmingPool) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SportSwimmingPool putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportSwimmingPoolEntity sportSwimmingPoolToUpdate = sportSwimmingPoolRepository.findById(sportObjectId);
        if (sportSwimmingPoolToUpdate == null) {
            throw SportSwimmingPoolException.sportSwimmingPoolNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        sportSwimmingPoolToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportSwimmingPoolRepository.persistAndFlush(sportSwimmingPoolToUpdate);
        return (SportSwimmingPool) sportObjectMapper.toDomain(sportSwimmingPoolToUpdate);
    }

}
