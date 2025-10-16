package com.hcmut.voltrent.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (this.writer != null) {
            throw new IllegalStateException("Writer already in use");
        }

        if (this.outputStream == null) {
            this.outputStream = new ServletOutputStream() {
                @Override public boolean isReady() { return true; }
                @Override public void setWriteListener(WriteListener listener) {}
                @Override public void write(int b) { cachedContent.write(b); }
            };
        }
        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (this.outputStream != null) {
            throw new IllegalStateException("OutputStream already in use");
        }

        if (this.writer == null) {
            this.writer = new PrintWriter(new OutputStreamWriter(cachedContent));
        }
        return this.writer;
    }

    public byte[] getCachedBody() {
        return cachedContent.toByteArray();
    }

    public void copyBodyToResponse() throws IOException {
        getResponse().getOutputStream().write(cachedContent.toByteArray());
    }
}
