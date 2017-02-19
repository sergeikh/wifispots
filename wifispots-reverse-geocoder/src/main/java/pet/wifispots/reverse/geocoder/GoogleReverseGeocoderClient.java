package pet.wifispots.reverse.geocoder;

import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pet.wifispots.data.model.Error;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.repository.ErrorRepository;

import java.util.Date;

@Component
@Log
public class GoogleReverseGeocoderClient {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GoogleReverseGeocoderParser parser;
    @Autowired
    ErrorRepository errorRepository;

    private static final String url = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=";

    public void reverseGeocode(Spot spot) {
        String forObject;
        String queryURL = url.concat(""+spot.getPoint().getLat()).concat(",").concat(""+spot.getPoint().getLng());
        try {
            forObject = restTemplate.getForObject(queryURL, String.class);
        } catch (Exception ex) {
            Error error = new Error();
            error.setCreated(new Date());
            error.setException(String.format("Reverse spotId %d \n URL ERROR %s \n", spot.getId(), queryURL).concat(ex.getStackTrace().toString()));
            error.setType(ex.getClass().getName());

            log.warning(error.getType());

            errorRepository.save(error);

            return;
        }

        JSONObject jsonObject = new JSONObject(forObject);

        if(!jsonObject.getString("status").equalsIgnoreCase("OK")) {
            Error error = new Error();
            error.setCreated(new Date());
            error.setType(String.format("Wrong Google reverse geocode response code %s. for spot %d", jsonObject.getString("status"), spot.getId()));
            error.setException(String.format("URL was %s", queryURL));

            log.warning(error.getType());

            errorRepository.save(error);
        }

        JSONArray results = jsonObject.getJSONArray("results");

        parser.parse(spot, results);
    }
}
