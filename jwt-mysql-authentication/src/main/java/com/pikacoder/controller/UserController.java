package com.pikacoder.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pikacoder.auth.AuthenticationRequest;
import com.pikacoder.auth.AuthenticationResponse;
import com.pikacoder.filter.JwtUtil;
import com.pikacoder.model.User;
import com.pikacoder.service.UserService;

@RestController
public class UserController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	UserService userService;
	
	
	@GetMapping("/findall") 
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<User> list(){
		return userService.findAll();
	}
	
	@PostMapping("/signup") 
	@ResponseStatus(HttpStatus.OK)
	public void create(@RequestBody User user){
		userService.create(user);
	}
	
	
	@GetMapping("/user/{id}")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public Optional<User> get(@PathVariable("id") int id){
		return userService.get(id);
	}
	

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
		
		try{
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
				);
		}
		catch(BadCredentialsException e){
			throw new Exception("Incorrect username or password");
		}
		
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		
		final String jwt = jwtUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
		
	}
}
