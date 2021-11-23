package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.GymMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.GymRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class GymService {
    private final GymRepository gymRepository;
    private final GymMapper gymMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public GymService(GymRepository gymRepository,
                      GymMapper gymMapper,
                      Logger logger,
                      RentEquipmentRepository rentEquipmentRepository) {
        this.gymRepository = gymRepository;
        this.gymMapper = gymMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<Gym> findAll() {
        return gymRepository.findAll().stream()
                .map(gymMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<Gym> findById(Integer courtId) {
        return gymRepository.findByIdOptional(courtId).map(gymMapper::toDomain);
    }

    @Transactional
    public Gym save(Gym gym) {
        GymEntity entity = gymMapper.toEntity(gym);
        gymRepository.persist(entity);
        return gymMapper.toDomain(entity);
    }

    @Transactional
    public Gym update(Gym gym) {
        if (gym.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<GymEntity> optional = gymRepository.findByIdOptional(gym.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", gym.getId()));
        }
        GymEntity entity = optional.get();
        entity.setFullPrice(gym.getFullPrice());
        entity.setName(gym.getName());
        entity.setCapacity(gym.getCapacity());
        entity.setSinglePrice(gym.getSinglePrice());
//        entity.set(beachVolleyballCourt.getFullPrice());
//        entity.setRentEquipment(beachVolleyballCourt.getRentEquipments());
        gymRepository.persist(entity);
        return gymMapper.toDomain(entity);
    }

    public Gym putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        GymEntity gymToUpdate = gymRepository.findById(sportObjectId);
        gymToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        gymRepository.persist(gymToUpdate);
        return gymMapper.toDomain(gymToUpdate);
    }

}
