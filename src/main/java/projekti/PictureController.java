package projekti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // Haetaan henkilön kuvagalleria.
    @GetMapping("/{username}/kuvat")
    public String showUserGallery(Model model, @PathVariable String username) {
        Account account = accountService.findByUsername(username); // SQL
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("currentUsername", currentUser.getUsername());
        boolean isAllowed = accountService.friendStatus(account) >= 1;
        if (isAllowed) {
            model.addAttribute("name", account.getName());
            model.addAttribute("pictures", account.getPictures());
            model.addAttribute("profilePictureId", account.getProfilePicture() != null ? account.getProfilePicture().getId() : (Long) 0L);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentUserLikedMessages", currentUser.getLikedMessages());
            model.addAttribute("currentUserLikedPictures", currentUser.getLikedPictures());
            model.addAttribute("friendStatus", accountService.friendStatus(account)); // SQL, jos vieras sivu, niin kaksi kyselyä.
            return "galleria";
        }
        return "forbidden";
    }

    // Haetaan käyttäjän profiilikuva.
    @GetMapping(path = "/{username}/kuvat/profiilikuva", produces = "image/*")
    @ResponseBody
    public byte[] getProfilePicture(@PathVariable String username) throws IOException {
        Account account = accountService.findByUsername(username); // SQL
        return pictureService.getPicture(account.getProfilePicture().getId());
    }

    // Haetaan käyttäjän yksittäinen kuva. Vain seuraajat voivat nähdä kuvat.
    @GetMapping(path = "/{username}/kuvat/{id}", produces = "image/*")
    @ResponseBody
    public byte[] getPicture(@PathVariable String username,
                             @PathVariable Long id) throws IOException {
        Account account = accountService.findByUsername(username); // SQL
        boolean isAllowed = accountService.friendStatus(account) >= 1;
        if (isAllowed) {
            return pictureService.getPicture(id);
        }
        byte[] forbiddenPicture = Files.readAllBytes(Paths.get("C:\\Users\\salla\\Documents\\NetBeansProjects\\mooc-wepa-s19-projekti\\wepa_Projekti\\src\\main\\resources\\public\\img\\forbidden.jpg"));
        return forbiddenPicture;
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

    // Lisätään kuva. Sallitaan POST vain, jos käyttäjä on kirjautunut sisään.
    @PostMapping("/{username}/kuvat")
    public String addPicture(@RequestParam("file") MultipartFile file,
            @RequestParam String description,
            @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.savePicture(username, file, description);
        }
        return "redirect:/{username}/kuvat";
    }

    // Tykätään kuvasta. Tykkääminen onnistuu vain, jos ei yritetä tykätä omasta
    // tai ei seurattavan kuvasta.
    @PostMapping("/{username}/kuvat/like")
    public String likeAPicture(@RequestParam Long pictureId,
            @PathVariable String username) {
        Account account = accountService.findByUsername(username);
        if (accountService.friendStatus(account) == 1) {
            if (accountService.currentUser().getLikedPictures().contains(pictureService.findById(pictureId))) {
                pictureService.unlikeAPicture(accountService.currentUser().getUsername(), pictureId);
            } else {
                pictureService.likeAPicture(accountService.currentUser().getUsername(), pictureId);
            }
        }
        return "redirect:/{username}/kuvat";
    }

    // Kommentoidaan kuvaa. Vain omaa tai kavereiden kuvaa voi kommentoida.
    @PostMapping("/{username}/kuvat/comment")
    public String commentAPicture(@RequestParam Long pictureId,
            @RequestParam String commentText,
            @PathVariable String username) {
        Account account = accountService.findByUsername(username);
        if (accountService.friendStatus(account) >= 1) {
            Comment comment = new Comment(commentText, LocalDateTime.now(), accountService.currentUser());
            pictureService.commentAPicture(comment, pictureId);
        }
        return "redirect:/{username}/kuvat";
    }

}
