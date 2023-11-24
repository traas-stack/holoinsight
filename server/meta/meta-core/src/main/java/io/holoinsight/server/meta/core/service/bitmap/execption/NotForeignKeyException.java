/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap.execption;

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
