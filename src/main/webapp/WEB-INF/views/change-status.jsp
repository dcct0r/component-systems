<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Incident Status</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="header mb-4">
        <h1>Change Incident Status</h1>
    </div>

    <c:if test="${empty incident}">
        <div class="alert alert-danger" role="alert">
            Incident not found!
        </div>
        <a href="incidents" class="btn btn-secondary">Back to List</a>
    </c:if>

    <c:if test="${not empty incident}">
        <div class="card mb-4">
            <div class="card-body">
                <h5 class="card-title">${incident.title}</h5>
                <p class="card-text">Current Status:
                    <span class="badge bg-info">${incident.status}</span>
                </p>
                <p class="card-text">Priority:
                    <span class="badge
                        <c:choose>
                            <c:when test="${incident.priority == 'HIGH'}">bg-danger</c:when>
                            <c:when test="${incident.priority == 'MEDIUM'}">bg-warning</c:when>
                            <c:otherwise>bg-success</c:otherwise>
                        </c:choose>">
                            ${incident.priority}
                    </span>
                </p>
            </div>
        </div>

        <form action="incidents" method="post">
            <input type="hidden" name="action" value="update-status">
            <input type="hidden" name="id" value="${incident.id}">

            <div class="mb-3">
                <label for="newStatus" class="form-label">New Status *</label>
                <select class="form-select" id="newStatus" name="newStatus" required>
                    <option value="">Select new status</option>
                    <c:if test="${incident.status == 'OPEN'}">
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="CLOSED">Closed</option>
                    </c:if>
                    <c:if test="${incident.status == 'IN_PROGRESS'}">
                        <option value="CLOSED">Closed</option>
                    </c:if>
                </select>
            </div>

            <div class="mb-3">
                <label for="assignee" class="form-label">Assignee</label>
                <input type="text" class="form-control" id="assignee" name="assignee"
                       value="<c:out value='${incident.assignee}'/>">
            </div>

            <div class="mb-3">
                <label for="comment" class="form-label">Comment</label>
                <textarea class="form-control" id="comment" name="comment" rows="3"></textarea>
            </div>

            <div class="mb-3">
                <button type="submit" class="btn btn-warning">Update Status</button>
                <a href="incidents" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>