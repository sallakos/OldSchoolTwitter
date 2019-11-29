package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.SecurityConfiguration;

@Service
public class AccountService {
    
    @Autowired
    SecurityConfiguration securityConfiguration;
    
    @Autowired
    AccountRepository accountRepo;
    
    @Autowired
    FollowRepository followRepo;
            
    @Transactional
    public void register(String username, String name, String password) {
        Account account = new Account(username, name, securityConfiguration.passwordEncoder().encode(password));
        accountRepo.save(account);
    }
    
    @Transactional
    public void follow(Account follower, Account followee, LocalDateTime startOfFollow) {
        Follow follow = new Follow(follower, followee, startOfFollow, true);
        followRepo.save(follow);
        System.out.println("SQL by AccountService.follow()");
    }
    
    @Transactional
    public void unfollow(Account follower, Account followee) {
        Long id = followRepo.findByFollowerAndFollowee(follower, followee).getId();
        followRepo.deleteById(id);
    }
        
    public Account findByUsername(String username) {
        System.out.println("SQL by AccountService / findByUsername():");
        return accountRepo.findByUsername(username);
    }
    
//    public Account findByUsernameAllData(String username) {
//        return accountRepo.allUserData(username);
//    }
    
    public List<Account> findAll() {
        return accountRepo.findAll();
    }
    
    public Account currentUser() {
        System.out.println("SQL by AccountService / currentUser():");
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepo.findByUsername(userLoggedIn);
    }
    
    public boolean isCurrentUser(String username) {
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return userLoggedIn.equals(username);
    }
    
    public boolean isRegistered() {
        if (currentUser() != null) {
            return true;
        }
        return false;
    }
    
    public boolean isFriend(String username) {
        if (isCurrentUser(username)) {
            return false;
        }
        for (Follow follow : currentUser().getFollowees()) {
            if (follow.getFollowee().getUsername().equals(username)) {
                if (!follow.isPending()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isPendingRequest(String username) {
        if (isCurrentUser(username)) {
            return false;
        }
        for (Follow follow : currentUser().getFollowees()) {
            if (follow.getFollowee().getUsername().equals(username)) {
                if (follow.isPending()) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
