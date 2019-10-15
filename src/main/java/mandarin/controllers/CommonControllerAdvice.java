package mandarin.controllers;

import mandarin.utils.FormatUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Instant;

@ControllerAdvice
public class CommonControllerAdvice {
    static class Formatter {
        public String format(Instant timestamp) {
            return FormatUtils.formatInstant(timestamp).orElse("-");
        }
    }

    @ModelAttribute("formatter")
    public Formatter getFormatter() {
        return new Formatter();
    }
}
