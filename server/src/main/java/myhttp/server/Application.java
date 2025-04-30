package myhttp.server;

import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.parser.HttpRequestParser;
import myhttp.server.config.JpaConfig;
import myhttp.server.handler.RequestHandler;
import myhttp.server.router.Router;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        // JPA EntityManagerFactory 싱글톤
        EntityManagerFactory emf = JpaConfig.getEntityManagerFactory();
        // 요청 URI 기반 핸들러 매핑
        Router router = new Router(emf);
        // 고정 크기 스레드풀 생성
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MyHTTP1.1 Server listening on port " + PORT);
            while (true) {
                // 클라이언트 연결 대기
                Socket client = serverSocket.accept();
                // 연결이 들어오면 스레드풀에 처리 위임
                pool.submit(() -> handleClient(client, router));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
            JpaConfig.shutdown();
        }
    }

    /**
     * 한 소켓 연결에서 여러 HTTP 요청을 연속 처리하여
     * HTTP/1.1 persistent connection(keep-alive)을 지원
     */
    private static void handleClient(Socket socket, Router router) {
        try (var in = socket.getInputStream();
             var out = socket.getOutputStream()) {

            while (true) {
                // 1. 요청 파싱
                HttpRequest request = new HttpRequestParser().parse(in);
                // 2. URI에 맞는 핸들러 호출
                RequestHandler handler = router.route(request);
                HttpResponse response = handler.handle(request);
                // 3. 응답 바이트 전송
                out.write(response.toByteArray());
                out.flush();
                // 4. 클라이언트에서 Connection: close 지시하면 루프 탈출하여 소켓 닫음
                // 그렇지 않으면 while문으로 돌아가서 같은 소켓으로 다음 요청 처리
                String connHdr = request.getHeaders().getFirst("Connection").orElse("");
                if (connHdr.equalsIgnoreCase("close")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }
}
