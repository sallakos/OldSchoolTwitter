package projekti;

import java.io.IOException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MessageController {

    @Autowired
    MessageService messageService;
    
    // Lähetetään uusi viesti.
    @PostMapping("users/{username}/messages")
    public String postMessage(@RequestParam String messageText,
                              @PathVariable String username) {
        messageService.sendMessage(username, messageText);
        return "redirect:/users/{username}";
    }

    public String home() {
        return "index";
    }
    
}
