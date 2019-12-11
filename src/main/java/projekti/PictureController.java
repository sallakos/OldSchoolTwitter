package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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

// ---------- GETIT ---------------------------------------------------------------------------------------------------
    
    // Haetaan henkilön kuvagalleria.
    @GetMapping("/{username}/kuvat")
    public String showUserGallery(Model model, @PathVariable String username) {
        Account account = accountService.findByUsername(username); // SQL
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("currentUsername", currentUser.getUsername());
        int friendStatus = accountService.friendStatus(account); // SQL, jos vieras sivu, niin kaksi kyselyä.
        if (friendStatus >= 1) {
            model.addAttribute("name", account.getName());
            model.addAttribute("pictures", pictureService.getPictures(account)); // SQL
            model.addAttribute("profilePictureId", account.getProfilePicture() != null ? account.getProfilePicture().getId() : (Long) 0L);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentUserLikedPictures", currentUser.getLikedPictures());
            model.addAttribute("friendStatus", friendStatus);
            if (friendStatus == 1) {
                model.addAttribute("galleryTitle", "Käyttäjän " + account.getName() + " kuvat");
            } else {
                model.addAttribute("galleryTitle", "Sinun kuvasi");
            } 
            System.out.println("THYMELEAFIN TEKEMIÄ KYSELYJÄ:");
            return "galleria";
        }
        return "forbidden";
    }

    // Haetaan käyttäjän profiilikuva.
    @GetMapping(path = "/{username}/kuvat/profiilikuva", produces = "image/*")
    @ResponseBody
    public byte[] getProfilePicture(@PathVariable String username) throws IOException {
//        System.out.println("SQL / KÄYTTÄJÄN " + username + " PROFIILIKUVA: ");
        Account account = accountService.findByUsername(username); // SQL
        return pictureService.getPicture(account.getProfilePicture().getId());
    }

    // Haetaan käyttäjän yksittäinen kuva. Vain seuraajat voivat nähdä kuvat.
    @GetMapping(path = "/{username}/kuvat/{id}", produces = "image/*")
    @ResponseBody
    public byte[] getPicture(@PathVariable String username,
                             @PathVariable Long id) throws IOException {
//        System.out.println("SQL / KÄYTTÄJÄN " + username + " KUVA: --------------------------------------------------------- \n" + 
//                            "   (vaatii kolme kyselyä: kenen sivulla ollaan, saako käyttäjä nähdä kuvan sekä itse kuvan lataus)");
        Account account = accountService.findByUsername(username); // SQL
        boolean isAllowed = accountService.friendStatus(account) >= 1;
        if (isAllowed) {
            return pictureService.getPicture(id);
        }
        return null;
    }

// ---------- POSTIT JA DELETE ---------------------------------------------------------------------------------------------    
    
    // Lisätään profiilikuva. Sallitaan POST vain, jos käyttäjä on omalla sivullaan.
    @PostMapping("/{username}/kuvat/profiilikuva")
    public String addProfilePicture(@RequestParam Long profilePictureId,
                                    @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.saveProfilePicture(username, profilePictureId);
        }
        return "redirect:/{username}";
    }

    // Lisätään kuva. Sallitaan POST vain, jos käyttäjä on omalla sivullaan.
    @PostMapping("/{username}/kuvat")
    public String addPicture(@RequestParam("file") MultipartFile file,
                             @RequestParam String description,
                             @PathVariable String username) throws IOException {
        if (accountService.isCurrentUser(username)) {
            pictureService.savePicture(username, file, description);
        }
        return "redirect:/{username}/kuvat";
    }
    
    // Poistetaan kuva. Sallitaan POST vain, jos käyttäjä on omalla sivullaan.
    @DeleteMapping("/{username}/kuvat/{deleteId}")
    public String deletePicture(@PathVariable String username,
                                @PathVariable Long deleteId) {
        if (accountService.isCurrentUser(username)) {
            pictureService.deletePicture(deleteId);
        }
        return "redirect:/{username}/kuvat";
    }

    // Tykätään kuvasta. Tykkääminen onnistuu vain, jos ei yritetä tykätä omasta
    // tai ei seurattavan kuvasta.
    @PostMapping("/{username}/kuvat/{pictureId}/like")
    public String likeAPicture(@PathVariable Long pictureId,
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
    @PostMapping("/{username}/kuvat/{pictureId}/comment")
    public String commentAPicture(@PathVariable Long pictureId,
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
