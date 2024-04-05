package com.sparta.finalticket.domain.user.repository;

import com.sparta.finalticket.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

  Optional<User> findByUsernameAndState(String userId,boolean state);
}
