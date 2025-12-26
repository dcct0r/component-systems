// src/main/java/com/incident/service/IncidentService.java
package com.incident.service;

import com.incident.model.Incident;
import com.incident.model.IncidentPriority;
import com.incident.model.IncidentStatus;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Local
public interface IncidentService {
    Incident createIncident(String title, String description, IncidentPriority priority);

    Optional<Incident> findIncidentById(String id);

    List<Incident> findAllIncidents();

    Incident updateIncident(Incident incident);

    boolean deleteIncident(String id);

    Incident changeStatus(String incidentId, IncidentStatus newStatus, String assignee, String comment);

    List<Incident> findIncidentsByStatus(IncidentStatus status);

    List<Incident> findIncidentsByPriority(IncidentPriority priority);

    List<Incident> findIncidentsPaginated(int page, int pageSize);

    List<Incident> findIncidentsPaginated(int page, int pageSize, IncidentStatus status);

    long getIncidentCount();

    long getIncidentCount(IncidentStatus status);

    List<Incident> searchIncidents(String query);

    List<Incident> findIncidentsByAssignee(String assignee);

    Map<IncidentStatus, Long> getIncidentStatistics();

    Map<IncidentPriority, Long> getPriorityStatistics();
}