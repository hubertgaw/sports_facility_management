package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.FullSizePitchMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.FullSizePitchRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class FullSizePitchService {
    private final FullSizePitchRepository fullSizePitchRepository;
    private final FullSizePitchMapper fullSizePitchMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public FullSizePitchService(FullSizePitchRepository fullSizePitchRepository,
                                FullSizePitchMapper fullSizePitchMapper,
                                Logger logger,
                                RentEquipmentRepository rentEquipmentRepository) {
        this.fullSizePitchRepository = fullSizePitchRepository;
        this.fullSizePitchMapper = fullSizePitchMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<FullSizePitch> findAll() {
        return fullSizePitchRepository.findAll().stream()
                .map(fullSizePitchMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<FullSizePitch> findById(Integer pitchId) {
        return fullSizePitchRepository.findByIdOptional(pitchId).map(fullSizePitchMapper::toDomain);
    }

    @Transactional
    public FullSizePitch save(FullSizePitch fullSizePitch) {
        FullSizePitchEntity entity = fullSizePitchMapper.toEntity(fullSizePitch);
        fullSizePitchRepository.persist(entity);
        return fullSizePitchMapper.toDomain(entity);
    }

    @Transactional
    public FullSizePitch update(FullSizePitch fullSizePitch) {
        if (fullSizePitch.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<FullSizePitchEntity> optional = fullSizePitchRepository.findByIdOptional(fullSizePitch.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", fullSizePitch.getId()));
        }
        FullSizePitchEntity entity = optional.get();
        entity.setFullPrice(fullSizePitch.getFullPrice());
        entity.setName(fullSizePitch.getName());
        entity.setHalfPitchPrice(fullSizePitch.getHalfPitchPrice());
        entity.setIsFullRented(fullSizePitch.getIsFullRented());
//        entity.set(beachVolleyballCourt.getFullPrice());
//        entity.setRentEquipment(beachVolleyballCourt.getRentEquipments());
        fullSizePitchRepository.persist(entity);
        return fullSizePitchMapper.toDomain(entity);
    }

    public FullSizePitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        FullSizePitchEntity fullSizeToUpdate = fullSizePitchRepository.findById(sportObjectId);
        fullSizeToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        fullSizePitchRepository.persist(fullSizeToUpdate);
        return fullSizePitchMapper.toDomain(fullSizeToUpdate);
    }

}
