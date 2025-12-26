<div class="mt-4">
    <h5>Create Incident (Rust)</h5>
    <form id="createIncidentForm">
        <div class="mb-3">
            <label for="title" class="form-label">Title</label>
            <input type="text" class="form-control" id="title" name="title" required>
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <input type="text" class="form-control" id="description" name="description" required>
        </div>
        <div class="mb-3">
            <label for="priority" class="form-label">Priority</label>
            <select id="priority" name="priority" class="form-select">
                <option value="Low">Low</option>
                <option value="Medium">Medium</option>
                <option value="High">High</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Create Incident</button>
    </form>

    <div id="createResult" class="mt-2"></div>
</div>

<script>
    document.getElementById('createIncidentForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const title = document.getElementById('title').value;
        const description = document.getElementById('description').value;
        const priority = document.getElementById('priority').value;

        fetch('http://localhost:8080/incident/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ title, description, priority })
        })
            .then(res => res.text())
            .then(data => {
                document.getElementById('createResult').innerText = 'Incident ID: ' + data;
            })
            .catch(err => {
                document.getElementById('createResult').innerText = 'Error: ' + err;
            });
    });
</script>
