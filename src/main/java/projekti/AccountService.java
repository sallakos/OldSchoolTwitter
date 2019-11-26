package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    
}
