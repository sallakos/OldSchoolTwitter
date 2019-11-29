package projekti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import projekti.Follow;
import projekti.Message;
import projekti.Picture;

/**
 * Mallintaa sovelluksen käyttäjää.
 *
 * @author Salla Koskinen
 */
@NamedEntityGraph(name = "Account.allUserData",
                  attributeNodes = {@NamedAttributeNode("pictures")
                                    /*@NamedAttributeNode("followers")*/})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String name;
    private String username;
    private String password;

    // private Long profilePictureId;
    // private Integer profilePictureIndex;
    
    @OneToOne
    private Picture profilePicture;
    
    // Käyttäjällä voi olla monta seuraajaa ja käyttäjä voi seurata montaa.    
    @OneToMany(mappedBy = "follower")
    private List<Follow> followees;

    @OneToMany(mappedBy = "followee")
    private List<Follow> followers;

    // Käyttäjällä voi olla monta kuvaa, mutta kuva kuuluu yhdelle käyttäjälle.
    @OneToMany(mappedBy = "owner")
    private List<Picture> pictures;
    
    // Käyttäjällä voi olla monta viestiä, mutta viesti on yhden käyttäjän.
    @OneToMany(mappedBy = "owner")
    private List<Message> messages;

    // Käyttäjä voi tykätä monesta kuvasta ja kuvasta voi tykätä moni käyttäjä.
    @ManyToMany(mappedBy = "likes")
    private List<Picture> likedPictures;

    // Käyttäjä voi tykätä monesta viestistä ja viestistä voi tykätä moni käyttäjä.
    @ManyToMany(mappedBy = "likes")
    private List<Message> likedMessages;

    public Account(String username, String name) {
        this.username = username;
        this.name = name;
    }
    
    public Account(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }
    
    public HashMap<String, Boolean> getFollowedUsernames() {
        HashMap<String, Boolean> usernames = new HashMap<>();
        for (Follow follow : this.followees) {
            usernames.put(follow.getFollowee().getUsername(), follow.isPending());
        }
        return usernames;
    }
    
}
