package com.ibm.migration.util;

import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.util.ConfigurationParameters;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

public class FileNetConnection {
	public final String FILENET_URL = "http://yfilenet:9080/wsi/FNCEWS40MTOM/";
	public final String FILENET_USERNAME= "p8admindev";
	public final String FILENET_PASSWORD = "123456";
	public final String FILENET_DOMAIN_NAME = "IBM";
	public final String FILENET_OBJECTSTORE_NAME = "OS1";
	
	public static final String ID_FOLDER1 = "{60822A86-0000-C61D-A51D-CB76FA004AA8}";
	public static final String ID_FOLDER2 = "{80822A86-0000-C219-9BCF-A078C56B722E}";
	public static final String ID_MAINF = "{B0862A86-0000-C51B-82B9-DDE96D692CFB}";
	public static final String ID_DOCUMENT = "{E0862A86-0000-C71B-91AF-09FAD8E78D67}";
	
	
	public Connection fCon;
	public UserContext uc;
	public Domain domain;
	public ObjectStore objectStore;
	
	public FileNetConnection() {
		uc = new UserContext();
	}
	
	public static void main(String[] args) {
		System.out.println("Starting the program");
		FileNetConnection fc = new FileNetConnection();
		fc.connect();
	
		fc.addDocumentToFolder(FileNetConnection.ID_DOCUMENT, FileNetConnection.ID_FOLDER1);
		//fc.removeDocumentFromFolder(FileNetConnection.ID_DOCUMENT, FileNetConnection.ID_FOLDER1);
		
		fc.disconnect();
	}

	private void addDocumentToFolder(String documentId, String folderId) {
		Document document = Factory.Document.fetchInstance(objectStore, Id.asIdOrNull(documentId), null); // filenet instance of document
		System.out.println("Obtained document is: " + document.get_Name());
		
		Folder fo = Factory.Folder.fetchInstance(objectStore,Id.asIdOrNull(folderId),null);
    	ReferentialContainmentRelationship rcr;
    	rcr = fo.file((Document) document, AutoUniqueName.AUTO_UNIQUE, ((Document) document).get_Name(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
    	
    	String documentName = document.get_Name(); // stored filenet document name
    	String query = "update docs setdocname = "+ documentName + " where ecmid="+documentId;
    	//System.out.println(document.get_Name());
    	rcr.save(RefreshMode.REFRESH);
	}
	
	private void removeDocumentFromFolder(String documentId, String folderId) {
		Document document = Factory.Document.fetchInstance(objectStore, Id.asIdOrNull(documentId), null);
		System.out.println("Obtained document is: " + document.get_Name());
		
		Folder fo = Factory.Folder.fetchInstance(objectStore,Id.asIdOrNull(folderId),null);
	
		ReferentialContainmentRelationship rcr = fo.unfile((Document)document);
		rcr.save(RefreshMode.REFRESH);		
	}
	
	public void connect() {
		fCon = Factory.Connection.getConnection(FILENET_URL);
		Subject subject = UserContext.createSubject(fCon, FILENET_USERNAME, FILENET_PASSWORD, null);
		uc.get().pushSubject(subject);
		System.out.println("Connect completed");
		
		domain = Factory.Domain.fetchInstance(fCon, null, null);
		System.out.println("Connected domain name is: " + domain.get_Name());
		
		objectStore = Factory.ObjectStore.fetchInstance(domain, FILENET_OBJECTSTORE_NAME, null);
		System.out.println("Connected Object Store name is: " + objectStore.get_Name());
		
		//fCon = null;
	}
	
	public void disconnect()
	{
		fCon = null;
		System.out.println("Disconnected");
	}
}
