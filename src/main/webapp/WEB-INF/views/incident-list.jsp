<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Incidents List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .badge-open { background-color: #dc3545; }
        .badge-in-progress { background-color: #ffc107; color: #000; }
        .badge-closed { background-color: #198754; }
        .priority-high { background-color: #dc3545; color: white; }
        .priority-medium { background-color: #ffc107; color: #000; }
        .priority-low { background-color: #198754; color: white; }

        .statistics-card { height: 100%; }
        .table-hover tbody tr:hover { background-color: rgba(0,0,0,0.05); }
        .pagination .page-item.active .page-link { background-color: #0d6efd; border-color: #0d6efd; }
    </style>
</head>
<body>
<div class="container mt-4">
    <!-- Заголовок и кнопка -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Incident Management</h1>
        <a href="incidents?action=new" class="btn btn-primary">
            <i class="bi bi-plus-circle"></i> New Incident
        </a>
    </div>

    <!-- Статистика - всегда показываем -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card statistics-card bg-danger text-white">
                <div class="card-body text-center">
                    <h5 class="card-title">OPEN</h5>
                    <h2 class="display-4">
                        <c:choose>
                            <c:when test="${not empty statistics}">
                                <c:set var="openCount" value="0" />
                                <c:forEach items="${statistics}" var="entry">
                                    <c:if test="${entry.key == 'OPEN'}">
                                        <c:set var="openCount" value="${entry.value}" />
                                    </c:if>
                                </c:forEach>
                                ${openCount}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card statistics-card bg-warning text-dark">
                <div class="card-body text-center">
                    <h5 class="card-title">IN PROGRESS</h5>
                    <h2 class="display-4">
                        <c:choose>
                            <c:when test="${not empty statistics}">
                                <c:set var="progressCount" value="0" />
                                <c:forEach items="${statistics}" var="entry">
                                    <c:if test="${entry.key == 'IN_PROGRESS'}">
                                        <c:set var="progressCount" value="${entry.value}" />
                                    </c:if>
                                </c:forEach>
                                ${progressCount}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card statistics-card bg-success text-white">
                <div class="card-body text-center">
                    <h5 class="card-title">CLOSED</h5>
                    <h2 class="display-4">
                        <c:choose>
                            <c:when test="${not empty statistics}">
                                <c:set var="closedCount" value="0" />
                                <c:forEach items="${statistics}" var="entry">
                                    <c:if test="${entry.key == 'CLOSED'}">
                                        <c:set var="closedCount" value="${entry.value}" />
                                    </c:if>
                                </c:forEach>
                                ${closedCount}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </h2>
                </div>
            </div>
        </div>
    </div>

    <!-- Фильтры -->
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="mb-0">Filters</h5>
        </div>
        <div class="card-body">
            <form action="incidents" method="get" class="row g-3">
                <div class="col-md-4">
                    <label for="statusFilter" class="form-label">Status</label>
                    <select name="status" id="statusFilter" class="form-select">
                        <option value="">All Statuses</option>
                        <option value="OPEN" ${param.status == 'OPEN' ? 'selected' : ''}>Open</option>
                        <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                        <option value="CLOSED" ${param.status == 'CLOSED' ? 'selected' : ''}>Closed</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="pageFilter" class="form-label">Page</label>
                    <input type="number" name="page" id="pageFilter" class="form-control"
                           value="${currentPage}" min="0" max="${totalPages > 0 ? totalPages - 1 : 0}"
                           placeholder="Page number">
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <div class="btn-group w-100">
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                        <a href="incidents" class="btn btn-outline-secondary">Clear All</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Основной контент -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <c:choose>
                    <c:when test="${not empty totalIncidents}">
                        All Incidents (${totalIncidents} total)
                    </c:when>
                    <c:otherwise>
                        All Incidents
                    </c:otherwise>
                </c:choose>
            </h5>
            <span class="badge bg-info">Page ${currentPage + 1} of ${totalPages > 0 ? totalPages : 1}</span>
        </div>

        <div class="card-body">
            <c:choose>
                <c:when test="${not empty incidents and fn:length(incidents) > 0}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>Title</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${incidents}" var="incident">
                                <tr>
                                    <td>
                                        <div class="fw-bold">${incident.title}</div>
                                        <c:if test="${not empty incident.description}">
                                            <div class="text-muted small mt-1">
                                                <c:choose>
                                                    <c:when test="${fn:length(incident.description) > 60}">
                                                        ${fn:substring(incident.description, 0, 60)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${incident.description}
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:if>
                                    </td>
                                    <td>
                                            <span class="badge rounded-pill
                                                <c:choose>
                                                    <c:when test="${incident.priority == 'HIGH'}">priority-high</c:when>
                                                    <c:when test="${incident.priority == 'MEDIUM'}">priority-medium</c:when>
                                                    <c:otherwise>priority-low</c:otherwise>
                                                </c:choose>">
                                                    ${incident.priority}
                                            </span>
                                    </td>
                                    <td>
                                            <span class="badge rounded-pill
                                                <c:choose>
                                                    <c:when test="${incident.status == 'OPEN'}">badge-open</c:when>
                                                    <c:when test="${incident.status == 'IN_PROGRESS'}">badge-in-progress</c:when>
                                                    <c:otherwise>badge-closed</c:otherwise>
                                                </c:choose>">
                                                    ${incident.status}
                                            </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty incident.createdAt}">
                                                <c:set var="createdStr" value="${incident.createdAt.toString()}" />
                                                <div class="small">
                                                    <div>${fn:substring(createdStr, 0, 10)}</div>
                                                    <div class="text-muted">${fn:substring(createdStr, 11, 16)}</div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted small">N/A</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="incidents?action=edit&id=${incident.id}"
                                               class="btn btn-outline-primary" title="Edit">
                                                <i class="bi bi-pencil"></i> Edit
                                            </a>
                                            <a href="incidents?action=change-status&id=${incident.id}"
                                               class="btn btn-outline-warning" title="Change Status">
                                                <i class="bi bi-arrow-repeat"></i> Status
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Пагинация -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 0}">
                                    <li class="page-item">
                                        <a class="page-link" href="incidents?page=0&status=${param.status}">
                                            <i class="bi bi-chevron-double-left"></i> First
                                        </a>
                                    </li>
                                    <li class="page-item">
                                        <a class="page-link" href="incidents?page=${currentPage - 1}&status=${param.status}">
                                            <i class="bi bi-chevron-left"></i> Previous
                                        </a>
                                    </li>
                                </c:if>

                                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                                    <c:if test="${i >= currentPage - 2 and i <= currentPage + 2}">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link" href="incidents?page=${i}&status=${param.status}">
                                                    ${i + 1}
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages - 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="incidents?page=${currentPage + 1}&status=${param.status}">
                                            Next <i class="bi bi-chevron-right"></i>
                                        </a>
                                    </li>
                                    <li class="page-item">
                                        <a class="page-link" href="incidents?page=${totalPages - 1}&status=${param.status}">
                                            Last <i class="bi bi-chevron-double-right"></i>
                                        </a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5">
                        <div class="mb-3">
                            <i class="bi bi-inbox" style="font-size: 3rem; color: #6c757d;"></i>
                        </div>
                        <h4 class="text-muted mb-3">No incidents found</h4>
                        <p class="text-muted mb-4">
                            <c:choose>
                                <c:when test="${not empty param.status}">
                                    No incidents found with status "${param.status}"
                                </c:when>
                                <c:otherwise>
                                    There are no incidents in the system yet.
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <a href="incidents?action=new" class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Create First Incident
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Информация о фильтрах -->
    <c:if test="${not empty filterStatus}">
        <div class="alert alert-info mt-3">
            <i class="bi bi-filter"></i> Showing incidents with status:
            <strong>${filterStatus}</strong>
            <a href="incidents" class="float-end">Clear filter</a>
        </div>
    </c:if>
</div>

<!-- Bootstrap Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>