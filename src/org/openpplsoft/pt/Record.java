/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.*;
import java.util.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.pt.peoplecode.*;
import org.apache.logging.log4j.*;

public class Record {

  public String RECNAME;
  public String RELLANGRECNAME;
  public int RECTYPE;

  public Map<String, RecordField> fieldTable;
  public TreeMap<Integer, Object> fldAndSubrecordTable;
  private List<String> subRecordNames;
  public Map<String, List<PeopleCodeProg>> recordProgsByFieldTable
      = new HashMap<String, List<PeopleCodeProg>>();

  private static Logger log = LogManager.getLogger(Record.class.getName());

  private boolean hasRecordPCBeenDiscovered = false;
  private boolean hasBeenInitialized = false;

  public Record(String recname) {
    this.RECNAME = recname;
  }

  public void init() {

    if(this.hasBeenInitialized) { return; }
    this.hasBeenInitialized = true;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSRECDEFN",
        new String[]{this.RECNAME});
    OPSResultSet rs = ostmt.executeQuery();

    int fieldcount = 0;
    if(rs.next()) {
      fieldcount = rs.getInt("FIELDCOUNT");
      this.RELLANGRECNAME = rs.getString("RELLANGRECNAME").trim();
      this.RECTYPE = rs.getInt("RECTYPE");
    } else {
      throw new OPSVMachRuntimeException("Expected record to be returned from PSRECDEFN query: " + this.RECNAME);
    }
    rs.close();
    ostmt.close();

    ostmt = StmtLibrary.getStaticSQLStmt("query.PSDBFIELD_PSRECFIELD_JOIN",
        new String[]{this.RECNAME});
    rs = ostmt.executeQuery();

    this.fieldTable = new HashMap<String, RecordField>();
    this.fldAndSubrecordTable = new TreeMap<Integer, Object>();

    int i = 0;
    while(rs.next()) {
      RecordField f = new RecordField(this.RECNAME,
          rs.getString("FIELDNAME").trim(), rs.getInt("FIELDTYPE"));
      f.USEEDIT = rs.getInt("USEEDIT");
      f.FIELDNUM = rs.getInt("FIELDNUM");
      f.LENGTH = rs.getInt("LENGTH");
      f.DEFRECNAME = rs.getString("DEFRECNAME");
      f.DEFFIELDNAME = rs.getString("DEFFIELDNAME");
      f.LABEL_ID = rs.getString("LABEL_ID");
      this.fieldTable.put(f.FIELDNAME, f);
      this.fldAndSubrecordTable.put(f.FIELDNUM, f);
      i++;
    }
    rs.close();
    ostmt.close();

    this.subRecordNames = new ArrayList<String>();

    /*
     * If the number of fields retrieved differs from the FIELDCOUNT for the record
     * definition, we need to query for subrecords.
     */
    if(fieldcount != i) {
      ostmt = StmtLibrary.getStaticSQLStmt(
          "query.PSDBFIELD_PSRECFIELD_JOIN_ForSubrecords", new String[]{this.RECNAME});
      rs = ostmt.executeQuery();

      while(rs.next()) {
        this.fldAndSubrecordTable.put(rs.getInt("FIELDNUM"), rs.getString("FIELDNAME"));
        subRecordNames.add(rs.getString("FIELDNAME"));
        i++;
      }
      rs.close();
      ostmt.close();

      if(fieldcount != i) {
        throw new OPSVMachRuntimeException("Even after querying for subrecords, field count " +
            "does not match that on PSRECDEFN.");
      }
    }

