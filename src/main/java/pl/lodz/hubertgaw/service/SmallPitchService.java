package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.SmallPitchMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SmallPitchRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SmallPitchService {
    private final SmallPitchRepository smallPitchRepository;
    private final SmallPitchMapper smallPitchMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SmallPitchService(SmallPitchRepository smallPitchRepository,
                             SmallPitchMapper smallPitchMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository) {
        this.smallPitchRepository = smallPitchRepository;
        this.smallPitchMapper = smallPitchMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SmallPitch> findAll() {
        return smallPitchRepository.findAll().stream()
                .map(smallPitchMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<SmallPitch> findById(Integer pitchId) {
        return smallPitchRepository.findByIdOptional(pitchId).map(smallPitchMapper::toDomain);
    }

    @Transactional
    public SmallPitch save(SmallPitch smallPitch) {
        SmallPitchEntity entity = smallPitchMapper.toEntity(smallPitch);
        smallPitchRepository.persist(entity);
        return smallPitchMapper.toDomain(entity);
    }

    @Transactional
    public SmallPitch update(SmallPitch smallPitch) {
        if (smallPitch.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<SmallPitchEntity> optional = smallPitchRepository.findByIdOptional(smallPitch.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", smallPitch.getId()));
        }
        SmallPitchEntity entity = optional.get();
        entity.setFullPrice(smallPitch.getFullPrice());
        entity.setName(smallPitch.getName());
        entity.setHalfPitchPrice(smallPitch.getHalfPitchPrice());
        entity.setIsFullRented(smallPitch.getIsFullRented());
//        entity.set(smallPitch.getFullPrice());
//        entity.setRentEquipment(smallPitch.getRentEquipments());
        smallPitchRepository.persist(entity);
        return smallPitchMapper.toDomain(entity);
    }

    public SmallPitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SmallPitchEntity smallPitchToUpdate = smallPitchRepository.findById(sportObjectId);
        smallPitchToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        smallPitchRepository.persist(smallPitchToUpdate);
        return smallPitchMapper.toDomain(smallPitchToUpdate);
    }

}
