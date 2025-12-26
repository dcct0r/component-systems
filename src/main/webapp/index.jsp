<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Incident Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h1>Incident Management System</h1>
    <p class="lead">Manage and track incidents efficiently</p>

    <div class="mt-4">
        <a href="incidents?action=new" class="btn btn-primary">
            Create New Incident
        </a>
        <a href="incidents" class="btn btn-secondary">
            View All Incidents
        </a>
        <a href="test" class="btn btn-info">
            Test Page
        </a>
    </div>

    <div class="mt-4">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title">Debug Information</h5>
            </div>
            <div class="card-body">
                <p><strong>Context Path:</strong> <%= request.getContextPath() %></p>
                <p><strong>Servlet Path:</strong> <%= request.getServletPath() %></p>
                <p><strong>Request URI:</strong> <%= request.getRequestURI() %></p>
            </div>
        </div>
    </div>
</div>
</body>
</html>