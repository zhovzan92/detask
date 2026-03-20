package com.example.detask.controller;

import com.example.detask.BE.Event;
import com.example.detask.BLL.EventManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventManager eventManager;

    public EventController(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public record CreateEventReq(Integer teamId, String name, String description,
                                 String startsAt, Integer createdBy) {}

    // GET /api/v1/events              all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventManager.getAllEvents());
    }

    // GET /api/v1/events/1             single event by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable int id) {
        Event e = eventManager.getById(id);
        if (e == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "NOT_FOUND", "message", "Event not found"));
        }
        return ResponseEntity.ok(e);
    }

    // GET /api/v1/events/team/1       all events for a team
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Event>> getEventsByTeam(
            @PathVariable int teamId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(eventManager.listEvents(teamId, from, to));
    }

    // POST /api/v1/events             create event
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CreateEventReq req) {
        Event e = eventManager.createEvent(
                req.teamId(),
                req.name(),
                req.description() == null ? "" : req.description(),
                req.startsAt(),
                req.createdBy()
        );
        if (e == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "VALIDATION_ERROR", "message", "Could not create event"));
        }
        return ResponseEntity.ok(e);
    }
}