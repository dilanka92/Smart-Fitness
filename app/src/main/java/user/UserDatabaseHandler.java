package user;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "smartFitness";

    // Table names
    private static final String TABLE_USER = "user";

    // user Table Columns names
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AGE = "age";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                "    " + KEY_EMAIL + "    TEXT      PRIMARY KEY," +
                "    " + KEY_USER_NAME + "     TEXT (50)," +
                "    " + KEY_PASSWORD + " TEXT," +
                "    " + KEY_AGE + "      INTEGER," +
                "    " + KEY_HEIGHT + "   DECIMAL," +
                "    " + KEY_WEIGHT + "   DECIMAL" + ");";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_AGE, user.getAge());
        values.put(KEY_HEIGHT, user.getHeight());
        values.put(KEY_WEIGHT, user.getWeight());

        db.insert(TABLE_USER, null, values);
        db.close();
    }


}
