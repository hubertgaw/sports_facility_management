package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.TennisCourtRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class TennisCourtService {
    private final TennisCourtRepository tennisCourtRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public TennisCourtService(TennisCourtRepository tennisCourtRepository,
                              SportObjectMapper sportObjectMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository) {
        this.tennisCourtRepository = tennisCourtRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<TennisCourt> findAll() {
        return tennisCourtRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(TennisCourt.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<TennisCourt> findById(Integer courtId) {
        return tennisCourtRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::toDomain)
                .map(TennisCourt.class::cast);
    }

    @Transactional
    public TennisCourt save(TennisCourt tennisCourt) {
        TennisCourtEntity entity = (TennisCourtEntity) sportObjectMapper.toEntity(tennisCourt);
        tennisCourtRepository.persist(entity);
        return (TennisCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public TennisCourt update(TennisCourt tennisCourt) {
        if (tennisCourt.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<TennisCourtEntity> optional = tennisCourtRepository.findByIdOptional(tennisCourt.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", tennisCourt.getId()));
        }
        TennisCourtEntity entity = optional.get();
        entity.setFullPrice(tennisCourt.getFullPrice());
        entity.setName(tennisCourt.getName());
        tennisCourtRepository.persist(entity);
        return (TennisCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public TennisCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        TennisCourtEntity tennisCourtToUpdate = tennisCourtRepository.findById(sportObjectId);
        tennisCourtToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        tennisCourtRepository.persistAndFlush(tennisCourtToUpdate);
        return (TennisCourt) sportObjectMapper.toDomain(tennisCourtToUpdate);
    }

}
