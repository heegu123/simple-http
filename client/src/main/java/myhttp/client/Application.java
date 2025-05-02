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
        ClientConfig config = new ClientConfig("localhost", 8080); // 서버 호스트와 포트 설정
        HttpClient httpClient = new HttpClient(config); // HTTP 클라이언트 생성
        Scanner scanner = new Scanner(System.in); // 사용자 입력을 위한 스캐너 생성

        while (true) {
            // 사용자에게 요청 입력 받기
            System.out.print("Enter request (Request format : METHOD /PATH [BODY] [--close]) or EXIT: ");
            // 사용자 입력을 한 줄로 받기
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("EXIT")) { // "EXIT" 입력 시 종료
                break; // 루프 종료
            }

            // 클라이언트 요청에 Connection: close가 포함된 경우 비지속 연결 처리
            // 응답 후 소켓 연결 종료
            boolean forceClose = line.endsWith("--close"); // 종료 플래그 확인
            if (forceClose) { // 종료 플래그가 설정된 경우
                line = line.substring(0, line.length() - "--close".length()).trim(); // 종료 플래그 제거
            }

            // 한 줄에 METHOD PATH [BODY] 입력
            String[] parts = line.split(" ", 3); // 공백으로 분리
            if (parts.length < 2) { // 입력 형식이 잘못된 경우
                System.out.println("Invalid format. EX): GET /users, POST /users {json}");
                continue;
            }

            HttpMethod method;
            try {
                method = HttpMethod.fromString(parts[0]); // HTTP 메서드 파싱
            } catch (IllegalArgumentException e) {
                System.out.println("Unsupported method: " + parts[0]); // 지원하지 않는 메서드
                continue;
            }

            String path = parts[1]; // 요청 경로
            String body = (parts.length == 3) ? parts[2] : null; // 요청 본문

            HttpRequestBuilder builder = new HttpRequestBuilder() // HTTP 요청 빌더 생성
                    .withMethod(method) // HTTP 메서드 설정
                    .withPath(path) // 요청 경로 설정
                    .addHeader("Host", config.getHost() + ":" + config.getPort()) // 호스트 헤더 설정
                    .addHeader("Accept", "application/json, text/plain, */*") // 수신 가능한 타입 설정
                    .addHeader("Accept-Charset", "utf-8") // 수신 가능한 문자셋 설정
                    .addHeader("Connection", forceClose ? "close" : "keep-alive") // 연결 설정
                    .addHeader("User-Agent", "MyHttpClient/1.0"); // 사용자 에이전트 설정
            // HTTP 요청 본문이 있는 경우 설정
            if (body != null && (method == HttpMethod.POST || method == HttpMethod.PUT)) {
                builder.addHeader("Content-Type", "application/json") // 본문 타입 설정
                        .withBody(body);
            }

            HttpRequest req = builder.build(); // HTTP 요청 빌드
            System.out.println("\n---- Request ----");
            System.out.println(new String(req.toByteArray())); // 요청 바이트 배열 출력

            try {
                HttpResponse response = httpClient.send(req); // HTTP 요청 전송 및 응답 수신
                System.out.println("---- Response ----"); // 응답 출력
                System.out.println(response.toString()); // 응답 문자열 출력

                // keep-alive 상태 확인 위한 소켓 정보 출력
                System.out.println("\nCurrent socket hash=" + httpClient.getSocket().hashCode() // 소켓 해시코드
                        + ", isClosed=" + httpClient.getSocket().isClosed() // 소켓 닫힘 여부
                        + ", isConnected=" + httpClient.getSocket().isConnected() // 소켓 연결 여부
                        + "\n"); // 소켓 정보 출력

            } catch (Exception e) {
                System.out.println("Request failed: " + e.getMessage()); // 요청 실패 시 예외 메시지 출력
            }
            System.out.println();
        }

        scanner.close(); // 스캐너 닫기
        System.out.println("Client exit"); // 종료 메시지 출력
    }
}
