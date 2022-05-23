package pl.lodz.hubertgaw.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.mapper.BookingMapper;
import pl.lodz.hubertgaw.repository.BookingRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.service.exception.BookingException;
import pl.lodz.hubertgaw.service.exception.BookingException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final SportObjectRepository sportObjectRepository;
    private final SportObjectService sportObjectService;
    private final BookingMapper bookingMapper;
    private final Logger logger;
    private final ServiceUtils serviceUtils;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository,
                          RentEquipmentRepository rentEquipmentRepository,
                          SportObjectRepository sportObjectRepository,
                          SportObjectService sportObjectService,
                          BookingMapper bookingMapper,
                          Logger logger,
                          ServiceUtils serviceUtils,
                          UserService userService) {
        this.bookingRepository = bookingRepository;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.sportObjectRepository = sportObjectRepository;
        this.sportObjectService = sportObjectService;
        this.bookingMapper = bookingMapper;
        this.logger = logger;
        this.serviceUtils = serviceUtils;
        this.userService = userService;

        logger.info("Constructor BookingService called");
    }

    public List<Booking> findAll() {
        logger.info("Method findAll() called");

        List<BookingEntity> allBookingEntities = bookingRepository.listAll();

        logger.info("All bookings as entities taken from repository: {}", allBookingEntities);

        List<Booking> allBookingsDto = allBookingEntities
                .stream()
                .map(bookingMapper::toDomain)
                .collect(Collectors.toList());

        logger.info("All bookings found (after mapping from entity to DTO): {}", allBookingsDto);

        return allBookingsDto;

    }

    public Booking findById(Integer bookingId) {
        logger.info("Method findById() called with argument: {}", bookingId);

        BookingEntity entity = bookingRepository.findByIdOptional(bookingId)
                .orElseThrow(() -> {

                    logger.warn("Exception", BookingException.bookingNotFoundException());

                    return BookingException.bookingNotFoundException();
                });

        logger.info("BookingEntity by id: {} found in database:{}", bookingId, entity);

        Booking bookingDto = bookingMapper.toDomain(entity);

        logger.info("Booking by id: {} found after mapping to DTO:{}", bookingId, bookingDto);

        return bookingDto;
    }

    public List<Booking> findByUserId(Integer userId) {
        logger.info("Method findByUserId() called with argument: {}", userId);

        List<BookingEntity> bookingsByUserEntities = bookingRepository.findByUserId(userId);

        logger.info("All bookings from user: {} as entities taken from repository: {}",
                userId, bookingsByUserEntities);

        List<Booking> bookingsByUserDto = bookingsByUserEntities
                .stream()
                .map(bookingMapper::toDomain)
                .map(Booking.class::cast)
                .collect(Collectors.toList());

        if (bookingsByUserDto.isEmpty()) {

            logger.warn("", BookingException.bookingForUserNotFoundException());

            throw BookingException.bookingForUserNotFoundException();
        }

        logger.info("All bookings for user: {} found (after mapping from entity to DTO): {}",
                userId, bookingsByUserDto);

        return bookingsByUserDto;
    }

    @Transactional
    public Booking save(Booking booking, SecurityContext userContext) {
        logger.info("Method save() called with arguments: {}, {}", booking, userContext);

        if (!userContext.isUserInRole("USER")) {

            logger.info("User is not in role USER");

            if (null == booking.getEmail() || booking.getEmail().isEmpty()) {

                logger.warn("Exception", BookingException.bookingEmptyEmailException());

                throw BookingException.bookingEmptyEmailException();
            }
            if (!EmailValidator.getInstance().isValid(booking.getEmail())) {

                logger.warn("Exception", BookingException.bookingWrongEmailFormatException());

                throw BookingException.bookingWrongEmailFormatException();
            }
            if (null == booking.getFirstName() || booking.getFirstName().isEmpty()) {

                logger.warn("Exception", BookingException.bookingEmptyFirstNameException());

                throw BookingException.bookingEmptyFirstNameException();
            }
            if (null == booking.getLastName() || booking.getLastName().isEmpty()) {

                logger.warn("Exception", BookingException.bookingEmptyLastNameException());

                throw BookingException.bookingEmptyLastNameException();
            }
            // 0 - not logged as user (guest or logged as admin)
            booking.setUserId(0);
        } else {
            User loggedUser = userService.findByEmail(userContext.getUserPrincipal().getName());
            // these fields can be also for example null or some string/value - based on convention that we decide.
            booking.setUserId(loggedUser.getId());
            booking.setEmail(loggedUser.getEmail());
            booking.setFirstName(loggedUser.getFirstName());
            booking.setLastName(loggedUser.getLastName());
            booking.setPhoneNumber(loggedUser.getPhoneNumber());
        }
        booking.setFromDate(serviceUtils.convertTime(booking.getFromDate()));
        if (null == booking.getHalfRent()) {
            booking.setHalfRent(false);
        }

        logger.info("Booking after checking if user is logged as USER and setting fields for appropriate values:{}",
                booking);

        List<Booking> bookings = sportObjectService.findBookingsForSportObject(booking.getSportObjectId());

        logger.info("All bookings found for sportObject:{} :{}", booking.getSportObjectId(), bookings);

        validateBooking(booking, bookings);
        BookingEntity entity = bookingMapper.toEntity(booking);
        bookingRepository.persist(entity);

        logger.info("BookingEntity after persisting in database:{}", entity);

        Booking bookingDto = bookingMapper.toDomain(entity);

        logger.info("Booking after mapping from entity to DTO:{}", bookingDto);

        return bookingDto;
    }

    @Transactional
    public Booking update(Booking booking, SecurityContext userContext) {
        logger.info("Method update() called with arguments: {}, {}", booking, userContext);

        if (userContext.isUserInRole("USER")) {
            User loggedUser = userService.findByEmail(userContext.getUserPrincipal().getName());

            logger.info("Found User with role USER:{}", userContext.getUserPrincipal().getName());

            if (!checkIfUserHasBooking(
                    booking.getId(), loggedUser.getId())) {

                logger.warn("Exception", BookingException.bookingForUserNotFoundException());

                throw BookingException.bookingForUserNotFoundException();
            }
            booking.setUserId(loggedUser.getId());
            booking.setEmail(loggedUser.getEmail());
            booking.setFirstName(loggedUser.getFirstName());
            booking.setLastName(loggedUser.getLastName());
            booking.setPhoneNumber(loggedUser.getPhoneNumber());
        }

        booking.setFromDate(serviceUtils.convertTime(booking.getFromDate()));
        if (booking.getId() == null) {

            logger.warn("Exception", BookingException.bookingEmptyIdException());

            throw BookingException.bookingEmptyIdException();
        }

        logger.info("Booking after checking if user is logged as USER and setting fields for appropriate values:{}",
                booking);

        BookingEntity entity = bookingRepository.findByIdOptional(booking.getId())
                .orElseThrow(() -> {
                    logger.warn("Exception", BookingException.bookingNotFoundException());

                    return BookingException.bookingNotFoundException();
                });

        List<Booking> bookings = sportObjectService.findBookingsForSportObject(booking.getSportObjectId());

        logger.info("All bookings found for sportObject:{} :{}", booking.getSportObjectId(), bookings);

        bookings.remove(bookingMapper.toDomain(entity)); // in put we romove booking that we update from list

        logger.info("Booking that will be updated temporary removed from BookingList");

        validateBooking(booking, bookings);

        BookingEntity entityToUpdate = bookingMapper.toEntity(booking);
        entity.setSportObject(entityToUpdate.getSportObject());
        entity.setFromDate(entityToUpdate.getFromDate());
        entity.setToDate(entityToUpdate.getToDate());
        entity.setHours(entityToUpdate.getHours());
        entity.setRentEquipment(entityToUpdate.getRentEquipment());
        entity.setFirstName(entityToUpdate.getFirstName());
        entity.setLastName(entityToUpdate.getLastName());
        entity.setEmail(entityToUpdate.getEmail());
        entity.setPhoneNumber(entityToUpdate.getPhoneNumber());
        bookingRepository.persist(entity);

        logger.info("BookingEntity after persisting in database:{}", entity);

        Booking bookingDto = bookingMapper.toDomain(entity);

        logger.info("Booking after mapping from entity to DTO:{}", bookingDto);

        return bookingDto;
    }

    @Transactional
    public void deleteBookingById(Integer bookingId, SecurityContext userContext) {
        logger.info("Method deleteBookingById() called with arguments: {}, {}", bookingId, userContext);

        if (userContext.isUserInRole("USER")) {
            User loggedUser = userService.findByEmail(userContext.getUserPrincipal().getName());

            logger.info("Found User with role USER:{}", userContext.getUserPrincipal().getName());

            if (!checkIfUserHasBooking(
                    bookingId, loggedUser.getId())) {
                logger.warn("Exception", BookingException.bookingForUserNotFoundException());

                throw BookingException.bookingForUserNotFoundException();
            }
        }
        BookingEntity bookingToDelete = bookingRepository.findById(bookingId);
        if (bookingToDelete == null) {

            logger.warn("Exception", BookingException.bookingNotFoundException());

            throw BookingException.bookingNotFoundException();
        }
        List<RentEquipmentEntity> rentEquipmentToDelete = bookingToDelete.getRentEquipment();
        for (RentEquipmentEntity rentEquipment : rentEquipmentToDelete) {
            rentEquipment.removeBooking(bookingToDelete);

            logger.info("Booking removed from rentEquipment:{}", rentEquipment);
        }
        bookingRepository.delete(bookingToDelete);

        logger.info("Booking deleted from repository");
    }


    private void validateBooking(Booking booking, List<Booking> bookings) {
        logger.info("Method validateBooking() called with arguments: {}, {}", booking, bookings);

        List<String> equipmentInSportObjectNames = sportObjectRepository
                .findById(booking.getSportObjectId())
                .getRentEquipment().stream()
                .map(RentEquipmentEntity::getName)
                .collect(Collectors.toList());
        List<String> equipmentFromRequestNames = booking.getRentEquipmentNames();
        if (!equipmentInSportObjectNames.containsAll(equipmentFromRequestNames)) {

            logger.warn("Exception", BookingException.rentEquipmentForSportObjectNotFoundException());

            throw BookingException.rentEquipmentForSportObjectNotFoundException();
        }

        LocalDateTime fromDate = booking.getFromDate();
        if (fromDate.isBefore(LocalDateTime.now())) {

            logger.warn("Exception", BookingException.wrongDatePastException());

            throw BookingException.wrongDatePastException();
        }
        if (!(fromDate.getMinute() == 0 || fromDate.getMinute() == 30)) {

            logger.warn("Exception", BookingException.wrongDateTimeException());

            throw BookingException.wrongDateTimeException();
        }

        if (null != booking.getNumberOfPlaces() && booking.getHalfRent()) {

            logger.warn("Exception", BookingException.numberOfPlacesHalfRentExcludeException());

            throw BookingException.numberOfPlacesHalfRentExcludeException();
        }

        SportObject sportObject = sportObjectService.findById(booking.getSportObjectId());

        logger.info("SportObject for validating booking:{} : {}", booking, sportObject);

        if (sportObject.getCapacity() == null && booking.getNumberOfPlaces() != null) {

            logger.warn("Exception", BookingException.invalidNumberOfPlacesForObject());

            throw BookingException.invalidNumberOfPlacesForObject();
        }

        if (!sportObject.getIsHalfRentable() && booking.getHalfRent()) {

            logger.warn("Exception", BookingException.invalidHalfRentedForObject());

            throw BookingException.invalidHalfRentedForObject();
        }

        // we set 1 to enable validating, when number of places is not specified we assume 1
        if (booking.getNumberOfPlaces() == null) {
            booking.setNumberOfPlaces(1);
        }

        if (null == sportObject.getCapacity() && !booking.getHalfRent()) {
            for (Booking existingBooking : bookings) {
                if (((booking.getFromDate().isAfter(existingBooking.getFromDate()))
                        && (booking.getFromDate().isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours()))))
                        ||
                        ((booking.getFromDate().plusHours(booking.getHours()).isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours())))
                                && (booking.getFromDate().plusHours(booking.getHours()).isAfter(existingBooking.getFromDate())))
                        ||
                        booking.getFromDate().isEqual(existingBooking.getFromDate())) {

                    logger.warn("Exception", BookingException.duplicateBookingTimeException());

                    throw BookingException.duplicateBookingTimeException();
                }
            }
        }
        int counter = 0;
        if (null != sportObject.getCapacity()) {
            for (Booking existingBooking : bookings) {
                if (((booking.getFromDate().isAfter(existingBooking.getFromDate()))
                        && (booking.getFromDate().isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours()))))
                        ||
                        ((booking.getFromDate().plusHours(booking.getHours()).isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours())))
                                && (booking.getFromDate().plusHours(booking.getHours()).isAfter(existingBooking.getFromDate())))
                        ||
                        booking.getFromDate().isEqual(existingBooking.getFromDate())) {
                    counter += existingBooking.getNumberOfPlaces();
                    if (counter + booking.getNumberOfPlaces() > sportObject.getCapacity()) {

                        logger.warn("Exception", BookingException.duplicateBookingTimeException());

                        throw BookingException.duplicateBookingTimeException();
                    }
                }
            }
        }

        int counterHalfRent = 0;
        if (booking.getHalfRent()) {
            for (Booking existingBooking : bookings) {
                if ((((booking.getFromDate().isAfter(existingBooking.getFromDate()))
                        && (booking.getFromDate().isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours()))))
                        ||
                        ((booking.getFromDate().plusHours(booking.getHours()).isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours())))
                                && (booking.getFromDate().plusHours(booking.getHours()).isAfter(existingBooking.getFromDate())))
                        ||
                        booking.getFromDate().isEqual(existingBooking.getFromDate()))
                        && !existingBooking.getHalfRent()) {

                    logger.warn("Exception", BookingException.duplicateBookingTimeException());

                    throw BookingException.duplicateBookingTimeException();
                }
                if ((((booking.getFromDate().isAfter(existingBooking.getFromDate()))
                        && (booking.getFromDate().isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours()))))
                        ||
                        ((booking.getFromDate().plusHours(booking.getHours()).isBefore(existingBooking.getFromDate().plusHours(existingBooking.getHours())))
                                && (booking.getFromDate().plusHours(booking.getHours()).isAfter(existingBooking.getFromDate())))
                        ||
                        booking.getFromDate().isEqual(existingBooking.getFromDate()))
                        && existingBooking.getHalfRent()) {
                    counterHalfRent ++;
                    if (counterHalfRent >= 2) {

                        logger.warn("Exception", BookingException.duplicateBookingTimeException());

                        throw BookingException.duplicateBookingTimeException();
                    }
                }
            }
        }

    }

    private Boolean checkIfUserHasBooking(Integer bookingId, Integer userId) {
        logger.info("Method checkIfUserHasBooking() called with arguments: {}, {}", bookingId, userId);

        boolean userHasBooking = findById(bookingId).getUserId().equals(userId);

        logger.info("UserHasBooking: {}", userHasBooking);

        return userHasBooking;
    }
}
