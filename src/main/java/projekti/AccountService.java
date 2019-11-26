package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    
    @Autowired
    AccountRepository accountRepo;
        
    public void register(String username, String name) {
        Account account = new Account(username, name);
        accountRepo.save(account);
    }
        
    public Account findByUsername(String username) {
        return accountRepo.findByUsername(username);
    }
    
    public List<Account> findAll() {
        return accountRepo.findAll();
    }
    
    public Account currentUser() {
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepo.findByUsername(userLoggedIn);
    }
    
    public boolean isCurrentUser(String username) {
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return userLoggedIn.equals(username);
    }
    
}
