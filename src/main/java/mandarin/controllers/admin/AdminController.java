package mandarin.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping({"/",""})
    public String index(Model model) {
        model.addAttribute("now", Instant.now());
        return "admin";
    }
}
