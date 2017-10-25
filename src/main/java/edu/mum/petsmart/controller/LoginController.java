package edu.mum.petsmart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.mum.petsmart.domain.Cart;
import edu.mum.petsmart.domain.Item;
import edu.mum.petsmart.domain.Login;
import edu.mum.petsmart.service.CartService;
import edu.mum.petsmart.service.CustomerService;
import edu.mum.petsmart.service.LoginService;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	SessionHelper sessionHelper;

 	@RequestMapping(value ="/login", method=RequestMethod.GET)
	public String login(@ModelAttribute("login") Login login, Model model) {
 		model.addAttribute("errors", "");
 		return "login";
	}
	
 	@RequestMapping(value ="/login", method=RequestMethod.POST)
	public String doLogin(@ModelAttribute("login") Login login, Model model,  HttpServletRequest request) {
 		
 		Login l = loginService.findByUsername(login.getUserId());
 		if (l != null) {
 			if (l.getPassword().equals(login.getPassword())) {
 				request.getSession().setAttribute("login", l);
 				
 				if(l.getCustomer().getCart() == null) {
 					l.getCustomer().setCart(new Cart());
 				}
 				
 				Cart customerCart = l.getCustomer().getCart();
 				Cart sessionCart = cartService.get(((Cart)request.getSession().getAttribute("cart")).getId());
 				
 				List<Item> customerItems = customerCart.getCartItems();
 				List<Item> sessionItems = sessionCart.getCartItems();
 				
 				customerCart.getCartItems().addAll(sessionItems);
 				sessionCart.getCartItems().addAll(customerItems);
 				
 				if ("ADMIN".equals(l.getRole())) {
 					return "redirect:admin";
 				} else {
 					return "redirect:products";
 				}
 			}
 		}
 		
 		model.addAttribute("errors", "Login failed. Please check you username and password.");
 		return "login";
	}
 	 
 	@RequestMapping(value ="/logout")
	public String logout(@ModelAttribute("login") Login login, HttpServletRequest request) {
 		request.getSession().removeAttribute("login");
 		return "redirect:products";
	}
 	
 	
 	@RequestMapping(value ="/addUser")
	public String addUser(@ModelAttribute("login") Login login) {
 		return "addUser";
	}

 	@RequestMapping(value ="/createUser")
	public String createUser(@Valid @ModelAttribute("login") Login login, BindingResult bindingResult) {
 		if (bindingResult.hasErrors()) {
 	 		return "addUser";
 		}
 		
 		login.setPassword(login.getPassword().replaceAll(",", ""));
 		login.setRole("USER");
 		loginService.save(login);
 		return "redirect:login";
	}

 	
 	
 	
}