package com.nongsan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.nongsan.repository.CategoryRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("api/categories")
public class CategoryApi {

	@Autowired
	CategoryRepository repo; // Spring data jpa sinh ra code cho repo

	// Api lấy danh sách tất cả danh mục: http://localhost:8080/api/categories
	@GetMapping
	public ResponseEntity<List<Category>> getAll() {
		return ResponseEntity.ok(repo.findAll());
	}

	// phương thức = hàm
	// Api lấy chi tiết danh mục theo id: http://localhost:8080/api/categories/{id}
	@GetMapping("{id}")
	public ResponseEntity<Category> getById(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.findById(id).get());
	}

	// Api thêm mới danh mục: http://localhost:8080/api/categories
	@PostMapping
	public ResponseEntity<Category> post(@RequestBody Category category) {
		if (repo.existsById(category.getCategoryId())) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(repo.save(category));
	}

	// Api cập nhật danh mục theo id: http://localhost:8080/api/categories/{id}
	@PutMapping("{id}")
	public ResponseEntity<Category> put(@RequestBody Category category, @PathVariable("id") Long id) {
		if (!id.equals(category.getCategoryId())) {
			return ResponseEntity.badRequest().build();
		}
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(repo.save(category));
	}

	// Api xóa danh mục theo id: http://localhost:8080/api/categories/{id}
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!repo.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		repo.deleteById(id);
		return ResponseEntity.ok().build();
	}

}
