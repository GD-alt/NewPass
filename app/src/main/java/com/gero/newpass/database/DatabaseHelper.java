package com.gero.newpass.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gero.newpass.R;
import com.gero.newpass.encryption.EncryptionHelper;
import com.gero.newpass.utilities.StringHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Password.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_password_record";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "record_name";
    private static final String COLUMN_EMAIL = "record_email";
    private static final String COLUMN_PASSWORD = "record_password";
    private static final String KEY_ENCRYPTION = StringHelper.getSharedString();

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        assert context != null;
        SQLiteDatabase.loadLibs(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_PASSWORD + " TEXT);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a new entry with the given name, email, and password to the database.
     *
     * @param name      The name of the entry.
     * @param email     The email of the entry.
     * @param password  The password of the entry (it will be encrypted before being inserted into the database)
     */
    public void addEntry(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase(KEY_ENCRYPTION);
        ContentValues cv = new ContentValues();

        String encryptedPassword = EncryptionHelper.encrypt(password);

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, encryptedPassword);

        db.insert(TABLE_NAME, null, cv);
    }

    public Cursor readAllData() {
        SQLiteDatabase db = this.getReadableDatabase(KEY_ENCRYPTION);
        String query = "SELECT * FROM " + TABLE_NAME;

        return db.rawQuery(query, null);
    }

    /**
     * Updates an existing row in the database table with the specified row ID.
     *
     * @param row_id   The ID of the row to be updated.
     * @param name     The new value for the name column.
     * @param email    The new value for the email column.
     * @param password The new value for the password column.
     */
    public void updateData(String row_id, String name, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase(KEY_ENCRYPTION);
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);

        db.update(TABLE_NAME, cv, "id=?", new String[]{row_id});
    }


    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase(KEY_ENCRYPTION);
        db.delete(TABLE_NAME, "id=?", new String[]{row_id});
    }

    public boolean checkIfAccountAlreadyExist(String name, String email) {
        SQLiteDatabase db = this.getReadableDatabase(KEY_ENCRYPTION);

        String selection = COLUMN_NAME + " = ? AND " + COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {name, email};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        boolean result = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }

    public static void changeDBPassword(String newPassword, Context context) {

        SQLiteDatabase.loadLibs(context);
        //Log.i("Database123", "loaded libs");


        String databasePath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        //Log.i("Database123", "got the db path: " + databasePath);


        SQLiteDatabase db = SQLiteDatabase.openDatabase(databasePath, KEY_ENCRYPTION, null, SQLiteDatabase.OPEN_READWRITE);
        Log.w("32890457", "db opened with the old key at path: " + databasePath);


        db.rawExecSQL("PRAGMA rekey = '" + newPassword + "'");
        //Log.i("Database123", "new key set");


        db.close();


        Toast.makeText(context, "Database password changed successfully!", Toast.LENGTH_SHORT).show();
    }


    public static void exportDB(Context context, String selectedFilePath) {
        try {
            String currentDBPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
            File currentDB = new File(currentDBPath);
            File exportDirecotry = new File(selectedFilePath);

            Log.i("32890457", "[EXPORT] Exporting to " + exportDirecotry);



            String backupDBPath = new File(exportDirecotry, "Password.db").getAbsolutePath();


            File backupDB = new File(backupDBPath);

            if (currentDB.exists()) {
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                fos.flush();
                fos.close();
                fis.close();

                Toast.makeText(context, "Database exported successfully to: " + backupDBPath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "No database found at: " + currentDBPath, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("32890457", "[EXPORT] Export failed: " + Objects.requireNonNull(e.getMessage()));
        }
    }

    public static void importDB(Context context, String path, String name, String inputPassword) {

        Log.i("32890457", "[IMPORT] selected file path:  " + path);
        Log.i("32890457", "[IMPORT] selected file name:  " + name);
        Log.i("32890457", "[IMPORT] selected full path:  " + path + "/" + name);
        Log.i("32890457", "[IMPORT] input password:      " + inputPassword);

        try (SQLiteDatabase ignored = SQLiteDatabase.openDatabase(path + "/" + name, inputPassword, null, SQLiteDatabase.OPEN_READWRITE)) {
            Log.i("32890457", "Password correct, database opened successfully.");

            // Ottieni il percorso del database corrente
            String currentDBPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
            File currentDB = new File(currentDBPath);

            // Copia il file del database importato nel percorso del database corrente
            boolean copySuccess = FileUtils.copyFile(new File(path, name), currentDB);

            if (copySuccess) {
                Toast.makeText(context, "Database imported successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to import database", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLiteException e) {
            Log.e("32890457", String.valueOf(R.string.incorrect_password_or_database_is_corrupt), e);
            Toast.makeText(context, R.string.incorrect_password_or_database_is_corrupt, Toast.LENGTH_SHORT).show();
        }
    }


    private static void deleteCurrentDB(Context context) {
        String currentDBPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        File currentDB = new File(currentDBPath);

        if (currentDB.exists()) {
            boolean deleted = context.deleteDatabase(DATABASE_NAME);

            if (deleted) {
                Toast.makeText(context, "Database deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete database", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Database not found", Toast.LENGTH_SHORT).show();
        }
    }

    private static class FileUtils {
        static boolean copyFile(File src, File dst) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try (InputStream in = Files.newInputStream(src.toPath()); OutputStream out = Files.newOutputStream(dst.toPath())) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    return true;
                } catch (IOException e) {
                    Log.e("32890457", "copyFile exception: " + e);
                    return false;
                }
            }
            return false;
        }
    }

}
