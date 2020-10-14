package com.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UniGeDataBaseRepo extends SQLiteOpenHelper {

	private static final String DB_NAME = "UNIGE_DB";
	private static final int DB_VERSION = 1;
	public SQLiteDatabase sqliteDBInstance;

	// STUDENTS TABLE DETAILS
	private static final String STUDENTS_TB = "STUDENTS";
	private static final String COL1_STUD_TB_ID = "STUD_ID";
	private static final String COL2_STUD_TB_PASSWORD = "STUD_PASSWORD";
	private static final String COL3_STUD_TB_EMAIL = "STUD_EMAIL";
	private static final String COL4_STUD_TB_FNAME = "STUD_FNAME";
	private static final String COL5_STUD_TB_LNAME = "STUD_LNAME";
	private static final String COL6_STUD_TB_YEAR = "STUD_YEAR";
	private static final String COL7_STUD_TB_STUD_PENDING_COLLEGE = "STUD_PENDING_COLLEGE";

	// BOOKS TABLE DETAILS
	private static final String BOOKS_TB = "BOOKS";
	private static final String COL1_BOOK_TB_ISBN = "BOOK_ISBN";
	private static final String COL2_BOOK_TB_NAME = "BOOK_NAME";
	private static final String COL3_BOOK_TB_AUTHOR = "BOOK_AUTHOR";
	private static final String COL4_BOOK_TB_IMAGE = "BOOK_IMAGE";
	private static final String COL5_BOOK_TB_DESCRIPTION = "BOOK_DESCRIPTION";
	private static final String COL6_BOOK_TB_AVAILABILITY = "BOOK_AVAILABILITY";

	// STUDENT_DUE_CHECK TABLE DETAILS
	private static final String STUDENT_DUE_CHECK_TB = "STUDENT_DUE_CHECK";
	private static final String COL1_ST_DUE_CHK_TB_ISBN = "BOOK_ISBN";
	private static final String COL2_ST_DUE_CHK_TB_STUD_ID = "STUD_ID";
	private static final String COL3_ST_DUE_CHK_TB_STUD_PENDING_LIBRARY = "STUD_PENDING_LIBRARY";
	private static final String COL4_ST_DUE_CHK_TB_BOOK_RETURNED_FLAG = "BOOK_RETURNED_FLAG";

	// BOOK SEARCH TABLE DETAILS
	private static final String BOOK_SEARCH_TB = "BOOK_SEARCH";
	private static final String COL1_BOOK_SEARCH_TB_ISBN = "BOOK_ISBN";
	private static final String COL2_BOOK_SEARCH_TB_STUD_ID = "STUD_ID";
	private static final String COL3_BOOK_SEARCH_TB_BOOK_DATE_OF_ISSUE = "BOOK_DATE_OF_ISSUE";
	private static final String COL4_BOOK_SEARCH_TB_BOOK_DATE_OF_RETURN = "BOOK_DATE_OF_RETURN";

	// BOOK RESERVATION TABLE DETAILS
	private static final String BOOK_RESERVATION_TB = "BOOK_RESERVATION";
	private static final String COL1_BOOK_RESERVATION_TB_ISBN = "BOOK_ISBN";
	private static final String COL2_BOOK_RESERVATION_TB_STUD_ID = "STUD_ID";
	private static final String COL3_BOOK_RESERVATION_TB_BOOK_RESERVED_DATE = "BOOK_RESERVED_DATE";

	// LIBRARIAN TABLE DETAILS
	private static final String LIBRARIAN_TB = "LIBRARIAN";
	private static final String COL1_LIBRARIAN_TB_LIBRARIAN_ID = "LIBRARIAN_ID";
	private static final String COL2_LIBRARIAN_TB_LIBRARIAN_PASSWORD = "LIBRARIAN_PASSWORD";
	private static final String COL3_LIBRARIAN_TB_LIBRARIAN_NAME = "LIBRARIAN_NAME";
	private static final String COL4_LIBRARIAN_TB_LIBRARIAN_ADDRESS = "LIBRARIAN_ADDRESS";
	private static final String COL5_LIBRARIAN_TB_LIBRARIAN_PIN = "LIBRARIAN_PIN";

	// parameterised constructor
	public UniGeDataBaseRepo(Context context, String name, CursorFactory factory,
                             int version) {
		super(context, DB_NAME, factory, DB_VERSION);
	}

	public UniGeDataBaseRepo(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void openDB() throws SQLException {
		if (this.sqliteDBInstance != null) {
		}
		if (this.sqliteDBInstance == null) {
			this.sqliteDBInstance = this.getWritableDatabase();
			this.sqliteDBInstance.enableWriteAheadLogging();
		}
	}

	// when we open db before finishing the activity we should close the
	public void closeDB() {
		if (this.sqliteDBInstance != null) {
			if (this.sqliteDBInstance.isOpen())
				this.sqliteDBInstance.close();
		}
	}

	// to check whether entered user id present in STUDENTS db or not
	public boolean checkStudentUserId(String userId) {
		boolean str_Flag = false;
		String qry = "select STUD_ID from STUDENTS where STUD_ID" + "='"
				+ userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		Log.i("QUERY", qry+cur.getCount());
		str_Flag = cur.getCount() == 1;
		return str_Flag;
	}

	// to get student user id password present in the STUDENTS db
	public Cursor getStudentPassword(String userId) {
		String qry = "select STUD_PASSWORD from STUDENTS where STUD_ID" + "='"
				+ userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	// to get all student details based on his user id present in the STUDENTS
	// db
	public Cursor getStudentDetails(String userId) {
		String qry = "select STUD_ID, STUD_EMAIL, STUD_FNAME, STUD_LNAME from STUDENTS where STUD_ID"
				+ "='" + userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	// to get all student book history based on his user id present in the
	// BOOK_SEARCH
	// db
	public Cursor getStudentBookHistory(String userId) {
		String qry = "select BOOK_ISBN, BOOK_DATE_OF_ISSUE, BOOK_DATE_OF_RETURN from BOOK_SEARCH where STUD_ID"
				+ "='" + userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	// to get all book names based on his book_isbn present in the BOOKS
	// db
	public Cursor getBookNames(String book_isbn) {
		String qry = "select BOOK_NAME from BOOKS WHERE BOOK_ISBN=" + "='"
				+ book_isbn + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	// to get all books details present in the BOOKS db
	public Cursor getAllBookDetails() {

		String qry = "select * from BOOKS";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	// to get all books isbn from BOOKS db
	public Cursor getAllBooks() {

		String qry = "select BOOK_ISBN,BOOK_NAME from BOOKS";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	public Cursor getSpecificBookByName(String name) {

		String qry = "select BOOK_NAME, BOOK_AUTHOR, BOOK_IMAGE,BOOK_DESCRIPTION, BOOK_AVAILABILITY,BOOK_ISBN from BOOKS where BOOK_NAME LIKE '%"+name+"%'";
		Log.i("QUERY", qry);
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	public Cursor getSpecificBookByISBN(String isbn) {

		String qry = "select BOOK_NAME, BOOK_AUTHOR, BOOK_IMAGE,BOOK_DESCRIPTION, BOOK_AVAILABILITY,BOOK_ISBN from BOOKS where BOOK_ISBN = '"+isbn+"'";
		Log.i("QUERY", qry);
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}


	// to get all books based books availability in the BOOKS db
	public Cursor getAvailableBooks(String available) {

		String qry = "select BOOK_ISBN, BOOK_NAME, BOOK_AUTHOR, BOOK_IMAGE from BOOKS where BOOK_AVAILABILITY"
				+ "='" + available + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	// to get full book details based books isbn in the BOOKS db
	public Cursor getFullBookDetailsBasedOnIsbn(String isbn) {

		String qry = "select BOOK_NAME, BOOK_AUTHOR, BOOK_IMAGE,BOOK_DESCRIPTION, BOOK_AVAILABILITY from BOOKS where BOOK_ISBN"
				+ "='" + isbn + "'";
		Log.i("QUERY", qry);
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	public Cursor getFullBookDetailsBasedOnName(String name) {

		String qry = "select BOOK_NAME, BOOK_AUTHOR, BOOK_IMAGE,BOOK_DESCRIPTION, BOOK_AVAILABILITY,BOOK_ISBN from BOOKS where BOOK_NAME "
				+ "LIKE '%" + name + "%'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		Log.i("QUERY", qry);
		return cur;
	}
	// to get book availability based books isbn in the BOOKS db
		public Cursor getBookAvailabilityBasedOnIsbn(String isbn) {

			String qry = "select BOOK_AVAILABILITY from BOOKS where BOOK_ISBN"
					+ "='" + isbn + "'";
			Cursor cur = sqliteDBInstance.rawQuery(qry, null);

			return cur;
		}

	public Cursor getStudentLibraryDue(String userId, String lib_pending) {

		String qry = "select * from STUDENT_DUE_CHECK where STUD_ID" + "='"
				+ userId + "'" + " AND " + "STUD_PENDING_LIBRARY" + "='"
				+ lib_pending + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	// to get count of records having for a student from STUDENT_DUE_CHECK
	public Cursor getStudentBookCount(String userId, String book_returned_flag) {
		String qry = "select * from STUDENT_DUE_CHECK where STUD_ID" + "='"
				+ userId + "'" + " AND " + "BOOK_RETURNED_FLAG" + "='"
				+ book_returned_flag + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	public Cursor getStudentBookCountFromReservation(String userId) {

		String qry = "select * from BOOK_RESERVATION where STUD_ID" + "='"
				+ userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur;
	}

	public boolean updateBookAvailabilityInBooksDb(String book_isbn,
			String availability_flag) {
		ContentValues args = new ContentValues();
		args.put(COL6_BOOK_TB_AVAILABILITY, availability_flag);
		Log.i("Updated COL6_BOOK_TB_AVAILABILITY", availability_flag
				+ " for Book isbn " + book_isbn);
		return sqliteDBInstance.update(BOOKS_TB, args, COL1_BOOK_TB_ISBN + "='"
				+ book_isbn + "'", null) > 0;
	}

	// to insert reserved details into BOOK_RESERVATION data base
	public long insertReseredBookDetails(String book_isbn, String stud_id,String reserved_date) {
		ContentValues cv1 = new ContentValues();
		cv1.put(COL1_BOOK_RESERVATION_TB_ISBN, book_isbn);
		cv1.put(COL2_BOOK_RESERVATION_TB_STUD_ID, stud_id);
		cv1.put(COL3_BOOK_RESERVATION_TB_BOOK_RESERVED_DATE, reserved_date);
		Log.i("insert","Inserted reserved book data into BOOK_RESERVATION db...");
		return sqliteDBInstance.insert(BOOK_RESERVATION_TB, null, cv1);
	}

	// to check whether entered user id present in LIBRARIAN db or not
	public boolean checkLibrarianUserId(String userId) {
		boolean str_Flag = false;

		String qry = "select LIBRARIAN_ID from LIBRARIAN where LIBRARIAN_ID"
				+ "='" + userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

        str_Flag = cur.getCount() == 1;
		return str_Flag;
	}

	// to get student user id password present in the LIBRARIAN db
	public Cursor getLibrarianPassword(String userId) {

		String qry = "select LIBRARIAN_PASSWORD from LIBRARIAN where LIBRARIAN_ID"
				+ "='" + userId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);

		return cur;
	}

	// to insert reserved details into BOOK_Search data base
	public long insertReservedBookDetailsIntoBookSearchDb(String book_isbn,
			String stud_id, String dateOfIssue, String dateOfReturn) {
		// TODO Auto-generated method stub
		ContentValues cv1 = new ContentValues();
		cv1.put(COL1_BOOK_SEARCH_TB_ISBN, book_isbn);
		cv1.put(COL2_BOOK_SEARCH_TB_STUD_ID, stud_id);
		cv1.put(COL3_BOOK_SEARCH_TB_BOOK_DATE_OF_ISSUE, dateOfIssue);
		cv1.put(COL4_BOOK_SEARCH_TB_BOOK_DATE_OF_RETURN, dateOfReturn);
		Log.i("insert", "Inserted reserved book data into BOOK_Search db...");
		return sqliteDBInstance.insert(BOOK_SEARCH_TB, null, cv1);

	}

	// to insert reserved details into STUDENT_DUE_CHECK data base
	public long insertReservedBookDetailsIntoStudentDueCheckDb(
			String book_isbn, String stud_id, String pending_lib,String returned_flag) {
		// TODO Auto-generated method stub
		ContentValues cv1 = new ContentValues();
		cv1.put(COL1_ST_DUE_CHK_TB_ISBN, book_isbn);
		cv1.put(COL2_ST_DUE_CHK_TB_STUD_ID, stud_id);
		cv1.put(COL3_ST_DUE_CHK_TB_STUD_PENDING_LIBRARY, pending_lib);
		cv1.put(COL4_ST_DUE_CHK_TB_BOOK_RETURNED_FLAG, returned_flag);
		Log.i("insert",
				"Inserted reserved book data into STUDENT_DUE_CHECK db...");
		return sqliteDBInstance.insert(STUDENT_DUE_CHECK_TB, null, cv1);

	}
	public int getNumberofBooksFromBookSearchonId(String studId){
		String qry = "select * from BOOK_SEARCH where STUD_ID" + "='"
				+ studId + "'";
		Cursor cur = sqliteDBInstance.rawQuery(qry, null);
		return cur.getCount();
	}

	// to delete reserved details in BOOK_RESERVATION data base
	public void deleteReservedBookDetailsFromBookReservationDb(
			String book_isbn, String stud_id) {
		sqliteDBInstance = this.getWritableDatabase();

		sqliteDBInstance.delete(BOOK_RESERVATION_TB, COL1_BOOK_SEARCH_TB_ISBN
				+ "='" + book_isbn + "'" + " AND "
				+ COL2_BOOK_SEARCH_TB_STUD_ID + "='" + stud_id + "'", null);

		Log.i("Deleted record  of book_isbn no,stud_id from BOOK_RESERVATION: ",
				book_isbn + " ," + stud_id);

	}
	
	public long insertRegistration(String stud_fname, String stud_lname, String stud_id, String stud_pwd,String stud_email )
	{
		ContentValues cv2 = new ContentValues();
		
		cv2.put(COL4_STUD_TB_FNAME, stud_fname);
		cv2.put(COL5_STUD_TB_LNAME, stud_lname);
		cv2.put(COL1_STUD_TB_ID, stud_id);
		cv2.put(COL2_STUD_TB_PASSWORD, stud_pwd);
		cv2.put(COL3_STUD_TB_EMAIL, stud_email);
		Log.i("UniGeDataBaseRepo", "insert student registration details into STUDENT_TB");
		return sqliteDBInstance.insert(STUDENTS_TB, null, cv2);
	}
	
	public long insertStudIdIntoStudDueCheckDB(String book_isbn, String stud_id, String stud_pending_library, String book_returned_flag)
	{
		ContentValues cv2 = new ContentValues();
		cv2.put(COL1_ST_DUE_CHK_TB_ISBN, book_isbn);
		cv2.put(COL2_ST_DUE_CHK_TB_STUD_ID, stud_id);
		cv2.put(COL3_ST_DUE_CHK_TB_STUD_PENDING_LIBRARY, stud_pending_library);
		cv2.put(COL4_ST_DUE_CHK_TB_BOOK_RETURNED_FLAG, book_returned_flag);
		
		Log.i("UniGeDataBaseRepo", "insert student registration details into STUDENT_DUE_CHECK_TB");
		return sqliteDBInstance.insert(STUDENT_DUE_CHECK_TB, null, cv2);
	}
	
	// to get librarian security pin present in the Librarian db
		public Cursor getLibSecurity(String libraian_id) {

			String qry = "select LIBRARIAN_PIN from LIBRARIAN where LIBRARIAN_ID" + "='"
					+ libraian_id + "'";
			Cursor cur = sqliteDBInstance.rawQuery(qry, null);
			return cur;
		}

	// to get librarian security pin present in the Librarian db
	public int checkMatricula(String studentMatricula) {
		String qry = "select * from STUDENTS where STUD_ID" + "='"
				+ studentMatricula + "'";
		int count = sqliteDBInstance.rawQuery(qry,null).getCount();
		return count;
	}

	public int checkIfBookAvailable(String isbn) {
		String qry = "select * from BOOKS where BOOK_ISBN " + "='"
				+ isbn + "'";
		int count = sqliteDBInstance.rawQuery(qry,null).getCount();
		return count;
	}
}