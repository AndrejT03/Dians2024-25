let companyId = 1;
let currentPage = 0;
const pageSize = 15;
let sort = "date-closest"
$(document).ready(function() {
    function fetchTableData(companyId, page, pageSize, sort) {
        fetch(`/api/table-data/stocks/?companyId=${companyId}&page=${page}&pageSize=${pageSize}&sort=${sort}`)
            .then(response => response.json())
            .then(data => {
                let stocks = data.stocks;
                let company = data.company;
                let totalCount = data.totalCount;
                const tableBody = document.querySelector("#stocksTable tbody")
                tableBody.innerHTML = "";
                stocks.forEach(stock => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${stock.date}</td>
                        <td>${stock.lastTransactionPrice}</td>
                        <td>${stock.maxPrice}</td>
                        <td>${stock.minPrice}</td>
                        <td>${stock.averagePrice}</td>
                        <td>${stock.averagePercentage}%</td>
                        <td>${stock.quantity}</td>
                        <td>${stock.turnoverInBestDenars}</td>
                        <td>${stock.totalTurnoverInDenars}</td>
                    `;
                    tableBody.appendChild(row);
                    $("#stocksText").text(`Daily Stocks - ${company.name}`)
                    let dropdown = document.querySelector("#issuerDropdown");
                    let option = dropdown.querySelector(`option[value="${companyId}"]`);
                    option.selected = true;
                });

                $("#prevPage").prop('disabled', page === 0);
                $("#nextPage").prop('disabled', page === totalCount - 1);
            })
            .catch(error => {
                console.error("Error fetching the API: ", error);
            });
    }

    //const companyDataElement = document.getElementById("companyData");
    //companyId = companyDataElement.getAttribute("data-company-id");

    fetchIssuersDropdownData();
    fetchTableData(companyId, currentPage, pageSize, sort);

    function fetchIssuersDropdownData() {
        fetch("/api/table-data/issuers/all")
            .then(response => response.json())
            .then(data => {
                const dropdownList = document.querySelector("#issuerDropdown")
                Object.entries(data).forEach(([compId, compCode]) => {
                    dropdownList.innerHTML += `
                    <option value="${compId}">${compCode}</option>
                    `
                });
            });
    }

    $("#issuerDropdown").change(function() {
        sort = "date-closest";
        currentPage = 0;
        companyId = $(this).val();
        fetchTableData(companyId, currentPage, pageSize, sort);
    });

    $("#sortFilterDropdown").change(function () {
        sort = $(this).val();
        currentPage = 0;
        fetchTableData(companyId, currentPage, pageSize, sort);
    });

    $("#prevPage").click(function() {
        if(currentPage > 0) {
            currentPage--;
            fetchTableData(companyId, currentPage, pageSize, sort);
        }
    });

    $("#nextPage").click(function() {
        currentPage++;
        fetchTableData(companyId, currentPage, pageSize, sort);
    });
});