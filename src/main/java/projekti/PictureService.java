package projekti;

import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    
    @Transactional
    public void saveProfilePicture(String username, Long id) throws IOException {
        Account account = accountRepo.findByUsername(username);
        Picture picture = pictureRepo.getOne(id);
        account.setProfilePicture(picture);
        accountRepo.save(account);
    }

    @Transactional
    public void savePicture(String username, MultipartFile file, String description) throws IOException {
        if (file.getContentType().substring(0, 5).equals("image")) {
            Account account = accountRepo.findByUsername(username);
            Picture picture = new Picture(file.getBytes(), description, account);
            List<Picture> userPictures = account.getPictures();
            if (userPictures.size() < 10) {
                account.getPictures().add(picture);
                pictureRepo.save(picture);
            }
        }
    }
    
    @Transactional
    public void deletePicture(Long id) {
        pictureRepo.deleteById(id);
    }

    @Cacheable("pictures")
    public byte[] getPicture(Long id) throws IOException {
//        System.out.print("- SQL / kuva " + id + ":");
        Picture picture = pictureRepo.getOne(id);
        return picture.getPicture();
    }

    public Picture findById(Long id) {
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
    public void unlikeAPicture(String username, Long pictureId) {
        Account account = accountRepo.findByUsername(username);
        Picture picture = pictureRepo.getOne(pictureId);
        picture.getLikes().remove(account);
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
