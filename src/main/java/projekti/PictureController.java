package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
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
    @GetMapping(path = "/{username}/profilepicture", produces = "image/jpeg")
    @ResponseBody
    public byte[] getProfilePicture(@PathVariable String username) throws IOException {
        return pictureService.getProfilePicture(username);
    }
    
    // Lisätään profiilikuva. Sallitaan POST vain, jos käyttäjä on kirjautunut sisään.
    @PostMapping("/{username}/profilepicture")
    public String addProfilePicture(@RequestParam Long profilePictureId,
                                    @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.saveProfilePicture(username, profilePictureId);
        }
        return "redirect:/{username}";
    }
    
    // Haetaan käyttäjän yksittäinen kuva.
    @GetMapping(path = "/{username}/pictures/{id}", produces = "image/jpeg")
    @ResponseBody
    public byte[] getPicture(@PathVariable Long id) throws IOException {
        return pictureService.getPicture(id);
    }
    
    // Lisätään profiilikuva. Sallitaan POST vain, jos käyttäjä on kirjautunut sisään.
    @PostMapping("/{username}/pictures")
    public String addPicture(@RequestParam("file") MultipartFile file,
                             @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.savePicture(username, file);
        }
        return "redirect:/{username}";
    }
    
    // Tykätään kuvasta. Tykkääminen onnistuu vain, jos ei yritetä tykätä omasta
    // tai ei seurattavan kuvasta.
    @PostMapping("/{username}/pictures/like")
    public String likeAPicture(@RequestParam Long pictureId,
                               @PathVariable String username) {
        if (!accountService.currentUser().getUsername().equals(username) && accountService.isFriend(username)) {
            pictureService.likeAPicture(accountService.currentUser().getUsername(), pictureId);
        }
        return "redirect:/{username}";
    }
    
    // Kommentoidaan kuvaa. Vain omaa tai kavereiden kuvaa voi kommentoida.
    @PostMapping("/{username}/pictures/comment")
    public String commentAPicture(@RequestParam Long pictureId,
                                  @RequestParam String commentText,
                                  @PathVariable String username) {
        if (accountService.currentUser().getUsername().equals(username) || accountService.isFriend(username)) {
            Comment comment = new Comment(commentText, LocalDateTime.now(), accountService.currentUser());
            pictureService.commentAPicture(comment, pictureId);
        }
        return "redirect:/{username}";
    }

}
