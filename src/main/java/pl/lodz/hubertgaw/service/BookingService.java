package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.mapper.BookingMapper;
import pl.lodz.hubertgaw.repository.BookingRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.service.exception.BookingException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
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

    public BookingService(BookingRepository bookingRepository,
                          RentEquipmentRepository rentEquipmentRepository,
                          SportObjectRepository sportObjectRepository,
                          SportObjectService sportObjectService,
                          BookingMapper bookingMapper,
                          Logger logger,
                          ServiceUtils serviceUtils) {
        this.bookingRepository = bookingRepository;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.sportObjectRepository = sportObjectRepository;
        this.sportObjectService = sportObjectService;
        this.bookingMapper = bookingMapper;
        this.logger = logger;
        this.serviceUtils = serviceUtils;
    }

    public List<Booking> findAll() {
        return bookingRepository.listAll()
                .stream()
                .map(bookingMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Booking findById(Integer bookingId) {
        BookingEntity entity = bookingRepository.findByIdOptional(bookingId)
                .orElseThrow(BookingException::bookingNotFoundException);

        return bookingMapper.toDomain(entity);
    }

    @Transactional
    public Booking save(Booking booking) {
        booking.setFromDate(serviceUtils.convertTime(booking.getFromDate()));
        if (null == booking.getHalfRent()) {
            booking.setHalfRent(false);
        }
        List<Booking> bookings = sportObjectService.findBookingsForSportObject(booking.getSportObjectId());
        validateBooking(booking, bookings);
        BookingEntity entity = bookingMapper.toEntity(booking);
        bookingRepository.persist(entity);
        return bookingMapper.toDomain(entity);
    }

    @Transactional
    public Booking update(Booking booking) {
        booking.setFromDate(serviceUtils.convertTime(booking.getFromDate()));
        if (booking.getId() == null) {
            throw BookingException.bookingEmptyIdException();
        }
        BookingEntity entity = bookingRepository.findByIdOptional(booking.getId())
                .orElseThrow(BookingException::bookingNotFoundException);

        List<Booking> bookings = sportObjectService.findBookingsForSportObject(booking.getSportObjectId());
        bookings.remove(bookingMapper.toDomain(entity)); // in put we romove booking that we update from list

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

        return bookingMapper.toDomain(entity);
    }

    @Transactional
    public void deleteBookingById(Integer bookingId) {
        BookingEntity bookingToDelete = bookingRepository.findById(bookingId);
        if (bookingToDelete == null) {
            throw BookingException.bookingNotFoundException();
        }
        List<RentEquipmentEntity> rentEquipmentToDelete = bookingToDelete.getRentEquipment();
        for (RentEquipmentEntity rentEquipment : rentEquipmentToDelete) {
            rentEquipment.removeBooking(bookingToDelete);
        }
        bookingRepository.delete(bookingToDelete);
    }


    private void validateBooking(Booking booking, List<Booking> bookings) {

        List<String> equipmentInSportObjectNames = sportObjectRepository
                .findById(booking.getSportObjectId())
                .getRentEquipment().stream()
                .map(RentEquipmentEntity::getName)
                .collect(Collectors.toList());
        List<String> equipmentFromRequestNames = booking.getRentEquipmentNames();
        if (!equipmentInSportObjectNames.containsAll(equipmentFromRequestNames)) {
            throw BookingException.rentEquipmentForSportObjectNotFoundException();
        }

        LocalDateTime fromDate = booking.getFromDate();
        if (fromDate.isBefore(LocalDateTime.now())) {
            throw BookingException.wrongDatePastException();
        }
        if (!(fromDate.getMinute() == 0 || fromDate.getMinute() == 30)) {
            throw BookingException.wrongDateTimeException();
        }

        if (null != booking.getNumberOfPlaces() && booking.getHalfRent()) {
            throw BookingException.numberOfPlacesHalfRentExcludeException();
        }

        SportObject sportObject = sportObjectService.findById(booking.getSportObjectId());

        if (sportObject.getCapacity() == null && booking.getNumberOfPlaces() != null) {
            throw BookingException.invalidNumberOfPlacesForObject();
        }

        if (!sportObject.getIsHalfRentable() && booking.getHalfRent()) {
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
                        throw BookingException.duplicateBookingTimeException();
                    }
                }
            }
        }

        //todo zwweryfikowac
    }




}
