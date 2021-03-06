package controllers;
 
import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;
import play.data.validation.Required;

 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            if (!user.isAdmin || (user.isAdmin && user.authorized))
                {
                    renderArgs.put("user", user.fullname);
                }
        }
    }
    
    public static void index() {
        String user = Security.connected();
        List<Advice> advices = Advice.find("author.email", user).fetch();
        render(advices);
    }
    
    public static void baskets(){
    	String user = Security.connected();
        List<Basket> baskets = Basket.find("user.email", user).fetch();
        render(baskets);
    }
    
    public static void form(Long id) {
        if(id != null) {
            Advice advice = Advice.findById(id);
            render(advice);
        }
        render();
    }
    
    public static void newBasket(Long id) {
        if(id != null) {
            Basket basket = Basket.findById(id);
            render(basket);
        }
        render();
    }
     
    public static void save(Long id,
                            @Required(message="Title required") String title,
                            @Required(message="Content required") String content,
                            String tags,
                            int mark,
                            @Required(message="Expected value required") double value) {
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
    

    public static void saveBasket(Long id,
                                  @Required(message = "basket name required") String name) {
        Basket basket;
        
        if (validation.hasErrors()) {
            for(play.data.validation.Error error : validation.errors()) {
                System.out.println(error.message());
            }
            baskets();
        }
        
        if(id == null) {
            // Create Basket
            User author = User.find("byEmail", Security.connected()).first();
            basket = new Basket(name, author);
        } else {
            // Retrieve Basket
            basket = Basket.findById(id);
            // Edit
            basket.name = name;
        }
        // Set advices list
        /**for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                advice.tags.add(Tag.findOrCreateByName(tag));
            }
        }**/
        // Validate
        validation.valid(basket);
        if(validation.hasErrors()) {
            render("@form", basket);
        }
        // Save
        basket.save();
        baskets();
    }

    public static void signUp() {
        render();

    }


}
