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
	
	@RequestMapping(value="/passwordSubmit",method=RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model,RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
	    String pattern = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}";
		if (pass.getConPassword().equals(pass.getNewPassword())) {
			
			if (passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				if(pass.getNewPassword().matches(pattern)) {
					userService.changePassword(user, pass.getNewPassword());
					message = "password berhasil diubah";
				}
				else {
					message = "password anda harus mengandung angka, huruf, dan memiliki setidaknya 8 karakter ";
				}
			}
			else {
				message = "password lama anda salah";
			}
			
		}
		else {
			message = "password baru tidak sesuai";
		}
		
		
		ModelAndView modelAndView = new ModelAndView("redirect:/user/updatePass");
		redir.addFlashAttribute("msg",message);
		return modelAndView;
	}
}
