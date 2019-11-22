package projekti;

import org.springframework.stereotype.Controller;

@Controller
public class DefaultController {

    public String home() {
        return "index";
    }
    
}
