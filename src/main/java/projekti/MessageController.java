package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    @Autowired
    MessageService messageService;
    
    @Autowired
    AccountService accountService;
    
    // Lähetetään uusi viesti. Lähettäminen onnistuu vain,
    // jos kirjautuneena olevan käyttäjän username on sama kuin sivu,
    // jolle viesti yritetään lähettää.
    @PostMapping("users/{username}/messages")
    public String postMessage(@RequestParam String messageText,
                              @PathVariable String username) {
        if (accountService.currentUser().getUsername().equals(username)) {
            messageService.sendMessage(username, messageText);
        }
        return "redirect:/users/{username}";
    }
    
}
