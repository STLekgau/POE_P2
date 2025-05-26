//Tumisho
//Lekgau
//ST10471856
package javaapplication4;

import java.util.*;
import java.io.*;
import javax.swing.*;

public class POE {

    private static final String USERS_FILE = "users.txt";
    private static final String MESSAGES_FILE = "messages.json";
    private static HashMap<String, String[]> userDatabase = new HashMap<>();

    public static void main(String[] args) {
        loadUsers();

        boolean exit = false;
        while (!exit) {
            String[] options = {"Register", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to MyApp!\nChoose an option:",
                    "MyApp",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0 ->
                    register();
                case 1 ->
                    login();
                case 2 -> {
                    exit = true;
                    JOptionPane.showMessageDialog(null, "Exiting. Have a great day!");
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
            }
        }
    }

    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userDatabase.put(parts[0], new String[]{parts[1], parts[2]});
                }
            }
        } catch (IOException e) {
            // File might not exist initially
        }
    }

    private static void saveUser(String username, String password, String cellphone) {
        try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
            writer.write(username + "," + password + "," + cellphone + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving user data: " + e.getMessage());
        }
    }

    private static void saveMessage(String sender, String recipient, String message, String code, String status) {
        try (FileWriter writer = new FileWriter(MESSAGES_FILE, true)) {
            writer.write(sender + "," + recipient + "," + message.replace(",", ";") + "," + code + "," + status + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving message: " + e.getMessage());
        }
    }

    private static void register() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField cellphoneField = new JTextField();
        Object[] message = {
            "Enter your username:", usernameField,
            "Enter your password:", passwordField,
            "Enter your cellphone number:", cellphoneField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String cellphone = cellphoneField.getText();

            if (username.length() != 5 || !username.contains("_")) {
                JOptionPane.showMessageDialog(null, "Username must be 5 characters and contain an underscore.");
            } else if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists.");
            } else if (!isPasswordValid(password)) {
                JOptionPane.showMessageDialog(null, "Password must be 8 characters, include uppercase and a special character.");
            } else if (!cellphone.matches("\\+27\\d{9}")) {
                JOptionPane.showMessageDialog(null, "Cellphone must start with +27 and have 12 digits total.");
            } else {
                userDatabase.put(username, new String[]{password, cellphone});
                saveUser(username, password, cellphone);
                JOptionPane.showMessageDialog(null, "Registration successful!");
            }
        }
    }

    private static void login() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "Enter your username:", usernameField,
            "Enter your password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userDatabase.containsKey(username)) {
                String[] credentials = userDatabase.get(username);
                if (credentials[0].equals(password)) {
                    JOptionPane.showMessageDialog(null, "Login successful. Welcome " + username + "!");
                    afterLogin(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Username not found.");
            }
        }
    }

    private static boolean isPasswordValid(String password) {
        return password.length() == 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    private static void afterLogin(String username) {
        boolean exit = false;

        markMessagesAsRead(username);
        ArrayList<String> messages = loadMessagesForUser(username);

        while (!exit) {
            String[] options = {"Send Messages", "Show Recent Messages", "Quit"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to MyApp:",
                    "Message Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0 ->
                    sendMessages(username, messages);
                case 1 ->
                    showRecentMessages(messages);
                case 2 -> {
                    exit = true;
                    JOptionPane.showMessageDialog(null, "Goodbye, " + username + "!");
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
            }
        }
    }

    private static void sendMessages(String sender, ArrayList<String> messages) {
        String countStr = JOptionPane.showInputDialog("How many messages to send?");
        int count;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number.");
            return;
        }

        String recipient = JOptionPane.showInputDialog("Enter recipient's cellphone number:");
        if (recipient == null || !recipient.matches("\\+27\\d{9}")) {
            JOptionPane.showMessageDialog(null, "Invalid cellphone number.");
            return;
        }

        for (int i = 0; i < count; i++) {
            String message = JOptionPane.showInputDialog("Enter message #" + (i + 1) + ":");
            if (message == null || message.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Message cannot be empty.");
                i--;
                continue;
            }

            while (message.length() > 250) {
                JOptionPane.showMessageDialog(null, "Message must be 250 characters or less. Please shorten your message.");
                message = JOptionPane.showInputDialog("Re-enter message #" + (i + 1) + ":");
                if (message == null || message.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Message cannot be empty.");
                }
            }

            String code = generateRandom10DigitCode();
            String fullMessage = "To: " + recipient + " - \"" + message + "\" ✓✓ #" + code;
            messages.add(fullMessage);
            saveMessage(sender, recipient, message, code, "DELIVERED");

            String hash = generateMessageHash(code, i + 1, message, recipient);
            JOptionPane.showMessageDialog(null, "Message Hash: " + hash);
        }

    }

    private static String generateMessageHash(String code, int messageNumber, String message, String recipient) {
        String[] words = message.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "null";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord.toUpperCase();
        String codePrefix = code.substring(0, 2);
        return codePrefix + ":" + messageNumber + ":" + firstWord + "_" + lastWord + "\n"+  ":" + recipient +  ":" + message.hashCode();
    }

    private static String generateRandom10DigitCode() {
        Random rand = new Random();
        long code = Math.abs(rand.nextLong() % 1_000_000_0000L);
        return String.format("%010d", code);
    }

    private static void showRecentMessages(ArrayList<String> messages) {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No recent messages.");
        } else {
            StringBuilder sb = new StringBuilder("Recent Messages:\n");
            for (int i = 0; i < messages.size(); i++) {
                sb.append(i + 1).append(". ").append(messages.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }

    private static ArrayList<String> loadMessagesForUser(String username) {
        ArrayList<String> messages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5 && parts[0].equals(username)) {
                    String recipient = parts[1];
                    String messageText = parts[2].replace(";", ",");
                    String code = parts[3];
                    String status = parts[4];

                    String ticks = switch (status) {
                        case "SENT" ->
                            "✓";
                        case "DELIVERED" ->
                            "✓✓";
                        case "READ" ->
                            "✅";
                        default ->
                            "";
                    };

                    String fullMessage = "To: " + recipient + " - \"" + messageText + "\" " + ticks + " #" + code;
                    messages.add(fullMessage);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading past messages: " + e.getMessage());
        }

        return messages;
    }

    private static void markMessagesAsRead(String username) {
        File inputFile = new File(MESSAGES_FILE);
        File tempFile = new File("messages_temp.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5) {
                    String sender = parts[0];
                    String recipient = parts[1];
                    String message = parts[2];
                    String code = parts[3];
                    String status = parts[4];

                    if (recipient.equals(username) && status.equals("DELIVERED")) {
                        status = "READ";
                    }

                    writer.write(String.join(",", sender, recipient, message, code, status) + "\n");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error updating message status: " + e.getMessage());
            return;
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            JOptionPane.showMessageDialog(null, "Failed to update message statuses.");
        }
    }
}
