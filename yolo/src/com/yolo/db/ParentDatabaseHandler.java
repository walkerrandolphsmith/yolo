package com.yolo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ParentDatabaseHandler extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "parentsManager";
	
		//TABLE NAMES
		private static final String TABLE_PARENTS = "parents";

		// PARENT TABLE COLUMN NAMES
		public final String KEY_ID = "_id";
		public final String KEY_NAME = "name";
		
		public ParentDatabaseHandler(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
		
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_PARENTS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_NAME + " TEXT UNIQUE"
	        		+ ")";
	        db.execSQL(CREATE_PLACES_TABLE);
	       // updateKeys(db);
	    }
	    
	    @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARENTS);
			onCreate(db);
		}
	    
	    public void updateKeys(SQLiteDatabase db){
			String updateKeysQuery = "UPDATE sqlite_sequence SET seq = 0 WHERE name = " + TABLE_PARENTS;
	        db.execSQL(updateKeysQuery);
		}
	    
	    // Count
		public int size() {
			String countQuery = "SELECT  * FROM " + TABLE_PARENTS;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			cursor.close();

			return cursor.getCount();
		}

	 
	    /**
	     * All CRUD Operations
	     */
		
		// CREATE
		public void create(String parent) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_NAME, parent);
			
			try{
				db.insertOrThrow(TABLE_PARENTS, null, values);
			} catch (SQLiteConstraintException e){
				Log.w("SQLITE UNIQUE EXCEPTION", "Parent is already asigned this child's device.");
			}
			db.close();
		}
		
		//READ
		public String read(int id) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_PARENTS, new String[] { KEY_ID, KEY_NAME },
					KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
					null, null);
			if (cursor != null)
				cursor.moveToFirst();

			cursor.getString(0);
			return cursor.getString(1);
		}
		
		public Cursor read() {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_PARENTS,
					null);
			return cursor;
		}
		
		public List<String> getParents(Cursor cursor){
		List<String> parents = new ArrayList<String>();
		if (cursor != null)
			if(cursor.moveToFirst()){
				do {
					parents.add(cursor.getString(1));
				} while (cursor.moveToNext());
			} 
			return parents;
		}
		
		// Update
		public int updatePlace(String oldParent, String newParent) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_NAME, newParent);

			// Updating Row
			return db.update(TABLE_PARENTS, values, KEY_NAME + " = ?",
					new String[] { String.valueOf(oldParent) });
		}
		
		// DELETE
		public void delete(int primaryKey) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_PARENTS, KEY_ID + " = ?",
					new String[] { String.valueOf(primaryKey) });
			db.close();
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			Cursor cursor = read();
			
			if (cursor != null)
				if(cursor.moveToFirst()){
					do {
						sb.append(cursor.getString(0));
						sb.append(" | ");
						sb.append(cursor.getString(1));
						sb.append("\n");
					} while (cursor.moveToNext());
				} 
			
			return sb.toString();
		}  
}
