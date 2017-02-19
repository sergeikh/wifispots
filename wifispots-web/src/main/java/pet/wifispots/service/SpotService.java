package pet.wifispots.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import pet.wifispots.data.model.Error;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.ErrorRepository;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.service.exception.SpotCantBeEditedException;
import pet.wifispots.service.exception.SpotNotFoundException;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

@Service
@Log
public class SpotService {
    @Autowired
    SpotRepository spotRepository;
    @Autowired
    ActiveUser activeUser;
    @Autowired
    AsyncRestTemplate asyncRestTemplate;
    @Autowired
    SettingsService settingsService;
    @Autowired
    ErrorRepository errorRepository;

    Predicate<User> isActiveUserAdministrator = (u) -> Objects.isNull(u) ? false : u.getRole().equals(UserRole.ADMINISTRATOR);

    /**
     * Create new spot.
     */
    public Spot createSpot(Spot spot) {
        requestReverseGeocoding(spot);

        return null;
    }

    /**
     * Update spot.
     */
    public Spot updateSpot(Spot spotWeb) {
        Spot spot = spotRepository.findOne(spotWeb.getId());

        checkSpotExist(spot);
        checkUserCanEditSpot(spot);

        spot.setCategory(spotWeb.getCategory());
        spot.setDescription(spotWeb.getDescription());
        spot.setName(spotWeb.getName());
        spot.setPassword(spotWeb.getPassword());
        spot.setUrl(spotWeb.getUrl());

        if(isActiveUserAdministrator.test(activeUser.getUser())) {
            spot.setPoint(spotWeb.getPoint());
        }

        return spotRepository.save(spot);
    }

    /**
     * Reverse geocode existing spot.
     */
    public void requestReverseGeocoding(Spot spot) {
        ListenableFuture<ResponseEntity<String>> futureResult = asyncRestTemplate.getForEntity(settingsService.getSettings().getReverseGeocoderURL(),
                String.class, spot.getId());

        futureResult.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                    @Override
                    public void onSuccess(ResponseEntity<String> result) {
                        // do nothing
                    }
                    @Override
                    public void onFailure(Throwable ex) {
                        Error error = new Error();
                        error.setCreated(new Date());
                        log.info(String.format("Reverse spotId %d ERROR %s",
                                spot.getId(),
                                ex.getMessage()));
                        error.setException(String.format("Reverse spotId %d ERROR %s",
                                spot.getId(),
                                ex.getMessage()));
                        error.setType(Arrays.toString(ex.getStackTrace()));

                        log.warning("Error in geocoding "+ex.getMessage()+ " trace "+ ex.fillInStackTrace());

                        errorRepository.save(error);
                    }
                });
    }

    private void checkUserCanEditSpot(Spot spot) {
        if((spot.isPersonal() && Objects.isNull(activeUser.getUser())) ||
                (spot.isPersonal() && activeUser.getUser().getRole() != UserRole.ADMINISTRATOR
                        && !activeUser.getUser().equals(spot.getUser()))) {
            throw new SpotCantBeEditedException(String.format("Spot by id=%d and personal %b can't be edited by user %s with role %s",
                    spot.getId(), spot.isPersonal(),
                    Objects.nonNull(activeUser.getUser()) ? activeUser.getUser().getEmail() : "",
                    Objects.nonNull(activeUser.getUser()) ? activeUser.getUser().getRole() : ""));
        }
    }

    private void checkSpotExist(Spot spot) {
        if(Objects.isNull(spot))
            throw new SpotNotFoundException("Update Spot is null.");
    }
}
