package mjt.shopwise;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static mjt.shopwise.SQLKWORD.*;

/**
 * ShopWise Database Common Methods
 */
@SuppressWarnings("WeakerAccess")
public class DBCommonMethods {


    private DBCommonMethods() {
    }


    /**
     * Gets table row count.
     *
     * @param db        the db
     * @param tablename the tablename
     * @param filter    the filter
     * @param order     the order
     * @return the table row count
     */
    static int getTableRowCount(SQLiteDatabase db,
                                String tablename,
                                String filter,
                                @SuppressWarnings("SameParameterValue") String order) {
        Cursor csr = getTableRows(db,tablename,filter,order);
        int rv = csr.getCount();
        csr.close();
        return rv;
    }

    /**
     * Gets table row count.
     *
     * @param db        the db
     * @param tablename the tablename
     * @return the table row count
     */
    static int getTableRowCount(SQLiteDatabase db, String tablename) {
        return getTableRowCount(db,tablename, "", "");
    }

    /**
     * Gets table rows.
     *
     * @param db     the db
     * @param table  the table
     * @param filter the filter
     * @param order  the order
     * @return the table rows
     */
    static Cursor getTableRows(SQLiteDatabase db,
                               String table,
                               String filter,
                               String order) {
        return getTableRows(db,"",table,"",filter,"",order,0);
    }


    /**
     * Gets table rows.
     *
     * @param db          the db
     * @param columns     the columns
     * @param table       the table
     * @param joinclauses the joinclauses
     * @param filter      the filter
     * @param groupclause the groupclause
     * @param order       the order
     * @param limit       the limit
     * @return the table rows
     */
    static Cursor getTableRows(SQLiteDatabase db,
                               @SuppressWarnings("SameParameterValue") String columns,
                               String table,
                               @SuppressWarnings("SameParameterValue") String joinclauses,
                               String filter,
                               @SuppressWarnings("SameParameterValue") String groupclause,
                               String order,
                               @SuppressWarnings("SameParameterValue") int limit) {
        if (columns.length() < 1 ) {
            columns = " * ";
        }
        String sql = SQLSELECT +
                columns + SQLFROM +
                table;
        if (joinclauses.length() > 0 ) {
            sql = sql + joinclauses;
        }
        if (filter.length() > 0 ) {
            sql = sql + SQLWHERE + filter;
        }
        if (groupclause.length() > 0 ) {
            sql = sql + SQLGROUP + groupclause;
        }
        if (order.length() > 0 ) {
            sql = sql + SQLORDERBY + order;
        }
        if (limit > 0 ) {
            sql = sql + SQLLIMIT + Integer.toString(limit);
        }
        sql = sql + SQLENDSTATEMENT;
        return db.rawQuery(sql,null);
    }

