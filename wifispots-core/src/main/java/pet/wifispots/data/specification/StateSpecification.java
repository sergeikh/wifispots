package pet.wifispots.data.specification;

import com.querydsl.core.types.dsl.BooleanExpression;
import pet.wifispots.data.model.address.Country;
import pet.wifispots.data.model.address.QState;

public class StateSpecification {

    public static BooleanExpression stateInCountry(Country country) {return QState.state.country.eq((country)); }

}
