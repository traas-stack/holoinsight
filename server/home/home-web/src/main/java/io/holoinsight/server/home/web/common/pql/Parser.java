/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql;

import io.holoinsight.server.home.web.common.pql.expr.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-01-02 8:19 下午
 */

@Slf4j
public class Parser {

  private Lexer lexer;

  public void initLexer(String s) {
    lexer = new Lexer("", new ArrayList<>(), new ArrayList<>(), s, s);
  }

  public Expr parse(String s) throws PqlException {
    lexer.Next();
    Expr e = parseExpr();
    return e;
  }

  public Expr parseExpr() throws PqlException {
    Expr e = parseSingleExpr();
    while (true) {
      if (!ParseStringUtil.isBinaryOp(lexer.getToken())) {
        return e;
      }

      BinaryOpExpr be = new BinaryOpExpr();
      be.setOp(lexer.getToken().toLowerCase());
      be.setLeft(e);
      lexer.Next();

      if (ParseStringUtil.isBinaryOpBoolModifier(lexer.getToken())) {
        if (!ParseStringUtil.IsBinaryOpCmp(be.getOp())) {
          log.error("bool modifier cannot be applied to {}", be.getOp());
          return null;
        }
        be.setBool(true);
        lexer.Next();
      }

      if (ParseStringUtil.isBinaryOpGroupModifier(lexer.getToken())) {
        parseModifierExpr(be.getGroupModifier());
        if (ParseStringUtil.isBinaryOpJoinModifier(lexer.getToken())) {
          if (ParseStringUtil.isBinaryOpLogicalSet(be.getOp())) {
            return null;
          }
          parseModifierExpr(be.getJoinModifier());
        }
      }

      Expr e2 = parseSingleExpr();
      be.setRight(e2);
      e = balanceBinaryOp(be);
    }
  }

  public Expr balanceBinaryOp(BinaryOpExpr be) {
    BinaryOpExpr bel = null;
    try {
      bel = (BinaryOpExpr) be.getLeft();
    } catch (Exception e) {
      log.info("can not convert to BinaryOpExpr");
      return be;
    }
    int lp = ParseStringUtil.binaryOpPriority(bel.getOp());
    int rp = ParseStringUtil.binaryOpPriority(be.getOp());
    if (rp < lp) {
      return be;
    }
    if (rp == lp && !ParseStringUtil.isRightAssociativeBinaryOp(be.getOp())) {
      return be;
    }
    be.setLeft(bel.getRight());
    bel.setRight(balanceBinaryOp(be));
    return bel;
  }

  public Boolean isWith(String s) {
    s = s.toLowerCase();
    return s.equals("with");
  }

  public Expr parseSingleExpr() throws PqlException {
    if (isWith(lexer.getToken())) {
      lexer.Next();
      String nextToken = lexer.getToken();
      lexer.Prev();
      if (nextToken.equals("(")) {
        return parseWithExpr();
      }
    }
    Expr e = parseSingleExprWithoutRollupSuffix();
    if (!isRollupStartToken(lexer.getToken())) {
      return e;
    }
    return parseRollupExpr(e);
  }

  public Expr parseRollupExpr(Expr arg) throws PqlException {
    RollupExpr re = new RollupExpr();
    re.setExpr(arg);
    if (lexer.getToken().equals("[")) {
      re = parseWindowAndStep();
      re.setExpr(arg);
      if (!ParseStringUtil.isOffset(lexer.getToken()) && !lexer.getToken().equals("@")) {
        return re;
      }
    }

    if (lexer.getToken().equals("@")) {
      Expr at = parseAtExpr();
      re.setAt(at);
    }

    if (ParseStringUtil.isOffset(lexer.getToken())) {
      DurationExpr offset = parseOffset();
      re.setOffset(offset);
    }

    if (lexer.getToken().equals("@")) {
      if (re.getAt() != null) {
        log.error("duplicate `@` token");
        return null;
      }
      Expr at = parseAtExpr();
      re.setAt(at);
    }
    return re;
  }

