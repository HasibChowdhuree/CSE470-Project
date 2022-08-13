package com.gadgetmonkey.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import com.gadgetmonkey.dao.CustomerRepository;
import com.gadgetmonkey.dao.ProductsRepository;
import com.gadgetmonkey.dao.ShopRepository;
import com.gadgetmonkey.dao.UserRepository;
import com.gadgetmonkey.entities.Customer;
import com.gadgetmonkey.entities.Product;
import com.gadgetmonkey.utilities.Cart;
import com.gadgetmonkey.utilities.Pair;

import org.springframework.ui.Model;

@Controller
public class CartController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    // @Autowired
    // private CartRepository cartRepository;
    public String get_context_path(String url){
        String[] url_splitted = url.split("/");
        String context = "/";
        for(int i=3;i<url_splitted.length;i++)
            context+=url_splitted[i]+"/";
        return context;
    }
    @GetMapping("/add-to-cart/{id}")
    public RedirectView add_to_cart(@PathVariable int id, Model model, Principal principal, HttpSession session, HttpServletRequest request){
        Cart cart = (Cart) session.getAttribute("cart");
        Product product = productsRepository.getReferenceById(id);
        String context = get_context_path(request.getHeader("referer"));
        if(cart==null){
            cart = new Cart(); 
            List<Pair> pairs = new ArrayList<>();
            Pair pair = new Pair();
            pair.setProduct(product);
            pair.setQuantity(1);
            pairs.add(pair);
            cart.setProducts(pairs);
            // cart.setProducts(prd);
            cart.calculateTotal();
            cart.getQuantity();
            session.setAttribute("cart", cart);
            // cart.setProducts(new ArrayList<Product>());  
        }
        else{
            // cart.getProducts().add(product);
            boolean found = false;
            for (Pair pair : cart.getProducts()) {
                if(pair.getProduct().getId()==product.getId()){
                    pair.setQuantity(pair.getQuantity()+1);
                    found = true;
                    break;
                }
            }
            if(found==false){
                Pair pair = new Pair();
                pair.setProduct(product);
                pair.setQuantity(1);
                cart.getProducts().add(pair);
            }
            cart.calculateTotal();
            cart.getQuantity();
            
        }
        session.setAttribute("cart", cart);
        // cartRepository.save(cart);
        // System.out.println(cart.getProducts().size());
        return new RedirectView(context);
    }
    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        if(principal!=null){
            String email = principal.getName();
            Customer customer = customerRepository.getUserByEmail(email);
            model.addAttribute("user", customer);
        }
        model.addAttribute("cart", cart);
        if(cart!=null){
            cart.calculateTotal();
            List<Pair> pairs = cart.getProducts();
            model.addAttribute("pairs", pairs);
        }
       
        model.addAttribute("title", "carts");
        return "cart";
    }
    @GetMapping("/delete-from-cart/{id}")
    public RedirectView delete_from_cart(@PathVariable int id,Model model, Principal principal, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        List<Pair> pairs = cart.getProducts();
        for(int i=0;i<pairs.size();i++){
            if(pairs.get(i).getProduct().getId()==id){
                pairs.get(i).setProduct(null);
                pairs.remove(i);
                // break;
            }
        }
        pairs = cart.getProducts();
        // List<Product> products = new ArrayList<>();
        // for(Pair pair: cart.getProducts()){
        //     products.add(pair.getProduct());
        // }
        cart.calculateTotal();
        model.addAttribute("cart", cart);
        // model.addAttribute("pairs", pairs);
        // model.addAttribute("title", "carts");
        return new RedirectView("/cart");
    }
    @GetMapping(value=("/increment-quantity/{id}"))
	public RedirectView increment_quantity(@PathVariable int id,Model model, Principal principal, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        for(Pair pair: cart.getProducts()){
            if(pair.getProduct().getId()==id){
                pair.setQuantity(pair.getQuantity()+1);
            }
        }
        cart.calculateTotal();
        session.setAttribute("cart", cart);
        return new RedirectView("/cart");
    }
    @GetMapping(value=("/decrement-quantity/{id}"))
	public RedirectView decrement_quantity(@PathVariable int id,Model model, Principal principal, HttpSession session){
        Cart cart = (Cart) session.getAttribute("cart");
        for(Pair pair: cart.getProducts()){
            if(pair.getProduct().getId()==id){
                if(pair.getQuantity()==1){
                    cart.getProducts().remove(pair);
                    session.setAttribute("cart", cart);
                    return new RedirectView("/cart");
                }
                else
                    pair.setQuantity(pair.getQuantity()-1);
            }
        }
        cart.calculateTotal();
        session.setAttribute("cart", cart);
        return new RedirectView("/cart");
    }
}