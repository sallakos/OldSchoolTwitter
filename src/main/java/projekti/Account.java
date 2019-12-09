package projekti;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Mallintaa sovelluksen käyttäjää.
 *
 * @author Salla Koskinen
 */
@NamedEntityGraph(name = "Account.allUserData",
                  attributeNodes = {@NamedAttributeNode("pictures"),
                                    @NamedAttributeNode("profilePicture")})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String name;
    private String username;
    private String password;
    
    @OneToOne
    @Basic(fetch = FetchType.LAZY)
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
    @Basic(fetch = FetchType.LAZY)
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
    
}
