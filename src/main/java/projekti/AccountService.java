package projekti;

/**
 * 
 * @author Salla Koskinen
 */
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
    
    // -------------------- TIETOKANNAN MUUTTAMINEN --------------------------------------------------------
    
    // Rekisteröityminen.
    @Transactional
    public void register(String username, String name, String password) {
        Account account = new Account(username, name, securityConfiguration.passwordEncoder().encode(password));
        accountRepo.save(account);
    }
    
    // Rekisteröityminen.
    @Transactional
    public void register(AccountData accountData) {
        Account account = new Account(accountData.getUsername(), accountData.getName(), securityConfiguration.passwordEncoder().encode(accountData.getPassword()));
        accountRepo.save(account);
    }
    
    // Seuraaminen.
    @Transactional
    public void follow(Account follower, Account followee, LocalDateTime startOfFollow) {
        Follow follow = new Follow(follower, followee, startOfFollow, true);
        followRepo.save(follow);
    }
    
    // Seuraamisen lopettaminen.
    @Transactional
    public void unfollow(Account follower, Account followee) {
        Long id = followRepo.findByFollowerAndFollowee(follower, followee).getId();
        followRepo.deleteById(id);
    }
    
    // Seuraamisen hyväksyminen.
    @Transactional
    public void acceptFollow(Account follower, Account followee) {
        Follow follow = followRepo.findByFollowerAndFollowee(follower, followee);
        follow.setPending(false);
        follow.setStartOfFollow(LocalDateTime.now());
    }
    
    // Seuraamisen hylkääminen.
    @Transactional
    public void declineFollow(Account follower, Account followee) {
        Long id = followRepo.findByFollowerAndFollowee(follower, followee).getId();
        followRepo.deleteById(id);
    }
    
    // -------------------- HAKUJA TIETOKANNASTA --------------------------------------------------------
    
    // Etsi käyttäjää käyttäjänimellä.
    public Account findByUsername(String username) {
        System.out.println("- SQL / AccountService / findByUsername(): \n" + 
                           "      (hakee myös profiilikuvan, jos se on määritelty)");
        return accountRepo.findByUsername(username);
    }
    
    // Kaikki käyttäjät.
    public List<Account> findAll() {
        System.out.println("- SQL / AccountService / findAll():");
        Sort sort = Sort.by("name");
        return accountRepo.findAll(sort);
    }
    
    // Käyttäjää seuraavat käyttäjät ja seuraamisen tila.
    public HashMap<String, Boolean> getFollowedUsernames(Account account) {
        System.out.println("- SQL / AccountService / getFollowedUsernames():");
        HashMap<String, Boolean> usernames = new HashMap<>();
        for (Follow follow : account.getFollowees()) {
            usernames.put(follow.getFollowee().getUsername(), follow.isPending());
        }
        return usernames;
    }
    
    // Käyttäjän kuvien idt:
    public List<Long> findUserPictureIds(Account account) {
        System.out.println("- SQL / AccountService / findUserPictures():");
        return accountRepo.findUserPictures(account.getId());
    }
    
    // Käyttäjän seurattavat.
    public List<Account> getUserFollowees(Account account) {
        System.out.println("- SQL / AccountService / getUserFollowees():");
        return accountRepo.findUserFollowees(account.getId());
    }
    
    // Käyttäjän seuraajat.
    public List<Account> getUserFollowers(Account account) {
        System.out.println("- SQL / AccountService / getUserFollowers():");
        return accountRepo.findUserFollowers(account.getId());
    }
    
    // Seurauspyynnöt.
    public List<Account> findPendingRequests(Account account) {
        System.out.println("- SQL / AccountService / findPendingRequests():");
        return accountRepo.findPendingRequests(account.getId());
    }
    
    // Nykyinen käyttäjä.
    public Account currentUser() {
        System.out.println("- SQL / AccountService / currentUser():");
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepo.findByUsername(userLoggedIn);
    }
    
    // Onko käyttäjä omalla sivullaan.
    public boolean isCurrentUser(String username) {
        System.out.println("- SQL / AccountService / isCurrentUser():");
        String userLoggedIn = SecurityContextHolder.getContext().getAuthentication().getName();
        return userLoggedIn.equals(username);
    }
    
    // Onko käyttäjä rekisteröitynyt.
    public boolean isRegistered() {
        return currentUser() != null;
    }
    
    // Seuraamisen tila.
    // -1 ei seurattava, 0 pyyntö lähetetty
    // 1 on seurattava,  2 oma sivu
    public int friendStatus(Account account) {
        System.out.println("SQL / AccountService / friendStatus(): \n" + 
                           "      (kaksi kyselyä, jos ei ole omalla sivulla)");
        Account currentUser = currentUser();
        if (account.equals(currentUser)) {
            return 2;
        } else {
            System.out.println("- SQL / AccountService / friendStatus() Follow for-loop: ");
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
        System.out.println("- SQL / AccountService / numberOfPendingRequests(): ");
        return accountRepo.numberOfPendingRequests(account.getId());
    }
    
    public List<Follow> get6Followees(Account account) {
        List<Follow> follows = new ArrayList<>();
        System.out.println("- SQL / AccountService / get6Followees():");
        for (Follow follow : account.getFollowees()) {
            if (!follow.isPending()) {
                follows.add(follow);
            }
            if (follows.size() == 6) {
                return follows;
            }
        }
        return follows;
    }
    
    public List<Follow> get6Followers(Account account) {
        List<Follow> follows = new ArrayList<>();
        System.out.println("- SQL / AccountService / get6Followers():");
        for (Follow follow : account.getFollowers()) {
            if (!follow.isPending()) {
                follows.add(follow);
            }
            if (follows.size() == 6) {
                return follows;
            }
        }
        return follows;
    }
    
    public boolean checkUniqueUsername(String username) {
        System.out.println("- SQL / AccountService / checkUniqueUsername():");
        return accountRepo.findByUsername(username) == null;
    }
    
}
