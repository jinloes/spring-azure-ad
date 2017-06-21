package com.jinloes.springazuread;


import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jinloes on 6/21/17.
 */
@XmlRootElement
public class User extends DirectoryObject{

	// The following are the individual private members of a User object that holds
	// a particular simple attribute of a User object.
	protected String objectId;
	protected String objectType;
	protected String accountEnabled;
	protected String city;
	protected String country;
	protected String department;
	protected String dirSyncEnabled;
	protected String displayName;
	protected String facsimileTelephoneNumber;
	protected String givenName;
	protected String jobTitle;
	protected String lastDirSyncTime;
	protected String mail;
	protected String mailNickname;
	protected String mobile;
	protected String password;
	protected String passwordPolicies;
	protected String physicalDeliveryOfficeName;
	protected String postalCode;
	protected String preferredLanguage;
	protected String state;
	protected String streetAddress;
	protected String surname;
	protected String telephoneNumber;
	protected String usageLocation;
	protected String userPrincipalName;
	protected boolean isDeleted;  // this will move to dto

	/**
	 * These four properties are for future use.
	 */
	// managerDisplayname of this user.
	protected String managerDisplayname;

	// The directReports holds a list of directReports.
	private ArrayList<User> directReports;

	// The groups holds a list of group entities this user belongs to.
	private ArrayList<Group> groups;

	// The roles holds a list of role entities this user belongs to.
	private ArrayList<Group> roles;

	/**
	 * The constructor for the **User** class. Initializes the dynamic lists and managerDisplayname variables.
	 */
	public User(){
		directReports = null;
		groups = new ArrayList<Group>();
		roles = new ArrayList<Group>();
		managerDisplayname = null;
	}
//
//    public User(String displayName, String objectId){
//        setDisplayName(displayName);
//        setObjectId(objectId);
//    }
//
//    public User(String displayName, String objectId, String userPrincipalName, String accountEnabled){
//        setDisplayName(displayName);
//        setObjectId(objectId);
//        setUserPrincipalName(userPrincipalName);
//        setAccountEnabled(accountEnabled);
//    }
//

	/**
	 * @return The objectId of this user.
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId The objectId to set to this User object.
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return The objectType of this user.
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType The objectType to set to this User object.
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return The userPrincipalName of this user.
	 */
	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	/**
	 * @param userPrincipalName The userPrincipalName to set to this User object.
	 */
	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	/**
	 * @return The usageLocation of this user.
	 */
	public String getUsageLocation() {
		return usageLocation;
	}

	/**
	 * @param usageLocation The usageLocation to set to this User object.
	 */
	public void setUsageLocation(String usageLocation) {
		this.usageLocation = usageLocation;
	}

	/**
	 * @return The telephoneNumber of this user.
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * @param telephoneNumber The telephoneNumber to set to this User object.
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * @return The surname of this user.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname The surname to set to this User object.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return The streetAddress of this user.
	 */
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * @param streetAddress The streetAddress to set to this user.
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * @return The state of this user.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state The state to set to this User object.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return The preferredLanguage of this user.
	 */
	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	/**
	 * @param preferredLanguage The preferredLanguage to set to this user.
	 */
	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	/**
	 * @return The postalCode of this user.
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode The postalCode to set to this user.
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return The physicalDeliveryOfficeName of this user.
	 */
	public String getPhysicalDeliveryOfficeName() {
		return physicalDeliveryOfficeName;
	}

	/**
	 * @param physicalDeliveryOfficeName The physicalDeliveryOfficeName to set to this User object.
	 */
	public void setPhysicalDeliveryOfficeName(String physicalDeliveryOfficeName) {
		this.physicalDeliveryOfficeName = physicalDeliveryOfficeName;
	}

	/**
	 * @return The passwordPolicies of this user.
	 */
	public String getPasswordPolicies() {
		return passwordPolicies;
	}

	/**
	 * @param passwordPolicies The passwordPolicies to set to this User object.
	 */
	public void setPasswordPolicies(String passwordPolicies) {
		this.passwordPolicies = passwordPolicies;
	}

	/**
	 * @return The mobile of this user.
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile The mobile to set to this User object.
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return The password of this user.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The mobile to set to this User object.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return The mail of this user.
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail The mail to set to this User object.
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return The MailNickname of this user.
	 */
	public String getMailNickname() {
		return mailNickname;
	}

	/**
	 * @param mail The MailNickname to set to this User object.
	 */
	public void setMailNickname(String mailNickname) {
		this.mailNickname = mailNickname;
	}

	/**
	 * @return The jobTitle of this user.
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param jobTitle The jobTitle to set to this User object.
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	/**
	 * @return The givenName of this user.
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName The givenName to set to this User object.
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return The facsimileTelephoneNumber of this user.
	 */
	public String getFacsimileTelephoneNumber() {
		return facsimileTelephoneNumber;
	}

	/**
	 * @param facsimileTelephoneNumber The facsimileTelephoneNumber to set to this User object.
	 */
	public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber) {
		this.facsimileTelephoneNumber = facsimileTelephoneNumber;
	}

	/**
	 * @return The displayName of this user.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set to this User object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return The dirSyncEnabled of this user.
	 */
	public String getDirSyncEnabled() {
		return dirSyncEnabled;
	}

	/**
	 * @param dirSyncEnabled The dirSyncEnabled to set to this User object.
	 */
	public void setDirSyncEnabled(String dirSyncEnabled) {
		this.dirSyncEnabled = dirSyncEnabled;
	}

	/**
	 * @return The department of this user.
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department The department to set to this User object.
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return The lastDirSyncTime of this user.
	 */
	public String getLastDirSyncTime() {
		return lastDirSyncTime;
	}

	/**
	 * @param lastDirSyncTime The lastDirSyncTime to set to this User object.
	 */
	public void setLastDirSyncTime(String lastDirSyncTime) {
		this.lastDirSyncTime = lastDirSyncTime;
	}

	/**
	 * @return The country of this user.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country The country to set to this user.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return The city of this user.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city The city to set to this user.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return The accountEnabled attribute of this user.
	 */
	public String getAccountEnabled() {
		return accountEnabled;
	}

	/**
	 * @param accountEnabled The accountEnabled to set to this user.
	 */
	public void setAccountEnabled(String accountEnabled) {
		this.accountEnabled = accountEnabled;
	}

	public boolean isIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}

	public String getManagerDisplayname(){
		return managerDisplayname;
	}

	public void setManagerDisplayname(String managerDisplayname){
		this.managerDisplayname = managerDisplayname;
	}
}

