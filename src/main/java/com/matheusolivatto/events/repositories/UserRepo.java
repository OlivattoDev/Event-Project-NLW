package com.matheusolivatto.events.repositories;

import org.springframework.data.repository.CrudRepository;

import com.matheusolivatto.events.models.User;

public interface UserRepo extends CrudRepository<User, Integer> {

	public User findByEmail(String email);

}
