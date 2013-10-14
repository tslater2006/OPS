package com.enterrupt.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @sql : bind tokens should be non-numeric (i.e., "?")
 *        to ensure equals() works correctly.
 */
public class SQLStmt {
    public String sql;
    public HashMap<Integer, String> bindVals;

    public SQLStmt(String sql) {
        this.sql = sql;
        this.bindVals = new HashMap<Integer, String>();
    }

    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof SQLStmt))
            return false;

        SQLStmt otherStmt = (SQLStmt)obj;
        if(!this.sql.equals(otherStmt.sql) ||
            this.bindVals.size() != otherStmt.bindVals.size()) {
            return false;
        }

        // Ensure bind value indices and values match those in the other statement.
        for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
            if(!cursor.getValue().equals(
                    otherStmt.bindVals.get(cursor.getKey()))) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        String str = sql + "\n";
        for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
            str = str + ":" + cursor.getKey() + " = " + cursor.getValue() + "\n";
        }
        return str;
    }
}

