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
    
    @Transactional
    public void sendMessage(String username, String messageText) {
        Account account = accountRepo.findByUsername(username);
        Message message = new Message(messageText, account);
        messageRepo.save(message);
    }
    
}
