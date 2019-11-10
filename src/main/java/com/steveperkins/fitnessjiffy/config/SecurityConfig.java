package com.steveperkins.fitnessjiffy.config;

import com.steveperkins.fitnessjiffy.domain.User;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.dto.converter.UserToUserDTO;
import com.steveperkins.fitnessjiffy.repository.UserRepository;
import com.steveperkins.fitnessjiffy.service.ReportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ReportDataService reportDataService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserToUserDTO userDTOConverter;

    /**
     * Sets up Spring Security rules for which URL patterns to exclude from authentication checks, as well as
     * config for the login form and logout link.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/static/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .successHandler(new AuthSuccessHandler())
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    /**
     * Spring Security framework-level method, to set the object that will verify a username and password during
     * the login process.  See {@link this#buildDaoAuthenticationProvider()}.
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(buildDaoAuthenticationProvider());
    }

    /**
     * Factory method for the object that verifies a username and password during the login process.
     *
     * @return
     */
    @Nonnull
    private DaoAuthenticationProvider buildDaoAuthenticationProvider() {
        final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(username -> {
            final User user = userRepository.findByEmailEquals(username);
            final UserDTO userDTO = userDTOConverter.convert(user);
            return (user == null || userDTO == null) ? null : new SpringUserDetails(userDTO, user.getPasswordHash());
        });
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * The {@link this#configure(HttpSecurity)} method registers an instance of this class, to be executed upon
     * every successful login.  This logic updates the user's last-login-date, kicks off an update of the user's
     * report data, and stores a JWT token as a browser cookie.
     *
     * TODO: Need a way to make Spring Security check for the JWT token in a cookie (and/or header), and allow or block based on that
     */
    private final class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            // Retrieve the user
            final SpringUserDetails userDetails = (SpringUserDetails) authentication.getPrincipal();
            final UserDTO userDTO = userDetails.getUserDTO();
            final User user = userRepository.findOne(userDTO.getId());

            // Schedule a ReportData update
            final Date lastUpdateDate = new Date(user.getLastUpdatedTime().getTime());
            reportDataService.updateUserFromDate(user, lastUpdateDate);

            // TODO: Create a JWT token and store it in a cookie
            response.addCookie(new Cookie("foo", "bar"));
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * A custom override of the Spring Security level POJO for authenticated users.  After authentication, Spring
     * Security makes this available through {@link SecurityContextHolder#getContext()#getAuthentication()#getPrincipal()}.
     */
    public static final class SpringUserDetails implements UserDetails {

        private final UserDTO userDTO;
        private final String password;

        public SpringUserDetails(
                @Nonnull final UserDTO userDTO,
                @Nonnull final String password
        ) {
            this.userDTO = userDTO;
            this.password = password;
        }

        @Override
        @Nonnull
        public String getUsername() {
            return this.userDTO.getEmail();
        }

        @Override
        @Nullable
        public String getPassword() {
            return this.password;
        }

        @Nullable
        public UserDTO getUserDTO() {
            return this.userDTO;
        }

        @Override
        @Nonnull
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return new HashSet<>();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
