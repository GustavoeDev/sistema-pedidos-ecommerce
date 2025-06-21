import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
  iterations: 28740,
  vus: 100,
};

const token = "SEU_TOKEN_AQUI"


const payload = JSON.stringify({
  items: [
    {
      productId: "d13c44c2-2395-4d3c-8e3a-b65ab8589097",
      quantity: 4,
      unitPrice: 3,
    },
  ],
});

export default function () {
  const url = "http://localhost:9000/order/";

  const params = {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    "status é 202": (r) => r.status === 202,
  });

  sleep(1);
}
