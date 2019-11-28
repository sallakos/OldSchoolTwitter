package projekti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureService {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    PictureRepository pictureRepo;
    
    @Autowired
    CommentRepository commentRepo;

    public byte[] getProfilePicture(String username) throws IOException {
        Account account = accountRepo.findByUsername(username);
        if (account.getProfilePicture() != null) {
            return account.getProfilePicture().getPicture();
        }
        byte[] defaultPicture = Files.readAllBytes(Paths.get("C:\\Users\\salla\\Documents\\NetBeansProjects\\mooc-wepa-s19-projekti\\wepa_Projekti\\src\\main\\resources\\public\\img\\default.jpg"));
        return defaultPicture;
    }

//    public void saveProfilePicture(String username, MultipartFile file) throws IOException {
//        if (file.getContentType().equals("image/jpeg")) {
//            Account account = accountRepo.findByUsername(username);
//            Picture picture = new Picture(file.getBytes(), "", account);
//            account.getPictures().add(picture);
//            account.setProfilePicture(account.getPictures().get(account.getPictures().size() - 1));
//            pictureRepo.save(picture);
//            accountRepo.save(account);
//        }
//    }
    public void saveProfilePicture(String username, Long id) throws IOException {
        Account account = accountRepo.findByUsername(username);
        Picture picture = pictureRepo.getOne(id);
        account.setProfilePicture(picture);
        // account.setProfilePictureId(id);
        accountRepo.save(account);
    }

    public void savePicture(String username, MultipartFile file) throws IOException {
        if (file.getContentType().equals("image/jpeg")) {
            Account account = accountRepo.findByUsername(username);
            Picture picture = new Picture(file.getBytes(), "", account);
            account.getPictures().add(picture);
            // account.setProfilePicture(account.getPictures().get(account.getPictures().size() - 1));
            pictureRepo.save(picture);
            // accountRepo.save(account);
        }
    }

    public byte[] getPicture(Long id) throws IOException {
        Picture picture = pictureRepo.getOne(id);
        return picture.getPicture();
    }

    public Picture getPic(Long id) {
        return pictureRepo.getOne(id);
    }

    @Transactional
    public void likeAPicture(String username, Long pictureId) {
        Account account = accountRepo.findByUsername(username);
        Picture picture = pictureRepo.getOne(pictureId);
        picture.getLikes().add(account);
        pictureRepo.save(picture);
    }

    @Transactional
    public void commentAPicture(Comment comment, Long pictureId) {
        commentRepo.save(comment);
        Picture picture = pictureRepo.getOne(pictureId);
        picture.getComments().add(comment);
        pictureRepo.save(picture);
    }

}