    static ArrayList<String> checkReferentialIntegrity(SQLiteDatabase db) {
        ArrayList<String> rv = new ArrayList<>();
        rv.add("No referential integrity issues have been detected.");
        /*
        -- Referential Inegrity Check
        SELECT
            (
                SELECT count() FROM aisles
                    LEFT JOIN shops ON aisleshopref = shops._id
                WHERE shopname IS NULL
            ) AS aisles_err,
            (
                SELECT count() FROM products
                    LEFT JOIN storage ON productstorageref = storage._id
                WHERE storagename IS NULL
            ) AS storage_err,
            (
                SELECT count() FROM rules
                    LEFT JOIN products ON ruleproductref = products._id
                    LEFT JOIN aisles ON ruleaisleref = aisles._id
                WHERE productname IS NULL OR aislename IS NULL
            ) AS rules_err,
            (
                SELECT count() FROM shoplist
                    LEFT JOIN products ON shoplistproductref = products._id
                    LEFT JOIN aisles ON shoplistaisleref = aisles._id
                    LEFT JOIN shops ON aisleshopref = shops._id
                WHERE productname IS NULL OR aislename IS NULL OR shopname IS NULL
            ) AS shoplist_err
        ;
         */
        String common_ri_columnname_suffix = "_err";
        String subqry_aisles_columnname = DBAislesTableConstants.AISLES_TABLE + common_ri_columnname_suffix;
        String subqry_aisles = "(SELECT count() FROM " + DBAislesTableConstants.AISLES_TABLE +
                " LEFT JOIN " + DBShopsTableConstants.SHOPS_TABLE +
                " ON " + DBAislesTableConstants.AISLES_SHOPREF_COL + " = " + DBShopsTableConstants.SHOPS_ID_COL_FULL +
                " WHERE " + DBShopsTableConstants.SHOPS_NAME_COL_FULL + " IS NULL) AS " +subqry_aisles_columnname;
        String qry_aisles = "SELECT * FROM " + DBAislesTableConstants.AISLES_TABLE +
                " LEFT JOIN " + DBShopsTableConstants.SHOPS_TABLE +
                " ON " + DBAislesTableConstants.AISLES_SHOPREF_COL + " = " + DBShopsTableConstants.SHOPS_ID_COL_FULL +
                " WHERE " + DBShopsTableConstants.SHOPS_NAME_COL_FULL + " IS NULL";

        String subqry_products_columnname = DBProductsTableConstants.PRODUCTS_TABLE + common_ri_columnname_suffix;
        String subqry_products = "(SELECT count() FROM " + DBProductsTableConstants.PRODUCTS_TABLE +
                " LEFT JOIN " + DBStorageTableConstants.STORAGE_TABLE +
                " ON " + DBProductsTableConstants.PRODUCTS_STORAGEREF_COL + " = " + DBStorageTableConstants.STORAGE_ID_COL_FULL +
                " WHERE " + DBStorageTableConstants.STORAGE_NAME_COL_FULL + " IS NULL) AS " + subqry_products_columnname;
        String qry_products = "SELECT * FROM " + DBProductsTableConstants.PRODUCTS_TABLE +
                " LEFT JOIN " + DBStorageTableConstants.STORAGE_TABLE +
                " ON " + DBProductsTableConstants.PRODUCTS_STORAGEREF_COL + " = " + DBStorageTableConstants.STORAGE_ID_COL_FULL +
                " WHERE " + DBStorageTableConstants.STORAGE_NAME_COL_FULL + " IS NULL";

        String subqry_rules_columname = DBRulesTableConstants.RULES_TABLE + common_ri_columnname_suffix;
        String subqry_rules = "(SELECT count() FROM " + DBRulesTableConstants.RULES_TABLE +
                " LEFT JOIN " + DBProductsTableConstants.PRODUCTS_TABLE +
                " ON " + DBRulesTableConstants.RULES_PRODUCTREF_COL + " = " + DBProductsTableConstants.PRODUCTS_ID_COL_FULL +
                " LEFT JOIN " + DBAislesTableConstants.AISLES_TABLE +
                " ON " + DBRulesTableConstants.RULES_AISLEREF_COL + " = " + DBAislesTableConstants.AISLES_ID_COL_FULL +
                " WHERE " + DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + " IS NULL " +
                " OR " + DBAislesTableConstants.AISLES_NAME_COL_FULL + " IS NULL) AS " + subqry_rules_columname;
        String qry_rules = "SELECT * FROM " + DBRulesTableConstants.RULES_TABLE +
                " LEFT JOIN " + DBProductsTableConstants.PRODUCTS_TABLE +
                " ON " + DBRulesTableConstants.RULES_PRODUCTREF_COL + " = " + DBProductsTableConstants.PRODUCTS_ID_COL_FULL +
                " LEFT JOIN " + DBAislesTableConstants.AISLES_TABLE +
                " ON " + DBRulesTableConstants.RULES_AISLEREF_COL + " = " + DBAislesTableConstants.AISLES_ID_COL_FULL +
                " WHERE " + DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + " IS NULL " +
                " OR " + DBAislesTableConstants.AISLES_NAME_COL_FULL + " IS NULL ";

        String subqry_shoplist_columnname = DBShopListTableConstants.SHOPLIST_TABLE + common_ri_columnname_suffix;
        String subqry_shoplist = "(SELECT count() FROM " + DBShopListTableConstants.SHOPLIST_TABLE +
                " LEFT JOIN " + DBProductsTableConstants.PRODUCTS_TABLE +
                " ON " + DBShopListTableConstants.SHOPLIST_PRODUCTREF_COL + " = " + DBProductsTableConstants.PRODUCTS_ID_COL_FULL +
                " LEFT JOIN " + DBAislesTableConstants.AISLES_TABLE +
                " ON " + DBShopListTableConstants.SHOPLIST_AISLEREF_COL + " = " + DBAislesTableConstants.AISLES_ID_COL_FULL +
                " LEFT JOIN " + DBShopsTableConstants.SHOPS_TABLE +
                " ON " + DBAislesTableConstants.AISLES_SHOPREF_COL + " = " + DBShopsTableConstants.SHOPS_ID_COL_FULL +
                " WHERE " + DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + " IS NULL " +
                " OR " + DBAislesTableConstants.AISLES_NAME_COL_FULL + " IS NULL " +
                " OR " + DBShopsTableConstants.SHOPS_NAME_COL_FULL + " IS NULL) AS " + subqry_shoplist_columnname;
        String qry_shoplist = "SELECT * FROM " + DBShopListTableConstants.SHOPLIST_TABLE +
                " LEFT JOIN " + DBProductsTableConstants.PRODUCTS_TABLE +
                " ON " + DBShopListTableConstants.SHOPLIST_PRODUCTREF_COL + " = " + DBProductsTableConstants.PRODUCTS_ID_COL_FULL +
                " LEFT JOIN " + DBAislesTableConstants.AISLES_TABLE +
                " ON " + DBShopListTableConstants.SHOPLIST_AISLEREF_COL + " = " + DBAislesTableConstants.AISLES_ID_COL_FULL +
                " LEFT JOIN " + DBShopsTableConstants.SHOPS_TABLE +
                " ON " + DBAislesTableConstants.AISLES_SHOPREF_COL + " = " + DBShopsTableConstants.SHOPS_ID_COL_FULL +
                " WHERE " + DBProductsTableConstants.PRODUCTS_NAME_COL_FULL + " IS NULL " +
                " OR " + DBAislesTableConstants.AISLES_NAME_COL_FULL + " IS NULL " +
                " OR " + DBShopsTableConstants.SHOPS_NAME_COL_FULL + " IS NULL";

        //NOTE can't use the following as SELECT  columns(sub queries) FROM  (i.e. the FROM keyword is added)
        // So have to use rawQuery.
        /*
        Cursor csr = db.query("",
                new String[]{subqry_aisles,subqry_products,subqry_rules,subqry_shoplist},
                null,null,null,null,null
        );
        */
        /*
        String full_qry = "SELECT " + subqry_aisles + "," + subqry_products + "," + subqry_rules + "," + subqry_shoplist;
        Log.d("RIQUERY",full_qry);
        Cursor csr = db.rawQuery(
                "SELECT " +
                        subqry_aisles + "," +
                        subqry_products + "," +
                        subqry_rules + "," +
                        subqry_shoplist,
                null
        );
        */
        Cursor csr = db.query(
                subqry_rules, // use subquery to fool/trick FROM clause so query method can be used rather than rawQuery
                new String[]{subqry_aisles,subqry_products,subqry_rules,subqry_shoplist},
                null,null,null,null,null);

        if (csr.moveToFirst()) {
            int aisle_issue_count = csr.getInt(csr.getColumnIndex(subqry_aisles_columnname));
            int product_issue_count = csr.getInt(csr.getColumnIndex(subqry_products_columnname));
            int rule_issues_count = csr.getInt(csr.getColumnIndex(subqry_rules_columname));
            int shoplist_issues_count = csr.getInt(csr.getColumnIndex(subqry_shoplist_columnname));
            csr.close();
            if (aisle_issue_count + product_issue_count + rule_issues_count + shoplist_issues_count == 0) {
                return rv;
            } else {
                Cursor csr2;
                rv.clear();
                if (aisle_issue_count > 0) {
                    rv.add("The " + DBAislesTableConstants.AISLES_TABLE + " has " + String.valueOf(aisle_issue_count) + " invalid references.");
                    csr2 = db.rawQuery(qry_aisles,null);
                    rv.addAll(dumpCursorToStringSrrayList(csr2,DBAislesTableConstants.AISLES_TABLE));

                }
                if (product_issue_count > 0 ) {
                    rv.add("The " + DBProductsTableConstants.PRODUCTS_TABLE + " has " + String.valueOf(product_issue_count) + " invalid references.");
                    csr2 = db.rawQuery(qry_products,null);
                    rv.addAll(dumpCursorToStringSrrayList(csr2,DBProductsTableConstants.PRODUCTS_TABLE));
                }
                if (rule_issues_count > 0 ) {
                    rv.add("The " + DBRulesTableConstants.RULES_TABLE + " has " + String.valueOf(rule_issues_count) + " invalid references.");
                    csr2 = db.rawQuery(qry_rules,null);
                    rv.addAll(dumpCursorToStringSrrayList(csr2,DBRulesTableConstants.RULES_TABLE));
                }
                if(shoplist_issues_count > 0) {
                    rv.add("The " + DBShopListTableConstants.SHOPLIST_TABLE + " has " + String.valueOf(shoplist_issues_count) + " invalid references");
                    csr2 = db.rawQuery(qry_shoplist,null);
                    rv.addAll(dumpCursorToStringSrrayList(csr2,DBShopListTableConstants.SHOPLIST_TABLE));
                }
            }
        } else {
            rv.clear();
            rv.add("It appears that the referential integrity check return no rows. Please notify the ShopWise developer of the issue");
        }
        return rv;
    }

    private static ArrayList<String> dumpCursorToStringSrrayList(Cursor csr, String tablename) {
        ArrayList<String> rv = new ArrayList<>();
        int original_position = csr.getPosition();
        csr.moveToPosition(-1);
        rv.add("Table: " + tablename);
        while (csr.moveToNext()) {
            rv.add("\tRow # " + String.valueOf(csr.getPosition() + 1));
            for (String column: csr.getColumnNames()) {
                rv.add("\t\tColumn: " + column + "\tvalue is: \t" + csr.getString(csr.getColumnIndex(column)));
            }
        }
        csr.moveToPosition(original_position);
        return rv;
    }
}
