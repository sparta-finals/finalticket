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

@Getter
@Setter
@Table(name = "users")
@Entity
@NoArgsConstructor
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

	public User(UserRequestDto requestDto, UserRoleEnum role){
		this.username = requestDto.getUsername();
		this.password = requestDto.getPassword();
		this.email = requestDto.getEmail();
		this.nickname = requestDto.getNickname();
		this.role = role;
		this.address = requestDto.getAddress();
		this.state = true;
	}
}
