/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.HashMap;
import java.util.Map;

public enum PTDefnLiteralKeyword {

  RECORD("record"),
  MENUNAME("menuname"),
  BARNAME("barname"),
  ITEMNAME("itemname"),
  PAGE("page"),
  COMPONENT("component"),
  FIELD("field"),
  SQL("sql"),
  PANELGROUP("panelgroup", "component"),
  HTML("html"),
  SCROLL("scroll");

  private final String lcKeyword, aliasOfKeyword;

  private PTDefnLiteralKeyword(final String lcKeyword) {
    this.lcKeyword = lcKeyword;
    this.aliasOfKeyword = null;
  }

  private PTDefnLiteralKeyword(final String lcKeyword,
      final String aliasOfKeyword) {
    this.lcKeyword = lcKeyword;
    this.aliasOfKeyword = aliasOfKeyword;
  }

  public String getLowerCaseKeyword() {
    return this.lcKeyword;
  }

  public static boolean isKeyword(final String prefix) {
    for (PTDefnLiteralKeyword k : PTDefnLiteralKeyword.values()) {
      if (k.getLowerCaseKeyword().equals(prefix.toLowerCase())) {
           return true;
      }
    }
    return false;
  }

  public static PTDefnLiteralKeyword getKeyword(final String prefix) {
    for (PTDefnLiteralKeyword k : PTDefnLiteralKeyword.values()) {
      if (k.getLowerCaseKeyword().equals(prefix.toLowerCase())) {
           return k;
      }
    }
    return null;
  }

  public PTType allocLiteralObj(final String literalSuffix) {

    String fullLiteral;
    if (this.aliasOfKeyword == null) {
      fullLiteral = this.lcKeyword;
    } else {
      fullLiteral = this.aliasOfKeyword;
    }
    fullLiteral += "." + literalSuffix;

    if (this == PTDefnLiteralKeyword.RECORD) {
      return new PTRecordLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.MENUNAME) {
      return new PTMenuLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.COMPONENT
                || this == PTDefnLiteralKeyword.PANELGROUP) {
      return new PTComponentLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.SQL) {
      return new PTSQLLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.PAGE) {
      return new PTPageLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.FIELD) {
      return new PTFieldLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.BARNAME) {
      return new PTMenuBarLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.ITEMNAME) {
      return new PTMenuItemLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.HTML) {
      return new PTHTMLLiteral(fullLiteral);
    } else if (this == PTDefnLiteralKeyword.SCROLL) {
      return new PTScrollLiteral(fullLiteral);
    } else {
      throw new OPSDataTypeException("TODO: This enumerated value "
          + "needs a mapping to a literal object class.");
    }
  }

  public static PTDefnLiteralPrefix allocLiteralPrefixObj(final String prefix) {

    if (!isKeyword(prefix)) {
      throw new OPSDataTypeException("Prefix provided (" + prefix + ") does not "
          + "map to a prefix keyword enumerated value.");
    }

    final PTDefnLiteralKeyword k = getKeyword(prefix.toLowerCase());
    return new PTDefnLiteralPrefix(k);
  }

  /**
   * Accepts literal defn strings (i.e., "MenuName.SA_LEARNER_SERVICES").
   */
  public static PTType allocLiteralObjFromDefnString(final String literal) {

    if (literal.indexOf(".") == -1) {
      throw new OPSDataTypeException("Illegal argument to fromLiteral; arg "
          + "must be a keyword pefix followed by '.' and the defn name.");
    }

    final String prefix =
        literal.substring(0, literal.indexOf(".")).toLowerCase();
    if (!isKeyword(prefix)) {
      throw new OPSDataTypeException("Prefix provided (" + prefix + ") does not "
          + "map to a prefix keyword enumerated value.");
    }

    final String suffix =
        literal.substring(literal.indexOf(".") + 1);
    return getKeyword(prefix).allocLiteralObj(suffix);
  }
}

