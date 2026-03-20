package com.example.detask.BLL;


import com.example.detask.BE.Event;
import com.example.detask.DLL.EventDAO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class EventManager {

    private final EventDAO repo;

    public EventManager(EventDAO repo) {
        this.repo = repo;
    }

    // NEW — all events across all teams
    public List<Event> getAllEvents() {
        return repo.getAllEvents();
    }

    // NEW — single event by id
    public Event getById(int id) {
        return repo.getById(id);
    }

    // existing — events for a specific team with optional date range
    public List<Event> listEvents(int teamId, String fromDate, String toDate) {
        if (teamId <= 0) return Collections.emptyList();
        return repo.findByTeamAndDateRange(teamId,
                isBlank(fromDate) ? null : fromDate.trim(),
                isBlank(toDate)   ? null : toDate.trim());
    }

    public Event createEvent(int teamId, String name, String description,
                             String startsAt, int createdBy) {
        if (teamId <= 0 || isBlank(name) || !isValidIsoInstant(startsAt)) return null;
        if (Instant.parse(startsAt).isBefore(Instant.now())) return null;

        Event e = new Event();
        e.setTeamId(teamId);
        e.setName(name.trim());
        e.setDescription(description == null ? "" : description.trim());
        e.setStartsAt(startsAt);
        e.setStatus("PUBLISHED");
        e.setCreatedBy(createdBy);

        return repo.save(e);
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private boolean isValidIsoInstant(String iso) {
        try { Instant.parse(Objects.requireNonNull(iso)); return true; }
        catch (DateTimeParseException | NullPointerException ex) { return false; }
    }
}