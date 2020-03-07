package sheridan.gergess.quiz1.encoder;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Component
public class CookieUrlEncoder implements CookieEncoder {

    @Override
    public String decode(String value){
        try {
            return URLDecoder.decode(value,java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException: " + e.getMessage());
        }
    }

    @Override
    public String encode(String value){
        try {
            return URLEncoder.encode(value,java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException: " + e.getMessage());
        }
    }
}
