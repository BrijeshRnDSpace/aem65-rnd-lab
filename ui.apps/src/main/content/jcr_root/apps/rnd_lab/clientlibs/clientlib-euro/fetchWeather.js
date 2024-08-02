   (function() {
        function fetchTemperature(city) {
            var request = new XMLHttpRequest();
            request.open('GET', 'http://localhost:4502/bin/practiceweather?city='+city, true);
            request.setRequestHeader('Content-Type', 'application/json');
            request.onreadystatechange = function() {
                if (request.readyState === 4 && request.status === 200) {
                    var response = JSON.parse(request.responseText);
                    if (response.temperature) {
                        document.getElementById('temperature-value').textContent = response.temperature;
                    } else {
                        console.error('Temperature property not found in the response.');
                    }
                }
            };
            request.send();
        }
        document.addEventListener('DOMContentLoaded', function() {
            var city = document.getElementById('temperature-city').textContent;
            fetchTemperature(city);
        });
    })();