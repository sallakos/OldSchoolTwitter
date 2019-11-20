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
public class DefaultController {

    @Autowired
    UserRepository userRepo;
    
    @Autowired
    MessageRepository messageRepo;

    // Haetaan henkilön tiedot.
    @GetMapping("users/{username}")
    public String showUser(Model model, @PathVariable String username) {
        User user = userRepo.findByUsername(username);
        model.addAttribute("name", user.getName());
        model.addAttribute("messages", user.getMessages());
        return "user";
    }
    
    // Lähetetään uusi viesti.
    @Transactional
    @PostMapping("users/{username}/messages")
    public String postMessage(@RequestParam String messageText,
                              @PathVariable String username) throws IOException {
        User user = userRepo.findByUsername(username);
        Message message = new Message(messageText, user);
        messageRepo.save(message);
        return "redirect:/users/{username}";
    }

//    // Haetaan gif.
//    @GetMapping(path = "/gifs/{id}/content", produces = "image/gif")
//    @ResponseBody
//    public byte[] get(@PathVariable Long id) {
//        return gifRepository.getOne(id).getContent();
//    }
//
//    // Tallennetaan uusi gif.
//    @PostMapping("/gifs")
//    public String addGif(@RequestParam("file") MultipartFile file) throws IOException {
//        if (file.getContentType().equals("image/gif")) {
//            Gif gif = new Gif();
//            gif.setContent(file.getBytes());
//            gifRepository.save(gif);
//        }
//        return "redirect:/gifs";
//    }

    public String home() {
        return "index";
    }
    
}
