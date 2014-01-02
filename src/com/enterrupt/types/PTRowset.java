package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import java.sql.*;
import com.enterrupt.sql.*;
import com.enterrupt.runtime.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;
import java.util.regex.*;

public class PTRowset extends PTObjectType {

	private static Type staticTypeFlag = Type.ROWSET;
	public Record recDefn;
	private List<PTRecord> rows;
	private static Map<String, Method> ptMethodTable;
	private static Pattern bindIdxPattern;
	private static Pattern dateInPattern;

	private static Logger log = LogManager.getLogger(PTRowset.class.getName());

	static {
		// cache pointers to PeopleTools Rowset methods.
		Method[] methods = PTRowset.class.getMethods();
    	ptMethodTable = new HashMap<String, Method>();
		for(Method m : methods) {
			if(m.getName().indexOf("PT_") == 0) {
				ptMethodTable.put(m.getName().substring(3), m);
			}
		}

		// compile meta-SQL detection regex patterns.
		bindIdxPattern = Pattern.compile(":\\d+");
		dateInPattern = Pattern.compile("%DATEIN\\((.+?)\\)");
	}

	protected PTRowset() {
		super(staticTypeFlag);
	}

	protected PTRowset(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		this.rows = new ArrayList<PTRecord>();

		// One row is always present in the rowset, even when flushed.
		this.rows.add(PTRecord.getSentinel().alloc(this.recDefn));
	}

    public PTType dotProperty(String s) {
		return null;
    }

    public Callable dotMethod(String s) {
		if(ptMethodTable.containsKey(s)) {
			return new Callable(ptMethodTable.get(s), this);
		}
		return null;
    }

	public void PT_Flush() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected zero arguments.");
        }

		// One row is always present in the rowset, even when flushed.
		this.rows.clear();
		this.rows.add(PTRecord.getSentinel().alloc(this.recDefn));
	}

	public void PT_Fill() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() < 1) {
            throw new EntVMachRuntimeException("Expected at least one string arg.");
        }

		StringBuilder query = new StringBuilder("SELECT ");
		List<RecordField> rfList = this.recDefn.getExpandedFieldList();
		PTRecord firstRow = this.rows.get(0);

		for(int i = 0; i < rfList.size(); i++) {
			if(i > 0) { query.append(","); }
			String fieldname = rfList.get(i).FIELDNAME;

			// Selected date fields must be wrapped with TO_CHAR directive.
			if(firstRow.fields.get(fieldname).getValue() instanceof PTDate) {
				query.append("TO_CHAR(FILL.").append(fieldname)
					.append(",'YYYY-MM-DD')");
			} else {
				query.append("FILL.").append(fieldname);
			}
		}
		query.append(" FROM PS_").append(this.recDefn.RECNAME).append(" FILL");

		String whereStr = ((PTString)args.get(0)).read();

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

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Gather bind values.
		String[] bindVals = new String[args.size() - 1];
		for(int i = 1; i < args.size(); i++) {
			bindVals[i-1] = (String)((PTPrimitiveType)args.get(i)).read();
			//log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
 		}

		try {
			pstmt = StmtLibrary.prepareArbitraryStmt(query.toString(), bindVals);
			rs = pstmt.executeQuery();

			int rowsRead = 0;
			while(rs.next()) {
				/**
				 * TODO: Fill this rowset with records for each result.
				 */
				rowsRead++;
			}

			// Return the number of rows read from the fill operation.
			Environment.pushToCallStack(Environment.getFromLiteralPool(rowsRead));

		} catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }
	}

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRowset &&
			this.getType() == a.getType());
	}

    public static PTRowset getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTRowset)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTRowset sentinelObj = new PTRowset();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    /**
     * Allocated rowsets must have an associated record defn in order
     * to determine the type of the value enclosed within them. However, this
     * defn is not part of the type itself; a Rowset variable can be assigned
     * any Rowset object, regardless of its underlying record defn.
     */
    public PTRowset alloc(Record recDefn) {
        PTRowset newObj = new PTRowset(recDefn);
        PTType.clone(this, newObj);
        return newObj;
    }

    private static String getCacheKey() {
        StringBuilder b = new StringBuilder(staticTypeFlag.name());
        return b.toString();
    }

	@Override
	public String toString() {
		return super.toString();
	}
}
