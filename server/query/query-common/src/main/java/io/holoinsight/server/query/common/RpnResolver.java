/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.common;

import lombok.extern.java.Log;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * reverse polish notation resolver
 *
 * @author xiangwanpeng
 * @version : RpnResolver.java, v 0.1 2020年05月19日 11:39 xiangwanpeng Exp $
 */
@Log
public class RpnResolver {

  private Stack<String> op = new Stack<>();

  public List<String> expr2Infix(String expr) {
    Objects.requireNonNull(expr);
    Pattern pattern_expr = Pattern.compile("(\\(|\\)|\\+|-|\\*|/|\\d+(\\.\\d+)?|\\w+(\\{\\w+})?)");
    Matcher matcher = pattern_expr.matcher(expr);
    List<String> exprs = new ArrayList<>();
    while (matcher.find()) {
      exprs.add(matcher.group(1));
    }
    return exprs;
  }

  public Double getv(String op, Double f1, Double f2) {
    if ("+".equals(op)) {
      return f2 + f1;
    } else if ("-".equals(op)) {
      return f2 - f1;
    } else if ("*".equals(op)) {
      return f2 * f1;
    } else if ("/".equals(op)) {
      return f1 == 0 ? 0 : (f2 / f1);
    } else {
      throw new IllegalArgumentException("illegal opType : " + op);
    }
  }

  /**
   * calculate the value of the reverse Polish expression
   *
   * @param rp - reverse Polish expression
   * @return - result of the expression
   */
  public double calrp(List rp) {
    Stack<Double> v = new Stack<>();
    for (Object arg : rp) {
      // if is operand, push to the stack
      if (arg instanceof Number) {
        v.push(Double.valueOf(arg.toString()));

        // if is operator, calculate the result
        // with top 2 operands in the stack,
        // push the result into the stack
      } else if (arg instanceof String) {
        v.push(getv(arg.toString(), v.pop(), v.pop()));
      } else {
        throw new IllegalArgumentException("illegal arg : " + arg);
      }
    }
    return v.pop();
  }

  /**
   * from infix to postfix
   *
   * @param infix - String in the form of infix
   * @return String in the form of postfix
   */
  public List getrp(List infix) {
    List out = new ArrayList();
    for (Object arg : infix) {
      // if is operand, add to
      // the output stream directly
      if (arg instanceof Number) {
        out.add(arg);
        continue;
      } else if (arg instanceof String) {
        String chStr = (String) arg;
        // if is '(', push to the stack directly
        if ("(".equals(chStr)) {
          op.push(chStr);
        }
        // if is '+' or '-', pop the operator
        // from the stack until '(' and add to
        // the output stream
        // push the operator to the stack
        if ("+".equals(chStr) || "-".equals(chStr)) {
          while (!op.empty() && (!"(".equals(op.peek()))) {
            out.add(op.pop());
          }
          op.push(chStr);
          continue;
        }
        // if is '*' or '/', pop the operator stack and
        // add to the output stream
        // until lower priority or '('
        // push the operator to the stack
        if ("*".equals(chStr) || "/".equals(chStr)) {
          while (!op.empty() && ("*".equals(op.peek()) || "/".equals(op.peek()))) {
            out.add(op.pop());
          }
          op.push(chStr);
          continue;
        }
        // if is ')' pop the operator stack and
        // add to the output stream until '(',
        // pop '('
        if (")".equals(arg)) {
          while (!op.empty() && !"(".equals(op.peek())) {
            out.add(op.pop());
          }
          op.pop();
          continue;
        }
      } else {
        throw new IllegalArgumentException("illegal arg : " + arg);
      }
    }
    while (!op.empty()) {
      out.add(op.pop());
    }
    return out;
  }

  /**
   * calculate by infix express
   *
   * @param args
   * @return
   */
  public double calByInfix(List args) throws Exception {
    double result = 0d;
    result = calrp(getrp(args));
    return result;
  }

  public static void main(String[] args) throws Exception {
    RpnResolver rpnResolver = new RpnResolver();
    String expr = "((a1+2.22)*3.58+b2)/(c3*99)";
    List<String> exprArgs = rpnResolver.expr2Infix(expr);
    // System.out.println(exprArgs);
    List list = new ArrayList();
    list.addAll(Arrays.asList(2700.0, "/", 0));

    // System.out.println(rpnResolver.getrp(list));
    // System.out.println(rpnResolver.calByInfix(list));
  }
}
