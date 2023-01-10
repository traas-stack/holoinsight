/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorDateFormat.java, v 0.1 2022年12月08日 下午5:23 jinsong.yjs Exp $
 */
public class MonitorDateFormat extends DateFormat {
  private DateFormat dateFormat;

  private SimpleDateFormat format1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

  public MonitorDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
    super.setCalendar(dateFormat.getCalendar());
  }

  @Override
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
    return dateFormat.format(date, toAppendTo, fieldPosition);
  }

  @Override
  public Date parse(String source, ParsePosition pos) {

    Date date = null;

    try {

      date = format1.parse(source, pos);
    } catch (Exception e) {

      date = dateFormat.parse(source, pos);
    }

    return date;
  }

  @Override
  public Date parse(String source) throws ParseException {

    Date date = null;

    try {
      date = format1.parse(source);
    } catch (Exception e) {
      date = dateFormat.parse(source);
    }

    return date;
  }

  @Override
  public Object clone() {
    Object format = dateFormat.clone();
    return new MonitorDateFormat((DateFormat) format);
  }
}
