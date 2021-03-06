package nl.lijstr;

import java.io.File;
import java.io.IOException;
import java.util.*;

import nl.lijstr.domain.other.ApprovedFor;
import nl.lijstr.domain.users.GrantedPermission;
import nl.lijstr.domain.users.Permission;
import nl.lijstr.domain.users.User;
import nl.lijstr.exceptions.LijstrException;
import nl.lijstr.processors.annotations.InjectLogger;
import nl.lijstr.repositories.users.PermissionRepository;
import nl.lijstr.repositories.users.UserRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.naming.InitialContext;

/**
 * Created by Stoux on 19/04/2016.
 */
@Component
public class StartupExecutor implements ApplicationListener<ContextRefreshedEvent> {

    private static final String TEST_FILE = "test.txt";

    @InjectLogger
    private Logger logger;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Value("${admin.email}")
    private String adminMail;
    @Value("${admin.password}")
    private String adminPassword;

    @Value("${sentry.tomcat}")
    private boolean sentryTomcat;

    @Value("${server.image-location}")
    private String imgFolderLocation;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        setupSentry();
        addPermissions();
        addAdmin();
        validateWritePermissions();
    }

    private void setupSentry() {
        if (!sentryTomcat) {
            logger.debug("[Sentry] Skipping tomcat integration");
            return;
        }

        try {
            logger.debug("[Sentry] Attempting tomcat integration");
            InitialContext context = new InitialContext();
            Object file = context.lookup("java:comp/env/sentry.properties.file");
            if (file == null) {
                logger.warn("[Sentry] No path to file found in context");
            } else {
                String pathToConfig = file.toString();
                File configFile = new File(pathToConfig);
                if (!configFile.exists()) {

                }

                logger.info("[Sentry] Setting to env: " + file);
                System.setProperty("sentry.properties.file", file.toString());
            }

        } catch (Exception e) {
            logger.warn(e);
        }
    }

    private void addPermissions() {
        logger.info("Checking User permissions...");
        //Get all permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> requiredPermissions = new HashSet<>(Arrays.asList(Permission.list()));

        //Check which ones have been made already
        for (Permission allPermission : allPermissions) {
            requiredPermissions.remove(allPermission.getName());
        }

        //Add any new ones
        for (String requiredPermission : requiredPermissions) {
            logger.info("Adding missing permission: {}", requiredPermission);
            permissionRepository.saveAndFlush(new Permission(requiredPermission));
        }
    }

    private void addAdmin() {
        if (userRepository.findByUsername("admin") != null) {
            return;
        }

        logger.info("No Admin account detected");

        User user = new User("admin", "Admin", adminMail, ApprovedFor.MEME);
        user.setHashedPassword(passwordEncoder.encode(adminPassword));
        user.setGrantedPermissions(new ArrayList<>());

        String[] perms = new String[]{Permission.ADMIN, Permission.USER};
        for (String name : perms) {
            Permission permission = permissionRepository.findByName(name);
            user.getGrantedPermissions().add(new GrantedPermission(user, permission));
        }

        User admin = userRepository.saveAndFlush(user);
        logger.info("Added Admin account. ID: {}", admin.getId());
    }

    private void validateWritePermissions() {
        logger.info("Validating image folder read/write permissions.");

        //Check if the folder exists
        File imgFolder = new File(imgFolderLocation);
        if (!imgFolder.exists()) {
            try {
                if (!imgFolder.mkdirs()) throw new SecurityException("Self thrown: Unable to create folder.");
            } catch (SecurityException e) {
                logger.fatal("Unable to create image folder", e);
                throw new LijstrException("Unable to create image folder");
            }
        }

        if (!imgFolder.isDirectory()) {
            logger.fatal("Path '{}' is not a valid directory.", imgFolderLocation);
            throw new LijstrException("Unable to use image folder");
        }

        File testFile = new File(imgFolder, TEST_FILE);

        //Delete test file if it still exists
        logger.info("Deleting test file in case it still exists.");
        deleteTestFile(testFile);

        //Try to store a file
        logger.info("Storing & deleting test file");
        byte[] storeData = "Random test!".getBytes();
        try {
            FileCopyUtils.copy(storeData, testFile);
        } catch (IOException e) {
            logger.fatal("Failed to write test file: {}", e.getMessage(), e);
            throw new LijstrException("Failed to write test file");
        }

        deleteTestFile(testFile);
    }

    private void deleteTestFile(File testFile) {
        if (testFile.exists() && !testFile.delete()) {
            throw new LijstrException("Failed to delete test file");
        }
    }

}