    ostmt = StmtLibrary.getStaticSQLStmt("query.PSDBFLDLBL",
       new String[]{this.RECNAME});
    rs = ostmt.executeQuery();
    while (rs.next()) {
      final FieldLabel label = new FieldLabel(rs.getString("LABEL_ID"),
          rs.getString("LONGNAME"), rs.getString("SHORTNAME"));
      if (rs.getInt("DEFAULT_LABEL") == 1) {
        label.setAsDefault();
      }
      this.fieldTable.get(rs.getString("FIELDNAME")).addLabel(label);
    }
    rs.close();
    ostmt.close();
  }

  public void discoverRecordPC() {

    if(this.hasRecordPCBeenDiscovered) { return; }
    this.hasRecordPCBeenDiscovered = true;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSPCMPROG_RecordPCList",
        new String[]{PSDefn.RECORD, this.RECNAME});
    OPSResultSet rs = ostmt.executeQuery();

    while (rs.next()) {
      PeopleCodeProg prog = new RecordPeopleCodeProg(rs.getString("OBJECTVALUE1"),
          rs.getString("OBJECTVALUE2"), rs.getString("OBJECTVALUE3"));
      prog = DefnCache.getProgram(prog);

      List<PeopleCodeProg> fieldProgList = this.recordProgsByFieldTable
          .get(rs.getString("OBJECTVALUE2"));
      if(fieldProgList == null) {
        fieldProgList = new ArrayList<PeopleCodeProg>();
      }

      fieldProgList.add(prog);
      this.recordProgsByFieldTable.put(rs.getString("OBJECTVALUE2"), fieldProgList);
    }

    rs.close();
    ostmt.close();
  }

  public List<String> getSubrecordNames() {
    return this.subRecordNames;
  }

  public boolean isTable() {
    return this.RECTYPE == 0;
  }

  public boolean isView() {
    return this.RECTYPE == 1;
  }

  public boolean isSubrecord() {
    return this.RECTYPE == 3;
  }

  public boolean isDerivedWorkRecord() {
    return this.RECTYPE == 2;
  }

  /*
   * TODO: This method is tentative. It may need to use the expanded field list
   * rather than the root field list. It also may be too expansive.
   */
  public boolean hasAnyKeys() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isKey() || rf.isSearchKey() || rf.isAlternateSearchKey()) {
        return true;
      }
    }
    return false;
  }

  /*
   * TODO: This method is tentative. It may need to use the expanded field list
   * rather than the root field list. It also may be too expansive.
   */
  public boolean hasAnySearchKeys() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isSearchKey()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if a required key field exists on this record.
   * @return true if a required key field exists, false otherwise
   */
  public boolean hasARequiredKeyField() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isKey() && rf.isRequired()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if a non-required key field exists on this record.
   * @return true if a non-required key field exists, false otherwise
   */
  public boolean hasANonRequiredKeyField() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isKey() && !rf.isRequired()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if the record has no keys.
   * @return true if no keys on record, false if at least one exists
   */
  public boolean hasNoKeys() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isKey()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if the record has at least one non-key field.
   * @return true if at least one non-key field exists, false otherwise
   */
  public boolean hasANonKeyField() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(!rf.isKey() && !rf.isSearchKey() && !rf.isAlternateSearchKey()) {
        return true;
      }
    }
    return false;
  }

  public List<RecordField> getExpandedFieldList() {
    List<RecordField> expandedFieldList = new ArrayList<RecordField>();
    for(Map.Entry<Integer, Object> cursor : this.fldAndSubrecordTable.entrySet()) {
      Object obj = cursor.getValue();
      if(obj instanceof RecordField) {
        expandedFieldList.add((RecordField) obj);
      } else {
        Record subrecDefn = DefnCache.getRecord((String) obj);
        expandedFieldList.addAll(subrecDefn.getExpandedFieldList());
      }
    }
    return expandedFieldList;
  }

  public List<PeopleCodeProg> getRecordProgsForField(String FLDNAME) {
    return this.recordProgsByFieldTable.get(FLDNAME);
  }

  public String getDbTableName() {
    if (this.RECNAME.startsWith("PS")) {
      return this.RECNAME;
    }
    return "PS_" + this.RECNAME;
  }

  public boolean hasField(final String fldName) {
    return this.fieldTable.containsKey(fldName);
  }

  @Override
  public String toString() {
    return this.RECNAME;
  }
}

