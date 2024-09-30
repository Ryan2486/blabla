$('#kerberosForm').on('submit', function(event) {
    event.preventDefault(); // Prevent default form submission
    var messageDiv = $('#message');
    messageDiv.empty();

    // Gather form data
    var formData = {
        server: $('#krb_server').val(),
        clockskew: $('#krb_clockskew').val(),
        udpPreferenceLimit: $('#krb_udp_limit').val(),
        DnsLookUpKdc: $('#krb_dns_kdc').val(),
        DnsLookUpRealm: $('#krb_dns_realm').val(),
        serverDomain: $('#krb_admin_server').val(),
    };
    var token = sessionStorage.getItem('token');
    
    console.log(JSON.stringify(formData));
    console.log('Token:', token);
    // Send form data via AJAX POST request
    $.ajax({
        type: 'POST',
        url: `${API_BASE_URL}/config/kerberos`, // Your server-side script to handle the POST request
        contentType: "application/json",
        data: JSON.stringify(formData),
        headers: {
            'Authorization': 'Bearer ' + token  // Add the Bearer token here
        },
        success: function(response) {
            // Create a new alert div with the response or custom message
            var alertDiv = $('<div>', {
                'class': 'alert alert-success',
                'role': 'alert',
                'text': 'Form submitted successfully! ' + response.message // Adjust message based on response
            });

            // Append the new alert div to the messageDiv
            messageDiv.append(alertDiv);
            // alert('Form submitted successfully!');
            console.log(response);
        },
        error: function(error) {
            var alertDiv = $('<div>', {
                'class': 'alert alert-warning',
                'role': 'alert',
                'text': 'Error submitting form! ' // Adjust message based on response
            });
            
            messageDiv.append(alertDiv);
            // alert('Error submitting form!');
            console.log(error);
        }
    });
});