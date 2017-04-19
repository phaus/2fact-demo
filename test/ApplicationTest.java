import static org.fest.assertions.Assertions.assertThat;
import static play.data.Form.form;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import org.junit.Test;

import models.User;
import play.data.Form;
import play.twirl.api.Content;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    //@Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    //@Test
    public void renderTemplate() {
    	Form<User> USER_FORM = form(User.class);
    	User user = User.create("username", "password");
        Content html = views.html.index.render("Welcome to 2Fact Auth Examples!", user, USER_FORM);
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Welcome to 2Fact Auth Examples!");
    }


}
