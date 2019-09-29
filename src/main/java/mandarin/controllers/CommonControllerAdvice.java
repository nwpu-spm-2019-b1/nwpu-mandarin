package mandarin.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class CommonControllerAdvice {
    static class Formatter {
        public String format(Instant timestamp) {
            if (timestamp == null) {
                return "-";
            }
            return DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withZone(ZoneId.systemDefault()).format(timestamp);
        }
    }

    @ModelAttribute("formatter")
    public Formatter getFormatter() {
        return new Formatter();
    }
}
