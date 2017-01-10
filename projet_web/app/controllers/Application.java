package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;

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
        render(advice);
    }

    public static void captcha() {
        Images.Captcha captcha = Images.captcha();
        renderBinary(captcha);
    }

}
