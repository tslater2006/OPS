package com.enterrupt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DbBroker {

	private static final String PS_NULL = " ";

	private static HashMap<String, Boolean> allRecordFields;
	private static HashMap<String, Boolean> allPages;

    public static void main(String[] args) {

		StmtLibrary.init();

		allRecordFields = new HashMap<String, Boolean>();
		allPages = new HashMap<String, Boolean>();

		loadComponent("SSS_STUDENT_CENTER", "GBL");

		for(Map.Entry<String, Boolean> cursor : allRecordFields.entrySet()) {
			System.out.println("Field: " + cursor.getKey());
		}

		System.out.println("Total pages: " + allPages.size());
		System.out.println("Total fields: " + allRecordFields.size());

		StmtLibrary.disconnect();
    }

	public static void loadComponent(String componentName, String market) {

		PreparedStatement pstmt;
		ResultSet rs;

		try {
			pstmt = StmtLibrary.getPSPNLGRPDEFN(componentName, market);
			rs = pstmt.executeQuery();
			rs.next();		//Do nothing with record for now.
			rs.close();
			pstmt.close();

			pstmt = StmtLibrary.getPSPNLGROUP(componentName, market);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				getAllPageFields(rs.getString("PNLNAME"));
			}
			rs.close();
			pstmt.close();

		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static void getAllPageFields(String pageName) {

		PreparedStatement pstmt;
		ResultSet rs;

		try{

			pstmt = StmtLibrary.getPSPNLDEFN(pageName);
			rs = pstmt.executeQuery();
			rs.next(); // Do nothing with record for now.
			rs.close();
			pstmt.close();

			pstmt = StmtLibrary.getPSPNLFIELD(pageName);
			rs = pstmt.executeQuery();

			ArrayList<String> subpanels = new ArrayList<String>();
	    	while(rs.next()) {
				if(rs.getInt("FIELDTYPE") == 11) {  //11: Subpage
					subpanels.add(rs.getString("SUBPNLNAME"));
    			} else {
					allRecordFields.put(rs.getString("RECNAME") + "." + rs.getString("FIELDNAME"), true);
				}
			}
			rs.close();
			pstmt.close();

			allPages.put(pageName, true);
			System.out.println("Retrieved Page." + pageName);

			for(String subpanelName : subpanels) {
				//Only retrieve page details if page hasn't been processed yet.
				if(!allPages.containsKey(subpanelName)) {
					getAllPageFields(subpanelName);
				}
			}

		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
