const API_URL = "http://localhost:8082";

var token = localStorage.getItem('token');

function updateAddButton() {
    $('.add-column').remove();
    $('.remove-column').remove();
    var lastColumn = $('#columns .input-group:last');
    lastColumn.append(`
        <button type="button" class="btn btn-outline-secondary add-column">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus" viewBox="0 0 16 16">
                <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
            </svg>
        </button>
    `);
    $('#columns .input-group:not(:last)').append(`
        <button type="button" class="btn btn-outline-danger remove-column">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
                <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
            </svg>
        </button>
    `);
}

async function fetchTables(){
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `${API_URL}/schema/tables`,
            type: 'POST',
            processData: false,
            contentType: false,
            headers: {
                'Authorization': 'Bearer ' + token  // Add the Bearer token here
            },
            success: function(response) {
                // Resolve the promise with the data
                resolve(response.data);
            },
            error: function(xhr, status, error) {
                // Reject the promise in case of error
                reject('Error fetching tables: ' + error);
            }
        });
    });
}

function addOption(listOfTables){
    var datalist = $('#options');
    // Clear the current options (if necessary)
    datalist.empty();

    // Map over the list of tables and append each as an option
    listOfTables.map(function(table) {
        // Create a new option element and append it to the datalist
        datalist.append($('<option>', {
            value: table
        }));
    });
}

async function initTableName(){
    try {
        var listOfNames = await fetchTables();
        addOption(listOfNames);
    } catch (error) {
        console.error(error);
    }
}

function addColumnsToList(columns) {
    var list = $('#columnsList');
    list.empty();  // Clear existing columns

    columns.forEach(function(column) {
        var listItem = `
            <li class="column-item input-group mb-3 ui-state-default">
                <span class="ui-icon ui-icon-arrowthick-2-n-s"></span>
                <input type="text" class="form-control column-input" name="column[]" value="${column}" />
                <button class="remove-button btn btn-outline-danger">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                        <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
                    </svg>
                </button>
            </li>
        `;
        list.append(listItem);
    });

    // Enable the list to be sortable
    list.sortable();

    // Bind remove functionality to the remove buttons
    $('.remove-button').on('click', function() {
        $(this).parent().remove();  // Remove the parent <li>
    });
}

async function fetchColumns(tableName){
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `${API_URL}/schema/columns`,
            type: 'POST',
            data:tableName,
            processData: false,
            contentType: false,
            headers: {
                'Authorization': 'Bearer ' + token  // Add the Bearer token here
            },
            success: function(response) {
                console.log(response);
                resolve(response.data);
            },
            error: function(xhr, status, error) {
                reject(error);
            }
        });
    });
}

// Bind the change event to the select element
function bindSelectChange() {
    $('#tableName').on('change', async function() {
        var selectedTable = $(this).val();  // Get the selected table name

        // Fetch columns for the selected table
        try {
            var columns = await fetchColumns(selectedTable);
            addColumnsToList(columns);  // Populate the input list
        } catch (error) {
            console.error('Error fetching columns: ', error);
        }
    });
}

function loader(isHidden){
    if(isHidden){
        $('#loader').removeAttr("hidden");
    }else{
        $('#loader').attr("hidden", "hidden");
    }
}

// Add new column input
$('#columns').on('click' , '.add-column', function() {
    var newColumn = `
        <div class="mb-3 input-group">
            <input type="text" class="form-control column-input" name="columns[]" placeholder="Column name" required>
        </div>
    `;
    $('#columns').append(newColumn);
    updateAddButton();
});

// Remove column input
$('#columns').on('click', '.remove-column', function() {
    $(this).closest('.input-group').remove();
    updateAddButton();
});
// Form submission
$('#uploadForm').submit(function(e) {
    e.preventDefault();
    var formData = new FormData();
    var isHidden = true;
    // Add file and table name to FormData
    formData.append('file', $('#file')[0].files[0]);
    formData.append('tableName', $('#tableName').val());
    
    // Collect column names into an array
    var columns = $('.column-input').map(function() {
        return $(this).val();
    }).get();
    
    // Add columns array to FormData
    formData.append('columns', JSON.stringify(columns));
    loader(isHidden)
    $("#message").empty();
    $.ajax({
        url: `${API_URL}/files/upload_generic`,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        headers: {
            'Authorization': 'Bearer ' + token  // Add the Bearer token here
        },
        success: function(response) {
            isHidden = false;
            loader(isHidden);
            let htmlStr = `<div class="alert alert-success" role="alert">`
             + response.message + 
            `</div>`
            $("#message").append(htmlStr);
            fetchTables();
        },
        error: function(xhr, status, error) {
            isHidden = false;
            loader(isHidden);
            console.log(error);
            let htmlStr = 
            `<div class="alert alert-danger" role="alert">
            Error uploading data `+ error + 
            `</div>`
            $("#message").append(htmlStr);
            // alert('Error uploading data: ' + error);
        }
    });
});