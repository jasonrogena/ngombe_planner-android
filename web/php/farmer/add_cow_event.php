<?php
include '../common/log.php';
include '../common/database.php';

class CowEventHandler {

	private $TAG = "add_cow_event.php";
	private $ROOT = "../../";
	private $settingsDir;
	private $settings;
	private $codes;
	private $jsonObject;
	private $logHandler;
	private $database;
	
	public function __construct() {
		$this->settingsDir = $this->ROOT."config/settings.ini";
		$this->logHandler = new LogHandler;
		$this->logHandler->log(3, $this->TAG,"Starting CowEventHandler");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		//get the cowID
		$simCardSN = $this->jsonObject['simCardSN'];
		$cowEarTagNumber = $this->jsonObject['cowEarTagNumber'];
		$cowName = $this->jsonObject['cowName'];
		$query = "SELECT `cow`.`id` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
		$result = $this->database->runMySQLQuery($query, true);
		$cowID = $result[0]['id'];
		
		//add event to database
		$eventTypeID = $this->getEventTypeID();
		$eventDate = $this->jsonObject['date'];
		$remarks = $this->jsonObject['remarks'];
		$time = $this->getTime("EAT");
		$query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}')";
		$this->database->runMySQLQuery($query, false);
		$this->logHandler->log(3, $this->TAG,"returning response code ".$this->codes['acknowledge_ok']);
		echo $this->codes['acknowledge_ok'];
		$this->logHandler->log(3, $this->TAG,"gracefully exiting");
	}

	private function getTime($timeZone) {
		$time = new DateTime('now', new DateTimeZone($timeZone));
		return $time->format('Y-m-d H:i:s');
	}

	private function getSettings() {
		$this->logHandler->log(3, $this->TAG,"getting settings from: ".$this->settingsDir);
      if(file_exists($this->settingsDir)) {
         $settings = parse_ini_file($this->settingsDir);
         $mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
         $this->settings = $settings;
         $this->logHandler->log(4, $this->TAG,"settings obtained: ".print_r($this->settings, true));
      }
      else {
		  $this->logHandler->log(1, $this->TAG,"unable to get settings from ".$this->settingsDir.", exiting");
         die();
      }
   }

   private function getCodes() {
	   $responseCodesLocation = $this->ROOT."config/".$this->settings['response_codes'];
	   $this->logHandler->log(3, $this->TAG,"getting response codes from: ".$responseCodesLocation);
	   if(file_exists($responseCodesLocation)) {
		   $this->codes = parse_ini_file($responseCodesLocation);
		   $this->logHandler->log(4, $this->TAG,"response codes are: ".print_r($this->codes, true));
	   }
	   else {
		  $this->logHandler->log(1, $this->TAG,"unable to get response codes from ".$responseCodesLocation.", exiting");
         die();
      }
   }
   
   private function getEventTypeID() {
	   $eventTypeName = $this->jsonObject['eventType'];
	   $query = "SELECT `id` FROM `event` WHERE `name` = '{$eventTypeName}'";
	   $result = $this->database->runMySQLQuery($query, true);
	   if(sizeOf($result)==1) {
		   $this->logHandler->log(4, $this->TAG, "fetched event type ID for ".$eventTypeName." which is ".$result[0]['id']);
		   return $result[0]['id'];
	   }
	   else {
		   $this->logHandler->log(1, $this->TAG, "it appears like there is no event type by the name '".$eventTypeName."' or more than one event type goes by this name, exiting");
		   die();
	   }
   }
   
   private function getPOSTJsonObject() {
	   $this->logHandler->log(3, $this->TAG,"obtaining POST request");
	   $this->jsonObject=json_decode($_POST["json"],true);
	   $this->logHandler->log(4, $this->TAG,"json_decode returned: ".print_r($this->jsonObject, true));
	}
}
$obj = new CowEventHandler;
?>