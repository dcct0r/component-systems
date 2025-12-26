<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title><c:choose><c:when
            test="${not empty incident}">Edit Incident</c:when><c:otherwise>Create New Incident</c:otherwise></c:choose></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .char-count {
            font-size: 0.8rem;
            color: #6c757d;
        }

        .char-count.warning {
            color: #dc3545;
        }
    </style>
    <script>
        function updateCharCount(inputId, maxLength) {
            const input = document.getElementById(inputId);
            const counter = document.getElementById(inputId + '-counter');
            if (input && counter) {
                const count = input.value.length;
                counter.textContent = count + '/' + maxLength;
                counter.className = 'char-count ' + (count > maxLength ? 'warning' : '');
            }
        }
    </script>
</head>
<body>
<div class="container mt-4">
    <h1><c:choose><c:when
            test="${not empty incident}">Edit Incident</c:when><c:otherwise>Create New Incident</c:otherwise></c:choose></h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="incidents" method="post" class="needs-validation" novalidate>
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="${incident.id}">

        <div class="mb-3">
            <label for="title" class="form-label">Title *</label>
            <input type="text" class="form-control" id="title" name="title"
                   value="<c:out value='${incident.title}'/>" maxlength="100" required
                   onkeyup="updateCharCount('title', 100)">
            <div id="title-counter" class="char-count">
                ${fn:length(incident.title)}/100
            </div>
            <div class="invalid-feedback">
                Title is required (max 100 characters)
            </div>
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <textarea class="form-control" id="description" name="description"
                      rows="4" maxlength="500"
                      onkeyup="updateCharCount('description', 500)"><c:out value='${incident.description}'/></textarea>
            <div id="description-counter" class="char-count">
                ${fn:length(incident.description)}/500
            </div>
        </div>

        <div class="mb-3">
            <label for="priority" class="form-label">Priority *</label>
            <select class="form-select" id="priority" name="priority" required>
                <option value="">Select Priority</option>
                <option value="LOW" <c:if test="${incident.priority == 'LOW'}">selected</c:if>>Low</option>
                <option value="MEDIUM" <c:if test="${incident.priority == 'MEDIUM'}">selected</c:if>>Medium</option>
                <option value="HIGH" <c:if test="${incident.priority == 'HIGH'}">selected</c:if>>High</option>
            </select>
            <div class="invalid-feedback">
                Please select a priority
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="incidents" class="btn btn-secondary">Cancel</a>
    </form>
</div>

<script>
    (function () {
        'use strict'
        const forms = document.querySelectorAll('.needs-validation')
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
    })()
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>