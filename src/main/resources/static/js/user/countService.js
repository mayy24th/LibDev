import {showAlertToast} from "../utils/showAlertToast.js";
import {apiRequestRetry} from "../utils/apiRequsetRetry.js";

(async () => {
    try {
        const response = await apiRequestRetry("/api/v1/users/count", {
            method: "GET"
        });

        if (!response.success || !response.data) {
            showAlertToast("정보를 가져오는 중 오류가 발생하였습니다.");
            return;
        }

        const { borrowCount, returnCount, reservationCount, reviewCount } = response.data;

        const counts = document.querySelectorAll(".count");
        counts[0].textContent = borrowCount;
        counts[1].textContent = returnCount;
        counts[2].textContent = reservationCount;
        counts[3].textContent = reviewCount;
    } catch (error) {
        console.error("Error fetching data:", error);
    }
})();