package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.Constants.CRLF;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startLine.HttpStatus;
import org.apache.coyote.http11.response.startLine.StatusLine;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    public HttpResponse(HttpStatus httpStatus, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.statusLine = new StatusLine(httpStatus);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpResponse() {
        this(HttpStatus.OK, new ResponseHeaders(), new ResponseBody());
    }

    public void ok(String fileName) throws IOException {
        String content = ResourceReader.read(fileName);
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.responseHeaders.addContentHeaders(ContentType.findByExtension(fileName), content);
        this.responseBody = new ResponseBody(content);
    }

    public void redirect(String path) {
        this.statusLine = new StatusLine(HttpStatus.FOUND);
        this.responseHeaders.addHeader(HttpHeader.LOCATION, path);
        this.responseBody = new ResponseBody();
    }

    public void write(String content) {
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);
        this.responseHeaders.addHeader(HttpHeader.CONTENT_LENGTH, contentLength);
        this.responseBody = new ResponseBody(content);
    }

    public void setContentType(ContentType contentType) {
        responseHeaders.addHeader(HttpHeader.CONTENT_TYPE, contentType.value());
    }

    public void addCookie(Cookie cookie) {
        responseHeaders.addHeader(HttpHeader.SET_COOKIE, cookie.toCookieHeader());
    }

    public String toMessage() {
        return String.join(CRLF,
                statusLine.toMessage(),
                responseHeaders.toMessage(),
                responseBody.toMessage());
    }
}
