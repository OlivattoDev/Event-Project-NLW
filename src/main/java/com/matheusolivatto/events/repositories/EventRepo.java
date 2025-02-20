package com.matheusolivatto.events.repositories;

import org.springframework.data.repository.CrudRepository;

import com.matheusolivatto.events.models.Event;

public interface EventRepo extends CrudRepository<Event, Integer> {
	
	 public Event findByPrettyName(String prettyName);

}
