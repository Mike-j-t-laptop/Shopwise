package mjt.shopwise;

public class DBTrigger {

    public static final int TRIGGER_ACTIVATEON_DELETE = 1;
    public static final int TRIGGER_ACTIVATEON_INSERT = 2;
    public static final int TRIGGER_ACTIVATEON_UPDATE = 3;
    public static final int TRIGGER_ACTIVATEON_UPDATEOFCOLUMN = 4;
    public static final int TRIGGER_ACTIVATEWHEN_AFTER = 1;
    public static final int TRIGGER_ACTIVATEWHEN_BEFORE = 2;
    public static final int TRIGGER_ACTIVATEWHEN_INSTEADOF = 3;


    private boolean usable;
    private boolean constructed;
    private String trigger_name;
    private int trigger_activation_when;
    private int trigger_activation_action;
    private DBColumn[] trigger_updateof_columns;
    private DBTable trigger_owningtable;
    private boolean trigger_include_foreachrow;
    private String trigger_whenclause;
    private String trigger_actionsql;
    private String problem_msg;
    private String trigger_create_sql = "";
    private String trigger_drop_sql = "";


    /**
     * Default Empty Constructor,
     * MUST be followed with setters for
     * the Trigger to be usable.
     */
    public DBTrigger() {
        this.usable = false;
        this.constructed = false;
        this.trigger_name = "";
        this.trigger_activation_action = 0;
        this.trigger_activation_when = 0;
        this.trigger_updateof_columns = null;
        this.trigger_owningtable = null;
        this.trigger_include_foreachrow = false;
        this.trigger_whenclause = "";
        this.trigger_actionsql = "";
        this.problem_msg = "\nWDBTRG0001 - Uninitialised - " +
                "Use at least setTriggerName, setTriggerActivationAction" +
                ", setTriggerOwningTable" +
                ", AND setTriggerActionSQL to make the TRIGGER usable."
        ;
    }

    /**
     * Full Constructor
     *
     * @param trigger_name              The name to be given to the Trigger
     * @param trigger_activation_action The action that fires the Trigger
     *                                      Actions can be
     *                                          INSERT, UPDATE, DELETE and
     *                                          UPDATE OF COLUMN
     *                                          use CONSTANTS
     *                                              TRIGGER_ACTIVATEON_????
     *                                                  DELETE, INSERT,
     *                                                  UPDATE or
     *                                                  UPDATE OF <columns>
     *                                                  see 4th arg.
     * @param trigger_activation_when   When to perform the Trigger's actions
     *                                      Can be
     *                                          BEFORE, AFTER or INSTEAD OF
     *
     * @param trigger_updateof_columns  The columns, for UPDATE OF to be fired
     *                                      if not null i.e. 1 column or more
     *                                      then UPDATE OF should be used
     *                                      if not then DBTrigger will be set
     *                                      as unusable (and therefore whole
     *                                          model schema).
     * @param trigger_owningtable       The owning DBTable
     * @param trigger_include_foreachrow    True to include FOR EACH ROW
     * @param trigger_whenclause        The WHEN clause is provided
     * @param trigger_actionsql         The SQL to action within the
     *                                  BEGIN ... END clause i.e the
     *                                  actions the be performed when the
     *                                  Trigger is fired.
     */
    public DBTrigger(String trigger_name,
                     int trigger_activation_action,
                     int trigger_activation_when,
                     DBColumn[] trigger_updateof_columns,
                     DBTable trigger_owningtable,
                     boolean trigger_include_foreachrow,
                     String trigger_whenclause,
                     String trigger_actionsql) {
        this.usable = false;
        this.constructed = false;
        this.trigger_name = trigger_name;
        this.trigger_activation_action = trigger_activation_action;
        this.trigger_activation_when = trigger_activation_when;
        this.trigger_updateof_columns = trigger_updateof_columns;
        this.trigger_owningtable = trigger_owningtable;
        this.trigger_include_foreachrow = trigger_include_foreachrow;
        this.trigger_whenclause = trigger_whenclause;
        this.trigger_actionsql = trigger_actionsql;
        checkDBTriggerIsUseable("FULL CONSTRUCTOR");
    }

    public DBTrigger(String trigger_name,
                     int trigger_activation_action,
                     int trigger_activation_when,
                     DBTable trigger_owningtable,
                     String trigger_actionsql) {
        this(trigger_name,
                trigger_activation_action,
                trigger_activation_when,
                null,
                trigger_owningtable,
                false,
                null,
                trigger_actionsql
        );
    }

