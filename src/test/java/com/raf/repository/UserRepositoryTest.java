package com.raf.repository;

import com.raf.entity.User;
import com.raf.enums.UserStatus;
import com.raf.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.raf.entity.Location;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindUserByEmail() {
        // Arrange
        Location location = new Location();
        location.setCode("LOC-1");
        location.setProvince("Kigali");
        location.setDistrict("Gasabo");
        location.setSector("Kimironko");
        location.setCell("Kibagabaga");
        location.setVillage("Kibagabaga");
        location = entityManager.persistAndFlush(location);

        User user = new User();
        user.setUserCode("USR-1234");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("securePassword");
        user.setUserType(UserType.FARMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setLocation(location);

        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
        assertEquals("USR-1234", foundUser.get().getUserCode());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent());
    }
}
