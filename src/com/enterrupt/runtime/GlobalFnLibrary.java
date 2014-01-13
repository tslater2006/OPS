package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.pt.*;
import java.sql.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;

public class GlobalFnLibrary {

	private static Logger log = LogManager.getLogger(GlobalFnLibrary.class.getName());

	/**
	 * This is a shared function used by logical PeopleCode functions (tests for
	 * blank values).
	 * IMPORTANT: Use this: http://it.toolbox.com/blogs/spread-knowledge/understanding-blanknull-field-values-for-using-with-all-and-none-peoplecode-functions-40672
	 * as a reference for null/blank rules with PeopleSoft data types.
	 */
	private static boolean doesContainValue(PTType p) {
		if(p instanceof PTField) {
			return doesContainValue(((PTField)p).getValue());
		} else if(p instanceof PTString) {
			return ((PTString)p).read() != null && !((PTString)p).read().equals(" ");
		} else {
			throw new EntVMachRuntimeException("Unexpected data type passed " +
				"to doesContainValue(ptdt).");
		}
	}

	/*************************************/
    /** Global system functions          */
    /*************************************/

    /**
     * Return true if none of the specified fields contain a value, return false
     * if one or more contain a value.
     */
    public static void PT_None() {
        for(PTType arg : Environment.getArgsFromCallStack()) {
			if(doesContainValue(arg)) {
	        	Environment.pushToCallStack(Environment.FALSE);
	            return;
			}
        }
        Environment.pushToCallStack(Environment.TRUE);
    }

	/**
	 * Return true if all of the specified fields contain a value, return false
	 * if one or more do not.
	 */
    public static void PT_All() {
        for(PTType arg : Environment.getArgsFromCallStack()) {
			if(!doesContainValue(arg)) {
	        	Environment.pushToCallStack(Environment.FALSE);
	            return;
			}
        }
        Environment.pushToCallStack(Environment.TRUE);
    }

    public static void PT_Hide() {
        Environment.getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void PT_SetSearchDialogBehavior() {
        Environment.getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void PT_AllowEmplIdChg() {
        Environment.getArgsFromCallStack();
        // Not yet implemented.
    }

    /**
     * TODO: Return true if DoModalComponent
     * has been previously called; requires more research.
     */
    public static void PT_IsModalComponent() {

        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected zero arguments.");
        }

        Environment.pushToCallStack(Environment.FALSE);
    }

	public static void PT_CreateRecord() {

        List<PTType> args = Environment.getArgsFromCallStack();
		if(args.size() != 1 || (!(args.get(0) instanceof PTString))) {
			throw new EntVMachRuntimeException("Expected single string arg.");
		}

		PTRecord rec = PTRecord.getSentinel().alloc(
			DefnCache.getRecord(((PTString)args.get(0)).read()));
		rec.setDefault();
		Environment.pushToCallStack(rec);
	}

	public static void PT_CreateRowset() {

        List<PTType> args = Environment.getArgsFromCallStack();
		if(args.size() != 1 || (!(args.get(0) instanceof PTString))) {
			throw new EntVMachRuntimeException("Expected single string arg.");
		}

		Environment.pushToCallStack(PTRowset.getSentinel().alloc(
			DefnCache.getRecord(((PTString)args.get(0)).read())));
	}

	public static void PT_CreateArray() {

		/**
		 * I am simply calling CreateArrayRept for now,
		 * because I saw input for CreateArray(" ", 0) despite
		 * the fact that the documentation says all arguments should
		 * be the same type. In the future, there will likely be
		 * instances where something other than CreateArrayRept
		 * should be done.
		 */
		PT_CreateArrayRept();
	}

	public static void PT_CreateArrayRept() {

        List<PTType> args = Environment.getArgsFromCallStack();
		if(args.size() != 2 || (!(args.get(1) instanceof PTInteger))) {
			throw new EntVMachRuntimeException("Expected two args, with the second "
				+ "being an integer.");
		}

		int initialSize = ((PTInteger)args.get(1)).read();

		/**
		 * If the type argument passed in is an array, create a new array
		 * reference with a dimension that exceeds the array argument's by 1.
		 * Otherwise create a one-dimension array.
		 */
		PTArray newArray = null;
		if(args.get(0) instanceof PTArray) {
			newArray = PTArray.getSentinel(((PTArray)args.get(0)).dimensions + 1,
				(PTArray)args.get(0)).alloc();
		} else {
			newArray = PTArray.getSentinel(1, args.get(0)).alloc();
		}

		/**
		 * IMPORTANT NOTE: If the type passed to this CreateArrayRept call is
		 * an array, each iteration of the initialization loop should insert
		 * a reference to that array into the array being initialized. The documentation
		 * states that this is the behavior that occurs, unless .Clone() is used.
		 */
		for(int i = 0; i < initialSize; i++) {
			throw new EntVMachRuntimeException("Must support array instantiation in "+
				"CreateArrayRept; make sure to check toString() output.");
		}

		Environment.pushToCallStack(newArray);
	}

	/*************************************/
    /** Shared EVM functions          	 */
    /*************************************/

	public static void readRecordFromResultSet(Record recDefn,
			PTRecord recObj, ResultSet rs) throws SQLException {

		List<RecordField> rfList = recDefn.getExpandedFieldList();
		ResultSetMetaData rsMetadata = rs.getMetaData();
		int numCols = rsMetadata.getColumnCount();

		for(int i = 1; i <= numCols; i++) {
			String colName = rsMetadata.getColumnName(i);
			PTField newFld = recObj.getField(rfList.get(i-1).FIELDNAME);
			String colTypeName = rsMetadata.getColumnTypeName(i);

			log.debug("Copying {} with type {} from resultset to Field:{} "+
				"with type flag {}", colName, colTypeName,
				newFld.recFieldDefn.FIELDNAME, newFld.getValue().getType());

			switch(newFld.getValue().getType()) {
				case STRING:
					if(colTypeName.equals("VARCHAR2")) {
						((PTString)newFld.getValue()).write(
							rs.getString(colName));
					} else {
						throw new EntVMachRuntimeException("Unexpected db " +
							"type for Type.STRING: " + colTypeName + "; " +
							"colName=" + colName);
					}
					break;
				case NUMBER:
					if(colTypeName.equals("NUMBER")) {
						if(rs.getDouble(colName) % 1 == 0) {
							((PTNumber)newFld.getValue()).write(
								rs.getInt(colName));
						} else {
							((PTNumber)newFld.getValue()).write(
								rs.getDouble(colName));
						}
					} else {
						throw new EntVMachRuntimeException("Unexpected db " +
							"type for Type.NUMBER: " + colTypeName + "; " +
							"colName=" + colName);
					}
					break;
				case DATE:
					if(colTypeName.equals("VARCHAR2")) {
						((PTDate)newFld.getValue()).write(
							rs.getString(colName));
					} else {
						throw new EntVMachRuntimeException("Unexpected db " +
							"type for Type.DATE: " + colTypeName + "; " +
							"colName=" + colName);
					}
					break;
				default:
					throw new EntVMachRuntimeException("Unexpected field " +
						"value type encountered when filling rowset: " +
						 newFld.getValue().getType());
			}
		}
	}
}