    public void setConstructed(boolean constructed) {
        this.constructed = constructed;
    }

    public boolean isConstructed() {
        return constructed;
    }

    public void setTriggeName(String trigger_name) {
        this.trigger_name = trigger_name;
    }

    public String getTriggerName() {
        return trigger_name;
    }

    public void setTriggerActivationAction(int trigger_activation_action) {
        this.trigger_activation_action = trigger_activation_action;
    }

    public int getTriggerActivationActionAsInt() {
        return trigger_activation_action;
    }

    public String getTriggerActivationActionAsString() {
        switch (this.trigger_activation_action) {
            case TRIGGER_ACTIVATEON_DELETE:
                return SQLKWORD.SQLDELETE;
            case TRIGGER_ACTIVATEON_INSERT:
                return SQLKWORD.SQLINSERT;
            case TRIGGER_ACTIVATEON_UPDATE:
                return SQLKWORD.SQLUPDATE;
            case TRIGGER_ACTIVATEON_UPDATEOFCOLUMN:
                if (this.trigger_updateof_columns != null) {
                    String sep =";";
                    StringBuilder columnlist = new StringBuilder();
                    StringBuilder messages = new StringBuilder();
                    for (DBColumn c: trigger_updateof_columns) {
                        columnlist.append(sep);
                        columnlist.append(c.getDBColumnName());
                        sep = ",";
                        if (!c.isDBColumnUsable()) {
                            messages.append(
                                    "\nEDBTRG0002 - Invalid Column " +
                                    c.getDBColumnName() +
                                    " - Unusable reason(s) are :-" +
                                    c.getDBColumnProblemMsg()
                            );
                            this.usable = false;
                        }
                        boolean in_owning_table = false;
                        for (DBColumn tc:
                                this.trigger_owningtable.getTableDBColumns()) {
                            if (tc.getDBColumnName().equals(c.getDBColumnName())) {
                                in_owning_table = true;
                                break;
                            }
                        }
                        if (!in_owning_table) {
                            messages.append(
                                    "\nEDBTRG0003 - Invalid Column " +
                                    c.getDBColumnName() +
                                    " - Not in Trigger's Owning Table " +
                                    trigger_owningtable.getDBTableName()
                            );
                            this.usable = false;
                        }
                    }
                    this.problem_msg = this.problem_msg + messages.toString();
                    return SQLKWORD.SQLUPDATEOF +
                            getTriggerUPDATEOFColumnsAsCSV(
                                    new String[]{"(",")"},
                                    ","
                            );
                }
            default:
                this.usable = false;
                this.problem_msg = this.problem_msg +
                        "\nEDBTRG0004 - Invalid TRIGGER ACTVIATION " +
                        "(must be DELETE, INSERT, UPDATE or " +
                        "UPDATE OF <column>).";
                return "INVALID - NO SUCH TRIGGER ACTIVATION";
        }
    }

    public void setTriggerUPDATEOFColumns(DBColumn[] trigger_updateof_columns) {
        this.trigger_updateof_columns = trigger_updateof_columns;
    }

    public DBColumn[] getTriggerUPDATEOFColumns() {
        return trigger_updateof_columns;
    }

    public String getTriggerUPDATEOFColumnsAsCSV(String[] enclosed_by, String seperator) {
        String sep = "";
        String actual_sep = ",";
        boolean enclose = false;
        if (seperator != null && seperator.length() > 0) {
            actual_sep = seperator;
        }
        StringBuilder sb = new StringBuilder();
        if (enclosed_by != null && enclosed_by.length > 0) {
            sb.append(enclosed_by[0]);
            enclose = true;
        }
        for (DBColumn c: this.trigger_updateof_columns
             ) {
            sb.append(sep);
            sb.append(c.getDBColumnName());
            sep = actual_sep;
        }
        if (enclose) {
            if (enclosed_by.length > 1) {
                sb.append(enclosed_by[1]);
            } else {
                sb.append(enclosed_by[0]);
            }
        }
        return sb.toString();
    }

    public void setTriggerActivationWhen(int trigger_activation_when) {
        this.trigger_activation_when = trigger_activation_when;
    }

    public int getTriggerActivationWhenAsInt() {
        return trigger_activation_when;
    }

