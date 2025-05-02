package myhttp.common.parser;

import myhttp.common.exception.HttpParseException;

import java.io.InputStream;

/**
 * HTTP 메시지를 파싱하는 인터페이스
 *
 * @param <T> 파싱된 메시지의 타입
 */
public interface HttpMessageParser<T> {
    T parse(InputStream in) throws HttpParseException;
}
