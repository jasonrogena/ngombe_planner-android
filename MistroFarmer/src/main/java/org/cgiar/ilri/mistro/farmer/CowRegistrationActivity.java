package org.cgiar.ilri.mistro.farmer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.cgiar.ilri.mistro.farmer.backend.Locale;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CowRegistrationActivity extends SherlockActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    public static final String TAG="CowRegistrationActivity";
    public static final String KEY_INDEX="index";
    public static final String KEY_NUMBER_OF_COWS="numberOfCows";
    private final String dateFormat="dd/MM/yyyy";
    private TextView nameTV;
    private EditText nameET;
    private TextView earTagNumberTV;
    private EditText earTagNumberET;
    private TextView ageTV;
    private Spinner ageS;
    private EditText ageET;
    private TextView dateOfBirthTV;
    private EditText dateOfBirthET;
    private TextView breedTV;
    private EditText breedET;
    private TextView sexTV;
    private Spinner sexS;
    private TextView deformityTV;
    private EditText deformityET;
    private TextView serviceTypeTV;
    private Spinner serviceTypeS;
    private TextView sireTV;
    private Spinner sireS;
    private TextView strawNumberTV;
    private EditText strawNumberET;
    private TextView damTV;
    private Spinner damS;
    private TextView embryoNumberTV;
    private EditText embryoNumberET;
    private TextView countryOfOriginTV;
    private AutoCompleteTextView countryOfOriginACTV;
    private Button previousButton;
    private Button nextButton;
    private DatePickerDialog datePickerDialog;

    private int index;
    private int numberOfCows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_registration);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            index=bundle.getInt(KEY_INDEX);
            numberOfCows=bundle.getInt(KEY_NUMBER_OF_COWS);
        }

        //init views
        nameTV=(TextView)this.findViewById(R.id.name_tv);
        nameET=(EditText)this.findViewById(R.id.name_et);
        earTagNumberTV=(TextView)this.findViewById(R.id.ear_tag_number_tv);
        earTagNumberET=(EditText)this.findViewById(R.id.ear_tag_number_et);
        ageTV=(TextView)this.findViewById(R.id.age_tv);
        ageS=(Spinner)this.findViewById(R.id.age_s);
        ageET=(EditText)this.findViewById(R.id.age_et);
        dateOfBirthTV=(TextView)this.findViewById(R.id.date_of_birth_tv);
        dateOfBirthET=(EditText)this.findViewById(R.id.date_of_birth_et);
        //dateOfBirthET.setOnFocusChangeListener(this);
        dateOfBirthET.setOnClickListener(this);
        breedTV=(TextView)this.findViewById(R.id.breed_tv);
        breedET=(EditText)this.findViewById(R.id.breed_et);
        //breedET.setOnFocusChangeListener(this);
        breedET.setOnClickListener(this);
        sexTV=(TextView)this.findViewById(R.id.sex_tv);
        sexS=(Spinner)this.findViewById(R.id.sex_s);
        deformityTV=(TextView)this.findViewById(R.id.deformity_tv);
        deformityET=(EditText)this.findViewById(R.id.deformity_et);
        //deformityET.setOnFocusChangeListener(this);
        deformityET.setOnClickListener(this);
        serviceTypeTV = (TextView)this.findViewById(R.id.service_type_tv);
        serviceTypeS = (Spinner)this.findViewById(R.id.service_type_s);
        sireTV = (TextView)this.findViewById(R.id.sire_tv);
        sireS = (Spinner)this.findViewById(R.id.sire_s);
        strawNumberTV = (TextView)this.findViewById(R.id.straw_number_tv);
        strawNumberET = (EditText)this.findViewById(R.id.straw_number_et);
        damTV = (TextView)this.findViewById(R.id.dam_tv);
        damS = (Spinner)this.findViewById(R.id.dam_s);
        embryoNumberTV = (TextView)this.findViewById(R.id.embryo_number_tv);
        embryoNumberET = (EditText)this.findViewById(R.id.embryo_number_et);
        countryOfOriginTV = (TextView)this.findViewById(R.id.country_of_origin_tv);
        countryOfOriginACTV = (AutoCompleteTextView)this.findViewById(R.id.country_of_origin_actv);
        previousButton = (Button)this.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);
        nextButton = (Button)this.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.cow_registration, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.action_english) {
            Locale.switchLocale(Locale.LOCALE_ENGLISH, this);
            initTextInViews();
            return true;
        }
        else if(item.getItemId() == R.id.action_swahili) {
            Locale.switchLocale(Locale.LOCALE_SWAHILI, this);
            initTextInViews();
            Toast.makeText(this, "kazi katika maendeleo", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTextInViews() {
        //TODO: set Title
        nameTV.setText(Locale.getStringInLocale("name",this));
        earTagNumberTV.setText(Locale.getStringInLocale("ear_tag_number",this));
        ageTV.setText(Locale.getStringInLocale("age",this));
        int ageTypeArrayID = Locale.getArrayIDInLocale("age_type_array",this);
        if(ageTypeArrayID!=0){
            ArrayAdapter<CharSequence> ageTypeArrayAdapter=ArrayAdapter.createFromResource(this, ageTypeArrayID, android.R.layout.simple_spinner_item);
            ageTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageS.setAdapter(ageTypeArrayAdapter);
        }
        dateOfBirthTV.setText(Locale.getStringInLocale("date_of_birth",this));
        breedTV.setText(Locale.getStringInLocale("breed",this));
        sexTV.setText(Locale.getStringInLocale("sex",this));
        int sexArrayID = Locale.getArrayIDInLocale("sex_array",this);
        if(sexArrayID!=0) {
            ArrayAdapter<CharSequence> sexArrayAdapter=ArrayAdapter.createFromResource(this, sexArrayID, android.R.layout.simple_spinner_item);
            sexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexS.setAdapter(sexArrayAdapter);
        }
        deformityTV.setText(Locale.getStringInLocale("deformity",this));
        serviceTypeTV.setText(Locale.getStringInLocale("service_type_used",this));
        int serviceTypesSireArrayID = Locale.getArrayIDInLocale("service_types",this);
        if(serviceTypesSireArrayID!=0){
            ArrayAdapter<CharSequence> serviceTypesAdapter = ArrayAdapter.createFromResource(this,serviceTypesSireArrayID,android.R.layout.simple_spinner_item);
            serviceTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceTypeS.setAdapter(serviceTypesAdapter);
        }
        sireTV.setText(Locale.getStringInLocale("sire",this));
        damTV.setText(Locale.getStringInLocale("dam",this));
        strawNumberTV.setText(Locale.getStringInLocale("straw_number",this));
        embryoNumberTV.setText(Locale.getStringInLocale("embryo_number",this));
        countryOfOriginTV.setText(Locale.getStringInLocale("country_of_origin",this));
        previousButton.setText(Locale.getStringInLocale("previous",this));
        nextButton.setText(Locale.getStringInLocale("next",this));
    }

    @Override
    public void onClick(View view) {
        if(view==previousButton) {

        }
        else if(view==nextButton) {

        }
        else if(view==dateOfBirthET) {
            dateOfBirthETClicked();
        }
        else if(view==breedET) {

        }
        else if(view==deformityET) {

        }
    }

    private void dateOfBirthETClicked() {
        Date date=null;
        if(dateOfBirthET.getText().toString().length()>0) {
            try {
                date=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateOfBirthET.getText().toString());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(date==null) {
            date=new Date();
        }
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);//TODO: this might be a bug
        dateOfBirthET.setText(dateString);
        setAgeFromDate(dateString);
    }

    private void setAgeFromDate(String dateString) {
        Log.d(TAG, "date entered : " + dateString);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(dateFormat);
        Date enteredDate=new Date();
        try
        {
            enteredDate=simpleDateFormat.parse(dateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Date today=new Date();
        Log.d(TAG,"today's date : "+today.toString());
        long millisecondDifference=today.getTime()-enteredDate.getTime();
        Log.d(TAG,"millisecond difference : "+String.valueOf(millisecondDifference));
        String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
        if(millisecondDifference>0&&millisecondDifference<604800000L)//less than one week
        {
            int days=(int)(millisecondDifference/86400000L);
            ageET.setText(String.valueOf(days));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Day")) {
                    ageS.setSelection(i);
                }
            }
        }
        else if(millisecondDifference>=604800000L&&millisecondDifference<31557600000L)//less than a year
        {
            int weeks=(int)(millisecondDifference/604800000L);
            ageET.setText(String.valueOf(weeks));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Weeks")) {
                    ageS.setSelection(i);
                }
            }
        }
        else if(millisecondDifference>=31557600000L)//a year or greater
        {
            int years=(int)(millisecondDifference/31557600000L);
            ageET.setText(String.valueOf(years));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Years")) {
                    ageS.setSelection(i);
                }
            }
        }
    }
}
