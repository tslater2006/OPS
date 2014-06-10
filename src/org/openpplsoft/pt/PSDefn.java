/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.*;

public class PSDefn {

  public static final String RECORD = "1";
  public static final String FIELD = "2";
  public static final String COMPONENT = "10";
  public static final String EVENT = "12";
  public static final String MARKET = "39";
  public static final String APP_PACKAGE = "104";
  public static final String PAGE = "9";
  public static final String NULL = " ";
  public static Map<String, String> defnLiteralReservedWordsTable;
  public static Map<String, Void> varTypesTable;

  /*
   * These are the allowed bit flags for the 4-bit Actions bit mask,
   * which defines which modes a component can be run in. In App Designer,
   * open a component, go to File > Definition Properties, then click the
   * Use tab; these flags map to the four Actions checkboxes.
   */
  public static final byte ACTIONS_Add = 0x1;
  public static final byte ACTIONS_UpdateDisplay = 0x2;
  public static final byte ACTIONS_UpdateDisplayAll = 0x4;
  public static final byte ACTIONS_Correction = 0x8;

  /*
   * These are the allowed values for the primary action to be
   * taken when opening a component. In App Designer, open a component,
   * go to File > Definition Properties, then click the Internet tab;
   * these values map to the Primary Action radio buttons.
   */
  public static final int PRIMARYACTION_New = 0;
  public static final int PRIMARYACTION_Search = 1;
  public static final int PRIMARYACTION_KeywordSearch = 2;

  /*
   * These are the allowed values for the default search action to be taken
   * when opening a component in search mode. In App Designer, open a component,
   * go to File > Definition Properties, then click the Internet tab; these
   * values map to the Default Search Action dropdown values, which appear
   * based on the selected Actions checkboxes on the Use tab.
   */
  public static final int DFLTACTION_UpdateDisplay = 1;
  public static final int DFLTACTION_UpdateDisplayAll = 2;
  public static final int DFLTACTION_Correction = 3;

  private static HashMap<String, Boolean> systemRecords;

  private static final String[] defnLiteralReservedWords = new String[]{
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
    systemRecords = new HashMap<String, Boolean>();
    systemRecords.put("PSXLATITEM", true);

    // Index defn literal reserved words for O(1) lookup.
    defnLiteralReservedWordsTable = new HashMap<String, String>();
    for(String s : defnLiteralReservedWords) {
      defnLiteralReservedWordsTable.put(s.toUpperCase(), s);
    }

    // Index var types for O(1) lookup.
    varTypesTable = new HashMap<String, Void>();
    for(String s : varTypes) {
      varTypesTable.put(s, null);
    }
  }

  public static boolean isSystemRecord(String RECNAME) {
    return systemRecords.get(RECNAME) != null;
  }
}

