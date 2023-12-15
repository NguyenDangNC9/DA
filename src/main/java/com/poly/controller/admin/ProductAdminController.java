package com.poly.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.AccountDao;
import com.poly.dao.CategoryDao;
import com.poly.dao.ProductDao;
import com.poly.dao.TrademarkDao;
import com.poly.entity.Account;
import com.poly.entity.Category;
import com.poly.entity.Product;
import com.poly.entity.Trademark;
import com.poly.service.AccountService;
import com.poly.service.ProductService;

@Controller
public class ProductAdminController {
	@Autowired
	ProductDao dao;

	@Autowired
	ProductService proservice;

	@Autowired
	CategoryDao catedao;

	@Autowired
	TrademarkDao tradao;

	// @RequestMapping({"/"})
	// public String home() {
	// return "redirect:/product/list";
	// }
	// Code load trang đơn hàng admin
	@GetMapping("/admin/product/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("productlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/admin/product/list/page/1";
	}

	// Phân trang hiện tại
	@GetMapping("/admin/product/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 10;
		List<Product> list = (List<Product>) proservice.findAll();

		model.addAttribute("sizepro", list.size());

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
		String baseUrl = "/admin/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		// laay dữ lieeju pan trang
		return "/admin/product/list";
	}

	// Tìm kiếm sản phẩm theo tên và id
	@GetMapping("/admin/product/findIdorName")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword.equals("")) {
			return "redirect:/admin/product/list";
		}
		model.addAttribute("items", dao.finbyIdOrName(keyword));
		model.addAttribute("message", "Tìm kiếm thành công");
		return "list";
	}

	// Phân trang tìm kiếm sản phẩm theo tên và id
	@RequestMapping("/admin/product/findIdorName/{pageNumber}")
	public String findIdorName(Model model, @RequestParam("keyword") String keyword, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keyword.equals("")) {
			return "redirect:/admin/product/list";
		}
		List<Product> list = dao.finbyIdOrName(keyword);

		if (list == null) {
			return "redirect:/admin/product/list";
		}
		model.addAttribute("message", "Tìm kiếm thành công");
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 10;
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
		String baseUrl = "/admin/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/product/list";
	}

	// Tìm kiếm nâng cao
	@RequestMapping("/admin/product/findallkeyword")
	public String findallkeyword(Model model) {
		List<Product> list = dao.findAll();
		model.addAttribute("items", list);
		return "list";
	}

	// Phân trang tìm kiếm nâng cao
	@RequestMapping("/admin/product/findallkeyword/{pageNumber}")
	public String findallkeyword(Model model, @RequestParam("Category_id") String Category_id,
			@RequestParam("Trademark_id") String Trademark_id, @RequestParam("Status") String Status,
			@RequestParam("Color") String Color, @RequestParam("Material") String Material,
			@RequestParam("Size") String Size, @RequestParam("MinPrice") Integer unit_price,
			@RequestParam("MaxPrice") Integer unit_price1, HttpServletRequest request, @PathVariable int pageNumber) {

		List<Product> list = dao.findByAllKeyWordAdmin(unit_price, unit_price1, Category_id, Trademark_id, Status,
				Color, Material, Size);
		model.addAttribute("items.pageList", list);
		model.addAttribute("sizepro", list.size());
		model.addAttribute("message", "Tìm kiếm thành công");
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 10;
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
		String baseUrl = "/admin/product/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/product/list";
	}

	// Làm mới chỉnh sửa sản phẩm
	@RequestMapping("/admin/product/detail")
	public String detail(Model model) {
		Product pro = new Product();
		pro.setName("");
		// pro.setChip(null);
		pro.setImage1("");
		pro.setImage2("");
		pro.setImage3("");
		// pro.setImage4("");
		// pro.setImage5("");
		// pro.setImage6("");
		// pro.setImage7("");
		// pro.setChip(null);
		pro.setDetail("");
		pro.setDescription("");
		pro.setDistcount(0.0);
		// pro.setPin(null);
		pro.setUnit_price(0.0);
		pro.setStatus(null);
		pro.setSpecial(null);
		pro.setMaterial(null);
		pro.setSize("");
		pro.setColor("");
		pro.setProduct_date(new Date());
		pro.setLastest(null);
		List<Category> cateList = catedao.findAll();
		List<Trademark> tradeList = tradao.findAll();
		model.addAttribute("cate", cateList);
		model.addAttribute("trade", tradeList);
		model.addAttribute("pro", pro);
		model.addAttribute("message", "Làm mới thành công");
		return "admin/product/detail";
	}

	// Hiển thị thông tin sản phẩm cần chỉnh sửa
	@RequestMapping("/admin/product/edit")
	public String edit(Model model, @RequestParam("product_id") Integer product_id) {
		try {
			Product pro = dao.findById(product_id).get();
			// List<Category> cateList = catedao.findAll();
			// List<Trademark> tradeList = tradao.findAll();
			model.addAttribute("pro", pro);
			// model.addAttribute("cate", cateList);
			// model.addAttribute("trade", tradeList);
			model.addAttribute("message", "Thao tác thành công");
			return "admin/product/edit";
		} catch (Exception e) {
			model.addAttribute("message", "Thao tác thất bại");
			return "admin/product/edit";
		}
	}

	// Xóa sản phẩm
	@RequestMapping("/admin/product/delete/{id}")
	public String delete(Model model, @PathVariable("id") Integer id) {
		try {
			dao.deleteById(id);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/product/list";
		} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message", "Sản phẩm đang có trong 1 đơn hàng");
			return "redirect:/admin/product/list";
		}

	}

	// Thêm sản phẩm
	@PostMapping("/admin/product/create")
	public String create(Product pro, Model model, @RequestParam("photo_img") MultipartFile[] file) throws IOException {
		model.addAttribute("pro", pro);
		if (file != null) {
			for (int i = 0; i < file.length; i++) {
				if (i == 0) {
					if (file[i].getOriginalFilename().equals("")) {
						break;
					} else {
						File saveFile = proservice.save(file[i], "/assets/images");
						pro.setImage1(file[i].getOriginalFilename());
					}
				}
				;
				if (i == 1) {
					File saveFile = proservice.save(file[i], "/assets/images");
					pro.setImage2(file[i].getOriginalFilename());
				}
				;
				if (i == 2) {
					File saveFile = proservice.save(file[i], "/assets/images");
					pro.setImage3(file[i].getOriginalFilename());
				}
				;
				// if (i == 3) {
				// File saveFile = proservice.save(file[i], "/assets/images");
				// pro.setImage4(file[i].getOriginalFilename());
				// }
				// ;
				// if (i == 4) {
				// File saveFile = proservice.save(file[i], "/assets/images");
				// pro.setImage5(file[i].getOriginalFilename());
				// }
				// ;
				// if (i == 5) {
				// File saveFile = proservice.save(file[i], "/assets/images");
				// pro.setImage6(file[i].getOriginalFilename());
				// }
				// ;
				// if (i == 6) {
				// File saveFile = proservice.save(file[i], "/assets/images");
				// pro.setImage7(file[i].getOriginalFilename());
				// }
				// ;
			}
			pro.setProduct_date(new Date());
			dao.save(pro);
			model.addAttribute("message", "Thêm mới thành công");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		} else {
			dao.save(pro);
			model.addAttribute("message", "Thêm mới thành công");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		}
	}

	// @RequestMapping("product/update")
	// public String update(Product pro, Model model, @RequestParam("photo_img1")
	// MultipartFile file1) throws IOException {
	// model.addAttribute("pro", pro);
	// if (dao.existsById(pro.getProduct_id())) {
	// File saveFile1 = proservice.save(file1 , "/assets/images");
	// String filename1 = file1.getOriginalFilename();
	// pro.setProduct_date(new Date());
	// if (filename1 == "") {
	// dao.save(pro);
	// model.addAttribute("message", "Cập nhập thành công");
	// return "redirect:/product/edit?product_id=" + pro.getProduct_id();
	// } else {
	// pro.setImage1(filename1);
	// dao.save(pro);
	// model.addAttribute("message", "Cập nhập thành công");
	// return "redirect:/product/edit?product_id=" + pro.getProduct_id();
	// }
	// } else {
	// model.addAttribute("message", "Cập nhập thất bại");
	// return "redirect:/product/edit?product_id=" + pro.getProduct_id();
	// }
	// }

	// Chỉnh sửa thông tin sản phẩm
	@RequestMapping("/admin/product/update")
	public String update(Product pro, Model model, @RequestParam("photo_img") MultipartFile[] file) throws IOException {
		model.addAttribute("pro", pro);
		if (dao.existsById(pro.getProduct_id())) {
			if (file != null) {
				for (int i = 0; i < file.length; i++) {
					if (i == 0) {
						if (file[i].getOriginalFilename().equals("")) {
							break;
						} else {
							File saveFile = proservice.save(file[i], "/assets/images");
							pro.setImage1(file[i].getOriginalFilename());
						}
					}
					;
					if (i == 1) {
						File saveFile = proservice.save(file[i], "/assets/images");
						pro.setImage2(file[i].getOriginalFilename());
					}
					;
					if (i == 2) {
						File saveFile = proservice.save(file[i], "/assets/images");
						pro.setImage3(file[i].getOriginalFilename());
					}
					;
					// if (i == 3) {
					// File saveFile = proservice.save(file[i], "/assets/images");
					// pro.setImage4(file[i].getOriginalFilename());
					// }
					// ;
					// if (i == 4) {
					// File saveFile = proservice.save(file[i], "/assets/images");
					// pro.setImage5(file[i].getOriginalFilename());
					// }
					// ;
					// if (i == 5) {
					// File saveFile = proservice.save(file[i], "/assets/images");
					// pro.setImage6(file[i].getOriginalFilename());
					// }
					// ;
					// if (i == 6) {
					// File saveFile = proservice.save(file[i], "/assets/images");
					// pro.setImage7(file[i].getOriginalFilename());
					// }
					// ;
				}
				// File saveFile1 = proservice.save(file1 , "/assets/images");
				// String filename1 = file1.getOriginalFilename();
				pro.setProduct_date(new Date());
				dao.save(pro);
				model.addAttribute("message", "Cập nhập thành công");
				return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
			} else {
				dao.save(pro);
				model.addAttribute("message", "Cập nhập thành công");
				return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
			}
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		}
	}

	// Xóa sản phẩm trên form
	@RequestMapping("/admin/product/delete/form/{id}")
	public String deleteform(Model model, @PathVariable("id") Integer id) {
		try {
			dao.deleteById(id);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/product/detail";
		} catch (Exception e) {
			model.addAttribute("message", "Xoá thất bại");
			return "redirect:/admin/product/detail";
		}

	}

}
