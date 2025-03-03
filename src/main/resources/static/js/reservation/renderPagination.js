export function renderPagination(totalPages, currentPage, loadPageCallback) {
    const paginationContainer = document.querySelector("#pagination");
    paginationContainer.innerHTML = "";

    const pageGroupSize = 5;
    const currentGroup = Math.floor(currentPage / pageGroupSize);
    const startPage = currentGroup * pageGroupSize;
    let endPage = startPage + pageGroupSize;
    if (endPage > totalPages) endPage = totalPages;

    // 이전 그룹 버튼 («)
    const prevLi = document.createElement("li");
    prevLi.classList.add("page-item");
    const prevLink = document.createElement("a");
    prevLink.classList.add("page-link");
    prevLink.href = "#";
    prevLink.textContent = "«";
    if (startPage > 0) {
        prevLink.addEventListener("click", () => loadPageCallback(startPage - 1));
    } else {
        prevLi.classList.add("disabled");
    }
    prevLi.appendChild(prevLink);
    paginationContainer.appendChild(prevLi);

    // 페이지 번호 버튼
    for (let i = startPage; i < endPage; i++) {
        const li = document.createElement("li");
        li.classList.add("page-item");
        if (i === currentPage) {
            li.classList.add("active");
        }
        const a = document.createElement("a");
        a.classList.add("page-link");
        a.href = "#";
        a.textContent = (i + 1);
        a.addEventListener("click", () => loadPageCallback(i));
        li.appendChild(a);
        paginationContainer.appendChild(li);
    }

    // 다음 그룹 버튼 (»)
    const nextLi = document.createElement("li");
    nextLi.classList.add("page-item");
    const nextLink = document.createElement("a");
    nextLink.classList.add("page-link");
    nextLink.href = "#";
    nextLink.textContent = "»";
    if (endPage < totalPages) {
        nextLink.addEventListener("click", () => loadPageCallback(endPage));
    } else {
        nextLi.classList.add("disabled");
    }
    nextLi.appendChild(nextLink);
    paginationContainer.appendChild(nextLi);
}