import { showAlertToast } from "../utils/showAlertToast.js";
import { showConfirmToast } from "../utils/showConfirmToast.js";
import { fetchReservations } from "./reservationList.js";

export async function cancelReservationAdmin(reservationId) {
    showConfirmToast(
        "정말 예약을 취소하시겠습니까?",
        async () => {
            try {
                const response = await fetch(`/api/v1/reservations/admin/${reservationId}`, {
                    method: "DELETE",
                    credentials: "include",
                });

                if (!response.ok) {
                    showAlertToast(`예약 취소 실패: ${response.status}`);
                    return;
                }

                await fetchReservations();

                showAlertToast("예약을 취소하였습니다.");
            } catch (error) {
                showAlertToast("예약 취소 실패: " + error.message);
            }
        }
    );
}