  public DurationExpr parseOffset() throws PqlException {
    if (!ParseStringUtil.isOffset(lexer.getToken())) {
      return null;
    }
    lexer.Next();
    DurationExpr de = parseDuration();
    return de;
  }

  public DurationExpr parseDuration() throws PqlException {
    Boolean isNegative = lexer.getToken().equals("-");
    if (isNegative) {
      lexer.Next();
    }
    DurationExpr de = parsePositiveDuration();
    if (isNegative) {
      de.setS("-" + de.getS());
    }
    return de;
  }

  public Expr parseAtExpr() throws PqlException {
    if (!lexer.getToken().equals("@")) {
      return null;
    }
    lexer.Next();
    Expr e = parseSingleExprWithoutRollupSuffix();
    if (e == null) {
      return null;
    }
    return e;
  }

  public RollupExpr parseWindowAndStep() throws PqlException {
    if (!lexer.getToken().equals("[")) {
      return null;
    }
    lexer.Next();
    DurationExpr window = new DurationExpr();
    if (!lexer.getToken().startsWith(":")) {
      window = parsePositiveDuration();
    }
    DurationExpr step = new DurationExpr();
    Boolean inheritStep = false;
    if (lexer.getToken().startsWith(":")) {
      lexer.setToken(lexer.getToken().substring(1, lexer.getToken().length()));
      if (lexer.getToken().equals("")) {
        lexer.Next();
        if (lexer.getToken().equals("]")) {
          inheritStep = true;
        }
      }
      if (!lexer.getToken().equals("]")) {
        step = parsePositiveDuration();
      }
    }
    if (!lexer.getToken().equals("]")) {
      log.error("windowAndStep: unexpected token {}; want \"]\"", lexer.getToken());
      return null;
    }
    lexer.Next();
    return new RollupExpr(null, window, null, step, inheritStep, null);
  }

  public Expr parseSingleExprWithoutRollupSuffix() throws PqlException {
    if (ParseStringUtil.isPositiveDuration(lexer.getToken())) {
      return parsePositiveDuration();
    }
    if (ParseStringUtil.isStringPrefix(lexer.getToken())) {
      return parseStringExpr();
    }
    if (ParseStringUtil.isPositiveNumberPrefix(lexer.getToken())
        || ParseStringUtil.isInfOrNaN(lexer.getToken())) {
      return parsePositiveNumberExpr();
    }
    if (ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      return parseIdentExpr();
    }
    switch (lexer.getToken()) {
      case "(":
        return parseParensExpr();
      case "{":
        return parseMetricExpr();
      case "-":
        lexer.Next();
        Expr e = parseSingleExpr();
        BinaryOpExpr be = new BinaryOpExpr();
        be.setOp("-");
        be.setLeft(new NumberExpr(0d, ""));
        be.setRight(e);
        return be;
      case "+":
        lexer.Next();
        return parseSingleExpr();
      default:
        throw new PqlException("unexpected token: " + lexer.getToken());
    }
  }

  public Expr parseIdentExpr() throws PqlException {
    lexer.Next();
    if (ParseStringUtil.isEOF(lexer.getToken()) || ParseStringUtil.isOffset(lexer.getToken())) {
      lexer.Prev();
      return parseMetricExpr();
    }

    if (ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      lexer.Prev();
      if (ParseStringUtil.isAggrFunc(lexer.getToken())) {
        return parseAggrFuncExpr();
      }
      return parseMetricExpr();
    }

    if (ParseStringUtil.isBinaryOp(lexer.getToken())) {
      lexer.Prev();
      return parseMetricExpr();
    }

    switch (lexer.getToken()) {
      case "(":
        lexer.Prev();
        if (ParseStringUtil.isAggrFunc(lexer.getToken())) {
          return parseAggrFuncExpr();
        }
        return parseFuncExpr();
      case "{":
      case "[":
      case ")":
      case ",":
      case "@":
        lexer.Prev();
        return parseMetricExpr();
      default:
        return null;
    }
  }

