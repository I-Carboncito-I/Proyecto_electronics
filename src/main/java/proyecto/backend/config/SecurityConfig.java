package proyecto.backend.config;

import org.apache.catalina.UserDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/manage/login").permitAll() // Rutas sin autenticación
                        .requestMatchers("/inicio").hasAnyRole("ADMIN", "OPERATOR") // Rutas para ADMIN y OPERATOR
                        .requestMatchers("/manage/add").hasAnyRole("ADMIN") // Rutas solo para ADMIN
                        .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((req, resp, excep) -> {
                            resp.sendRedirect("/manage/restricted");
                        })
                )
                .formLogin(form -> form
                        .loginPage("/manage/login")
                        .defaultSuccessUrl("/inicio", false)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/manage/logout")
                        .logoutSuccessUrl("/manage/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/delete/**")  // Ignorar CSRF para ciertas rutas (como eliminación)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
