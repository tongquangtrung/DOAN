package com.nongsan.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nongsan.dto.CartRequest;
import com.nongsan.entity.Cart;
import com.nongsan.entity.CartDetail;
import com.nongsan.entity.Order;
import com.nongsan.entity.OrderDetail;
import com.nongsan.entity.Product;
import com.nongsan.repository.CartDetailRepository;
import com.nongsan.repository.CartRepository;
import com.nongsan.repository.OrderDetailRepository;
import com.nongsan.repository.OrderRepository;
import com.nongsan.repository.ProductRepository;
import com.nongsan.repository.UserRepository;
import com.nongsan.utils.SendMailUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("api/orders")
public class OrderApi {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartDetailRepository cartDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SendMailUtil senMail;

	// Api lấy tất cả đơn hàng và sắp xếp theo id giảm dần (mới nhất đến cũ nhất)
	@GetMapping
	public ResponseEntity<List<Order>> findAll() {
		return ResponseEntity.ok(orderRepository.findAllByOrderByOrdersIdDesc());
	}

	// Lấy chi tiết đơn hàng theo id
	@GetMapping("{id}")
	public ResponseEntity<Order> getById(@PathVariable("id") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findById(id).get());
	}

	// Lấy đơn hàng theo người dùng (email)
	@GetMapping("/user/{email}")
	public ResponseEntity<List<Order>> getByUser(@PathVariable("email") String email) {
		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity
				.ok(orderRepository.findByUserOrderByOrdersIdDesc(userRepository.findByEmail(email).get()));
	}

	// Api lấy đơn hàng theo id và trạng thái, đồng thời cập nhật trạng thái đơn hàng
	// Dành cho phê duyệt yêu cầu hủy đơn hàng từ người dùng
	@GetMapping("updateStatus/{id}/{status}")
	public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id, @PathVariable("status") int status) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();
		order.setStatus(status);
		orderRepository.save(order);

		// Gửi mail thông báo tùy theo trạng thái
		if (status == 3 || status == 6)
			senMail.sendMailOrderCancel(order);
		if (status == 1)
			senMail.sendMailOrderDeliver(order);
		if (status == 2)
			senMail.sendMailOrderSuccess(order);

		return ResponseEntity.ok().build();
	}

	// Api thanh toán đơn hàng COD ~ trực tiếp
	@PostMapping("/{email}")
	public ResponseEntity<Order> checkout(@PathVariable("email") String email, @RequestBody CartRequest cartRequest) {

		// Lấy từ mặt hàng từ giỏ hàng
		Cart cart = cartRepository.findById(cartRequest.getCartId()).get();

		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}
		if (!cartRepository.existsById(cart.getCartId())) {
			return ResponseEntity.notFound().build();
		}
		// Lấy danh sách chi tiết giỏ hàng
		List<CartDetail> items = cartDetailRepository.findByCart(cart);

		// Tính tổng trọng lượng đơn hàng
		Double weight = 0.0;
		for (CartDetail i : items) {
			weight += i.getProduct().getWeight() * i.getQuantity();
		}

		// Tạo đơn hàng mới
		Order order = orderRepository.save(new Order(0L, new Date(), cartRequest.getAmount(), cart.getAddress(),
				cart.getPhone(), cartRequest.getShippingFee(), weight, 0,
				userRepository.findByEmail(email).get()));

		// Lưu chi tiết đơn hàng
		for (CartDetail i : items) {
			OrderDetail orderDetail = new OrderDetail(0L, i.getQuantity(), i.getProduct().getPrice(), i.getProduct(), order);
			orderDetailRepository.save(orderDetail);
		}
		
		// Xoá chi tiết giỏ hàng sau khi đặt hàng thành công
		for (CartDetail i : items) {
			cartDetailRepository.delete(i);
		}

		// Gửi mail xác nhận đơn hàng
		senMail.sendMailOrder(order);

		return ResponseEntity.ok(order);
	}

	// Api hủy đơn hàng
	@GetMapping("cancel/{orderId}")
	public ResponseEntity<Void> cancel(@PathVariable("orderId") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();
		order.setStatus(3); // Cập nhật trạng thái hủy đơn hàng
		orderRepository.save(order); // Lưu thay đổi
		senMail.sendMailOrderCancel(order); // Gửi mail hủy đơn hàng
		return ResponseEntity.ok().build();
	}

	// Api cấp nhật trạng thái đơn hàng (đang giao hàng)
	@GetMapping("deliver/{orderId}")
	public ResponseEntity<Void> deliver(@PathVariable("orderId") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();
		order.setStatus(1); // Cập nhật trạng thái đang giao hàng
		orderRepository.save(order);
		senMail.sendMailOrderDeliver(order);  // Gửi mail thông báo đang giao hàng
		return ResponseEntity.ok().build();
	}

	// Api cập nhật trạng thái đơn hàng (hoàn tất)
	@GetMapping("success/{orderId}")
	public ResponseEntity<Void> success(@PathVariable("orderId") Long id) {
			Order order = orderRepository.findById(id).get();
			order.setStatus(2); // Cập nhật trạng thái hoàn tất
			orderRepository.save(order);
			updateProduct(order); // Cập nhật kho khi thành công
			senMail.sendMailOrderSuccess(order); // Gửi mail thông báo hoàn tất đơn hàng
			return ResponseEntity.ok().build();
	}

	// hàm cập nhật tồn kho
	public void updateProduct(Order order) {
		List<OrderDetail> listOrderDetail = orderDetailRepository.findByOrder(order);
		for (OrderDetail orderDetail : listOrderDetail) {
			Product product = productRepository.findById(orderDetail.getProduct().getProductId()).get();
			if (product != null) {
				product.setQuantity(product.getQuantity() - orderDetail.getQuantity());
				product.setSold(product.getSold() + orderDetail.getQuantity());
				productRepository.save(product); // Lưu thay đổi sản phẩm
			}
		}
	}

}
