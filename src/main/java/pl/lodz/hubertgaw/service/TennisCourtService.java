package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.TennisCourtMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.TennisCourtRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
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
    private final TennisCourtMapper tennisCourtMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public TennisCourtService(TennisCourtRepository tennisCourtRepository,
                              TennisCourtMapper tennisCourtMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository) {
        this.tennisCourtRepository = tennisCourtRepository;
        this.tennisCourtMapper = tennisCourtMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<TennisCourt> findAll() {
        return tennisCourtRepository.findAll().stream()
                .map(tennisCourtMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<TennisCourt> findById(Integer courtId) {
        return tennisCourtRepository.findByIdOptional(courtId).map(tennisCourtMapper::toDomain);
    }

    @Transactional
    public TennisCourt save(TennisCourt tennisCourt) {
        TennisCourtEntity entity = tennisCourtMapper.toEntity(tennisCourt);
        tennisCourtRepository.persist(entity);
        return tennisCourtMapper.toDomain(entity);
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
//        entity.set(tennisCourt.getFullPrice());
//        entity.setRentEquipment(tennisCourt.getRentEquipments());
        tennisCourtRepository.persist(entity);
        return tennisCourtMapper.toDomain(entity);
    }

    public TennisCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        TennisCourtEntity tennisCourtToUpdate = tennisCourtRepository.findById(sportObjectId);
        tennisCourtToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        tennisCourtRepository.persist(tennisCourtToUpdate);
        return tennisCourtMapper.toDomain(tennisCourtToUpdate);
    }


}
