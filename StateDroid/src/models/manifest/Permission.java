package models.manifest;

public class Permission {

	private String name;

	//Will add other fields later, as per requirement.

	//	   <permission android:name="com.google.android.hangouts.START_HANGOUT" android:protectionLevel="signature"/>
	//	    <uses-permission android:name="android.permission.INTERNET"/>

	public Permission(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
