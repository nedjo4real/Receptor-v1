package prep.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import prep.model.binding.UserChangeRoleBindModel;
import prep.model.entity.User;
import prep.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/roles")
public class RolesController {

    private final UserService userService;

    public RolesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/edit")
    public String getEdit(Model model,HttpSession httpSession) {
        if (!model.containsAttribute("userChangeRoleBindModel")) {
            model.addAttribute("userChangeRoleBindModel", new UserChangeRoleBindModel());
        }

        User u = (User) httpSession.getAttribute("user");
        User user = this.userService.getById(u.getId());
        if (user.getRole().getRoleName().toString().equals("ADMIN")) {
            model.addAttribute("isADMIN", true);
            return "role-edit";
        } else {
            return "unauthorized";
        }

    }

    @PostMapping("/edit")
    public ModelAndView postEdit(@Valid
                                 @ModelAttribute("userChangeRoleBindModel") UserChangeRoleBindModel userChangeRoleBindModel,
                                 BindingResult bindingResult, ModelAndView modelAndView, HttpSession httpSession,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userChangeRoleBindModel", userChangeRoleBindModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userChangeRoleBindModel", bindingResult);
            modelAndView.setViewName("redirect:/roles/edit");
        } else {
            if (this.userService.findByUsername(userChangeRoleBindModel.getUsername()) == null) {
                redirectAttributes.addFlashAttribute("notFound", true);
                redirectAttributes.addFlashAttribute("userChangeRoleBindModel", userChangeRoleBindModel);
                modelAndView.setViewName("redirect:/roles/edit");
            } else {
                this.userService.changeRole(userChangeRoleBindModel);
                modelAndView.setViewName("redirect:/");
            }
        }


        return modelAndView;
    }
}
