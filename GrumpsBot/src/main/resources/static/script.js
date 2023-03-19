// Функция для отправки GET запроса и обновления контента на странице
function updateContent() {
    // Отправляем GET запрос на сервер
    fetch('http://localhost:8080/alerts/points', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json()) // Преобразуем ответ в формат JSON
        .then(data => {
            // Проверяем, что ответ не пустой
            if (data.path) {
                // Обновляем контент на странице
                if (data.path.endsWith('.mp4')) {
                    // Если это видео, создаем и вставляем элемент <video>
                    const video = document.createElement('video');
                    video.src = data.path;
                    video.autoplay = true;
                    video.controls = true;
                    video.style.width = '100%';
                    document.getElementById('content').innerHTML = '';
                    document.getElementById('content').appendChild(video);
                } else if (data.path.endsWith('.png')) {
                    // Если это картинка, создаем и вставляем элемент <img>
                    const img = document.createElement('img');
                    img.src = data.path;
                    img.style.width = '100%';
                    document.getElementById('content').innerHTML = '';
                    document.getElementById('content').appendChild(img);
                } else if (data.path.endsWith('.gif')) {
                    // Если это GIF-изображение, создаем и вставляем элемент <img>
                    const img = document.createElement('img');
                    img.src = data.path;
                    img.style.width = '100%';
                    document.getElementById('content').innerHTML = '';
                    document.getElementById('content').appendChild(img);
                    // Устанавливаем таймер на скрытие контента после заданного времени
                    setTimeout(() => {
                        document.getElementById('content').innerHTML = '';
                    }, data.time * 1000);
                }
                // Устанавливаем таймер на скрытие контента после заданного времени
                setTimeout(() => {
                    document.getElementById('content').innerHTML = '';
                }, data.time * 1000);
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}

// Устанавливаем интервал для отправки GET запроса каждые n секунд
setInterval(() => {
    updateContent();
}, 1 * 1000); // замените n на желаемый интервал в секундах