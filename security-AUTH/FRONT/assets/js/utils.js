function includeHTML(file, elementId) {

  const element = document.getElementById(elementId);

  if (element) {
      fetch(file)
          .then(response => {
              if (!response.ok) {
                  throw new Error('Failed to fetch file: ' + file);
              }
              return response.text();
          })
          .then(html => {
              element.innerHTML = html;
          })
          .catch(error => {
              console.error('Error loading HTML:', error);
          });
  }
}

// Function to get a cookie value by name
function getCookie(name) {
  let matches = document.cookie.match(new RegExp(
      "(?:^|; )" + name.replace(/([.$?*|{}()[\]\\/+^])/g, '\\$1') + "=([^;]*)"
  ));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}
