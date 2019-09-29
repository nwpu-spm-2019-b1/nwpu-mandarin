package mandarin.controllers.api;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.UserType;
import mandarin.dao.UserRepository;
import mandarin.entities.User;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.CryptoUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@AuthenticationNeeded(UserType.Admin)
public class AdminAPIController {
    @Resource
    private UserRepository userRepository;

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
                                   @RequestParam("password") String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw new APIException("Username already existed");
        }
        User newUser = new User(username, password, UserType.Librarian);
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
        userRepository.save(user);
        return ResponseEntity.ok(BasicResponse.ok().message("Changed username successfully"));
    }
}
