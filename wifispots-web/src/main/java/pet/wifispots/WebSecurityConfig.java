package pet.wifispots;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import lombok.extern.java.Log;
import pet.wifispots.data.model.UserRole;
import pet.wifispots.data.model.settings.RegistrationMode;
import pet.wifispots.data.model.settings.Settings;
import pet.wifispots.data.model.settings.SpotsAdditionMode;
import pet.wifispots.data.model.settings.SpotsViewMode;
import pet.wifispots.service.SettingsService;
import pet.wifispots.web.security.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
@DependsOn("settingsService")
@Log
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {
	@Autowired
    CustomAuthenticationProvider customAuthenticationProvider;
	@Autowired
	SettingsService settingsService;
	@Autowired
	Environment env;
    
    Settings settings;
    
    @PostConstruct
    public void onInit() {
    	settings = settingsService.getSettings();
    	    	
    	if(Objects.isNull(settings)) {
    		settings = new Settings();
    		
    		log.info("Application settings are empty, loading defaults");    		    		
    	}
    }
    
    @Override
	protected void configure(HttpSecurity http) throws Exception {
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry configuration =
		 http
         .authorizeRequests()
         .antMatchers("/images/**").permitAll()
         .antMatchers("/css/**").permitAll()
         .antMatchers("/js/**").permitAll()
         .antMatchers("/sass/**").permitAll()
         .antMatchers("/fonts/**").permitAll()
         .antMatchers("/", "/home", "/contact", "/welcome").permitAll()
         .antMatchers("/users/**").hasAuthority(UserRole.ADMINISTRATOR.toString())
         .antMatchers("/settings/**").hasAuthority(UserRole.ADMINISTRATOR.toString());

		if(settings.getAdditionMode().equals(SpotsAdditionMode.EVERY_ONE)) {
            configuration = configuration
                    .antMatchers("/add/**", "/update/**").permitAll();

		} else if (settings.getAdditionMode().equals(SpotsAdditionMode.ONLY_REGISTERED_USERS)) {
            configuration = configuration
                    .antMatchers("/add/**", "/update/**").hasAnyAuthority(
							UserRole.USER.toString(),
                            UserRole.ADMINISTRATOR.toString());
		} else if (settings.getAdditionMode().equals(SpotsAdditionMode.ONLY_ADMINISTRATORS)) {
			configuration = configuration
					.antMatchers("/add/**", "/update/**").hasAnyAuthority(UserRole.ADMINISTRATOR.toString());
		}
		
		if(settings.getViewMode().equals(SpotsViewMode.EVERY_ONE)) {
			configuration = configuration
					.antMatchers("/spots/**", "/maps/**").permitAll();
		} else {
			configuration = configuration
					.antMatchers("/spots/**", "/maps/**").hasAnyAuthority(
							UserRole.USER.toString(),
							UserRole.ADMINISTRATOR.toString());
		}

		if(settings.getRegistrationMode().equals(RegistrationMode.ADMINISTRATOR_ONLY)) {
			configuration = configuration
					.antMatchers("/register/**").hasAuthority(UserRole.ADMINISTRATOR.toString());
		} else {
			configuration = configuration
					.antMatchers("/register/**").permitAll();
		}

		// h2 console enabled in Default profile only
		if(env.getActiveProfiles().length == 0)  {
			configuration = configuration.antMatchers("/").permitAll().and()
					.authorizeRequests().antMatchers("/console/**").permitAll();

			configuration.and().csrf().disable();
			configuration.and().headers().frameOptions().disable();
		}

        configuration.anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
        .logout()
        .permitAll();
	}
            
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }
}

