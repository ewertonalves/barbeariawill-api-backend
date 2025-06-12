import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "teste";
        String encodedPassword = encoder.encode(plainPassword);
        System.out.println("Encoded password for \"teste\": " + encodedPassword);
    }
}
