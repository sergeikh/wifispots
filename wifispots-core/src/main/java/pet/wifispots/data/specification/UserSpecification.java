package pet.wifispots.data.specification;

import com.querydsl.core.types.dsl.BooleanExpression;
import pet.wifispots.data.model.QUser;
import pet.wifispots.data.model.UserRole;

public class UserSpecification {
    public static BooleanExpression isEmailContains(String email) {return QUser.user.email.containsIgnoreCase(email); }

    public static BooleanExpression isEmailEqualsIgnoreCase(String email) {return QUser.user.email.equalsIgnoreCase(email); }

    public static BooleanExpression isRole(UserRole role) {
        return QUser.user.role.eq(role);
    }

    public static BooleanExpression isBlocked(boolean blocked) {
        return QUser.user.blocked.eq(blocked);
    }
}
