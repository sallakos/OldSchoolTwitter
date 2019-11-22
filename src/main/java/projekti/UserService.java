package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepo;
        
    public void register(String username, String name) {
        User user = new User(username, name);
        userRepo.save(user);
    }
        
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    public List<User> findAll() {
        return userRepo.findAll();
    }
    
}
