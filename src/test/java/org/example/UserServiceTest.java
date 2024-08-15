package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private Map<String, User> mockUserDatabase;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a mock for the user database map
        mockUserDatabase = Mockito.mock(Map.class);

        // Instantiate UserService using the default constructor
        userService = new UserService();

        // Use reflection to inject the mock map into the userService's private userDatabase field
        Field field = UserService.class.getDeclaredField("userDatabase");
        field.setAccessible(true); // Make the private field accessible
        field.set(userService, mockUserDatabase); // Set the mock database to the field
    }

    // --------------- Tests for registerUser() ---------------

    @Test
    public void testRegisterUser_Positive() {
        User newUser = new User("john_doe", "password123", "john@example.com");

        // Set up the mock behavior
        when(mockUserDatabase.containsKey("john_doe")).thenReturn(false);
        when(mockUserDatabase.put("john_doe", newUser)).thenReturn(null);

        boolean result = userService.registerUser(newUser);

        assertTrue(result); // Assert that the user was successfully registered
    }

    @Test
    public void testRegisterUser_Negative() {
        User existingUser = new User("john_doe", "password123", "john@example.com");

        // Simulate that the user already exists in the database
        when(mockUserDatabase.containsKey("john_doe")).thenReturn(true);

        boolean result = userService.registerUser(existingUser);

        assertFalse(result); // Assert that registration failed
    }

    // --------------- Tests for loginUser() ---------------

    @Test
    public void testLoginUser_Positive() {
        User newUser = new User("john_doe", "password123", "john@example.com");

        // Simulate the user being present in the database
        when(mockUserDatabase.get("john_doe")).thenReturn(newUser);

        User result = userService.loginUser("john_doe", "password123");

        assertNotNull(result); // Assert that the user was found
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    public void testLoginUser_Negative_WrongPassword() {
        User newUser = new User("john_doe", "password123", "john@example.com");

        // Simulate the user being present in the database with the correct credentials
        when(mockUserDatabase.get("john_doe")).thenReturn(newUser);

        User result = userService.loginUser("john_doe", "wrongpassword");

        assertNull(result); // Assert that login failed due to wrong password
    }

    // --------------- Tests for updateUserProfile() ---------------

    @Test
    public void testUpdateUserProfile_Positive() {
        User user = new User("john_doe", "password123", "john@example.com");

        // Simulate the user being present in the database
        when(mockUserDatabase.get("john_doe")).thenReturn(user);

        boolean result = userService.updateUserProfile(user, "john_doe_updated", "newpassword", "john_new@example.com");

        assertTrue(result); // Assert that the profile was updated successfully
    }

    @Test
    public void testUpdateUserProfile_Negative_UsernameTaken() {
        User user1 = new User("john_doe", "password123", "john@example.com");
        User user2 = new User("jane_doe", "password123", "jane@example.com");

        // Simulate the presence of both users in the database
        when(mockUserDatabase.get("john_doe")).thenReturn(user1);
        when(mockUserDatabase.containsKey("jane_doe")).thenReturn(true);

        boolean result = userService.updateUserProfile(user1, "jane_doe", "newpassword", "john_new@example.com");

        assertFalse(result); // Assert that the update failed due to the username being taken
    }
}
