package myhttp.common.factory;

import myhttp.common.parser.HttpMessageParser;
import myhttp.common.parser.HttpRequestParser;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.parser.HttpResponseParser;

public class HttpParserFactory {
    public static HttpMessageParser<HttpRequest> requestParser() {
        return new HttpRequestParser();
    }
    public static HttpMessageParser<HttpResponse> responseParser() {
        return new HttpResponseParser();
    }
}