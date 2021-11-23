package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.ClimbingWallMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.ClimbingWallRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
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
    private final ClimbingWallMapper climbingWallMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public ClimbingWallService(ClimbingWallRepository climbingWallRepository,
                               ClimbingWallMapper climbingWallMapper,
                               Logger logger,
                               RentEquipmentRepository rentEquipmentRepository) {
        this.climbingWallRepository = climbingWallRepository;
        this.climbingWallMapper = climbingWallMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<ClimbingWall> findAll() {
        return climbingWallRepository.findAll().stream()
                .map(climbingWallMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<ClimbingWall> findById(Integer wallId) {
        return climbingWallRepository.findByIdOptional(wallId).map(climbingWallMapper::toDomain);
    }

    @Transactional
    public ClimbingWall save(ClimbingWall climbingWall) {
        ClimbingWallEntity entity = climbingWallMapper.toEntity(climbingWall);
        climbingWallRepository.persist(entity);
        return climbingWallMapper.toDomain(entity);
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
//        entity.set(climbingWall.getFullPrice());
//        entity.setRentEquipment(climbingWall.getRentEquipments());
        climbingWallRepository.persist(entity);
        return climbingWallMapper.toDomain(entity);
    }

    public ClimbingWall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        ClimbingWallEntity climbingWallToUpdate = climbingWallRepository.findById(sportObjectId);
        climbingWallToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        climbingWallRepository.persist(climbingWallToUpdate);
        return climbingWallMapper.toDomain(climbingWallToUpdate);
    }

}
