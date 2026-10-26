package com.spring.demo.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo.dto.AuthResponseDTO;
import com.spring.demo.dto.AssignRoleRequestDTO;
import com.spring.demo.dto.PasswordChangeRequestDTO;
import com.spring.demo.dto.RegisterAndLoginRequestDTO;
import com.spring.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

// @CrossOrigin is redundant here since we defined global settings in the SecurityConfig.java
// this won't take priority over the settings in that file 
// just leaving it here for demonstration purposes 
@RestController
@CrossOrigin(origins = {
		"http://localhost:4200",
		"http://localhost:3000",
		"https://localhost:3000"
}, allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO> register(
			@Valid @RequestBody RegisterAndLoginRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public AuthResponseDTO login(
			@Valid @RequestBody RegisterAndLoginRequestDTO request,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		return authService.login(request, servletRequest, servletResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) {
		authService.logout(request, response, authentication);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public AuthResponseDTO currentUser(Authentication authentication) {
		return authService.toResponse(authentication);
	}

	@PostMapping("/change-password")
	public ResponseEntity<Void> changePassword(
			@Valid @RequestBody PasswordChangeRequestDTO request) {
		authService.changePassword(request);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/assign-role")
	public AuthResponseDTO assignRole(@Valid @RequestBody AssignRoleRequestDTO request) {
		return authService.assignRole(request);
	}

	@GetMapping("/csrf")
	public CsrfToken csrfToken(CsrfToken csrfToken) {
		return csrfToken;
	}
}
