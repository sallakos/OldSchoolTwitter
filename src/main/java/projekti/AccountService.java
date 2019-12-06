package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public void register(AccountData accountData) {
        Account account = new Account(accountData.getUsername(), accountData.getName(), securityConfiguration.passwordEncoder().encode(accountData.getPassword()));
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
    
    @Transactional
    public void acceptFollow(Account follower, Account followee) {
        followRepo.findByFollowerAndFollowee(follower, followee).setPending(false);
        followRepo.findByFollowerAndFollowee(follower, followee).setStartOfFollow(LocalDateTime.now());
    }
    
    @Transactional
    public void declineFollow(Account follower, Account followee) {
        Long id = followRepo.findByFollowerAndFollowee(follower, followee).getId();
        followRepo.deleteById(id);
    }
        
    public Account findByUsername(String username) {
        System.out.println("SQL by AccountService / findByUsername():");
        return accountRepo.findByUsername(username);
    }
    
    public List<Account> findAll() {
        System.out.println("SQL by AccountService / findAll():");
        Sort sort = Sort.by("name");
        return accountRepo.findAll(sort);
    }
    
    public HashMap<String, Boolean> getFollowedUsernames(Account account) {
        System.out.println("SQL by AccountService / getFollowedUsernames():");
        HashMap<String, Boolean> usernames = new HashMap<>();
        for (Follow follow : account.getFollowees()) {
            usernames.put(follow.getFollowee().getUsername(), follow.isPending());
        }
        return usernames;
    }
    
    public List<Account> getUserFollowees(Account account) {
        System.out.println("SQL by AccountService / getUserFollowees():");
        return accountRepo.findUserFollowees(account.getId());
    }
    
    public List<Account> getUserFollowers(Account account) {
        System.out.println("SQL by AccountService / getUserFollowers():");
        return accountRepo.findUserFollowers(account.getId());
    }
    
    public List<Account> findPendingRequests(Account account) {
        return accountRepo.findPendingRequests(account.getId());
    }
    
    public Account currentUser() {
        System.out.println("SQL by AccountService / currentUser():");
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepo.findByUsername(userLoggedIn);
    }
    
    public boolean isCurrentUser(String username) {
        System.out.println("SQL by AccountService / isCurrentUser():");
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return userLoggedIn.equals(username);
    }
    
    public boolean isRegistered() {
        if (currentUser() != null) {
            return true;
        }
        return false;
    }
    
    // -1 ei seurattava
    // 0 pyyntö lähetetty
    // 1 on seurattava
    // 2 oma sivu
    public int friendStatus(Account account) {
        Account currentUser = currentUser();
        if (account.equals(currentUser)) {
            return 2;
        } else {
            System.out.println("SQL by AccountService / friendStatus() Follow for-loop: ");
            for (Follow follow : currentUser.getFollowees()) {
                if (follow.getFollowee().equals(account)) {
                    if (!follow.isPending()) { return 1; }
                    else if (follow.isPending()) { return 0; }
                }
            }
        }
        return -1;
    }
    
    public int numberOfPendingRequests(Account account) {
        System.out.println("SQL by AccountService / numberOfPendingRequests(): ");
        return accountRepo.numberOfPendingRequests(account.getId());
    }
    
    public List<Follow> get6Followees(Account account) {
        return account.getFollowees().subList(0, Math.min(account.getFollowees().size(), 6));
    }
    public List<Follow> get6Followers(Account account) {
        return account.getFollowers().subList(0, Math.min(account.getFollowers().size(), 6));
    }
    
    public boolean checkUniqueUsername(String username) {
        return accountRepo.findByUsername(username) == null;
    }
  
}