    public String getTriggerActiviationWhenAsString() {
        switch (this.trigger_activation_when) {
            case TRIGGER_ACTIVATEWHEN_AFTER:
                return SQLKWORD.SQLAFTER;
            case TRIGGER_ACTIVATEWHEN_BEFORE:
                return SQLKWORD.SQLBEFORE;
            case TRIGGER_ACTIVATEWHEN_INSTEADOF:
                return SQLKWORD.SQLINSTEADOF;
            default:
                return SQLKWORD.SQLAFTER;
        }
    }

    public void setTriggerOwningTable(DBTable trigger_owningtable) {
        this.trigger_owningtable = trigger_owningtable;
    }

    public DBTable getTriggerOwningTableAsDBTable() {
        return trigger_owningtable;
    }

    public String getTriggerOwningTableAsString() {
        return trigger_owningtable.getDBTableName();
    }

    public void setTriggerInclude_FOR_EACH_ROW(boolean trigger_include_foreachrow) {
        this.trigger_include_foreachrow = trigger_include_foreachrow;
    }

    public boolean getTriggerIncludeFOR_EACH_ROW() {
        return trigger_include_foreachrow;
    }

    public boolean isTriggerIncludeFOR_EACH_ROW() {
        return trigger_include_foreachrow;
    }

    public String getTriggerFOR_EACH_ROW() {
        if (this.trigger_include_foreachrow) {
            return SQLKWORD.SQLFOREACHROW;
        }
        return "";
    }

    public void setTriggerWhenClause(String trigger_whenclause) {
        this.trigger_whenclause = trigger_whenclause;
    }

    public String getTrigger_whenclause() {
        if (trigger_whenclause.length() > 0) {
            return SQLKWORD.SQLWHEN + trigger_whenclause;
        }
        return "";
    }

    public String getTriggerCreateSQL() {
        return trigger_create_sql;
    }

    public String getTriggerDropSQL() {
        return trigger_drop_sql;
    }

    public void setTriggerActionSQL(String trigger_actionsql) {
        this.trigger_actionsql = trigger_actionsql;
    }

    public String getTrigger_actionsql() {
        if (trigger_actionsql.length() > 0) {
            String rightTrailingWhitespaceRemoved =
                    trigger_actionsql.replaceAll("\\s+$", "");
            if (!rightTrailingWhitespaceRemoved.substring(
                    rightTrailingWhitespaceRemoved.length() -1).equals(";")
            ) {
                trigger_actionsql = trigger_actionsql + ";";
            }
            return SQLKWORD.SQLBEGIN + trigger_actionsql + SQLKWORD.SQLEND;
        }
        return "";
    }

    public String getProblemmsg() {
        if (problem_msg != null ) {
            return problem_msg;
        }
        return "";
    }

    private boolean checkDBTriggerIsUseable(String caller) {
        this.usable = true;
        if ((this.trigger_name.length() < 1)) {
            this.problem_msg = this.problem_msg +
                    "\nEDBTRG0010 - Invalid Trigger Name - " +
                    "Must be at least 1 character in length. ";
            this.usable = false;
        }
        if (!this.trigger_owningtable.isDBTableUsable()) {
            this.problem_msg = this.problem_msg +
                    "\nEDBTRG0011 - Table " +
                    this.trigger_owningtable.getDBTableName() +
                    " is unusable. Reasons are :-" +
                    this.trigger_owningtable.getAllDBTableProblemMsgs();
            this.usable = false;
        }
        getTriggerActivationActionAsString(); // checks sets and add problem msg
        getTriggerActiviationWhenAsString(); // checks defaults to AFTER if invalid
        if (getTrigger_actionsql().length() < 1) {
            this.problem_msg = this.problem_msg +
                    "\nEDBTRG0012 - No Action to Perform - ";
            this.usable = false;
        }
        buildCreateTriggerSQL();
        return this.usable;
    }

    private void buildCreateTriggerSQL() {
        trigger_create_sql = "CREATE TRIGGER" +
                SQLKWORD.SQLIFNOTEXISTS +
                this.trigger_name +
                getTriggerActiviationWhenAsString() +
                getTriggerActivationActionAsString() +
                SQLKWORD.SQLON +
                this.getTriggerOwningTableAsString() +
                this.getTriggerFOR_EACH_ROW() +
                this.getTrigger_whenclause() +
                this.getTrigger_actionsql();

        trigger_drop_sql = "DROP TRIGGER " +
                SQLKWORD.SQLIFEXISTS +
                this.getTriggerName() +
                ";";
    }
}
