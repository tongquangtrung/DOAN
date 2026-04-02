package com.nongsan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nongsan.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByStatusTrue(); // Tìm những user có status = true - đang hoạt động

	Boolean existsByEmail(String email); // Kiểm tra email đã tồn tại hay chưa

	Optional<User> findByEmail(String email); // Tìm user theo email

	User findByToken(String token); // Tìm user theo token

}
