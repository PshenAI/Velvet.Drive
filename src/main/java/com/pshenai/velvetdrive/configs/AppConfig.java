package com.pshenai.velvetdrive.configs;

import com.pshenai.velvetdrive.entities.storage.Storage;
import com.pshenai.velvetdrive.entities.storage.StorageManager;
import com.pshenai.velvetdrive.entities.storage.StorageService;
import com.pshenai.velvetdrive.entities.user.UserFactory;
import com.pshenai.velvetdrive.entities.user.UserRole;
import com.pshenai.velvetdrive.entities.user.UserService;
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
    public CommandLineRunner onStart(final StorageService storageService,
                                     final PasswordEncoder encoder,
                                     final UserFactory factory) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                Storage storage1 = new Storage("\u202AD:\\Velvet.DriveStorage1",50,50);
                Storage storage2 = new Storage("\u202AD:\\Velvet.DriveStorage2",10,10);
                Storage storage3 = new Storage("\u202AD:\\Velvet.DriveStorage3",30,30);
                List<Storage> storages = List.of(storage1, storage2, storage3);
                storageService.addStorages(storages);
                factory.createUser("velvet@test.com", encoder.encode("admin"),
                        UserRole.ADMIN, "Velvet", "Velvetov");
                factory.createUser("lavender@test.com", encoder.encode("user"),
                        UserRole.USER, "Lavender", "Lavenderov");
//                factory.createUser("lexon@test.com", encoder.encode("admin"),
//                        UserRole.ADMIN, "Velvet", "Velvetov");
//                factory.createUser("horny@test.com", encoder.encode("user"),
//                        UserRole.USER, "Lavender", "Lavenderov");
//                userService.addUser("velvet@test.com", encoder.encode("admin"),
//                        UserRole.ADMIN, "Velvet", "Velvetov");
//                userService.addUser("lavender@test.com", encoder.encode("user"),
//                        UserRole.USER, "Lavender", "Lavenderov");
            }
        };
    }
}