  public AggrFuncExpr parseAggrFuncExpr() throws PqlException {
    if (!ParseStringUtil.isAggrFunc(lexer.getToken())) {
      return null;
    }

    AggrFuncExpr ae = new AggrFuncExpr();
    ae.setModifierExpr(new ModifierExpr());
    ae.setName(ParseStringUtil.unescapeIdent(lexer.getToken()));
    lexer.Next();
    if (ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      if (!ParseStringUtil.isAggrFuncModifier(lexer.getToken())) {
        return null;
      }
      parseModifierExpr(ae.getModifierExpr());
    }

    if (lexer.getToken().equals("(")) {
      List<Expr> args = parseArgListExpr();
      ae.setArgs(args);
      if (ae.getModifierExpr().getOp().equals("")
          && ParseStringUtil.isAggrFuncModifier(lexer.getToken())) {
        ModifierExpr me = parseModifierExpr(ae.getModifierExpr());
        if (me == null) {
          return null;
        }
      }

      if (lexer.getToken().toLowerCase().equals("limit")) {
        lexer.Next();
        int limit = Integer.valueOf(lexer.getToken());
        lexer.Next();
        ae.setLimit(limit);
      }

      return ae;
    }

    return null;
  }

  public ModifierExpr parseModifierExpr(ModifierExpr me) throws PqlException {
    if (!ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      return null;
    }

    me.setOp(lexer.getToken().toLowerCase());
    lexer.Next();
    if (ParseStringUtil.isBinaryOpJoinModifier(me.getOp()) && !lexer.getToken().equals("(")) {
      return null;
    }
    List<String> args = parseIdentList();
    me.setArgs(args);
    return me;
  }

  public FuncExpr parseFuncExpr() throws PqlException {
    if (!ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      return null;
    }
    FuncExpr fe = new FuncExpr();
    fe.setName(ParseStringUtil.unescapeIdent(lexer.getToken()));
    lexer.Next();
    List<Expr> args = parseArgListExpr();
    fe.setExprs(args);
    if (ParseStringUtil.isKeepMetricNames(lexer.getToken())) {
      fe.setKeepMetricNames(true);
      lexer.Next();
    }
    return fe;
  }

  public List<Expr> parseArgListExpr() throws PqlException {
    if (!lexer.getToken().equals("(")) {
      return null;
    }
    List<Expr> args = new ArrayList<>();
    while (true) {
      lexer.Next();
      if (lexer.getToken().equals(")")) {
        lexer.Next();
        return args;
      }
      Expr expr = parseExpr();
      args.add(expr);
      switch (lexer.getToken()) {
        case ",":
          continue;
        case ")":
          lexer.Next();
          return args;
        default:
          return null;
      }
    }
  }

  public ParensExpr parseParensExpr() throws PqlException {
    if (!lexer.getToken().equals("(")) {
      return null;
    }
    List<Expr> exprs = new ArrayList<>();
    while (true) {
      lexer.Next();
      if (lexer.getToken().equals(")")) {
        break;
      }
      Expr expr = parseExpr();
      exprs.add(expr);
      if (lexer.getToken().equals(",")) {
        continue;
      }
      if (lexer.getToken().equals(")")) {
        break;
      }
      return null;
    }
    lexer.Next();
    return new ParensExpr(exprs);
  }

  public MetricExpr parseMetricExpr() throws PqlException {
    MetricExpr me = new MetricExpr();
    if (ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      LabelFilterExpr lfe = new LabelFilterExpr();
      StringExpr se = new StringExpr();
      se.getTokens().add(ParseStringUtil.unescapeIdent(lexer.getToken()));
      lfe.setLabel("__name__");
      lfe.setValue(se);
      me.getLabelFilterExprs().add(lfe);
      lexer.Next();
      if (!lexer.getToken().equals("{")) {
        return me;
      }
    }
    List<LabelFilterExpr> lfes = parseLabelFilters();
    me.getLabelFilterExprs().addAll(lfes);
    return me;
  }

  public List<LabelFilterExpr> parseLabelFilters() throws PqlException {
    if (!lexer.getToken().equals("{")) {
      return null;
    }
    List<LabelFilterExpr> lfes = new ArrayList<>();
    while (true) {
      lexer.Next();
      if (lexer.getToken().equals("}")) {
        lexer.Next();
        return lfes;
      }
      LabelFilterExpr lfe = parseLabelFilterExpr();
      lfes.add(lfe);
      switch (lexer.getToken()) {
        case ",":
          continue;
        case "}":
          lexer.Next();
          return lfes;
        default:
          return null;
      }
    }
  }

