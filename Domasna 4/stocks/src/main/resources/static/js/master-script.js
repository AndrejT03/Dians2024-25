$(document).ready(function() {
    /* This next function is an event listener that sends an API POST request to DBController
    * whose job is to activate the Web Scraper implemented using a Pipe and Filter architecture. With that we
    * update the database with the newest stocks information. */
    document.getElementById("btnUpdateDb").addEventListener("click", function() {
        const updatingMessage = document.getElementById("dbUpdatingStarted");
        updatingMessage.style.display = 'block';
        fetch('/api/update-database', {
            method: 'POST'
        }).then(response => {
            if(response.ok) {
                console.log('Database updated successfully.');
                updatingMessage.style.display = 'none';
                document.getElementById('dbUpdatingFinished').style.display = 'block';
            }
            else {
                console.log("Failed to update database. Status: ", response.status);
                document.getElementById('dbUpdatingFailed').style.display = 'block';
            }
        }).catch(error => {
            console.log("Error: ", error);
        });
    });
});
