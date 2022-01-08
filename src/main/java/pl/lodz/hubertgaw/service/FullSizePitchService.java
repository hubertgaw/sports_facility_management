package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.FullSizePitchRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;
import pl.lodz.hubertgaw.service.exception.FullSizePitchException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class FullSizePitchService {
    private final FullSizePitchRepository fullSizePitchRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public FullSizePitchService(FullSizePitchRepository fullSizePitchRepository,
                                SportObjectMapper sportObjectMapper,
                                Logger logger,
                                RentEquipmentRepository rentEquipmentRepository,
                                ServiceUtils serviceUtils) {
        this.fullSizePitchRepository = fullSizePitchRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<FullSizePitch> findAll() {
        return fullSizePitchRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(FullSizePitch.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<FullSizePitch> findById(Integer pitchId) {
        return fullSizePitchRepository.findByIdOptional(pitchId)
                .map(sportObjectMapper::toDomain)
                .map(FullSizePitch.class::cast);
    }

    @Transactional
    public FullSizePitch save(FullSizePitch fullSizePitch) {
        if (serviceUtils.compareSportObjectNameWithExisting(fullSizePitch.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        FullSizePitchEntity entity = (FullSizePitchEntity) sportObjectMapper.toEntity(fullSizePitch);
        fullSizePitchRepository.persist(entity);
        return (FullSizePitch) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public FullSizePitch update(FullSizePitch fullSizePitch) {
        if (fullSizePitch.getId() == null) {
            throw FullSizePitchException.fullSizePitchEmptyIdException();
        }
        FullSizePitchEntity entity = fullSizePitchRepository.findByIdOptional(fullSizePitch.getId())
                .orElseThrow(FullSizePitchException::fullSizePitchNotFoundException);
        if (serviceUtils.compareSportObjectNameWithExisting(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        entity.setFullPrice(fullSizePitch.getFullPrice());
        entity.setName(fullSizePitch.getName());
        entity.setHalfPitchPrice(fullSizePitch.getHalfPitchPrice());
        entity.setIsFullRented(fullSizePitch.getIsFullRented());
        fullSizePitchRepository.persist(entity);
        return (FullSizePitch) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public FullSizePitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        FullSizePitchEntity fullSizeToUpdate = fullSizePitchRepository.findById(sportObjectId);
        if (fullSizeToUpdate == null) {
            throw FullSizePitchException.fullSizePitchNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        fullSizeToUpdate.addRentEquipment(rentEquipmentToAdd);
        fullSizePitchRepository.persistAndFlush(fullSizeToUpdate);
        return (FullSizePitch) sportObjectMapper.toDomain(fullSizeToUpdate);
    }

}
