package prep.web;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import prep.model.binding.RestaurantAddBindingModel;
import prep.model.entity.Restaurant;
import prep.model.entity.User;
import prep.model.service.RestaurantServiceModel;
import prep.service.RestaurantService;
import prep.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public RestaurantController(RestaurantService restaurantService, ModelMapper modelMapper, UserService userService) {
        this.restaurantService = restaurantService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String add(Model model){

        if(!model.containsAttribute("restaurantAddBindingModel")){
            model.addAttribute("restaurantAddBindingModel", new RestaurantAddBindingModel());
        }

        return "add-restaurant";
    }

    @PostMapping("/add")
    public String addConfirm(@Valid @ModelAttribute("restaurantAddBindingModel")
                                         RestaurantAddBindingModel restaurantAddBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession httpSession){

        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("restaurantAddBindingModel",restaurantAddBindingModel);
            redirectAttributes.addFlashAttribute
                    ("org.springframework.validation.BindingResult.restaurantAddBindingModel",bindingResult);
            return "redirect:/";
        }

        this.restaurantService.addRestaurant(this.modelMapper
                .map(restaurantAddBindingModel, RestaurantServiceModel.class),httpSession.getAttribute("id").toString());

        return "redirect:/";
    }

    @GetMapping("/details")
    public ModelAndView details(@RequestParam("id") String id, ModelAndView modelAndView, HttpSession httpSession) {
        User u = (User) httpSession.getAttribute("user");
        User user2 = this.userService.getById(u.getId());
        if(user2.getRole().getRoleName().toString().equals("ADMIN")){
            modelAndView.addObject("isADMIN",true);
        }

        modelAndView.addObject("restaurant",this.restaurantService.findById(id));
        modelAndView.setViewName("details-restaurant");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id")String id){
        this.restaurantService.delete(id);

        return "redirect:/";
    }
    @GetMapping("/rate")
    public ModelAndView rate(@RequestParam(name = "id") String id, ModelAndView modelAndView, HttpSession httpSession,@RequestParam(name="stars") int stars) {
        User user = (User) httpSession.getAttribute("user");
        Restaurant restaurant = this.restaurantService.getById(id);
        this.restaurantService.rate(user, restaurant, stars);

        User u = (User) httpSession.getAttribute("user");
        User user2 = this.userService.getById(u.getId());
        if(user2.getRole().getRoleName().toString().equals("ADMIN")){
            modelAndView.addObject("isADMIN",true);
        }
        modelAndView.setViewName("redirect:/restaurants");
        return modelAndView;
    }

}
