let currentPage = 0;
const pageSize = 15;
let sort = "code-asc"
$(document).ready(function() {
    function fetchTableData(page, sort) {
        fetch(`/api/table-data/issuers/?sort=${sort}&page=${page}&pageSize=${pageSize}`)
            .then(response => response.json())
            .then(data => {
                let companies = data.companies;
                let totalCount = data.totalCount;
                const tableBody = document.querySelector("#issuersTable tbody")
                tableBody.innerHTML = "";
                companies.forEach(company => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <th scope="row">${company.companyId}</th>
                        <td>
                            <a href="/stocks/?companyId=${company.companyId}" style="text-decoration: none; color: #000">${company.code}</a>
                        </td>
                        <td>
                            <a href="/stocks/?companyId=${company.companyId}" style="text-decoration: none; color: #000">${company.name}</a>
                        </td>
                        <td>${company.latestTurnoverDate}</td>
                    `;
                    tableBody.appendChild(row);
                });

                $("#prevPage").prop('disabled', page === 0);
                $("#nextPage").prop('disabled', page === totalCount - 1);
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