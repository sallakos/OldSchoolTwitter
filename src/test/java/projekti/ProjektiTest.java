package projekti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjektiTest extends org.fluentlenium.adapter.junit.FluentTest {

    @LocalServerPort
    private Integer port;
    
    @Autowired
    AccountRepository accountRepo;
    
    @Test
    public void canRegister() {
        
        assertTrue(accountRepo.findByUsername("rolle") == null);
        
        goTo("http://localhost:" + port + "/rekisteroidy");
        find("#name").fill().with("Rolle Ryöväri");
        find("#username").fill().with("rolle");
        find("#password").fill().with("salasana");
        find("form").first().submit();

        assertTrue(accountRepo.findByUsername("rolle") != null);
        
    }
    
    @Test
    public void canLogIn() {
        
        goTo("http://localhost:" + port + "/login");
        find("#username").fill().with("rolle");
        find("#password").fill().with("salasana");
        find("form").first().submit();
        goTo("http://localhost:" + port + "/rolle");
        
        assertTrue(pageSource().contains("Rolle Ryöväri"));
        
    }
    
}
