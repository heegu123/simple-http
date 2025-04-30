package myhttp.client;

import myhttp.client.config.ClientConfig;
import myhttp.client.http.HttpClient;
import myhttp.common.builder.HttpRequestBuilder;
import myhttp.common.model.HttpMethod;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig("localhost", 8080);
        HttpClient httpClient = new HttpClient(config);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter request (Request format : METHOD /PATH [BODY] [--close]) or EXIT: ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("EXIT")) {
                break;
            }

            // 클라이언트 요청에 Connection: close가 포함된 경우 비지속 연결 처리
            // 응답 후 소켓 연결 종료
            boolean forceClose = line.endsWith("--close");
            if (forceClose) {
                line = line.substring(0, line.length() - "--close".length()).trim();
            }

            // 한 줄에 METHOD PATH [BODY] 입력
            String[] parts = line.split(" ", 3);
            if (parts.length < 2) {
                System.out.println("Invalid format. EX): GET /users, POST /users {json}");
                continue;
            }

            HttpMethod method;
            try {
                method = HttpMethod.fromString(parts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Unsupported method: " + parts[0]);
                continue;
            }

            String path = parts[1];
            String body = (parts.length == 3) ? parts[2] : null;

            HttpRequestBuilder builder = new HttpRequestBuilder()
                    .withMethod(method)
                    .withPath(path)
                    .addHeader("Host", config.getHost() + ":" + config.getPort())
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Accept-Charset", "utf-8")
                    .addHeader("Connection", forceClose ? "close" : "keep-alive")
                    .addHeader("User-Agent", "MyHttpClient/1.0");

            if (body != null && (method == HttpMethod.POST || method == HttpMethod.PUT)) {
                builder.addHeader("Content-Type", "application/json")
                        .withBody(body);
            }

            HttpRequest req = builder.build();
            System.out.println("\n---- Request ----");
            System.out.println(new String(req.toByteArray()));

            try {
                HttpResponse response = httpClient.send(req);
                System.out.println("---- Response ----");
                System.out.println(response.toString());

                // keep-alive 상태 확인 위한 소켓 정보 출력
                System.out.println("\nCurrent socket hash=" + httpClient.getSocket().hashCode()
                        + ", isClosed=" + httpClient.getSocket().isClosed()
                        + ", isConnected=" + httpClient.getSocket().isConnected()
                        + "\n");

            } catch (Exception e) {
                System.out.println("Request failed: " + e.getMessage());
            }
            System.out.println();
        }

        scanner.close();
        System.out.println("Client exit");
    }
}
