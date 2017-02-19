package pet.wifispots.web.spots;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pet.wifispots.data.model.Spot;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.repository.SpotRepository;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.data.repository.address.CityRepository;
import pet.wifispots.data.repository.address.CountryRepository;
import pet.wifispots.data.repository.address.StateRepository;
import pet.wifispots.service.ActiveUser;
import pet.wifispots.service.CategoryService;
import pet.wifispots.service.SpotService;
import pet.wifispots.service.exception.SpotNotFoundException;
import pet.wifispots.web.filter.SpotsFilterForm;
import pet.wifispots.web.utils.PageWrapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pet.wifispots.data.specification.CitySpecification.cityInCountryAndState;
import static pet.wifispots.data.specification.SpotSpecification.*;
import static pet.wifispots.data.specification.StateSpecification.stateInCountry;

@Controller
@Log
public class SpotsController {
    @Autowired
    ActiveUser activeUser;
    @Autowired
    SpotRepository spotRepository;
    @Autowired
    SpotService spotService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StateRepository stateRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;

    @RequestMapping(value="/spots", method= RequestMethod.GET)
    public String getSpots(Model model, Pageable p) {
        BooleanExpression filterExpr = null;
        SpotsFilterForm sff = null;

        if(isNull(activeUser.getUser())) { // he can view only non personal spots
            filterExpr = isPersonal(false);
            model.addAttribute("page", new PageWrapper<Spot>(spotRepository.findAll(filterExpr, p), "/spots"));
        } else if(activeUser.getUser().getRole() == UserRole.USER) {
            filterExpr = isPersonal(false).orAllOf(isPersonal(true), isBelongToUser(activeUser.getUser()));
            model.addAttribute("page", new PageWrapper<Spot>(spotRepository.findAll(filterExpr, p), "/spots"));
        } else { // administrator can view all
            model.addAttribute("page", new PageWrapper<Spot>(spotRepository.findAll(p), "/spots"));
        }

        // filter spots
        if (model.containsAttribute("spotsFilterForm")) {
            sff = (SpotsFilterForm) model.asMap().get("spotsFilterForm");

            if(nonNull(sff.getCountry())) {
                filterExpr = filterExpr.and(isInCountry(sff.getCountry()));
            }

            if(nonNull(sff.getState())) {
                filterExpr = filterExpr.and(isInState(sff.getState()));
            }

            if(nonNull(sff.getCity())) {
                filterExpr = filterExpr.and(isInCity(sff.getCity()));
            }

            if(nonNull(sff.getCategory()))
                filterExpr = filterExpr.and(isCategory(sff.getCategory()));

            if(nonNull(sff.getPostalCode()) && !sff.getPostalCode().trim().isEmpty())
                filterExpr = filterExpr.and(inPostalCode(sff.getPostalCode()));

            if(nonNull(sff.getUserEmail()) && !sff.getUserEmail().trim().isEmpty())
                filterExpr = filterExpr.and(isCreatedByUserWithEmailContains(sff.getUserEmail()));

            model.addAttribute("spotsFilterForm", sff);
            model.addAttribute("page", new PageWrapper<Spot>(spotRepository.findAll(filterExpr, p), "/spots"));
        } else {
            model.addAttribute("spotsFilterForm", new SpotsFilterForm());
        }

        addFilterAttributes(model, sff);

        return "spots/list";
    }

    private void addFilterAttributes(Model model, SpotsFilterForm sff) {
        model.addAttribute("countries", countryRepository.findAll());

        if(nonNull(sff) && nonNull(sff.getCountry()))
            model.addAttribute("states", stateRepository.findAll(stateInCountry(sff.getCountry())));

        if(nonNull(sff) && nonNull(sff.getCountry()) && nonNull(sff.getState()))
            model.addAttribute("cities", cityRepository.findAll(cityInCountryAndState(sff.getCountry(), sff.getState())));

        model.addAttribute("categories", categoryService.getCategories());
    }

    @RequestMapping(value="/spots/{id}", method=RequestMethod.GET)
    public String editSpot(Model model, @PathVariable("id") Long id) {
        Spot spot = spotRepository.findOne(id);

        model.addAttribute("spot", spot);
        model.addAttribute("categories", categoryService.getCategories());

        return "spots/spot";
    }

    @RequestMapping(value="/update/{id}", method=RequestMethod.POST)
    public String updateSpot(RedirectAttributes model, @PathVariable("id") Long id, Spot spot, BindingResult bindingResult ) {

        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.warning("Spots binding error " + error.toString()));
        }

        if(id != spot.getId())
            throw new SpotNotFoundException(String.format("Spot.id=%d is not same as post parameter id=%d.", spot.getId(), id));

        spotService.updateSpot(spot);

        if(model.containsAttribute("spotsFilterForm")) {
            System.out.println("Contains spotFilterForm");
            model.addFlashAttribute("spotsFilterForm", model.getFlashAttributes().get("spotsFilterForm"));
        }

        return "redirect:/spots";
    }

    @RequestMapping(value="/update/{id}", params="updateAddress", method=RequestMethod.POST)
    public String updateSpotAddress(@PathVariable("id") Long id, Spot spot, BindingResult bindingResult ) {
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.warning("Spots binding error " + error.toString()));
        }

        if(id != spot.getId())
            throw new SpotNotFoundException(String.format("Spot.id=%d is not same as post parameter id=%d.", spot.getId(), id));

        spotService.requestReverseGeocoding(spot);

        return "redirect:/spots";
    }
}
