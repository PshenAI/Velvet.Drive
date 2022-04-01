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

import java.util.List;


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
            public void run(String... strings) throws Exception {
                Storage storage1 = new Storage("D:\\Velvet.DriveStorage1\\",51200L,51200L);
                Storage storage2 = new Storage("D:\\Velvet.DriveStorage2\\",40960L,40960L);
                Storage storage3 = new Storage("D:\\Velvet.DriveStorage3\\",30720L,30720L);
                List<Storage> storages = List.of(storage1, storage2, storage3);
                storageService.addStorages(storages);
                factory.createUser("admin", encoder.encode("admin"),
                        UserRole.ADMIN, "Velvet Velvetov");
                factory.createUser("user", encoder.encode("user"),
                        UserRole.USER, "Lavender Lavenderov");
//                factory.createUser("lexon@test.com", encoder.encode("admin"),
//                        UserRole.ADMIN, "Velvet", "Velvetov");
//                factory.createUser("horny@test.com", encoder.encode("user"),
//                        UserRole.USER, "Lavender", "Lavenderov");
            }
        };
    }
}
