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
public class MessageService {
    
    @Autowired
    AccountRepository accountRepo;
    
    @Autowired
    MessageRepository messageRepo;
    
    @Autowired
    CommentRepository commentRepo;
    
    @Transactional
    public void sendMessage(String username, String messageText) {
        Account account = accountRepo.findByUsername(username);
        Message message = new Message(messageText, account);
        messageRepo.save(message);
    }
    
    @Transactional
    public void likeAMessage(String username, Long messageId) {
        Account account = accountRepo.findByUsername(username);
        Message message = messageRepo.getOne(messageId);
        message.getLikes().add(account);
        messageRepo.save(message);
    }
    
    @Transactional
    public void commentAMessage(Comment comment, Long messageId) {
        commentRepo.save(comment);
        Message message = messageRepo.getOne(messageId);
        message.getComments().add(comment);
        messageRepo.save(message);
    }
    
}
