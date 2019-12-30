package mjt.shopwise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static mjt.shopwise.SQLKWORD.*;

/**
 * StockAddActivity - Stock Aisles with products
 *
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "CanBeFinal", "unused"})
public class StockAddActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String THIS_ACTIVITY = "StockAddActivity";
    private static final String LOGTAG = "SW_StockA";
    @SuppressWarnings("unused")
    private static String caller;
    private static int calledmode;
    private int resumestate = StandardAppConstants.RESUMSTATE_NORMAL;
    private Activity thisactivity;
    private static int internalmode = StandardAppConstants.CM_CLEAR;
    private static final int EDITMODE = 1;
    private static final int ADDMODE = 0;
    private boolean editdisplayed = false;
    public static final String THISCLASS = StockAddActivity.class.getSimpleName();


    //DBDAO dbdao;
    DBShopMethods dbshopmethods;
    DBAisleMethods dbaislemethods;
    DBProductMethods dbproductmethods;
    DBProductUsageMethods dbpumethods;
    DBShopListMethods dbshoplistmethods;
    DBRuleMethods dbrulemethods;

    Cursor slcsr;
    Cursor alcsr;
    Cursor plcsr;
    Cursor stockedcursor;

    private static final String SHOPID_COLUMN = DBShopsTableConstants.SHOPS_ID_COL;
    private static final String SHOPNAME_COLUMN = DBShopsTableConstants.SHOPS_NAME_COL;
    private static final String SHOPCITY_COLUMN = DBShopsTableConstants.SHOPS_CITY_COL;
    @SuppressWarnings("unused")
    private static final String SHOPORDER_COLUMN = DBShopsTableConstants.SHOPS_ORDER_COL;
    @SuppressWarnings("unused")
    private static final String SHOPID_FULLCOLUMN = DBShopsTableConstants.SHOPS_ID_COL_FULL;
    private static final String SHOPNAME_FULLCOLUMN = DBShopsTableConstants.SHOPS_NAME_COL_FULL;
    @SuppressWarnings("unused")
    private static final String SHOPCITY_FULLCOLUMN = DBShopsTableConstants.SHOPS_CITY_COL_FULL;
    @SuppressWarnings("unused")
    private static final String SHOPORDER_FULLCOLUMN = DBShopsTableConstants.SHOPS_ORDER_COL_FULL;

    private static final String AISLEID_COLUMN = DBAislesTableConstants.AISLES_ID_COL;
    @SuppressWarnings("unused")
    private static final String AILSEID_FULLCOLUMN = DBAislesTableConstants.AISLES_ID_COL_FULL;
    private static final String AISLENAME_COLUMN = DBAislesTableConstants.AISLES_NAME_COL;
    @SuppressWarnings("unused")
    private static final String AISLENAME_FULLCOLUMN = DBAislesTableConstants.AISLES_NAME_COL_FULL;
    @SuppressWarnings("unused")
    private static final String AISLEORDER_COLUMN = DBAislesTableConstants.AISLES_ORDER_COL;
    @SuppressWarnings("unused")
    private static final String AISLEORDER_FULLCOLUMN = DBAislesTableConstants.AISLES_ORDER_COL_FULL;
    private static final String AISLESHOPREF_COLUMN = DBAislesTableConstants.AISLES_SHOPREF_COL;
    private static final String AISLESHOPREF_FULLCOLUMN = DBAislesTableConstants.AISLES_SHOPREF_COL_FULL;

    private static final String PRODUCTID_COLUMN = DBProductsTableConstants.PRODUCTS_ID_COL;
    private static final String PRODUCTNAME_COLUMN = DBProductsTableConstants.PRODUCTS_NAME_COL;
    @SuppressWarnings("unused")
    private static final String PRODUCTID_FULLCOLUMN = DBProductsTableConstants.PRODUCTS_ID_COL_FULL;
    private static final String PRODUCTNAME_FULLCOLUMN = DBProductsTableConstants.PRODUCTS_NAME_COL_FULL;
    @SuppressWarnings("unused")
    private static final String PRODUCTSTORAGEREF_COLUMN = DBProductsTableConstants.PRODUCTS_STORAGEREF_COL;
    @SuppressWarnings("unused")
    private static final String PRODUCTSTORAGEREF_FULLCOLUMN = DBProductsTableConstants.PRODUCTS_STORAGEREF_COL_FULL;
    @SuppressWarnings("unused")
    private static final String PRODUCTSTORAGEORDER_COLUMN = DBProductsTableConstants.PRODUCTS_STORAGEORDER_COL;
    @SuppressWarnings("unused")
    private static final String PODUCTSTORAGEORDER_FULLCOLUMN = DBProductsTableConstants.PRODUCTS_STORAGEORDER_COL_FULL;

    @SuppressWarnings("unused")
    private static final String STORAGEID_COLUMN = DBStorageTableConstants.STORAGE_ID_COL;
    @SuppressWarnings("unused")
    private static final String STORAGEID_FULLCOLUMN = DBStorageTableConstants.STORAGE_ID_COL_FULL;
    @SuppressWarnings("unused")
    private static final String STORAGEID_ALTCOLUMN = DBStorageTableConstants.STORAGE_ALTID_COL;
    @SuppressWarnings("unused")
    private static final String STORAGEID_FULLALTCOLUMN = DBStorageTableConstants.STORAGE_ALTID_COL_FULL;
    @SuppressWarnings("unused")
    private static final String STORAGENAME_COLUMN = DBStorageTableConstants.STORAGE_NAME_COL;
    @SuppressWarnings("unused")
    private static final String STORAGENAME_FULLCOLUMN = DBStorageTableConstants.STORAGE_NAME_COL_FULL;
    @SuppressWarnings("unused")
    private static final String STORAGEORDER_COLUMN = DBStorageTableConstants.STORAGE_ORDER_COL;
    @SuppressWarnings("unused")
    private static final String STORAGEORDER_FULLCOLUMN = DBStorageTableConstants.STORAGE_ORDER_COL_FULL;

    private static final String PRODUCTREF_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL;
    @SuppressWarnings("unused")
    private static final String PRODUCTREF_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL;
    private static final String AISLEREF_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL;
    private static final String AISLEREF_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL;
    private static final String COST_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_COST_COL;
    private static final String COST_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_COST_FULL;
    @SuppressWarnings("unused")
    private static final String BUYCOUNT_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL;
    @SuppressWarnings("unused")
    private static final String BUYCOUNT_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL_FULL;
    @SuppressWarnings("unused")
    private static final String FIRSTBUY_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL;
    @SuppressWarnings("unused")
    private static final String FIRSTBUY_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL_FULL;
    @SuppressWarnings("unused")
    private static final String LASTBUY_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL;
    @SuppressWarnings("unused")
    private static final String LASTBUY_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL_FULL;
    private static final String PRODUCTUSAGEORDER_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL;
    private static final String PRODUCTUSAGEORDER_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL_FULL;
    private static final String CHECKLISTFLAG_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL;
    @SuppressWarnings("unused")
    private static final String CHECKLISTFLAG_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL_FULL;
    private static final String CHECKLISTCOUNT_COLUMN = DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL;
    @SuppressWarnings("unused")
    private static final String CHECKLISTCOUNT_FULLCOLUMN = DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL_FULL;

    /**
     * Sorting Productlist columns
     */
    private static final int BYPRODUCT = 0;
    private static final int BYCOST = 1;
    private static final int BYORDER = 2;
    private static final String SORTASCENDING = SQLORDERASCENDING;
    private static final String SORTDESCENDING = SQLORDERDESCENDING;
    static String orderby = PRODUCTNAME_FULLCOLUMN + SORTASCENDING;
    static int orderfld = BYPRODUCT;
    static boolean ordertype = true;
    static boolean sortchanged = false;
    static String lastmessage = "";
    static String currentproductname = "";
    static String productfilter = "";
    static String productorderby = PRODUCTNAME_FULLCOLUMN;
    static String shopfilter = "";
    static String shoporderby = SHOPNAME_FULLCOLUMN;
    static String aislefilter = "";
    static String aisleorderby = AISLENAME_COLUMN;
    static String stockfilter = "";
    static String stockorderby = PRODUCTNAME_FULLCOLUMN;
    @SuppressWarnings("unused")
    static int shopcount = 0;
    @SuppressWarnings("unused")
    static int aislecount = 0;
    @SuppressWarnings("unused")
    static int productcount = 0;

    Context context;
    ActionBar actionbar;

    /**
     * Colours
     */
    private static int h1;
    private static int h2;
    @SuppressWarnings("unused")
    private static int h3;
    @SuppressWarnings("unused")
    private static int h4;
    private static int primary_color;
    private String menucolorcode;
    @SuppressWarnings("unused")
    int passedmenucolorcode;

    @SuppressWarnings("unused")
    AdapterShopList shoplistadapter;
    AdapterAisleList aislelistadapter;
    AdapterProductList productlistadapter;
    AdapterStockList stocklistadapter;

    TextView messagebar;
    @SuppressWarnings("unused")
    LinearLayout selectshop_linearlayout;
    TextView shoplabel;
    Spinner selectshop;
    @SuppressWarnings("unused")
    LinearLayout selectaisle_linearlayout;
    TextView aislelabel;
    Spinner selectaisle;
    @SuppressWarnings("unused")
    LinearLayout productfilter_linearlayout;
    TextView productfilterlabel;
    EditText inputproductfilter;
    @SuppressWarnings("unused")
    LinearLayout selectproduct_linearlayout;
    TextView productlabel;
    Spinner selectproduct;
    TextView donebutton;
    TextView savebutton;
    LinearLayout stocklistheading_linearlayout;
    ListView stocklist;
    EditText inputstockcost;
    TextView inputstockcostlabel;
    EditText inputstockorder;
    TextView inputstockorderlabel;
    CheckBox inputchecklistflag;
    TextView inputchecklistflaglabel;
    EditText inputchecklistcount;
    TextView inputchecklistcountlabel;
    TextView sortable;
    TextView clickable;
    TextView longclickable;

    @SuppressWarnings("unused")
    String stocklabel;

    long currentshopid = 0;
    long currentaisleid = 0;
    long currentproductid = 0;

    protected void onCreate(Bundle savedInstanceState) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        context = this;
        thisactivity = (Activity) context;
        logmsg = "Retrieving Intent Extras";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        caller = getIntent().getStringExtra(
                StandardAppConstants.INTENTKEY_CALLINGACTIVITY);
        calledmode = getIntent().getIntExtra(
                StandardAppConstants.INTENTKEY_CALLINGMODE,0);

        logmsg = "Preparing ColorCoding";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        menucolorcode = StandardAppConstants.INTENTKEY_MENUCOLORCODE;
        passedmenucolorcode = getIntent().getIntExtra(menucolorcode,0);
        productfilter = "";
        stockfilter = "";

        // Get the UI views from their resource id's
        messagebar = findViewById(R.id.stock_messagebar);
        donebutton = findViewById(R.id.stock_donebutton);
        savebutton = findViewById(R.id.stock_savebutton);
        selectshop_linearlayout = findViewById(
                R.id.inputstockshop_linearlayout);
        shoplabel = findViewById(R.id.inputstockshop_label);
        selectshop = findViewById(R.id.selectstockshop);
        selectaisle_linearlayout = findViewById(
                R.id.inputstockaisle_linearlayout);
        aislelabel = findViewById(R.id.inputstockaisle_label);
        selectaisle = findViewById(R.id.selectstockaisle);
        selectproduct_linearlayout = findViewById(
                R.id.inputstockproduct_linearlayout);
        productlabel = findViewById(R.id.inputproductname_label);
        selectproduct = findViewById(R.id.selectstockproduct);
        productfilter_linearlayout = findViewById(
                R.id.stock_productfilter_linearlayout);
        productfilterlabel = findViewById(R.id.products_productfilterlabel);
        inputproductfilter = findViewById(R.id.products_inputfilter);
        productlabel = findViewById(R.id.inputstockproduct_label);
        stocklabel = getResources().getString(R.string.stocklabel);
        stocklistheading_linearlayout = findViewById(R.id.stock_stocklist_heading);
        inputstockcost = findViewById(R.id.inputstockcost);
        inputstockcostlabel = findViewById(R.id.inputstockcost_label);
        inputstockorder = findViewById(R.id.inputstockorder);
        inputstockorderlabel = findViewById(R.id.inputstockorder_label);
        inputchecklistflag = findViewById(R.id.inputchecklistflag);
        inputchecklistflaglabel = findViewById(R.id.inputchecklistflag_label);
        inputchecklistcount = findViewById(R.id.inputchecklistcount);
        inputchecklistcountlabel = findViewById(R.id.inputchecklistcount_label);
        stocklist = findViewById(R.id.stock_stocklist);
        sortable = findViewById(R.id.sortable);
        clickable = findViewById(R.id.clickable);
        longclickable = findViewById(R.id.longclickable);


        // Apply Color Coding
        actionbar = getSupportActionBar();
        //noinspection ConstantConditions
        actionbar.setTitle(this.getResources().getString(R.string.stockaddlabel));
        ActionColorCoding.setActionBarColor(this,getIntent(),actionbar);
        primary_color = ActionColorCoding.setHeadingColor(this,getIntent(),0);
        h1 = ActionColorCoding.setHeadingColor(this,getIntent(),1);
        h2 = ActionColorCoding.setHeadingColor(this,getIntent(),2);
        h3 = ActionColorCoding.setHeadingColor(this,getIntent(),3);
        h4 = ActionColorCoding.setHeadingColor(this,getIntent(),4);
        ActionColorCoding.setActionButtonColor(donebutton, primary_color);
        ActionColorCoding.setActionButtonColor(savebutton, primary_color);
        stocklistheading_linearlayout.setBackgroundColor(h1);
        ActionColorCoding.setActionButtonColor(selectshop,h2);
        ActionColorCoding.setActionButtonColor(selectaisle,h2);
        ActionColorCoding.setActionButtonColor(selectproduct,h2);
        ActionColorCoding.setActionButtonColor(inputproductfilter,
                h2 & ActionColorCoding.transparency_optional);
        ActionColorCoding.setActionButtonColor(inputstockcost,
                h2 & ActionColorCoding.transparency_required);
        ActionColorCoding.setActionButtonColor(inputstockorder,
                h2 & ActionColorCoding.transparency_required);
        ActionColorCoding.setActionButtonColor(inputchecklistcount,
                h2 & ActionColorCoding.transparency_optional);
        ActionColorCoding.setActionButtonColor(inputchecklistflag,
                h2 & ActionColorCoding.transparency_optional);
        ActionColorCoding.setCheckBoxAccent(this,getIntent(),inputchecklistflag);
        shoplabel.setTextColor(primary_color);
        aislelabel.setTextColor(primary_color);
        productfilterlabel.setTextColor(h2);
        productlabel.setTextColor(primary_color);
        inputstockcostlabel.setTextColor(primary_color);
        inputstockorderlabel.setTextColor(primary_color);
        inputchecklistflaglabel.setTextColor(primary_color);
        inputchecklistcountlabel.setTextColor(primary_color);
        sortable.setTextColor(primary_color);
        clickable.setTextColor(primary_color);
        longclickable.setTextColor(primary_color);

        ActionColorCoding.setSwatches(findViewById(android.R.id.content), this.getIntent());

        // Perpare to use the database and the underlying methods
        logmsg = "Preparing Database";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        //dbdao = new DBDAO(this);
        dbshopmethods = new DBShopMethods(this);
        dbaislemethods = new DBAisleMethods(this);
        dbproductmethods = new DBProductMethods(this);
        dbpumethods = new DBProductUsageMethods(this);
        dbshoplistmethods = new DBShopListMethods(this);
        dbrulemethods = new DBRuleMethods(this);

        // Get Intent extras according to called mode
        // i.e. :-
        //      if called from shops then we only know the shop
        //      if called from aisles then we know shop and aisle
        //      if called from products then we only know product
        //
        //      if called from shop or aisle then we can set the
        //      aisle filter so that only aisles relevant to the shop
        //      are selected.
        logmsg = "Retrieving IntentExtras (conditional upon mode)";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        switch (calledmode) {
            case StandardAppConstants.CM_STOCKFROMSSHOP:
                currentshopid = getIntent().getLongExtra(StandardAppConstants.INTENTKEY_SHOPID,0);
                aislefilter = AISLESHOPREF_FULLCOLUMN + " = " + currentshopid;
                logmsg = "Called from ShopID=" + currentshopid +
                        " Aisle Filter=" + aislefilter;
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
                actionbar.setTitle(actionbar.getTitle().toString() + " - (from Shops)");
                break;
            case StandardAppConstants.CM_STOCKFROMAISLE:
                currentaisleid = getIntent().getLongExtra(StandardAppConstants.INTENTKEY_AISLEID,0);
                currentshopid = getIntent().getLongExtra(StandardAppConstants.INTENTKEY_AISLESHOPREF,0);
                aislefilter = AISLESHOPREF_FULLCOLUMN + " = " + currentshopid;
                logmsg = "Called from AisleID=" + currentaisleid +
                        " with parent ShopID=" + currentshopid +
                        "Aisle Filter=" + aislefilter;
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
                actionbar.setTitle(actionbar.getTitle().toString() + " - (from Aisles)");
                break;
            case StandardAppConstants.CM_STOCKFROMPRODUCT:
                currentproductid = getIntent().getLongExtra(StandardAppConstants.INTENTKEY_PRODUCTID,0);
                slcsr = dbshopmethods.getShopsWithAisles(shopfilter,shoporderby);
                slcsr.moveToFirst();
                currentshopid = slcsr.getLong(slcsr.getColumnIndex(SHOPID_COLUMN));
                aislefilter = AISLESHOPREF_FULLCOLUMN + " = " + currentshopid;
                alcsr = dbaislemethods.getAisles(aislefilter,aisleorderby, false);
                alcsr.moveToFirst();
                currentaisleid = alcsr.getLong(alcsr.getColumnIndex(AISLEID_COLUMN));
                logmsg = "Called from ProductID=" + currentproductid +
                        " ShopID set to=" + currentshopid +
                        " AisledID set to=" + currentaisleid +
                        " Aisle Filter=" + aislefilter;
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
                actionbar.setTitle(actionbar.getTitle().toString() + " - (from Products)");
                break;
            default:
                break;
        }

        // Extract the  data for the spinners from the DB
        logmsg = "Retrieving Shop, Aisle and Product Data for Selection Spinners";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        slcsr = dbshopmethods.getShopsWithAisles(shopfilter,shoporderby);
        alcsr = dbaislemethods.getAisles(aislefilter,aisleorderby, false);
        plcsr = dbproductmethods.getProductsNotInAisle(currentaisleid,productfilter,productorderby);

        shoplistadapter = setupShopSelectSpinner(currentshopid);
        aislelistadapter = setupAisleSelectSpinner(currentaisleid);
        productlistadapter = setupProductSelectSpinner(currentproductid);

        logmsg = "Adding Product Filter Listener";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        addProductFilterListener();

        //  Prepare the list of productusages (stock) for the current aisle
        //  Note currentaisleid will be either exist (when called from aisles) or
        //  set when the Shop's spinner is populated
        logmsg = "Retrieving List of Products already Stocked in the current Aisle";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        stockfilter = AISLEREF_FULLCOLUMN + " = " + currentaisleid;
        stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,stockorderby);
        stocklistadapter = new AdapterStockList(this,stockedcursor,0,this.getIntent(),false, false, false);
        stocklist.setAdapter(stocklistadapter);

        setNewInput(inputstockorder, inputstockcost, inputchecklistcount);


        // Add StockList clikitem and longclickitem listeners
        logmsg = "Adding StockList OnItemClick Listener";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        stocklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {
                listItemClick(view, position, id);
            }
        });
        logmsg = "Adding StockList OnItemLongClick Listener";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        stocklist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           View view, int position, long id) {
                listItemLongClick(view, position, id);
                return true;
            }
        });

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
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        super.onResume();
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
                messagebar.setVisibility(View.INVISIBLE);
                break;
        }
        resumestate = StandardAppConstants.RESUMSTATE_NORMAL;
        stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,stockorderby);
        stocklistadapter.swapCursor(stockedcursor);
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
        if (menuitemid == R.id.actionhelp) {//new DisplayHelp(this,"ALt Title",R.array.help_main_activity,80,true,0xffff0000, 0xbbffffff,20f,16f,12);
            new DisplayHelp(this,
                    getResources().getString(
                            R.string.title_help_stockadd_activity),
                    R.array.help_stockadd_activty,
                    85,
                    true,
                    primary_color,
                    0xbbffffff,
                    22f,
                    16f,
                    12
            );
            return true;
        }
        return  onOptionsItemSelected(menuitem);
    }

    /**************************************************************************
     * onDestroy - do any clean up before th application is destroyed
     * e.g. close any open cursors and then close the database
     */
    @Override
    protected void onDestroy() {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        super.onDestroy();
        slcsr.close();
        alcsr.close();
        plcsr.close();
        stockedcursor.close();
    }

    /**************************************************************************
     * Button Click Handler
     *
     * @param view The view (i.e the TextView that was clicked)
     */
    @SuppressWarnings("unused")
    public void actionButtonClick(View view) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        switch (view.getId()) {
            case R.id.stock_donebutton:
                logmsg = "Finishing";
                LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
                this.finish();
                break;
            case R.id.stock_savebutton:
                stockProductinAisle((internalmode == EDITMODE));
                break;
            default:
                break;
        }
    }

    /**************************************************************************
     *
     * @param values    values passed getLong1 = Aisleref getLong2 = productref
     */
    public void stockEdit(@SuppressWarnings("SameParameterValue") RequestDialogParameters values) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        Intent intent = new Intent(this,StockEditActivity.class);
        intent.putExtra(StandardAppConstants.INTENTKEY_SHOPID,0);
        intent.putExtra(StandardAppConstants.INTENTKEY_PUPRODUCTREF,values.getLong2());
        intent.putExtra(StandardAppConstants.INTENTKEY_PUAISLEREF,values.getLong1());
        intent.putExtra(menucolorcode,passedmenucolorcode);
        intent.putExtra(StandardAppConstants.INTENTKEY_CALLINGACTIVITY,THIS_ACTIVITY);
        intent.putExtra(StandardAppConstants.INTENTKEY_CALLINGMODE,calledmode);
        logmsg = "Starting " + StockEditActivity.class.getSimpleName() +
                " for ShopID=" + values.getLong3() +
                " AisleID=" + values.getLong1() +
                " ProductID=" + values.getLong2();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        startActivity(intent);

    }

    /**************************************************************************
     *
     * @param values    values passed getLong1 = Aisleref getLong2 = productref
     */
    public void stockDelete(@SuppressWarnings("SameParameterValue") RequestDialogParameters values) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        Activity activity = values.getPassedactivity();
        StockAddActivity sa = (StockAddActivity) activity;
        sa.dbpumethods.deleteStock(values.getLong1(),values.getLong2(),false);
        stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,stockorderby);
        stocklistadapter.swapCursor(stockedcursor);
        plcsr = dbproductmethods.getProductsNotInAisle(values.getLong1(),productfilter,productorderby);
        productlistadapter.swapCursor(plcsr);
        sa.setMessage(sa,"Stocked Product " +
                currentproductname +
                " Deleted from this ShopAisle.",
                false
        );
        internalmode = ADDMODE;
    }

    /**************************************************************************
     *
     */
    public void stockProductinAisle(boolean update) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        Emsg emsg;
        double cost;
        int order;
        int chklstcnt;

        //Product spinner must have Products (filter can result in none)
        if (plcsr.getCount() < 1) {
            setMessage(this,"Unable to Save. No Products to select. Filtered out?", true);
            return;
        }

        // Get the Product id from the database cursor according to position of
        // the current selection in the Product spinner/dropdown
        int spos = plcsr.getPosition();
        int npos = selectproduct.getSelectedItemPosition();

        plcsr.moveToPosition(npos);
        long productref = plcsr.getLong(plcsr.getColumnIndex(
                PRODUCTID_COLUMN
        ));
        plcsr.moveToPosition(spos);

        // Get the Aisle id from the database cursor according to position of
        // the current selection in the Aisle spinner/dropdown
        // Note! Shop is implied via the Aisle
        spos = alcsr.getPosition();
        npos = selectaisle.getSelectedItemPosition();
        if (alcsr.getCount() < 1) {
            setMessage(this,"Unable to Save. No Aisles to select.",true);
        }
        alcsr.moveToPosition(npos);
        long aisleref = alcsr.getLong(alcsr.getColumnIndex(
                AISLEID_COLUMN
        ));
        alcsr.moveToPosition(spos);

        // Get and validate the input Cost/Price
        String costasstr = inputstockcost.getText().toString();
        emsg = ValidateInput.validateMonetary(costasstr);
        if (!emsg.getErrorIndicator()) {
            cost = Double.parseDouble(costasstr);
        } else {
            setMessage(this, emsg.getErrorMessage(),true);
            inputstockcost.requestFocus();
            logmsg = "Cannot STOCK product as Price is invalid error=" +
                    emsg.getErrorMessage();
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                    logmsg,THISCLASS,methodname);
            return;
        }

        // Get and validate the order of the product in the aisle
        String orderasstr = inputstockorder.getText().toString();
        emsg = ValidateInput.validateInteger(orderasstr);
        if (!emsg.getErrorIndicator()) {
            order = Integer.parseInt(orderasstr);
        } else {
            setMessage(this,emsg.getErrorMessage(),true);
            inputstockorder.requestFocus();
            logmsg = "Cannot STOCK product as Order is invalid error=" +
                    emsg.getErrorMessage();
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                    logmsg,THISCLASS,methodname);
            return;
        }

        // Get and validate the checklist count
        String chklstcntasstr = inputchecklistcount.getText().toString();
        if (chklstcntasstr.length() < 1) {
            chklstcntasstr = "1";
        }
        emsg = ValidateInput.validateInteger(chklstcntasstr);
        if (!emsg.getErrorIndicator()) {
            chklstcnt = Integer.parseInt(chklstcntasstr);
        } else {
            setMessage(this,emsg.getErrorMessage(),true);
            inputchecklistcount.requestFocus();
            logmsg = "Cannot STOCK product as Count is invalid error=" +
                    emsg.getErrorMessage();
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                    logmsg,THISCLASS,methodname);
            return;
        }

        // Reject duplicate (same aisle and product)
        if (dbpumethods.doesProductUsageExist(aisleref,productref) && (!update)) {
            setMessage(this,"Product already exists in this Aisle.",true);
            logmsg = "cannot STOCK product as it would try to create a duplicate" +
                    "\n\t i.e. Product-Aisle combination must be unqiue";
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
            return;
        }

        if (!update) {
            //Saved the productusage and refresh the stock list
            int addedcnt = 0;
            dbpumethods.insertProductUsage(
                    productref,aisleref,
                    cost,order,
                    inputchecklistflag.isChecked(),chklstcnt);
            logmsg = "AisleID=" + aisleref +
                    " ProductID=" + productref +
                    " ADD=" + dbpumethods.ifProductUsageAdded();
            if (dbpumethods.ifProductUsageAdded()) {
                setMessage(this,
                        "Product Added",
                        false);
            }
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,
                    logmsg,THISCLASS,methodname);
        } else {
            dbpumethods.modifyProductUsage(productref,
                    aisleref,
                    cost,
                    order,
                    inputchecklistflag.isChecked(),
                    chklstcnt);
            logmsg = "AisleID=" + aisleref +
                    " ProductID=" + productref + "" +
                    " EDIT=" + dbpumethods.ifProductUsageUpdated();
            if (dbpumethods.ifProductUsageUpdated()) {
                setMessage(this,"Product Updated",false);
            }
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
            internalmode = EDITMODE;
        }

        logmsg = "Refreshing ProductList and StockedList";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        plcsr = dbproductmethods.getProductsNotInAisle(aisleref,productfilter,productorderby);
        productlistadapter.swapCursor(plcsr);
        stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,stockorderby);
        stocklistadapter.swapCursor(stockedcursor);
        if (internalmode != EDITMODE) {
            setNewInput(inputstockorder, inputstockcost, inputchecklistcount);
        }
    }

    /**************************************************************************
     * sortClick - Handle list heading clicks (i.e. sort by that field)
     *
     * @param view the view that was clicked
     */
    @SuppressWarnings("unused")
    public void sortClick(View view) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        lastmessage = getResources().getString(R.string.stocklabel) +
                " in Shop/Aisle sorted by ";
        switch (view.getId()) {
            case R.id.stock_stocklist_heading_productname:
                getOrderBy(PRODUCTNAME_FULLCOLUMN,BYPRODUCT);
                lastmessage = lastmessage + " PRODUCT NAME (";
                break;
            case R.id.stock_stocklist_heading_productcost:
                getOrderBy(COST_FULLCOLUMN,BYCOST);
                lastmessage = lastmessage + "  COST/PRICE (";
                break;
            case R.id.stock_stocklist_heading_productorder:
                getOrderBy(PRODUCTUSAGEORDER_FULLCOLUMN,BYORDER);
                lastmessage = lastmessage + " ORDER (";
            default:
                break;
        }
        if (sortchanged) {
            stockorderby = orderby;
            stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,stockorderby);
            stocklistadapter.swapCursor(stockedcursor);
            if (ordertype) {
                lastmessage = lastmessage + "ascending)";
            } else {
                lastmessage = lastmessage + "descending)";
            }
            setMessage(this,lastmessage,false);
        }
    }

    /**************************************************************************
     *  addProductFilterListener - Add a Listener for whenever anything is
     *                              typed in the filter field. This is then
     *                              used to filter the products that are
     *                              available in the product selection spinner
     */
    public void addProductFilterListener() {

        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        inputproductfilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                productfilter = DBProductsTableConstants.PRODUCTS_NAME_COL_FULL +
                        SQLLIKECHARSTART +
                        inputproductfilter.getText().toString() +
                        SQLLIKECHAREND;
                plcsr = dbproductmethods.getProductsNotInAisle(currentaisleid, productfilter, productorderby);
                productlistadapter.swapCursor(plcsr);
            }
        });
    }

    /**************************************************************************
     *  listItemClick - Handle clicking an entry (item) in the list of products
     *                  currently assigned/stocked in the current shop/aisle
     *                  combination.
     *
     * @param view      The View that was clicked
     * @param position  The Position within the ListView
     * @param id        The id column of the clicked item
     */
    public void listItemClick(View view, int position, long id) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        long aisleid = stockedcursor.getLong(
                stockedcursor.getColumnIndex(AISLEREF_COLUMN)
        );
        String aislename = stockedcursor.getString(
                stockedcursor.getColumnIndex(AISLENAME_COLUMN)
        );
        long productid = stockedcursor.getLong(
                stockedcursor.getColumnIndex(PRODUCTREF_COLUMN)
        );
        String productname = stockedcursor.getString(
                stockedcursor.getColumnIndex(PRODUCTNAME_COLUMN)
        );
        Class cls = null;
        String classname = this.getClass().getCanonicalName();
        String positivebuttontext = getResources().getString(R.string.editbutton);
        String positiveaction = "stockEdit";
        String negativebuttontext = "";
        String negativeaction = "";
        String neutralbuttontext = getResources().getString(R.string.cancelbutton);
        String neutralaction = "";
        String title = "Edit Product " + productname + " in Aisle " + aislename;
        String message = getResources().getString(R.string.availableoptions) +
                getResources().getString(R.string.cancelbutton) +
                getResources().getString(R.string.and) +
                getResources().getString(R.string.editbutton) +
                getResources().getString(R.string.doublenewline) +
                getResources().getString(R.string.tab1) +
                getResources().getString(R.string.cancelbutton) +
                getResources().getString(R.string.canceldoes) +
                getResources().getString(R.string.doublenewline) +
                getResources().getString(R.string.tab1) +
                getResources().getString(R.string.editbutton) +
                " populates the Cost, Order and the  CheckList checkbox and count inputs." +
                getResources().getString(R.string.newline) +
                getResources().getString(R.string.tab1) +
                " You can then makes changes to these values and then " +
                "Click SAVE to save the changes to the stocked product.";
        MixTripleLongTripleInt values = new MixTripleLongTripleInt();
        values.setMIXTRPPLONGINT(aisleid,productid,0,0,0,0);
        logmsg = "Presenting RequestDialog for Product=" + productname +
                " Aisle=" + aislename;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        new RequestDialog().requestDialog(thisactivity,
                classname,
                title,
                message,
                positivebuttontext,negativebuttontext,neutralbuttontext,
                positiveaction,negativeaction,neutralaction,
                values
        );
    }

    /**************************************************************************
     *
     * @param view          The View that was LongClicked
     * @param position      the position in the ListView of the item
     * @param id            The id column of the longclicked item
     */
    public void listItemLongClick(View view, int position, long id) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);

        long aisleid = stockedcursor.getLong(
                stockedcursor.getColumnIndex(AISLEREF_COLUMN)
        );
        String aislename = stockedcursor.getString(
                stockedcursor.getColumnIndex(SHOPNAME_COLUMN)
        );
        long productid = stockedcursor.getLong(
                stockedcursor.getColumnIndex(PRODUCTREF_COLUMN)
        );
        String productname = stockedcursor.getString(
                stockedcursor.getColumnIndex(PRODUCTNAME_COLUMN)
        );
        Class cls = null;
        String classname = this.getClass().getCanonicalName();
        String positivebuttontext = getResources().getString(R.string.deletebutton);
        String positiveaction = "stockDelete";
        String negativebuttontext = "";
        String negativeaction = "";
        String neutralbuttontext = getResources().getString(R.string.cancelbutton);
        String neutralaction = "";
        String title = "Delete " + productname + " in Aisle " + aislename;
        String message = "Available Options are CANCEL and DELETE.\n\n" +
                "\tCANCEL returns to  STOCK, doing nothing.\n" +
                "\tDELETE allows you to delete the stocked item.\n\n";

        ArrayList<String> impact = dbpumethods.stockDeleteImapct(aisleid,productid);
        impact.addAll(dbshoplistmethods.shopListEntryDeleteImpact(aisleid,productid));
        impact.addAll(dbrulemethods.ruleDeleteImpact(aisleid,productid));
        StringBuilder sb = new StringBuilder();
        for (String msg: impact) {
            sb.append(msg).append("\n");
        }
        message = message + sb.toString();

        MixTripleLongTripleInt values = new MixTripleLongTripleInt();
        values.setMIXTRPPLONGINT(aisleid,productid,0,0,0,0);
        logmsg = "Presenting RequestDialog for Product=" + productname +
                " Aisle=" + aislename;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        new RequestDialog().requestDialog(thisactivity,
                classname,
                title,
                message,
                positivebuttontext,negativebuttontext,neutralbuttontext,
                positiveaction,negativeaction,neutralaction,
                values
        );

    }

    /**************************************************************************
     *  Setup the Shop Spinner
     *
     *  Note, if the shop is changed then the available aisle will change
     *  so the cursor needs to be rebuilt, then the adapter needs to be rebuilt
     *  and then finally the spinner needs to be set to use the new adapter.
     *
     *  Note to avoid initial onItemSelected event (system generated rather than
     *      being due to user input) the listener is added via a new thread
     *      initiated by the spinner's post
     *
     * @param id        The id of the shop
     * @return          The AdapterShopList that was setup
     */
    public AdapterShopList setupShopSelectSpinner(long id) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        AdapterShopList rv = new AdapterShopList(
                this,slcsr,0,this.getIntent(),true, false, false);
        selectshop.setAdapter(rv);
        SpinnerMove.moveToColumn(selectshop,id,slcsr,SHOPID_COLUMN,true);
        logmsg = "Shop Selection Spinner setup and positioned to Shop=" +
                slcsr.getString(slcsr.getColumnIndex(SHOPNAME_COLUMN)) +
                " ID=" + id +
                " City=" + slcsr.getString(slcsr.getColumnIndex(SHOPCITY_COLUMN));
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        selectshop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long rowid) {
                currentshopid = rowid;
                aislefilter = AISLESHOPREF_FULLCOLUMN + " = " + currentshopid;
                alcsr = dbaislemethods.getAisles(aislefilter,aisleorderby, false);
                aislelistadapter.swapCursor(alcsr);
                if (internalmode == EDITMODE) {
                    inputstockorder.setText(DBConstants.DEFAULTORDER);
                    inputstockcost.setText("0");
                    inputchecklistcount.setText("1");
                    inputchecklistflag.setChecked(false);
                    editdisplayed = false;
                } else {
                    setNewInput(inputstockorder, inputstockcost, inputchecklistcount);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return rv;
    }


    /**************************************************************************
     *
     * @param id    The id (Aisleref) of the Aisle to be selected
     * @return      The AdapterAisleList that was setup
     */
    public AdapterAisleList setupAisleSelectSpinner(long id) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        AdapterAisleList rv = new AdapterAisleList(
                this,alcsr,0,this.getIntent(),true, false, false);
        selectaisle.setAdapter(rv);
        if (id > 0 ) {
            SpinnerMove.moveToColumn(selectaisle,id,alcsr,AISLEID_COLUMN);
            long parentshopid = alcsr.getLong(
                    alcsr.getColumnIndex(AISLESHOPREF_COLUMN));
            SpinnerMove.moveToColumn(selectshop,parentshopid,slcsr,SHOPID_COLUMN,true);
            currentshopid = parentshopid;
            logmsg = "Aisle Selection Spinner setup and positioned to Aisle=" +
                    alcsr.getString(alcsr.getColumnIndex(AISLENAME_COLUMN)) +
                    " ID=" + id;
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        } else {
            logmsg = "No existing Aisle prodived for positioning (i.e. Aisle ID=0)" +
            " and Arbritary Aisle will be selected.";
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        }

        selectaisle.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view,
                                       int position, long rowid) {
                currentaisleid = rowid;
                stockfilter = AISLEREF_FULLCOLUMN + " = " + rowid;
                stockedcursor = dbpumethods.getExpandedProductUsages(stockfilter,
                        stockorderby);
                stocklistadapter.swapCursor(stockedcursor);
                plcsr = dbproductmethods.getProductsNotInAisle(currentaisleid,productfilter,productorderby);
                productlistadapter.swapCursor(plcsr);

                if (internalmode == EDITMODE) {
                    inputstockorder.setText(DBConstants.DEFAULTORDER);
                    inputstockcost.setText("0");
                    inputchecklistcount.setText("1");
                    inputchecklistflag.setChecked(false);
                    editdisplayed = false;
                } else {
                    setNewInput(inputstockorder, inputstockcost, inputchecklistcount);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return rv;
    }

    /**************************************************************************
     *
     * @param id    The id of the product that is to be selected
     * @return      the AdpaterProductList that was setup
     */
    public AdapterProductList setupProductSelectSpinner(long id) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);

        AdapterProductList rv = new AdapterProductList(this,
                plcsr,0,this.getIntent(), true, false, false);
        selectproduct.setAdapter(rv);
        SpinnerMove.moveToColumn(selectproduct,id,plcsr,PRODUCTID_COLUMN,true);
        //positionSpinnerFromID(selectproduct,
        //        id,plcsr,DBProductsTableConstants.PRODUCTS_ID_COL);
        logmsg = "Product Selection Spinner setup and positioned to Product=" +
                plcsr.getString(plcsr.getColumnIndex(PRODUCTNAME_COLUMN)) +
                " ID=" + id;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);

        selectproduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long rowid) {
                currentproductid = rowid;
                if (internalmode == EDITMODE && editdisplayed) {
                    inputstockorder.setText(DBConstants.DEFAULTORDER);
                    inputstockcost.setText("0");
                    inputchecklistcount.setText("1");
                    inputchecklistflag.setChecked(false);
                    plcsr = dbproductmethods.getProductsNotInAisle(currentaisleid,productfilter,productorderby);
                    productlistadapter.swapCursor(plcsr);
                }
                if (internalmode == EDITMODE && !editdisplayed) {
                    editdisplayed = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return rv;
    }

    /**************************************************************************
     * setMessage - set the Message regarding the lat action performed
     * Note as an action may have altered the database getDBcounts
     * is called and the title changed
     *
     * @param sa   the sa
     * @param msg  The message to be displayed.
     * @param flag Message imnportant, if true Yellow text, esle green
     */
    public void setMessage(StockAddActivity sa, String msg, boolean flag) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);

        TextView messagebar = sa.findViewById(R.id.stock_messagebar);
        messagebar.setText(context.getResources().getString(
                R.string.messagebar_prefix_lastaction,msg));
        if (flag) {
            messagebar.setTextColor(Color.YELLOW);
        } else {
            messagebar.setTextColor(Color.GREEN);
        }
        messagebar.setVisibility(View.VISIBLE);
    }

    /**************************************************************************
     *
     * @param stockorder    The EditText containing the stockorder
     * @param stockcost     The EditText containing the stockcost
     * @param stockchkcount The editText containing the stock check count
     */
    @SuppressLint("SetTextI18n")
    private void setNewInput(EditText stockorder,
                             EditText stockcost,
                             EditText stockchkcount) {
        String logmsg = "Invoked";
        String methodname =
                new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,
                LOGTAG,
                logmsg,
                THISCLASS,
                methodname);
        int highorder =
                dbpumethods.getHighestProductUsageOrderPerAisle(currentaisleid) +
                        100;
        if (highorder < 1000) {
            highorder = 1000;
        }
        if (highorder > 9999) {
            highorder = highorder - 100 + 1;
        }
        if (highorder > 9999) {
            highorder = 9999;
        }
        stockorder.setText(Integer.toString(highorder));
        stockcost.setText(context.getResources().getString(R.string.costdefault));
        stockchkcount.setText("1");
    }

    /**************************************************************************
     * getOrderBy - Generate the new ORDEY BY sql (ORDER BY already exists)
     * @param newcolumn     the DB column to sort by
     * @param neworderfld   the column as an integer as per constants
     */
    private void getOrderBy(String newcolumn, int neworderfld) {
        String logmsg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,logmsg,THISCLASS,methodname);
        orderby = newcolumn;
        // If already sorted by this column then toggle between ascedning and
        // descending.
        // If not then default to ascending
        if (orderfld == neworderfld) {
            if (ordertype) {
                orderby = orderby + SORTDESCENDING;
                ordertype = false;
            } else {
                orderby = orderby + SORTASCENDING;
                ordertype = true;
            }
        } else {
            orderby = orderby + SORTASCENDING;
            ordertype = true;
        }
        orderfld = neworderfld;
        sortchanged = true;
    }

    /**************************************************************************
     * Do nothing.
     *
     * @param values the values
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void doNothing(RequestDialogParameters values) {
    }

    /**************************************************************************
     * Usecalledmethods.
     */
    @SuppressWarnings("unused")
    protected void usecalledmethods() {
        stockDelete(null);
        stockEdit(null);
    }
}
