package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;
import play.libs.*;
import play.cache.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        Advice frontAdvice = Advice.find("order by postedAt desc").first();
        List<Advice> olderAdvices = Advice.find(
                                                "order by postedAt desc"
                                                ).from(1).fetch(10);
		List<Tag> Tags = Tag.find("order by name desc").fetch();
        if (Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            render(frontAdvice, olderAdvices, Tags, user);
        } else {
            render(frontAdvice, olderAdvices, Tags);
        }
    }
	
    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }
	
	public static void show(Long id ,boolean reviewers) {
	if (Security.isConnected()) {
		Advice advice = Advice.findById(id);
        	String randomID = Codec.UUID();
		User user = User.find("byEmail", Security.connected()).first();
		render(advice, randomID, reviewers, user);
	} else {
        	Advice advice = Advice.findById(id);
        	String randomID = Codec.UUID();
        	render(advice, randomID,reviewers);
	}
    }


    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }
   
    public static void postComment(
                                   Long adviceId, 
                                   @Required(message="Author is required") String author, 
                                   @Required(message="A message is required") String content, 
                                   @Required(message="Please type the code") String code, 
                                   String randomID)
    {
        Advice advice = Advice.findById(adviceId);
        if(!Play.id.equals("test")) {
            validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
        }
        if(validation.hasErrors()) {
            render("Application/show.html", advice, randomID);
        }
        advice.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
        show(adviceId,false);
    }
    
    public static void postReview(Long adviceId ,@Required (message="A mark is required")  int mark,
                                  @Required(message="Author is required") String author) {
        Advice advice = Advice.findById(adviceId);
        validation.isTrue("invalid",mark>0 && mark <= 10).message("Invalid value for mark. Insert a number between 1 and 10");
        if(validation.hasErrors()) {
            render("Application/show.html",advice,mark);
        }
        advice.addReview(mark,author);
        flash.success("Rating successfully added");
        show(adviceId,true);
    }
    

    public static void listTagged(String tag) {
        List<Advice> advices = Advice.findTaggedWith(tag);
        render(tag, advices);
    }

    public static void signUp() {
        render();
    }
    
    public static void newMember(@Required(message = "email required") String email,
                                 @Required(message = "full name required") String fullName,
                                 @Required(message = "password required") String password,
                                 @Required(message = "password required") String confirm,
                                 boolean admin
                                 )
    {
        if (!confirm.equals(password)) {
            // TODO: generate error
            System.out.println("Passwords do not match");
        }
        User usr = new User(email, password, fullName).save();
        flash.success("Thanks for registering");
        
        List<User> users = User.find("select distinct u from User u").fetch();
        index();
    }
    
    public static void search(@Required(message = "String is required")String recherche) {
        if (recherche.equals("")) {
            // TODO: generate error
            /*index();
              return;*/
        }
        List<Advice> myList;
        List<Advice> listFromTag = Advice.findTaggedWith(recherche);
        String qr = "select distinct a from Advice a where upper(a.title) like upper('%" + recherche + "%')";
        myList = Advice.find(qr).fetch();
        myList.addAll(listFromTag);
        render(recherche, myList);
    }
}
