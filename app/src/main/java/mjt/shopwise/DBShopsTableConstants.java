package mjt.shopwise;

import java.util.ArrayList;
import java.util.Arrays;

import static mjt.shopwise.DBConstants.DEFAULTORDER;
import static mjt.shopwise.SQLKWORD.*;

/**
 * ShopWise Shops Table Constants
 */
@SuppressWarnings("WeakerAccess")
public class DBShopsTableConstants {
    /**************************************************************************
     * SHOPS TABLE -
     * ID(INT) - KEY/PRIMARY INDEX
     * ORDER(INT) - default as per DEFAULTORDER
     * SHOPNAME(TXT)
     * SHOPSTREET(TXT)
     * SHOPCITY(TXT)
     * SHOPSTATE(TXT)
     * SHOPNOTES(TXT)
     */
    public static final String SHOPS_TABLE = "shops";       // tables name

    /**
     * _id - primary integer(long) - index/key
     */
    public static final String SHOPS_ID_COL = SQLSTD_ID;
    /**
     * The constant SHOPS_ID_COL_FULL.
     */
    public static final String SHOPS_ID_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_ID_COL;
    /**
     * The constant SHOPS_ALTID_COL.
     */
    public static final String SHOPS_ALTID_COL = SHOPS_TABLE + SQLSTD_ID;
    /**
     * The constant SHOPS_ALTID_COL_FULL.
     */
    @SuppressWarnings("unused")
    public static final String SHOPS_ALTID_COL_FULL =  SHOPS_TABLE +
            SQLPERIOD + SHOPS_ALTID_COL;
    /**
     * The constant SHOPSIDCOL
     */
    public static final DBColumn SHOPSIDCOL = new DBColumn(true);
    /**
     * shoporder - integer - order of the shop (ascending)
     */


    public static final String SHOPS_ORDER_COL = "shoporder";
    /**
     * The constant SHOPS_ORDER_COL_FULL.
     */
    public static final String SHOPS_ORDER_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_ORDER_COL;
    /**
     * The constant SHOPS_ORDER_TYPE.
     */
    public static final String SHOPS_ORDER_TYPE = SQLINTEGER;
    /**
     * The constant SHOPS_ORDER_PRIMARY_INDEX.
     */
    public static final boolean SHOPS_ORDER_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSORDERCOL.
     */
    public static final DBColumn SHOPSORDERCOL = new DBColumn(SHOPS_ORDER_COL,
            SHOPS_ORDER_TYPE,
            SHOPS_ORDER_PRIMARY_INDEX,
            DEFAULTORDER
    );


    /**
     * shopname - text - name of the shop
     */
    public static final String SHOPS_NAME_COL = "shopname";
    /**
     * The constant SHOPS_NAME_COL_FULL.
     */
    public static final String SHOPS_NAME_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_NAME_COL;
    /**
     * The constant SHOPS_NAME_TYPE.
     */
    public static final String SHOPS_NAME_TYPE = SQLTEXT;
    /**
     * The constant SHOPS_NAME_PRIMARY_INDEX.
     */
    @SuppressWarnings("unused")
    public static final boolean SHOPS_NAME_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSNAMECOL.
     */
    public static final DBColumn SHOPSNAMECOL = new DBColumn(SHOPS_NAME_COL,
            SHOPS_NAME_TYPE,
            SHOPS_ORDER_PRIMARY_INDEX,
            ""
    );


    /**
     * shopstreet - text - street part of address
     */
    public static final String SHOPS_STREET_COL = "shopstreet";
    /**
     * The constant SHOPS_STREET_COL_FULL.
     */
    public static final String SHOPS_STREET_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_STREET_COL;
    /**
     * The constant SHOPS_STREET_TYPE.
     */
    public static final String SHOPS_STREET_TYPE = SQLTEXT;
    /**
     * The constant SHOPS_STREET_PRIMARY_INDEX.
     */
    public static final boolean SHOPS_STREET_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSSTREETCOL.
     */
    public static final DBColumn SHOPSSTREETCOL = new DBColumn(SHOPS_STREET_COL,
            SHOPS_STREET_TYPE,
            SHOPS_STREET_PRIMARY_INDEX,
            ""
    );


    /**
     * shopcity - text - city/area part of the address
     */
    public static final String SHOPS_CITY_COL = "shopcity";
    /**
     * The constant SHOPS_CITY_COL_FULL.
     */
    public static final String SHOPS_CITY_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_CITY_COL;
    /**
     * The constant SHOPS_CITY_TYPE.
     */
    public static final String SHOPS_CITY_TYPE = SQLTEXT;
    /**
     * The constant SHOPS_CITY_PRIMARY_INDEX.
     */
    public static final boolean SHOPS_CITY_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSCITYCOL.
     */
    public static final DBColumn SHOPSCITYCOL = new DBColumn(SHOPS_CITY_COL,
            SHOPS_CITY_TYPE,
            SHOPS_CITY_PRIMARY_INDEX,
            "");

    /**
     * shopstate - text - state/county part of the address
     */
    public static final String SHOPS_STATE_COL = "shopstate";
    /**
     * The constant SHOPS_STATE_COL_FULL.
     */
    public static final String SHOPS_STATE_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_STATE_COL;
    /**
     * The constant SHOPS_STATE_TYPE.
     */
    public static final String SHOPS_STATE_TYPE = SQLTEXT;
    /**
     * The constant SHOPS_STATE_PRIMARY_INDEX.
     */
    public static final boolean SHOPS_STATE_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSSTATECOL.
     */
    public static final DBColumn SHOPSSTATECOL = new DBColumn(SHOPS_STATE_COL,
            SHOPS_STATE_TYPE,
            SHOPS_STATE_PRIMARY_INDEX,
            "");


    /**
     * shopnotes - text - notes about the shop
     */
    public static final String SHOPS_NOTES_COL = "shopnotes";
    /**
     * The constant SHOPS_NOTES_COL_FULL.
     */
    public static final String SHOPS_NOTES_COL_FULL = SHOPS_TABLE +
            SQLPERIOD +
            SHOPS_NOTES_COL;
    /**
     * The constant SHOPS_NOTES_TYPE.
     */
    public static final String SHOPS_NOTES_TYPE = SQLTEXT;
    /**
     * The constant SHOPS_NOTES_PRIMARY_INDEX.
     */
    public static final boolean SHOPS_NOTES_PRIMARY_INDEX = false;
    /**
     * The constant SHOPSNOTESCOL.
     */
    public static final DBColumn SHOPSNOTESCOL = new DBColumn(SHOPS_NOTES_COL,
            SHOPS_NOTES_TYPE,
            SHOPS_NOTES_PRIMARY_INDEX,
            "");


    /**
     * The constant SHOPSCOLS.
     */
    public static final ArrayList<DBColumn> SHOPSCOLS = new ArrayList<>(Arrays.asList(SHOPSIDCOL,
            SHOPSORDERCOL,
            SHOPSNAMECOL,
            SHOPSSTREETCOL,
            SHOPSCITYCOL,
            SHOPSSTATECOL,
            SHOPSNOTESCOL));
    /**
     * The constant SHOPSTABLE.
     */
    public static final DBTable SHOPSTABLE = new DBTable(SHOPS_TABLE,SHOPSCOLS);
    public static final String SHOPSMAXORDERCOLUMN = "maxorder";
}
