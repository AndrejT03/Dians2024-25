$(document).ready(function() {
    document.getElementById("btn-update-db").addEventListener("click", function() {
        const updatingMessage = document.getElementById("db-updating-started");
        updatingMessage.style.display = 'block';
        fetch('/api/update-database', {
            method: 'POST'
        }).then(response => {
            if(response.ok) {
                console.log('Database updated successfully.');
                updatingMessage.style.display = 'none';
                document.getElementById('db-updating-finished').style.display = 'block';
            }
            else {
                console.log("Failed to update database. Status: ", response.status);
                document.getElementById('db-updating-failed').style.display = 'block';
            }
        }).catch(error => {
            console.log("Error: ", error);
        });
    });
});
