package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.mapper.RentEquipmentMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class RentEquipmentService {
    private final RentEquipmentRepository rentEquipmentRepository;
    private final RentEquipmentMapper rentEquipmentMapper;
    private final Logger logger;
    private final ServiceUtils serviceUtils;

    public RentEquipmentService(RentEquipmentRepository rentEquipmentRepository,
                                RentEquipmentMapper rentEquipmentMapper,
                                Logger logger,
                                ServiceUtils serviceUtils) {
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.rentEquipmentMapper = rentEquipmentMapper;
        this.logger = logger;
        this.serviceUtils = serviceUtils;
    }

    public List<RentEquipment> findAll() {
//        return rentEquipmentRepository.findAll().stream()
//                .map(rentEquipmentMapper::toDomain)
//                .collect(Collectors.toList());

        return rentEquipmentRepository.listAll()
                .stream()
                .map(rentEquipmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    public RentEquipment findById(Integer equipmentId) {
//        return rentEquipmentRepository.findByIdOptional(equipmentId).map(rentEquipmentMapper::toDomain);
        RentEquipmentEntity entity = rentEquipmentRepository.findByIdOptional(equipmentId)
                .orElseThrow(RentEquipmentException::rentEquipmentNotFoundException);

        return rentEquipmentMapper.toDomain(entity);
    }

    @Transactional
    public RentEquipment save(RentEquipment rentEquipment) {
        if (serviceUtils.compareRentEquipmentNameWithExisting(rentEquipment.getName())) {
            throw RentEquipmentException.rentEquipmentDuplicateNameException();
        }
        RentEquipmentEntity entity = rentEquipmentMapper.toEntity(rentEquipment);
        rentEquipmentRepository.persist(entity);
        return rentEquipmentMapper.toDomain(entity);
    }

    @Transactional
    public RentEquipment update(RentEquipment rentEquipment) {
        if (rentEquipment.getId() == null) {
            throw RentEquipmentException.rentEquipmentEmptyIdException();
        }
        RentEquipmentEntity entity = rentEquipmentRepository.findByIdOptional(rentEquipment.getId())
                .orElseThrow(RentEquipmentException::rentEquipmentNotFoundException);

        if (!rentEquipment.getName().equals(entity.getName())) {
            if (serviceUtils.compareRentEquipmentNameWithExisting(rentEquipment.getName())) {
                throw RentEquipmentException.rentEquipmentDuplicateNameException();
            }
        }

        entity.setName(rentEquipment.getName());
        entity.setPrice(rentEquipment.getPrice());
        return rentEquipmentMapper.toDomain(entity);
    }

    @Transactional
    public void deleteRentEquipmentById(Integer rentEquipmentId) {
        RentEquipmentEntity rentEquipmentToDelete = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToDelete == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        Set<SportObjectEntity> sportObjectsToDelete = rentEquipmentToDelete.getSportObjects();
        for (SportObjectEntity sportObject : sportObjectsToDelete) {
            sportObject.removeRentEquipment(rentEquipmentToDelete);
        }
        rentEquipmentRepository.delete(rentEquipmentToDelete);
//        sportObjectRepository.delete(sportObjectRepository.findById(sportObjectId));
//        return sportObjectRepository.deleteById(sportObjectId);
    }

}
