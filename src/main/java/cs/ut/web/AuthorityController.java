package cs.ut.web;
import cs.ut.security.Authorities;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/security/authorities")
@Controller
@RooWebScaffold(path = "security/authorities", formBackingObject = Authorities.class)
public class AuthorityController {
}
