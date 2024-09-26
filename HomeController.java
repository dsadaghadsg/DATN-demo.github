package com.phat.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Cần import này
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.phat.dao.CustomerDAO;
import com.phat.dao.ProductDAO;
import com.phat.dao.UserDAO;
import com.phat.entity.Customer;
import com.phat.entity.Product;
import com.phat.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
//controller cung cấp thông tin người dùng, xử lý yêu cầu người dùng
// userdao là quản lý truy vấn dữ liệu của sql
//user entity là cấu trúc bảng Users trong sql 
//CustomUserDetailsService lấy email làm tên người dùng

@Controller
public class HomeController {
	  @Autowired
	    private UserDAO userDAO;
	  @Autowired
	    private CustomerDAO customerdao;

	@GetMapping("/home/index")
	public String index(Model model, HttpSession session) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // chứa thông tin người dùng hiện tại
	    
	    if (authentication != null && authentication.isAuthenticated()) {
	        // Lấy tên người dùng (email)
	        String username = authentication.getName(); // trả về tên người dùng là email
	        model.addAttribute("username", username);

	        // Lấy danh sách vai trò
	        List<String> roles = authentication.getAuthorities().stream() // lấy danh sách quyền của người dùng
	            .map(GrantedAuthority::getAuthority)
	            .collect(Collectors.toList());

	        model.addAttribute("roles", roles); // thêm các thuộc tính vào mô hình
	        session.setAttribute("roles", roles); // Lưu trữ vai trò trong session


	        // Kiểm tra vai trò admin
	        boolean isAdmin = roles.contains("ROLE_ADMIN");
	        model.addAttribute("isAdmin", isAdmin);
	    } else {
	        // Nếu không có principal, đảm bảo trang vẫn được trả về và không có thông tin người dùng
	        model.addAttribute("username", "Guest");
	        model.addAttribute("roles", List.of("ROLE_ANONYMOUS"));
	        model.addAttribute("isAdmin", false);
	    }
	 // Nhận thông báo từ RedirectAttributes
	    if (model.containsAttribute("message")) {
	        model.addAttribute("message", model.getAttribute("message"));
	        model.addAttribute("role", model.getAttribute("role"));
	    }

	    return "home/index";
	}


@GetMapping("/index")
public String quaylai(Model model, HttpSession session) {
	 // Lấy vai trò từ session
    List<String> roles = (List<String>) session.getAttribute("roles");
    model.addAttribute("roles", roles);

    // Kiểm tra nếu vai trò là admin
    boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");
    model.addAttribute("isAdmin", isAdmin);
	return "home/index";
}
@GetMapping("/admin")
public String admin() {
	return "home/admin";
}
//@Autowired
//private ProductDAO productDAO;

//@GetMapping("/sanpham")
//public String sanpham(Model model, HttpSession session) {
//	 // Lấy vai trò từ session
//    List<String> roles = (List<String>) session.getAttribute("roles");
//    model.addAttribute("roles", roles);
//
//    // Kiểm tra nếu vai trò là admin
//    boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");
//    model.addAttribute("isAdmin", isAdmin);
//    // Lấy sản phẩm từ ID 39 đến 74
//    List<Product> products = productDAO.findByProductIdBetween(150, 185);
//    model.addAttribute("products", products); // thêm danh sách vào sp vào đối tượng model
//    //dữ liệu này được sử dụng tromng view để hiển thị sản phẩm  
//    return "home/sanpham"; // Đảm bảo tên template đúng
//}

@GetMapping("/home/category")
public String category() {
	return "home/category";
}

@GetMapping("/logout")
public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
    // Xóa session và các thông tin liên quan
    session.invalidate();
    return "redirect:/home/index";
}
//@GetMapping("/register")
//public String showRegisterForm(Model model) {
//    model.addAttribute("customer", new Customer()); // Đổi thành Customer
//    return "auth/register"; // Tên file .html trong templates
//}
//@PostMapping("/register")
//public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
//    // Mã hóa mật khẩu
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//    user.setPassword(encoder.encode(user.getPassword()));
//    
//    // Lưu người dùng với vai trò mặc định là khách hàng
//    user.setAdmin(false); // false = USER (Khách hàng)
//    userDAO.save(user);
//   
//   
//    // Thêm thông báo và vai trò vào RedirectAttributes
//    redirectAttributes.addFlashAttribute("message", "Đăng ký thành công!");
//    redirectAttributes.addFlashAttribute("role", "Khách hàng"); // Thêm vai trò vào mô hình
//
//    return "redirect:/home/index"; // Đảm bảo rằng đường dẫn này đúng với cấu trúc của bạn
//}
//@GetMapping("/ctsp")
//public String Ctsp() {
//	return "home/ctsp";
//}
//@GetMapping("/ctsp/{id}")
//public String ctsp(@PathVariable("id") Integer id, Model model, HttpSession session) {
//	 // Lấy vai trò từ session
//    List<String> roles = (List<String>) session.getAttribute("roles");
//    model.addAttribute("roles", roles);
//
//    // Kiểm tra nếu vai trò là admin
//    boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");
//    model.addAttribute("isAdmin", isAdmin);
//   // Lấy sản phẩm từ cơ sở dữ liệu dựa trên ID
//   Product product = productDAO.findById(id).orElse(null);
//
//
//   // Nếu sản phẩm không tồn tại, bạn có thể chuyển hướng đến một trang lỗi hoặc trang không tìm thấy
//   if (product == null) {
//       return "error/404"; // Hoặc một trang lỗi phù hợp
//   }
//   List<Product> products = productDAO.findByProductIdBetween(150, 161);
//
//   // Đưa sản phẩm vào model để hiển thị trên trang
//   model.addAttribute("product", product);
//   model.addAttribute("products", products); // thêm danh sách vào sp vào đối tượng model
//
//   return "home/ctsp"; // Tên của trang HTML (Thymeleaf template) bạn muốn hiển thị
//}
}
