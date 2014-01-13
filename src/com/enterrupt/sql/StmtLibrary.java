package com.enterrupt.sql;

import java.sql.*;
import java.util.*;
import com.enterrupt.pt.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;
import java.util.regex.*;

public class StmtLibrary {

	private static Connection conn;
	private static Logger log = LogManager.getLogger(StmtLibrary.class.getName());

	private static Pattern bindIdxPattern;
    private static Pattern dateInPattern;

	static {
		try {
			String dbSID = System.getProperty("DbSID");
			String dbIP = System.getProperty("DbIP");
			String dbDriver = System.getProperty("DbDriver");
			conn = DriverManager.getConnection(
				dbDriver + ":@//" + dbIP +":1521/" + dbSID, "SYSADM", "SYSADM");

		} catch(java.sql.SQLException sqle) {
			log.fatal(sqle.getMessage(), sqle);
			System.exit(ExitCode.UNABLE_TO_ACQUIRE_DB_CONN.getCode());
		}

		// compile meta-SQL detection regex patterns.
        bindIdxPattern = Pattern.compile(":\\d+");
        dateInPattern = Pattern.compile("%DATEIN\\((.+?)\\)");
	}

	public static PreparedStatement getPSPNLGRPDEFN(String b1, String b2) {
		ENTStmt stmt = new ENTStmt("SELECT DESCR, ACTIONS, VERSION, SEARCHRECNAME, ADDSRCHRECNAME,  SEARCHPNLNAME, LOADLOC, SAVELOC, DISABLESAVE, PRIMARYACTION, DFLTACTION, DFLTSRCHTYPE,  DEFERPROC, EXPENTRYPROC, WSRPCOMPLIANT, REQSECURESSL, INCLNAVIGATION, FORCESEARCH, ALLOWACTMODESEL,  PNLNAVFLAGS, TBARBTNS, SHOWTBAR, ADDLINKMSGSET, ADDLINKMSGNUM, SRCHLINKMSGSET, SRCHLINKMSGNUM,  SRCHTEXTMSGSET, SRCHTEXTMSGNUM, OBJECTOWNERID, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID,  DESCRLONG  FROM PSPNLGRPDEFN WHERE PNLGRPNAME = ? AND MARKET = ?");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPNLGROUP(String b1, String b2) {
		ENTStmt stmt = new ENTStmt("SELECT PNLNAME, ITEMNAME, HIDDEN, ITEMLABEL, FOLDERTABLABEL, SUBITEMNUM FROM PSPNLGROUP WHERE PNLGRPNAME = ? AND MARKET = ? ORDER BY SUBITEMNUM");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSMENUDEFN(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT MENULABEL, MENUGROUP, GROUPORDER, MENUORDER, VERSION, INSTALLED, GROUPSEP, MENUSEP, MENUTYPE, OBJECTOWNERID, LASTUPDOPRID, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), DESCR, DESCRLONG FROM PSMENUDEFN WHERE MENUNAME = ?");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSMENUITEM(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT BARNAME, ITEMNAME, BARLABEL, ITEMLABEL, MARKET, ITEMTYPE, PNLGRPNAME, SEARCHRECNAME, ITEMNUM, XFERCOUNT FROM PSMENUITEM WHERE MENUNAME = ? ORDER BY ITEMNUM");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSRECDEFN(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT VERSION, FIELDCOUNT, RECTYPE, RECUSE, OPTTRIGFLAG, AUDITRECNAME, SETCNTRLFLD, RELLANGRECNAME, OPTDELRECNAME, PARENTRECNAME, QRYSECRECNAME, SQLTABLENAME, BUILDSEQNO, OBJECTOWNERID, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, SYSTEMIDFIELDNAME, TIMESTAMPFIELDNAME, RECDESCR, AUXFLAGMASK, DESCRLONG  FROM PSRECDEFN WHERE RECNAME = ?");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSDBFIELD_PSRECFIELD_JOIN(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT VERSION, A.FIELDNAME, FIELDTYPE, LENGTH, DECIMALPOS, FORMAT, FORMATLENGTH, IMAGE_FMT, FORMATFAMILY, DISPFMTNAME, DEFCNTRYYR,IMEMODE,KBLAYOUT,OBJECTOWNERID, DEFRECNAME, DEFFIELDNAME, CURCTLFIELDNAME, USEEDIT, USEEDIT2, EDITTABLE, DEFGUICONTROL, SETCNTRLFLD, LABEL_ID, TIMEZONEUSE, TIMEZONEFIELDNAME, CURRCTLUSE, RELTMDTFIELDNAME, TO_CHAR(CAST((B.LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), B.LASTUPDOPRID, B.FIELDNUM, A.FLDNOTUSED, A.AUXFLAGMASK, B.RECNAME FROM PSDBFIELD A, PSRECFIELD B WHERE B.RECNAME = ? AND A.FIELDNAME = B.FIELDNAME AND B.SUBRECORD = 'N' ORDER BY B.RECNAME, B.FIELDNUM");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSDBFLDLBL(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT FIELDNAME, LABEL_ID, LONGNAME, SHORTNAME, DEFAULT_LABEL FROM PSDBFLDLABL WHERE FIELDNAME IN (SELECT A.FIELDNAME FROM PSDBFIELD A, PSRECFIELD B WHERE B.RECNAME = ? AND A.FIELDNAME = B.FIELDNAME) ORDER BY FIELDNAME, LABEL_ID");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPCMPROG_CompPCList(String b1, String b2, String b3, String b4) {
		ENTStmt stmt = new ENTStmt("SELECT OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7  FROM PSPCMPROG WHERE OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? ORDER BY OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		stmt.bindVals.put(3, b3);
		stmt.bindVals.put(4, b4);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPCMPROG_RecordPCList(String b1, String b2) {
		ENTStmt stmt = new ENTStmt("SELECT OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7  FROM PSPCMPROG WHERE OBJECTID1 = ? AND OBJECTVALUE1 = ? ORDER BY OBJECTID1,OBJECTVALUE1, OBJECTID2,OBJECTVALUE2, OBJECTID3,OBJECTVALUE3, OBJECTID4,OBJECTVALUE4, OBJECTID5,OBJECTVALUE5, OBJECTID6,OBJECTVALUE6, OBJECTID7,OBJECTVALUE7");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPCMPROG_GetPROGTXT(String b1, String b2, String b3, String b4, String b5, String b6, String b7, String b8, String b9, String b10, String b11, String b12, String b13, String b14) {
		ENTStmt stmt = new ENTStmt("SELECT VERSION, PROGRUNLOC, NAMECOUNT, PROGLEN, PROGTXT, LICENSE_CODE, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, PROGFLAGS, PROGEXTENDS, PROGSEQ FROM PSPCMPROG WHERE  OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? AND  OBJECTID3 = ? AND OBJECTVALUE3 = ? AND  OBJECTID4 = ? AND OBJECTVALUE4 = ? AND  OBJECTID5 = ? AND OBJECTVALUE5 = ? AND  OBJECTID6 = ? AND OBJECTVALUE6 = ? AND  OBJECTID7 = ? AND OBJECTVALUE7 = ? ORDER BY PROGSEQ");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		stmt.bindVals.put(3, b3);
		stmt.bindVals.put(4, b4);
		stmt.bindVals.put(5, b5);
		stmt.bindVals.put(6, b6);
		stmt.bindVals.put(7, b7);
		stmt.bindVals.put(8, b8);
		stmt.bindVals.put(9, b9);
		stmt.bindVals.put(10, b10);
		stmt.bindVals.put(11, b11);
		stmt.bindVals.put(12, b12);
		stmt.bindVals.put(13, b13);
		stmt.bindVals.put(14, b14);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPCMPROG_GetRefs(String b1, String b2, String b3, String b4, String b5, String b6, String b7, String b8, String b9, String b10, String b11, String b12, String b13, String b14) {
		ENTStmt stmt = new ENTStmt("SELECT RECNAME, REFNAME, PACKAGEROOT, QUALIFYPATH, NAMENUM FROM PSPCMNAME WHERE  OBJECTID1 = ? AND OBJECTVALUE1 = ? AND  OBJECTID2 = ? AND OBJECTVALUE2 = ? AND  OBJECTID3 = ? AND OBJECTVALUE3 = ? AND  OBJECTID4 = ? AND OBJECTVALUE4 = ? AND  OBJECTID5 = ? AND OBJECTVALUE5 = ? AND  OBJECTID6 = ? AND OBJECTVALUE6 = ? AND  OBJECTID7 = ? AND OBJECTVALUE7 = ? ORDER BY NAMENUM");
		stmt.bindVals.put(1, b1);
		stmt.bindVals.put(2, b2);
		stmt.bindVals.put(3, b3);
		stmt.bindVals.put(4, b4);
		stmt.bindVals.put(5, b5);
		stmt.bindVals.put(6, b6);
		stmt.bindVals.put(7, b7);
		stmt.bindVals.put(8, b8);
		stmt.bindVals.put(9, b9);
		stmt.bindVals.put(10, b10);
		stmt.bindVals.put(11, b11);
		stmt.bindVals.put(12, b12);
		stmt.bindVals.put(13, b13);
		stmt.bindVals.put(14, b14);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPNLDEFN(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT VERSION, PNLTYPE, GRIDHORZ, GRIDVERT, FIELDCOUNT, MAXPNLFLDID, HELPCONTEXTNUM, PANELLEFT, PANELTOP, PANELRIGHT, PANELBOTTOM, PNLSTYLE, STYLESHEETNAME, PNLUSE, DEFERPROC, DESCR, POPUPMENU, LICENSE_CODE, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, OBJECTOWNERID, DESCRLONG FROM PSPNLDEFN WHERE PNLNAME = ?");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getPSPNLFIELD(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT PNLFLDID, FIELDTYPE, EDITSIZE, FIELDLEFT, FIELDTOP, FIELDRIGHT, FIELDBOTTOM, EDITLBLLEFT, EDITLBLTOP, EDITLBLRIGHT, EDITLBLBOTTOM, DSPLFORMAT, DSPLFILL, LBLTYPE, LBLLOC, LBLPADSIZE, LABEL_ID, LBLTEXT, FIELDUSE, FIELDUSETMP, DEFERPROC, OCCURSLEVEL, OCCURSCOUNT1, OCCURSCOUNT2, OCCURSCOUNT3, OCCURSOFFSET1, OCCURSOFFSET2, OCCURSOFFSET3, PNLFIELDNAME, RECNAME, FIELDNAME, SUBPNLNAME, ONVALUE, OFFVALUE, ASSOCFIELDNUM, FIELDSTYLE, LABELSTYLE, FIELDSIZETYPE, LABELSIZETYPE, PRCSNAME, PRCSTYPE, FORMATFAMILY, DISPFMTNAME, PROMPTFIELD, POPUPMENU, TREECTRLID, TREECTRLTYPE, MULTIRECTREE, NODECOUNT, GRDCOLUMNCOUNT, GRDSHOWCOLHDG, GRDSHOWROWHDG, GRDODDROWSTYLE, GRDEVENROWSTYLE, GRDACTIVETABSTYLE, GRDINACTIVETABSTYL, GRDNAVBARSTYLE, GRDLABELSTYLE, GRDLBLMSGSET, GRDLBLMSGNUM, GRDLBLALIGN, GRDACTTYPE, TABENABLE, PBDISPLAYTYPE, OPENNEWWINDOW, URLDYNAMIC,  URL_ID, GOTOPORTALNAME, GOTONODENAME, GOTOMENUNAME, GOTOPNLGRPNAME,  GOTOMKTNAME, GOTOPNLNAME, GOTOPNLACTION, SRCHBYPNLDATA, SCROLLACTION, TOOLACTION, CONTNAME, CONTNAMEOVER, CONTNAMEDISABLE, PTLBLIMGCOLLAPSE, PTLBLIMGEXPAND, SELINDICATORTYPE, PTADJHIDDENFIELDS, PTCOLLAPSEDATAAREA, PTDFLTVIEWEXPANDED, PTHIDEFIELDS, SHOWCOLHIDEROWS, PTLEBEXPANDFIELD, SHOWTABCNTLBTN, SECUREINVISIBLE, ENABLEASANCHOR, URLENCODEDBYAPP, USEDEFAULTLABEL, GRDALLOWCOLSORT FROM PSPNLFIELD WHERE PNLNAME = ? ORDER BY FIELDNUM");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	/**
	 * TODO: Clean this up. Remember that this query is dynamically generated based on 1) the structure
	 * and keys on the search record 2) the data in the component buffer following execution of SearchInit PC.
	 */
	public static PreparedStatement getSearchRecordFillQuery() {
		// I hardcoded: "PS_LS_SS_PERS_SRCH", "EMPLID", the value for EMPLID, "OPRID", and the value for OPRID.
/**		ENTStmt stmt = new ENTStmt("SELECT DISTINCT EMPLID FROM PS_LS_SS_PERS_SRCH WHERE EMPLID LIKE '" +
			((PTString)Environment.getCompBufferEntry("LS_SS_PERS_SRCH.EMPLID").dereference()).value() + "%' AND OPRID=? ORDER BY EMPLID");
*/
		ENTStmt stmt = new ENTStmt("SELECT DISTINCT EMPLID FROM PS_LS_SS_PERS_SRCH WHERE EMPLID LIKE '"
			+ "AA0001%' AND OPRID=? ORDER BY EMPLID");
		stmt.bindVals.put(1, ((PTString)Environment.getSystemVar("%OperatorId")).read());
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement getSubrecordsUsingPSDBFIELD_PSRECFIELD_JOIN(String b1) {
		ENTStmt stmt = new ENTStmt("SELECT FIELDNUM, FIELDNAME, TO_CHAR(CAST((LASTUPDDTTM) AS TIMESTAMP),'YYYY-MM-DD-HH24.MI.SS.FF'), LASTUPDOPRID, RECNAME FROM PSRECFIELD WHERE RECNAME = ? AND SUBRECORD = 'Y' ORDER BY RECNAME, FIELDNUM");
		stmt.bindVals.put(1, b1);
		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement prepareFillStmt(Record recDefn, String whereStr, String[] bindVals) {

		StringBuilder query = new StringBuilder(
			generateSelectClause(recDefn, "FILL"));

        // Replace numeric bind sockets (":1") with "?".
        Matcher bindIdxMatcher = bindIdxPattern.matcher(whereStr);
        whereStr = bindIdxMatcher.replaceAll("?");

        // Replace occurrences of %DATEIN(*) with TO_DATE(*,'YYYY-MM-DD')
        Matcher dateInMatcher = dateInPattern.matcher(whereStr);
        while(dateInMatcher.find()) {
            //log.debug("Found DATEIN: " + dateInMatcher.group(0));
            whereStr = dateInMatcher.replaceAll("TO_DATE("+
                dateInMatcher.group(1)+",'YYYY-MM-DD')");
        }

        query.append("  ").append(whereStr);
        //log.debug("Fill query string: {}", query.toString());

		/**
		 * Prepare the statement.
		 */
		ENTStmt stmt = new ENTStmt(query.toString());
		for(int i = 0; i < bindVals.length; i++) {
			stmt.bindVals.put(i+1, bindVals[i]);
		}

		return stmt.generatePreparedStmt(conn);
	}

	public static PreparedStatement prepareSelectByKeyEffDtStmt(
		Record recDefn, PTRecord recObj, PTDate effDt) {

		String tableAlias = "A";
		StringBuilder query = new StringBuilder(
			generateSelectClause(recDefn, tableAlias));

		query.append(" WHERE ");

		List<RecordField> rfList = recDefn.getExpandedFieldList();
		List<String> bindVals = new ArrayList<String>();

		boolean isFirstKey = true;
		for(RecordField rf : rfList) {
			if(rf.isKey()) {
				if(!isFirstKey) { query.append(" AND "); }
				isFirstKey = false;

				query.append(tableAlias).append(".")
						.append(rf.FIELDNAME).append("=");

				if(!rf.FIELDNAME.equals("EFFDT")) {
					query.append("?");
					bindVals.add((String)recObj.fields.get(rf.FIELDNAME)
						.getValue().read());
				} else {
					/**
					 * Insert subquery for EFFDT field.
					 */
					query.append("(")
						 .append("SELECT MAX(EFFDT) FROM PS_")
						 .append(recDefn.RECNAME)
						 .append(" B WHERE ");

					boolean isFirstKeyOnSub = true;
					for(RecordField subRf : rfList) {
						if(subRf.isKey()) {
							if(!isFirstKeyOnSub) { query.append(" AND "); }
							if(!subRf.FIELDNAME.equals("EFFDT")) {
								query.append("B.").append(subRf.FIELDNAME)
									 .append("=").append(tableAlias)
									 .append(".").append(subRf.FIELDNAME);
							} else {
								query.append("B.EFFDT<=TO_DATE(")
									 .append("?,'YYYY-MM-DD')");
								bindVals.add(effDt.read());
							}
							isFirstKeyOnSub = false;
						}
					}
					query.append(")");
				}
			}
		}

		ENTStmt stmt = new ENTStmt(query.toString());
		for(int i = 0; i < bindVals.size(); i++) {
			stmt.bindVals.put(i+1, bindVals.get(i));
		}

		return stmt.generatePreparedStmt(conn);
	}

	private static String generateSelectClause(Record recDefn,
			String tableAlias) {

		StringBuilder selectClause = new StringBuilder("SELECT ");
		List<RecordField> rfList = recDefn.getExpandedFieldList();

        for(int i = 0; i < rfList.size(); i++) {
            if(i > 0) { selectClause.append(","); }
            String fieldname = rfList.get(i).FIELDNAME;

            // Selected date fields must be wrapped with TO_CHAR directive.
            if(rfList.get(i).getSentinelForUnderlyingValue()
                     instanceof PTDate) {
                selectClause.append("TO_CHAR(").append(tableAlias)
					.append(".").append(fieldname)
                    .append(",'YYYY-MM-DD')");
            } else {
                selectClause.append(tableAlias).append(".").append(fieldname);
            }
        }
        selectClause.append(" FROM PS_").append(recDefn.RECNAME)
			.append(" ").append(tableAlias);

		return selectClause.toString();
	}

	public static void disconnect() {
		try {
			conn.close();
		} catch(java.sql.SQLException sqle) {
			log.warn("Unable to close connection to database.", sqle);
		}
	}
}
