package myhttp.server.handler;

import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;

// HTTP 요청을 처리하는 인터페이스
public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}