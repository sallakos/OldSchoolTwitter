package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String newUser(@RequestParam String username,
                          @RequestParam String name) {

        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (accountService.findByUsername(username) == null) {
            return "fail";
        }
        
        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        accountService.register(username, name);
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
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("test", "Your page!");
            model.addAttribute("userFollowsWho", "Sinä seuraat");
            model.addAttribute("whoFollowsUser", "Sinun seuraajasi");
        } else {
            model.addAttribute("test", "");
            model.addAttribute("userFollowsWho", account.getName() + " seuraa");
            model.addAttribute("whoFollowsUser", "Käyttäjän " + account.getName() + " seuraajat");
        }
        return "user";
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
