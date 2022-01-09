package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.TennisCourtRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.TennisCourtException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TennisCourtService {
    private final TennisCourtRepository tennisCourtRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public TennisCourtService(TennisCourtRepository tennisCourtRepository,
                              SportObjectMapper sportObjectMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository,
                              ServiceUtils serviceUtils) {
        this.tennisCourtRepository = tennisCourtRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<TennisCourt> findAll() {
        return tennisCourtRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(TennisCourt.class::cast)
                .collect(Collectors.toList());
    }

    public TennisCourt findById(Integer courtId) {
        TennisCourtEntity entity = tennisCourtRepository.findByIdOptional(courtId)
                .orElseThrow(TennisCourtException::tennisCourtNotFoundException);
        return (TennisCourt) sportObjectMapper.toDomain(entity);
      }

    @Transactional
    public TennisCourt save(TennisCourt tennisCourt) {
        if (serviceUtils.compareSportObjectNameWithExisting(tennisCourt.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        TennisCourtEntity entity = (TennisCourtEntity) sportObjectMapper.toEntity(tennisCourt);
        tennisCourtRepository.persist(entity);
        return (TennisCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public TennisCourt update(TennisCourt tennisCourt) {
        if (tennisCourt.getId() == null) {
            throw TennisCourtException.tennisCourtEmptyIdException();
        }
        TennisCourtEntity entity = tennisCourtRepository.findByIdOptional(tennisCourt.getId()).
                orElseThrow(TennisCourtException::tennisCourtNotFoundException);
        if (!tennisCourt.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(tennisCourt.getName())) {
                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }
        entity.setFullPrice(tennisCourt.getFullPrice());
        entity.setName(tennisCourt.getName());
        tennisCourtRepository.persist(entity);
        return (TennisCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public TennisCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        TennisCourtEntity tennisCourtToUpdate = tennisCourtRepository.findById(sportObjectId);
        if (tennisCourtToUpdate == null) {
            throw TennisCourtException.tennisCourtNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        tennisCourtToUpdate.addRentEquipment(rentEquipmentToAdd);
        tennisCourtRepository.persistAndFlush(tennisCourtToUpdate);
        return (TennisCourt) sportObjectMapper.toDomain(tennisCourtToUpdate);
    }

}
