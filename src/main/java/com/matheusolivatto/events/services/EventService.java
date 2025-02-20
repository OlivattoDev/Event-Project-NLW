package com.matheusolivatto.events.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheusolivatto.events.models.Event;
import com.matheusolivatto.events.repositories.EventRepo;

@Service
public class EventService {

	@Autowired
	private EventRepo eventRepo;

	public Event addNewEvent(Event event) {

		event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
		return eventRepo.save(event);
	}

	public List<Event> getAllEvents() {
		return (List<Event>) eventRepo.findAll();
	}

	public Event getByPrettyName(String prettyName) {
		return eventRepo.findByPrettyName(prettyName);
	}

}
