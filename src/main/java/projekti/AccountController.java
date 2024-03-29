package projekti;

/**
 * Käyttäjään liittyvät get- ja post-pyynnöt.
 * 
 * @author Salla Koskinen
 */
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    MessageService messageService;

    @Autowired
    PictureService pictureService;
    
    @Autowired
    MessageRepository messageRepo;

    @ModelAttribute
    private AccountData getAccountData() {
        return new AccountData();
    }

// ---------- GETIT ---------------------------------------------------------------------------------------------------
    // Ohjataan kirjautumissivulle.
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Rekisteröitymissivu. Jos käyttäjä on jo kirjautunut, näytetään hänelle tämä tieto.
    @GetMapping("/rekisteroidy")
    public String register(Model model) {
        
        if (accountService.isRegistered()) {
            return "redirect:/" + accountService.currentUser().getUsername();
        }
        
        model.addAttribute("isRegistered", accountService.isRegistered());
        model.addAttribute("uniqueUsername", true);
        return "register";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // Haetaan henkilön tiedot.
    @GetMapping("/{username}")
    public String showUser(Model model, @PathVariable String username) {
        Account account = accountService.findByUsername(username); // SQL
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("account", account);
        model.addAttribute("name", account.getName());
        model.addAttribute("profilePicture", account.getProfilePicture());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUsername", currentUser.getUsername());
        model.addAttribute("friendStatus", accountService.friendStatus(account)); // SQL, jos vieras sivu, niin kaksi kyselyä.
        model.addAttribute("followers", accountService.get6Followers(account)); // SQL
        model.addAttribute("followees", accountService.get6Followees(account)); // SQL
        model.addAttribute("numberOfFollowers", accountService.getUserFollowers(account).size()); // SQL
        model.addAttribute("numberOfFollowees", accountService.getUserFollowees(account).size()); // SQL
        model.addAttribute("currentUserLikedMessages", currentUser.getLikedMessages());
        model.addAttribute("currentUserLikedPictures", currentUser.getLikedPictures());
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("userFollowsWho", "Sinä seuraat");
            model.addAttribute("whoFollowsUser", "Sinun seuraajasi");
            model.addAttribute("pendingRequests", accountService.findPendingRequests(currentUser)); // SQL
            model.addAttribute("numberOfPendingRequests", accountService.numberOfPendingRequests(currentUser)); // SQL
            model.addAttribute("messages", messageService.getAllMessages(account)); // SQL
        } else {
            model.addAttribute("userFollowsWho", account.getName() + " seuraa");
            model.addAttribute("whoFollowsUser", "Käyttäjän " + account.getName() + " seuraajat");
            model.addAttribute("messages", messageService.getUserMessages(account)); // SQL
        }
        model.addAttribute("pictures", accountService.findUserPictureIds(account)); // SQL
        System.out.println("THYMELEAFIN TEKEMIÄ KYSELYJÄ:");
        // Thymeleaf hakee jokaisen viestin kommentit ja tykkäykset erikseen. Tämän lisäksi haetaan aina käyttäjän profiilikuva tarvittaessa
        // sekä kyseisen käyttäjän kuvat. Omalla sivulla jokainen kuva (ei profiilikuva) aiheuttaa kaksi kyselyä, koska tarkistetaan, onko
        // käyttäjällä oikeus nähdä kuva.
        return "user";
    }

    // Haetaan kaikki henkilöt.
    @GetMapping("/kayttajat")
    public String allUsers(Model model) {
        model.addAttribute("whatToShow", 0);
        Account currentUser = accountService.currentUser(); // SQL
        model.addAttribute("users", accountService.findAll()); // SQL
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("followees", currentUser.getFollowees());
        model.addAttribute("title", "Käyttäjät");
        model.addAttribute("id", "search");
        // Näiden lisäksi Thymeleaf hakee aina kunkin käyttäjän profiilikuvan erikseen.
        System.out.println("THYMELEAFIN TEKEMIÄ KYSELYJÄ:");
        return "users";
    }

    // Haetaan käyttäjän seuraamat henkilöt.
    @GetMapping("/{username}/seurattavat")
    public String allUserFollowees(Model model, @PathVariable String username) {
        model.addAttribute("whatToShow", 1);
        Account currentUser = accountService.currentUser(); // SQL
        Account account = accountService.findByUsername(username); // SQL
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("follows", accountService.getAllFollowees(account)); // SQL
        model.addAttribute("id", "search" + account.getId() + "1");
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("title", "Käyttäjät, joita seuraat:");
        } else {
            model.addAttribute("title", "Käyttäjät, joita " + account.getName() + " seuraa:");
        }
        // Näiden lisäksi SQL hakee aina kunkin käyttäjän profiilikuvan erikseen.
        System.out.println("THYMELEAFIN TEKEMIÄ KYSELYJÄ:");
        return "users";
    }

    // Haetaan henkilöt, jotka seuraavat käyttäjää.
    @GetMapping("/{username}/seuraajat")
    public String allUserFollowers(Model model, @PathVariable String username) {
        model.addAttribute("whatToShow", 2);
        Account currentUser = accountService.currentUser(); // SQL
        Account account = accountService.findByUsername(username); // SQL
        model.addAttribute("currentUser", currentUser.getUsername());
        model.addAttribute("followedUsernames", accountService.getFollowedUsernames(currentUser)); // SQL
        model.addAttribute("follows", accountService.getAllFollowers(account)); // SQL
        model.addAttribute("id", "search" + account.getId() + "2");
        if (accountService.isCurrentUser(username)) {
            model.addAttribute("title", "Sinun seuraajasi:");
        } else {
            model.addAttribute("title", "Käyttäjän " + account.getName() + " seuraajat:");
        }
        // Näiden lisäksi SQL hakee aina kunkin käyttäjän profiilikuvan erikseen.
        System.out.println("THYMELEAFIN TEKEMIÄ KYSELYJÄ:");
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
    @PostMapping("/rekisteroidy")
    public String register(@Valid @ModelAttribute AccountData accountData,
            BindingResult bindingResult,
            Model model) {

        // Jos joku käyttäjä on jo kirjautunut, ei voida rekisteröityä.
        // Ohjataan käyttäjä omalle profiilisivulleen.
        // Käyttäjän ei pitäisi edes nähdä tätä sivua, mutta estetään myös POST.
        if (accountService.isRegistered()) {
            String currentUsername = accountService.currentUser().getUsername();
            return "redirect:/" + currentUsername;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isRegistered", accountService.isRegistered());
            model.addAttribute("uniqueUsername", accountService.checkUniqueUsername(accountData.getUsername()));
            return "register";
        }

        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (!accountService.checkUniqueUsername(accountData.getUsername())) {
            model.addAttribute("isRegistered", accountService.isRegistered());
            model.addAttribute("uniqueUsername", false);
            return "register";
        }

        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        accountService.register(accountData);
        return "redirect:/user";

    }

    // Seurataan henkilöä.
    @PostMapping("/{username}/follow")
    public String followUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.follow(whoFollows, whoToFollow, LocalDateTime.now());
        return "redirect:/" + username;
    }

    // Lopetetaan seuraaminen.
    @PostMapping("/{username}/unfollow")
    public String unfollowUser(@PathVariable String username) {
        Account whoFollows = accountService.currentUser();
        Account whoToFollow = accountService.findByUsername(username);
        accountService.unfollow(whoFollows, whoToFollow);
        return "redirect:/" + username;
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
