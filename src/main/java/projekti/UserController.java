package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepo;
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @PostMapping("/register")
    public String newUser(@RequestParam String username,
                          @RequestParam String name) {
        
        // Jos käyttäjänimi on jo käytössä, ei voida lisätä uutta käyttäjää.
        // Toistaiseksi palautetaan virhesivu, lopuksi tehdään jotain muuta.
        if (userRepo.findByUsername(username) != null) {
            return "fail";
        }
        
        // Jos käyttäjänimeä ei löydy, lisätään uusi käyttäjä ja ohjataan
        // käyttäjä omalle profiilisivulleen.
        User user = new User(username, name);
        userRepo.save(user);
        return "redirect:/users/" + username;
        
    }
    
}
