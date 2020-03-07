package sheridan.gergess.quiz1.encoder;

public interface CookieEncoder {
    String decode(String value);
    String encode(String value);
}