  public LabelFilterExpr parseLabelFilterExpr() throws PqlException {
    if (!ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      throw new PqlException("unexpected token: " + lexer.getToken());
    }
    LabelFilterExpr lfe = new LabelFilterExpr();
    lfe.setLabel(ParseStringUtil.unescapeIdent(lexer.getToken()));
    lexer.Next();
    switch (lexer.getToken()) {
      case "=":
        // do nothing
        break;
      case "!=":
        lfe.setIsNegative(true);
        break;
      case "=~":
        lfe.setIsRegexp(true);
        break;
      case "!~":
        lfe.setIsNegative(true);
        lfe.setIsRegexp(true);
        break;
      case ",":
      case "}":
        return lfe;
      default:
        break;
    }
    lexer.Next();
    StringExpr se = parseStringExpr();
    lfe.setValue(se);
    return lfe;
  }

  public StringExpr parseStringExpr() throws PqlException {
    StringExpr se = new StringExpr();
    while (true) {
      if (ParseStringUtil.isStringPrefix(lexer.getToken())
          || ParseStringUtil.isIdentPrefix(lexer.getToken())) {
        se.getTokens().add(lexer.getToken());
      }
      lexer.Next();
      if (!lexer.getToken().equals("+")) {
        return se;
      }

      lexer.Next();
      if (ParseStringUtil.isStringPrefix(lexer.getToken())) {
        continue;
      }

      if (ParseStringUtil.isIdentPrefix(lexer.getToken())) {
        lexer.Prev();
        return se;
      }

      lexer.Next();
      if (lexer.getToken().equals("(") || lexer.getToken().equals("{")) {
        lexer.Prev();
        lexer.Prev();
        return se;
      }

      lexer.Prev();
    }
  }

  public DurationExpr parsePositiveDuration() throws PqlException {
    String s = lexer.getToken();
    if (ParseStringUtil.isPositiveDuration(s)) {
      lexer.Next();
    } else {
      if (!ParseStringUtil.isPositiveNumberPrefix(s)) {
        return null;
      }
      Expr e = parsePositiveNumberExpr();
      if (e == null) {
        log.error("duration: unexpected token {}", s);
        return null;
      }
    }
    return new DurationExpr(s);
  }

  public NumberExpr parsePositiveNumberExpr() throws PqlException {
    if (!ParseStringUtil.isPositiveNumberPrefix(lexer.getToken())
        && !ParseStringUtil.isInfOrNaN((lexer.getToken()))) {
      return null;
    }
    String s = lexer.getToken();
    Double n = parsePositiveNumber(s);
    lexer.Next();
    return new NumberExpr(n, s);
  }

