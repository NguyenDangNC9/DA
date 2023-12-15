package com.poly.controller.user;

import java.util.List;
import java.util.Optional;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.FavoriteDao;
import com.poly.dao.ProductDao;
import com.poly.dao.VoteDao;
import com.poly.entity.Favorite;
import com.poly.entity.Product;
import com.poly.entity.Vote;
import com.poly.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Controller
public class ProductController {
	@Autowired
	ProductService productService;
	@Autowired
	ProductDao pdao;
	@Autowired
	HttpServletRequest request;
	@Autowired
	FavoriteDao  fadao;
	@Autowired
	VoteDao votedao;
	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
    private ApplicationContext applicationContext;

//	@RequestMapping({ "/" })
//	public String home() {
//		return "redirect:/product/list";
//	}

	@GetMapping("/product/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("productlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/product/list/page/1";
	}

	@GetMapping("/product/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		List<Product> list = (List<Product>) productService.findAll();
		model.addAttribute("sizepro", productService.findAll().size());
		
		if (pages == null) {
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}


	@RequestMapping("/product/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id) {
		double vote_arg = 0;
		Product item = productService.findById(id);
		String username= request.getRemoteUser();
		List<Vote> VoteList = votedao.findbyProductId(id);
		if(VoteList.size()!=0) {
		for(int i=0 ; i < VoteList.size() ; i++) {
			vote_arg +=VoteList.get(i).getVote(); 
		}
		model.addAttribute("vote_arage",vote_arg/VoteList.size());
		}
		else {
		model.addAttribute("vote_arage",0);
		}
		model.addAttribute("votes", VoteList);
		model.addAttribute("votesize", VoteList.size());
		
		
		List<Favorite> listcheck = fadao.checkFavaritesAdmin(id, username);
		model.addAttribute("checklist", listcheck.size());
		model.addAttribute("item", item);
		return "user/product/detail";
	}


	@RequestMapping("/product/discount/{pageNumber}")
	public String discount(Model model, @RequestParam("cid") Optional<Integer> cid,
			HttpServletRequest request,
			@PathVariable int pageNumber) {
		
			List<Product> list = pdao.findByDis();
			model.addAttribute("items", list);

			PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
			int pagesize = 4;
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
			request.getSession().setAttribute("productlist", pages);
			int current = pages.getPage() + 1;
			int begin = Math.max(1, current - list.size());
			int end = Math.min(begin + 5, pages.getPageCount());
			int totalPageCount = pages.getPageCount();
			String baseUrl = "/product/discount/";
			model.addAttribute("beginIndex", begin);
			model.addAttribute("endIndex", end);
			model.addAttribute("currentIndex", current);
			model.addAttribute("totalPageCount", totalPageCount);
			model.addAttribute("baseUrl", baseUrl);
			model.addAttribute("items", pages);

		return "user/product/discount";
	}

	@RequestMapping("/product/latest/{pageNumber}")
	public String latest(Model model, @RequestParam("cid") Optional<Integer> cid,HttpServletRequest request,
			@PathVariable int pageNumber) {
	
			List<Product> list = pdao.findByLatest();
			model.addAttribute("items", list);
		
			PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
			int pagesize = 4;
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
			request.getSession().setAttribute("productlist", pages);
			int current = pages.getPage() + 1;
			int begin = Math.max(1, current - list.size());
			int end = Math.min(begin + 5, pages.getPageCount());
			int totalPageCount = pages.getPageCount();
			String baseUrl = "/product/latest/";
			model.addAttribute("beginIndex", begin);
			model.addAttribute("endIndex", end);
			model.addAttribute("currentIndex", current);
			model.addAttribute("totalPageCount", totalPageCount);
			model.addAttribute("baseUrl", baseUrl);
			model.addAttribute("items", pages);
		return "user/product/latest";
	}

	@RequestMapping("/product/special/{pageNumber}")
	public String special(Model model, @RequestParam("cid") Optional<Integer> cid,
			HttpServletRequest request,
			@PathVariable int pageNumber) {
		
			List<Product> list = pdao.findBySpecial();
			model.addAttribute("items", list);

			PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
			int pagesize = 4;
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
			request.getSession().setAttribute("productlist", pages);
			int current = pages.getPage() + 1;
			int begin = Math.max(1, current - list.size());
			int end = Math.min(begin + 5, pages.getPageCount());
			int totalPageCount = pages.getPageCount();
			String baseUrl = "/product/special/";
			model.addAttribute("beginIndex", begin);
			model.addAttribute("endIndex", end);
			model.addAttribute("currentIndex", current);
			model.addAttribute("totalPageCount", totalPageCount);
			model.addAttribute("baseUrl", baseUrl);
			model.addAttribute("items", pages);
		
		return "user/product/special";
	}

	@GetMapping("/product/list/search")
	public String search(@RequestParam("keywords") String keywords, Model model) {
		if (keywords.equals("")) {
			return "redirect:/product/list";
		}
		model.addAttribute("items", productService.findByKeywords(keywords));
		return "list";
	}

	@GetMapping("/product/list/search/{pageNumber}")
	public String search(@RequestParam("keywords") String keywords, Model model, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keywords.equals("")) {
			return "redirect:/product/list";
		}
		List<Product> list = productService.findByKeywords(keywords);
		if (list == null) {
			return "redirect:/product/list";
		}
		model.addAttribute("sizepro", productService.findByKeywords(keywords).size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}

	@RequestMapping("product/list/find")
	public String find(Model model) {
		List<Product> list = pdao.findAll();
		model.addAttribute("items", list);
		return "list";
	}

	@RequestMapping("/product/list/find/{pageNumber}")
	public String find(Model model, @RequestParam("Category_id") String Category_id, @RequestParam("Trademark_id") String Trademark_id,
			@RequestParam("Color") String Color, @RequestParam("Material") String Material, @RequestParam("Size") String Size,
			@RequestParam("MinPrice") Integer unit_price, @RequestParam("MaxPrice") Integer unit_price1, HttpServletRequest request,
			@PathVariable int pageNumber) {
		List<Product> list = pdao.findByAllKeyWord(unit_price, unit_price1, Category_id, Trademark_id,Color, Material, Size);
		model.addAttribute("items.pageList", list);
		
		model.addAttribute("sizepro", pdao.findByAllKeyWord(unit_price, unit_price1, Category_id, Trademark_id, Color, Material, Size).size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";

	}


	@GetMapping("/product/list/findByTrademarkId")
	public String trademark(@RequestParam("tid") Integer tid, Model model) {
		if (tid.equals("")) {
			return "redirect:/product/list";
		}
		model.addAttribute("items", productService.findByTrademarkId(tid));
		model.addAttribute("sizepro", productService.findByTrademarkId(tid).size());
		
		return "list";
	}

	@GetMapping("/product/list/findByTrademarkId/{pageNumber}")
	public String trademark(@RequestParam("tid") Integer tid, Model model, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (tid.equals("")) {
			return "redirect:/product/list";
		}
		List<Product> list = productService.findByTrademarkId(tid);
		if (list == null) {
			return "redirect:/product/list";
		}
		model.addAttribute("sizepro", productService.findByTrademarkId(tid).size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}

	@GetMapping("/product/list/top10/{pageNumber}")
	public String getTop10(Model model, HttpServletRequest request, @PathVariable int pageNumber) {

		List<Product> list = pdao.getTop10();
		if (list == null) {
			return "redirect:/product/list";
		}
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/top10/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}
	
	@GetMapping("/product/list/desc/{pageNumber}")
	public String getDesc(Model model, HttpServletRequest request, @PathVariable int pageNumber) {
		List<Product> list = pdao.getDesc();
		model.addAttribute("sizepro", pdao.getDesc().size());
		if (list == null) {
			return "redirect:/product/list";
		}
		model.addAttribute("sizepro", pdao.getDesc().size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/desc/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}
	
	@GetMapping("/product/list/asc/{pageNumber}")
	public String getAsc(Model model, HttpServletRequest request, @PathVariable int pageNumber) {
		List<Product> list = pdao.getAsc();
		model.addAttribute("sizepro", pdao.getAsc().size());
		if (list == null) {
			return "redirect:/product/list";
		}
		model.addAttribute("sizepro", pdao.getAsc().size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/product/list/asc/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "user/product/list";
	}
	

	// @RequestMapping("/send1")
	// public String sendMailShare(HttpServletRequest request, @RequestParam("id") Integer id, @RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("content") String content) {
	// 	SimpleMailMessage msg = new SimpleMailMessage();
	// 	String url = request.getRequestURL().toString().replace("send1", "product/detail/" + id);
	// 	msg.setTo(to);
	// 	msg.setText(content + "'" + url + "'");
	// 	msg.setSubject(subject);
	// 	javaMailSender.send(msg);
	// 	return "user/result";
	
	// }
	@RequestMapping("/send1")
public String sendMailShare(@RequestParam("id") Integer id, @RequestParam("to") String to, @RequestParam("subject") String subject) {
    MimeMessage message = javaMailSender.createMimeMessage();

    try {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String url = "http://localhost:8080/product/detail/" + id;
        String emailContent = "<html><head> <meta charset='UTF-8'> </head><body>" +
		"<p style='font-family: Arial, sans-serif;'>Đây là một ví dụ về chữ tiếng Việt.</p>" +
                                "<p>Xin chào,</p>" +
                                "<p>Hãy nhấp vào liên kết bên dưới để xem chi tiết sản phẩm:</p>" +
                                "<p><a href='" + url + "'>Chi tiết sản phẩm</a></p>" +
                                "<p>Xin cảm ơn!</p>" +
                              "</body></html>";

        helper.setTo(to);
        helper.setText(emailContent, true);
        helper.setSubject(subject);
        javaMailSender.send(message);
        return "user/result";
    } catch (Exception e) {
        return "error";
    }
}


	
	
	
	@RequestMapping("/contact-us")
	public String contact(Model model) {

		return "user/contact/contact_us";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {

		return "user/contact/about";
	}
	

	// thanh toán thành công vnpay
		@RequestMapping("/product/success")
	public String index1(Model model) {
		return "user/product/success";
	}

	@RequestMapping("/product/cancel")
	public String index2(Model model) {
		return "user/product/cancel";
	}
}