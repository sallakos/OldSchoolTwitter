package projekti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureService {
    
    @Autowired
    UserRepository userRepo;
    
    @Autowired
    PictureRepository pictureRepo;
    
    public byte[] getProfilePicture(String username) throws IOException {
        
        User user = userRepo.findByUsername(username);

        if (user.getProfilePicture() != null) {
            return user.getProfilePicture().getPicture();
        }
       
        byte[] defaultPicture = Files.readAllBytes(Paths.get("C:\\Users\\salla\\Documents\\NetBeansProjects\\mooc-wepa-s19-projekti\\wepa_Projekti\\src\\main\\resources\\public\\img\\default.jpg"));
        
        return defaultPicture;
        
    }
    
    public void saveProfilePicture(String username, MultipartFile file) throws IOException {
        if (file.getContentType().equals("image/jpeg")) {
            User user = userRepo.findByUsername(username);
            Picture picture = new Picture(file.getBytes(), "", user, new ArrayList<>());
            user.getPictures().add(picture);
            user.setProfilePicture(user.getPictures().get(user.getPictures().size() - 1));
            pictureRepo.save(picture);
            userRepo.save(user);
        }
    }
    
}
