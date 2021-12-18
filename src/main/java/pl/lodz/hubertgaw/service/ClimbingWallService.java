package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.ClimbingWallRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClimbingWallService {
    private final ClimbingWallRepository climbingWallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public ClimbingWallService(ClimbingWallRepository climbingWallRepository,
                               SportObjectMapper sportObjectMapper,
                               Logger logger,
                               RentEquipmentRepository rentEquipmentRepository) {
        this.climbingWallRepository = climbingWallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<ClimbingWall> findAll() {
        return climbingWallRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(ClimbingWall.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<ClimbingWall> findById(Integer wallId) {
        return climbingWallRepository.findByIdOptional(wallId)
                .map(sportObjectMapper::toDomain)
                .map(ClimbingWall.class::cast);
    }

    @Transactional
    public ClimbingWall save(ClimbingWall climbingWall) {
        ClimbingWallEntity entity = (ClimbingWallEntity) sportObjectMapper.toEntity(climbingWall);
        climbingWallRepository.persist(entity);
        return (ClimbingWall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public ClimbingWall update(ClimbingWall climbingWall) {
        if (climbingWall.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<ClimbingWallEntity> optional = climbingWallRepository.findByIdOptional(climbingWall.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", climbingWall.getId()));
        }
        ClimbingWallEntity entity = optional.get();
        entity.setFullPrice(climbingWall.getFullPrice());
        entity.setName(climbingWall.getName());
        entity.setCapacity(climbingWall.getCapacity());
        entity.setSinglePrice(climbingWall.getSinglePrice());
        climbingWallRepository.persist(entity);
        return (ClimbingWall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public ClimbingWall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        ClimbingWallEntity climbingWallToUpdate = climbingWallRepository.findById(sportObjectId);
        climbingWallToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        climbingWallRepository.persistAndFlush(climbingWallToUpdate);
        return (ClimbingWall) sportObjectMapper.toDomain(climbingWallToUpdate);
    }

}
