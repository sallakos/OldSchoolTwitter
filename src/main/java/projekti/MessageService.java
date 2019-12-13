package projekti;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Salla Koskinen
 */
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
    public void unlikeAMessage(String username, Long messageId) {
        Account account = accountRepo.findByUsername(username);
        Message message = messageRepo.getOne(messageId);
        message.getLikes().remove(account);
        messageRepo.save(message);
    }
    
    @Transactional
    public void commentAMessage(Comment comment, Long messageId) {
        commentRepo.save(comment);
        Message message = messageRepo.getOne(messageId);
        message.getComments().add(comment);
        messageRepo.save(message);
    }
    
    public Message findById(Long id) {
        return messageRepo.getOne(id);
    }
    
    public List<Message> getAllMessages(Account account) {
//        System.out.print("- SQL / MessageService / getAllMessages(): ");
        Long userId = account.getId();
        return messageRepo.getAllMessages(userId);
    }
    
    public List<Message> getUserMessages(Account account) {
//        System.out.print("- SQL / MessageService / getUserMessages(): ");
        Long userId = account.getId();
        return messageRepo.getUserMessages(userId);
    }
        
}
