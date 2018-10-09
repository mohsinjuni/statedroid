package decompilation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import models.cfg.APK;
import models.cfg.BasicBlock;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.MethodSignature;
import models.cfg.Package;
import models.cfg.Parameter;
import models.manifest.AndroidManifest;
import models.manifest.Application;
import models.manifest.ComponentManifest;
import models.manifest.IntentFilter;
import models.manifest.Permission;

import org.apache.log4j.Logger;

import configuration.Config;
import enums.ComponentTypes;
import enums.MethodTypes;

public class ManifestParser {

	private static Logger logger;
	//	private Hashtable componentsList;
	private Hashtable compList;

	private Hashtable activityCallbacks;
	private Hashtable serviceCallbacks;
	private Hashtable broadcastReceiverCallbacks;
	private Hashtable asynchTaskCallbacks;
	private Hashtable threadCallbacks;
	private HashSet<String> viewGroupCallbackSet;
	private HashSet<String> webViewCallbackSet;

	public Hashtable getCompList() {
		return compList;
	}

	public void setCompList(Hashtable compList) {
		this.compList = compList;
	}

	public ManifestParser()
	{
		compList = new Hashtable();
		activityCallbacks= Config.getInstance().getActivityCallbackList() ;
		serviceCallbacks = Config.getInstance().getServiceCallbackList();
		broadcastReceiverCallbacks = Config.getInstance().getBroadcastReceiverCallbackList();
		asynchTaskCallbacks = Config.getInstance().getAsynchTaskCallbackList();
		threadCallbacks = Config.getInstance().getThreadCallbackList();
		viewGroupCallbackSet = Config.getInstance().getViewGroupCallbacks();
		webViewCallbackSet = Config.getInstance().getWebViewCallbacks();
	}

	public APK getManifestInfo(String apkfilePath, APK apk)  // Path to input .apk file. The same folder will have .apk.txt, and .apk.xml file
	{

		//TODO, find app's package name correctly.
		//TODO: need code refactoring to do it in a better way.

		String manifestFilePath = apkfilePath.concat(".xml");

		File f = new File(manifestFilePath);

		if(f.exists() && checkIfManifestFileIsEmpty(f)) 
		{
			AndroidManifest manifest = getParsedManifest(manifestFilePath);
			manifest.setUsedPermAsStrings();
			manifest.getApplication().setComponentList();

			apk.setAndroidManifest(manifest); //TODO For now, I will keep a copy with apk also, but I should remove it laters.
		}
		apk = setChildrenTypesBasedOnParents(apk);
		//			apk = identifyComponentsUsingManifest(apk); //Though don't need to return the object, but just making sure, we get the latest copy.

		//			apk.getAndroidManifest().printAllComponents();
		addDynamicComponents(apk);

		addInlinedClasses(apk);
		apk = setApplicationComponent(apk);

		apk = markComponentCallbacks(apk);

		//			apk.getAndroidManifest().printAllComponents();
		apk = setParentChildRelationships(apk);
		//			apk.logParentChildRelationships();

		Config.getInstance().setAndroidManifest(apk.getAndroidManifest());

		if(Config.getInstance().isResourceHandling()){
			LayoutXMLParser layoutParser = new LayoutXMLParser();
			String dirPath = apkfilePath.substring(0, apkfilePath.length()-4);
			layoutParser.identifyLayoutComponents(dirPath);
			Hashtable layoutComponents = layoutParser.getLayoutComponents();
			apk = markLayoutCallbacks(apk,layoutComponents );
		}

		return apk;
	}


