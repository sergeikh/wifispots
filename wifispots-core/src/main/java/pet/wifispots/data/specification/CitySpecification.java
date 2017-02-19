package pet.wifispots.data.specification;

import com.querydsl.core.types.dsl.BooleanExpression;
import pet.wifispots.data.model.address.Country;
import pet.wifispots.data.model.address.QCity;
import pet.wifispots.data.model.address.State;

public class CitySpecification {

    public static BooleanExpression cityInCountryAndState(Country country, State state) {
        return QCity.city.country.eq((country)).and(QCity.city.state.eq(state));
    }

}
