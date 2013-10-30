package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.spinner.Spinner;
import java.util.Date;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Dam;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.Sire;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.GeneralArrays;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class CowRegistrationScreen extends Form implements Screen{

    private final Midlet midlet;
    private final int locale;
    private final int cowIndex;
    private final int cowNumber;
    private final Farmer farmer;
    private Vector validSires;
    private Vector validSireNames;
    private Vector validDams;
    private Vector validDamNames;
    
    private Command previousCommand;
    private Command nextCommand;
    
    private BoxLayout parentBoxLayout;
    private Label cowNameL;
    private TextField cowNameTF;
    private Label earTagNumberL;
    private TextField earTagNumberTF;
    private Label ageL;
    private TextField ageTF;
    private Label ageTypeL;
    private ComboBox ageTypeCB;
    //private Label dateOfBirthL;
    //private Spinner dateOfBirthS;
    private Label breedL;
    private ComboBox breedCB;
    private MultiselectRenderer breedMultiselectRenderer;
    private Label sexL;
    private ComboBox sexCB;
    private Label deformityL;
    private ComboBox deformityCB;
    private MultiselectRenderer deformityMultiselectRenderer;
    private Label serviceTypeL;
    private ComboBox serviceTypeCB;
    private Label sireL;
    private ComboBox sireCB;
    private Label damL;
    private ComboBox damCB;
    private Label countryL;
    private ComboBox countryCB;
    public CowRegistrationScreen(Midlet midlet, int locale, Farmer farmer, int cowIndex, int cowNumber) {
        super(Locale.getStringInLocale(locale, StringResources.cow_registration_wth_indx)+String.valueOf(cowIndex+1));
        
        this.midlet = midlet;
        this.locale = locale;
        this.cowIndex = cowIndex;
        this.cowNumber = cowNumber;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        previousCommand = new Command(Locale.getStringInLocale(locale, StringResources.previous));
        this.addCommand(previousCommand);
        
        if(cowIndex < (cowNumber-1))
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        else
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.finish));
        this.addCommand(nextCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {  
                if(evt.getCommand().equals(previousCommand)) {
                    saveCowDetails();
                    if(CowRegistrationScreen.this.cowIndex>0){
                        CowRegistrationScreen previousScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer, CowRegistrationScreen.this.cowIndex - 1, CowRegistrationScreen.this.cowNumber);
                        previousScreen.start();
                    }
                    else {
                        FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer);
                        farmerRegistrationScreen.start();
                    }
                }
                else if(evt.getCommand().equals(nextCommand)) {
                    if(validateInput()) {
                        saveCowDetails();;
                        if(CowRegistrationScreen.this.cowIndex < (CowRegistrationScreen.this.cowNumber -1)) {
                            CowRegistrationScreen nextScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer, CowRegistrationScreen.this.cowIndex + 1, CowRegistrationScreen.this.cowNumber);
                            nextScreen.start();
                        }
                    }
                }
            }
        });
        
        cowNameL = new Label(Locale.getStringInLocale(locale, StringResources.name));
        setLabelStyle(cowNameL);
        this.addComponent(cowNameL);
        
        cowNameTF = new TextField();
        setComponentStyle(cowNameTF, false);
        this.addComponent(cowNameTF);
        
        earTagNumberL = new Label(Locale.getStringInLocale(locale, StringResources.ear_tag_number));
        setLabelStyle(earTagNumberL);
        this.addComponent(earTagNumberL);
        
        earTagNumberTF = new TextField();
        setComponentStyle(earTagNumberTF, false);
        this.addComponent(earTagNumberTF);
        
        ageL = new Label(Locale.getStringInLocale(locale, StringResources.age));
        setLabelStyle(ageL);
        this.addComponent(ageL);
        
        ageTF = new TextField();
        setComponentStyle(ageTF, false);
        ageTF.setConstraint(TextField.NUMERIC);
        ageTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(ageTF);
        
        ageTypeL = new Label(Locale.getStringInLocale(locale, StringResources.age_type));
        setLabelStyle(ageTypeL);
        this.addComponent(ageTypeL);
        
        ageTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array));
        setComponentStyle(ageTypeCB, true);
        ageTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array)));
        this.addComponent(ageTypeCB);
        
        /*dateOfBirthL = new Label(Locale.getStringInLocale(locale, StringResources.date_of_birth));
        setLabelStyle(dateOfBirthL);
        this.addComponent(dateOfBirthL);*/
        
        /*dateOfBirthS = Spinner.createDate(System.currentTimeMillis() - (31536000730l*50), System.currentTimeMillis(), System.currentTimeMillis(), '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        setComponentStyle(dateOfBirthS, true);
        dateOfBirthS.getSelectedStyle().setFgColor(0x2ecc71);
        this.addComponent(dateOfBirthS);*/
        
        breedL = new Label(Locale.getStringInLocale(locale, StringResources.breed));
        setLabelStyle(breedL);
        this.addComponent(breedL);
        
        breedCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array));
        setComponentStyle(breedCB, true);
        breedMultiselectRenderer = new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array),4);
        breedCB.setRenderer(breedMultiselectRenderer);
        breedCB.addActionListener(breedMultiselectRenderer);
        this.addComponent(breedCB);
        
        sexL = new Label(Locale.getStringInLocale(locale, StringResources.sex));
        setLabelStyle(sexL);
        this.addComponent(sexL);
        
        sexCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array));
        setComponentStyle(sexCB, true);
        sexCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array)));
        this.addComponent(sexCB);
        
        deformityL = new Label(Locale.getStringInLocale(locale, StringResources.deformity));
        setLabelStyle(deformityL);
        this.addComponent(deformityL);
        
        deformityCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array));
        deformityMultiselectRenderer = new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array),0);
        deformityCB.setRenderer(deformityMultiselectRenderer);
        deformityCB.addActionListener(deformityMultiselectRenderer);
        setComponentStyle(deformityCB, true);
        //deformityCB.setRenderer(new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array)));
        this.addComponent(deformityCB);
        
        serviceTypeL = new Label(Locale.getStringInLocale(locale, StringResources.service_type_used));
        setLabelStyle(serviceTypeL);
        this.addComponent(serviceTypeL);
        
        serviceTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.service_types));
        setComponentStyle(serviceTypeCB, true);
        serviceTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.service_types)));
        this.addComponent(serviceTypeCB);
        
        sireL = new Label(Locale.getStringInLocale(locale, StringResources.sire));
        setLabelStyle(sireL);
        this.addComponent(sireL);
        
        String[] sireNames = getValidSires();
        sireCB = new ComboBox(sireNames);
        setComponentStyle(sireCB, true);
        sireCB.setRenderer(new MistroListCellRenderer(sireNames));
        this.addComponent(sireCB);
        
        damL = new Label(Locale.getStringInLocale(locale, StringResources.dam));
        setLabelStyle(damL);
        this.addComponent(damL);
        
        String[] damNames = getValidDams();
        damCB = new ComboBox(damNames);
        setComponentStyle(damCB, true);
        damCB.setRenderer(new MistroListCellRenderer(damNames));
        this.addComponent(damCB);
        
        countryL = new Label(Locale.getStringInLocale(locale, StringResources.sire_country_of_origin));
        setLabelStyle(countryL);
        this.addComponent(countryL);
        
        countryCB = new ComboBox(GeneralArrays.all_countries);
        setComponentStyle(countryCB, true);
        countryCB.setRenderer(new MistroListCellRenderer(GeneralArrays.all_countries));
        this.addComponent(countryCB);
        
        
        restoreCowDetails();
    }
    
    private void setLabelStyle(Label label){
        label.getStyle().setMargin(10, 0, 10, 0);
        label.getSelectedStyle().setMargin(10, 0, 10,0);
    }
    
    private void setComponentStyle(Component component, boolean isFocusable){
        component.getStyle().setMargin(5, 0, 0, 0);
        component.getSelectedStyle().setMargin(5, 0, 0, 0);
        if(isFocusable){
            component.getSelectedStyle().setBgColor(0x2ecc71);
        }
    }
    
    private boolean validateInput(){
        final Dialog infoDialog = new Dialog();
        infoDialog.setDialogType(Dialog.TYPE_INFO);
        final Command backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        infoDialog.addCommand(backCommand);
        infoDialog.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    infoDialog.dispose();
                }
            }
        });
        Label text = new Label();
        text.getStyle().setAlignment(CENTER);
        infoDialog.addComponent(text);
        
        if(earTagNumberTF.getText()== null || earTagNumberTF.getText().trim().length()==0){
            earTagNumberTF.requestFocus();
            text.setText(Locale.getStringInLocale(locale, StringResources.enter_ear_tag_number));
            infoDialog.show(100, 100, 11, 11, true);
            return false;
        }
        return true;
    }
    
    private void saveCowDetails() {
        Cow thisCow = farmer.getCow(cowIndex);
        thisCow.setMode(Cow.MODE_ADULT_COW_REGISTRATION);
        thisCow.setName(cowNameTF.getText());
        thisCow.setEarTagNumber(earTagNumberTF.getText());
        if(ageTF.getText()!=null && ageTF.getText().trim().length() > 0){
            thisCow.setAge(Integer.parseInt(ageTF.getText().trim()));
        }
        String[] ageTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.age_type_array);
        thisCow.setAgeType(ageTypesInEN[ageTypeCB.getSelectedIndex()]);
        //thisCow.setDateOfBirth((Date) dateOfBirthS.getValue());
        thisCow.setBreeds(breedMultiselectRenderer.getSelectedItems());
        String[] sexInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.sex_array);
        thisCow.setSex(sexInEN[sexCB.getSelectedIndex()]);
        thisCow.setDeformities(deformityMultiselectRenderer.getSelectedItems());
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.service_types);
        thisCow.setServiceType(serviceTypesInEN[serviceTypeCB.getSelectedIndex()]);
        
        Sire sire = new Sire();
        Cow selectedSire = (Cow)validSires.elementAt(sireCB.getSelectedIndex());
        sire.setName(selectedSire.getName());
        sire.setEarTagNumber(selectedSire.getEarTagNumber());
        
        Dam dam = new Dam();
        Cow selectedDam = (Cow)validDams.elementAt(damCB.getSelectedIndex());
        dam.setName(selectedDam.getName());
        dam.setEarTagNumber(selectedDam.getEarTagNumber());
        thisCow.setDam(dam);
        
        //set country of origin to sire not this cow
        String[] countries = GeneralArrays.all_countries;
        sire.setCountryOfOrigin(countries[countryCB.getSelectedIndex()]);
        thisCow.setSire(sire);
    }
    
    private void restoreCowDetails() {
        Cow thisCow = farmer.getCow(cowIndex);
        
        cowNameTF.setText(thisCow.getName());
        earTagNumberTF.setText(thisCow.getEarTagNumber());
        if(thisCow.getAge()!=-1)
            ageTF.setText(String.valueOf(thisCow.getAge()));
        String[] ageTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.age_type_array);
        for(int i = 0; i < ageTypesInEN.length; i ++) {
            if(ageTypesInEN[i].equals(thisCow.getAgeType())){
                ageTypeCB.setSelectedIndex(i);
            }
        }
        
        /*long dateOfBirthLong = thisCow.getDateOfBirthMilliseconds();
        if(dateOfBirthLong != -1){
            dateOfBirthS.setValue(new Long(dateOfBirthLong));//TODO: not sure this will work
        }
        else{
            dateOfBirthS.setValue(new Long(System.currentTimeMillis()));
        }*/
        
        String[] breedsInEN = Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array);
        //for each breed, check if breed in cow's breed array
        String[] cowBreeds = thisCow.getBreeds();
        int noSelectedBreeds = 0;
        if(cowBreeds!=null){
            for(int i = 0; i < breedsInEN.length; i++) {
                for(int j = 0; j < cowBreeds.length; j++) {
                    if(cowBreeds[j].equals(breedsInEN[i])){
                        noSelectedBreeds++;
                    }
                }
            }
            int[] cowBreedsIndexes = new int[noSelectedBreeds];
            int count=0;
            for(int i = 0; i < breedsInEN.length; i++) {
                for(int j = 0; j < cowBreeds.length; j++) {
                    if(cowBreeds[j].equals(breedsInEN[i])){
                        cowBreedsIndexes[count] = i;
                        count++;
                    }
                }
            }
            if(cowBreedsIndexes.length > 0)
                breedMultiselectRenderer.setSelectedItems(cowBreedsIndexes);
        }
        
        
        String[] sexInEN = Locale.getStringArrayInLocale(locale, ArrayResources.sex_array);
        for(int i = 0; i < sexInEN.length; i++){
            if(sexInEN[i].equals(thisCow.getSex())){
                sexCB.setSelectedIndex(i);
            }
        }
        
        String[] deformitiesInEN = Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array);
        String[] cowDeformities = thisCow.getDeformities();
        int noDeformities = 0;
        if(cowDeformities!=null){
            for(int i = 0; i < deformitiesInEN.length; i++ ){
                for(int j = 0; j < cowDeformities.length; j++){
                    if(deformitiesInEN[i].equals(cowDeformities[j])){
                        noDeformities++;
                    }
                }
            }
            int[] cowDeformitiesIndexes = new int[noDeformities];
            int count = 0;
            for(int i = 0; i < deformitiesInEN.length; i++ ){
                for(int j = 0; j < cowDeformities.length; j++){
                    if(deformitiesInEN[i].equals(cowDeformities[j])){
                        cowDeformitiesIndexes[count] = i;
                        count++;
                    }
                }
            }
            if(cowDeformitiesIndexes.length>0)
                deformityMultiselectRenderer.setSelectedItems(cowDeformitiesIndexes);
        }
        
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(locale, ArrayResources.service_types);
        for(int i =0; i < serviceTypesInEN.length; i++){
            if(serviceTypesInEN[i].equals(thisCow.getServiceType())){
                serviceTypeCB.setSelectedIndex(i);
            }
        }
        
        Sire cowSire = thisCow.getSire();
        if(cowSire != null) {
            for(int i = 0; i < validSires.size(); i++){
                Cow currentSire = (Cow)validSires.elementAt(i);
                if(currentSire.getName().equals(cowSire.getName()) && currentSire.getEarTagNumber().equals(cowSire.getEarTagNumber())){
                    sireCB.setSelectedIndex(i);
                }
            }
        }
        
        Dam cowDam = thisCow.getDam();
        if(cowDam != null) {
            for(int i = 0; i < validDams.size(); i++){
                Cow currentDam = (Cow)validDams.elementAt(i);
                if(currentDam.getName().equals(cowDam.getName()) && currentDam.getEarTagNumber().equals(cowDam.getEarTagNumber())){
                    sireCB.setSelectedIndex(i);
                }
            }
        }
        
        //set countryCB to sire's country of origin
        if(cowSire!=null){
            String[] countries = GeneralArrays.all_countries;
            for(int i = 0; i<countries.length;i++){
                if(countries[i].equals(cowSire.getCountryOfOrigin())){
                    countryCB.setSelectedIndex(i);
                }
            }
        }
    }
    
    private String[] getValidSires() {
        Cow[] allCows = farmer.getCows();
        
        validSires = new Vector(allCows.length);
        validSireNames = new Vector(allCows.length);
        //add placibo
        validSires.addElement(new Cow(true));
        validSireNames.addElement(" ");
        
        for(int i = 0;i < allCows.length; i++) {
            Cow currentCow = allCows[i];
            if(i != cowIndex && currentCow.getEarTagNumber() != null && currentCow.getEarTagNumber().length() > 0 && currentCow.getSex().equals(Cow.SEX_MALE)) {
                validSires.addElement(currentCow);
                if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                    validSireNames.addElement((currentCow.getEarTagNumber()+" ("+currentCow.getName()+")"));
                else
                    validSireNames.addElement(currentCow.getEarTagNumber());
            }
        }
        
        String[] sireNames = new String[validSireNames.size()];
        for(int i = 0; i < validSireNames.size(); i++) {
            sireNames[i] = (String)validSireNames.elementAt(i);
        }
        
        return sireNames;
    }
    
    private String[] getValidDams() {
        Cow[] allCows = farmer.getCows();
        
        validDams = new Vector(allCows.length);
        validDamNames = new Vector(allCows.length);
        //add placibo
        validDams.addElement(new Cow(true));
        validDamNames.addElement(" ");
        
        for(int i = 0;i < allCows.length; i++) {
            Cow currentCow = allCows[i];
            if(i != cowIndex && currentCow.getEarTagNumber() != null && currentCow.getEarTagNumber().length() > 0 && currentCow.getSex().equals(Cow.SEX_FEMALE)) {
                validDams.addElement(currentCow);
                if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                    validDamNames.addElement((currentCow.getEarTagNumber()+" ("+currentCow.getName()+")"));
                else
                    validDamNames.addElement((currentCow.getEarTagNumber()));
            }
        }
        
        String[] damNames = new String[validDamNames.size()];
        for(int i = 0; i < validDamNames.size(); i++) {
            damNames[i] = (String)validDamNames.elementAt(i);
        }
        
        return damNames;
    }
    
    public void start() {
        this.show();
    }

    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}