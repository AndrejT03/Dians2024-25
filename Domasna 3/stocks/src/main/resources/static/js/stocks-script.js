let companyId = 1;
let currentPage = 0;
const pageSize = 15;
let sort = "date-closest";
let graphYear;
$(document).ready(function() {
    function fetchTableData(companyId, page, pageSize, sort) {
        fetch(`/api/table-data/stocks?companyId=${companyId}&page=${page}&pageSize=${pageSize}&sort=${sort}`)
            .then(response => response.json())
            .then(data => {
                let stocks = data.stocks;
                let totalPageCount = data.totalPageCount;
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
                    let dropdown = document.querySelector("#issuerDropdown");
                    let option = dropdown.querySelector(`option[value="${companyId}"]`);
                    option.selected = true;
                });

                $("#prevPage").prop('disabled', page === 0);
                $("#nextPage").prop('disabled', page === totalPageCount - 1);
            })
            .catch(error => {
                console.error("Error fetching the API: ", error);
            });
    }

    //const companyDataElement = document.getElementById("companyData");
    //companyId = companyDataElement.getAttribute("data-company-id");

    fetchIssuersDropdownData();
    fetchTableData(companyId, currentPage, pageSize, sort);

    const canvas = document.getElementById("chart");
    const canvasCtx = canvas.getContext('2d');
    let chartObj = null;

    fetchGraphYearsAvailable().then(() => {
        loadChart();
    });

    function fetchIssuersDropdownData() {
        fetch("/api/table-data/issuers/all")
            .then(response => response.json())
            .then(data => {
                const dropdownList = document.getElementById("issuerDropdown");
                data.forEach(company => {
                    dropdownList.innerHTML += `
                    <option value="${company.companyId}">${company.name} - ${company.code}</option>
                    `
                });
            });
    }

    function fetchGraphYearsAvailable() {
        return fetch(`/api/table-data/stocks/graph-years?companyId=${companyId}`)
            .then(response => response.json())
            .then(data => {
               const dropdownList = document.getElementById("yearDropdown");
               dropdownList.innerHTML = "";
               data.forEach(year => {
                  dropdownList.innerHTML += `
                  <option value="${year}">${year}</option>
                  `
               });

               graphYear = dropdownList.value;
            });
    }

    $("#issuerDropdown").change(function() {
        sort = "date-closest";
        currentPage = 0;
        companyId = $(this).val();
        fetchTableData(companyId, currentPage, pageSize, sort);
        fetchGraphYearsAvailable().then(() => {
            loadChart();
        });
    });

    $("#sortFilterDropdown").change(function () {
        sort = $(this).val();
        currentPage = 0;
        fetchTableData(companyId, currentPage, pageSize, sort);
    });

    $("#yearDropdown").change(function () {
       graphYear = $(this).val();
       loadChart();
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

    function loadChart() {
        fetch(`/api/table-data/stocks/graph?companyId=${companyId}&year=${graphYear}`)
            .then(response => response.json())
            .then(data => {
                const xValues = [];
                const yValues = [];
               data.forEach(dayInfo => {
                   xValues.push(dayInfo.date);
                   yValues.push(dayInfo.price);
               });

               canvasCtx.clearRect(0, 0, canvas.width, canvas.height);
               chartObj !== null && chartObj.destroy();
               chartObj = new Chart("chart", {
                  type: "line",
                  data: {
                      labels: xValues,
                      datasets: [{
                          fill: false,
                          lineTension: 0,
                          borderColor: 'rgb(75, 192, 192)',
                          backgroundColor: '#466cd9',
                          data: yValues
                      }]
                  },
                   options: {
                      legend: {display: false},
                       title: {
                          display: false
                       },
                       elements: {
                          point: {
                              radius: 2,
                              hitRadius: 3
                          }
                       }
                   }
               });
            });
    }
});