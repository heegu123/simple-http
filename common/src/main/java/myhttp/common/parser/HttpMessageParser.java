package myhttp.common.parser;

import myhttp.common.exception.HttpParseException;

import java.io.InputStream;

public interface HttpMessageParser<T> {
    T parse(InputStream in) throws HttpParseException;
}
