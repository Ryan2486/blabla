const API_BASE_URL = "http://localhost:8081";

function logout(){
    localStorage.removeItem('token');
    window.location.replace("login.html");
}

$('#logout').on('click', function(e) {
    e.preventDefault();
    logout();
});

$('#login-form').on('submit', function(e) {
    e.preventDefault();  // Prevent form submission
    var messageDiv = $('#error');
    messageDiv.empty();
    // Collect form data
    var formData = {
        username: $('#matricule').val(),
        password: $('#mdp').val()
    };
    console.log(formData);
    
    // Send AJAX request
    $.ajax({
        url: `${API_BASE_URL}/login/authenticate`,  // Replace with your server endpoint
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(formData),
        success: function(response) {
            var token = response.data.token;
            console.log(token);
            // Storing the token in a cookie
            document.cookie = "token=" + token + "; path=/;";
        },
        error: function(xhr, status, error) {
            // Handle error (you can display an error message, etc.)
            var response = `
            <div class="alert alert-danger" role="alert">
                Login failed
            </div>
             `
            messageDiv.html(response);
            console.log(xhr.responseText);
        }
    });
});

// Function to check for token in localStorage
function checkToken() {
    // Get the token from localStorage
    const token = localStorage.getItem('token');

    // If token is not found, redirect to login page
    if (!token) {
        window.location.href = 'login.html';
    } else {
        console.log('Token found:', token); // You can remove this or use it for debugging
    }
}

// Call the checkToken function when the page loads
window.onload = checkToken;