package projekti;

import java.io.IOException;
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
    PictureService pictureService;
    
    @GetMapping(path = "/users/{username}/profilepicture", produces = "image/jpeg")
    @ResponseBody
    public byte[] getProfilePicture(@PathVariable String username) throws IOException {
        return pictureService.getProfilePicture(username);
    }
    
    @PostMapping("/users/{username}/profilepicture")
    public String addGif(@RequestParam("file") MultipartFile file,
                         @PathVariable String username) throws IOException {
        pictureService.saveProfilePicture(username, file);
        return "redirect:/users/{username}";
    }

}
