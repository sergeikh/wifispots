package pet.wifispots.reverse.geocoder.v1.boundary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pet.wifispots.WifispotsReverseGeocoderApplication;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.repository.SpotRepository;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WifispotsReverseGeocoderApplication.class)
@WebAppConfiguration
public class ReverseGeocoderV1TestIT {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    SpotRepository spotRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGeocodeExistingSpot() throws Exception {
        mockMvc.perform(get("/v1/rgeocode/1"))
                .andExpect(status().isOk());

        Spot spot = spotRepository.findOne(1l);
        assertThat(spot.getAddress(), notNullValue());
        assertThat(spot.getCity(), notNullValue());
        assertThat(spot.getState(), notNullValue());
        assertThat(spot.getCountry(), notNullValue());
        assertThat(spot.getCountry().getPlaceId(), notNullValue());
    }

    @Test
    public void testGeocodeExistingSpotWithEmptyPoint() throws Exception {
        Spot emptyPointSpot = new Spot();
        emptyPointSpot = spotRepository.save(emptyPointSpot);

        mockMvc.perform(get("/v1/rgeocode/".concat(""+emptyPointSpot.getId())))
                .andExpect(status().isFound());
    }

    @Test
    public void testGeocodeNotExistingSpot() throws Exception {
        mockMvc.perform(get("/v1/rgeocode/3"))
                .andExpect(status().isNotFound());
    }
}