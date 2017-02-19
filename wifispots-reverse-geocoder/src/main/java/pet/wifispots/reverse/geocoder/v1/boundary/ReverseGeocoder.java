package pet.wifispots.reverse.geocoder.v1.boundary;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.reverse.geocoder.GoogleReverseGeocoderClient;

import java.util.Objects;

@RestController("reverseGeocoderV1")
@RequestMapping("/v1/")
@Log
public class ReverseGeocoder {
    @Autowired
    SpotRepository spotRepository;

    @Autowired
    GoogleReverseGeocoderClient client;

    /**
     * Decode address components from coordinates.
     * @param spotId Spot id for address geocoding.
     * @return Returns NOT_FOUND if spot id is not found, FOUND if spot id is found but no coordinates provided.
     */
    @RequestMapping(value="/rgeocode/{spotId}", method= RequestMethod.GET)
    public ResponseEntity<?> reverseGeocodeSpot(@PathVariable Long spotId) {
        log.info(String.format("Reverse geocode spot id=%s.", spotId));

        Spot spotById  = spotRepository.findOne(spotId);
        if(Objects.isNull(spotById)) {
            log.warning(String.format("Spot id=%s is not found.", spotId));

            return new ResponseEntity<>(String.format("Spot id=%s is not found.", spotId), HttpStatus.NOT_FOUND);
        }

        if(Objects.isNull(spotById.getPoint()) || spotById.getPoint().getLat() == 0 || spotById.getPoint().getLng() == 0) {
            log.warning(String.format("Spot id=%s is found but coordinates are empty.", spotId));

            return new ResponseEntity<>(String.format("Spot id=%s is found but coordinates are empty.", spotId), HttpStatus.FOUND);
        }

        client.reverseGeocode(spotById);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