	public boolean checkIfManifestFileIsEmpty(File f){
		int lineCount = 0;

		// <application
		BufferedReader br;

		try{
			br = new BufferedReader(new FileReader(f));
			String st= "";
			while( (st= br.readLine()) != null){
				if(st.contains("<application")){
					lineCount = 1;
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return lineCount>0? true: false;
	}
	public APK markLayoutCallbacks( APK apkObj, Hashtable layoutComponents)
	{
		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;

			for(CFGComponent clsComp: pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;

				String type = clsObj.getType();

				if( type.equalsIgnoreCase(ComponentTypes.activity.toString()))
				{
					for(CFGComponent cfgComp: clsObj.getCompCollection())
					{
						CFG cfg = (CFG) cfgComp;
						for(CFGComponent bbComp: cfg.getCompCollection())
						{
							BasicBlock bb = (BasicBlock) bbComp;

							Instruction prevInstr = null;
							for(CFGComponent instrComp: bb.getCompCollection())
							{
								Instruction instr = (Instruction) instrComp;
								String text= instr.getText();

								//Though it is ad-hoc but it should work for now.
								if(text.contains(";->setContentView(I)"))
								{
									//Assumingly, previous instruction contains the ID.
									//		0x6 const v0, 2130903048 # [1.741290297071008e+38]
									// 		0xc invoke-virtual v2, v0, Lcmp/netsentry/ui/InterfaceStatsEditor;->setContentView(I)V
									//
									if(prevInstr != null){
										String prevInstrText = prevInstr.getText();

										String[] prevSplit1 = prevInstrText.split(" ");
										String id = prevSplit1[3]; //it contains the ID.

										if(prevInstrText.contains("const/high16"))
										{
											String idHighStr = Integer.toHexString(Integer.parseInt(id));
											idHighStr = idHighStr.concat("0000");
											Integer full32Int = Integer.parseInt(idHighStr, 16); //converts back to decimal
											id = String.valueOf(full32Int);
										}
										Layout layout = (Layout) layoutComponents.get(id);
										if(layout != null){
											clsObj.setLayoutMethods(layout.getMethods());
										}
									}
								}
								prevInstr = instr;
							}
						}
					}
				}
			}
		}
		return apkObj;
	}

	public APK setChildrenTypesBasedOnParents(APK apkObj){

		Hashtable<String, String> allComponentTypes = Config.getInstance().getAllComponentTypes();
		Hashtable dontAnalyzeAPIs = Config.getInstance().loadDontAnalyzeAPIs();
		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;

			for(CFGComponent clsComp: pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;
				String className = clsObj.getKey();
				String pkgClassName = pkg.getKey().concat("/").concat(className).concat(";"); 

				String parentCls = clsObj.getParentName();
				String[] split1 = parentCls.split("/");
				String parentClsType = split1[0]; 

				if(allComponentTypes.containsKey(parentCls))
				{
					String type = allComponentTypes.get(parentCls);
					clsObj.setType(type);
				}
			}
		}
		return apkObj;
	}
	public APK setParentChildRelationships(APK apkObj)
	{
		Hashtable dontAnalyzeAPIs = Config.getInstance().loadDontAnalyzeAPIs();
		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;

			for(CFGComponent clsComp: pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;
				String className = clsObj.getKey();
				String pkgClassName = pkg.getKey().concat("/").concat(className).concat(";"); 

				String parentCls = clsObj.getParentName();
				String[] split1 = parentCls.split("/");
				String parentClsType = split1[0]; 

				if(!dontAnalyzeAPIs.containsKey(parentClsType))
				{
					// find parent class.
					ClassObj parentClsObj = apkObj.findClassByKey(parentCls);

					if(parentClsObj != null)
					{
						clsObj.setParent(parentClsObj);
						if(parentClsObj.getChildren() == null)
							parentClsObj.setChildren(new ArrayList<ClassObj>());
						parentClsObj.getChildren().add(clsObj);
					}
				}
			}
		}
		return apkObj;

	}
	public APK setApplicationComponent(APK apkObj)
	{
		String pckgName = apkObj.getAndroidManifest().getPackageName();
		String[] pkgNameSplit = pckgName.split("[.]");
		String pckgFirstName = pkgNameSplit[0];

		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;
			String pkgKey = pkg.getKey();

			if(pkgKey.contains(pckgFirstName))
			{
				for(CFGComponent clsComp: pkg.getCompCollection())
				{
					ClassObj clsObj = (ClassObj) clsComp;

					String type;
					if( (type = getApplicationOrContentProviderClass(clsObj)) != null)
					{
						clsObj.setType(type);
					}
				}
			}
		}
		return apkObj;
	}
	public APK identifyComponentsUsingManifest( APK apkObj)
	{
		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;

			//			System.out.println("PkgNAMEEEEEEEEEE: " + pkg.getKey());

			for(CFGComponent clsComp: pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;
				String className = clsObj.getKey();
				String pkgClassName = pkg.getKey().concat("/").concat(className).concat(";"); 

				clsObj.setType("");  //Type wont be null anymore for other classes.
				if(compList.containsKey(pkgClassName))
				{
					String clsType = (String) compList.get(pkgClassName);
					clsObj.setType(clsType);
				}
			}
		}

		return apkObj;
	}
	public String getApplicationOrContentProviderClass(CFGComponent classObj)
	{
		ClassObj clsObj = (ClassObj) classObj;

		for(CFGComponent cfg: clsObj.getCompCollection())
		{
			if(cfg.getKey().equalsIgnoreCase("<init>"))
			{
				//get first BB and iterates through its instructions.
				for(CFGComponent instr: ((BasicBlock)cfg.getCompCollection().get(0)).getCompCollection())
				{
					Instruction ins = (Instruction) instr;
					String insText = ins.getText();

					if(insText.contains("Landroid/app/Application;-><init>"))
						return ComponentTypes.application.toString();
					else if(insText.contains("Landroid/content/ContentProvider;-><init>"))
						return ComponentTypes.contentProvider.toString();
				}
			}
		}
		return null;
	}
	public APK markComponentCallbacks( APK apkObj)
	{
		for(CFGComponent comp: apkObj.getCompCollection())
		{
			Package pkg = (Package) comp;

			for(CFGComponent clsComp: pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;

				String type = clsObj.getType();

				if( type.equalsIgnoreCase(ComponentTypes.activity.toString())
						|| type.equalsIgnoreCase(ComponentTypes.service.toString())
						|| type.equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString())
						|| type.equalsIgnoreCase(ComponentTypes.task.toString())
						|| type.equalsIgnoreCase(ComponentTypes.thread.toString())
						|| type.equalsIgnoreCase(ComponentTypes.application.toString())
						|| type.equalsIgnoreCase(ComponentTypes.contentProvider.toString())
						|| type.equalsIgnoreCase(ComponentTypes.contentObserver.toString())
						|| type.equalsIgnoreCase(ComponentTypes.adapter.toString())
						)
				{

					for(CFGComponent cfgComp: clsObj.getCompCollection())
					{
						CFG cfg = (CFG) cfgComp;
						String newType = getMethodType(cfg.getKey()); 
						cfg.setType(newType);

					}
				}
			}
		}
		return apkObj;
	}


	public AndroidManifest getParsedManifest(String inputXMLFileStr)
	{
		String pkgName="";


		AndroidManifest am = new AndroidManifest();
		ArrayList<Permission> permList = new ArrayList<Permission>();
		Application application = new Application();
		InputStream is = null;
		boolean componentEnabled = true;
		try {
			is = new FileInputStream(new File(inputXMLFileStr));
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			while(reader.hasNext())
			{

				if(reader.getEventType()== XMLStreamConstants.START_ELEMENT)
				{
					int myAttrCount = reader.getAttributeCount();

					ArrayList<String> attributeValueList = new ArrayList<String>();

					for(int i=0; i < myAttrCount; i++)
					{
						String attrName =reader.getAttributeLocalName(i); 
						if(attrName.equalsIgnoreCase("name"))
						{
							String attrValue = reader.getAttributeValue(i);
							attributeValueList.add(attrValue);
						}
					}
					String localName = reader.getLocalName();

					if(localName.equalsIgnoreCase("activity")
							|| localName.equalsIgnoreCase("activity-alias")
							|| localName.equalsIgnoreCase("service")
							|| localName.equalsIgnoreCase("receiver")
							) 
						//--inheritance might have helped to help three components.
					{
						//Run another loop to reach ending element for one of these components and extract the information in-between.
						//Activity and BroadcastReceiver can have intent-filter in between starting and ending element.


						String compType = localName; //reader.getLocalName();
						ComponentManifest compManifest = null;
						ArrayList<IntentFilter> iFilterList = new ArrayList<IntentFilter>();

						compManifest = new ComponentManifest();

						int attrCount = reader.getAttributeCount();

						// Get component Name
						for(int i=0; i < attrCount; i++)
						{
							String attrName =reader.getAttributeLocalName(i); 

							if(attrName.equalsIgnoreCase("name"))
							{
								String compFullName = "";
								String attrValue = reader.getAttributeValue(i); //*.*.*.MyActivity

								if(attrValue.startsWith(".") ) //  ".MainActivity". In this case, package name is appended at start
								{
									compFullName = pkgName.concat(attrValue); // com.mypackage.MainActivity
								}
								else if( !attrValue.contains("."))  // "MainActivity". In this case, package name is appended at start
								{
									compFullName = pkgName.concat(".").concat(attrValue); // com.mypackage.MainActivity
								}
								else
								{
									compFullName = attrValue; // Just keep it as it is.
								}

								// Now we need to make it look like Lcom/mypackage/MainActivity
								compFullName = "L".concat(compFullName);  // I should use StringBuilder here actually.
								compFullName = compFullName.replace(".", "/");
								//						         System.out.println("CompFullName= " + compFullName);
								compFullName = compFullName.concat(";");

								compManifest.setName(compFullName);

								//						         System.out.println("compType" + compType);

								if(compType.equalsIgnoreCase("receiver"))
								{
									compType = ComponentTypes.broadcastReceiver.toString();
								}
								//TODO: Use some other alternative way to include components; using global-variable is error-prone.
								compList.put(compFullName, compType); //Going to make redundant info but still useful.
							}
							else if(attrName.equalsIgnoreCase("enabled"))
							{
								String attrValue = reader.getAttributeValue(i); //"false or true"

								if(attrValue.equalsIgnoreCase("false") ) 
								{
									componentEnabled = false;
								}
							}
						}
						reader.next();
						IntentFilter iFilter = null;
						while(reader.hasNext())
						{
							//if there is an intent filter, we will catch here.

							if(reader.getEventType()== XMLStreamConstants.START_ELEMENT)
							{
								//									 System.out.println("read local name : " + reader.getLocalName());
								String subLocalName = reader.getLocalName();

								if(subLocalName.equalsIgnoreCase("intent-filter"))
								{
									iFilter = new IntentFilter();
								}
								else if(subLocalName.equalsIgnoreCase("action"))
								{
									int subAttrCount = reader.getAttributeCount();
									// Get component Name
									for(int i=0; i < subAttrCount; i++)
									{
										String attrName =reader.getAttributeLocalName(i); 

										if(attrName.equalsIgnoreCase("name"))
										{
											String actionName = reader.getAttributeValue(i); //android:name
											if(actionName != null && !actionName.isEmpty())
											{
												if(iFilter != null)
												{
													iFilter.getActionList().add(actionName);
												}
											}
										}
									}
								}
								else if(subLocalName.equalsIgnoreCase("data"))
								{
									int subAttrCount = reader.getAttributeCount();
									// Get component Name
									for(int i=0; i < subAttrCount; i++)
									{
										String attrName =reader.getAttributeLocalName(i); 

										if(attrName.equalsIgnoreCase("name"))
										{
											String dataName = reader.getAttributeValue(i); //android:name
											if(dataName != null && !dataName.isEmpty())
												iFilter.getDataList().add(dataName);
										}
									}
								}
								else if(subLocalName.equalsIgnoreCase("category"))
								{
									int subAttrCount = reader.getAttributeCount();
									// Get component Name
									for(int i=0; i < subAttrCount; i++)
									{
										String attrName =reader.getAttributeLocalName(i); 

										if(attrName.equalsIgnoreCase("name"))
										{
											String categoryName = reader.getAttributeValue(i); //android:name
											if(categoryName != null && !categoryName.isEmpty() && iFilter != null)
											{
												if(iFilter.getCategoriesList() == null)
													iFilter.setCategoriesList(new ArrayList<String>());
												iFilter.getCategoriesList().add(categoryName);
											}
										}
									}
								}
							}
							else if(reader.getEventType() == XMLStreamConstants.END_ELEMENT)
							{
								String subLocalName = reader.getLocalName();

								if(subLocalName.equalsIgnoreCase("intent-filter")) 
								{
									if(iFilter != null)
										iFilterList.add(iFilter);
								}
								else if(subLocalName.equalsIgnoreCase("activity")
										|| subLocalName.equalsIgnoreCase("activity-alias"))
								{ 
									compManifest.setType(ComponentTypes.activity.toString());
									compManifest.setIntentFilters(iFilterList);
									compManifest.setEnabled(componentEnabled);
									application.getComponentsManifest().add(compManifest);
									break; // breaks the inner while loop and goes to next component.
								}
								else if(subLocalName.equalsIgnoreCase("service")) 
								{
									compManifest.setType(ComponentTypes.service.toString());
									compManifest.setIntentFilters(iFilterList);
									compManifest.setEnabled(componentEnabled);
									application.getComponentsManifest().add(compManifest);
									break; // breaks the inner while loop and goes to next component.
								}
								else if(subLocalName.equalsIgnoreCase("receiver")) 
								{
									compManifest.setType(ComponentTypes.broadcastReceiver.toString());
									compManifest.setIntentFilters(iFilterList);
									compManifest.setEnabled(componentEnabled);
									application.getComponentsManifest().add(compManifest);
									break; // breaks the inner while loop and goes to next component.
								}

							}
							reader.next(); 
						}

					}
					else if(localName.equalsIgnoreCase("uses-permission")) // least probable checks at the end.
					{
						//				    	 String compType = reader.getLocalName();
						//				    	 String something = reader.get ElementText();
						//				    	 int attrCount = reader.getAttributeCount();
						String permissionName = "";

						//				    	 System.out.println(attrCount);
						for(String attrValue: attributeValueList)
						{				    		 
							permissionName = attrValue; // reader.getAttributeValue(i); // Obtained permission value.

							Permission perm = new Permission(permissionName);
							permList.add(perm);

							//						         System.out.println("PackageName " + pkgName);
						}
					}
					else if(reader.getLocalName().equalsIgnoreCase("manifest")) //It's only one time check. Should be at the end.
					{
						String compType = reader.getLocalName();
						int attrCount = reader.getAttributeCount();

						//				    	 System.out.println(attrCount);
						for(int i=0; i < attrCount; i++)
						{
							String attrName =reader.getAttributeLocalName(i); 

							if(attrName.equalsIgnoreCase("package"))
							{
								pkgName = reader.getAttributeValue(i); 
								am.setPackageName(pkgName);

								// System.out.println("PackageName " + pkgName);


							}
						}

					}

				}
				reader.next();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		am.setUsedPermList(permList);
		am.setApplication(application);

		return am;
	}

	public String getMethodType(String methodName)
	{
		String type = "";
		if (activityCallbacks.containsKey(methodName)
				|| serviceCallbacks.containsKey(methodName)
				|| broadcastReceiverCallbacks.containsKey(methodName)
				|| asynchTaskCallbacks.containsKey(methodName)
				|| threadCallbacks.containsKey(methodName)
				|| viewGroupCallbackSet.contains(methodName)
				)
		{
			type = MethodTypes.componentCallback.toString();
		}
		else if (methodName.equalsIgnoreCase("<init>") 
				|| methodName.equalsIgnoreCase("<clinit>") 
				|| methodName.equalsIgnoreCase("init") 
				)
		{
			type = MethodTypes.init.toString();
		}


		return type;
	}

	public void addInlinedClasses(APK apkParam)
	{
		// Some of activities have inlined functions. 
		// Lme/bosegoogle/app/settings/CustomerDataActivity$3; onClick
		// So to handle such functions, I am going to first find activity and then find if any of other class name contains '$' with activity name at the start.
		// Typically such functions are called. They are not called even with ->start() method sometimes.

		ArrayList<String> activityList = new ArrayList<String>();
		for(CFGComponent pkgComp : apkParam.getCompCollection())
		{
			Package pkg = (Package) pkgComp;

			String pkgKey = pkg.getKey();
			//			System.out.println("PackageName: " + pkgKey);
			for(CFGComponent clsComp : pkg.getCompCollection())
			{
				ClassObj clsObj = (ClassObj) clsComp;
				String clsKey = clsObj.getKey();
				String clsType = clsObj.getType();

				if(clsType.isEmpty())
				{
					if(clsKey.contains("$"))
					{
						String[] strSplit = clsKey.split("[$]");  // activity$Class2

						//					System.out.println(clsKey);

						if(strSplit != null && strSplit.length > 0)
						{
							String origClassName = strSplit[0];   // activity
							String origPkgClassNameWOTermination = pkgKey.concat("/").concat(origClassName); 
							String origPkgClassName = origPkgClassNameWOTermination.concat(";"); //Lcom/mypackage/activity as given in compList

							String dollarPkgClassName = pkgKey.concat("/").concat(clsKey); //Lcom/mypackage/activity$Class2 

							ClassObj parentCls = apkParam.findClassByKey(origPkgClassName);

							if(parentCls != null)
							{
								CFG cfg = apkParam.findMethodByKey(dollarPkgClassName.concat(";"), "onClick");
								if(cfg != null)
								{
									//									System.out.println(dollarPkgClassName);
									parentCls.getCompCollection().add(cfg);
								}
							}

							if(compList.containsKey(origPkgClassName)
									|| compList.containsKey(origClassName))
							{
								String compType = (String) compList.get(origPkgClassName);

								//Set the same type as of its parent component.
								clsObj.setType(compType);
							}
						}
					}
				}
			}
		}
		//		return compList;
	}

	public void addDynamicComponents(APK apkParam)
	{
		AndroidManifest am = apkParam.getAndroidManifest();
		Hashtable<String, String> blackListAPIs = Config.getInstance().getBlackListedAPIs();
		for(CFGComponent pkgComp : apkParam.getCompCollection())
		{
			Package pkg = (Package) pkgComp;

			String pkgKey = pkg.getKey();
			for(int i=0; i < pkg.getCompCollection().size(); i++)
			{
				CFGComponent clsComp = pkg.getCompCollection().get(i);

				ClassObj clsObj = (ClassObj) clsComp;
				String clsKey = clsObj.getKey();
				String pkgClsName = clsObj.getCurrPkgClassName();

				if(clsObj.getType().isEmpty())
				{
					for(CFGComponent cfgComp : clsComp.getCompCollection())
					{
						CFG cfg = (CFG) cfgComp;
						String cfgKey = cfg.getKey();

						if(cfgKey.equalsIgnoreCase("run"))
						{
							clsObj.setType(ComponentTypes.thread.toString());

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.thread.toString());

							am.getApplication().getComponentsManifest().add(comp);

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");
							compList.put(pkgClsName, ComponentTypes.thread.toString());

							break;
							//							pkg.getCompCollection().set(i, clsObj);
						}
						else if(cfgKey.equalsIgnoreCase("onChange"))
						{
							clsObj.setType(ComponentTypes.contentObserver.toString());

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.contentObserver.toString());

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");
							compList.put(pkgClsName, ComponentTypes.contentObserver.toString());

							break;
						}
						//						//Each activity should have onCreate method.
						else if(cfgKey.equalsIgnoreCase("onCreate")) 
						{
							MethodSignature ms = cfg.getSignature();

							ArrayList<Parameter> params = ms.getParams();

							//  onCreate (Landroid/os/Bundle;)V 3
							if(params.size() > 0)
							{
								Parameter firstParam = params.get(0);
								String type = firstParam.getType();

								if(type != null && !type.isEmpty())
								{
									if(type.equalsIgnoreCase("Landroid/os/Bundle;"))
									{
										// Now mark this class as a receiver and handle it accordingly.

										String compType = ComponentTypes.activity.toString();

										clsObj.setType(compType);

										ComponentManifest comp = new ComponentManifest();
										comp.setName(pkgClsName);
										comp.setType(compType);

										//										System.out.println("DynAddedComp: Actvitiy = " + pkgClsName);

										am.getApplication().getComponentsManifest().add(comp);

										if(!pkgClsName.endsWith(";"))
											pkgClsName = pkgClsName.concat(";");
										compList.put(pkgClsName, compType);

										break;
									}
								}
							}
						}
						else if (cfgKey.equalsIgnoreCase("onRestoreInstanceState")
								|| cfgKey.equalsIgnoreCase("onSaveInstanceState")
								|| cfgKey.equalsIgnoreCase("onRestart")
								|| cfgKey.equalsIgnoreCase("onResume")
								|| cfgKey.equalsIgnoreCase("onPostResume")
								|| cfgKey.equalsIgnoreCase("onUserLeaveHint")
								|| cfgKey.equalsIgnoreCase("onStop"))
						{
							clsObj.setType(ComponentTypes.activity.toString());

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.activity.toString());

							//							System.out.println("DynAddedComp: Actvitiy = " + pkgClsName);

							am.getApplication().getComponentsManifest().add(comp);

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");
							compList.put(pkgClsName, ComponentTypes.activity.toString());

							break;
						}
						else if(cfgKey.equalsIgnoreCase("onStartCommand")
								||cfgKey.equalsIgnoreCase("onBind")
								||cfgKey.equalsIgnoreCase("onUnbind")
								||cfgKey.equalsIgnoreCase("onRebind")
								)
						{
							clsObj.setType(ComponentTypes.service.toString());

							//							System.out.println("DynAddedComp: Service = " + pkgClsName);

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.service.toString());

							am.getApplication().getComponentsManifest().add(comp);

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");
							compList.put(pkgClsName, ComponentTypes.service.toString());

							break;

						}
						else if(cfgKey.equalsIgnoreCase("onPreExecute")
								||cfgKey.equalsIgnoreCase("doInBackground")
								||cfgKey.equalsIgnoreCase("onProgressUpdate")
								||cfgKey.equalsIgnoreCase("onPostExecute")
								)
						{
							clsObj.setType(ComponentTypes.task.toString());

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.task.toString());

							//							System.out.println("DynAddedComp: Task = " + pkgClsName);

							am.getApplication().getComponentsManifest().add(comp);

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");

							compList.put(pkgClsName, ComponentTypes.task.toString());

							break;

						}
						else if(cfgKey.equalsIgnoreCase("handle")
								)
						{
							clsObj.setType(ComponentTypes.adapter.toString());

							ComponentManifest comp = new ComponentManifest();
							comp.setName(pkgClsName);
							comp.setType(ComponentTypes.adapter.toString());

							//							System.out.println("DynAddedComp: Task = " + pkgClsName);

							am.getApplication().getComponentsManifest().add(comp);

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");

							compList.put(pkgClsName, ComponentTypes.adapter.toString());

							break;

						}
						else if(viewGroupCallbackSet.contains(cfgKey)
								&& (!clsObj.isClassBlacklisted())
								)
						{
							clsObj.setType(ComponentTypes.viewGroup.toString());

							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");

							compList.put(pkgClsName, ComponentTypes.viewGroup.toString());

							break;

						}
						else if(webViewCallbackSet.contains(cfgKey)
								&& (!clsObj.isClassBlacklisted())
								)
						{
							clsObj.setType(ComponentTypes.webView.toString());
							if(!pkgClsName.endsWith(";"))
								pkgClsName = pkgClsName.concat(";");

							compList.put(pkgClsName, ComponentTypes.webView.toString());

							break;

						}
						else if(cfgKey.equalsIgnoreCase("onReceive"))
						{
							MethodSignature ms = cfg.getSignature();

							ArrayList<Parameter> params = ms.getParams();

							// onReceive (Landroid/content/Context; Landroid/content/Intent;)
							if(params.size() > 0)
							{
								Parameter firstParam = params.get(0);
								String type = firstParam.getType();

								if(type != null && !type.isEmpty())
								{
									if(type.equalsIgnoreCase("Landroid/content/Context;"))
									{
										// Now we check the second parameter.

										Parameter secondParam = params.get(1);
										String secondParamType = secondParam.getType();

										if(secondParamType != null && secondParamType.equalsIgnoreCase("Landroid/content/Intent;"))
										{
											// Now mark this class as a receiver and handle it accordingly.
											clsObj.setType(ComponentTypes.broadcastReceiver.toString());

											ComponentManifest comp = new ComponentManifest();
											comp.setName(pkgClsName);
											comp.setType(ComponentTypes.broadcastReceiver.toString());

											//											System.out.println("DynAddedComp: Receiver = " + pkgClsName);

											am.getApplication().getComponentsManifest().add(comp);

											if(!pkgClsName.endsWith(";"))
												pkgClsName = pkgClsName.concat(";");
											compList.put(pkgClsName, ComponentTypes.broadcastReceiver.toString());
											break;

										}
									}
								}
							}

						}
					}
				}
			}
		}
		apkParam.setAndroidManifest(am);
	}
}
