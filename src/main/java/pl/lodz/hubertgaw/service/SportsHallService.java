package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportsHall;
//import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
//import pl.lodz.hubertgaw.mapper.SportsHallMapper;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportsHallRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.SportsHallException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SportsHallService {
    private final SportsHallRepository sportsHallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public SportsHallService(SportsHallRepository sportsHallRepository,
                             SportObjectMapper sportObjectMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository,
                             ServiceUtils serviceUtils) {
        this.sportsHallRepository = sportsHallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<SportsHall> findAll() {
        return sportsHallRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportsHall.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<SportsHall> findById(Integer courtId) {
        return sportsHallRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::toDomain)
                .map(SportsHall.class::cast);
    }

    @Transactional
    public SportsHall save(SportsHall sportsHall) {
        if (serviceUtils.compareSportObjectNameWithExisting(sportsHall.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        SportsHallEntity entity = (SportsHallEntity) sportObjectMapper.toEntity(sportsHall);
        sportsHallRepository.persist(entity);
        return (SportsHall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SportsHall update(SportsHall sportsHall) {
        if (sportsHall.getId() == null) {
            throw SportsHallException.sportsHallEmptyIdException();
        }
        SportsHallEntity entity = sportsHallRepository.findByIdOptional(sportsHall.getId())
                .orElseThrow(SportsHallException::sportsHallNotFoundException);
        if (serviceUtils.compareSportObjectNameWithExisting(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        entity.setFullPrice(sportsHall.getFullPrice());
        entity.setName(sportsHall.getName());
        entity.setSectorPrice(sportsHall.getSectorPrice());
        sportsHall.setSectorsNumber(sportsHall.getSectorsNumber());
        sportsHallRepository.persist(entity);
        return (SportsHall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SportsHall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportsHallEntity sportsHallToUpdate = sportsHallRepository.findById(sportObjectId);
        if (sportsHallToUpdate == null) {
            throw SportsHallException.sportsHallNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        sportsHallToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportsHallRepository.persistAndFlush(sportsHallToUpdate);
        return (SportsHall) sportObjectMapper.toDomain(sportsHallToUpdate);
    }

}
