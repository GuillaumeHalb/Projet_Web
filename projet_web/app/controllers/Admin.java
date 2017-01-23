package controllers;
 
import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;
 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
    
    public static void index() {
        String user = Security.connected();
        List<Advice> advices = Advice.find("author.email", user).fetch();
        render(advices);
    }
    
    public static void form(Long id) {
        if(id != null) {
            Advice advice = Advice.findById(id);
            render(advice);
        }
        render();
    }
     
    public static void save(Long id, String title, String content, String tags, int mark, double value) {
        Advice advice;
        if(id == null) {
            // Create advice
            User author = User.find("byEmail", Security.connected()).first();
            advice = new Advice(author, title, content, mark, value);
        } else {
            // Retrieve advice
            advice = Advice.findById(id);
            // Edit
            advice.title = title;
            advice.content = content;
            advice.totalMark = mark;
            advice.tags.clear();
            advice.expectedValue = value;
        }
        // Set tags list
        for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                advice.tags.add(Tag.findOrCreateByName(tag));
            }
        }
        // Validate
        validation.valid(advice);
        if(validation.hasErrors()) {
            render("@form", advice);
        }
        // Save
        advice.save();
        index();
    }
    
    public static void signUp() {
        render();
    }
}
