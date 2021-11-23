package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.mapper.AthleticsTrackMapper;
import pl.lodz.hubertgaw.mapper.RentEquipmentMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RentEquipmentService {
    private final RentEquipmentRepository rentEquipmentRepository;
    private final RentEquipmentMapper rentEquipmentMapper;
    private final Logger logger;

    public RentEquipmentService(RentEquipmentRepository rentEquipmentRepository, RentEquipmentMapper rentEquipmentMapper, Logger logger) {
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.rentEquipmentMapper = rentEquipmentMapper;
        this.logger = logger;
    }

    public List<RentEquipment> findAll(){
        return rentEquipmentRepository.findAll().stream()
                .map(rentEquipmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<RentEquipment> findById(Integer equipmentId) {
        return rentEquipmentRepository.findByIdOptional(equipmentId).map(rentEquipmentMapper::toDomain);
    }

    @Transactional
    public RentEquipment save(RentEquipment rentEquipment) {
        RentEquipmentEntity entity = rentEquipmentMapper.toEntity(rentEquipment);
        rentEquipmentRepository.persist(entity);
        return rentEquipmentMapper.toDomain(entity);
    }

    @Transactional
    public RentEquipment update(RentEquipment rentEquipment) {
        if (rentEquipment.getName() == null) {
            throw new ServiceException("RentEquipment does not have an id");
        }
        Optional<RentEquipmentEntity> optional = rentEquipmentRepository.findByName(rentEquipment.getName());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No RentEquipment found for id[%s]", rentEquipment.getName()));
        }
        RentEquipmentEntity entity = optional.get();
        entity.setName(rentEquipment.getName());
        entity.setPrice(rentEquipment.getPrice());
//        entity.setFullPrice(rentEquipment.getFullPrice());
//        entity.setRentEquipment(athleticsTrack.getRentEquipments());
        return rentEquipmentMapper.toDomain(entity);
    }

}
