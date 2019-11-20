package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractPersistable<Long> {

    private String name;
    private String username;

//    @OneToOne
//    private Picture profilePicture;
    
    // Käyttäjällä voi olla monta seuraajaa ja käyttäjä voi seurata montaa.    
    @ManyToMany
    private List<User> followees;

    @ManyToMany
    private List<User> followers;

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

}
