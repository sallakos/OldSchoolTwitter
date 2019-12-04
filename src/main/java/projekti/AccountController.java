package projekti;

import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    MessageService messageService;
    
// ---------- GETIT ---------------------------------------------------------------------------------------------------

    // Rekisteröitymissivu. Jos käyttäjä on jo kirjautunut, näytetään hänelle tämä tieto.
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("isRegistered", accountService.isRegistered());
        return "register";
    }
    
    // Haetaan henkilön tiedot.
    @GetMapping("/{username}")
    public String showUser(Model model, @PathVariable String username) {
        Account account = accountService.findByUsername(username); // SQL
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("name", account.getName());
        model.addAttribute("followers", accountService.get6Followers(account));
        model.addAttribute("followees", accountService.get6Followees(account));
        model.addAttribute("numberOfFollowers", accountService.getUserFollowers(account).size());
        model.addAttribute("numberOfFollowees", accountService.getUserFollowees(account).size());
        model.addAttribute("pictures", account.getPictures());
        model.addAttribute("profilePicture", account.getProfilePicture());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUsername", currentUser.getUsername());
        model.addAttribute("currentUserLikedMessages", currentUser.getLikedMessages());
        model.addAttribute("currentUserLikedPictures", currentUser.getLikedPictures());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("numberOfPendingRequests", accountService.numberOfPendingRequests(currentUser)); // SQL
        model.addAttribute("friendStatus", accountService.friendStatus(account)); // SQL, jos vieras sivu, niin kaksi kyselyä.
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("userFollowsWho", "Sinä seuraat");
            model.addAttribute("whoFollowsUser", "Sinun seuraajasi");
            model.addAttribute("messages", messageService.getAllMessages(account)); // SQL
        } else {
            model.addAttribute("userFollowsWho", account.getName() + " seuraa");
            model.addAttribute("whoFollowsUser", "Käyttäjän " + account.getName() + " seuraajat");
            model.addAttribute("messages", messageService.getUserMessages(account)); // SQL
        }
        System.out.println("Thymeleafin tekemiä kyselyjä:");
        return "user";
    }
    
    // Haetaan kaikki henkilöt.
    @GetMapping("/kayttajat")
    public String allUsers(Model model) {
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("users", accountService.findAll()); // SQL MIKSI 3 kyselyä????
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("followees", currentUser.getFollowees());
        model.addAttribute("title", "Käyttäjät");
        model.addAttribute("id", "search" + 0);
        return "users";
    }
    
    // Haetaan käyttäjän seuraamat henkilöt.
    @GetMapping("/{username}/seurattavat")
    public String allUserFollowees(Model model, @PathVariable String username) {
        Account currentUser = accountService.currentUser(); // SQL
        Account account = accountService.findByUsername(username);
        model.addAttribute("users", accountService.getUserFollowees(account));
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("followees", currentUser.getFollowees());
        model.addAttribute("id", "search" + account.getId());
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("title", "Käyttäjät, joita seuraat:");
        } else {
            model.addAttribute("title", "Käyttäjät, joita " + account.getName() + " seuraa:");
        }
        return "users";
    }
    
    // Haetaan henkilöt, jotka seuraavat käyttäjää.
    @GetMapping("/{username}/seuraajat")
    public String allUserFollowers(Model model, @PathVariable String username) {
        Account currentUser = accountService.currentUser(); // SQL
        Account account = accountService.findByUsername(username);
        model.addAttribute("users", accountService.getUserFollowers(account));
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("followees", currentUser.getFollowees());
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("title", "Sinun seuraajasi:");
        } else {
            model.addAttribute("title", "Käyttäjän " + account.getName() + " seuraajat:");
        }
        return "users";
    }

    // Haetaan kirjautuneen henkilön profiili kirjautumisen yhteydessä.
    @GetMapping("/user")
    public String currentUser() {
        String username = accountService.currentUser().getUsername();
        return "redirect:/" + username;
    }

// ---------- POSTIT --------------------------------------------------------------------------------------------------

    // Lähetetään rekisteröityminen. Tämän jälkeen käyttäjän tulee vielä kirjautua sisään.
    @PostMapping("/register")
    public String newUser(@RequestParam String username,
            @RequestParam String name,
            @RequestParam String password) {
        // Jos joku käyttäjä on jo kirjautunut, ei voida rekisteröityä.
        // Ohjataan käyttäjä omalle profiilisivulleen.
        // Käyttäjän ei pitäisi edes nähdä tätä sivua, mutta estetään myös POST.
        if (accountService.isRegistered()) {
            String currentUsername = accountService.currentUser().getUsername();
            return "redirect:/" + currentUsername;
        }

        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (accountService.findByUsername(username) != null) {
            return "fail";
        }

        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        accountService.register(username, name, password);
        return "redirect:/" + username;

    }
    
    // Seurataan henkilöä.
    @PostMapping("/{username}/follow")
    public String followUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.follow(whoFollows, whoToFollow, LocalDateTime.now());
        return "redirect:/" + username;
    }
    
    // Seurataan henkilöä ja pysytään omalla sivulla.
    @PostMapping("/{username}/followown")
    public String followUserStayOnOwnPage(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.follow(whoFollows, whoToFollow, LocalDateTime.now());
        return "redirect:/" + whoFollows.getUsername();
    }
    
    // Seurataan henkilöä ja pysytään käyttäjäsivulla.
    @PostMapping("/{username}/followstay")
    public String followUserStayOnPage(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.follow(whoFollows, whoToFollow, LocalDateTime.now());
        return "redirect:/kayttajat";
    }

    // Lopetetaan seuraaminen.
    @PostMapping("/{username}/unfollow")
    public String unfollowUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.unfollow(whoFollows, whoToFollow);
        return "redirect:/" + username;
    }
    
    // Lopetetaan seuraaminen ja pysytään omalla sivulla.
    @PostMapping("/{username}/unfollowown")
    public String unfollowUserStayOnOwnPage(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.unfollow(whoFollows, whoToFollow);
        return "redirect:/" + whoFollows.getUsername();
    }
    
    // Lopetetaan seuraaminen ja pysytään käyttäjäsivulla.
    @PostMapping("/{username}/unfollowstay")
    public String unfollowUserStayOnPage(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.unfollow(whoFollows, whoToFollow);
        return "redirect:/kayttajat";
    }
    
    // Hyväksytään seuraamispyyntö.
    @PostMapping("/{username}/accept/{who}")
    public String acceptFollow(@PathVariable String username,
                               @PathVariable String who) {
        Account whoFollows = accountService.findByUsername(who);
        Account whoToFollow = accountService.findByUsername(username);
        accountService.acceptFollow(whoFollows, whoToFollow);
        return "redirect:/" + username;
    }
    
    // Hylätään seuraamispyyntö.
    @PostMapping("/{username}/decline/{who}")
    public String declineFollow(@PathVariable String username,
                                @PathVariable String who) {
        Account whoFollows = accountService.findByUsername(who);
        Account whoToFollow = accountService.findByUsername(username);
        accountService.declineFollow(whoFollows, whoToFollow);
        return "redirect:/" + username;
    }

}
