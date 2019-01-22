package mjt.shopwise;

import java.util.ArrayList;
import java.util.Arrays;

import static mjt.shopwise.DBConstants.DEFAULTORDER;
import static mjt.shopwise.SQLKWORD.*;


/**
 * DBAislesTableConstants - Constant values for the Aisles Table
 * <p>
 * The Aisles table is used to represent a location withint a shop/store into
 * which products can be found.
 */
@SuppressWarnings("WeakerAccess")
public class DBAislesTableConstants {
    /**
     * AISLES TABLE
     * ID(INT) KEY/PRIMARY INDEX
     * AISLESHOPREF(INT) Shop this aisle belongs to
     * AISLEORDER(INT) Order of the aisle within the shop
     * AISLENAME(TEXT) Name of the aisle/location
     */
    public static final String AISLES_TABLE = "aisles";

    /**
     * _id (aka aisles_id)(INT) Key/primary index
     */
    public static final String AISLES_ID_COL = SQLSTD_ID;
    /**
     * The constant AISLES_ID_COL_FULL.
     */
    public static final String AISLES_ID_COL_FULL = AISLES_TABLE +
            SQLPERIOD +
            AISLES_ID_COL;
    /**
     * The constant AISLES_ALTID_COL.
     */
    public static final String AISLES_ALTID_COL = AISLES_TABLE + SQLSTD_ID;
    /**
     * The constant AISLES_ALTID_COL_FULL.
     */
    @SuppressWarnings("unused")
    public static final String AISLES_ALTID_COL_FULL = AISLES_TABLE +
            SQLPERIOD +
            AISLES_ALTID_COL;
    /**
     * The constant AISLESIDCOL.
     */
    public static final DBColumn AISLESIDCOL = new DBColumn(true);

    /**
     * aisleshopref - shop that owns this aisle (1 owner)
     */
    public static final String AISLES_SHOPREF_COL = "aisleshopref";
    /**
     * The constant AISLES_SHOPREF_COL_FULL.
     */
    public static final String AISLES_SHOPREF_COL_FULL = AISLES_TABLE +
            SQLPERIOD +
            AISLES_SHOPREF_COL;
    /**
     * The constant AISLES_SHOPREF_TYPE.
     */
    public static final String AISLES_SHOPREF_TYPE = SQLINTEGER;
    /**
     * The constant AISLES_SHOPREF_PRIMARY_INDEX.
     */
    public static final Boolean AISLES_SHOPREF_PRIMARY_INDEX = false;
    /**
     * The constant AISLESSHOPREFCOL.
     */
    public static final DBColumn AISLESSHOPREFCOL = new DBColumn(AISLES_SHOPREF_COL,
            AISLES_SHOPREF_TYPE,
            AISLES_SHOPREF_PRIMARY_INDEX,
            ""
    );

    /**
     * aisleorder - order of the aisle within the shop
     */
    public static final String AISLES_ORDER_COL = "aisleorder";
    /**
     * The constant AISLES_ORDER_COL_FULL.
     */
    public static final String AISLES_ORDER_COL_FULL = AISLES_TABLE +
            SQLPERIOD +
            AISLES_ORDER_COL;
    /**
     * The constant AISLES_ORDER_TYPE.
     */
    public static final String AISLES_ORDER_TYPE = SQLINTEGER;
    /**
     * The constant AISLES_ORDER_PRIMARY_INDEX.
     */
    public static final Boolean AISLES_ORDER_PRIMARY_INDEX = false;
    /**
     * The constant AISLESORDERCOL.
     */
    public static final DBColumn AISLESORDERCOL = new DBColumn(AISLES_ORDER_COL,
            AISLES_ORDER_TYPE,
            AISLES_ORDER_PRIMARY_INDEX,
            DEFAULTORDER
    );


    /**
     * aislename - name of the aisle
     */
    public static final String AISLES_NAME_COL = "aislename";
    /**
     * The constant AISLES_NAME_COL_FULL.
     */
    public static final String AISLES_NAME_COL_FULL = AISLES_TABLE +
            SQLPERIOD +
            AISLES_NAME_COL;
    /**
     * The constant AISLES_NAME_TYPE.
     */
    public static final String AISLES_NAME_TYPE = SQLTEXT;
    /**
     * The constant AISLES_NAME_PRIMARY_INDEX.
     */
    public static final Boolean AISLES_NAME_PRIMARY_INDEX = false;
    /**
     * The constant AISLESNAMECOL.
     */
    public static final DBColumn AISLESNAMECOL = new DBColumn(AISLES_NAME_COL,
            AISLES_NAME_TYPE,
            AISLES_NAME_PRIMARY_INDEX,
            ""
    );
    /**
     * Create the Array od DBcolumns ready for the DBTable
     */
    public static final ArrayList<DBColumn> AISLESCOLS = new ArrayList<>(Arrays.asList(AISLESIDCOL,
            AISLESSHOPREFCOL,
            AISLESORDERCOL,
            AISLESNAMECOL));
    /**
     * Finally create the DBTable
     */
    public static final DBTable AISLESTABLE = new DBTable(AISLES_TABLE,AISLESCOLS);
    public static final DBIndex AISLESSHOPREFINDEX = new DBIndex(
            AISLES_TABLE+AISLES_SHOPREF_COL+"index",
            AISLESTABLE,
            AISLESSHOPREFCOL,
            true,
            false
    );
    public static final String AISLESMAXORDERCOLUMN = "maxorder";
}
