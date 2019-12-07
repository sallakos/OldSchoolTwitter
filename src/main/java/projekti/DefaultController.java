package projekti;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    
    @Autowired
    AccountRepository accountRepo;
    
    @Autowired
    SecurityConfiguration securityConfiguration;

//    @GetMapping("/")
//    public String home(Model model) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        model.addAttribute("username", username);
//        return "index";
//    }
    
//    @GetMapping("/login")
//    public String redirect(@RequestParam logout) {
//        return "redirect:/";
//    }
    
//    @PostConstruct
//    public void init() {
//        
//        Account user1 = new Account("user1", "User 1", securityConfiguration.passwordEncoder().encode("salasana"));
//        Account user2 = new Account("user2", "User 2", securityConfiguration.passwordEncoder().encode("salasana"));
//
//        if (accountRepo.findByUsername(user1.getUsername()) == null) {
//            accountRepo.save(user1);
//        }
//        
//        if (accountRepo.findByUsername(user2.getUsername()) == null) {
//            accountRepo.save(user2);
//        }
//        
//    }
    
}
