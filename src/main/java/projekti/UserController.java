package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String newUser(@RequestParam String username,
                          @RequestParam String name) {

        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (userService.findByUsername(username) == null) {
            return "fail";
        }
        
        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        userService.register(username, name);
        return "redirect:/users/" + username;

    }
    
    // Haetaan henkilön tiedot.
    @GetMapping("/users/{username}")
    public String showUser(Model model, @PathVariable String username) {
        User user = userService.findByUsername(username);
        model.addAttribute("name", user.getName());
        model.addAttribute("messages", user.getMessages());
        model.addAttribute("followers", user.getFollowers());
        model.addAttribute("followees", user.getFollowees());
        return "user";
    }
    
    // Haetaan kaikki henkilöt.
    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

}
