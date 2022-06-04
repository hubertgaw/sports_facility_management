package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.mapper.RentEquipmentMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
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

        logger.info("Constructor RentEquipmentService called");
    }

    public List<RentEquipment> findAll() {
        logger.info("Method findAll() called");

        List<RentEquipmentEntity> allRentEquipmentsEntities = rentEquipmentRepository.listAll();

        logger.info("All rentEquipments as entities taken from repository: {}", allRentEquipmentsEntities);

        List<RentEquipment> allRentEquipmentsDto = allRentEquipmentsEntities
                .stream()
                .map(rentEquipmentMapper::toDomain)
                .collect(Collectors.toList());

        logger.info("All rentEquipments found (after mapping from entity to DTO): {}", allRentEquipmentsDto);

        return allRentEquipmentsDto;
    }

    public RentEquipment findById(Integer rentEquipmentId) {
        logger.info("Method findById() called with argument: {}", rentEquipmentId);

        RentEquipmentEntity entity = rentEquipmentRepository.findByIdOptional(rentEquipmentId)
                .orElseThrow(() -> {

                    logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

                    return RentEquipmentException.rentEquipmentNotFoundException();
                });

        logger.info("RentEquipmentEntity by id: {} found in database:{}", rentEquipmentId, entity);

        RentEquipment rentEquipmentDto = rentEquipmentMapper.toDomain(entity);

        logger.info("RentEquipment by id: {} found after mapping to DTO:{}", rentEquipmentId, rentEquipmentDto);

        return rentEquipmentDto;
    }

    public RentEquipment findByName(String name) {
        logger.info("Method findByName() called with argument: {}", name);

        RentEquipmentEntity entity = rentEquipmentRepository.findByName(name);
        if (entity == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentForNameNotFoundException());

            throw RentEquipmentException.rentEquipmentForNameNotFoundException();
        }

        logger.info("RentEquipmentEntity by name: {} found in database:{}", name, entity);

        RentEquipment rentEquipmentDto = rentEquipmentMapper.toDomain(entity);

        logger.info("RentEquipment by name: {} found after mapping to DTO:{}", name, rentEquipmentDto);

        return rentEquipmentDto;
    }

    @Transactional
    public RentEquipment save(RentEquipment rentEquipment) {
        logger.info("Method save() called with argument: {}", rentEquipment);

        if (serviceUtils.compareRentEquipmentNameWithExisting(rentEquipment.getName())) {
            logger.warn("Exception", RentEquipmentException.rentEquipmentDuplicateNameException());

            throw RentEquipmentException.rentEquipmentDuplicateNameException();
        }
        RentEquipmentEntity entity = rentEquipmentMapper.toEntity(rentEquipment);
        rentEquipmentRepository.persist(entity);

        logger.info("RentEquipment persisted in repository: {}", entity);

        RentEquipment rentEquipmentDto = rentEquipmentMapper.toDomain(entity);

        logger.info("RentEquipment mapped from entity to DTO: {}", rentEquipmentDto);

        return rentEquipmentDto;
    }

    @Transactional
    public RentEquipment update(RentEquipment rentEquipment) {
        logger.info("Method update() called with argument: {}", rentEquipment);

        if (rentEquipment.getId() == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentEmptyIdException());

            throw RentEquipmentException.rentEquipmentEmptyIdException();
        }
        RentEquipmentEntity entity = rentEquipmentRepository.findByIdOptional(rentEquipment.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

                    return RentEquipmentException.rentEquipmentNotFoundException();
                });

        if (!rentEquipment.getName().equals(entity.getName())) {
            if (serviceUtils.compareRentEquipmentNameWithExisting(rentEquipment.getName())) {

                logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

                throw RentEquipmentException.rentEquipmentNotFoundException();
            }
        }

        logger.info("RentEquipment before update: {}", entity);

        entity.setName(rentEquipment.getName());
        entity.setPrice(rentEquipment.getPrice());
        rentEquipmentRepository.persist(entity);

        logger.info("RentEquipment updated and persisted: {}", entity);

        RentEquipment rentEquipmentDto = rentEquipmentMapper.toDomain(entity);

        logger.info("Updated rentEquipment mapped from entity to DTO: {}", rentEquipmentDto);

        return rentEquipmentDto;
    }

    @Transactional
    public void deleteRentEquipmentById(Integer rentEquipmentId) {
        logger.info("Method deleteRentEquipmentById() called with argument: {}", rentEquipmentId);

        RentEquipmentEntity rentEquipmentToDelete = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToDelete == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        Set<SportObjectEntity> sportObjectsToDelete = rentEquipmentToDelete.getSportObjects();

        logger.info("SportObjectEntities from rentEquipment with id: {} to delete: {}",
                rentEquipmentId, sportObjectsToDelete);

        for (SportObjectEntity sportObject : sportObjectsToDelete) {
            sportObject.removeRentEquipment(rentEquipmentToDelete);

            logger.info("RentEquipment with id: {} removed from sportObject with id: {}",
                    rentEquipmentId, sportObject.getId());
        }
        rentEquipmentRepository.delete(rentEquipmentToDelete);

        logger.info("RentEquipment with id: {} deleted successfully", rentEquipmentId);
    }

}