  public Double parsePositiveNumber(String s) {
    if (ParseStringUtil.isSpecialIntegerPrefix(s)) {
      return Double.valueOf(s);
    }
    s = s.toLowerCase();
    Double m = 1d;
    if (s.endsWith("kib")) {
      s = s.substring(0, s.length() - 3);
      m = 1024d;
    } else if (s.endsWith("ki")) {
      s = s.substring(0, s.length() - 2);
      m = 1024d;
    } else if (s.endsWith("kb")) {
      s = s.substring(0, s.length() - 2);
      m = 1000d;
    } else if (s.endsWith("k")) {
      s = s.substring(0, s.length() - 1);
      m = 1000d;
    } else if (s.endsWith("mib")) {
      s = s.substring(0, s.length() - 3);
      m = 1024d * 1024;
    } else if (s.endsWith("mi")) {
      s = s.substring(0, s.length() - 2);
      m = 1024d * 1024;
    } else if (s.endsWith("mb")) {
      s = s.substring(0, s.length() - 2);
      m = 1000d * 1000;
    } else if (s.endsWith("m")) {
      s = s.substring(0, s.length() - 1);
      m = 1000d * 1000;
    } else if (s.endsWith("gib")) {
      s = s.substring(0, s.length() - 3);
      m = 1024d * 1024 * 1024;
    } else if (s.endsWith("gi")) {
      s = s.substring(0, s.length() - 2);
      m = 1024d * 1024 * 1024;
    } else if (s.endsWith("gb")) {
      s = s.substring(0, s.length() - 2);
      m = 1000d * 1000 * 1000;
    } else if (s.endsWith("g")) {
      s = s.substring(0, s.length() - 1);
      m = 1000d * 1000 * 1000;
    } else if (s.endsWith("tib")) {
      s = s.substring(0, s.length() - 3);
      m = 1024d * 1024 * 1024 * 1024;
    } else if (s.endsWith("ti")) {
      s = s.substring(0, s.length() - 2);
      m = 1024d * 1024 * 1024 * 1024;
    } else if (s.endsWith("tb")) {
      s = s.substring(0, s.length() - 2);
      m = 1000d * 1000 * 1000 * 1000;
    } else if (s.endsWith("t")) {
      s = s.substring(0, s.length() - 1);
      m = 1000d * 1000 * 1000 * 1000;
    }
    Double v = Double.valueOf(s);
    return m * v;
  }

  public WithExpr parseWithExpr() throws PqlException {
    WithExpr we = new WithExpr();
    if (!isWith(lexer.getToken())) {
      return null;
    }
    lexer.Next();
    if (lexer.getToken() != "(") {
      return null;
    }

    while (true) {
      lexer.Next();
      if (lexer.getToken().equals(")")) {
        checkDuplicateWithArgNames(we.getWithArgExprs());
        lexer.Next();
        Expr e = parseExpr();
        we.setExpr(e);
        return we;
      }
      WithArgExpr wa = parseWithArgExpr();
      List<WithArgExpr> withArgExprs = we.getWithArgExprs();
      withArgExprs.add(wa);
      we.setWithArgExprs(withArgExprs);
      switch (lexer.getToken()) {
        case ",":
          continue;
        case ")":
          checkDuplicateWithArgNames(we.getWithArgExprs());
          lexer.Next();
          Expr e = parseExpr();
          we.setExpr(e);
          return we;
        default:
          return null;
      }
    }

  }

  public void checkDuplicateWithArgNames(List<WithArgExpr> withArgExprs) {
    Map<String, WithArgExpr> map = new HashMap<>();
    for (WithArgExpr withArgExpr : withArgExprs) {
      if (map.get(withArgExpr.getName()) != null) {
        throw new IllegalArgumentException("bug");
      }
      map.put(withArgExpr.getName(), withArgExpr);
    }
  }

  public WithArgExpr parseWithArgExpr() throws PqlException {
    WithArgExpr wa = new WithArgExpr();
    if (!ParseStringUtil.isIdentPrefix(lexer.getToken())) {
      return null;
    }
    wa.setName(ParseStringUtil.unescapeIdent(lexer.getToken()));
    lexer.Next();
    if (lexer.getToken().equals("(")) {
      List<String> args = parseIdentList();
      wa.setArgs(args);
    }
    if (lexer.getToken() != "=") {
      return null;
    }
    lexer.Next();
    Expr e = parseExpr();
    wa.setExpr(e);
    return wa;
  }

  public List<String> parseIdentList() throws PqlException {
    List<String> idents = new ArrayList<>();
    if (!lexer.getToken().equals("(")) {
      return idents;
    }
    while (true) {
      lexer.Next();
      if (lexer.getToken().equals(")")) {
        lexer.Next();
        return idents;
      }
      if (!ParseStringUtil.isIdentPrefix(lexer.getToken())) {
        return idents;
      }
      idents.add(ParseStringUtil.unescapeIdent(lexer.getToken()));
      lexer.Next();
      switch (lexer.getToken()) {
        case ",":
          continue;
        case ")":
          lexer.Next();
          return idents;
        default:
          return idents;
      }
    }
  }

  public Boolean isRollupStartToken(String token) {
    return token.equals("[") || token.equals("@") || ParseStringUtil.isOffset(token);
  }
}
