const token = getCookie('token');

$('#ldapForm').on('submit', function(event) {
    event.preventDefault(); // Prevent default form submission
    var  messageDiv = $('#message');
    messageDiv.empty();
    // Gather form data into an object
    var formData = {
            ldapUrl: $('#ldap_url').val(),
            port: $('#ldap_port').val(),
            domainName: $('#ldap_base_dn').val(),
            authType: $('#ldap_auth_type').val(),
            suffix: $('#ldap_suffix').val(),
            min: $('#ldap_pool_min').val(),
            max: $('#ldap_pool_max').val(),
            timeoutPool: $('#ldap_pool_timeout').val(),
            timeoutConnect: $('#ldap_connect_timeout').val(),
            timeoutRead: $('#ldap_read_timeout').val(),
            useSSL: $('#SSL').val(),
            keystorePath: $('#ldap_keystore_path').val(),
            keyStorePwd: $('#ldap_keystore_pwd').val()
    };

    console.log(JSON.stringify(formData));
    console.log(token);
    // Send form data via AJAX POST request
    $.ajax({
        type: 'POST',
        url: `${API_BASE_URL}/config/ldap`, // Your server-side script to handle the POST request
        data: JSON.stringify(formData),
        contentType: "application/json",
        headers: {
            'Authorization': 'Bearer ' + token  // Add the Bearer token here
        },
        success: function(response) {
            // Create a new alert div with the response or custom message
            var alertDiv = $('<div>', {
                'class': 'alert alert-success',
                'role': 'alert',
                'text': 'Form submitted successfully! Response: ' + response.message // Adjust message based on response
            });

            // Append the new alert div to the messageDiv
            messageDiv.append(alertDiv);
            // alert('Form submitted successfully!');
            console.log(response);
        },
        error: function(error) {
            // Create a new alert div with the response or custom message
            var alertDiv = $('<div>', {
                'class': 'alert alert-warning',
                'role': 'alert',
                'text': 'Error submitting form! ' // Adjust message based on response
            });

            // Append the new alert div to the messageDiv
            messageDiv.append(alertDiv);
            // alert('Error submitting form!');
            console.log(error);
        }
    });
});