package pet.wifispots.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pet.wifispots.data.model.User;
import pet.wifispots.data.repository.UserRepository;
import pet.wifispots.data.specification.UserSpecification;
import pet.wifispots.service.ActiveUser;
import pet.wifispots.service.exception.UserAuthenticationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    ActiveUser activeUser;
    @Autowired
    UserRepository userRepository;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (Objects.isNull(authentication.getName()) || Objects.isNull(authentication.getCredentials()))
            throw new UserAuthenticationException(String.format("null name=%s or password=%s", 
            		authentication.getName(), authentication.getCredentials()));

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findOne(UserSpecification.isBlocked(false).and(UserSpecification.isEmailEqualsIgnoreCase(name.trim())));

        if (Objects.isNull(user))
            throw new UserAuthenticationException(String.format("User with email-%s", name));

        if (!user.getPassword().equalsIgnoreCase(password))
            throw new UserAuthenticationException(String.format("User password with email=%s is not same", name));

        activeUser.setUser(user);

        user.setLastLogin(new Date());
        userRepository.save(user);

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });

        return new UsernamePasswordAuthenticationToken("user", "password", grantedAuths);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
