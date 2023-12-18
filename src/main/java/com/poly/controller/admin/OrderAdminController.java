package com.poly.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.poly.dao.OrderDao;
import com.poly.dao.OrderDetailDao;
import com.poly.dao.ProductDao;
import com.poly.entity.Order;
import com.poly.entity.OrderDetail;
import com.poly.entity.Product;

@Controller
public class OrderAdminController {
	@Autowired
	OrderDao odao;

	@Autowired
	OrderDetailDao otddao;

	@Autowired
	ProductDao prodao;
	@Autowired
	JavaMailSender javaMailSender;

	// Hiển thị danh sách đơn hàng
	@GetMapping("/admin/order/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("orderlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/admin/order/list/page/1";
	}

	// Phân trang danh sách đơn hàng
	@GetMapping("/admin/order/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		List<Order> list = (List<Order>) odao.findByAllDesc();
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
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";
	}

	// @RequestMapping("/order/list")
	// public String index(Model model){
	// List<Order> list = odao.findByAllDesc();
	// model.addAttribute("orders", list);
	// return "admin/order/list";
	// }
	// Tìm kiếm sản phẩm theo Id
	@GetMapping("/admin/order/findbyOrderId")
	public String search(@RequestParam("order_id") String order_id, Model model) {
		if (order_id.equals("")) {
			return "redirect:/admin/order/list";
		}
		model.addAttribute("items", odao.findByOrder_Id(order_id));
		return "list";
	}

	// Phân trang tìm kiếm sản phẩm theo Id
	@RequestMapping("/admin/order/findbyOrderId/{pageNumber}")
	public String findIdorName(Model model, @RequestParam("order_id") String order_id, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (order_id.equals("")) {
			return "redirect:/admin/order/list";
		}
		List<Order> list = odao.findByOrder_Id(order_id);
		if (list == null) {
			return "redirect:/admin/order/list";
		}
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";
	}

	// @RequestMapping("/order/findbyOrderId")
	// public String findbyOrderId(Model model , @RequestParam("order_id") String
	// order_id) {
	// try {
	// List<Order> list = odao.findByOrder_Id(order_id);
	// if(list.size() == 0) {
	// model.addAttribute("message", "ID HOẶC TÊN SẢN PHẨM không tồn tại ");
	// return "admin/order/list";
	// }
	// else {
	// model.addAttribute("orders", list);
	// model.addAttribute("message", "Tìm kiếm thành công ");
	// return "admin/order/list";
	// }
	// } catch (Exception e) {
	// model.addAttribute("message", "ID HOẶC TÊN SẢN PHẨM không tồn tại ");
	// return "admin/order/list";
	// }
	//
	// }
	//

	@RequestMapping("/admin/order/findallkeyword")
	public String findallkeyword(Model model) {
		List<Order> list = odao.findAll();
		model.addAttribute("items", list);
		return "list";
	}

