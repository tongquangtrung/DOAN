package com.nongsan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nongsan.entity.Category;
import com.nongsan.entity.Product;
import com.nongsan.repository.CategoryRepository;
import com.nongsan.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
public class ProductApi {

	private final ProductRepository repo;
	private final CategoryRepository cRepo;


	@PostMapping("import")
	public ResponseEntity<List<Product>> importCsv(@RequestBody List<Product> products) {
		return ResponseEntity.ok(repo.saveAll(products));
	}

	// Api lấy tất cả sản phẩm đang hoạt động
	@GetMapping
	public ResponseEntity<List<Product>> getAll() {
		return ResponseEntity.ok(repo.findByStatusTrue());
	}

	// Api lấy tất cả sản phẩm bán chạy nhất để hiển thị trên trang chủ
	@GetMapping("bestseller")
	public ResponseEntity<List<Product>> getBestSeller() {
		return ResponseEntity.ok(repo.findByStatusTrueOrderBySoldDesc());
	}

	// Api lấy tất cả sản phẩm bán chạy nhất cho trang quản trị
	@GetMapping("bestseller-admin")
	public ResponseEntity<List<Product>> getBestSellerAdmin() {
		return ResponseEntity.ok(repo.findTop10ByOrderBySoldDesc());
	}

	// Api lấy tất cả sản phẩm mới nhất
	@GetMapping("latest")
	public ResponseEntity<List<Product>> getLasted() {
		return ResponseEntity.ok(repo.findByStatusTrueOrderByEnteredDateDesc());
	}

	// Lấy tất cả sản phẩm có đánh giá
	@GetMapping("rated")
	public ResponseEntity<List<Product>> getRated() {
		return ResponseEntity.ok(repo.findProductRated());
	}

	// Lấy tất cả sản phẩm liên quan/gợi ý/đề xuất theo danh mục và sản phẩm hiện tại
	@GetMapping("suggest/{categoryId}/{productId}")
	public ResponseEntity<List<Product>> suggest(@PathVariable("categoryId") Long categoryId,
			@PathVariable("productId") Long productId) {
		return ResponseEntity.ok(repo.findProductSuggest(categoryId, productId, categoryId, categoryId));
	}

	// Api lấy tất cả sản phẩm theo danh mục
	@GetMapping("category/{id}")
	public ResponseEntity<List<Product>> getByCategory(@PathVariable("id") Long id) {
		if (!cRepo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Category c = cRepo.findById(id).get();
		return ResponseEntity.ok(repo.findByCategory(c));
	}

	// Api lấy chi tiết sản phẩm theo id
	@GetMapping("{id}")
	public ResponseEntity<Product> getById(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	// Api thêm mới sản phẩm
	@PostMapping
	public ResponseEntity<Product> post(@RequestBody Product product) {
		if (repo.existsById(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(repo.save(product));
	}

	// Api cập nhật sản phẩm theo id
	@PutMapping("{id}")
	public ResponseEntity<Product> put(@PathVariable("id") Long id, @RequestBody Product product) {
		if (!id.equals(product.getProductId())) {
			return ResponseEntity.badRequest().build();
		}
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.save(product));
	}

	// Api xóa mềm sản phẩm theo id (chuyển trạng thái sang không hoạt động)
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Product p = repo.findById(id).get();
		p.setStatus(false);
		repo.save(p);
		return ResponseEntity.ok().build();
	}

}