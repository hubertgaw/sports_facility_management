package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SmallPitchRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SmallPitchException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SmallPitchService {
    private final SmallPitchRepository smallPitchRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public SmallPitchService(SmallPitchRepository smallPitchRepository,
                             SportObjectMapper sportObjectMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository,
                             ServiceUtils serviceUtils) {
        this.smallPitchRepository = smallPitchRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<SmallPitch> findAll() {
        return smallPitchRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SmallPitch.class::cast)
                .collect(Collectors.toList());
    }

    public SmallPitch findById(Integer pitchId) {
        SmallPitchEntity entity = smallPitchRepository.findByIdOptional(pitchId)
                .orElseThrow(SmallPitchException::smallPitchNotFoundException);
        return (SmallPitch) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SmallPitch save(SmallPitch smallPitch) {
        if (serviceUtils.compareRentEquipmentNameWithExisting(smallPitch.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        SmallPitchEntity entity = (SmallPitchEntity) sportObjectMapper.toEntity(smallPitch);
        smallPitchRepository.persist(entity);
        return (SmallPitch) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SmallPitch update(SmallPitch smallPitch) {
        if (smallPitch.getId() == null) {
            throw SmallPitchException.smallPitchEmptyIdException();
        }
        SmallPitchEntity entity = smallPitchRepository.findByIdOptional(smallPitch.getId()).
                orElseThrow(SmallPitchException::smallPitchNotFoundException);
        if (!smallPitch.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(smallPitch.getName())) {
                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }
        entity.setFullPrice(smallPitch.getFullPrice());
        entity.setName(smallPitch.getName());
        entity.setHalfPitchPrice(smallPitch.getHalfPitchPrice());
        entity.setIsFullRented(smallPitch.getIsFullRented());
        smallPitchRepository.persist(entity);
        return (SmallPitch) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SmallPitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SmallPitchEntity smallPitchToUpdate = smallPitchRepository.findById(sportObjectId);
        if (smallPitchToUpdate == null) {
            throw SmallPitchException.smallPitchNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        smallPitchToUpdate.addRentEquipment(rentEquipmentToAdd);
        smallPitchRepository.persistAndFlush(smallPitchToUpdate);
        return (SmallPitch) sportObjectMapper.toDomain(smallPitchToUpdate);
    }

}
