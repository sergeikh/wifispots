package pet.wifispots.reverse.geocoder;

import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.address.*;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.data.repository.address.CityRepository;
import pet.wifispots.data.repository.address.CountryRepository;
import pet.wifispots.data.repository.address.PostalCodeRepository;
import pet.wifispots.data.repository.address.StateRepository;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Log
public class GoogleReverseGeocoderParser {
    @Autowired
    SpotRepository spotRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    PostalCodeRepository postalCodeRepository;

    public void parse(Spot spot, JSONArray results) {
        int size = results.length();

        for (int i = 0; i < size; i++) {
            JSONObject object = results.getJSONObject(i);
            String addressType = (String) object.getJSONArray("types").get(0);

            switch (addressType) {
                case "street_address": {
                    spot.setAddress(object.getString("formatted_address"));
                    break;
                }
                case "country": {
                    Optional<Country> country = countryRepository.findByPlaceId(object.getString("place_id"));
                    spot.setCountry(country.orElseGet(() -> createCountry(object)));
                    break;
                }
                case "administrative_area_level_1": {
                    Optional<State> state = stateRepository.findByPlaceId(object.getString("place_id"));
                    spot.setState(state.orElseGet(() -> createState(object)));
                    break;
                }
                case "administrative_area_level_2": {
                    Optional<City> city = cityRepository.findByPlaceId(object.getString("place_id"));
                    spot.setCity(city.orElseGet(() -> createCity(object)));
                    break;
                }
                case "postal_code": {
                    Optional<PostalCode> postalCode = postalCodeRepository.findByPlaceId(object.getString("place_id"));
                    spot.setPostalCode(postalCode.orElseGet(() -> createPostalCode(object)));
                }
            }
        }

        updateAddressComponents(spot);

        spotRepository.save(spot);
    }

    // set country in state, state & country in city
    private void updateAddressComponents(Spot spot) {
        // update spot state, country
        if(nonNull(spot.getState())
                && isNull(spot.getState().getCountry())
                && nonNull(spot.getCountry())) {
            spot.getState().setCountry(spot.getCountry());

            stateRepository.save(spot.getState());
        }

        // update spot city, country
        if(nonNull(spot.getCity())
                && isNull(spot.getCity().getCountry())
                && nonNull(spot.getCountry())){
            spot.getCity().setCountry(spot.getCountry());

            cityRepository.save(spot.getCity());
        }

        // update spot city, state
        if(nonNull(spot.getCity())
                && isNull(spot.getCity().getCountry())
                && nonNull(spot.getState())){
            spot.getCity().setState(spot.getState());

            cityRepository.save(spot.getCity());
        }
    }

    private PostalCode createPostalCode(JSONObject object) {
        PostalCode postalCode = new PostalCode();
        setAddressComponentAttributes(object, postalCode);

        postalCodeRepository.save(postalCode);

        return postalCode;
    }

    private City createCity(JSONObject object) {
        City city = new City();
        setAddressComponentAttributes(object, city);

        cityRepository.save(city);

        return city;
    }

    private State createState(JSONObject object) {
        State state = new State();
        setAddressComponentAttributes(object, state);

        stateRepository.save(state);

        return state;
    }

    private Country createCountry(JSONObject object) {
        Country country = new Country();
        setAddressComponentAttributes(object, country);

        countryRepository.save(country);

        return country;
    }

    private void setAddressComponentAttributes(JSONObject object, AddressComponent addressComponent) {
        setPlaceId(addressComponent, object);
        setBounds(addressComponent, object);
        setName(addressComponent, object);
    }

    private void setName(AddressComponent component, JSONObject object) {
        component.setName(object.getJSONArray("address_components").getJSONObject(0).getString("long_name"));
    }

    private void setBounds(AddressComponent component, JSONObject object) {
        JSONObject geometry = object.getJSONObject("geometry");
        JSONObject boundsGeometry;

        boundsGeometry = geometry.getJSONObject("bounds");

        Bounds bounds = new Bounds();

        Point northeast = new Point();
        northeast.setLat(boundsGeometry.getJSONObject("northeast").getDouble("lat"));
        northeast.setLng(boundsGeometry.getJSONObject("northeast").getDouble("lng"));

        Point southwest = new Point();
        southwest.setLat(boundsGeometry.getJSONObject("southwest").getDouble("lat"));
        southwest.setLng(boundsGeometry.getJSONObject("southwest").getDouble("lng"));

        bounds.setNortheast(northeast);
        bounds.setSouthwest(southwest);

        component.setBounds(bounds);

        boundsGeometry = geometry.getJSONObject("viewport");

        Bounds viewPort = new Bounds();

        northeast = new Point();
        northeast.setLat(boundsGeometry.getJSONObject("northeast").getDouble("lat"));
        northeast.setLng(boundsGeometry.getJSONObject("northeast").getDouble("lng"));

        southwest = new Point();
        southwest.setLat(boundsGeometry.getJSONObject("southwest").getDouble("lat"));
        southwest.setLng(boundsGeometry.getJSONObject("southwest").getDouble("lng"));
        viewPort.setNortheast(northeast);
        viewPort.setSouthwest(southwest);

        component.setViewport(viewPort);
    }

    private void setPlaceId(AddressComponent component, JSONObject object) {
        component.setPlaceId(object.getString("place_id"));
    }
}
