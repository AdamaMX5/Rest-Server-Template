package de.freeschool.api;

import de.freeschool.api.models.Role;
import de.freeschool.api.models.type.RoleType;
import de.freeschool.api.repository.RoleRepository;
import de.freeschool.api.manager.UserManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Component
public class InitializeRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(InitializeRunner.class);

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private UserManager userManager;
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Override
    @Transactional
    public void run(
            String... args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        validateConfig();
        initDatabase();
    }

    private void validateConfig() {
        logger.info("Checking config");
        if (jwtSecret.isEmpty()) {
            throw new RuntimeException("app.jwtSecret must be set to a randomly generated value! Please generated a " +
                    "secret and set it in you overrides.properties file!");
        }
    }

    @Transactional
    private void initDatabase() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        logger.info("Initializing the database");

        if (roleRepository.findByName(RoleType.USER).isEmpty()) {
            Role role = new Role();
            role.setName(RoleType.USER);
            roleRepository.save(role);
            logger.info("InitializeRunner: UserRole saved");
        }

        if (roleRepository.findByName(RoleType.ADMIN).isEmpty()) {
            Role role = new Role();
            role.setName(RoleType.ADMIN);
            roleRepository.save(role);
            logger.info("InitializeRunner: AdminRole saved");
        }

        userManager.setupDefaultUsers();
    }
}
