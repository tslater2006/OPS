package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import java.sql.*;
import com.enterrupt.sql.*;
import com.enterrupt.runtime.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;

public class PTRowset extends PTObjectType {

	private static Type staticTypeFlag = Type.ROWSET;
	public Record recDefn;
	private PTRow emptyRow;
	private List<PTRow> rows;
	private static Map<String, Method> ptMethodTable;

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
	}

	protected PTRowset() {
		super(staticTypeFlag);
	}

	protected PTRowset(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		this.rows = new ArrayList<PTRow>();

		// One row is always present in the rowset, even when flushed.
		this.emptyRow = new PTRow(PTRecord.getSentinel().alloc(this.recDefn));
		this.emptyRow.setReadOnly();
		this.rows.add(this.emptyRow);
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

	public void getRow() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 1 || !(args.get(0) instanceof PTInteger)) {
            throw new EntVMachRuntimeException("Expected only one integer arg.");
        }

		int idx = ((PTInteger)args.get(0)).read();
		if(idx < 1 || idx > this.rows.size()) {
			throw new EntVMachRuntimeException("Index (" + idx + ") provided to " +
				"getRows is out of bounds; number of rows is " + this.rows.size());
		}

		// Must subtract 1 from idx; rowset indices start at 1.
		Environment.pushToCallStack(this.rows.get(idx - 1));
	}

	public void PT_Flush() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected zero arguments.");
        }
		this.internalFlush();
	}

	private void internalFlush() {
		// One row is always present in the rowset, even when flushed.
		this.rows.clear();
		this.rows.add(this.emptyRow);
	}

	public void PT_Fill() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() < 1) {
            throw new EntVMachRuntimeException("Expected at least one string arg.");
        }

		// Gather bind values following the WHERE string on the stack.
		String[] bindVals = new String[args.size() - 1];
		for(int i = 1; i < args.size(); i++) {
			bindVals[i-1] = (String)((PTPrimitiveType)args.get(i)).read();
			//log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
 		}

		// The rowset must be flushed before continuing.
		this.internalFlush();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			List<RecordField> rfList = this.recDefn.getExpandedFieldList();

			pstmt = StmtLibrary.prepareFillStmt(this.recDefn,
						((PTString)args.get(0)).read(), bindVals);
			rs = pstmt.executeQuery();

			int numCols = rs.getMetaData().getColumnCount();
			if(numCols != rfList.size()) {
				throw new EntVMachRuntimeException("The number of columns returned " +
					"by the fill query (" + numCols + ") differs from the number " +
					"of fields (" + rfList.size() +
					") in the record defn field list.");
			}

			int rowsRead = 0;
			while(rs.next()) {

				//If at least one row exists, remove the empty row.
				if(rowsRead == 0) {
					this.rows.clear();
				}

				PTRecord newRecord = PTRecord.getSentinel().alloc(this.recDefn);
				GlobalFnLibrary
					.readRecordFromResultSet(this.recDefn, newRecord, rs);
				this.rows.add(new PTRow(newRecord));
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
		StringBuilder b = new StringBuilder(super.toString());
		b.append(":").append(recDefn.RECNAME);
		b.append(",rows=").append(rows.size());
		return b.toString();
	}
}
