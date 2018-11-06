package com.apap.tutorial6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial6.model.PasswordModel;
import com.apap.tutorial6.model.UserRoleModel;
import com.apap.tutorial6.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value="/addUser", method=RequestMethod.POST)
	private ModelAndView addUser(@ModelAttribute UserRoleModel user, RedirectAttributes redir) {
	    String pattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}";
	    String message = "";
		if(user.getPassword().matches(pattern)){
			userService.addUser(user);
			message = null;
		}
		else {
			message = "password anda harus mengandung angka, huruf kecil, dan memiliki setidaknya 8 karakter";
			
		}
		ModelAndView modelAndView = new ModelAndView("redirect:/");
		redir.addFlashAttribute("msg",message);
		return modelAndView;
	}
	
	@RequestMapping(value = "/updatePass")
	private String updatePass() {
		return "update-password";
	}
	
	@RequestMapping(value = "/updatePass", method = RequestMethod.POST)
	private String updatePassSubmit(@ModelAttribute PasswordModel pass, Model model) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		if (pass.getConfirmedPassword().equals(pass.getNewPassword())) {
			
			if (passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				message = "Success";
				userService.changePassword(user, pass.getNewPassword());
			}
			else {
				message = "NotMatch";
			}
			
		}
		else {
			message = "Failed";
		}
		model.addAttribute("message" , message);
		return "update-password";
	}
}
