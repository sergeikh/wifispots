package pet.wifispots.data.specification;

import com.querydsl.core.types.dsl.BooleanExpression;
import pet.wifispots.data.model.Category;
import pet.wifispots.data.model.QSpot;
import pet.wifispots.data.model.User;
import pet.wifispots.data.model.address.City;
import pet.wifispots.data.model.address.Country;
import pet.wifispots.data.model.address.State;

public class SpotSpecification {
    public static BooleanExpression isCreatedByUserWithEmailContains(String email) {return QSpot.spot.user.email.containsIgnoreCase(email); }

    public static BooleanExpression inPostalCode(String postalCode) {return QSpot.spot.postalCode.name.containsIgnoreCase(postalCode); }

    public static BooleanExpression isInState(State state) {return QSpot.spot.state.eq(state); }

    public static BooleanExpression isInCountry(Country country) {return QSpot.spot.country.eq(country); }

    public static BooleanExpression isInCity(City city) {return QSpot.spot.city.eq(city); }

    public static BooleanExpression isCategory(Category category) {return QSpot.spot.category.eq(category); }

    public static BooleanExpression isPersonal(boolean personal) {return QSpot.spot.personal.eq(personal); }

    public static BooleanExpression isBelongToUser(User user) {return QSpot.spot.user.eq(user); }

}
