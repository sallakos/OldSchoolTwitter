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
    AccountRepository accountRepo;
    
    @Autowired
    PictureRepository pictureRepo;
    
    public byte[] getProfilePicture(String username) throws IOException {
        Account account = accountRepo.findByUsername(username);
        if (account.getProfilePicture() != null) {
            return account.getProfilePicture().getPicture();
        }
        byte[] defaultPicture = Files.readAllBytes(Paths.get("C:\\Users\\salla\\Documents\\NetBeansProjects\\mooc-wepa-s19-projekti\\wepa_Projekti\\src\\main\\resources\\public\\img\\default.jpg"));
        return defaultPicture;
    }
    
    public void saveProfilePicture(String username, MultipartFile file) throws IOException {
        if (file.getContentType().equals("image/jpeg")) {
            Account account = accountRepo.findByUsername(username);
            Picture picture = new Picture(file.getBytes(), "", account, new ArrayList<>());
            account.getPictures().add(picture);
            account.setProfilePicture(account.getPictures().get(account.getPictures().size() - 1));
            pictureRepo.save(picture);
            accountRepo.save(account);
        }
    }
    
    public byte[] getPicture(Long id) throws IOException {
        Picture picture = pictureRepo.getOne(id);
        return picture.getPicture();
    }
    
}
