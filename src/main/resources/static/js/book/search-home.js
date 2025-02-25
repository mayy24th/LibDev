document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("search-button").addEventListener("click", function() {
        const searchQuery = document.getElementById("search-input").value.trim();
        const searchType = document.getElementById("search-type").value;

        if (searchQuery) {
            window.location.href = `/books/search-simple?query=${encodeURIComponent(searchQuery)}&searchType=${encodeURIComponent(searchType)}`;
        }
    });
});
