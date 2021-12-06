package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportsHall;
//import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
//import pl.lodz.hubertgaw.mapper.SportsHallMapper;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportsHallRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//TODO : zrobic resource do tego
@ApplicationScoped
public class SportsHallService {
    private final SportsHallRepository sportsHallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportsHallService(SportsHallRepository sportsHallRepository,
                             SportObjectMapper sportObjectMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository) {
        this.sportsHallRepository = sportsHallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SportsHall> findAll() {
        return sportsHallRepository.findAll().stream()
                .map(sportObjectMapper::map)
                .map(SportsHall.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<SportsHall> findById(Integer courtId) {
        return sportsHallRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::map)
                .map(SportsHall.class::cast);
    }

    @Transactional
    public SportsHall save(SportsHall sportsHall) {
        SportsHallEntity entity = (SportsHallEntity) sportObjectMapper.map(sportsHall);
        sportsHallRepository.persist(entity);
        return (SportsHall) sportObjectMapper.map(entity);
    }

    @Transactional
    public SportsHall update(SportsHall sportsHall) {
        if (sportsHall.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<SportsHallEntity> optional = sportsHallRepository.findByIdOptional(sportsHall.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", sportsHall.getId()));
        }
        SportsHallEntity entity = optional.get();
        entity.setFullPrice(sportsHall.getFullPrice());
        entity.setName(sportsHall.getName());
        entity.setSectorPrice(sportsHall.getSectorPrice());
        sportsHall.setSectorsNumber(sportsHall.getSectorsNumber());
        sportsHallRepository.persist(entity);
        return (SportsHall) sportObjectMapper.map(entity);
    }

    public SportsHall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportsHallEntity sportsHallToUpdate = sportsHallRepository.findById(sportObjectId);
        sportsHallToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        sportsHallRepository.persistAndFlush(sportsHallToUpdate);
        return (SportsHall) sportObjectMapper.map(sportsHallToUpdate);
    }

}
