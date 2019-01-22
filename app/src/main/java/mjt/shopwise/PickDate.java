package mjt.shopwise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * Pickdate Class - methods to contruct, prepare, invoke (show) and interrogate
 *                  a date picker.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class PickDate {

    // Define INTENT EXTRA keys
    static final String
            INTENTEXTRA_SCRPERCENTAGE = "iextra_screenpercent",
            INTENTEXTRA_TITLE = "iextra_title",
            INTENTEXTRA_INITIALDATE = "iextra_initialdate",
            INTENTEXTRA_TITLEBACKGROUNDCOLOUR =
                    "iextra_titlebckgrndcolour",
            INTENTEXTRA_TITLEBACKGROUNDCOLOURSET =
                    "iextra_titlebckgrndcolourset",
            INTENTEXTRA_TITLETEXTCOLOUR = "iextra_titletextcolour",
            INTENTEXTRA_TITLETEXTCOLOURSET = "iextra_titlecolourset",
            INTENTEXTRA_OUTERBACKGROUNDCOLOURSET =
                    "iextra_outerbackgroundcolourset",
            INTENTEXTRA_OUTERBACKGROUNDCOLOUR =
                    "iextra_outerbackgroundcolour",
            INTENTEXTRA_INNERBACKGROUNDCOLOURSET =
                    "iextra_innerbackgroundcolourset",
            INTENTEXTRA_INNERBACKGROUNDCOLOUR =
                    "iextra_innerbackgroundcolour",
            INTENTEXTRA_INMONTHCELLTEXTCOLOURSET =
                    "iextra_inmonthcelltextcolourset",
            INTENTEXTRA_GRIDHDRTEXTXOLOURSET = "iextra_gridhdrcolourset",
            INTENTEXTRA_GRIDHDRTEXTCOLOUR = "iextra_gridhdrcolour",
            INTENTEXTRA_INMONTHCELLTEXTCOLOUR =
                    "iextra_inmonthcelltextcolour",
            INTENTEXTRA_DIMMEDCELLTEXTCOLOURSET =
                    "iextra_dimmedcelltextcolourset",
            INTENTEXTRA_DIMMEDCELLTEXTCOLOUR =
                    "iextra_dimmedcelltextcolour",
            INTENTEXTRA_SELECTEDCELLTEXTCOLOURSET =
                    "iextra_selectedcelltextcolourset",
            INTENTEXTRA_SELECTEDCELLTEXTCOLOUR =
                    "iextra_selecteccelltextcolour",
            INTENTEXTRA_SELECTEDCELLOVALCOLOURSET =
                    "iextra_selecteddateovalcolourset",
            INTENTEXTRA_SELECTEDCELLOVALCOLOUR =
                    "iextra_selectedovalcolour",
            INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOURSET =
                    "iextra_selectedcellbackgroundcolourset",
            INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOUR =
                    "iextra_selectedcellbackgroundcolour",
            INTENTEXTRA_CELLBORDERCOLOURSET = "iextra_cellbordercolourset",
            INTENTEXTRA_CELLBORDERCOLOUR = "iextra_cellbordercolour",
            INTENTEXTRA_CELLBORDERWIDTHSET = "iextra_cellborderwidthset",
            INTENTEXTRA_CELLBORDERWIDTH = "iextra_cellborderwidth",
            INTENTEXTRA_SELECTEDCELLHIGHLIGHTSTATE = "iextra_disableoval",
            INTENTEXTRA_DATEDISPLAYCOLOURSET = "iextra_datedisplaycolourset",
            INTENTEXTRA_DATEDISPLAYCOLOUR = "iextra_datedisplaycolour",
            INTENTEXTRA_HEIGHTTOWIDTHRATIOSET = "iextra_heighttowidthratioset",
            INTENTEXTRA_HEIGHTTOWIDTHRATIO = "iextra_heighttowidthratio",
                    x = ""
                    ;
    public static final int PICKDATE_RESULTCODE = 111;

    private static long mSelectedDate = -1;
    private transient Context mContext;
    private transient int mScrPercent;
    private transient String pd_title;

    private int mTitleBackgroundColour;
    private boolean mTitleBackgroundColourSet = false;
    private int mTitleTextColour;
    private boolean mTitleTextColourSet = false;
    private int mOuterBackgroundColour;
    private boolean mOuterBackgroundColourSet = false;
    private int mInnerBackgroundColour;
    private boolean mInnerBackgroundColourSet = false;
    private int mGridHdrTextColour;
    private boolean mGridHdrTextColourSet = false;
    private int mInMonthCellTextColour;
    private boolean mInMonthCellTextColourSet = false;
    private int mNotInMonthCellTextColour;
    private boolean mNotInMonthCellTextColourSet = false;
    private int mSelectedCellTextColour;
    private boolean mSelectedCellTextColourSet = false;
    private int mSelectedCellHightLightColour;
    private boolean mSelectedCellHighlightColourSet = false;
    private boolean mSelectedCellBackgrndColourSet = false;
    private int mSelectedCellBackgrndColour;
    private int mCellBorderColour;
    private boolean mCellBorderColourSet = false;
    private int mCellBorderWidth;
    private boolean mCellBorderWidthSet = false;
    private boolean mSelectedCellHighlight = true;
    private boolean mDateDisplayColourSet = false;
    private int mDateDisplayColour;
    private float mHeightToWidthRatio = 0.75f;
    private boolean mHeightToWidthRatioSet = false;


    /**************************************************************************
     * Most basic Constructor
     * @param context       The context from the activity that will use the
     *                      PickDate object.
     */
    public PickDate(Context context) {
        initPickDate(context,
                System.currentTimeMillis(),
                75
        );
    }

    /**************************************************************************
     * Basic constructor using defaults but allowing initial date to be set
     * @param context       The context from the activity that will use the
     *                      PickDate object.
     * @param initial_date  The date as a long (timestamp)
     */
    public PickDate(Context context,
                    long initial_date) {
        initPickDate(context,
                initial_date,
                75
        );
    }

    /**
     * Intermediate constructor allowing initial date and percentages of screen
     *  size to be set.
     * @param context       The context from the activity that will use the
     *                      PickDate object.
     * @param initial_date  The initial date as a long (timestamp)
     * @param scrpercent    The percentage of the usable screen height to use
     *                      !Note orientation is considered
     */
    public PickDate(Context context,
                    long initial_date,
                    int scrpercent) {
        initPickDate(context,
                initial_date,
                scrpercent
        );
    }

    /**************************************************************************
     * Construct the Pickdate object (invoked by the various constructors)
     * @param context       The context from the activity that will use the
     *                      PickDate object.
     * @param date          The initial date as a long (timestamp)
     * @param scrpercent    The percentage of the usable screen height to use
     */
    private void initPickDate(Context context,
                              long date,
                              int scrpercent) {
        this.mContext = context;
        mSelectedDate = date;
        this.mScrPercent = scrpercent;
    }

    /**************************************************************************
     * Set the title to be used by the DatePicker
     * @param title     The title as a string. If empty string, null
     *                  or not used (null) then the
     *                  default title is used.
     */
    public void setTitle(String title) {
            pd_title = title;
    }


    /**************************************************************************
     * Invoke the Date Picker Activity after setting up the various intent
     * extras used for customisation of the Date Picker
     * @param callingactivity   The invoking/calling activity
     */
    public void show(Activity callingactivity) {

        Intent intent = new Intent(mContext, PickDateActivity.class);
        // Add intent extra for Title if provided
        if (pd_title != null) {
            intent.putExtra(INTENTEXTRA_TITLE, pd_title);
        }

        // Add IntentExtra title background colour flag, if the flag is true
        // add the colour to be used.
        intent.putExtra(INTENTEXTRA_TITLEBACKGROUNDCOLOURSET,
                mTitleBackgroundColourSet
        );
        if (mTitleBackgroundColourSet) {
            intent.putExtra(INTENTEXTRA_TITLEBACKGROUNDCOLOUR,
                    mTitleBackgroundColour
            );
        }

        // Add IntentExtra for title text colour flag and if the flag is true
        // add the colour to be used
        intent.putExtra(INTENTEXTRA_TITLETEXTCOLOURSET,
                mTitleBackgroundColourSet);
        if (mTitleTextColourSet) {
            intent.putExtra(INTENTEXTRA_TITLETEXTCOLOUR,
                    mTitleTextColour);
        }

        // Add IntentExtra for outer background colour if set to be used
        intent.putExtra(INTENTEXTRA_OUTERBACKGROUNDCOLOURSET,
                mOuterBackgroundColourSet);
        if (mOuterBackgroundColourSet) {
            intent.putExtra(INTENTEXTRA_OUTERBACKGROUNDCOLOUR,
                    mOuterBackgroundColour);
        }

        // Add IntentExtra for inner background colour is set to be used
        intent.putExtra(INTENTEXTRA_INNERBACKGROUNDCOLOURSET,
                mInnerBackgroundColourSet);
        if (mInnerBackgroundColourSet) {
            intent.putExtra(INTENTEXTRA_INNERBACKGROUNDCOLOUR,
                    mInnerBackgroundColour);
        }

        // Add IntentExtra for dategrid headings text colour
        intent.putExtra(INTENTEXTRA_GRIDHDRTEXTXOLOURSET,
                mGridHdrTextColourSet);
        if (mGridHdrTextColourSet) {
            intent.putExtra(INTENTEXTRA_GRIDHDRTEXTCOLOUR,
                    mGridHdrTextColour);
        }

        // Add IntentExtra for notinmoth cell text colour
        intent.putExtra(INTENTEXTRA_DIMMEDCELLTEXTCOLOURSET,
                mNotInMonthCellTextColourSet);
        if (mNotInMonthCellTextColourSet) {
            intent.putExtra(INTENTEXTRA_DIMMEDCELLTEXTCOLOUR,
                    mNotInMonthCellTextColour);
        }

        // Add IntentExtra for inmonth cell text colour
        intent.putExtra(INTENTEXTRA_INMONTHCELLTEXTCOLOURSET,
                mInMonthCellTextColourSet);
        if (mInMonthCellTextColourSet) {
            intent.putExtra(INTENTEXTRA_INMONTHCELLTEXTCOLOUR,
                    mInMonthCellTextColour);
        }

        // Add intent for selected cell text colour
        intent.putExtra(INTENTEXTRA_SELECTEDCELLTEXTCOLOURSET,
                mSelectedCellTextColourSet);
        if (mSelectedCellTextColourSet) {
            intent.putExtra(INTENTEXTRA_SELECTEDCELLTEXTCOLOUR,
                    mSelectedCellTextColour);
        }

        // Add IntentExtra for selected cell hightlight colour
        intent.putExtra(INTENTEXTRA_SELECTEDCELLOVALCOLOURSET,
                mSelectedCellHighlightColourSet);
        if (mSelectedCellHighlightColourSet) {
            intent.putExtra(INTENTEXTRA_SELECTEDCELLOVALCOLOUR,
                    mSelectedCellHightLightColour);
        }

        // Add IntentExtra for selected cell background colour
        intent.putExtra(INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOURSET,
                mSelectedCellBackgrndColourSet);
        if (mSelectedCellBackgrndColourSet) {
            intent.putExtra(INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOUR,
                    mSelectedCellBackgrndColour);
        }

        // Add IntentExtra for cell borders
        intent.putExtra(INTENTEXTRA_CELLBORDERCOLOURSET,
                mCellBorderColourSet);
        intent.putExtra(INTENTEXTRA_CELLBORDERWIDTHSET,
                mCellBorderWidthSet);
        if (mCellBorderWidthSet && mCellBorderColourSet) {
            intent.putExtra(INTENTEXTRA_CELLBORDERCOLOUR,
                    mCellBorderColour);
            intent.putExtra(INTENTEXTRA_CELLBORDERWIDTH,
                    mCellBorderWidth);
        }

        // Add IntentExtra for Selected Cell Highlight
        intent.putExtra(INTENTEXTRA_SELECTEDCELLHIGHLIGHTSTATE,
                mSelectedCellHighlight);

        intent.putExtra(INTENTEXTRA_SCRPERCENTAGE, mScrPercent);
        intent.putExtra(INTENTEXTRA_INITIALDATE, mSelectedDate);

        // Add IntentExtra for the DateDisplay text colour, if supplied
        intent.putExtra(INTENTEXTRA_DATEDISPLAYCOLOURSET,
                mDateDisplayColourSet);
        if (mDateDisplayColourSet) {
            intent.putExtra(INTENTEXTRA_DATEDISPLAYCOLOUR,
                    mDateDisplayColour);
        }

        // Add IntentExtra for the Height to Width Ratio
        intent.putExtra(INTENTEXTRA_HEIGHTTOWIDTHRATIOSET,
                mHeightToWidthRatioSet);
        if (mHeightToWidthRatioSet) {
            intent.putExtra(INTENTEXTRA_HEIGHTTOWIDTHRATIO,
                    mHeightToWidthRatio);
        }

        callingactivity.startActivityForResult(intent, PICKDATE_RESULTCODE);

    }

    /**************************************************************************
     * Set the TitleBackground Colour
     * Note! will set entire bacckground colour unless outer and or
     * inner background colours are set
     * @param titlecolour   argb colour
     */
    public void setTitleBackgroundColour(int titlecolour) {
        mTitleBackgroundColour = titlecolour;
        mTitleBackgroundColourSet = true;
    }

    /**************************************************************************
     * Set the Title Text Colour (doesn't work)
     * @param titletextcolour colour for the title text
     */
    public void setTitletextColour(int titletextcolour) {
        mTitleTextColour = titletextcolour;
        mTitleTextColourSet = true;
    }

    /**************************************************************************
     * Set the colour of the outer background
     * @param outerbackgroundcolour colour of the outer background
     */
    public void setOuterBackgroundColour(int outerbackgroundcolour) {
        mOuterBackgroundColour = outerbackgroundcolour;
        mOuterBackgroundColourSet = true;
    }

    /**************************************************************************
     * Set the colour of the inner background
     * @param innerbackgroundcolour colour of the inner bacckground
     */
    public void setInnerBackGroundColour(int innerbackgroundcolour) {
        mInnerBackgroundColour = innerbackgroundcolour;
        mInnerBackgroundColourSet = true;
    }

    public void setDateGridHeadingsTextColour(int dategridheadingstextcolour) {
        mGridHdrTextColour = dategridheadingstextcolour;
        mGridHdrTextColourSet = true;
    }

    /**************************************************************************
     * Set the colour of the text for cells that are in the current month
     * @param inmonthcelltextcolour colour of the text
     */
    public void setInMonthCellTextColour(int inmonthcelltextcolour) {
        mInMonthCellTextColour = inmonthcelltextcolour;
        mInMonthCellTextColourSet = true;

    }

    /**************************************************************************
     * Set the text colour for cells that are not in the current month
     * @param notinmonthcelltextcolour colour of the text
     */
    public void setNotInMonthCellTextColour(int notinmonthcelltextcolour) {
        mNotInMonthCellTextColour = notinmonthcelltextcolour;
        mNotInMonthCellTextColourSet = true;
    }

    /**************************************************************************
     * Set the text colour of the currently selected cell
     * @param selectedcelltextcolour colour of the text
     */
    public void setSelectedCellTextColour(int selectedcelltextcolour) {
        mSelectedCellTextColour = selectedcelltextcolour;
        mSelectedCellTextColourSet = true;
    }

    /**************************************************************************
     *
     * @param selectedcellhightlightcolour The colour with which to highlight
     *                                     the selected cell integer as argb
     */
    public void setSelectedcellHightColour(int selectedcellhightlightcolour) {
        mSelectedCellHightLightColour = selectedcellhightlightcolour;
        mSelectedCellHighlightColourSet = true;
    }

    public void setSelectedCellBackgroundColour(int selectedcellbackgrndcolour) {
        mSelectedCellBackgrndColour = selectedcellbackgrndcolour;
        mSelectedCellBackgrndColourSet = true;
    }

    /**************************************************************************
     * Set the border colour and width of all cells
     * @param cellbordercolour  border colour
     * @param cellborderwidth   border width
     */
    public void setCellBorder(int cellbordercolour, int cellborderwidth) {
        mCellBorderColour = cellbordercolour;
        mCellBorderWidth = cellborderwidth;
        mCellBorderColourSet = true;
        mCellBorderWidthSet = true;
    }

    /**************************************************************************
     * Show or Hide the Selected cell background image highlight (circle)
     * @param selectedcellhighlight true to show, false to hide
     */
    public void showSelectedCellHighlight(boolean selectedcellhighlight) {
        mSelectedCellHighlight = selectedcellhighlight;
    }

    /**************************************************************************
     * Set the text colour of the DisplayDate
     * @param datedisplaytextcolour The colour with which to set the display
     *                              date text, integer as argb.
     */
    public void setDateDisplayTextColour(int datedisplaytextcolour) {
        mDateDisplayColour = datedisplaytextcolour;
        mDateDisplayColourSet = true;
    }

    public void alterHeightToWidthRatio(float heighttowidthratio) {
        mHeightToWidthRatio = heighttowidthratio;
        mHeightToWidthRatioSet = true;
    }

    /**************************************************************************
     * Returns the last date that was selected
     * @return date as long timestamp
     */
    public long getSelectedDate() {
        return mSelectedDate;
    }

    /**************************************************************************
     * Set the selected date, for external use by PickDateActivity
     * @param newdate The date, as a long timestamp, with which to set the
     *                date before showing the Date Pikcer.
     */
    static void setSelectedDate(long newdate) {
        mSelectedDate = newdate;
    }
}
