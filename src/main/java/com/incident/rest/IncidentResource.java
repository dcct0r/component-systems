// src/main/java/com/incident/rest/IncidentResource.java
package com.incident.rest;

import com.incident.model.Incident;
import com.incident.model.IncidentPriority;
import com.incident.model.IncidentStatus;
import com.incident.service.IncidentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Path("/incidents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidentResource {

    @Inject
    private IncidentService incidentService;

    @GET
    public Response getAllIncidents(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("status") IncidentStatus status) {

        List<Incident> incidents;
        long total;

        if (status != null) {
            incidents = incidentService.findIncidentsPaginated(page, size, status);
            total = incidentService.getIncidentCount(status);
        } else {
            incidents = incidentService.findIncidentsPaginated(page, size);
            total = incidentService.getIncidentCount();
        }

        return Response.ok()
                .entity(new IncidentPageResponse(incidents, page, size, total))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getIncidentById(@PathParam("id") String id) {
        return incidentService.findIncidentById(id)
                .map(incident -> Response.ok(incident).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createIncident(CreateIncidentRequest request) {
        try {
            Incident incident = incidentService.createIncident(
                    request.getTitle(),
                    request.getDescription(),
                    request.getPriority()
            );
            return Response.status(Response.Status.CREATED)
                    .entity(incident)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response changeIncidentStatus(
            @PathParam("id") String id,
            ChangeStatusRequest request) {

        try {
            Incident incident = incidentService.changeStatus(
                    id,
                    request.getNewStatus(),
                    request.getAssignee(),
                    request.getComment()
            );
            return Response.ok(incident).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{id}/comments")
    public Response addComment(
            @PathParam("id") String id,
            AddCommentRequest request) {

        return incidentService.findIncidentById(id)
                .map(incident -> {
                    incident.addComment(request.getComment());
                    incidentService.updateIncident(incident);
                    return Response.ok(incident).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/statistics")
    public Response getStatistics() {
        Map<IncidentStatus, Long> statusStats = incidentService.getIncidentStatistics();
        Map<IncidentPriority, Long> priorityStats = incidentService.getPriorityStatistics();

        return Response.ok()
                .entity(new StatisticsResponse(statusStats, priorityStats))
                .build();
    }

    public static class CreateIncidentRequest {
        private String title;
        private String description;
        private IncidentPriority priority;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public IncidentPriority getPriority() {
            return priority;
        }

        public void setPriority(IncidentPriority priority) {
            this.priority = priority;
        }
    }

    public static class ChangeStatusRequest {
        private IncidentStatus newStatus;
        private String assignee;
        private String comment;

        public IncidentStatus getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(IncidentStatus newStatus) {
            this.newStatus = newStatus;
        }

        public String getAssignee() {
            return assignee;
        }

        public void setAssignee(String assignee) {
            this.assignee = assignee;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class AddCommentRequest {
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class IncidentPageResponse {
        private final List<Incident> incidents;
        private final int page;
        private final int size;
        private final long total;
        private final int totalPages;

        public IncidentPageResponse(List<Incident> incidents, int page, int size, long total) {
            this.incidents = incidents;
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = (int) Math.ceil((double) total / size);
        }

        public List<Incident> getIncidents() {
            return incidents;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }

        public long getTotal() {
            return total;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public static class StatisticsResponse {
        private final Map<IncidentStatus, Long> statusStatistics;
        private final Map<IncidentPriority, Long> priorityStatistics;

        public StatisticsResponse(
                Map<IncidentStatus, Long> statusStatistics,
                Map<IncidentPriority, Long> priorityStatistics) {
            this.statusStatistics = statusStatistics;
            this.priorityStatistics = priorityStatistics;
        }

        public Map<IncidentStatus, Long> getStatusStatistics() {
            return statusStatistics;
        }

        public Map<IncidentPriority, Long> getPriorityStatistics() {
            return priorityStatistics;
        }
    }

    public static class ErrorResponse {
        private final String message;
        private final long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }


        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}