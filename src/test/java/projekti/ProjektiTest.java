package projekti;

import java.time.LocalDateTime;
import java.util.List;
import org.fluentlenium.core.FluentPage;
import static org.fluentlenium.core.filter.FilterConstructor.containingText;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testit.
 * 
 * @author Salla Koskinen
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class ProjektiTest extends org.fluentlenium.adapter.junit.FluentTest {

    @LocalServerPort
    private Integer port;
    
    @Autowired
    private MockMvc mockMvc;
        
    @Autowired
    AccountRepository accountRepo;
    
    @Autowired
    MessageRepository messageRepo;
    
    @Autowired
    PictureRepository pictureRepo;
    
    @Autowired
    CommentRepository commentRepo;
    
    @Autowired
    FollowRepository followRepo;
    
    @Test
    public void A_canRegister() {
        
        assertTrue(accountRepo.findByUsername("rolle") == null);
        
        goTo("http://localhost:" + port + "/rekisteroidy");
        find("#name").fill().with("Rolle Ryöväri");
        find("#username").fill().with("rolle");
        find("#password").fill().with("salasana");
        find("form").first().submit();

        assertTrue("Käyttäjän rekisteröityminen onnistuu.", accountRepo.findByUsername("rolle") != null);
        
    }

    @Test
    public void B_cannotLogInWithInvalidUsername() {
        
        assertTrue("Rolle löytyy", accountRepo.findByUsername("rolle") != null);
        
        goTo("http://localhost:" + port + "/login");
        find("#username").fill().with("role");
        find("#password").fill().with("salasana");
        find("form").first().submit();
        
        assertTrue("Kirjautuminen ei onnistu väärällä käyttäjätunnuksella.", pageSource().contains("Käyttäjätunnus tai salasana väärin."));
        
    }
    
    @Test
    public void C_canLogInWithValidUsernameAndPassword() {
        
        assertTrue("Rolle löytyy", accountRepo.findByUsername("rolle") != null);
        login("rolle");
        goTo("http://localhost:" + port + "/rolle");
        assertTrue("Kirjautuminen onnistuu.", pageSource().contains("Rolle Ryöväri"));
        
    }
    
    @Test
    public void D_canSendAMessageToOwnPage() throws Exception {
        int messageNumber = messageRepo.findAll().size();
        mockMvc.perform(post("/rolle/messages").param("messageText", "Testiviesti").with(user("rolle")));
        login("rolle");
        goTo("http://localhost:" + port + "/rolle");
        // Varmistetaan, että viesti näkyy omalla seinällä ja on lisätty tietokantaan.
        assertTrue("Viestin kirjoittaminen omalle seinälle onnistuu.", pageSource().contains("Testiviesti"));
        assertTrue("Viesti lisätty tietokantaan.", messageRepo.findAll().size() == messageNumber + 1);
    }
                
    @Test
    public void E_cantRegisterWhileLoggedIn() throws Exception {
        assertTrue(accountRepo.findByUsername("uusi") == null);
        mockMvc.perform(post("/rekisteroidy").param("name", "Uusi Käyttäjä").param("username", "uusi").param("password", "salasana").with(user("rolle")));
        assertTrue("Rekisteröityminen ei onnistu, jos on kirjautunut sisään.", accountRepo.findByUsername("uusi") == null);
    }
    
    @Test
    public void F_cantPostToOthersWall() throws Exception {
        addAccount("kukkis", "Kukka Aurinko", "salasana");
        addFollow("rolle", "kukkis");
        addFollow("kukkis", "rolle");
        int messageNumber = messageRepo.findAll().size();
        mockMvc.perform(post("/kukkis/messages").param("messageText", "Rollen viesti").with(user("rolle")));
        login("rolle");
        goTo("http://localhost:" + port + "/kukkis");
        // Varmistetaan, että viesti näkyy omalla seinällä ja on lisätty tietokantaan.
        assertFalse("Viestin kirjoittaminen muiden seinälle ei onnistu.", pageSource().contains("Rollen viesti"));
        assertTrue("Viestiä ei lisätty tietokantaan.", messageRepo.findAll().size() == messageNumber);
    }
    
    @Test
    public void G_canCommentMessage() throws Exception {
        
        int commentNumber = commentRepo.findAll().size();
        
        // Haetaan Rollen viestin id viestien kautta, jotta ei tule LazyInitializationException,
        // ilmeisesti johtuu siitä, että henkilön viesti on mapped by messages?
        Long id = 0L;
        for (Message message : messageRepo.findAll()) {
            if (message.getOwner().getUsername().equals("rolle")) {
                id = message.getId();
            }
        }
        
        mockMvc.perform(post("/rolle/messages/" + id + "/comment").param("commentText", "Rollen kommentti").with(user("rolle")));
        mockMvc.perform(post("/rolle/messages/" + id + "/comment").param("commentText", "Kukan kommentti").with(user("kukkis")));
        
        login("rolle");
        goTo("http://localhost:" + port + "/rolle");
        // Varmistetaan, että kommentit näkyvät seinällä ja on lisätty tietokantaan.
        assertTrue("Kommentin kirjoittaminen omaan viestiin onnistuu.", pageSource().contains("Rollen kommentti"));
        assertTrue("Kommentin kirjoittaminen muiden viestiin onnistuu.", pageSource().contains("Kukan kommentti"));
        assertTrue("Kommentit lisätty tietokantaan.", commentRepo.findAll().size() == commentNumber + 2);
        
    }
        
    @Test
    public void H_cannotCommentMessageIfNotFriend() throws Exception {
        
        addAccount("juukelispuukelis", "Voi Juku", "salasana");
        
        int commentNumber = commentRepo.findAll().size();
        
        // Haetaan Rollen viestin id viestien kautta, jotta ei tule LazyInitializationException,
        // ilmeisesti johtuu siitä, että henkilön viesti on mapped by messages?
        Long id = 0L;
        for (Message message : messageRepo.findAll()) {
            if (message.getOwner().getUsername().equals("rolle")) {
                id = message.getId();
            }
        }
        
        mockMvc.perform(post("/rolle/messages/" + id + "/comment").param("commentText", "Juku mikä kommentti").with(user("juukelispuukelis")));
        
        login("rolle");
        goTo("http://localhost:" + port + "/rolle");
        // Varmistetaan, että kommentit näkyvät seinällä ja on lisätty tietokantaan.
        assertFalse("Kommentin kirjoittaminen muiden viestiin ei onnistu.", pageSource().contains("Juku mikä kommentti"));
        assertTrue("Kommentit lisätty tietokantaan.", commentRepo.findAll().size() == commentNumber);
        
    }
    
    @Test
    public void I_cannotAcceptOthersFollowRequests() throws Exception {
        
        addPendingFollow("juukelispuukelis", "rolle");
        
        Account juukelispuukelis = accountRepo.findByUsername("juukelispuukelis");
        Account rolle = accountRepo.findByUsername("rolle");
        
        assertTrue(followRepo.findByFollowerAndFollowee(juukelispuukelis, rolle) != null);
                
        mockMvc.perform(post("/rolle/accept/juukelispuukelis").with(user("juukelispuukelis")));
        
        assertTrue("Ei voi hyväksyä muiden kaveripyyntöjä.", followRepo.findByFollowerAndFollowee(juukelispuukelis, rolle).isPending() == true);
        
    }
    
    @Test
    public void J_canAcceptOwnFollowRequests() throws Exception {
        
        Account juukelispuukelis = accountRepo.findByUsername("juukelispuukelis");
        Account rolle = accountRepo.findByUsername("rolle");
                        
        mockMvc.perform(post("/rolle/accept/juukelispuukelis").with(user("rolle")));
        
        assertTrue("Voi hyväksyä omia kaveripyyntöjä.", followRepo.findByFollowerAndFollowee(juukelispuukelis, rolle).isPending() == false);
        
    }
    
    @Test
    public void K_cannotMakeFriendRequestsForSelf() throws Exception {
        
        Account rolle = accountRepo.findByUsername("rolle");
        
        assertTrue(followRepo.findByFollowerAndFollowee(rolle, rolle) == null);
                
        mockMvc.perform(post("/rolle/follow").with(user("rolle")));
        
        assertTrue("Ei voi seurata itseään.", followRepo.findByFollowerAndFollowee(rolle, rolle) == null);
        
    }
    
    @Test
    public void L_userSearchWorks() throws Exception {
        
        Account rolle = accountRepo.findByUsername("rolle");
        Account juukelispuukelis = accountRepo.findByUsername("juukelispuukelis");
        Account kukkis = accountRepo.findByUsername("kukkis");
        
        assertTrue(followRepo.findByFollowerAndFollowee(kukkis, rolle) != null);
        assertTrue(followRepo.findByFollowerAndFollowee(juukelispuukelis, rolle) != null);
        
        login("rolle");
        goTo("http://localhost:" + port + "/kayttajat");
        
        assertTrue(el(("a"), containingText("Kukka Aurinko")).present());
        assertTrue(el(("a"), containingText("Voi Juku")).present());
                
        find(withId().startsWith("search")).fill().with("oi");
        
        assertFalse("Henkilö ei näy sivulla, jos hänen nimessään ei ole haun tekstipätkää.", el(("a"), containingText("Kukka Aurinko")).present());
        assertTrue("Henkilö näkyy sivulla, jos hänen nimessään on haun tekstipätkä.", el(("a"), containingText("Voi Juku")).present());
        
        goTo("http://localhost:" + port + "/kayttajat");
        
        assertTrue(el(("a"), containingText("Kukka Aurinko")).present());
        assertTrue(el(("a"), containingText("Voi Juku")).present());
        
        find(withId().startsWith("search")).fill().with("@kk");
        
        assertFalse("Henkilö ei näy sivulla, jos hänen käyttäjänimessään ei ole haun tekstipätkää.", el(("a"), containingText("Voi Juku")).present());
        assertTrue("Henkilö näkyy sivulla, jos hänen käyttäjänimessään on haun tekstipätkä.", el(("a"), containingText("Kukka Aurinko")).present());
        
    }
    
    @Test
    public void M_postingAndCommentingWorksWithoutRefresh() throws Exception {
        
        Account kukkis = accountRepo.findByUsername("kukkis");
        Account rolle = accountRepo.findByUsername("rolle");
        
        // Lähetetään kukkiksen sivuille viesti, tämä näkyy Rollen sivuilla.
        mockMvc.perform(post("/kukkis/messages").param("messageText", "Kukkiksen viesti").with(user("kukkis")));
        List<Message> kukkisMessages = messageRepo.getUserMessages(kukkis.getId());
        Long kukkisId = kukkisMessages.get(kukkisMessages.size() - 1).getId();
        
        login("rolle");
        
        assertTrue(pageSource().contains("Kukkiksen viesti"));
        assertFalse(pageSource().contains("Rollen testiviesti!"));
        assertFalse(pageSource().contains("Rollen testikommentti!"));
        assertFalse(pageSource().contains("Rollen toinen testikommentti!"));
        
        find(".messagesend > .form-control").fill().with("Rollen testiviesti!");
        find("form", withClass("messagesend")).submit();
        
        List<Message> rollesMessages = messageRepo.getUserMessages(rolle.getId());
        Long rolleId = rollesMessages.get(rollesMessages.size() - 1).getId();
              
        assertTrue("Viestin lähettäminen onnistuu.", pageSource().contains("Rollen testiviesti!"));
        
        find("#rolle-cm-" + kukkisId + " > .form-control").fill().with("Rollen testikommentti!");
        find("form", withId("rolle-cm-" + kukkisId)).submit();
                
        assertTrue("Kommentin lähettäminen onnistuu.", pageSource().contains("Rollen testikommentti!"));
        
        find("#rolle-cm-" + rolleId + " > .form-control").fill().with("Rollen toinen testikommentti!");
        find("form", withId("rolle-cm-" + rolleId)).submit();
              
        assertTrue("Kommentin lähettäminen onnistuu.", pageSource().contains("Rollen toinen testikommentti!"));
        
    }
    
    public void login(String username) {
        goTo("http://localhost:" + port + "/logout");
        goTo("http://localhost:" + port + "/login");
        find("#username").fill().with(username);
        find("#password").fill().with("salasana");
        find("form").first().submit();
    }
    
    public void addAccount(String username, String name, String password) {
        Account account = new Account(username, name, password);
        accountRepo.save(account);
    }
    
    public void addFollow(String user1, String user2) {
        Account account1 = accountRepo.findByUsername(user1);
        Account account2 = accountRepo.findByUsername(user2);
        Follow follow1 = new Follow(account1, account2, LocalDateTime.now(), false);
        followRepo.save(follow1);
    }
    
    public void addPendingFollow(String user1, String user2) {
        Account account1 = accountRepo.findByUsername(user1);
        Account account2 = accountRepo.findByUsername(user2);
        Follow follow = new Follow(account1, account2, LocalDateTime.now(), true);
        followRepo.save(follow);
    }
    
}
