package mandarin.controllers.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.UserType;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.services.ConfigurationService;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@AuthenticationNeeded(UserType.Admin)
public class AdminAPIController {
    @Resource
    private UserRepository userRepository;

    @Resource
    private ConfigurationService configurationService;

    @ResponseBody
    @PostMapping("/profile/change/password")
    public ResponseEntity passwordChange(@RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         HttpSession session) {
        User user = userRepository.findById((Integer) session.getAttribute("userId")).orElse(null);
        if (user == null) {
            throw new APIException("No such user");
        }
        if (!CryptoUtils.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new APIException("Old password incorrect");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        session.invalidate();
        return ResponseEntity.ok().body(BasicResponse.ok().message("Password changed successfully"));
    }

    @ResponseBody
    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("email") String email) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            throw new APIException("Username already existed");
        }
        User newUser = new User(username, password, UserType.Librarian);
        newUser.setEmail(email);
        userRepository.save(newUser);
        return ResponseEntity.ok(BasicResponse.ok().message("Registered librarian successfully"));
    }

    @ResponseBody
    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteLibrarian(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new APIException("No such user");
        }
        if (!user.getType().equals(UserType.Librarian)) {
            throw new APIException("User not a librarian");
        }
        userRepository.delete(user);
        return ResponseEntity.ok(BasicResponse.ok());
    }

    @PutMapping(value = "/user/{userId}", consumes = "application/json")
    public ResponseEntity updateUser(@PathVariable Integer userId,
                                     @RequestBody Map<String, Object> body) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new APIException("No such user");
        }
        user.setUsername((String) body.get("username"));
        user.setEmail((String) body.get("email"));
        userRepository.save(user);
        return ResponseEntity.ok(BasicResponse.ok().message("Changed user info successfully"));
    }

    static class SettingsUpdateRequest {
        @JsonProperty("return_period")
        public Integer returnPeriod;
        @JsonProperty("fine_rate")
        public BigDecimal fineRate;
        @JsonProperty("reader_deposit")
        public BigDecimal readerDeposit;
    }

    @PutMapping(value = "/settings", consumes = "application/json")
    public ResponseEntity updateSettings(@RequestBody SettingsUpdateRequest request) {
        configurationService.setReturnPeriod(request.returnPeriod);
        configurationService.setFineRate(request.fineRate);
        configurationService.setReaderDeposit(request.readerDeposit);
        return ResponseEntity.ok(BasicResponse.ok().message("Changed settings successfully"));
    }

    @GetMapping(value = "/users/search")
    public ResponseEntity searchUsers(@RequestParam String type, @RequestParam String keyword) {
        Predicate<User> predicate = null;
        switch (type) {
            case "id":
                int id;
                try {
                    id = Integer.parseInt(keyword);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(BasicResponse.fail().message("Invalid ID"));
                }
                predicate = user -> user.getId().equals(id);
                break;
            case "username":
                predicate = user -> user.getUsername().contains(keyword);
                break;
            default:
                throw new APIException("Invalid search type");
        }
        return ResponseEntity.ok(BasicResponse.ok().data(userRepository.findAllByType(UserType.Librarian).stream().sorted(Comparator.comparing(User::getId)).filter(predicate).collect(Collectors.toList())));
    }
}
