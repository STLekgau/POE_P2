

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

public class POETest {

    @Test
    public void testIsPasswordValid() {
        // Valid password
        assertTrue(POE.isPasswordValid("Abcdef@1"));

        // Too short
        assertFalse(POE.isPasswordValid("A@1"));

        // Missing uppercase
        assertFalse(POE.isPasswordValid("abcdef@1"));

        // Missing special character
        assertFalse(POE.isPasswordValid("Abcdef12"));

        // Incorrect length
        assertFalse(POE.isPasswordValid("Abcdefghij@1"));
    }

    @Test
    public void testRegisterUser() {
        // Simulate userDatabase
        HashMap<String, String[]> userDatabase = new HashMap<>();

        // Add test data
        String username = "user_1";
        String password = "Password1@";
        String cellphone = "1234567890";

        // Registration logic
        if (username.length() == 5 && username.contains("_") &&
            POE.isPasswordValid(password) &&
            cellphone.length() == 10 && cellphone.matches("\\d+")) {

            userDatabase.put(username, new String[]{password, cellphone});
        }

        // Test if user is registered
        assertTrue(userDatabase.containsKey(username));
        assertArrayEquals(new String[]{password, cellphone}, userDatabase.get(username));
    }
}
