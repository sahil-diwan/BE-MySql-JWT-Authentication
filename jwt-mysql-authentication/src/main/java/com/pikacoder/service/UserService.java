package com.pikacoder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.pikacoder.model.User;

public interface UserService {
	public List<User> findAll();
	public Optional<User> get(int id);
	public void create(User user);
}
