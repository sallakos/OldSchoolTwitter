package projekti;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    AccountService accountService;
    
    // Rekisteröitymissivu. Jos käyttäjä on jo kirjautunut, näytetään hänelle tämä tieto.
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("isRegistered", accountService.isRegistered());
        return "register";
    }
    
    @PostMapping("/register")
    public String newUser(@RequestParam String username,
                          @RequestParam String name,
                          @RequestParam String password) {
        // Jos joku käyttäjä on jo kirjautunut, ei voida rekisteröityä.
        // Ohjataan käyttäjä omalle profiilisivulleen.
        // Käyttäjän ei pitäisi edes nähdä tätä sivua, mutta estetään myös POST.
        if (accountService.isRegistered()) {
            String currentUsername = accountService.currentUser().getUsername();
            return "redirect:/users/" + currentUsername;
        }
        
        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (accountService.findByUsername(username) != null) {
            return "fail";
        }
        
        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        accountService.register(username, name, password);
        return "redirect:/users/" + username;

    }
    
    // Haetaan henkilön tiedot.
    @GetMapping("/users/{username}")
    public String showUser(Model model, @PathVariable String username) {
        Account account = accountService.findByUsername(username);
        model.addAttribute("name", account.getName());
        model.addAttribute("messages", account.getMessages());
        model.addAttribute("followers", account.getFollowers());
        model.addAttribute("followees", account.getFollowees());
        model.addAttribute("pictures", account.getPictures());
        model.addAttribute("currentUsername", accountService.currentUser().getUsername());
        model.addAttribute("isFriend", accountService.isFriend(username));
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("userFollowsWho", "Sinä seuraat");
            model.addAttribute("whoFollowsUser", "Sinun seuraajasi");
            model.addAttribute("isOwnProfile", true);
        } else {
            model.addAttribute("userFollowsWho", account.getName() + " seuraa");
            model.addAttribute("whoFollowsUser", "Käyttäjän " + account.getName() + " seuraajat");
            model.addAttribute("isOwnProfile", false);
        }
        return "user";
    }
    
    // Seurataan henkilöä.
    @PostMapping("/users/{username}/follow")
    public String followUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.follow(whoFollows, whoToFollow, LocalDateTime.now());
        return "redirect:/users/" + username;
    }
    
    // Lopetetaan seuraaminen.
    @PostMapping("/users/{username}/unfollow")
    public String unfollowUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.unfollow(whoFollows, whoToFollow);
        return "redirect:/users/" + username;
    }
    
    // Haetaan kaikki henkilöt.
    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("users", accountService.findAll());
        return "users";
    }
    
    // Haetaan kirjautuneen henkilön profiili kirjautumisen yhteydessä.
    @GetMapping("/users/user")
    public String currentUser() {
        String username = accountService.currentUser().getUsername();
        return "redirect:/users/" + username;
    }

}
