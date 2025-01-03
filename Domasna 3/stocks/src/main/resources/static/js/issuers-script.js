let currentPage = 0;
const pageSize = 15;
let sort = "code-asc"
$(document).ready(function() {
    function fetchTableData(page, sort) {
        fetch(`/api/table-data/issuers/?page=${page}&pageSize=${pageSize}&sort=${sort}`)
            .then(response => response.json())
            .then(data => {
                let companies = data.companies;
                let totalPageCount = data.totalPageCount;
                const tableBody = document.querySelector("#issuersTable tbody")
                tableBody.innerHTML = "";
                companies.forEach(company => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <th scope="row">${company.companyId}</th>
                        <td>${company.code}</td>
                        <td>${company.name}</td>
                        <td>${company.latestTurnoverDate}</td>
                    `;
                    tableBody.appendChild(row);
                });

                $("#prevPage").prop('disabled', page === 0);
                $("#nextPage").prop('disabled', page === totalPageCount - 1);
            })
            .catch(error => {
                console.error("Error fetching the API: ", error);
            });
    }

    fetchTableData(currentPage, sort);

    $("#sortFilterDropdown").change(function () {
        sort = $(this).val();
        currentPage = 0;
        fetchTableData(currentPage, sort);
    });

    $("#prevPage").click(function() {
        if(currentPage > 0) {
            currentPage--;
            fetchTableData(currentPage, sort);
        }
    });

    $("#nextPage").click(function() {
        currentPage++;
        fetchTableData(currentPage, sort);
    });
});