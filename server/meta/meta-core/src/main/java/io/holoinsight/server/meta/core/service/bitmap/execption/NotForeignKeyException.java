/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap.execption;

/**
 * @author wangfei
 * @version $Id: NotForeignKeyException.java, v 0.1 2019年08月06日 9:01 PM wangfei Exp $
 */
public class NotForeignKeyException extends Exception {

  public NotForeignKeyException(String table, String column) {
    super(String.format("The column[%s] of table[%s] is not a foreign key.", column, table));
    this.table = table;
    Column = column;
  }

  private String table;
  private String Column;

  public String getTable() {
    return table;
  }

  public String getColumn() {
    return Column;
  }

}
