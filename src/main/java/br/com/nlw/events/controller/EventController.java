package br.com.nlw.events.controller;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/events")
    public Event addNewEvent(@RequestBody Event event) {
        return eventService.addNewEvent(event);
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/events/{prettyName}")
    public ResponseEntity<Event> getEvent(@PathVariable String prettyName) {
        Event event = eventService.getByPrettyName(prettyName);

        if (event != null) {
            return ResponseEntity.ok().body(event);
        }

        return ResponseEntity.notFound().build();
    }
}
