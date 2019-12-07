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
    AccountService accountService;
    
    @Autowired
    SecurityConfiguration securityConfiguration;

    @PostConstruct
    public void init() {
        
        AccountData user1 = new AccountData("aku", "Aku Ankka", "aku");
        AccountData user2 = new AccountData("iinez", "Iines Ankka", "iinez");

        if (accountService.findByUsername(user1.getUsername()) == null) {
            accountService.register(user1);
        }
        
        if (accountService.findByUsername(user2.getUsername()) == null) {
            accountService.register(user2);
        }
        
    }
    
}
