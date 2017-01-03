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
	    
	    // Create a new post
	    new Advice(bob, "My first post", "Hello world").save();
	    
	    // Test that the post has been created
	    assertEquals(1, Advice.count());
	    
	    // Retrieve all posts created by Bob
	    List<Advice> bobPosts = Advice.find("byAuthor", bob).fetch();
	    
	    // Tests
	    assertEquals(1, bobPosts.size());
	    Advice firstPost = bobPosts.get(0);
	    assertNotNull(firstPost);
	    assertEquals(bob, firstPost.author);
	    assertEquals("My first post", firstPost.title);
	    assertEquals("Hello world", firstPost.content);
	    assertNotNull(firstPost.postedAt);
	}
	
	@Test
	public void adviceComments() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new post
	    Advice bobAdvice = new Advice(bob, "My first post", "Hello world").save();
	 
	    // Post a first comment
	    new Comment(bobAdvice, "Jeff", "Nice post").save();
	    new Comment(bobAdvice, "Tom", "I knew that !").save();
	 
	    // Retrieve all comments
	    List<Comment> bobAdviceComments = Comment.find("byAdvice", bobAdvice).fetch();
	 
	    // Tests
	    assertEquals(2, bobAdviceComments.size());
	 
	    Comment firstComment = bobAdviceComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice post", firstComment.content);
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
	 
	    // Create a new post
	    Advice bobAdvice = new Advice(bob, "My first post", "Hello world").save();
	 
	    // Post a first comment
	    bobAdvice.addComment("Jeff", "Nice post");
	    bobAdvice.addComment("Tom", "I knew that !");
	 
	    // Count things
	    assertEquals(1, User.count());
	    assertEquals(1, Advice.count());
	    assertEquals(2, Comment.count());
	 
	    // Retrieve Bob's post
	    bobAdvice = Advice.find("byAuthor", bob).first();
	    assertNotNull(bobAdvice);
	 
	    // Navigate to comments
	    assertEquals(2, bobAdvice.comments.size());
	    assertEquals("Jeff", bobAdvice.comments.get(0).author);
	    
	    // Delete the post
	    bobAdvice.delete();
	    
	    // Check that all comments have been deleted
	    assertEquals(1, User.count());
	    assertEquals(0, Advice.count());
	    assertEquals(0, Comment.count());
	}
}
