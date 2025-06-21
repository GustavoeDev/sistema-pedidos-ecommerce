import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
  iterations: 28740,
  vus: 100,
};

const token =
  "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJQa2prem9UNk1BekRNT0FkTHQtTk5WeTNnOHctME9tNkJ4OW5zbGhGU1lzIn0.eyJleHAiOjE3NTA1MTA3NDAsImlhdCI6MTc1MDUxMDQ0MCwianRpIjoiYWZiMDViMTItNTEyZS00N2RkLWJiNDItMmU5NGMwZjIyMjE1IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL2d1c3Rhdm9lZGV2IiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjgyN2I3YWY5LWZhNjktNGRjYy05MDg2LTIxOTdhYThlNTMyOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdhdGV3YXkiLCJzZXNzaW9uX3N0YXRlIjoiMzEyZTgyOTEtMjBlNS00YmM2LWE5ZGQtZmViYjE1ZmQ0YjEyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjkwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtZ3VzdGF2b2VkZXYiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbInVzZXIiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwic2lkIjoiMzEyZTgyOTEtMjBlNS00YmM2LWE5ZGQtZmViYjE1ZmQ0YjEyIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJndXN0YXZvIn0.OMJj698gn8bGxkPJ3XueezsGmDDIEwCmSelgixnxAuIGN_R4i3RDY69-qO77TFE9sV64b4Wpv8Z0xaEdlg0KzlYyT8_sBcQe-cXmPzfjuaHhN-q2D0jfYFfma0DRLS1Le66EluZfQfRggneVrgHQvyni8TALqxaNgVJ_KvKy70JONYZ-sw1UcdjEGBv_ritzHNDlWOP-42sGYQSremgovD8ao6fUkETUWTopeHrajMgvNvrpvxhcQD3ImJi6d33AmxwqB9TU1cFYBZnBvFkhXw79nlMNEQmT1S02rNe35wyenwNdiA0i2FmafQNUS-dNc-Ow0by0Hk5sSkkX_TkDNA";

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
    "status é 201": (r) => r.status === 201,
  });

  sleep(1);
}
