package com.tf;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import java.awt.FileDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.widgets.Label;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder;  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  

public class MainWindow {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	
	String sourceFile;
	String dstFile;
	
	Label srcFileLabel;
	Label dstFileLabel;
	Label programStatus;
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 224);
		shell.setText("SWT Application");
		
		Button btnChooseFile = new Button(shell, SWT.NONE);
		btnChooseFile.setBounds(10, 10, 161, 25);
		btnChooseFile.setText("Choose source File");
		srcFileLabel = new Label(shell, SWT.NONE);
		
		Button btnChooseFileTo = new Button(shell, SWT.NONE);
		btnChooseFileTo.setBounds(187, 10, 151, 25);
		btnChooseFileTo.setText("Choose file to fix");
		
		dstFileLabel = new Label(shell, SWT.NONE);
		dstFileLabel.setText("Nothing picked");
		dstFileLabel.setBounds(102, 62, 322, 15);
		
		btnChooseFile.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
				FileDialog dlg = new java.awt.FileDialog((java.awt.Frame) null);

				dlg.setMultipleMode(false);
				dlg.setVisible(true);
				
				File[] f = dlg.getFiles();
						
				if (f.length > 0)
				{
					sourceFile= f[0].toString();
					srcFileLabel.setText( f[0].getName());
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnChooseFileTo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

				
				
				FileDialog dlg = new java.awt.FileDialog((java.awt.Frame) null);

				dlg.setMultipleMode(false);
				dlg.setVisible(true);
				
				File[] f = dlg.getFiles();
				
				if (f.length > 0)
				{
					dstFile= f[0].toString();
					dstFileLabel.setText( f[0].getName());
				}
				
				
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		
		Label lblStars = new Label(shell, SWT.NONE);
		lblStars.setBounds(10, 41, 86, 15);
		lblStars.setText("Source File:");
		
		Label lblCategory = new Label(shell, SWT.NONE);
		lblCategory.setBounds(10, 62, 86, 15);
		lblCategory.setText("File to fix:");
		
		
		srcFileLabel.setBounds(102, 41, 322, 15);
		srcFileLabel.setText("Nothing picked");

		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(10, 151, 414, 25);
		btnNewButton.setText("Start");
		
		btnNewButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

				
				programStatus.setText("Started...");
				ProcessingBlock(sourceFile, dstFile);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setBounds(10, 83, 55, 15);
		lblStatus.setText("Status:");
		
		programStatus = new Label(shell, SWT.NONE);
		programStatus.setBounds(10, 104, 414, 41);
		programStatus.setText("Nothing started yet");

	}
	
	
	void ProcessingBlock(String src, String dest)
	{
		
		if (src.equals(dest))
		{
			programStatus.setText("Cannot process the same file. Source and dest is the same");
			return;
		}
		List<IndexRecord> records =  new ArrayList<>();;
	     
        try   
        {  

            File file = new File(src);  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse(file);  
            doc.getDocumentElement().normalize();  
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());  
            
            NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();  
            //System.out.println("Rootest element: " + nodeList.getLength());

            for (int itr = 0; itr < nodeList.getLength(); itr++)   
            {  
                Node node = nodeList.item(itr);  
                
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    Element eElement = (Element) node;  
                    
                    if (eElement.getAttributes().item(0).getNodeValue() != null)
                    {
                        IndexRecord record = new IndexRecord();
                        record.elementName = eElement.getAttributes().item(0).getNodeValue();
                        record.index = itr;
                        records.add(record);
                    
                    }
                }  
            }  
            
            
            CheckForUnclosedStrings(dest);

            File file2 = new File(dest);  

            DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();  

            DocumentBuilder db2 = dbf2.newDocumentBuilder();  
            Document doc2 = db2.parse(file2);  
            doc2.getDocumentElement().normalize();  
            
            NodeList nodeList2 = doc2.getChildNodes().item(0).getChildNodes();  
            
            
            for (int itr = 0; itr < nodeList2.getLength(); itr++)   
            {  
                Node node = nodeList2.item(itr);  

                
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    
                    int indexToModify = findInList(records, itr);
                        
                        
                    Element eElement = (Element) node;  
                    if (indexToModify > -1 && eElement.getAttributes().item(0).getNodeValue() != null)
                    {
                        eElement.getAttributes().item(0).setNodeValue(records.get(indexToModify).elementName);
                    }

                }
                //else 
                //{
                //   System.out.println("Not Adding "+ node.getNodeType());
                //   
                //}
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
 
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc2);
 
            StreamResult streamResult = new StreamResult(new File(dest));
            transformer.transform(domSource, streamResult);
            programStatus.setText("Finished...");
        }   
        catch (Exception e)   
        {  
            System.out.println("xception " + e);
            e.printStackTrace();  
        }  	
		
	}
	
	
	void CheckForUnclosedStrings(String fileName) throws FileNotFoundException, XMLStreamException
    {
        InputStream is = new FileInputStream(fileName);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(is, "utf-8");
        Stack<StartElement> stack = new Stack<StartElement>();
        while (eventReader.hasNext()) {
            try 
            {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) 
                {
                    StartElement startElement = event.asStartElement();
                    //System.out.println("processing element: " + startElement.getName().getLocalPart());
                    stack.push(startElement);
                }
                if(event.isEndElement())
                {
                    stack.pop();
                }
            }
            catch(XMLStreamException e){

            	programStatus.setText("Problem in Line:" + e.getLocation().getLineNumber());
                System.out.println("error in line: " +e.getLocation().getLineNumber());
                StartElement se = stack.pop();
                System.out.println("non-closed tag:" + se.getName().getLocalPart() + " " + se.getLocation().getLineNumber());

                throw e;
            }
        }
    }
    
    static int findInList(List<IndexRecord> records, int toFind)
    {
        int foundAt = -1;
        for (int i = 0; i< records.size(); i++)
        {

            if (records.get(i).index == toFind)
            {
                
                foundAt = i;
                break;
            }
            
        }
        return foundAt;
    }

    
    static class IndexRecord
    {
        public String elementName;
        public int index;
    }
	
	
}
