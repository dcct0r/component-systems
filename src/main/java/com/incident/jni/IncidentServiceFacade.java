package com.incident.jni;

import com.incident.model.*;
import com.incident.service.IncidentService;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class IncidentServiceFacade {

    private static final IncidentService incidentService;

    static {
        try {
            InitialContext ctx = new InitialContext();
            incidentService = (IncidentService) ctx.lookup(
                    "java:global/incident-management/IncidentServiceImpl!com.incident.service.IncidentService"
            );
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createIncident(
            String title,
            String description,
            String priority
    ) {
        IncidentPriority p = IncidentPriority.valueOf(priority);
        return incidentService.createIncident(title, description, p).getId();
    }

    public static String changeStatus(
            String id,
            String status,
            String assignee,
            String comment
    ) {
        IncidentStatus s = IncidentStatus.valueOf(status);
        return incidentService.changeStatus(id, s, assignee, comment)
                .getStatus()
                .name();
    }

    public static String getIncidentAsJson(String id) {
        return incidentService.findIncidentById(id)
                .map(IncidentJsonMapper::toJson)
                .orElse("{}");
    }
}
