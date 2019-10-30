package mandarin.utils;

import mandarin.entities.LendingLogItem;
import mandarin.services.BookService;
import mandarin.services.ConfigurationService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
public class PollingThread extends Thread {
    private final long interval = Duration.ofHours(8).toMillis();
    private Set<Runnable> callbacks = new HashSet<>();
    private BookService bookService;
    private ConfigurationService configurationService;

    public PollingThread(BookService bookService, ConfigurationService configurationService) {
        super();
        this.bookService = bookService;
        this.configurationService = configurationService;
        this.registerAllCallbacks();
    }

    public void registerAllCallbacks() {
        this.registerCallback(() -> {
            for (LendingLogItem item : bookService.getAllOverdueLogs()) {
                Instant dueTime = item.getStartTime().plus(Duration.ofDays(configurationService.getAsInt("return_period")));
                MiscUtils.sendMail(item.getUser().getEmail(), String.format("Book \"%s\" overdue since %s", item.getBook().getTitle(), FormatUtils.formatInstant(dueTime).orElseThrow(() -> new RuntimeException("Error unwrapping"))), "Please return the book as soon as possible, or you will be fined.");
            }
        });
    }

    public void registerCallback(Runnable runnable) {
        this.callbacks.add(runnable);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                for (Runnable runnable : callbacks) {
                    runnable.run();
                }
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
