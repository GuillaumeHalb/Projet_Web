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
        render(frontAdvice, olderAdvices);
    }

    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }
    
    public static void show(Long id) {
        Advice advice = Advice.findById(id);
        String randomID = Codec.UUID();
        render(advice, randomID);
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
        validation.equals(
                          code, Cache.get(randomID)
                          ).message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
            render("Application/show.html", advice, randomID);
        }
        advice.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
        show(adviceId);
    }
    
    public static void updateMark(Long adviceId ,@Required (message="A mark is required")  int mark) {
        Advice advice = Advice.findById(adviceId);
        validation.isTrue("invalid",mark>0 && mark <= 10).message("Invalid value for mark. Insert a number between 1 and 10");
        if(validation.hasErrors()) {
            render("Application/show.html",advice,mark);
        }
        advice.addMark(mark);
        flash.success("Rating successfully added");
        show(adviceId);
        
    }
    

    public static void listTagged(String tag) {
        List<Advice> advices = Advice.findTaggedWith(tag);
        render(tag, advices);
    }

    
    public static void signUp(
                              @Required(message = "email required") String email,
                              @Required(message = "password required") String password,
                              @Required(message = "full name required") String fullName) 
    {
        User usr = new User(email, password, fullName).save();
        flash.success("Thanks for registering");
        render(email, fullName);
    }
    
    public static void search(@Required(message = "String is required")String recherche) {
        List<Advice> myList;
        String qr = "select distinct a from Advice a where upper(a.title) like upper('%" + recherche + "%')";
        myList = Advice.find(qr).fetch();
        render(recherche, myList);
    }
}
