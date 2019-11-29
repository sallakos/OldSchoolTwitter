package projekti;

import java.time.LocalDateTime;
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
    @PostMapping("/{username}/messages")
    public String postMessage(@RequestParam String messageText,
                              @PathVariable String username) {
        if (accountService.currentUser().getUsername().equals(username)) {
            messageService.sendMessage(username, messageText);
        }
        return "redirect:/{username}";
    }
    
    // Tykätään viestistä. Tykkääminen onnistuu vain, jos ei yritetä tykätä omasta
    // tai ei seurattavan viestistä.
    @PostMapping("/{username}/messages/like")
    public String likeAMessage(@RequestParam Long messageId,
                               @PathVariable String username) {
        if (!accountService.currentUser().getUsername().equals(username) && accountService.isFriend(username)) {
            if (accountService.currentUser().getLikedMessages().contains(messageService.findById(messageId))) {
                messageService.unlikeAMessage(accountService.currentUser().getUsername(), messageId);
            } else {
                messageService.likeAMessage(accountService.currentUser().getUsername(), messageId);
            }
        }
        return "redirect:/{username}";
    }
    
    // Kommentoidaan viestiä. Vain omaa tai kavereiden viestiä voi kommentoida.
    @PostMapping("/{username}/messages/comment")
    public String commentAMessage(@RequestParam Long messageId,
                                  @RequestParam String commentText,
                                  @PathVariable String username) {
        if (accountService.currentUser().getUsername().equals(username) || accountService.isFriend(username)) {
            Comment comment = new Comment(commentText, LocalDateTime.now(), accountService.currentUser());
            messageService.commentAMessage(comment, messageId);
        }
        return "redirect:/{username}";
    }
    
}
