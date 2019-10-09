package mandarin.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FormatUtils {
    public static Optional<String> formatInstant(Instant instant) {
        if (instant == null) {
            return Optional.empty();
        }
        return Optional.of(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withZone(ZoneId.systemDefault()).format(instant));
    }
}
