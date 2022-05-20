package pl.lodz.hubertgaw.service.utils;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceUtils {

    private final SportObjectRepository sportObjectRepository;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final UserRepository userRepository;
    private final Logger logger;

    public ServiceUtils(SportObjectRepository sportObjectRepository,
                        RentEquipmentRepository rentEquipmentRepository,
                        UserRepository userRepository,
                        Logger logger) {
       this.sportObjectRepository = sportObjectRepository;
       this.rentEquipmentRepository = rentEquipmentRepository;
       this.userRepository = userRepository;
       this.logger = logger;

       logger.info("Constructor ServiceUtils called");
    }

    public boolean compareSportObjectNameWithExisting(String name) {
        logger.info("Method compareSportObjectNameWithExisting() called with argument: {}", name);

        List<String> existingNames = sportObjectRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(SportObjectEntity::getName)
                .collect(Collectors.toList());

        logger.info("Existing in database names for sportObjects: {}", existingNames);

        boolean ifContains = existingNames
                .contains(name);

        logger.info("If compared name matches with one from existed names: {}", ifContains);

        return ifContains;
    }

    public boolean compareRentEquipmentNameWithExisting(String name) {
        logger.info("Method compareRentEquipmentNameWithExisting() called with argument: {}", name);

        List<String> existingNames = rentEquipmentRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(RentEquipmentEntity::getName)
                .collect(Collectors.toList());

        logger.info("Existing in database names for rentEquipment: {}", existingNames);

        boolean ifContains = existingNames
                .contains(name);

        logger.info("If compared name matches with one from existed names: {}", ifContains);

        return ifContains;

    }

    public boolean compareUserEmailWithExisting(String email){
        logger.info("Method compareUserEmailWithExisting() called with argument: {}", email);

        List<String> existingEmails = userRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(UserEntity::getEmail)
                .collect(Collectors.toList());

        logger.info("Existing in database emails for users: {}", existingEmails);

        boolean ifContains = existingEmails
                .contains(email);

        logger.info("If compared email matches with one from existed emails: {}", ifContains);

        return ifContains;
    }

    public LocalDateTime convertTime (LocalDateTime time) {
        logger.info("Method convertTime() called with argument: {}", time);

        LocalDateTime convertedTime = time.truncatedTo(ChronoUnit.MINUTES);

        logger.info("Converted time: {}", convertedTime);

        return convertedTime;
    }
}
