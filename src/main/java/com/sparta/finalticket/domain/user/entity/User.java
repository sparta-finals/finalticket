package com.sparta.finalticket.domain.user.entity;

import com.sparta.finalticket.domain.user.dto.request.UserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Table(name = "users")
@Entity
@NoArgsConstructor
@SQLDelete(sql = "UPDATE game SET state = false WHERE id = ?")
@Where(clause = "state = true")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String address;

	@Column
	private String nickname;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role;

	@Column
	private boolean state;

	public User(UserRequestDto requestDto) {
		this.username = requestDto.getUsername();
		this.password = requestDto.getPassword();
		this.email = requestDto.getEmail();
		this.nickname = requestDto.getNickname();
		this.role = requestDto.getRole();
		this.address = requestDto.getAddress();
		this.state = true;
	}

	public User(UserRequestDto requestDto, User user){
		this.id = user.getId();
		this.username = requestDto.getUsername();
		this.password = requestDto.getPassword();
		this.email = requestDto.getEmail();
		this.nickname = requestDto.getNickname();
		this.role = user.getRole();
		this.address = requestDto.getAddress();
		this.state = true;
	}
}
