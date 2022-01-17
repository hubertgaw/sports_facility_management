package pl.lodz.hubertgaw.utils;

import org.apache.commons.lang3.RandomStringUtils;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.RentEquipment;

public class TestUtils {
    public static String getActualExceptionMessage(String response) {
        String[] parts = response.split(" ", 2);
        return parts[1];
    }

    public static RentEquipment createRentEquipment() {
        RentEquipment rentEquipment = new RentEquipment();
        rentEquipment.setName(RandomStringUtils.randomAlphabetic(10));
        rentEquipment.setPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return rentEquipment;
    }

    public static AthleticsTrack createAthleticsTrack() {
        AthleticsTrack athleticsTrack = new AthleticsTrack();
        athleticsTrack.setName(RandomStringUtils.randomAlphabetic(10));
        athleticsTrack.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setCapacity(Integer.valueOf(RandomStringUtils.randomNumeric(2)));
        athleticsTrack.setSingleTrackPrice(Double.valueOf(RandomStringUtils.randomNumeric(1)));
        return athleticsTrack;
    }

    public static BeachVolleyballCourt createBeachVolleyballCourt() {
        BeachVolleyballCourt beachVolleyballCourt = new BeachVolleyballCourt();
        beachVolleyballCourt.setName(RandomStringUtils.randomAlphabetic(10));
        beachVolleyballCourt.setFullPrice(Double.valueOf(RandomStringUtils.randomNumeric(2)));
        return beachVolleyballCourt;
    }

}
