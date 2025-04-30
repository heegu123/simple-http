package myhttp.server.handler;

import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request);
}