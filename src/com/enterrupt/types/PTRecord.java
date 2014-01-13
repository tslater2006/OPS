package com.enterrupt.types;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.pt.*;
import com.enterrupt.sql.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class PTRecord extends PTObjectType {

	private static Type staticTypeFlag = Type.RECORD;
	public Record recDefn;
	public Map<String, PTField> fields;
	public Map<Integer, PTField> fieldIdxTable;
	private static Map<String, Method> ptMethodTable;

	private static Logger log = LogManager.getLogger(PTRecord.class.getName());

    static {
        // cache pointers to PeopleTools Record methods.
        Method[] methods = PTRecord.class.getMethods();
        ptMethodTable = new HashMap<String, Method>();
        for(Method m : methods) {
            if(m.getName().indexOf("PT_") == 0) {
                ptMethodTable.put(m.getName().substring(3), m);
            }
        }
    }

	protected PTRecord() {
		super(staticTypeFlag);
	}

	protected PTRecord(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		// this map is linked in order to preserve the order in which fields are added.
		this.fields = new LinkedHashMap<String, PTField>();
		this.fieldIdxTable = new LinkedHashMap<Integer, PTField>();
		int i = 1;
		for(RecordField rf : this.recDefn.getExpandedFieldList()) {
		 	PTField newFld = PTField.getSentinel().alloc(rf);
			this.fields.put(rf.FIELDNAME, newFld);
			this.fieldIdxTable.put(i++, newFld);
		}
	}

    public PTType dotProperty(String s) {
		return this.fields.get(s);
    }

    public Callable dotMethod(String s) {
       if(ptMethodTable.containsKey(s)) {
            return new Callable(ptMethodTable.get(s), this);
        }
        return null;
    }

	public PTField getField(String fldName) {
		if(!this.fields.containsKey(fldName)) {
			throw new EntVMachRuntimeException("Call to getField with fldname=" +
				fldName + " did not match any field on this record: " + this.toString());
		}
		return this.fields.get(fldName);
	}

	public void setDefault() {
		for(Map.Entry<String, PTField> cursor : this.fields.entrySet()) {
			cursor.getValue().setDefault();
		}
	}

	public void PT_SetDefault() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected no args.");
        }

		this.setDefault();
	}

	public void PT_SelectByKeyEffDt() {
        List<PTType> args = Environment.getArgsFromCallStack();
	    if(args.size() != 1 || (!(args.get(0) instanceof PTDate))) {
            throw new EntVMachRuntimeException("Expected single date arg.");
        }

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			List<RecordField> rfList = this.recDefn.getExpandedFieldList();

			pstmt = StmtLibrary.prepareSelectByKeyEffDtStmt(this.recDefn,
						this, (PTDate)args.get(0));
			rs = pstmt.executeQuery();

			int numCols = rs.getMetaData().getColumnCount();
			if(numCols != rfList.size()) {
				throw new EntVMachRuntimeException("The number of columns returned " +
					"by the select by key query (" + numCols + ") differs from the number " +
					"of fields (" + rfList.size() +
					") in the record defn field list.");
			}

			/**
			 * Although multiple rows may exist in the ResultSet,
			 * only one row is read by SelectByKeyEffDt.
			 */
			PTBoolean returnVal = Environment.FALSE;
			if(rs.next()) {
				GlobalFnLibrary.readRecordFromResultSet(
					this.recDefn, this, rs);
				returnVal = Environment.TRUE;
			}

			// Return true if record was read, false otherwise.
			Environment.pushToCallStack(returnVal);

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

	/**
	 * Calls to make a record read-only should make its
	 * fields read-only as well.
	 */
	@Override
    public PTType setReadOnly() {
		super.setReadOnly();
		if(fields != null) {
			for(Map.Entry<String, PTField> cursor : fields.entrySet()) {
				cursor.getValue().setReadOnly();
			}
		}
        return this;
    }

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRecord &&
			this.getType() == a.getType());
	}

    public static PTRecord getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTRecord)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTRecord sentinelObj = new PTRecord();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    /**
     * Allocated records must have an associated record defn in order
     * to determine the type of the value enclosed within them. However, this
     * defn is not part of the type itself; a Record variable can be assigned
     * any Record object, regardless of its underlying record defn.
     */
    public PTRecord alloc(Record recDefn) {
        PTRecord newObj = new PTRecord(recDefn);
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
		b.append(",fields=").append(this.fields);
		return b.toString();
	}
}
