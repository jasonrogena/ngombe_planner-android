package org.cgiar.ilri.np.farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.np.farmer.backend.DataHandler;
import org.cgiar.ilri.np.farmer.backend.Locale;
import org.cgiar.ilri.np.farmer.carrier.Cow;
import org.cgiar.ilri.np.farmer.carrier.Dam;
import org.cgiar.ilri.np.farmer.carrier.Event;
import org.cgiar.ilri.np.farmer.carrier.EventConstraint;
import org.cgiar.ilri.np.farmer.carrier.Farmer;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddEventActivity extends SherlockActivity implements NPActivity, View.OnClickListener,View.OnFocusChangeListener,DatePickerDialog.OnDateSetListener, Spinner.OnItemSelectedListener
{
    private Boolean cacheData;

    public  final String TAG="AddEventActivity";
    private final String dateFormat="dd/MM/yyyy";

    public static final String KEY_MODE="mode";
    public static final String MODE_SERVICING="Servicing";
    public static final String MODE_CALVING="Calving";
    public static final String KEY_SERVICING_TYPE="servicingType";

    private EditText phantomET;
    private Menu menu;
    private TextView cowIdentifierTV;
    private Spinner cowIdentifierS;
    private TextView dateTV;
    private EditText dateET;
    private TextView eventTypeTV;
    private Spinner eventTypeS;
    private TextView eventSubtypeTV;
    private Spinner eventSubtypeS;
    private TextView remarksTV;
    private EditText remarksET;
    private Button okayB;
    private Button cancelB;
    private DatePickerDialog datePickerDialog;
    private TextView strawNumberTV;
    private EditText strawNumberET;
    private TextView vetUsedTV;
    private EditText vetUsedET;
    private TextView bullNameTV;
    private AutoCompleteTextView bullNameACTV;
    private TextView bullOwnerTV;
    private Spinner bullOwnerS;
    private TextView specBullOwnerTV;
    private EditText specBullOwnerET;

    /*private TextView bullETNTV;
    private AutoCompleteTextView bullETNACTV;*/
    private TextView noOfServicingDaysTV;
    private EditText noOfServicingDaysET;
    private TextView servicingTV;
    private Spinner servicingS;
    private TextView causeOfDeathTV;
    private Spinner causeOfDeathS;
    /*private TextView liveBirthsTV;
    private EditText liveBirthsET;*/

    private String[] cowNameArray;
    private String[] cowEarTagNumberArray;
    private String[] cowSexArray;
    private String enterDate;
    private String dateInFuture;
    private String eventRecorded;
    private String sendUnsuccessfulWarning;
    private String loadingPleaseWait;
    private List<Integer> servicingIDs;
    private List<String> servicingTypes;
    private List<EventConstraint> eventConstraints;
    private Farmer farmer;

    private String presetMode;
    private String presetServicingType;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //DataHandler.requestPermissionToUseSMS(this);

        cacheData = true;

        phantomET = (EditText) this.findViewById(R.id.phantom_et);
        phantomET.requestFocus();
        cowIdentifierTV=(TextView)findViewById(R.id.cow_identifier_tv);
        cowIdentifierS=(Spinner)findViewById(R.id.cow_identifier_s);
        dateTV=(TextView)findViewById(R.id.date_tv);
        dateET=(EditText)findViewById(R.id.date_et);
        dateET.setOnFocusChangeListener(this);
        dateET.setOnClickListener(this);
        eventTypeTV=(TextView)findViewById(R.id.event_type_tv);
        eventTypeS=(Spinner)findViewById(R.id.event_type_s);
        eventTypeS.setOnItemSelectedListener(this);
        eventSubtypeTV=(TextView)findViewById(R.id.event_subtype_tv);
        eventSubtypeS=(Spinner)findViewById(R.id.event_subtype_s);
        servicingTV = (TextView)findViewById(R.id.servicing_tv);
        servicingS = (Spinner)findViewById(R.id.servicing_s);
        strawNumberTV = (TextView)findViewById(R.id.straw_number_tv);
        strawNumberET = (EditText)findViewById(R.id.straw_number_et);
        vetUsedTV = (TextView)findViewById(R.id.vet_used_tv);
        vetUsedET = (EditText)findViewById(R.id.vet_used_et);
        causeOfDeathTV = (TextView)findViewById(R.id.cause_of_death_tv);
        causeOfDeathS = (Spinner)findViewById(R.id.cause_of_death_s);
        bullNameTV = (TextView)findViewById(R.id.bull_name_tv);
        bullNameACTV = (AutoCompleteTextView)findViewById(R.id.bull_name_actv);
        bullOwnerTV = (TextView)findViewById(R.id.bull_owner_tv);
        bullOwnerS = (Spinner)findViewById(R.id.bull_owner_s);
        bullOwnerS.setOnItemSelectedListener(this);
        specBullOwnerTV = (TextView)findViewById(R.id.spec_bull_owner_tv);
        specBullOwnerET = (EditText)findViewById(R.id.spec_bull_owner_et);
        /*bullETNTV = (TextView)findViewById(R.id.bull_etn_tv);
        bullETNACTV = (AutoCompleteTextView)findViewById(R.id.bull_etn_actv);*/
        noOfServicingDaysTV = (TextView)findViewById(R.id.no_of_servicing_days_tv);
        noOfServicingDaysET = (EditText)findViewById(R.id.no_of_servicing_days_et);
        remarksTV=(TextView)findViewById(R.id.remarks_tv);
        remarksET=(EditText)findViewById(R.id.remarks_et);
        /*liveBirthsTV = (TextView)findViewById(R.id.live_births_tv);
        liveBirthsET = (EditText)findViewById(R.id.live_births_et);*/
        okayB=(Button)findViewById(R.id.okay_b);
        okayB.setOnClickListener(this);
        cancelB = (Button)findViewById(R.id.cancel_b);
        cancelB.setOnClickListener(this);

        initTextInViews();
        fetchCowIdentifiers();
        fetchServicingEvents();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            presetMode = bundle.getString(KEY_MODE);
            if(presetMode.equals(MODE_SERVICING)){
                presetServicingType = bundle.getString(KEY_SERVICING_TYPE);
            }
            modeSet();
        }
    }

    private void cacheEditTextData(){
        if(cacheData) {
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_DATE, dateET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_REMARKS, remarksET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_STRAW_NUMBER, strawNumberET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_VET_USED, vetUsedET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_NAME, bullNameACTV.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_OWNER, specBullOwnerET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_NO_SERVICING_DAYS, noOfServicingDaysET.getText().toString());
        }
    }

    private void restoreEditTextData(){
        dateET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_DATE, ""));
        remarksET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_REMARKS, ""));
        strawNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_STRAW_NUMBER, ""));
        vetUsedET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_VET_USED, ""));
        bullNameACTV.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_NAME, ""));
        specBullOwnerET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_OWNER, ""));
        noOfServicingDaysET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AEA_NO_SERVICING_DAYS, ""));
    }

    private void clearEditTextDataCache(){
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_DATE, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_REMARKS, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_STRAW_NUMBER, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_VET_USED, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_NAME, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_OWNER, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_NO_SERVICING_DAYS, "");

        cacheData = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        cacheEditTextData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreEditTextData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.add_event, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==DialogInterface.BUTTON_POSITIVE){
                        dialog.dismiss();

                        clearEditTextDataCache();

                        Intent intent = new Intent(AddEventActivity.this, MainMenu.class);
                        intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    else{
                        dialog.cancel();
                    }
                }
            };
            AlertDialog mainMenuDialog = Utils.createMainMenuDialog(this, onClickListener);
            mainMenuDialog.show();
        }
        return false;
    }

    private void modeSet(){
        if(presetMode.equals(MODE_CALVING)){
            String[] eventTypesInEN = Locale.getArrayInLocale("cow_event_types",this);
            for(int i = 0; i < eventTypesInEN.length; i++){
                if(eventTypesInEN[i].equals("Calving")){//**
                    eventTypeS.setSelection(i);
                    break;
                }
            }

            eventTypeSelected();

            this.setTitle(Locale.getStringInLocale("calving",this));
            dateTV.setText(Locale.getStringInLocale("birth_date",this));
            eventTypeTV.setVisibility(TextView.GONE);
            eventTypeS.setVisibility(Spinner.GONE);
            servicingTV.setVisibility(TextView.GONE);
            servicingS.setVisibility(Spinner.GONE);
        }
        else if(presetMode.equals(MODE_SERVICING)){
            if(presetServicingType.equals(Cow.SERVICE_TYPE_BULL)){
                String[] eventTypesInEN = Locale.getArrayInLocale("cow_event_types",this);
                for(int i = 0; i < eventTypesInEN.length; i++){
                    if(eventTypesInEN[i].equals("Bull Servicing")){
                        eventTypeS.setSelection(i);
                        break;
                    }
                }

                eventTypeSelected();

                this.setTitle(Locale.getStringInLocale("bull_servicing",this));
                dateTV.setText(Locale.getStringInLocale("date_served", this));
                eventTypeTV.setVisibility(TextView.GONE);
                eventTypeS.setVisibility(Spinner.GONE);
                noOfServicingDaysTV.setVisibility(TextView.GONE);
                noOfServicingDaysET.setVisibility(EditText.GONE);
                bullNameTV.setVisibility(TextView.VISIBLE);
                bullNameACTV.setVisibility(AutoCompleteTextView.VISIBLE);
                bullOwnerTV.setVisibility(TextView.VISIBLE);
                bullOwnerS.setVisibility(Spinner.VISIBLE);
                //specBullOwnerTV.setVisibility(TextView.VISIBLE);
                //specBullOwnerET.setVisibility(EditText.VISIBLE);
                /*bullETNTV.setVisibility(TextView.VISIBLE);
                bullETNACTV.setVisibility(AutoCompleteTextView.VISIBLE);*/
                remarksTV.setVisibility(TextView.GONE);
                remarksET.setVisibility(EditText.GONE);
            }
            else if(presetServicingType.equals(Cow.SERVICE_TYPE_AI)){
                String[] eventTypesInEN = Locale.getArrayInLocale("cow_event_types",this);
                for(int i = 0; i < eventTypesInEN.length; i++){
                    if(eventTypesInEN[i].equals("Artificial Insemination")){
                        eventTypeS.setSelection(i);
                        break;
                    }
                }

                eventTypeSelected();

                this.setTitle(Locale.getStringInLocale("artificial_inseminamtion",this));
                dateTV.setText(Locale.getStringInLocale("date_of_insemination", this));
                eventTypeTV.setVisibility(TextView.GONE);
                eventTypeS.setVisibility(Spinner.GONE);
                bullNameTV.setVisibility(TextView.GONE);
                bullNameACTV.setVisibility(AutoCompleteTextView.GONE);
                bullOwnerTV.setVisibility(TextView.GONE);
                bullOwnerS.setVisibility(Spinner.GONE);
                specBullOwnerTV.setVisibility(TextView.GONE);
                specBullOwnerET.setVisibility(EditText.GONE);
                /*bullETNTV.setVisibility(TextView.GONE);
                bullETNACTV.setVisibility(AutoCompleteTextView.GONE);*/
                remarksTV.setVisibility(TextView.GONE);
                remarksET.setVisibility(EditText.GONE);
            }
        }
    }

    @Override
    public void initTextInViews() {
        setTitle(Locale.getStringInLocale("add_an_event",this));
        cowIdentifierTV.setText(Locale.getStringInLocale("cow",this));
        dateTV.setText(Locale.getStringInLocale("event_date",this));
        eventTypeTV.setText(Locale.getStringInLocale("event",this));

        int eventTypeArrayID = Locale.getArrayIDInLocale("cow_event_types",this);
        if(eventTypeArrayID !=0) {
            ArrayAdapter<CharSequence> eventTypeArrayAdapter=ArrayAdapter.createFromResource(this, eventTypeArrayID, android.R.layout.simple_spinner_item);
            eventTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventTypeS.setAdapter(eventTypeArrayAdapter);
        }
        strawNumberTV.setText(Locale.getStringInLocale("straw_number",this));
        vetUsedTV.setText(Locale.getStringInLocale("vet_used",this));
        causeOfDeathTV.setText(Locale.getStringInLocale("cause_of_death",this));
        int causesOfDeathID = Locale.getArrayIDInLocale("causes_of_death",this);
        if(causesOfDeathID != 0) {
            ArrayAdapter<CharSequence> causesOfDeathArrayAdapter=ArrayAdapter.createFromResource(this, causesOfDeathID, android.R.layout.simple_spinner_item);
            causesOfDeathArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            causeOfDeathS.setAdapter(causesOfDeathArrayAdapter);
        }
        bullNameTV.setText(Locale.getStringInLocale("servicing_bull_name",this));
        bullNameACTV.setHint(Locale.getStringInLocale("servicing_bull_identifier_hint", this));
        bullOwnerTV.setText(Locale.getStringInLocale("bull_owner", this));

        int bullOwnersID = Locale.getArrayIDInLocale("bull_owners", this);
        if(bullOwnersID != 0){
            ArrayAdapter<CharSequence> bullOwnerArrayAdapter = ArrayAdapter.createFromResource(this, bullOwnersID, android.R.layout.simple_spinner_item);
            bullOwnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bullOwnerS.setAdapter(bullOwnerArrayAdapter);
        }

        specBullOwnerTV.setText(Locale.getStringInLocale("name_bull_owner", this));
        specBullOwnerET.setHint(Locale.getStringInLocale("name_of_other_farmer_or_group", this));

        //bullETNTV.setText(Locale.getStringInLocale("servicing_bull_ear_tag_number",this));
        noOfServicingDaysTV.setText(Locale.getStringInLocale("no_of_days_in_servicing",this));
        remarksTV.setText(Locale.getStringInLocale("remarks",this));
        okayB.setText(Locale.getStringInLocale("okay",this));
        enterDate=Locale.getStringInLocale("enter_date",this);
        dateInFuture=Locale.getStringInLocale("date_in_future",this);
        eventRecorded=Locale.getStringInLocale("event_successfully_recorded",this);
        sendUnsuccessfulWarning=Locale.getStringInLocale("something_went_wrong_try_again",this);
        loadingPleaseWait = Locale.getStringInLocale("loading_please_wait",this);
        servicingTV.setText(Locale.getStringInLocale("associated_servicing",this));
        //liveBirthsTV.setText(Locale.getStringInLocale("previous_live_births",this));

        cancelB.setText(Locale.getStringInLocale("cancel", this));

        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    private void fetchCowIdentifiers()
    {
        CowIdentifierThread cowIdentifierThread=new CowIdentifierThread();
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        cowIdentifierThread.execute(telephonyManager.getSimSerialNumber());
    }

    private void setCowIdentifiers(String[] cowIdentifiers)
    {
        if(cowIdentifierS!=null)
        {
            ArrayAdapter<String> cowsArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cowIdentifiers);
            cowsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cowIdentifierS.setAdapter(cowsArrayAdapter);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==okayB)
        {
            sendEvent();
        }
        else if(view==dateET)
        {
            dateETClicked();
        }
        else if(view == cancelB){
            Intent intent;

            clearEditTextDataCache();

            if(presetMode != null && (presetMode.equals(MODE_CALVING) || presetMode.equals(MODE_SERVICING))){
                intent = new Intent(this, FertilityActivity.class);
            }
            else{
                intent = new Intent(this, EventsActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void eventTypeSelected(){
        okayB.setText(Locale.getStringInLocale("okay",this));
        dateTV.setText(Locale.getStringInLocale("event_date",this));
        eventSubtypeTV.setVisibility(TextView.GONE);
        eventSubtypeS.setVisibility(Spinner.GONE);
        strawNumberTV.setVisibility(TextView.GONE);
        strawNumberET.setVisibility(EditText.GONE);
        vetUsedTV.setVisibility(TextView.GONE);
        vetUsedET.setVisibility(EditText.GONE);
        bullNameTV.setVisibility(TextView.GONE);
        bullNameACTV.setVisibility(AutoCompleteTextView.GONE);
        bullOwnerTV.setVisibility(TextView.GONE);
        bullOwnerS.setVisibility(Spinner.GONE);
        specBullOwnerTV.setVisibility(TextView.GONE);
        specBullOwnerET.setVisibility(EditText.GONE);
        /*bullETNTV.setVisibility(TextView.GONE);
        bullETNACTV.setVisibility(AutoCompleteTextView.GONE);*/
        remarksTV.setVisibility(TextView.GONE);
        remarksET.setVisibility(EditText.GONE);
        cowIdentifierS.setVisibility(Spinner.GONE);
        cowIdentifierTV.setVisibility(TextView.GONE);
        noOfServicingDaysTV.setVisibility(TextView.GONE);
        noOfServicingDaysET.setVisibility(EditText.GONE);
        servicingTV.setVisibility(TextView.GONE);
        servicingS.setVisibility(Spinner.GONE);
        causeOfDeathTV.setVisibility(TextView.GONE);
        causeOfDeathS.setVisibility(Spinner.GONE);
        /*liveBirthsTV.setVisibility(TextView.GONE);
        liveBirthsET.setVisibility(EditText.GONE);*/
        String[] eventTypesEN = Locale.getArrayInLocale("cow_event_types", this, Locale.LOCALE_ENGLISH);
        if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Calving")) {//**
            birthEventSelected();
            dateTV.setText(Locale.getStringInLocale("birth_date", this));
            eventSubtypeTV.setVisibility(TextView.VISIBLE);
            eventSubtypeS.setVisibility(Spinner.VISIBLE);
            servicingTV.setVisibility(TextView.VISIBLE);
            servicingS.setVisibility(Spinner.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
            okayB.setText(Locale.getStringInLocale("next",this));
            /*liveBirthsTV.setVisibility(TextView.VISIBLE);
            liveBirthsET.setVisibility(EditText.VISIBLE);*/
        }
        else if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Abortion")) {
            servicingTV.setVisibility(TextView.VISIBLE);
            servicingS.setVisibility(Spinner.VISIBLE);
            remarksTV.setVisibility(TextView.VISIBLE);
            remarksET.setVisibility(EditText.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
        }
        else if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Acquisition")) {
            okayB.setText(Locale.getStringInLocale("next",this));
        }
        else if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Artificial Insemination")) {
            dateTV.setText(Locale.getStringInLocale("date_of_insemination", this));
            strawNumberTV.setVisibility(TextView.VISIBLE);
            strawNumberET.setVisibility(EditText.VISIBLE);
            vetUsedTV.setVisibility(TextView.VISIBLE);
            vetUsedET.setVisibility(EditText.VISIBLE);
            remarksTV.setVisibility(TextView.VISIBLE);
            remarksET.setVisibility(EditText.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
        }
        else if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Bull Servicing")) {
            dateTV.setText(Locale.getStringInLocale("date_served", this));
            bullNameTV.setVisibility(TextView.VISIBLE);
            bullNameACTV.setVisibility(AutoCompleteTextView.VISIBLE);
            bullOwnerTV.setVisibility(TextView.VISIBLE);
            bullOwnerS.setVisibility(Spinner.VISIBLE);
            specBullOwnerTV.setVisibility(TextView.VISIBLE);
            specBullOwnerET.setVisibility(EditText.VISIBLE);
            /*bullETNTV.setVisibility(TextView.VISIBLE);
            bullETNACTV.setVisibility(AutoCompleteTextView.VISIBLE);*/
            remarksTV.setVisibility(TextView.VISIBLE);
            remarksET.setVisibility(EditText.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
            noOfServicingDaysTV.setVisibility(TextView.VISIBLE);
            noOfServicingDaysET.setVisibility(EditText.VISIBLE);
            dateTV.setText(Locale.getStringInLocale("start_date", this));
        }
        else if(eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Death")) {
            causeOfDeathTV.setVisibility(TextView.VISIBLE);
            causeOfDeathS.setVisibility(Spinner.VISIBLE);
            remarksTV.setVisibility(TextView.VISIBLE);
            remarksET.setVisibility(EditText.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
        }
        else {
            remarksTV.setVisibility(TextView.VISIBLE);
            remarksET.setVisibility(EditText.VISIBLE);
            cowIdentifierS.setVisibility(Spinner.VISIBLE);
            cowIdentifierTV.setVisibility(TextView.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == eventTypeS) {
            eventTypeSelected();
        }
        else if(parent == bullOwnerS){
            String[] bullOwnersInEN = Locale.getArrayInLocale("bull_owners", this, Locale.LOCALE_ENGLISH);
            if(bullOwnersInEN[bullOwnerS.getSelectedItemPosition()].equals("Own bull")){
                specBullOwnerET.setVisibility(EditText.GONE);
                specBullOwnerTV.setVisibility(TextView.GONE);
            }
            else{
                specBullOwnerET.setVisibility(EditText.VISIBLE);
                specBullOwnerTV.setVisibility(TextView.VISIBLE);
            }
        }
    }

    private void birthEventSelected() {
        eventSubtypeTV.setText(Locale.getStringInLocale("type_of_birth",this));
        int eventSubtypeArrayID = Locale.getArrayIDInLocale("birth_types",this);
        if(eventSubtypeArrayID != 0) {
            ArrayAdapter<CharSequence> eventSubtypeArrayAdapter=ArrayAdapter.createFromResource(this, eventSubtypeArrayID, android.R.layout.simple_spinner_item);
            eventSubtypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventSubtypeS.setAdapter(eventSubtypeArrayAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean validateInput()
    {

        String[] eventTypesInEN = Locale.getArrayInLocale("cow_event_types",this, Locale.LOCALE_ENGLISH);
        String selectedEvent = eventTypesInEN[eventTypeS.getSelectedItemPosition()];
        if(dateET.getText().toString()==null||dateET.getText().toString().length()==0) {
            Toast.makeText(this,enterDate,Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            if(!validateDate()){
                return false;
            }
        }
        if(selectedEvent.length() == 0){
            Toast.makeText(this,Locale.getStringInLocale("select_an_event",this),Toast.LENGTH_LONG).show();
            return false;
        }
        if(selectedEvent.equals("Abortion") || selectedEvent.equals("Birth") || selectedEvent.equals("Start of Lactation") || selectedEvent.equals("Dry Off") || selectedEvent.equals("Artificial Insemination") || selectedEvent.equals("Bull Servicing")) {
            if(cowSexArray != null && cowSexArray[cowIdentifierS.getSelectedItemPosition()].equals(Cow.SEX_MALE)) {
                Toast.makeText(this,Locale.getStringInLocale("event_only_for_female_cattle",this),Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if(selectedEvent.equals("Birth")){
            /*if(liveBirthsET.getText().toString()==null || liveBirthsET.getText().toString().trim().length()==0){
                Toast.makeText(this,Locale.getStringInLocale("enter_previous_live_births",this),Toast.LENGTH_LONG).show();
                return false;
            }*/
        }

        Cow selectedCow = farmer.getCows().get(cowIdentifierS.getSelectedItemPosition());
        if(selectedCow != null){
            if(selectedEvent.equals("Birth")){
                for(int i = 0; i < eventConstraints.size(); i++){
                    EventConstraint currConstraint = eventConstraints.get(i);
                    if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_CALVING)){
                        if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                            Toast.makeText(this,Locale.getStringInLocale("cow_too_young", this),Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }
            else if(selectedEvent.equals("Start of Lactation")){
                for(int i = 0; i < eventConstraints.size(); i++){
                    EventConstraint currConstraint = eventConstraints.get(i);
                    if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_MILKING)){
                        if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                            Toast.makeText(this,Locale.getStringInLocale("cow_too_young", this),Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }
            else if(selectedEvent.equals("Artificial Insemination") || selectedEvent.equals("Bull Servicing") || selectedEvent.equals("Signs of Heat")){
                for(int i = 0; i < eventConstraints.size(); i++){
                    EventConstraint currConstraint = eventConstraints.get(i);
                    if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_MATURITY)){
                        if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                            Toast.makeText(this,Locale.getStringInLocale("cow_too_young", this),Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                }
            }

        }

        return true;
    }

    private boolean validateDate() {
        try
        {
            Date dateEntered=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateET.getText().toString());
            Date today=new Date();
            long milisecondDifference = today.getTime() - dateEntered.getTime();
            long days = milisecondDifference / 86400000;

            String[] eventTypesEN = Locale.getArrayInLocale("cow_event_types", this, Locale.LOCALE_ENGLISH);

            if((today.getTime()-dateEntered.getTime())<0)
            {
                Toast.makeText(this,dateInFuture,Toast.LENGTH_LONG).show();
                return false;
            }
            else if(days > 15 && !eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Calving") && !eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Dry Off")) {//more than 15 days for other events other than birth and dry off
                Toast.makeText(this,Locale.getStringInLocale("event_too_old",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else if(days > 730 && eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Calving")) {//more than 2 years for births
                Toast.makeText(this,Locale.getStringInLocale("birth_event_too_old",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else if(days > 180 && eventTypesEN[eventTypeS.getSelectedItemPosition()].equals("Dry Off")) {//more than 6 months for dry offs
                Toast.makeText(this,Locale.getStringInLocale("dry_off_event_too_old",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else {
                return true;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void sendEvent()
    {
        if(validateInput()){
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_DATE, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_REMARKS, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_STRAW_NUMBER, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_VET_USED, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_NAME, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_BULL_OWNER, "");
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AEA_NO_SERVICING_DAYS, "");

            String[] eventTypes = Locale.getArrayInLocale("cow_event_types",this,Locale.LOCALE_ENGLISH);
            String selectedEvent = eventTypes[eventTypeS.getSelectedItemPosition()];
            String[] eventSubtypes = Locale.getArrayInLocale("birth_types",this,Locale.LOCALE_ENGLISH);
            if(selectedEvent.equals("Calving") && (eventSubtypes[eventSubtypeS.getSelectedItemPosition()].equals("Normal") || eventSubtypes[eventSubtypeS.getSelectedItemPosition()].equals("Premature"))) {
                AlertDialog cowRegistrationAlertDialog = constructCalfRegistrationDialog();
                cowRegistrationAlertDialog.show();
            }
            else if(selectedEvent.equals("Acquisition")) {
                AlertDialog cowRegistrationAlertDialog = constructCowRegistrationDialog();
                cowRegistrationAlertDialog.show();
            }
            else {
                String selectedCowETN = cowEarTagNumberArray[cowIdentifierS.getSelectedItemPosition()];
                String selectedCowName = cowNameArray[cowIdentifierS.getSelectedItemPosition()];
                TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
                String serialNumber = telephonyManager.getSimSerialNumber();
                JSONObject jsonObject = new JSONObject();
                try
                {
                    String[] causesOfDeathInEN = Locale.getArrayInLocale("causes_of_death",AddEventActivity.this,Locale.LOCALE_ENGLISH);
                    jsonObject.put("simCardSN", serialNumber);
                    jsonObject.put("cowEarTagNumber", selectedCowETN);
                    jsonObject.put("cowName", selectedCowName);
                    jsonObject.put("date", dateET.getText().toString());
                    jsonObject.put("eventType", selectedEvent);
                    jsonObject.put("remarks", remarksET.getText().toString());
                    jsonObject.put("strawNumber", strawNumberET.getText().toString());
                    jsonObject.put("vetUsed", vetUsedET.getText().toString());
                    //jsonObject.put("bullName", bullNameACTV.getText().toString());

                    //check if hint put as value of bullNameACTV
                    String bullEarTagNo = bullNameACTV.getText().toString();
                    if(bullEarTagNo.equals(Locale.getStringInLocale("servicing_bull_identifier_hint", AddEventActivity.this)))
                        bullEarTagNo = "";
                    jsonObject.put("bullEarTagNo", bullEarTagNo);

                    if(selectedEvent.equals("Bull Servicing")){
                        String[] bullOwnersInEN = Locale.getArrayInLocale("bull_owners", AddEventActivity.this, Locale.LOCALE_ENGLISH);
                        jsonObject.put("bullOwner", bullOwnersInEN[bullOwnerS.getSelectedItemPosition()]);

                        //check if hint put as value of EditText
                        String bullOwnerName = specBullOwnerET.getText().toString();
                        if(bullOwnerName.equals(Locale.getStringInLocale("name_of_other_farmer_or_group", AddEventActivity.this)))
                            bullOwnerName = "";
                        jsonObject.put("bullOwnerName", bullOwnerName);
                    }
                    //jsonObject.put("bullEarTagNo", bullETNACTV.getText().toString());
                    jsonObject.put("noOfServicingDays", noOfServicingDaysET.getText().toString());
                    if(servicingIDs != null && servicingS.getSelectedItemPosition() != -1) {
                        jsonObject.put("parentEvent", servicingIDs.get(servicingS.getSelectedItemPosition()));
                    }
                    if(selectedEvent.equals("Birth")){
                        String[] birthTypesInEN = Locale.getArrayInLocale("birth_types", AddEventActivity.this, Locale.LOCALE_ENGLISH);
                        jsonObject.put("birthType", birthTypesInEN[eventSubtypeS.getSelectedItemPosition()]);
                        //jsonObject.put("liveBirths", liveBirthsET.getText().toString());
                    }
                    jsonObject.put("causeOfDeath", causesOfDeathInEN[causeOfDeathS.getSelectedItemPosition()]);
                    CowEventAdditionThread cowEventAdditionThread=new CowEventAdditionThread();
                    cowEventAdditionThread.execute(jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private AlertDialog constructCalfRegistrationDialog() {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Locale.getStringInLocale("calf_registration", this));
        alertDialogBuilder
                .setMessage(Locale.getStringInLocale("next_screen_is_calf_registration",AddEventActivity.this))
                .setCancelable(false)
                .setPositiveButton(Locale.getStringInLocale("next", AddEventActivity.this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int numberOfCows = 1;
                        Farmer farmer = new Farmer();
                        farmer.setCowNumber(numberOfCows);
                        farmer.setMode(Farmer.MODE_NEW_COW_REGISTRATION);
                        TelephonyManager telephonyManager = (TelephonyManager) AddEventActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                        farmer.setSimCardSN(telephonyManager.getSimSerialNumber());
                        Cow thisCalf = new Cow(true);
                        Dam calfDam = new Dam();
                        calfDam.setEarTagNumber(cowEarTagNumberArray[cowIdentifierS.getSelectedItemPosition()]);
                        calfDam.setName(cowNameArray[cowIdentifierS.getSelectedItemPosition()]);
                        thisCalf.setDam(calfDam);
                        JSONObject jsonObject = new JSONObject();
                        String[] birthTypesInEN = Locale.getArrayInLocale("birth_types", AddEventActivity.this, Locale.LOCALE_ENGLISH);
                        try {
                            String selectedCowETN = cowEarTagNumberArray[cowIdentifierS.getSelectedItemPosition()];
                            String selectedCowName = cowNameArray[cowIdentifierS.getSelectedItemPosition()];

                            jsonObject.put("motherETN", selectedCowETN);
                            jsonObject.put("motherName", selectedCowName);
                            jsonObject.put("birthType", birthTypesInEN[eventSubtypeS.getSelectedItemPosition()]);
                            if(servicingIDs != null && servicingS.getSelectedItemPosition() != -1) {
                                jsonObject.put("parentEvent", servicingIDs.get(servicingS.getSelectedItemPosition()));
                            }
                            //jsonObject.put("liveBirths", liveBirthsET.getText().toString());

                            jsonObject.put("eventDate", dateET.getText().toString());
                            thisCalf.setPiggyBack(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(servicingTypes != null && servicingS.getSelectedItemPosition() != -1) {
                            if (servicingTypes.get(servicingS.getSelectedItemPosition()).equals("Artificial Insemination")) {
                                thisCalf.setServiceType(Cow.SERVICE_TYPE_AI);
                            } else if (servicingTypes.get(servicingS.getSelectedItemPosition()).equals("Bull Servicing")) {
                                thisCalf.setServiceType(Cow.SERVICE_TYPE_BULL);
                            }
                        }
                        thisCalf.setDateOfBirth(dateET.getText().toString());
                        thisCalf.setMode(Cow.MODE_BORN_CALF_REGISTRATION);

                        farmer.setCow(thisCalf, 0);

                        clearEditTextDataCache();

                        Intent intent = new Intent(AddEventActivity.this, CowRegistrationActivity.class);
                        //intent.putExtra(CowRegistrationActivity.KEY_MODE,CowRegistrationActivity.MODE_COW);
                        intent.putExtra(CowRegistrationActivity.KEY_INDEX, 0);
                        intent.putExtra(CowRegistrationActivity.KEY_NUMBER_OF_COWS, numberOfCows);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Farmer.PARCELABLE_KEY, farmer);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(Locale.getStringInLocale("cancel", AddEventActivity.this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog=alertDialogBuilder.create();
        return alertDialog;
    }

    private AlertDialog constructCowRegistrationDialog() {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Locale.getStringInLocale("cow_registration_proper", this));
        alertDialogBuilder
                .setMessage(Locale.getStringInLocale("next_screen_is_cow_registration",AddEventActivity.this))
                .setCancelable(false)
                .setPositiveButton(Locale.getStringInLocale("next", AddEventActivity.this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int numberOfCows = 1;
                        Farmer farmer = new Farmer();
                        farmer.setCowNumber(numberOfCows);
                        farmer.setMode(Farmer.MODE_NEW_COW_REGISTRATION);
                        TelephonyManager telephonyManager = (TelephonyManager) AddEventActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                        farmer.setSimCardSN(telephonyManager.getSimSerialNumber());
                        Cow thisCow = new Cow(true);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("remarks", remarksET.getText().toString());
                            thisCow.setPiggyBack(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        thisCow.setMode(Cow.MODE_ADULT_COW_REGISTRATION);
                        farmer.setCow(thisCow, 0);

                        clearEditTextDataCache();

                        Intent intent = new Intent(AddEventActivity.this, CowRegistrationActivity.class);
                        //intent.putExtra(CowRegistrationActivity.KEY_MODE,CowRegistrationActivity.MODE_COW);
                        intent.putExtra(CowRegistrationActivity.KEY_INDEX, 0);
                        intent.putExtra(CowRegistrationActivity.KEY_NUMBER_OF_COWS, numberOfCows);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Farmer.PARCELABLE_KEY, farmer);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(Locale.getStringInLocale("cancel", AddEventActivity.this), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog=alertDialogBuilder.create();
        return alertDialog;
    }

    private class CowEventAdditionThread extends AsyncTask<JSONObject, Integer, Boolean>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddEventActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            //public static final void cacheRequest(Context context, String jsonString, String appendedURL){
            boolean response = DataHandler.cacheRequest(AddEventActivity.this, params[0].toString(), DataHandler.FARMER_ADD_COW_EVENT_URL);
            return response;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result == null || result == false){
                Toast.makeText(AddEventActivity.this, sendUnsuccessfulWarning, Toast.LENGTH_LONG).show();
            }
            else if(result == true) {
                Toast.makeText(AddEventActivity.this, eventRecorded, Toast.LENGTH_LONG).show();
                Intent intent;

                clearEditTextDataCache();

                if(presetMode != null && (presetMode.equals(MODE_SERVICING) || presetMode.equals(MODE_CALVING))){
                    intent = new Intent(AddEventActivity.this, FertilityActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                else{
                    intent = new Intent(AddEventActivity.this, EventsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        if(view==dateET && hasFocus)
        {
            dateETClicked();
        }
    }

    private void dateETClicked()
    {
        Date date=null;

        if(dateET.getText().toString().length()>0)
        {
            try
            {
                date=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateET.getText().toString());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        if(date==null)
        {
            date=new Date();
        }

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
        dateET.setText(dateString);
        if(!validateDate()){
            dateET.setText("");
        }
    }

    private class CowIdentifierThread extends AsyncTask<String,Integer,Farmer> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddEventActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected Farmer doInBackground(String... params) {
            Farmer farmer = DataHandler.getFarmerData(AddEventActivity.this);
            eventConstraints = DataHandler.getEventConstraints(AddEventActivity.this);
            return farmer;
        }

        @Override
        protected void onPostExecute(Farmer farmer) {
            super.onPostExecute(farmer);
            progressDialog.dismiss();

            if(farmer == null){
                Toast.makeText(AddEventActivity.this, Locale.getStringInLocale("unable_to_fetch_cached_data", AddEventActivity.this),Toast.LENGTH_LONG).show();
            }
            else{
                AddEventActivity.this.farmer = farmer;
                List<Cow> cows = farmer.getCows();
                String[] cowArray=new String[cows.size()];
                String[] earTagArray=new String[cows.size()];
                String[] sexArray=new String[cows.size()];

                for(int i=0;i<cows.size();i++) {
                    cowArray[i]=cows.get(i).getName();
                    earTagArray[i]=cows.get(i).getEarTagNumber();
                    sexArray[i]=cows.get(i).getSex();
                }

                if(cowArray.length==0) {
                    Toast.makeText(AddEventActivity.this, Locale.getStringInLocale("you_do_not_have_cows", AddEventActivity.this),Toast.LENGTH_LONG).show();
                }
                AddEventActivity.this.cowNameArray =cowArray;
                AddEventActivity.this.cowEarTagNumberArray=earTagArray;
                AddEventActivity.this.cowSexArray = sexArray;
                String[] identifierArray=new String[cowArray.length];
                for (int i=0;i<cowArray.length;i++)
                {
                    if(cowArray[i]!=null&&!cowArray[i].equals(""))
                    {
                        identifierArray[i]=cowArray[i];
                    }
                    else
                    {
                        identifierArray[i]=earTagArray[i];
                    }
                }
                setCowIdentifiers(identifierArray);
            }
        }
    }

    private void fetchServicingEvents() {
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        CowEventHistoryThread cowEventHistoryThread =new CowEventHistoryThread();
        cowEventHistoryThread.execute(telephonyManager.getSimSerialNumber());
    }

    private class CowEventHistoryThread extends AsyncTask<String, Integer, Farmer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddEventActivity.this, "",Locale.getStringInLocale("loading_please_wait",AddEventActivity.this), true);
        }

        @Override
        protected Farmer doInBackground(String... params) {
            /*String result = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("simCardSN",params[0]);
                result = DataHandler.sendDataToServer(AddEventActivity.this, jsonObject.toString(),DataHandler.FARMER_FETCH_COW_SERVICING_EVENTS_URL, true);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }*/

            Farmer farmer = DataHandler.getFarmerData(AddEventActivity.this);
            return farmer;
        }

        @Override
        protected void onPostExecute(Farmer farmer) {
            super.onPostExecute(farmer);
            progressDialog.dismiss();
            if(farmer == null) {
                Toast.makeText(AddEventActivity.this, Locale.getStringInLocale("unable_to_fetch_cached_data",AddEventActivity.this), Toast.LENGTH_LONG).show();
            }
            else {
                /*try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray historyArray = jsonObject.getJSONArray("history");
                    List<String> servicingNames = new ArrayList<String>();
                    servicingIDs = new ArrayList<Integer>();
                    servicingTypes = new ArrayList<String>();
                    for(int i = 0; i < historyArray.length(); i++) {
                        JSONObject currentServicing = historyArray.getJSONObject(i);
                        servicingIDs.add(currentServicing.getInt("id"));
                        servicingNames.add(currentServicing.getString("event_date")+" ("+currentServicing.getString("ear_tag_number")+")");
                        servicingTypes.add(currentServicing.getString("event_name"));
                    }
                    ArrayAdapter<String> servicingsArrayAdapter=new ArrayAdapter<String>(AddEventActivity.this,android.R.layout.simple_spinner_dropdown_item,servicingNames);
                    servicingsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    servicingS.setAdapter(servicingsArrayAdapter);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }*/

                List<Cow> allCows = farmer.getCows();
                List<String> servicingNames = new ArrayList<String>();
                servicingIDs = new ArrayList<Integer>();
                servicingTypes = new ArrayList<String>();
                for(int cowIndex = 0; cowIndex < allCows.size(); cowIndex++){
                    //get all the events associated with this cow
                    Cow currentCow = allCows.get(cowIndex);
                    List<Event> cowEvents = currentCow.getEvents();
                    for(int eventIndex = 0; eventIndex < cowEvents.size(); eventIndex++){
                        Event currentEvent = cowEvents.get(eventIndex);
                        if(currentEvent.isServicingEvent()){
                            servicingIDs.add(currentEvent.getId());
                            servicingTypes.add(currentEvent.getType());
                            String name = currentCow.getEarTagNumber();
                            if(name == null || name.length() == 0){
                                name = currentCow.getName();
                            }
                            servicingNames.add(currentEvent.getEventDate()+" (" + name + ")");
                        }
                    }
                }

                ArrayAdapter<String> servicingsArrayAdapter=new ArrayAdapter<String>(AddEventActivity.this,android.R.layout.simple_spinner_dropdown_item,servicingNames);
                servicingsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                servicingS.setAdapter(servicingsArrayAdapter);
            }
        }
    }
    
}
