package mjt.shopwise;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Pickdate Activity - Display a Dialog for selecting a Date
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PickDateActivity extends Activity {

    public static final SimpleDateFormat PDSDF =
            new SimpleDateFormat("EE d MMM yyyy", Locale.getDefault());
    private static final Long oneday = (long) 1000 * 60 * 60 * 24;

    Context context;

    String pd_title = "";
    private final Calendar calendar = Calendar.getInstance();

    int pd_width;
    float pd_widthpercentageofheight = 0.75f;
    int pd_height;
    int pd_scrpercent;
    int pd_basecellcolour;
    int pd_dimmedcellcolour;
    int pd_highlightedcellcolour;
    int pd_highlightedcellhighlightcolour;
    int pd_cellbordercolour;
    int pd_selectedcellbordercolour;    //TODO allow the selected cell border to be changed
    int pd_selectedcellborderwidth;     //TODO allow the selected cell border to be changed
    int pd_selectedcellbackgrndcolour;
    boolean pd_selectedcellbackgrndcolourflag = false;
    int pd_cellborderwidth;
    Drawable pd_normalcell;
    Drawable pd_selectedcell;
    GradientDrawable pd_selectedcellhighlight;
    GradientDrawable pd_selectedcellborder;
    GradientDrawable pd_normalcellborder;
    private long pd_selecteddate;
    final float pd_displaydatetext_multiplier = 0.45f; //TODO allow percentage of largest text to be adjusted
    final float datecelltext_multiplier = 0.35f;
    int datecells_adjusted_height;
    float datecells_textsize;
    float displaydate_textsize;
    int pd_adjustbuttons_height;
    int pd_datecellheadings_height;
    int pd_okbutton_height;


    TextView pd_displaydate;
    LinearLayout pd_outerview;
    LinearLayout pd_innerview;
    LinearLayout pd_datedisplay;
    LinearLayout pd_adjustbuttons;
    LinearLayout pd_datecellheadings;
    final Button[] adjustbuttons = new Button[4];
    LinearLayout datecells_row1;
    LinearLayout datecells_row2;
    LinearLayout datecells_row3;
    LinearLayout datecells_row4;
    LinearLayout datecells_row5;
    LinearLayout datecells_row6;
    private final TextView[][] dategrid = new TextView[6][7];
    private final TextView[] dategridhdrs = new TextView[7];
    LinearLayout pd_okbutton;
    Button okbutton;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickdate_layout);
        context = this;

        // Get the width percentage to be applied if provided
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_HEIGHTTOWIDTHRATIOSET, false)) {
            pd_widthpercentageofheight =
                    this.getIntent().getFloatExtra(
                            PickDate.INTENTEXTRA_HEIGHTTOWIDTHRATIO,
                            0.75f
                    );
        }
        // Set the overall size to be used based upon the devices screen dimensions
        setOverallSize();

        // Get the various View id's
        pd_outerview = this.findViewById(R.id.pickdate_outer);
        pd_innerview = this.findViewById(R.id.pickdate_inner);
        pd_datedisplay =
                this.findViewById(
                        R.id.pickdate_datedisplaylayout);
        pd_displaydate =
                this.findViewById(
                        R.id.pickdate_datedisplaytext);

        pd_adjustbuttons = this.findViewById(R.id.pickdate_adjustbuttonslayout);
        pd_datecellheadings = this.findViewById(R.id.pickdate_columnheaders);


        datecells_row1 = this.findViewById(R.id.pickdate_r1);
        okbutton = this.findViewById(R.id.pickdate_okbutton);
        pd_normalcell = ContextCompat.getDrawable(context,R.drawable.pickdatecell);
        LayerDrawable normalcellld = (LayerDrawable) pd_normalcell;
        pd_normalcellborder = (GradientDrawable) normalcellld.findDrawableByLayerId(R.id.normalcellborder);
        pd_selectedcell = ContextCompat.getDrawable(context,R.drawable.selecteddatecell);
        //LayerDrawable ld = (LayerDrawable) ContextCompat.getDrawable(this,R.drawable.selecteddatecell);
        LayerDrawable selectedcellld = (LayerDrawable) pd_selectedcell;
        pd_selectedcellhighlight = (GradientDrawable) selectedcellld.findDrawableByLayerId(R.id.cellhighlight);
        pd_selectedcellborder = (GradientDrawable) selectedcellld.findDrawableByLayerId(R.id.cellborder);
        adjustbuttons[0] = this.findViewById(R.id.month_subtract);
        adjustbuttons[1] = this.findViewById(R.id.month_add);
        adjustbuttons[2] = this.findViewById(R.id.year_subtract);
        adjustbuttons[3] = this.findViewById(R.id.year_add);

        pd_okbutton = this.findViewById(R.id.pickdate_okbutton_layout);

        // Initialise the Dates grid and adjust the height and width
        // and also set the garvity to CENTER
        // Note setting cell colours MUST be done after as initDateGrid
        // sets the cell colours according to the theme
        initDateGrid();

        // Set the outerview (LinearLayout) size according to the
        FrameLayout.LayoutParams vfl =
                (FrameLayout.LayoutParams) pd_outerview.getLayoutParams();
        vfl.height = pd_height;
        vfl.width = pd_width;
        vfl.gravity = Gravity.CENTER;
        pd_outerview.setLayoutParams(vfl);

        // Add Runnable setting the Date Display Text Size
        // whenever the layout has completed being built
        pd_datedisplay.post(new Runnable() {
            @Override
            public void run() {
                pd_displaydate.measure(0,0);
                displaydate_textsize =
                        pd_displaydate.getHeight() *
                                pd_displaydatetext_multiplier;
                pd_displaydate.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        displaydate_textsize);
            }
        });

        // Add Runnnable setting the date Adjust Buttons Height and Text size
        // whenever the layout they are in has completed being built
        pd_adjustbuttons.post(new Runnable() {
            @Override
            public void run() {
                pd_adjustbuttons.measure(0,0);
                pd_adjustbuttons_height =
                        Math.round(pd_adjustbuttons.getHeight() * 0.75f);

                for (Button adjbutton : adjustbuttons) {
                    ViewGroup.LayoutParams adjbuttonparamas =
                            adjbutton.getLayoutParams();
                    adjbuttonparamas.height = pd_adjustbuttons_height;
                    adjbutton.setLayoutParams(adjbuttonparamas);
                    adjbutton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            ((float)pd_adjustbuttons_height *
                                    datecelltext_multiplier
                            ));
                }
            }
        });

        // Add Runnable setting the datecell headings Text Size
        // whenever the layout they are in has been built
        pd_datecellheadings.post(new Runnable() {
            @Override
            public void run() {
                pd_datecellheadings.measure(0,0);
                pd_datecellheadings_height =
                        Math.round(pd_datecellheadings.getHeight());
                for (TextView dghdrs : dategridhdrs) {
                    dghdrs.setHeight(pd_datecellheadings_height);
                    dghdrs.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            ((float)pd_datecellheadings_height *
                                    datecelltext_multiplier
                            ));
                }
            }
        });

        // Add Runnable setting the OK button's height and Text Size
        // whenever the layout has been built
        pd_okbutton.post(new Runnable() {
            @Override
            public void run() {
                pd_okbutton.measure(0,0);
                pd_okbutton_height =
                        Math.round(pd_okbutton.getHeight() * 0.75f);
                ViewGroup.LayoutParams okbuttonparams =
                        okbutton.getLayoutParams();
                okbuttonparams.height = pd_okbutton_height;
                okbutton.setLayoutParams(okbuttonparams);
                okbutton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        ((float)pd_okbutton_height *
                                datecelltext_multiplier
                        ));
            }
        });

        setTitle();
        setOuterBackgroundColour();
        setInnerBackgroundColour();
        setDateGridHeadingsTextColour();
        setDateDisplay();
        setCellCustomAttributes();

        // Set the Date to be displayed, defaulting to the current date (should always be provided)
        pd_selecteddate = this.getIntent().getLongExtra(
                PickDate.INTENTEXTRA_INITIALDATE, System.currentTimeMillis()
        );
        calendar.setTimeInMillis(pd_selecteddate);


        // Uses the screen size to set textsizes
        final float displaydate_text_size = (float) pd_height / 20;
        okbutton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(displaydate_text_size * (float) 0.55));

        // Determine the height to be used to make date cells square
        // Note only if width of cell is smaller than height
        datecells_row1.measure(0,0);
        int datecells_height = datecells_row1.getMeasuredHeight();
        int datecells_width = datecells_row1.getMeasuredWidth();
        float pixelsperdp = ((float) pd_width / (float) datecells_width);
        datecells_adjusted_height = Math.round(pixelsperdp * (float) datecells_height);
        int singledatecellwidth = Math.round((float) datecells_width / (float) 7);
        if (datecells_height > singledatecellwidth) {
            datecells_adjusted_height = datecells_height;
        }
        datecells_textsize = ((float) datecells_adjusted_height * datecelltext_multiplier);

        pd_datedisplay.measure(0,0);
        displaydate_textsize = ((float) pd_datedisplay.getMeasuredHeight() * pd_displaydatetext_multiplier);

        setLinearLayoutHeight(datecells_row1,datecells_adjusted_height);
        setLinearLayoutHeight(datecells_row2,datecells_adjusted_height);
        setLinearLayoutHeight(datecells_row3,datecells_adjusted_height);
        setLinearLayoutHeight(datecells_row4,datecells_adjusted_height);
        setLinearLayoutHeight(datecells_row5,datecells_adjusted_height);
        setLinearLayoutHeight(datecells_row6,datecells_adjusted_height);

        populateDateGrid(calendar);
    }


    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }


    public void okbuttonclick(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Apply the date
        PickDate.setSelectedDate(pd_selecteddate);
    }

    // Initialise the dategrid with the respective textView id's
    private void initDateGrid() {
        // Top Row
        datecells_row1 = this.findViewById(R.id.pickdate_r1);
        dategrid[0][0] = this.findViewById(R.id.pickdate_r1c1);
        pd_basecellcolour = dategrid[0][0].getCurrentTextColor();
        pd_dimmedcellcolour = pd_basecellcolour & 0x99ffffff;
        pd_highlightedcellcolour = pd_basecellcolour & 0x99ffffff;
        dategrid[0][1] = this.findViewById(R.id.pickdate_r1c2);
        dategrid[0][2] = this.findViewById(R.id.pickdate_r1c3);
        dategrid[0][3] = this.findViewById(R.id.pickdate_r1c4);
        dategrid[0][4] = this.findViewById(R.id.pickdate_r1c5);
        dategrid[0][5] = this.findViewById(R.id.pickdate_r1c6);
        dategrid[0][6] = this.findViewById(R.id.pickdate_r1c7);
        // 2nd Row
        datecells_row2 = this.findViewById(R.id.pickdate_r2);
        dategrid[1][0] = this.findViewById(R.id.pickdate_r2c1);
        dategrid[1][1] = this.findViewById(R.id.pickdate_r2c2);
        dategrid[1][2] = this.findViewById(R.id.pickdate_r2c3);
        dategrid[1][3] = this.findViewById(R.id.pickdate_r2c4);
        dategrid[1][4] = this.findViewById(R.id.pickdate_r2c5);
        dategrid[1][5] = this.findViewById(R.id.pickdate_r2c6);
        dategrid[1][6] = this.findViewById(R.id.pickdate_r2c7);
        // 3rd Row
        datecells_row3 = this.findViewById(R.id.pickdate_r3);
        dategrid[2][0] = this.findViewById(R.id.pickdate_r3c1);
        dategrid[2][1] = this.findViewById(R.id.pickdate_r3c2);
        dategrid[2][2] = this.findViewById(R.id.pickdate_r3c3);
        dategrid[2][3] = this.findViewById(R.id.pickdate_r3c4);
        dategrid[2][4] = this.findViewById(R.id.pickdate_r3c5);
        dategrid[2][5] = this.findViewById(R.id.pickdate_r3c6);
        dategrid[2][6] = this.findViewById(R.id.pickdate_r3c7);
        // 4th Row
        datecells_row4 = this.findViewById(R.id.pickdate_r4);
        dategrid[3][0] = this.findViewById(R.id.pickdate_r4c1);
        dategrid[3][1] = this.findViewById(R.id.pickdate_r4c2);
        dategrid[3][2] = this.findViewById(R.id.pickdate_r4c3);
        dategrid[3][3] = this.findViewById(R.id.pickdate_r4c4);
        dategrid[3][4] = this.findViewById(R.id.pickdate_r4c5);
        dategrid[3][5] = this.findViewById(R.id.pickdate_r4c6);
        dategrid[3][6] = this.findViewById(R.id.pickdate_r4c7);
        // 5th Row
        datecells_row5 = this.findViewById(R.id.pickdate_r5);
        dategrid[4][0] = this.findViewById(R.id.pickdate_r5c1);
        dategrid[4][1] = this.findViewById(R.id.pickdate_r5c2);
        dategrid[4][2] = this.findViewById(R.id.pickdate_r5c3);
        dategrid[4][3] = this.findViewById(R.id.pickdate_r5c4);
        dategrid[4][4] = this.findViewById(R.id.pickdate_r5c5);
        dategrid[4][5] = this.findViewById(R.id.pickdate_r5c6);
        dategrid[4][6] = this.findViewById(R.id.pickdate_r5c7);
        // Last (6th) row
        datecells_row6 = this.findViewById(R.id.pickdate_r6);
        dategrid[5][0] = this.findViewById(R.id.pickdate_r6c1);
        dategrid[5][1] = this.findViewById(R.id.pickdate_r6c2);
        dategrid[5][2] = this.findViewById(R.id.pickdate_r6c3);
        dategrid[5][3] = this.findViewById(R.id.pickdate_r6c4);
        dategrid[5][4] = this.findViewById(R.id.pickdate_r6c5);
        dategrid[5][5] = this.findViewById(R.id.pickdate_r6c6);
        dategrid[5][6] = this.findViewById(R.id.pickdate_r6c7);

        for (int i=0;i < dategrid.length; i++) {
            for (int ii=0; ii < dategrid[i].length; ii++) {
                dategrid[i][ii].setTag(Integer.toString(ii + (i * dategrid[i].length)));
                dategrid[i][ii].measure(0,0);
                dategrid[i][ii].setHeight(datecells_adjusted_height);
                dategrid[i][ii].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDateCell(v);
                    }
                });
            }
        }

        // get the TextView id's for the dateGrid Column Headings
        dategridhdrs[0] = this.findViewById(R.id.pickdate_c1hdr);
        dategridhdrs[1] = this.findViewById(R.id.pickdate_c2hdr);
        dategridhdrs[2] = this.findViewById(R.id.pickdate_c3hdr);
        dategridhdrs[3] = this.findViewById(R.id.pickdate_c4hdr);
        dategridhdrs[4] = this.findViewById(R.id.pickdate_c5hdr);
        dategridhdrs[5] = this.findViewById(R.id.pickdate_c6hdr);
        dategridhdrs[6] = this.findViewById(R.id.pickdate_c7hdr);

    }

    /*************************************************************************
     * Set the height of a LinearLayout
     * @param ll        The Linearlayout to change
     * @param height    The new height
     */
    private void setLinearLayoutHeight(LinearLayout ll, int height) {
        LinearLayout.LayoutParams llp =
                (LinearLayout.LayoutParams) ll.getLayoutParams();
        llp.height = height;
        ll.setLayoutParams(llp);
    }

    /**************************************************************************
     * Populate the Date Grid based upon the current month
     * The Date Grid always has 6 rows, each with 7 columns for the days of the
     * week.
     * There will alawys be some dates outside of the month, some for the
     * previous month and some for the following month.
     * @param cal   The Calendar set to the currently selected date
     */
    @SuppressLint("SetTextI18n")
    private void populateDateGrid(Calendar cal) {
        pd_displaydate.setText(PDSDF.format(cal.getTimeInMillis()));
        pd_displaydate.setTextSize(TypedValue.COMPLEX_UNIT_PX,displaydate_textsize);
        int selected_dayinmonth = cal.get(Calendar.DAY_OF_MONTH);
        int selected_month = cal.get(Calendar.MONTH);
        // Create working copy of the calendar passed
        Calendar tempcal = calendar;
        // Set working copy to 1st of the month
        tempcal.set(Calendar.DAY_OF_MONTH,1);
        // Adjust the temporary calendar so that it starts on a Monday
        // If the working copy (tempcal) is not the first day of the week then
        // change it to the date as of the first day of the week
        // i.e. subtract the number of days of the week.
        int dayofweek = tempcal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek < 2) {
            long newstart = tempcal.getTimeInMillis() - ((long)  (7 - dayofweek) * oneday);
            tempcal.setTimeInMillis(newstart);
        }
        // Adjust the temporary calendar so that the 1st cell is not the selected day
        // i.e. ensure that some of the previous month's dates are display.
        if (dayofweek == 2)
            tempcal.setTimeInMillis(tempcal.getTimeInMillis() - ((long) 7 * oneday));
        // Adjust the temporary calendar so that the first date is a Monday
        if (dayofweek > 2) {
            long newstart = tempcal.getTimeInMillis() - ((long) (dayofweek - 2) * oneday);
            tempcal.setTimeInMillis(newstart);
        }

        // loop through each cell apply the respective date and setting the
        // respective attributes for:-
        // Days not in the current month, days in the current month and the
        // selected date.
        for (TextView[] aDategrid : dategrid) {
            for (TextView anADategrid : aDategrid) {
                anADategrid.setTextSize(datecells_textsize);
                boolean selectedcell = false;
                anADategrid.setTextColor(pd_basecellcolour);
                // Not in this month so not in month text colour
                if (tempcal.get(Calendar.MONTH) != selected_month) {
                    anADategrid.setTextColor(pd_dimmedcellcolour);
                } else {
                    // If the selected date then use text colour and background
                    if (tempcal.get(Calendar.DAY_OF_MONTH) == selected_dayinmonth) {
                        anADategrid.setTextColor(pd_highlightedcellcolour);
                        ViewCompat.setBackground(anADategrid,pd_selectedcell);
                        // If we want to use an alternative background colour
                        if (pd_selectedcellbackgrndcolourflag) {
                            GradientDrawable gdhighlight = pd_selectedcellhighlight;
                            GradientDrawable gdborder = pd_selectedcellborder;
                            pd_selectedcellborder.setColor(pd_selectedcellbackgrndcolour);
                        }
                        selectedcell = true;
                    }
                }
                if (!selectedcell) {
                    ViewCompat.setBackground(anADategrid,pd_normalcell);
                }
                // Always set the text to the day of the month
                anADategrid.setHeight(datecells_adjusted_height);
                anADategrid.setTextSize(TypedValue.COMPLEX_UNIT_PX,datecells_textsize);
                anADategrid.setText(
                        Integer.toString(
                                tempcal.get(
                                        Calendar.DAY_OF_MONTH
                                )
                        )
                );
                anADategrid.setTag(tempcal.getTimeInMillis());
                // Prepare for next iteration by setting the calendar to the
                // next day
                long nextday =
                        tempcal.getTimeInMillis() + oneday;
                tempcal.setTimeInMillis(nextday);
            }
        }

    }

    /**************************************************************************
     * Set the Overall Size of the Dialog
     * Set overall height and width based upon the given percentage.
     * The height is set to the given percentage.
     * The width is set as a specific percentage of the height.
     * If the calculated width is greater than the screen width
     * the height is reduced to accomodate a width that would
     * be the specific percentage.
     */
    private void setOverallSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        pd_scrpercent =
                this.getIntent().getIntExtra(
                        PickDate.INTENTEXTRA_SCRPERCENTAGE,
                        90
                );
        pd_height =
                Math.round((float) dm.heightPixels *
                        ((float)pd_scrpercent / 100f));
        pd_width =
                Math.round((float)pd_height * pd_widthpercentageofheight);
        if (pd_width > dm.widthPixels) {
            pd_height =
                    Math.round(pd_height *
                            ((float) dm.widthPixels / (float)pd_width));
            pd_width =
                    Math.round((float)pd_height *
                            pd_widthpercentageofheight);
        }
    }

    /**************************************************************************
     * Set the Title attributes if customised
     */
    private void setTitle() {
        // Set the Custom Title if provided
        pd_title = this.getIntent().getStringExtra(
                PickDate.INTENTEXTRA_TITLE
        );
        if (pd_title != null) {
            this.setTitle(pd_title);
        }
        if (pd_title == null || pd_title.length() < 1) {
            this.setTitle("Pick a Date");
        }

        // Set the Title Background Colour if given
        if (getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_TITLEBACKGROUNDCOLOURSET,false)) {
            int passedbckgrndcolor = getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_TITLEBACKGROUNDCOLOUR,0
            );
            this.getWindow().setBackgroundDrawable(
                    argbToColor(passedbckgrndcolor)
            );
        }

    }

    /**************************************************************************
     * Set the Date Display attributes if cusomised
     */
    private void setDateDisplay() {
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_DATEDISPLAYCOLOURSET,false)) {
            pd_displaydate.setTextColor(
                    this.getIntent().getIntExtra(
                            PickDate.INTENTEXTRA_DATEDISPLAYCOLOUR,0
                    )
            );
        }
    }

    /**************************************************************************
     * Set the OuterBackground attributes if customised
     */
    private void setOuterBackgroundColour() {
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_OUTERBACKGROUNDCOLOURSET, false)) {
            pd_outerview.setBackgroundColor(
                    this.getIntent().getIntExtra(
                            PickDate.INTENTEXTRA_OUTERBACKGROUNDCOLOUR,0
                    )
            );
        }
    }

    /**************************************************************************
     * Set the InnerBackground attributes if customised
     */
    private void setInnerBackgroundColour() {
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_INNERBACKGROUNDCOLOURSET, false)) {
            pd_innerview.setBackgroundColor(
                    this.getIntent().getIntExtra(
                            PickDate.INTENTEXTRA_INNERBACKGROUNDCOLOUR,0
                    )
            );
        }
    }

    /**************************************************************************
     * Set the Text Colour of the date grid headings (mon, tue etc)
     */
    private void setDateGridHeadingsTextColour() {
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_GRIDHDRTEXTXOLOURSET,false)) {
            int  colour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_GRIDHDRTEXTCOLOUR,0
            );
            for (TextView dghdrtv : dategridhdrs) {
                dghdrtv.setTextColor(colour);
            }
        }
    }

    /**************************************************************************
     * Set Cell Attributes if customised
     */
    private void setCellCustomAttributes() {
        // Set the text colour to be used, if given for when a cell contains
        // a date that is not in the current month. i.e DIMMED CELL
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_DIMMEDCELLTEXTCOLOURSET,false)) {
            pd_dimmedcellcolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_DIMMEDCELLTEXTCOLOUR,0
            );
        }
        // Set the text colour to be used, if given, for when a cell contains
        // a date that is in the current month. i.e. NORMAL CELL
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_INMONTHCELLTEXTCOLOURSET,false)) {
            pd_basecellcolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_INMONTHCELLTEXTCOLOUR,0
            );
        }
        // Set the text colour to be used, if given, for when a cell contains
        // the date that is set as the selected date. i.e. SELECTED CELL
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_SELECTEDCELLTEXTCOLOURSET,false)) {
            pd_highlightedcellcolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_SELECTEDCELLTEXTCOLOUR,0
            );
        }
        // Set the colour, if given, to be used for the background (circle)
        // used to HIGHLIGHT the SELECTED CELL.
        // If not given then set the colour to be the base/normal cell colour
        // with transparentcy (alpha) applied
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_SELECTEDCELLOVALCOLOURSET,false)) {
            pd_highlightedcellhighlightcolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_SELECTEDCELLOVALCOLOUR,0
            );
            pd_selectedcellhighlight.setColor(pd_highlightedcellhighlightcolour);
        } else {
            pd_selectedcellhighlight.setColor(pd_basecellcolour & 0x55ffffff);
        }
        // Turn the SELECTED CELL HIGHLIGHT off, if given
        // Does this by setting full transparency to the highlight colour
        if (!this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_SELECTEDCELLHIGHLIGHTSTATE,true)) {
            pd_selectedcellhighlight.setColor(pd_highlightedcellhighlightcolour & 0x00ffffff);
        }
        // Set the colour and width of the cell borders (better without borders)
        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_CELLBORDERCOLOURSET, false
        )
                &&
                this.getIntent().getBooleanExtra(
                        PickDate.INTENTEXTRA_CELLBORDERWIDTHSET,false
                )) {
            pd_cellbordercolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_CELLBORDERCOLOUR,0
            );
            pd_cellborderwidth = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_CELLBORDERWIDTH,0
            );
            pd_normalcellborder.setStroke(pd_cellborderwidth,pd_cellbordercolour);
            pd_selectedcellborder.setStroke(pd_cellborderwidth,pd_cellbordercolour);
        }

        if (this.getIntent().getBooleanExtra(
                PickDate.INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOURSET,false)) {
            pd_selectedcellbackgrndcolour = this.getIntent().getIntExtra(
                    PickDate.INTENTEXTRA_SELECTEDCELLBACKGROUNDCOLOUR,0
            );
            pd_selectedcellbackgrndcolourflag = true;
        }
    }

    /**************************************************************************
     * Handle a Date Cell being clicked
     * @param v the View that was clicked
     */
    public void clickDateCell(View v) {
        long thisdatetime =  (Long) v.getTag();
        calendar.setTimeInMillis(thisdatetime);
        pd_selecteddate = calendar.getTimeInMillis();
        PickDate.setSelectedDate(pd_selecteddate);
        populateDateGrid(calendar);
    }

    /**************************************************************************
     * Handle the -Month button being clicked
     * @param v the view that was clicked
     */
    public void subtractMonth(View v) {
        calendar.setTimeInMillis(pd_selecteddate);
        calendar.add(Calendar.MONTH,-1);
        applyChangedDate();
    }

    /**************************************************************************
     * Handle the Month+ button being clicked
     * @param v the view that was clicked
     */
    public void addMonth(View v) {
        calendar.setTimeInMillis(pd_selecteddate);
        calendar.add(Calendar.MONTH,1);
        applyChangedDate();
    }

    /**************************************************************************
     * Handle the -Year button being clicked
     * @param v the view that was clicked
     */
    public void subtractYear(View v) {
        calendar.setTimeInMillis(pd_selecteddate);
        calendar.add(Calendar.YEAR,-1);
        applyChangedDate();
    }

    /**************************************************************************
     * Handle the Year+ button being clicked
     * @param v the view that was clicked
     */
    public void addYear(View v) {
        calendar.setTimeInMillis(pd_selecteddate);
        calendar.add(Calendar.YEAR,1);
        applyChangedDate();
    }

    /**************************************************************************
     * Apply a Changed Date
     */
    private void applyChangedDate() {
        pd_selecteddate = calendar.getTimeInMillis();
        PickDate.setSelectedDate(pd_selecteddate);
        populateDateGrid(calendar);
    }

    /**************************************************************************
     * Convert an argb integer into a ColorDrawable i.e.
     * exract the 4 componants Alpha, Red, Green and Blue and use them to
     * build the ColorDrawable
     * i.e.
     * @param argb  The colour to ve converted
     * @return      The resultant ColorDrawable
     */
    private ColorDrawable argbToColor(int argb) {
        int alphapart = (argb >> 24) & 0xff;
        int redpart = (argb >> 16) & 0xff;
        int greenpart = (argb >> 8) & 0xff;
        int bluepart = argb & 0xff;
        return  new ColorDrawable(Color.argb(
                alphapart,
                redpart,
                greenpart,
                bluepart
        ));
    }
}
