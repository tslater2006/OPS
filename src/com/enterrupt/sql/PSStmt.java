package com.enterrupt.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSStmt extends SQLStmt {

	// Note: this regex uses positve lookbehind and lookahead.
	private final String BIND_IDX_REGEX = "(?<=\\s)(:\\d+)(?=\\s?)";
	public String originalStmt;
	public int line_nbr;

	public PSStmt(String sql, int line_nbr) {
		super(sql.trim());

		this.line_nbr = line_nbr;

		Pattern bindIdxPattern = Pattern.compile(BIND_IDX_REGEX);
		Matcher m = bindIdxPattern.matcher(sql);
		this.originalStmt = sql;
		this.sql =  m.replaceAll("?").trim();
	}
}
