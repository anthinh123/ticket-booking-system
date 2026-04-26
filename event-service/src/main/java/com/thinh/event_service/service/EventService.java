package com.thinh.event_service.service;

import com.thinh.event_service.dto.request.EventRequest;
import com.thinh.event_service.entity.Event;
import com.thinh.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EVENT_NOT_FOUND"));
    }

    public Event createEvent(EventRequest request) {
        Event event = Event.builder()
                .name(request.getName())
                .date(request.getDate())
                .venueName(request.getVenueName())
                .totalSeats(request.getTotalSeats())
                .status(request.getStatus())
                .saleStartTime(request.getSaleStartTime())
                .build();
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, EventRequest request) {
        Event event = getEventById(id);
        
        event.setName(request.getName());
        event.setDate(request.getDate());
        event.setVenueName(request.getVenueName());
        event.setTotalSeats(request.getTotalSeats());
        event.setStatus(request.getStatus());
        event.setSaleStartTime(request.getSaleStartTime());
        
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
}
