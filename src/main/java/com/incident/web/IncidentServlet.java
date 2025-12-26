package com.incident.web;

import com.incident.model.Incident;
import com.incident.model.IncidentPriority;
import com.incident.model.IncidentStatus;
import com.incident.service.IncidentService;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/incidents")
public class IncidentServlet extends HttpServlet {

    @EJB
    private IncidentService incidentService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            listIncidents(request, response);
        } else {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "view":
                    showIncidentDetails(request, response);
                    break;
                case "change-status":
                    showChangeStatusForm(request, response);
                    break;
                default:
                    listIncidents(request, response);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("incidents");
            return;
        }

        switch (action) {
            case "save":
                saveIncident(request, response);
                break;
            case "update-status":
                updateIncidentStatus(request, response);
                break;
            case "delete":
                deleteIncident(request, response);
                break;
            default:
                response.sendRedirect("incidents");
                break;
        }
    }

    private void listIncidents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String statusParam = request.getParameter("status");
            String pageParam = request.getParameter("page");

            int page = 0;
            int pageSize = 10;

            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    page = 0;
                }
            }

            List<Incident> incidents;
            long total;

            if (statusParam != null && !statusParam.isEmpty()) {
                try {
                    IncidentStatus status = IncidentStatus.valueOf(statusParam);
                    incidents = incidentService.findIncidentsPaginated(page, pageSize, status);
                    total = incidentService.getIncidentCount(status);
                    request.setAttribute("filterStatus", statusParam);
                } catch (IllegalArgumentException e) {
                    incidents = incidentService.findIncidentsPaginated(page, pageSize);
                    total = incidentService.getIncidentCount();
                }
            } else {
                incidents = incidentService.findIncidentsPaginated(page, pageSize);
                total = incidentService.getIncidentCount();
            }

            Map<IncidentStatus, Long> statistics = incidentService.getIncidentStatistics();

            int totalPages = (int) Math.ceil((double) total / pageSize);

            request.setAttribute("incidents", incidents);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalIncidents", total);
            request.setAttribute("statistics", statistics);

            request.getRequestDispatcher("/WEB-INF/views/incident-list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading incidents: " + e.getMessage());
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/incident-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) {
            incidentService.findIncidentById(id).ifPresent(incident -> {
                request.setAttribute("incident", incident);
            });
            request.getRequestDispatcher("/WEB-INF/views/incident-form.jsp").forward(request, response);
        } else {
            response.sendRedirect("incidents");
        }
    }

    private void showIncidentDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) {
            incidentService.findIncidentById(id).ifPresent(incident -> {
                request.setAttribute("incident", incident);
            });
            request.getRequestDispatcher("/WEB-INF/views/incident-details.jsp").forward(request, response);
        } else {
            response.sendRedirect("incidents");
        }
    }

    private void showChangeStatusForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) {
            incidentService.findIncidentById(id).ifPresent(incident -> {
                request.setAttribute("incident", incident);
            });
            request.getRequestDispatcher("/WEB-INF/views/change-status.jsp").forward(request, response);
        } else {
            response.sendRedirect("incidents");
        }
    }

    private void saveIncident(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priorityStr = request.getParameter("priority");

        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("error", "Title is required");
            request.getRequestDispatcher("/WEB-INF/views/incident-form.jsp").forward(request, response);
            return;
        }

        try {
            IncidentPriority priority = IncidentPriority.valueOf(priorityStr);

            if (id == null || id.isEmpty()) {
                incidentService.createIncident(title, description, priority);
            } else {
                incidentService.findIncidentById(id).ifPresent(incident -> {
                    incident.setTitle(title);
                    incident.setDescription(description);
                    incident.setPriority(priority);
                    incidentService.updateIncident(incident);
                });
            }

            response.sendRedirect("incidents");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid priority");
            request.getRequestDispatcher("/WEB-INF/views/incident-form.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error saving incident: " + e.getMessage());
        }
    }

    private void updateIncidentStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String id = request.getParameter("id");
        String newStatusStr = request.getParameter("newStatus");
        String assignee = request.getParameter("assignee");
        String comment = request.getParameter("comment");

        if (id == null || id.isEmpty()) {
            response.sendRedirect("incidents");
            return;
        }

        try {
            IncidentStatus newStatus = IncidentStatus.valueOf(newStatusStr);
            incidentService.changeStatus(id, newStatus, assignee, comment);
            response.sendRedirect("incidents");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid status");
            request.getRequestDispatcher("/WEB-INF/views/change-status.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error updating status: " + e.getMessage());
        }
    }

    private void deleteIncident(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) {
            incidentService.deleteIncident(id);
        }
        response.sendRedirect("incidents");
    }
}