import {reissue} from "./reissue.js";

export async function apiRequestRetry(url, options = {}){
    try {
        let response = await fetch(url, options);

        if (response.status === 401 || response.status === 500) {
            const reissued = await reissue();

            if (reissued) {
                response = await fetch(url, options);
            } else {
                return null;
            }
        }

        const result = response.json();

        if (!response.ok) {
            throw new Error(result.data);
        }

        return result
    } catch (error) {
        console.error(error);
        alert(result.data());
        return null;
    }
}