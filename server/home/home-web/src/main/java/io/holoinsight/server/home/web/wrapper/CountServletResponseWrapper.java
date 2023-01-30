/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @author xiangwanpeng
 * @version : CountServletResponseWrapper.java, v 0.1 2022年12月06日 13:55 xiangwanpeng Exp $
 */
public class CountServletResponseWrapper extends HttpServletResponseWrapper {
  private CountServletOutputStream countOs;

  public CountServletResponseWrapper(HttpServletResponse response) {
    super(response);
  }

  /**
   * 强制刷
   *
   * @return
   * @throws IOException
   */
  public int getSize() throws IOException {
    flushBuffer();
    if (countOs == null) {
      return 0;
    }
    return countOs.size();
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (countOs == null) {
      countOs = new CountServletOutputStream(getResponse().getOutputStream());
    }
    return countOs;
  }

  class CountServletOutputStream extends ServletOutputStream {

    private ServletOutputStream os;
    private int count;

    public CountServletOutputStream(ServletOutputStream oStream) {
      this.os = oStream;
    }

    @Override
    public void write(int b) throws IOException {
      count++;
      os.write(b);
    }

    public int size() {
      return count;
    }

    @Override
    public boolean isReady() {
      return os.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      os.setWriteListener(writeListener);
    }
  }
}
