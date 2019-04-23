package mjt.shopwise;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static mjt.shopwise.StandardAppConstants.*;
import static mjt.shopwise.SQLKWORD.*;

/**
 * Main/Start Activity for ShopWise, displays the Main Menu
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MainActivity extends AppCompatActivity {

    private static final String THIS_ACTIVITY = "MainActivity";
    private static final String LOGTAG = "SW-MA";
    public static final String THISCLASS = "MainActivity";

    /*
    Define Database/Table objects.
     */
    public DBHelper db;
    private DBDAO dbdao;
    private DBShopMethods dbshopmethods;
    private DBAisleMethods dbaislemethods;
    private DBProductMethods dbproductmethods;
    private DBAppvaluesMethods dbappvaluesmethods;
    private DBProductUsageMethods dbproductusagemethods;
    private DBRuleMethods dbrulemethods;
    private DBShopListMethods dbshoplistmethods;
    private DBStorageMethods dbstoragemethods;

    /*
    Define the options ListView and the Adapter and Cursor
     */
    private ListView options_listview;
    AdapterMainActivityOptionsMenu options_adapter;
    private Cursor mocsr;

    /*
    Set row counts to 0
     */
    int shopcount = 0;
    int aislecount = 0;
    int productcount = 0;
    int productusagecount = 0;
    int shoplistcount = 0;
    int rulecount = 0;
    int appvaluecount = 0;
    int storagecount = 0;

    /*
    Set the resume state as normal
     */
    private static int resumestate = StandardAppConstants.RESUMSTATE_NORMAL;

    /*
    Note LogMsg will only log msg if a) devmode is true and b)
    if the relevant section is set to true.
    This is accomplished by changing the values in
    StandardAppConstants.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String methodname =
                new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Starting",
                this,
                methodname
        );
        super.onCreate(savedInstanceState);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "External Storage Permissions Check", this, methodname);
        /*
        To use the backup/restore feature for Android 23+ permissions have to
        be requested.
         */
        if(Build.VERSION.SDK_INT >= 23) {
            ExternalStoragePermissions.verifyStoragePermissions(this);
        }
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "DB Initialisations",this,methodname);
        db = new DBHelper(this,DBConstants.DATABASE_NAME,null,1);
        dbdao = new DBDAO(this);
        dbshopmethods = new DBShopMethods(this);
        dbaislemethods = new DBAisleMethods(this);
        dbproductmethods = new DBProductMethods(this);
        dbappvaluesmethods = new DBAppvaluesMethods(this);
        dbproductusagemethods = new DBProductUsageMethods(this);
        dbrulemethods = new DBRuleMethods(this);
        dbshoplistmethods = new DBShopListMethods(this);
        dbstoragemethods = new DBStorageMethods(this);

        //TODO Currently experimenting with Referential Integrity Check
        for (String s :DBCommonMethods.checkReferentialIntegrity(dbdao.getDb())) {
            Log.d("REFITEGRITYCHECK",s);
        }

        // Expand the database
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Expanding Database (if required)", this, methodname);
        db.expand(null, true);
        getDBCounts();

        // Setup Colour Coding
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Preparing Colour Coding", this, methodname);
        ActionColorCoding acc = ActionColorCoding.getInstance(this);
        ActionColorCoding.forceStoreColors(this);

        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Setting Layout", this, methodname);
        setContentView(R.layout.activity_main);


        /**
         * !!!! IMPORTANT  !!!!
         * DataBinding is only available if turned on the in build.gradle (Module: app) using :-
         *
         *     dataBinding {
         *         enabled = true
         *     }
         */
        DataBindingUtil.setContentView(this,R.layout.activity_main);

        // Prepare ListView for Main Options menu
        options_listview = this.findViewById(R.id.activity_main_OptionsMenu);

        // Ensure that the AppValues table has entries for the Rule periods
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Setting RULEPERIODS", this, methodname);
        String rp_appvalname = StandardAppConstants.RULEPERIODS_APPVALKEY;
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"DAYS",true,true,"");
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"WEEKS",true,true,"");
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"FORTNIGHTS",true,true,"");
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"MONTHS",true,true,"");
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"QUARTERS",true,true,"");
        dbappvaluesmethods.insertAppvalue(rp_appvalname,"YEARS",true,true,"");

        //Build the menu (do a second time to correct any missed insertions
        // due to deletes)
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Building Options Menu", this, methodname);
        buildMenu();
        buildMenu();

        //Notify if in Development Mode (loggin has been turned on)
        if (StandardAppConstants.DEVMODE) {
            Toast.makeText(this,getResources().getString(R.string.devmodeon),Toast.LENGTH_LONG).show();
            final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,100);
            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
        }
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Ending", this, methodname);
    }

    /**
     * Add the help option to the Activity's menu bar.
     * @param menu  The menu xml
     * @return  true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_help, menu);
        return true;
    }

    /**
     * Action the respective option when the menu is selected
     * @param menuitem  The menuitem that was selected
     * @return true to indicate actioned.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int menuitemid = menuitem.getItemId();
        switch (menuitemid) {
            case R.id.actionhelp:
                //new DisplayHelp(this,"ALt Title",R.array.help_main_activity,80,true,0xffff0000, 0xbbffffff,20f,16f,12);
                new DisplayHelp(this,
                        getResources().getString(
                                R.string.title_help_main_activity),
                        R.array.help_main_activity,
                        85,
                        true,
                        0xff0000ff,
                        0xbbffffff,
                        22f,
                        16f,
                        12
                );
                return true;
            default:
                break;
        }
        return  onOptionsItemSelected(menuitem);
    }

    /**************************************************************************
     * onResume do any processing upon resume of the activity
     * e.g. refresh any listviews etc.
     * RESUMESTATES would be set when starting another activity if that
     * activity could alter the contents to be displayed.
     * Should always set the resumestate to RESUMESTATE_NORMAL
     */
    @Override
    protected void onResume() {
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Invoked",this,methodname);
        super.onResume();

        getDBCounts();
        //Resume state handling (no special handling)
        switch (resumestate) {
            case StandardAppConstants.RESUMESTATE_ALT1:
                break;
            case StandardAppConstants.RESUMESTATE_ALT2:
                break;
            case StandardAppConstants.RESUMESTATE_ALT3:
                break;
            case StandardAppConstants.RESUMESTATE_ALT4:
                break;
            default:
                break;
        }
        buildMenu();
        resumestate = StandardAppConstants.RESUMSTATE_NORMAL;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Ending",this,methodname);
    }

    /**************************************************************************
     * onDestroy - do any clean up before the application is destroyed
     * e.g. close any open cursors and then close the database
     */
    @Override
    protected void onDestroy() {
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Starting",this,methodname);
        super.onDestroy();
        mocsr.close();
        db.close();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Ending",this,methodname);
    }

    /**************************************************************************
     * getDBCounts - get Database table counts. Used for display and logic for
     * for which Options are available.
     * e.g. Aisles Option should only be visible when Shops
     * exist etc.
     */
    void getDBCounts() {
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Starting",this,methodname);
        shopcount = dbshopmethods.getShopCount();
        aislecount = dbaislemethods.getAisleCount();
        productcount = dbproductmethods.getProductCount();
        productusagecount = dbproductusagemethods.getProductUsageCount();
        shoplistcount = dbshoplistmethods.getShopListCount();
        rulecount = dbrulemethods.getRuleCount();
        appvaluecount = dbappvaluesmethods.getAppvalueCount();
        storagecount = dbstoragemethods.getStorageCount();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Ending",this,methodname);
    }

    /**************************************************************************
     * buildMenu - Build the Main Activity Options Menu
     *              This menu is built dynamically, as a ListView, from rows
     *              in the Appvalues Table.
     *
     *                  Only rows that have menuoptions (or whatever is defined
     *                  in StandardAppConstants.MENUOPTIONS) in the NAME column.
     *
     *                  The TEXT column has the Text shown by the button.
     *
     *                  The settingssinfo column contains the notes for
     *                  the option.
     *
     *
     *              The respective database rows are generated, deleted or
     *              updated according to data in String Arrays menuoptions and
     *              menuoptions_info (defined as MainActivity Class Members
     *              above.)
     *
     *              This method will perform the database operations and then
     *              build the ListView thus catering for a relatively simple
     *              means of updating the menus as available features change.
     *
     */
    private void buildMenu() {
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Starting",this,methodname);

        // Set Menu Options Strings
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Setting Option String definitions", this, methodname);
        String include_shops = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + SHOPSAPPVALNAME + "') ";
        String include_products = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + PRODUCTSAPPVALNAME + "') ";
        String include_aisles = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + AISLESAPPVALNAME + "') ";
        String include_stock = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + STOCKAPPVALNAME + "') ";
        String include_order = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + ORDERAPPVALNAME + "') ";
        String include_checklist = "(" +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + CHECKLISTAPPVALNAME + "') ";
        String include_shopping = "( " +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + SHOPPINGAPPVALNAME + "') ";
        String include_rules = "( " +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + RULESAPPVALNAME + "') ";
        String include_tools = "( " +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + TOOLSAPPVALNAME + "') ";
        String include_storage = "( " +
                DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                " = '" + STORAGEAPPVALNAME + "') ";

        // Determine Options to display
        // If no data then show SHOPS, STORAGE and TOOLS
        // If 1 or more Shops then show AISLES
        // if 1 or more Storage locations then show PRODUCTS
        // if 1 or more Aisles and  1 or more products then show STOCK
        // if 1 or more Stock then show ORDER, CHECKLIST, SHOPPING and RULES
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Determening Options to use", this, methodname);
        String filter = "(" +
                include_shops +
                SQLOR + include_storage +
                SQLOR + include_tools;
        if (storagecount > 0 ) {
            filter = filter +
                    SQLOR + include_products;
        }
        if (shopcount > 0) {
            filter = filter +
                    SQLOR + include_aisles;
        }
        if (shopcount > 0 && productcount > 0 && storagecount > 0 && aislecount > 0) {
            filter = filter +
                    SQLOR + include_stock;
            if (productusagecount > 0) {
                filter = filter +
                        SQLOR + include_order +
                        SQLOR + include_checklist +
                        SQLOR + include_shopping +
                        SQLOR + include_rules;
            }
        }
        filter = filter + ")";

        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Extracting current APPVALUES Table\n\tfilter=" + filter,
                this, methodname);
        // Get a cursor with the relevant rows according to the filter
        Cursor csr =  dbdao.getTableRows(
                DBAppvaluesTableConstants.APPVALUES_TABLE,
                "",
                filter,
                ""
        );

        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Performing comparisons",
                this, methodname);
        SQLiteDatabase modb = DBHelper.getHelper(this).getWritableDatabase();
        //Phase 1 - determine if there are any rows that need to be added or
        // updated.
        //      i.e. loop through menuoptions array looking for rows that do
        //      not exist (add/insert new row required) or do not match (
        //      update required) and perform respective inserts or updates.
        boolean optionmatch;
        boolean notesmatch;
        boolean ordermatch;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < MAINACTIVITYOPTIONLIST.length; i++) {
            String msg = "Comparing DEFINED Option Named " + MAINACTIVITYOPTIONLIST[i].getMenuOptionName();
            // compare cursor values and array equivalents
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                    msg,
                    this, methodname);
            csr.moveToPosition(-1);     //position before first row
            optionmatch = false;
            notesmatch = false;
            ordermatch = false;
            while (csr.moveToNext()) {
                if (csr.getString(csr.getColumnIndex(
                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL
                )).equals(
                        MAINACTIVITYOPTIONLIST[i].getMenuOptionName()
                )) {
                    optionmatch = true;
                }

                if (csr.getString(csr.getColumnIndex(
                        DBAppvaluesTableConstants.APPVALUES_SETTINGSINFO_COL
                )).equals(
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionInfo()
                )) {
                    notesmatch = true;
                }
                if (csr.getInt(csr.getColumnIndex(
                        DBAppvaluesTableConstants.APPVALUES_INT_COL
                )) ==
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionOrder()
                ) {
                    ordermatch = true;
                }
            }
            // comparisons done so act accordingly
            // skip to next if both match or only the option mismatches
            // (latter cases means the notes are for another option))
            if ((optionmatch && notesmatch && ordermatch) || (!optionmatch && notesmatch &&ordermatch)) {
                msg = MAINACTIVITYOPTIONLIST[i].getMenuOptionName() + " Not Changed";
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                        msg,
                        this, methodname);
                continue;
            }
            ContentValues cv = new ContentValues();
            // insert required as no matches
            if ((!optionmatch)) {
                msg = "Inserting DEFINED Option " + MAINACTIVITYOPTIONLIST[i].getMenuOptionName();
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                        msg,
                        this, methodname);

                dbappvaluesmethods.insertStringAndIntAppvalue(
                        MENUOPTIONS,
                        MAINACTIVITYOPTIONLIST[i].getMenuOptionName(),
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionOrder(),
                        true,
                        false,
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionInfo());
            }
            // update required as only notes mismatch
            if(optionmatch) {
                cv.put(DBAppvaluesTableConstants.APPVALUES_SETTINGSINFO_COL,
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionInfo());
                cv.put(DBAppvaluesTableConstants.APPVALUES_INT_COL,
                        MAINACTIVITYOPTIONLIST[i].getmMenuOptionOrder());
                String whereargs[] = { MENUOPTIONS,
                        MAINACTIVITYOPTIONLIST[i].getMenuOptionName()
                };
                String whereclause = DBAppvaluesTableConstants.APPVALUES_NAME_COL +
                        " = ? AND " +
                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL +
                        " = ?";
                modb.update(
                        DBAppvaluesTableConstants.APPVALUES_TABLE,
                        cv,
                        whereclause,
                        whereargs);
            }
        }
        // Phase 2 - Determine if and then delete, any rows that have no matching
        //              array entry.
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Checking for deletion of redundant APPVALUES Table options",
                this, methodname);
        csr.moveToPosition(-1);
        while (csr.moveToNext()) {
            optionmatch = false;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < MAINACTIVITYOPTIONLIST.length; i++) {
                if (csr.getString(
                        csr.getColumnIndex(
                                DBAppvaluesTableConstants.APPVALUES_TEXT_COL
                        )).equals(
                        MAINACTIVITYOPTIONLIST[i].getMenuOptionName())
                ) {
                    optionmatch = true;
                }

            }
            if (!optionmatch) {
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                        "Deleting redundant Option " +
                                csr.getString(csr.getColumnIndex(
                                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL
                                )),
                        this, methodname);
                String sql = "DELETE FROM " +
                        DBAppvaluesTableConstants.APPVALUES_TABLE +
                        SQLWHERE +
                        DBAppvaluesTableConstants.APPVALUES_NAME_COL_FULL +
                        " = '" +
                        MENUOPTIONS +
                        "' AND " +
                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL +
                        " = '" +
                        csr.getString(
                                csr.getColumnIndex(
                                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL_FULL
                                )) +
                        "' " + SQLENDSTATEMENT;
                modb.execSQL(sql);
            } else {
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                        "Not Deleteing Option " +
                                csr.getString(csr.getColumnIndex(
                                        DBAppvaluesTableConstants.APPVALUES_TEXT_COL
                                )),
                        this, methodname);
            }
        }
        csr.close();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                "Building the Options ListView and setting the adapter.",
                this, methodname);
        // Phase 3 build the actual Listview that is displayed
        String order = DBAppvaluesTableConstants.APPVALUES_INT_COL_FULL + SQLORDERASCENDING;
        mocsr = dbdao.getTableRows(
                DBAppvaluesTableConstants.APPVALUES_TABLE,
                "",
                filter,
                order
        );
        String appvoption;
        String appvorder;
        while (mocsr.moveToNext()) {
            appvoption = mocsr.getString(mocsr.getColumnIndex(DBAppvaluesTableConstants.APPVALUES_TEXT_COL));
            appvorder = mocsr.getString(mocsr.getColumnIndex(DBAppvaluesTableConstants.APPVALUES_INT_COL));
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,
                    LOGTAG,
                    "Option=" + appvoption + " Order=" + appvorder,
                    THISCLASS,methodname);
        }
        options_adapter = new AdapterMainActivityOptionsMenu(
                this,
                mocsr,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
                ,this.getIntent()
        );
        options_listview.setAdapter(options_adapter);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Ending",this,methodname);

    }

    /**************************************************************************
     * actionButtonClick - Take the appropriate action when an action button is
     * touched/clicked
     * Curenttly the same activity is invoked however the position is passed
     *
     * @param view the view (TextView acting as Button)that was clicked
     */
    @SuppressWarnings("unused")
    public void actionButtonClick(View view) {
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Starting",this,methodname);
        Integer tag = (Integer) view.getTag();
        Intent intent;
        switch (tag) {
            case 0:
                intent = new Intent(this,ShopsActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.SHOPSAPPVALORDER);
                break;
            case 1:
                intent = new Intent(this,AislesActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.AISLESAPPVALORDER);
                break;
            case 2:
                intent = new Intent(this,StorageActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.STORAGEAPPVALORDER);
                break;
            case 3:
                intent = new Intent(this,ProductsActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.PRODUCTSAPPVALORDER);
                break;
            case 4:
                intent = new Intent(this,StockListActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.STOCKAPPVALORDER);
                break;
            case 5:
                intent = new Intent(this,OrderActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.ORDERAPPVALORDER);
                break;
            case 6:
                intent = new Intent(this,CheckListActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.CHECKLISTAPPVALORDER);
                break;
            case 7:
                intent = new Intent(this,ShoppingActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.SHOPPINGAPPVALORDER);
                break;
            case 8:
                intent = new Intent(this,RulesActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.RULESAPPVALORDER);
                break;
            case 9:
                intent = new Intent(this,ToolsActivity.class);
                intent.putExtra(
                        StandardAppConstants.INTENTKEY_MENUCOLORCODE,
                        StandardAppConstants.TOOLSAPPVALORDER);
                break;
            default:
                intent = new Intent(this, TestAction.class);
        }
        intent.putExtra(INTENTKEY_CALLINGACTIVITY,THIS_ACTIVITY);
        intent.putExtra(INTENTKEY_CALLINGMODE,0);
        startActivity(intent);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,"Ending",this,methodname);
    }
}

