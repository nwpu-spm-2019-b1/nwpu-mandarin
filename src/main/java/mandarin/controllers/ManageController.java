package mandarin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@RequestMapping("/manage")
@Controller
public class ManageController {
    @GetMapping({"/", ""})
    public String index(Model model) {
        model.addAttribute("now", Instant.now());
        return "manage";
    }
}
