package edu.mum.polls.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.mum.polls.exception.AppException;
import edu.mum.polls.model.Role;
import edu.mum.polls.model.RoleName;
import edu.mum.polls.model.User;
import edu.mum.polls.payloads.ApiResponse;
import edu.mum.polls.payloads.JwtAuthenticationResponse;
import edu.mum.polls.payloads.LoginRequest;
import edu.mum.polls.payloads.SignUpRequest;
import edu.mum.polls.repository.RoleRepository;
import edu.mum.polls.repository.UserRepository;
import edu.mum.polls.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticateManager;
	
	@Autowired 
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		Authentication authentication = authenticateManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUserNameOrEmail(),
						loginRequest.getPassword())
				);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
		if(userRepo.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<Object>(new ApiResponse(false, "Username already exists"), HttpStatus.BAD_REQUEST); 
		}
		
		if(userRepo.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<Object>(new ApiResponse(false, "Email already exists"), HttpStatus.BAD_REQUEST);
		}
		
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),signUpRequest.getEmail(),
				signUpRequest.getPassword());
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role Not set"));
		user.setRoles(Collections.singleton(userRole));
		
		User result = userRepo.save(user);
		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/users/{username}")
				.buildAndExpand(result.getUsername())
				.toUri();
		
		return ResponseEntity.created(location).body(new ApiResponse(true, "User Registered Successfully"));
	}
}