	// Tìm kiếm theo dữ liệu đã chọn
	@SuppressWarnings("deprecation")
	@RequestMapping("/admin/order/findByAllkeyword/{pageNumber}")
	public String findByAllkeyword(Model model, @RequestParam("Username") String username,
			@RequestParam(value = "Minday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date minday,
			@RequestParam(value = "Maxday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxday,
			@RequestParam("Status") String Status, @RequestParam("Phone") String Phone,
			@RequestParam("MinPrice") Integer unit_price, @RequestParam("MaxPrice") Integer unit_price1,
			HttpServletRequest request, @PathVariable int pageNumber) {
		if (minday == (null)) {
			minday = new Date(2001 - 01 - 23);
		}
		if (maxday == (null)) {
			maxday = new Date();
		}

		List<Order> list = odao.findByAllKeyWord(username, minday, maxday, Status, Phone, unit_price, unit_price1);
		model.addAttribute("orders", list);
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";

	}

	// Hiển thị thông tin đơn hàng cần chỉnh sửa
	@RequestMapping("/admin/order/edit")
	public String orderDetail(Model model, @RequestParam("order_id") Integer order_id) {

		Order ListOrder = odao.findById(order_id).get();
		List<OrderDetail> ListOrderDetail = otddao.findByOrderID(order_id);
		model.addAttribute("ord", ListOrder);
		model.addAttribute("odetail", ListOrderDetail);
		model.addAttribute("message", "Thao tác thành công");
		return "admin/order/edit";

	}

	// Xóa đơn hàng
	@RequestMapping("/admin/order/delete/{order_id}")
	public String deleteOrder_Id(Model model, @PathVariable("order_id") Integer order_id) {
		otddao.deleteOrderId(order_id);
		odao.deleteById(order_id);
		return "admin/order/list";
	}

	// Xóa đơn hàng trên form
	@RequestMapping("/admin/order/delete/form/{order_id}")
	public String deleteformOrder_Id(Model model, @PathVariable("order_id") Integer order_id) {
		otddao.deleteOrderId(order_id);
		odao.deleteById(order_id);
		return "admin/order/list";
	}

	// Cập nhật đơn hàng
	@RequestMapping("/admin/order/update")
	public String updateOrder(Model model, Order ord) {
		if (odao.existsById(ord.getOrder_id())) {
			if (ord.getStatus() == 1) {

				List<OrderDetail> listOrder = otddao.findByOrderID(ord.getOrder_id());
				for (int i = 0; i < listOrder.size(); i++) {
					Optional<Product> pro = prodao.findById(listOrder.get(i).getProduct().getProduct_id());
					pro.orElseThrow().setQuantity(pro.get().getQuantity() - listOrder.get(i).getQuantity());
					prodao.save(pro.get());
				}
			}

			if (ord.getStatus() == 2) {

				List<OrderDetail> listOrder = otddao.findByOrderID(ord.getOrder_id());
				for (int i = 0; i < listOrder.size(); i++) {
					Optional<Product> pro = prodao.findById(listOrder.get(i).getProduct().getProduct_id());
					pro.orElseThrow().setQuantity(pro.get().getQuantity() + listOrder.get(i).getQuantity());
					prodao.save(pro.get());
				}
			}
			if (ord.getStatus() == 3) {
				ord.setDescription("Đã thanh toán");

			}
			model.addAttribute("message", "Thao tác thành công");

			odao.save(ord);
			return "redirect:/admin/order/edit?order_id=" + ord.getOrder_id();
		} else {
			model.addAttribute("message", "Thao tác thất bại");
			return "redirect:/admin/order/edit?order_id=" + ord.getOrder_id();
		}

	}
	
	// Gửi mail thông báo cho khách
	@RequestMapping("/admin/order/sent/{order_id}")
	public String sendMail(Model model, @PathVariable("order_id") Integer order_id) {

		String sta = "Đang sử lý";
		Order order = odao.findById(order_id).get();

		// định dạng số tiền
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		String formattedPrice = numberFormat.format(order.getPrice());
		// định dạng ngày
		Date createDate = order.getCreateDate(); 
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String formattedDate = formatter.format(createDate);

		if (order.getStatus() == 1) {
			sta = "Đang giao hàng";
		}
		if (order.getStatus() == 2) {
			sta = "Hủy giao hàng";
		}
		if (order.getStatus() == 3) {
			sta = "Hoàn thành đơn hàng";
		}
		if (order.getStatus() == 4) {
			sta = "Đã hủy";
		}
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(order.getAccount().getEmail());
		msg.setText(
				"Cảm ơn Quý khách đã sử dụng dịch vụ Black Bear.\n" + 
						"" +
				"Black Bear xin thông báo giao dịch của Quý khách đã được thực hiện như sau:\n" + 
						
			 "\n"+ "\n"+
				"Tên người đặt hàng : "
						+ order.getAccount().getFullname()
						+ "\n"
						+ "Phương thức thanh toán :  "
						+ order.getMethod()
						+ "\n"
						+ "Số điện thoại người đặt hàng : "
						+ order.getPhone()
						+ "\n"
						+ "Đơn hàng có giá trị :"
						+ formattedPrice
						+ "\n"
						+ "Loại tiền tệ : "
						+ order.getCurrency()
						+ "\n"

						+ "Ngày tạo đơn : "
						+ formattedDate
						+ "\n"

						+ "Tình trạng đơn hàng : "
						+ sta
						+ "\n"
						+ "Địa chỉ nhận hàng : "
						+ order.getAddress()
						+ "\n"
						+ "\n"
						+ "Cảm ơn bạn đã mua hàng tại BlackBear : "
						+ "\n"
						+ "================================");

		msg.setSubject("Đơn hàng số : " + order.getOrder_id());
		javaMailSender.send(msg);
		model.addAttribute("message", "Gửi mail thành công");
		return "redirect:/admin/order/edit?order_id=" + order_id;
	}
}
