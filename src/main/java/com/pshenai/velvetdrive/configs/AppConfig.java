package com.pshenai.velvetdrive.configs;

import com.pshenai.velvetdrive.entities.storage.Storage;
import com.pshenai.velvetdrive.entities.storage.StorageService;
import com.pshenai.velvetdrive.entities.user.UserFactory;
import com.pshenai.velvetdrive.entities.user.UserRole;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public EmailValidator emailValidator() {return EmailValidator.getInstance();}

    @Bean
    public CommandLineRunner onStart(final StorageService storageService,
                                     final PasswordEncoder encoder,
                                     final UserFactory factory) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings){
                Storage mainStorage = new Storage("velvet-drive-storage",1_048_576L,0L);
                storageService.addStorage(mainStorage);
                factory.createUser("admin", encoder.encode("admin"),
                        UserRole.ADMIN, "Velvet Velvetov",null);
//                factory.createUser("user", encoder.encode("user"),
//                        UserRole.USER, "Lavender Lavenderov",null);
            }
        };

    }
}
