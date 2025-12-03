package Student_management_system.com.studentapp.util;

public final class Validator {
    private Validator() {}

    public static void requireNonBlank(String v, String name) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(name + " cannot be blank");
    }

    public static void requireValidEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) throw new IllegalArgumentException("Invalid email");
    }
}
