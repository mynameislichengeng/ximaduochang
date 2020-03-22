package com.evideo.kmbox.dao;

import com.evideo.kmbox.model.dao.data.StorageConstant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageVolumeDAOHelper extends SQLiteOpenHelper {
	
	public static final int VERSION = 1;
	
	public StorageVolumeDAOHelper(Context context, String volumePath) {
		this(new StorageVolumeDbContext(context, volumePath), StorageConstant.STORAGE_VOLUME_KMBOX_DATABASE_FILENAME, 
				null, VERSION);
	}

	private StorageVolumeDAOHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE if not exists tblMedia(" +
				"type INT DEFAULT 0, " +
				"songId INT, " +
				"originalInfo INT NOT NULL DEFAULT 0," + 
				"companyInfo INT NOT NULL DEFAULT 0, " +
				"volume INT, " +
				"volumeUUID NVARCHAR," + 
				"subtitle NVARCHAR," +
				"CONSTRAINT [] PRIMARY KEY (songId COLLATE NOCASE ASC));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	@Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.enableWriteAheadLogging();
    }

}
