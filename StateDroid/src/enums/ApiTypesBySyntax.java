package enums;

public enum ApiTypesBySyntax {

	java, // StringBuilder -> toString()
	android, // Even though first two are also android APIs, we just consider them as different for easier evaluation.
	dalvik,
	otherlibrary,
	userdefined, // sendsms() or addTwoNumbers(a,b).
	nativemethod,
}
