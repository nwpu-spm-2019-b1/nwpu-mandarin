package mandarin.services;

import mandarin.auth.SessionHelper;
import mandarin.dao.ActionLogRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.ReservationRepository;
import mandarin.dao.UserRepository;
import mandarin.entities.ActionLogItem;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import mandarin.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private BookService bookService;
    @Resource
    private LendingLogRepository lendingLogRepository;
    @Resource
    private ReservationRepository reservationRepository;
    @Resource
    private ActionLogRepository actionLogRepository;
    @Resource
    private SessionHelper sessionHelper;
    @Resource
    private ConfigurationService configurationService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("No such user");
        }
        deleteUser(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteUser(User user) {
        if (lendingLogRepository.findOutstandingByUser(user).size() > 0) {
            throw new RuntimeException("User has outstanding borrowings");
        }
        reservationRepository.deleteAllByUser(user);
        Map<String, Object> actionInfo = new HashMap<>();
        actionInfo.put("user", user);
        actionInfo.put("lending_logs", lendingLogRepository.findAllByUser(user).stream().map((LendingLogItem item) -> ObjectUtils.copyFieldsIntoMap(item, null, "id", "startTime", "endTime", "book")).collect(Collectors.toList()));
        actionLogRepository.save(new ActionLogItem(sessionHelper.getCurrentUser(), "DeleteUser", actionInfo));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal getFineAmount(User user) {
        Instant now = Instant.now();
        return lendingLogRepository.findOutstandingByUser(user).stream().map((LendingLogItem item) -> {
            long duration = Duration.between(item.getStartTime(), now).toDays() - configurationService.getAsInt("return_period");
            if (duration <= 0) {
                return BigDecimal.ZERO;
            }
            return configurationService.getAsBigDecimal("fine_rate").multiply(BigDecimal.valueOf(duration));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
