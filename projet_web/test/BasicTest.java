import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }
    
	@Test
	public void createAndRetrieveUser() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Retrieve the user with e-mail address bob@gmail.com
	    User bob = User.find("byEmail", "bob@gmail.com").first();
	    
	    // Test 
	    assertNotNull(bob);
	    assertEquals("Bob", bob.fullname);
	}
	
	@Test
	public void tryConnectAsUser() {
	    // Create a new user and save it
	    new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Test 
	    assertNotNull(User.connect("bob@gmail.com", "secret"));
	    assertNull(User.connect("bob@gmail.com", "badpassword"));
	    assertNull(User.connect("tom@gmail.com", "secret"));
	}
	
	@Test
	public void createAdvice() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Create a new advice
	    new Advice(bob, "My first advice", "Hello world").save();
	    
	    // Test that the advice has been created
	    assertEquals(1, Advice.count());
	    
	    // Retrieve all advices created by Bob
	    List<Advice> bobAdvises = Advice.find("byAuthor", bob).fetch();
	    
	    // Tests
	    assertEquals(1, bobAdvises.size());
	    Advice firstAdvice = bobAdvises.get(0);
	    assertNotNull(firstAdvice);
	    assertEquals(bob, firstAdvice.author);
	    assertEquals("My first advice", firstAdvice.title);
	    assertEquals("Hello world", firstAdvice.content);
	    assertNotNull(firstAdvice.postedAt);
	}
	
	@Test
	public void adviceComments() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new advice
	    Advice bobAdvice = new Advice(bob, "My first advice", "Hello world").save();
	 
	    // advice a first comment
	    new Comment(bobAdvice, "Jeff", "Nice advice").save();
	    new Comment(bobAdvice, "Tom", "I knew that !").save();
	 
	    // Retrieve all comments
	    List<Comment> bobAdviceComments = Comment.find("byAdvice", bobAdvice).fetch();
	 
	    // Tests
	    assertEquals(2, bobAdviceComments.size());
	 
	    Comment firstComment = bobAdviceComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice advice", firstComment.content);
	    assertNotNull(firstComment.postedAt);
	 
	    Comment secondComment = bobAdviceComments.get(1);
	    assertNotNull(secondComment);
	    assertEquals("Tom", secondComment.author);
	    assertEquals("I knew that !", secondComment.content);
	    assertNotNull(secondComment.postedAt);
	}
	
	@Test
	public void useTheCommentsRelation() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new advice
	    Advice bobAdvice = new Advice(bob, "My first advice", "Hello world").save();
	 
	    // advice a first comment
	    bobAdvice.addComment("Jeff", "Nice advice");
	    bobAdvice.addComment("Tom", "I knew that !");
	 
	    // Count things
	    assertEquals(1, User.count());
	    assertEquals(1, Advice.count());
	    assertEquals(2, Comment.count());
	 
	    // Retrieve Bob's advice
	    bobAdvice = Advice.find("byAuthor", bob).first();
	    assertNotNull(bobAdvice);
	 
	    // Navigate to comments
	    assertEquals(2, bobAdvice.comments.size());
	    assertEquals("Jeff", bobAdvice.comments.get(0).author);
	    
	    // Delete the advice
	    bobAdvice.delete();
	    
	    // Check that all comments have been deleted
	    assertEquals(1, User.count());
	    assertEquals(0, Advice.count());
	    assertEquals(0, Comment.count());
	}
	
	@Test
	public void fullTest() {
	    Fixtures.loadModels("data.yml");
	 
	    // Count things
	    assertEquals(2, User.count());
	    assertEquals(3, Advice.count());
	    assertEquals(3, Comment.count());
	 
	    // Try to connect as users
	    assertNotNull(User.connect("bob@gmail.com", "secret"));
	    assertNotNull(User.connect("jeff@gmail.com", "secret"));
	    assertNull(User.connect("jeff@gmail.com", "badpassword"));
	    assertNull(User.connect("tom@gmail.com", "secret"));
	 
	    // Find all of Bob's advises
	    List<Advice> bobadvices = Advice.find("author.email", "bob@gmail.com").fetch();
	    assertEquals(2, bobadvices.size());
	 
	    // Find all comments related to Bob's advises
	    List<Comment> bobComments = Comment.find("advice.author.email", "bob@gmail.com").fetch();
	    assertEquals(3, bobComments.size());
	 
	    // Find the most recent advice
	    Advice frontAdvice = Advice.find("order by postedAt desc").first();
	    assertNotNull(frontAdvice);
	    assertEquals("About the model layer", frontAdvice.title);
	 
	    // Check that this advice has two comments
	    assertEquals(2, frontAdvice.comments.size());
	 
	    // advice a new comment
	    frontAdvice.addComment("Jim", "Hello guys");
	    assertEquals(3, frontAdvice.comments.size());
	    assertEquals(4, Comment.count());
	}
}
