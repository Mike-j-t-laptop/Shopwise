package mjt.shopwise;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static mjt.shopwise.SQLKWORD.*;

/**
 * DBProductUsageMethods - Database methods sepcific to ProductUsage handling
 * <p>
 * A ProductUsage represents a product within an aisle (location) so is
 * a shop specific representation of a product. (This is a generalisation, as
 * it is possible to have a product in multiple aisles within a store).
 * <p>
 * Although a ProductUsage represents the link between product and store
 * (a many-many relationship) other values that are store/aisle specfifc
 * are held in this table. e.g. cost and order.
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
class DBProductUsageMethods {
    private Context context;
    private DBDAO dbdao;
    private static SQLiteDatabase db;
    private static long lastproductusageadded;
    private static boolean lastproductusageaddok = false;
    private static boolean lastproductusageduplicate = false;
    private static boolean lastprdductusageupdateok = false;
    public static final String THISCLASS = DBProductUsageMethods.class.getSimpleName();
    private static final String LOGTAG = "SW_DBPUM";
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat(StandardAppConstants.EXTENDED_DATE_FORMAT);

    /**
     * Instantiates a new Db product usage methods.
     *
     * @param ctxt the ctxt
     */
    DBProductUsageMethods(Context ctxt) {
        String msg = "Constructing";
        String methodname = "Construct";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        context = ctxt;
        dbdao = new DBDAO(context);
        db = dbdao.getDb();
    }

    /**************************************************************************
     * @return the number of ProductUsage rows
     */
    int getProductUsageCount() {
        return dbdao.getTableRowCount(
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE
        );
    }

    /**************************************************************************
     * @return ProudctUsage id of the last added
     */
    @SuppressWarnings("unused")
    long getLastProductusageAdded() { return lastproductusageadded; }

    /**************************************************************************
     * @return true if last ProductUsage was add OK, else false
     */
    boolean ifProductUsageAdded() { return lastproductusageaddok; }

    /**************************************************************************
     *
     * @return true if lastupdate attempt updated, esel false
     */
    boolean ifProductUsageUpdated() { return lastprdductusageupdateok; }

    /**************************************************************************
     * If product usage was duplicate boolean.
     *
     * @return true if attempt to insert productusage failed because the
     * ProductUsage (combination of AisleRef and ProductRef) already
     * existed, otherwisee false.
     */
    @SuppressWarnings("unused")
    boolean ifProductUsageWasDuplicate() { return lastproductusageduplicate; }

    /**************************************************************************
     *
     * @param aisleid   id of the aisle
     * @return          the highest productusage order
     */
    int getHighestProductUsageOrderPerAisle(long aisleid) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        int rv = 0;

        String[] columns = new String[]{
                SQLMAX +
                        DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL +
                        SQLMAXCLOSE +
                        SQLAS +
                        DBProductusageTableConstants.PRODUCTUSAGEMAXORDERCOLUMN
        };
        String whereclause = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                " = ? ";
        String[] whereargs = new String[]{
                Long.toString(aisleid)
        };
        Cursor csr = db.query(
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                columns,
                whereclause,
                whereargs,
                null,null,null
        );
        if (csr.getCount() > 0 ) {
            csr.moveToFirst();
            rv = csr.getInt(csr.getColumnIndex(
                    DBProductusageTableConstants.PRODUCTUSAGEMAXORDERCOLUMN
            ));
        }
        csr.close();
        msg = "Highhest ProductUsage Order=" + rv;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return rv;
    }


    /**************************************************************************
     * @param aisleref   id of the Aisle
     * @param productref id of the product
     * @return true if found, else false
     */
    boolean doesProductUsageExist(long aisleref, long productref) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        boolean rv = false;
        String filter = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL +
                " = " +
                aisleref +
                SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL +
                " = " +
                productref;
        Cursor csr = DBCommonMethods.getTableRows(db,
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                filter,
                ""
        );
        if(csr.getCount() > 0 ) {
            rv = true;
        }
        csr.close();
        msg = "Product Usage for AisleID=" + aisleref +
                " ProductID=" + productref;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return rv;
    }

    /**************************************************************************
     *
     * @param aisleref      id of the aisle
     * @param productref    id of the product
     */
    void setChecklistCheckedStatus(long aisleref, long productref) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        int currentcheckstatus;
        int toggledcheckstatus = 2;
        int updatecount;
        String filter = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL +
                " = " +
                aisleref +
                SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL +
                " = " +
                productref;
        Cursor csr = DBCommonMethods.getTableRows(db,
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                filter,
                "");
        if (csr.getCount() > 0 ) {
            csr.moveToFirst();
            currentcheckstatus = csr.getInt(csr.getColumnIndex(
                    DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL
            ));
            if (currentcheckstatus > 1) {
                toggledcheckstatus =1;
            }
        }
        csr.close();
        ContentValues cv = new ContentValues();
        String[] whereargs = {Long.toString(aisleref), Long.toString(productref)};
        String whereclause = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL + " = ? " +
                SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL + " = ?";
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL,toggledcheckstatus);
        updatecount = db.update(
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                cv,
                whereclause,
                whereargs);
        msg = "Set Checklist Checked Status UpdateOK=" +
                (updatecount > 0);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     *
     */
    void resetChecklistCheckedStatus() {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        int updatecount;
        String[] whereargs = {Integer.toString(1)};
        String whereclause = DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL + " >= ?";
        ContentValues cv = new ContentValues();
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL,1);
        updatecount = db.update(
                DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                cv,
                whereclause,
                whereargs);
        msg = "RESET Checklist Updated " + updatecount + " Product Usages";
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * getProductUsages - get ProductUsages as a Cursor
     *
     * @param filter filter  clause, if required, less WHERE keyword
     * @param order  order clause, if required, LESS ORDER BY keywords
     * @return product usages
     */
    Cursor getProductUsages(String filter, @SuppressWarnings("SameParameterValue") String order) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return dbdao.getTableRows(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                "",
                filter,
                order);
    }

    /**************************************************************************
     * Get ProductUsages that have been set to appear in the checklist
     * Note! adds 0 in the Shopping List if there is no current Shopping list entry
     * for the productUsage SQL generated for this is (brackets included):-
     * (CASE WHEN shoplist.numbertoget IS NULL THEN 0 ELSE shoplist.numbertoget END)
     *
     *
     * @param filter    SQL WHERE clause less WHERE Noting that
     *                  productusage.productusagechecklistflag > 0 WILL ALWAYS
     *                  be the first item of the WHERE clause.
     * @param order     SQL ORDER clause (none by default)
     * @return          The generated SQLIte DB Cursor
     */
    Cursor getCheckList(String filter, String order) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        Cursor rv;
        String sql = SQLSELECT +
                " ABS(RANDOM()) AS " + SQLSTD_ID + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_COST_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL_FULL + ", " +

                "(CASE WHEN " +
                DBShopListTableConstants.SHOPLIST_NUMBERTOGET_COL_FULL +
                " IS NULL THEN 0 ELSE " +
                DBShopListTableConstants.SHOPLIST_NUMBERTOGET_COL_FULL +
                " END ) AS " +
                DBConstants.CALCULATED_PRODUCTSORDERED_NAME
                + ", " +

                DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + ", " +
                DBProductsTableConstants.PRODUCTS_STORAGEREF_COL_FULL + ", " +
                DBProductsTableConstants.PRODUCTS_STORAGEORDER_COL_FULL + ", " +
                DBProductsTableConstants.PRODUCTS_NOTES_COL_FULL + ", " +

                DBStorageTableConstants.STORAGE_NAME_COL_FULL + ", " +
                DBStorageTableConstants.STORAGE_ORDER_COL_FULL + ", " +

                DBAislesTableConstants.AISLES_SHOPREF_COL_FULL + ", " +
                DBAislesTableConstants.AISLES_NAME_COL_FULL + ", " +
                DBAislesTableConstants.AISLES_ORDER_COL_FULL + ", " +

                DBShopsTableConstants.SHOPS_NAME_COL_FULL + ", " +
                DBShopsTableConstants.SHOPS_CITY_COL_FULL + ", " +
                DBShopsTableConstants.SHOPS_ORDER_COL_FULL + " " +

                SQLFROM + DBProductusageTableConstants.PRODUCTUSAGE_TABLE +

                SQLLEFTJOIN + DBProductsTableConstants.PRODUCTS_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + " = " +
                DBProductsTableConstants.PRODUCTS_ID_COL_FULL +

                SQLLEFTJOIN +
                DBAislesTableConstants.AISLES_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + " = " +
                DBAislesTableConstants.AISLES_ID_COL_FULL +

                SQLLEFTJOIN +
                DBShopsTableConstants.SHOPS_TABLE +
                SQLON +
                DBAislesTableConstants.AISLES_SHOPREF_COL_FULL + " = " +
                DBShopsTableConstants.SHOPS_ID_COL_FULL +

                SQLLEFTJOIN +
                DBShopListTableConstants.SHOPLIST_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + " = " +
                DBShopListTableConstants.SHOPLIST_PRODUCTREF_COL_FULL +
                SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + " = " +
                DBShopListTableConstants.SHOPLIST_AISLEREF_COL_FULL +

                SQLLEFTJOIN +
                DBStorageTableConstants.STORAGE_TABLE +
                SQLON +
                DBProductsTableConstants.PRODUCTS_STORAGEREF_COL_FULL + " = " +
                DBStorageTableConstants.STORAGE_ID_COL_FULL +

                SQLWHERE +
                DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL_FULL + " > 0 " +
                SQLAND +
                DBStorageTableConstants.STORAGE_NAME_COL_FULL +
                SQLISNOTNULL;
        if (filter.length() > 0 ) {
            sql = sql + SQLAND + filter;
        }
        if (order.length() > 0 ) {
            sql = sql + SQLORDERBY +
                    DBStorageTableConstants.STORAGE_ORDER_COL_FULL +
                    SQLORDERASCENDING + ", " +
                    DBProductsTableConstants.PRODUCTS_STORAGEORDER_COL_FULL +
                    SQLORDERASCENDING;
        }
        sql = sql +SQLENDSTATEMENT;

        rv = db.rawQuery(sql,null);
        msg = "Returned Cursor with " + rv.getCount() + " rows." +
                "\n\t SQL used=" + sql;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return rv;

    }

    /**************************************************************************
     *
     * @param filter    SQL filter clause less WHERE
     * @param order     SQL ORDER clause less ORDER BY
     * @return          Cursor containing expanded product usage
     *                  expanded means joined to referenced tables
     *                  i.e. respective Aisle, Product Shop (from aisle)
     *                  and ShopList
     */
    Cursor getExpandedProductUsages(String filter, String order) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        Cursor rv;
        String sql = SQLSELECT +
                " ABS(RANDOM()) AS " + SQLSTD_ID + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_COST_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL_FULL + ", " +
                DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL_FULL + ", " +

                "(CASE WHEN " +
                DBShopListTableConstants.SHOPLIST_NUMBERTOGET_COL_FULL +
                " IS NULL THEN 0 ELSE " +
                DBShopListTableConstants.SHOPLIST_NUMBERTOGET_COL_FULL +
                " END ) AS " +
                DBConstants.CALCULATED_PRODUCTSORDERED_NAME
                + ", " +

                DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + ", " +

                DBAislesTableConstants.AISLES_SHOPREF_COL_FULL + ", " +
                DBAislesTableConstants.AISLES_NAME_COL_FULL + ", " +
                DBAislesTableConstants.AISLES_ORDER_COL_FULL + ", " +

                DBShopsTableConstants.SHOPS_NAME_COL_FULL + ", " +
                DBShopsTableConstants.SHOPS_CITY_COL_FULL + ", " +
                DBShopsTableConstants.SHOPS_ORDER_COL_FULL + " " +

                SQLFROM + DBProductusageTableConstants.PRODUCTUSAGE_TABLE +

                SQLLEFTJOIN + DBProductsTableConstants.PRODUCTS_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + " = " +
                DBProductsTableConstants.PRODUCTS_ID_COL_FULL +

                SQLLEFTJOIN +
                DBAislesTableConstants.AISLES_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + " = " +
                DBAislesTableConstants.AISLES_ID_COL_FULL +

                SQLLEFTJOIN +
                DBShopsTableConstants.SHOPS_TABLE +
                SQLON +
                DBAislesTableConstants.AISLES_SHOPREF_COL_FULL + " = " +
                DBShopsTableConstants.SHOPS_ID_COL_FULL +

                SQLLEFTJOIN +
                DBShopListTableConstants.SHOPLIST_TABLE +
                SQLON +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL + " = " +
                DBShopListTableConstants.SHOPLIST_PRODUCTREF_COL_FULL +
                SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL + " = " +
                DBShopListTableConstants.SHOPLIST_AISLEREF_COL_FULL;
        if (filter.length() > 0 ) {
            sql = sql + SQLWHERE + filter;
        }
        if (order.length() > 0 ) {
            sql = sql + SQLORDERBY + order;
        }
        sql = sql + SQLENDSTATEMENT;

        rv = db.rawQuery(sql,null);
        msg = "Returned Cursor with " + rv.getCount() + " rows." +
                "\n\t SQL used=" + sql;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return rv;
    }

    /**************************************************************************
     *
     * @param aisleid       The Aisleref
     * @param productid     The Productref
     * @return              The String Array with the impact strings
     */
    ArrayList<String> stockDeleteImapct(long aisleid, long productid) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        ArrayList<String> rv = new ArrayList<>();

        String pufilter = DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL +
                " = " + aisleid +
                " AND " +
                DBProductsTableConstants.PRODUCTS_ID_COL_FULL +
                " = " + productid;
        Cursor pucsr = getExpandedProductUsages(pufilter,"");
        if (pucsr.getCount() > 0 ) {
            while (pucsr.moveToNext()) {
                String aislename = pucsr.getString(pucsr.getColumnIndex(
                        DBAislesTableConstants.AISLES_NAME_COL
                ));
                String shopname = pucsr.getString(pucsr.getColumnIndex(
                        DBShopsTableConstants.SHOPS_NAME_COL
                ));
                String productname = pucsr.getString(pucsr.getColumnIndex(
                        DBProductsTableConstants.PRODUCTS_NAME_COL
                ));
                rv.add("Stocked Item " + productname +
                " in Aisle " + aislename +
                ", Shop " + shopname +
                " would be deleted.");
                rv.addAll(
                        new DBShopListMethods(context).shopListEntryDeleteImpact(
                                aisleid,
                                productid
                        )
                );
                rv.addAll(
                        new DBRuleMethods(context).ruleDeleteImpact(
                                aisleid,
                                productid
                        )
                );
            }
        }
        return rv;
    }

    /**************************************************************************
     *
     * @param aisled        The Aisleref
     * @param productid     The Productref
     * @param intransaction true if already in a transaction, false if not
     * @return              Number of rows deleted
     */
    @SuppressWarnings("UnusedReturnValue")
    int deleteStock(long aisled, long productid, @SuppressWarnings("SameParameterValue") boolean intransaction) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        int rv;

        String slfilter = DBShopListTableConstants.SHOPLIST_AISLEREF_COL_FULL +
                " = " + aisled +
                " AND " +
                DBShopListTableConstants.SHOPLIST_PRODUCTREF_COL_FULL +
                " = " + productid;
        Cursor sllcsr = new DBShopListMethods(context)
                .getShopListEntries(slfilter,"");
        String rlfilter = DBRulesTableConstants.RULES_AISLEREF_COL_FULL +
                " = " + aisled +
                " AND " +
                DBRulesTableConstants.RULES_PRODUCTREF_COL_FULL +
                " = " + productid;
        Cursor rlcsr = new DBRuleMethods(context).getRules(rlfilter,"");

        if(!intransaction) {
            db.beginTransaction();
            msg = "Starting DB Transaction";
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        }

        while (sllcsr.moveToNext()) {
            long shoplistid = sllcsr.getLong(sllcsr.getColumnIndex(
                    DBShopListTableConstants.SHOPLIST_ID_COL
            ));
            new DBShopListMethods(context).deleteShopListEntry(shoplistid);
        }
        sllcsr.close();

        while (rlcsr.moveToNext()) {
            long ruleid = rlcsr.getLong(rlcsr.getColumnIndex(
                    DBRulesTableConstants.RULES_ID_COL
            ));
            new DBRuleMethods(context).deleteRule(ruleid);
        }
        rlcsr.close();

        String dlt_shoplistentry_whereclause =
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                        " = ? AND " +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL +
                        " = ?";
        String[] dlt_shoplistentry_whereargs = {
                Long.toString(aisled),
                Long.toString(productid)
        };
        rv = db.delete(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                dlt_shoplistentry_whereclause,
                dlt_shoplistentry_whereargs);
        if(!intransaction) {
            db.setTransactionSuccessful();
            db.endTransaction();
            msg = "SET AND ENDED DB Transaction";
            LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        }
        return rv;
    }

    /**************************************************************************
     *
     * @param aisleid       The Aisleref
     * @param productid     The productID
     * @param newcost       The new cost to be applied
     */
    void amendProductUsageCost(long aisleid,
                               long productid,
                               double newcost) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        int updatecount = 0;
        ContentValues cv = new ContentValues();
        lastprdductusageupdateok = false;
        String whereclause =
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                        "= ? AND " +
                        DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL +
                        " = ? ";
        String[] whereargs = new String[]{
                Long.toString(aisleid),
                Long.toString(productid)
        };
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_COST_COL,newcost);
        if (doesProductUsageExist(aisleid,productid)) {
            updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    cv,whereclause,whereargs);
            if (updatecount > 0) {
                lastprdductusageupdateok = true;
            }
        }
        msg = "Update Cost=" + (updatecount > 0) +
                " for ProductUsage AisleID=" + aisleid +
                " ProductID=" + productid;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     *
     * @param aisleid           id of the aisle
     * @param productid         id of the product
     * @param numberpurchased   number of the items purchased
     */
    void amendPurchasedProductUsage(long aisleid,
                                    long productid,
                                    @SuppressWarnings("SameParameterValue") int numberpurchased) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        long currentfirstbuydate;
        int currentbuycount;
        int updatecount = 0;
        lastprdductusageupdateok = false;
        ContentValues cv = new ContentValues();
        String whereclause =
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                        " = ? AND " +
                        DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL +
                        " = ? ";
        String[] whereargs = new String[]{
                Long.toString(aisleid),
                Long.toString(productid)
        };
        Cursor csr = db.query(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                null,whereclause,whereargs,null,null,null
        );

        if (csr.getCount() > 0) {
            csr.moveToFirst();
            currentfirstbuydate =  csr.getLong(csr.getColumnIndex(
                    DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL
            ));
            if (currentfirstbuydate == 0) {
                cv.put(DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL,
                        System.currentTimeMillis());
            }
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL,
                    System.currentTimeMillis());
            currentbuycount = csr.getInt(csr.getColumnIndex(
                    DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL
            ));
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL,
                    currentbuycount + numberpurchased);
            csr.close();
            updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    cv,
                    whereclause,
                    whereargs
            );
            if (updatecount > 0 ) {
                lastprdductusageupdateok = true;
            }
        } else {
            csr.close();
        }
        msg = "Productusage AisleID=" + aisleid +
                " ProductID=" + productid +
                " Purchased Update=" + (updatecount > 0);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     *
     * @param productref        The Productref
     * @param aisleref          The Aisleref
     * @param cost              The Cost to be applied
     * @param order             The Order to be applied
     * @param checklistflag     The CheckList Flag to be applied
     * @param checklistcount    The CheckList count to be applied
     */
    void modifyProductUsage(long productref,
                            long aisleref,
                            double cost,
                            int order,
                            boolean checklistflag,
                            int checklistcount) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        lastprdductusageupdateok = false;
        //int zero = 0;
        //long lzero = 0;
        int modified;
        String whereclause =
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                " = ? AND " +
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL +
                " = ?";
        String[] whereargs = {Long.toString(aisleref), Long.toString(productref)};
        if (doesProductUsageExist(aisleref,productref)) {
            lastproductusageduplicate = false;
            ContentValues cv = new ContentValues();
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_COST_COL, cost);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL, order);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL,checklistflag);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL,checklistcount);
            modified = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    cv,whereclause,whereargs);
            if (modified > 0 ) {
                lastprdductusageupdateok = true;
            }

        }
        msg = "ProductUsage for AisleID=" + aisleref +
                " ProductID=" + productref +
                " Modified=" + lastprdductusageupdateok;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * insertProductUsage       Insert/Add a ProductUsage entry
     *
     * @param productref The id of the Product
     * @param aisleref   The id of the Aisle
     * @param cost       The cost to be associated with this
     *                   ProductUsage
     * @param order      The order within the aisle
     *                   NOTE!! combination of productref and aisleref
     *                   has to be unqiue for the insertion to work.
     *                   If not then lastproductusageduplicate will
     *                   be set to true
     */
    void insertProductUsage(long productref,
                            long aisleref,
                            double cost,
                            int order,
                            boolean checklistflag,
                            int checklistcount) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        int zero = 0;
        long lzero = 0;
        long addedid;
        if (!doesProductUsageExist(aisleref, productref)) {
            lastproductusageduplicate = false;
            ContentValues cv = new ContentValues();
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL,
                    productref);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL,
                    aisleref);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL, zero);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_COST_COL, cost);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL, lzero);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL, lzero);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL, order);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL, zero);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTFLAG_COL,checklistflag);
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_CHECKLISTCOUNT_COL,checklistcount);
            addedid = db.insert(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    null,
                    cv);
            if (addedid > -1) {
                lastproductusageadded = addedid;
                lastproductusageaddok = true;
            } else {
                lastproductusageaddok = false;
            }
        } else {
            lastproductusageduplicate = true;
            lastproductusageaddok = false;
        }
        msg = "ProductUsage AisleID=" + aisleref +
                " ProductID=" + productref +
                " Inserted=" + lastproductusageaddok +
                " Duplicate=" + lastproductusageduplicate;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * getBuyCount          return the current Productusage BuyCount as an
     * integer, if not found then returns -1
     * Buycount should never be -1.
     *
     * @param productref id of the product
     * @param aisleref   id of the aisle
     * @return a MixTripleLongTripleInt (3 longs and 3 ints) :-                          long1 the firstbutdate                          long2 the latestbuydate                          long3 unused                          int1 the buycount                          int2 unused                          int3 unused
     */
    MixTripleLongTripleInt getBuyCount(long productref, long aisleref) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        int buycount = -1;
        long firstbuydate = 0;
        long lastbuydate = 0;
        MixTripleLongTripleInt rv = new MixTripleLongTripleInt();
        if (doesProductUsageExist(aisleref, productref)) {
            String filter = buildSingleProductUsageFilter(productref, aisleref);
            Cursor csr = getProductUsages(filter, "");
            if (csr.moveToFirst()) {
                buycount = csr.getInt(
                        csr.getColumnIndex(
                                DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL));
                firstbuydate = csr.getLong(
                        csr.getColumnIndex(
                                DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL));
                lastbuydate = csr.getLong(
                        csr.getColumnIndex(
                                DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL));
            }
            csr.close();
        }
        rv.setMIXTRPPLONGINT(firstbuydate,lastbuydate,0,buycount,0,0);
        msg = "Returning for ProductUsage AisleID=" + aisleref +
                " ProductID=" + productref +
                " :- \n\tBought=" + buycount +
                " First Bought=" + sdf.format(firstbuydate) +
                " Last Bought=" + sdf.format(lastbuydate);
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return rv;
    }

    /**************************************************************************
     * incrementBuyCount        increment the buycount, the firstbuydate
     * and the lastbuydate to reflect that the
     * productusage has been purchased.
     * Note! firstbuydate is only updated if
     * it is 0 (as initialised).
     *
     * @param productref product id referenced by this productusage
     * @param aisleref   aisle id referenced by this productusage
     */
    @SuppressWarnings("unused")
    void incrementBuyCount(Long productref, Long aisleref) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        lastprdductusageupdateok = false;

        // Get the buycount, firstbuydate and latestbuydate
        MixTripleLongTripleInt mixvalues = getBuyCount(productref, aisleref);
        // increment the buycount
        int buycount = mixvalues.getint1() + 1;
        long firstbuydate = mixvalues.getlong1();
        long lastbuydate = new Date().getTime();
        if (firstbuydate == 0) { firstbuydate = lastbuydate; }
        if (buycount < 1 ) { return; }
        ContentValues cv = new ContentValues();
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_BUYCOUNT_COL,
                buycount);
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_FIRSTBUYDATE_COL,
                firstbuydate);
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_LATESTBUYDATE_COL,
                lastbuydate);
        String[] whereargs = {Long.toString(productref), Long.toString(aisleref)};
        String whereclause = buildCVWhereClause();
        int updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                cv,
                whereclause,
                whereargs);
        if (updatecount > 0 ) {
            lastprdductusageupdateok = true;
        }
        msg = "ProductUsage AisleID=" + aisleref +
                " ProductID=" + productref +
                " BuyCount Incremented=" + lastprdductusageupdateok;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * setProductusageOrder     set the order of a specific ProductUsage
     *
     * @param productref product id referenced by the ProductUsage
     * @param aisleref   aisle id referenced by the ProductUsage
     * @param neworder   the new order to be used
     */
    @SuppressWarnings("unused")
    void setProductUsageOrder(long productref, long aisleref, int neworder) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        lastprdductusageupdateok = false;
        if (doesProductUsageExist(productref, aisleref)) {
            String[] whereargs = {Long.toString(productref), Long.toString(aisleref)};
            ContentValues cv = new ContentValues();
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_ORDER_COL,neworder);
            int updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    cv,
                    buildCVWhereClause(),
                    whereargs);
            if (updatecount > 0 ) {
                lastprdductusageupdateok = true;
            }
        }
        msg = "ProductUsage AisleID=" + aisleref +
                " ProductID=" + productref +
                " Order Updated=" + lastprdductusageupdateok;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * setRuleSuggestFlag   set the RuleSuggestFlag
     *
     * @param productref product id referenced by this productusage
     * @param aisleref   aisle id referenced by this productusage
     * @param flag       flag (0=clear, 1=to skip, 2=disable)
     */
    void setRuleSuggestFlag(long productref, long aisleref, int flag) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        lastprdductusageupdateok = false;
        if (doesProductUsageExist(aisleref,productref)) {
            ContentValues cv = new ContentValues();
            cv.put(DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL, flag);
            String[] whereargs = {Long.toString(productref), Long.toString(aisleref)};
            int updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                    cv,
                    buildCVWhereClause(),
                    whereargs);
            if (updatecount > 0 ) {
                lastprdductusageupdateok = true;
            }
        }
        msg = "ProductUsage AisleID=" + aisleref +
                " ProductID=" + productref +
                " RuleSuggestFlag Updated=" + lastprdductusageupdateok;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * enableSkipped    change ruleSggestFlags from skipped to clear
     */
    void enableSkipped() {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);

        lastprdductusageupdateok = false;
        int updatecount;
        String[] whereargs = {Integer.toString(DBProductusageTableConstants.RULESUGGESTFLAG_SKIP)};
        String whereclause = DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL +
                " = ?";
        ContentValues cv = new ContentValues();
        cv.put(DBProductusageTableConstants.PRODUCTUSAGE_RULESUGGESTFLAG_COL,
                DBProductusageTableConstants.RULESUGGESTFLAG_CLEAR);
        updatecount = db.update(DBProductusageTableConstants.PRODUCTUSAGE_TABLE,
                cv,whereclause,
                whereargs);
        if (updatecount > 0 ) {
            lastprdductusageupdateok = true;
        }
        msg = "RuleSuggestFlags Cleared=" + updatecount;
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
    }

    /**************************************************************************
     * buildSingleProductUsageFilter    Build a filter string for a specific
     *                                  Productusage
     * @param productref    product id referenced by the ProductUsage
     * @param aislref       aisle id referenced by the ProductUsage
     * @return              Filter String
     */
    private String buildSingleProductUsageFilter(long productref, long aislref) {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return
                DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL_FULL +
                        " = " +
                        productref +
                        SQLAND +
                        DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL_FULL +
                        " = " +
                        aislref +
                        " ";
    }

    /**************************************************************************
     * buildCVWhereClause   build the ContentValues clause for a single
     *                      Productusage (albiet it generic and driven
     *                      by the whereagrs)
     * @return              ContentValues Where Clause
     */
    private String buildCVWhereClause() {
        String msg = "Invoked";
        String methodname = new Object(){}.getClass().getEnclosingMethod().getName();
        LogMsg.LogMsg(LogMsg.LOGTYPE_INFORMATIONAL,LOGTAG,msg,THISCLASS,methodname);
        return DBProductusageTableConstants.PRODUCTUSAGE_PRODUCTREF_COL +
                " = ? " + SQLAND +
                DBProductusageTableConstants.PRODUCTUSAGE_AISLEREF_COL +
                " = ?";
    }
}
