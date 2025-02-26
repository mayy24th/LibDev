document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("search-button").addEventListener("click", function() {
        const searchQuery = document.getElementById("search-input").value.trim();
        const searchType = document.getElementById("search-type").value;

        if (searchQuery) {
            window.location.href = `/books/search-simple?query=${encodeURIComponent(searchQuery)}&searchType=${encodeURIComponent(searchType)}`;
        }
    });

    const backgroundContainer = document.querySelector('.background-container');
    const images = ['/images/libImage1.jpg', '/images/libImage2.jpg', '/images/libImage3.jpg']; // 추가된 이미지 경로
    let currentImageIndex = 0;

    backgroundContainer.style.backgroundImage = `url('${images[currentImageIndex]}')`;

    function showNextImage() {
        currentImageIndex = (currentImageIndex + 1) % images.length; // 다음 이미지 인덱스 계산
        backgroundContainer.style.backgroundImage = `url('${images[currentImageIndex]}')`; // 배경 이미지 변경
    }

    setInterval(showNextImage, 5000);
});
