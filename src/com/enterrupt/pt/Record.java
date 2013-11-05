package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.TreeMap;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.DefnCache;

public class Record {

    public String RECNAME;
	public String RELLANGRECNAME;
	public int RECTYPE;

	public HashMap<String, RecordField> fieldTable;
	public TreeMap<Integer, Object> fldAndSubrecordTable;
	public ArrayList<String> subRecordNames;
	public HashMap<String, ArrayList<PeopleCodeProg>> recordProgsByFieldTable;
	public ArrayList<PeopleCodeProg> orderedRecordProgs;

	private boolean hasRecordPCBeenDiscovered = false;
	private boolean hasBeenInitialized = false;

    public Record(String recname) {
        this.RECNAME = recname;
    }

    public void init() throws Exception {

		if(this.hasBeenInitialized) { return; }
		this.hasBeenInitialized = true;

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSRECDEFN(this.RECNAME);
        rs = pstmt.executeQuery();

		int fieldcount = 0;
		if(rs.next()) {
			fieldcount = rs.getInt("FIELDCOUNT");
			this.RELLANGRECNAME = rs.getString("RELLANGRECNAME").trim();
			this.RECTYPE = rs.getInt("RECTYPE");
		} else {
			System.out.println("[ERROR] Expected record to be returned from PSRECDEFN query.");
			System.exit(1);
		}
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
        rs = pstmt.executeQuery();

		this.fieldTable = new HashMap<String, RecordField>();
		this.fldAndSubrecordTable = new TreeMap<Integer, Object>();

		int i = 0;
        while(rs.next()) {
			RecordField f = new RecordField();
			f.RECNAME = this.RECNAME;
			f.FIELDNAME = rs.getString("FIELDNAME").trim();
			f.USEEDIT = (byte) rs.getInt("USEEDIT");
			f.FIELDNUM = rs.getInt("FIELDNUM");
			this.fieldTable.put(f.FIELDNAME, f);
			this.fldAndSubrecordTable.put(f.FIELDNUM, f);
            i++;
        }
        rs.close();
        pstmt.close();

		this.subRecordNames = new ArrayList<String>();

		/**
		 * If the number of fields retrieved differs from the FIELDCOUNT for the record
 		 * definition, we need to query for subrecords.
		 */
		if(fieldcount != i) {
			pstmt = StmtLibrary.getSubrecordsUsingPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				this.fldAndSubrecordTable.put(rs.getInt("FIELDNUM"), rs.getString("FIELDNAME"));
				subRecordNames.add(rs.getString("FIELDNAME"));
				i++;
			}

			if(fieldcount != i) {
				System.out.println("[ERROR] Even after querying for subrecords, field count does not match that on PSRECDEFN.");
				System.exit(1);
			}
		}

        pstmt = StmtLibrary.getPSDBFLDLBL(this.RECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();

		for(String subrecname : subRecordNames) {
			//System.out.println("Loading subrecord : " + subrecname);
			DefnCache.getRecord(subrecname);
		}

		/**
		 * If RELLANGRECNAME is not blank, load it here.
		 * If this part causes issues, consider:
		 *   1) moving this above the subrecord initialization.
		 * TODO: the record attached as RELLANGRECNAME may not have a discernible use or applicability
	     * to Enterrupt; need to look into this.
		 */
		if(this.RELLANGRECNAME.length() > 0) {
			DefnCache.getRecord(this.RELLANGRECNAME);
		}
    }

	public void discoverRecordPC() throws Exception {

		if(this.hasRecordPCBeenDiscovered) { return; }
		this.hasRecordPCBeenDiscovered = true;

		PreparedStatement pstmt;
        ResultSet rs;

		this.recordProgsByFieldTable = new HashMap<String, ArrayList<PeopleCodeProg>>();
		this.orderedRecordProgs = new ArrayList<PeopleCodeProg>();

        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.RECORD, this.RECNAME);
        rs = pstmt.executeQuery();

		while(rs.next()) {

			PeopleCodeProg prog = new RecordPeopleCodeProg(rs.getString("OBJECTVALUE1"),
				rs.getString("OBJECTVALUE2"), rs.getString("OBJECTVALUE3"));
			prog = DefnCache.getProgram(prog);

			ArrayList<PeopleCodeProg> fieldProgList = this.recordProgsByFieldTable
				.get(rs.getString("OBJECTVALUE2"));
			if(fieldProgList == null) {
				fieldProgList = new ArrayList<PeopleCodeProg>();
			}

			fieldProgList.add(prog);
			this.recordProgsByFieldTable.put(rs.getString("OBJECTVALUE2"), fieldProgList);
			this.orderedRecordProgs.add(prog);
		}

        rs.close();
        pstmt.close();
	}

	public boolean isSubrecord() {
		return this.RECTYPE == 3;
	}

	public boolean isDerivedWorkRecord() {
		return this.RECTYPE == 2;
	}

	public ArrayList<RecordField> getExpandedFieldList() throws Exception {

		ArrayList<RecordField> expandedFieldList = new ArrayList<RecordField>();

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
}

