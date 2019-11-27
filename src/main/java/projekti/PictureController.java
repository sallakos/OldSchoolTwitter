package projekti;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PictureController {

     @Autowired
     AccountService accountService;
    
    @Autowired
    PictureService pictureService;
    
    // Haetaan käyttäjän profiilikuva.
    @GetMapping(path = "/users/{username}/profilepicture", produces = "image/jpeg")
    @ResponseBody
    public byte[] getProfilePicture(@PathVariable String username) throws IOException {
        return pictureService.getProfilePicture(username);
    }
    
    // Lisätään profiilikuva. Sallitaan POST vain, jos käyttäjä on kirjautunut sisään.
    @PostMapping("/users/{username}/profilepicture")
    public String addProfilePicture(@RequestParam("file") MultipartFile file,
                                    @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.saveProfilePicture(username, file);
        }
        return "redirect:/users/{username}";
    }
    
    // Haetaan käyttäjän yksittäinen kuva.
    @GetMapping(path = "/pictures/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] getPicture(@PathVariable Long id) throws IOException {
        return pictureService.getPicture(id);
    }

}
