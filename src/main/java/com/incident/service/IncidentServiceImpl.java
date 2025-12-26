// src/main/java/com/incident/service/IncidentServiceImpl.java
package com.incident.service;

import com.incident.model.Incident;
import com.incident.model.IncidentPriority;
import com.incident.model.IncidentStatus;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Startup
public class IncidentServiceImpl implements IncidentService {
    private final Map<String, Incident> incidents = new ConcurrentHashMap<>();
    private final AtomicLong incidentCounter = new AtomicLong(1);

    @PostConstruct
    public void init() {

        // КРИТИЧЕСКИЕ инциденты (HIGH)
        createIncident("Production Server Crash",
                "Main production server went down at 2:00 AM, affecting all users",
                IncidentPriority.HIGH);

        createIncident("Database Outage",
                "Primary database cluster is unreachable, all transactions failing",
                IncidentPriority.HIGH);

        createIncident("Payment System Failure",
                "Payment gateway API returning 500 errors, customers cannot complete purchases",
                IncidentPriority.HIGH);

        createIncident("Security Breach Attempt",
                "Multiple failed login attempts from suspicious IP addresses",
                IncidentPriority.HIGH);

        createIncident("Data Loss Incident",
                "Customer data backup failed, potential data corruption detected",
                IncidentPriority.HIGH);

        // СРЕДНЕЙ важности инциденты (MEDIUM)
        createIncident("API Response Time Degradation",
                "REST API response times increased from 200ms to 1500ms",
                IncidentPriority.MEDIUM);

        createIncident("Memory Leak in User Service",
                "User service memory consumption growing by 1GB/hour",
                IncidentPriority.MEDIUM);

        createIncident("Email Notification Delay",
                "Email notifications delayed by 30+ minutes",
                IncidentPriority.MEDIUM);

        createIncident("Mobile App Crash on iOS 15",
                "App crashes on launch for iOS 15.4 users",
                IncidentPriority.MEDIUM);

        createIncident("Incorrect Invoice Calculation",
                "Some customers receiving incorrect invoice amounts",
                IncidentPriority.MEDIUM);

        createIncident("Search Function Broken",
                "Product search returning empty results for valid queries",
                IncidentPriority.MEDIUM);

        createIncident("Dashboard Loading Slow",
                "Admin dashboard takes 15+ seconds to load",
                IncidentPriority.MEDIUM);

        // НИЗКОЙ важности инциденты (LOW)
        createIncident("Typo in Welcome Email",
                "Welcome email contains spelling error in subject line",
                IncidentPriority.LOW);

        createIncident("Footer Link Broken",
                "Privacy policy link in footer redirects to 404",
                IncidentPriority.LOW);

        createIncident("Icon Missing on Settings Page",
                "Gear icon not displaying on user settings page",
                IncidentPriority.LOW);

        createIncident("Mobile Menu Alignment Issue",
                "Mobile navigation menu slightly misaligned on some devices",
                IncidentPriority.LOW);

        createIncident("Export CSV Formatting",
                "Exported CSV has extra commas in some columns",
                IncidentPriority.LOW);

        createIncident("Color Scheme Inconsistency",
                "Button colors differ between dashboard and settings",
                IncidentPriority.LOW);

        createIncident("Console Warning Messages",
                "Browser console shows deprecation warnings",
                IncidentPriority.LOW);

        createIncident("Documentation Update Needed",
                "API documentation missing new endpoint details",
                IncidentPriority.LOW);

        String[] closedIncidentIds = new String[5];
        for (int i = 0; i < 5; i++) {
            Incident incident = createIncident("Resolved Test Issue " + (i + 1),
                    "This was a test issue that has been resolved",
                    i % 3 == 0 ? IncidentPriority.HIGH :
                            i % 3 == 1 ? IncidentPriority.MEDIUM : IncidentPriority.LOW);
            closedIncidentIds[i] = incident.getId();
        }

        String[] inProgressIds = new String[5];
        for (int i = 0; i < 5; i++) {
            Incident incident = createIncident("Issue Being Investigated " + (i + 1),
                    "This issue is currently under investigation",
                    i % 3 == 0 ? IncidentPriority.HIGH :
                            i % 3 == 1 ? IncidentPriority.MEDIUM : IncidentPriority.LOW);
            inProgressIds[i] = incident.getId();
        }


        for (int i = 0; i < 5; i++) {
            try {
                changeStatus(closedIncidentIds[i], IncidentStatus.CLOSED,
                        "Support Team", "Issue resolved after applying patch v2.1." + i);

                changeStatus(inProgressIds[i], IncidentStatus.IN_PROGRESS,
                        "Dev Team", "Assigned to developer for investigation");
            } catch (Exception e) {
                System.out.println("Error setting status for test incident: " + e.getMessage());
            }
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public Incident createIncident(String title, String description, IncidentPriority priority) {
        if (title == null || title.trim().isEmpty() || title.length() > 100) {
            throw new IllegalArgumentException(
                    "Title is required and must be between 1 and 100 characters"
            );
        }

        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }

        Incident incident = new Incident(title, description, priority);
        incidents.put(incident.getId(), incident);
        return incident;
    }

    @Override
    @Lock(LockType.READ)
    public Optional<Incident> findIncidentById(String id) {
        return Optional.ofNullable(incidents.get(id));
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findAllIncidents() {
        return new ArrayList<>(incidents.values());
    }

    @Override
    @Lock(LockType.WRITE)
    public Incident updateIncident(Incident incident) {
        if (!incidents.containsKey(incident.getId())) {
            throw new NoSuchElementException("Incident not found with id: " + incident.getId());
        }
        incidents.put(incident.getId(), incident);
        return incident;
    }

    @Override
    @Lock(LockType.WRITE)
    public boolean deleteIncident(String id) {
        return incidents.remove(id) != null;
    }

    @Override
    @Lock(LockType.WRITE)
    public Incident changeStatus(String incidentId, IncidentStatus newStatus,
                                 String assignee, String comment) {
        Incident incident = findIncidentById(incidentId)
                .orElseThrow(() -> new NoSuchElementException("Incident not found"));

        incident.transitionStatus(newStatus, assignee, comment);
        return incident;
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findIncidentsByStatus(IncidentStatus status) {
        return incidents.values().stream()
                .filter(incident -> incident.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findIncidentsByPriority(IncidentPriority priority) {
        return incidents.values().stream()
                .filter(incident -> incident.getPriority() == priority)
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findIncidentsPaginated(int page, int pageSize) {
        return incidents.values().stream()
                .sorted(Comparator.comparing(Incident::getCreatedAt).reversed())
                .skip((long) page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findIncidentsPaginated(int page, int pageSize, IncidentStatus status) {
        return incidents.values().stream()
                .filter(incident -> incident.getStatus() == status)
                .sorted(Comparator.comparing(Incident::getCreatedAt).reversed())
                .skip((long) page * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public long getIncidentCount() {
        return incidents.size();
    }

    @Override
    @Lock(LockType.READ)
    public long getIncidentCount(IncidentStatus status) {
        return incidents.values().stream()
                .filter(incident -> incident.getStatus() == status)
                .count();
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> searchIncidents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAllIncidents();
        }

        String lowerQuery = query.toLowerCase();
        return incidents.values().stream()
                .filter(incident ->
                        (incident.getTitle() != null && incident.getTitle().toLowerCase().contains(lowerQuery)) ||
                                (incident.getDescription() != null && incident.getDescription().toLowerCase().contains(lowerQuery))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public List<Incident> findIncidentsByAssignee(String assignee) {
        return incidents.values().stream()
                .filter(incident ->
                        incident.getAssignee() != null &&
                                incident.getAssignee().equalsIgnoreCase(assignee)
                )
                .collect(Collectors.toList());
    }

    @Override
    @Lock(LockType.READ)
    public Map<IncidentStatus, Long> getIncidentStatistics() {
        return Arrays.stream(IncidentStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> incidents.values().stream()
                                .filter(incident -> incident.getStatus() == status)
                                .count()
                ));
    }

    @Override
    @Lock(LockType.READ)
    public Map<IncidentPriority, Long> getPriorityStatistics() {
        return Arrays.stream(IncidentPriority.values())
                .collect(Collectors.toMap(
                        priority -> priority,
                        priority -> incidents.values().stream()
                                .filter(incident -> incident.getPriority() == priority)
                                .count()
                ));
    }
}