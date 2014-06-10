/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines PeopleTools/PeopleSoft constants and utility methods
 * to process them.
 */
public final class PSDefn {

  public static final String RECORD = "1";
  public static final String FIELD = "2";
  public static final String COMPONENT = "10";
  public static final String EVENT = "12";
  public static final String MARKET = "39";
  public static final String APP_PACKAGE = "104";
  public static final String PAGE = "9";
  public static final String NULL = " ";
  public static final Map<String, String> DEFN_LITERAL_RESERVED_WORDS_TABLE;
  public static final Map<String, Void> VAR_TYPES_TABLE;

  /*
   * These are the allowed bit flags for the 4-bit Actions bit mask,
   * which defines which modes a component can be run in. In App Designer,
   * open a component, go to File > Definition Properties, then click the
   * Use tab; these flags map to the four Actions checkboxes.
   */
  public static final byte ACTIONS_ADD = 0x1;
  public static final byte ACTIONS_UPDATE_DISPLAY = 0x2;
  public static final byte ACTIONS_UPDATE_DISPLAY_ALL = 0x4;
  public static final byte ACTIONS_CORRECTION = 0x8;

  /*
   * These are the allowed values for the primary action to be
   * taken when opening a component. In App Designer, open a component,
   * go to File > Definition Properties, then click the Internet tab;
   * these values map to the Primary Action radio buttons.
   */
  public static final int PRIMARYACTION_NEW = 0;
  public static final int PRIMARYACTION_SEARCH = 1;
  public static final int PRIMARYACTION_KEYWORD_SEARCH = 2;

  /*
   * These are the allowed values for the default search action to be taken
   * when opening a component in search mode. In App Designer, open a component,
   * go to File > Definition Properties, then click the Internet tab; these
   * values map to the Default Search Action dropdown values, which appear
   * based on the selected Actions checkboxes on the Use tab.
   */
  public static final int DFLTACTION_UPDATE_DISPLAY = 1;
  public static final int DFLTACTION_UPDATE_DISPLAY_ALL = 2;
  public static final int DFLTACTION_CORRECTION = 3;

  private static final Map<String, Boolean> SYSTEM_RECORDS;

  private static final String[] DEFN_LITERAL_RESERVED_WORDS = new String[]{
    "BarName",
    "BusActivity",
    "BusEvent",
    "BusProcess",
    "CompIntfc",
    "Component",
    "Field",
    "FileLayout",
    "HTML",
    "Image",
    "Interlink",
    "ItemName",
    "MenuName",
    "Message",
    "Operation",
    "Page",
    "Panel",
    "PanelGroup",
    "RecName",
    "Record",
    "Scroll",
    "SQL",
    "StyleSheet"
  };

  private static final String[] varTypes = new String[] {
    "any",
    "array",
    "boolean",
    "date",
    "datetime",
    "integer",
    "number",
    "string",
    "time",
    "ApiObject",
    "Field",
    "Grid",
    "GridColumn",
    "Record",
    "Row",
    "Rowset",
    "SQL"
  };

  static {
    SYSTEM_RECORDS = new HashMap<String, Boolean>();
    SYSTEM_RECORDS.put("PSXLATITEM", true);

    // Index defn literal reserved words for O(1) lookup.
    DEFN_LITERAL_RESERVED_WORDS_TABLE = new HashMap<String, String>();
    for (String s : DEFN_LITERAL_RESERVED_WORDS) {
      DEFN_LITERAL_RESERVED_WORDS_TABLE.put(s.toUpperCase(), s);
    }

    // Index var types for O(1) lookup.
    VAR_TYPES_TABLE = new HashMap<String, Void>();
    for (String s : varTypes) {
      VAR_TYPES_TABLE.put(s, null);
    }
  }

  private PSDefn() {}

  /**
   * Determines whether or not the record defn with the provided name
   * is a system record.
   * @param recName the name of the record to check
   * @return true if record is a system record, false otherwise
   */
  public static boolean isSystemRecord(final String recName) {
    return SYSTEM_RECORDS.get(recName) != null;
  }

  /**
   * Contains all recognized values of the FIELDTYPE field on PSPNLFIELD.
   */
  public final class PageFieldType {
    // represents text on page linked to msg set/nbr; this text
    // text is not in component or page buffer.
    public static final int STATIC_TEXT = 0;
    public static final int FRAME = 1;
    public static final int GROUPBOX = 2;
    public static final int STATIC_IMAGE = 3;
    public static final int SCROLL_BAR = 10;
    public static final int SUBPAGE = 11;
    public static final int PUSHBTN_LINK_PEOPLECODE = 12;
    public static final int PUSHBTN_LINK_SCROLL_ACTION = 13;
    public static final int PUSHBTN_LINK_TOOLBAR_ACTION = 14;
    public static final int PUSHBTN_LINK_EXTERNAL = 15;
    public static final int PUSHBTN_LINK_INTERNAL = 16;
    public static final int PUSHBTN_LINK_PROCESS = 17;
    public static final int GRID = 19;
    public static final int PUSHBTN_LINK_SECPAGE = 21;
    public static final int SECPAGE = 18;
    public static final int HORIZONTAL_RULE = 23;
    public static final int PUSHBTN_LINK_PROMPT_ACTION = 26;
    public static final int SCROLL_AREA = 27;
    public static final int PUSHBTN_LINK_PAGE_ANCHOR = 29;
    public static final int PUSHBTN_LINK_INST_MSG_ACTION = 31;

    private PageFieldType() {}
  }
}

