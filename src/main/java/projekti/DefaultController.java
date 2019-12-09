package projekti;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DefaultController {
    
    @Autowired
    AccountService accountService;
    
    @PostConstruct
    public void init() {
        
        AccountData user1 = new AccountData("Aku Ankka", "aku", "aku");
        AccountData user2 = new AccountData("Iines Ankka", "iinez", "iinez");
        AccountData user3 = new AccountData("Mikki Hiiri", "mikkihiiri", "mikkihiiri");
        AccountData user4 = new AccountData("Hessu Hopo", "hessuh", "hessuh");
        AccountData user5 = new AccountData("Hannu Hanhi", "hanzuu", "hanzuu");
        AccountData user6 = new AccountData("Minni Hiiri", "minnie", "minnie");
        

        if (accountService.findByUsername(user1.getUsername()) == null) {
            accountService.register(user1);
        }
        if (accountService.findByUsername(user2.getUsername()) == null) {
            accountService.register(user2);
        }
        if (accountService.findByUsername(user3.getUsername()) == null) {
            accountService.register(user3);
        }
        if (accountService.findByUsername(user4.getUsername()) == null) {
            accountService.register(user4);
        }
        if (accountService.findByUsername(user5.getUsername()) == null) {
            accountService.register(user5);
        }
        if (accountService.findByUsername(user6.getUsername()) == null) {
            accountService.register(user6);
        }
        
    }
    
}
