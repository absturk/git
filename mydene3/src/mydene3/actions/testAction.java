package mydene3.actions;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.lang.model.type.ReferenceType;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.jscape.inet.sftp.Sftp;
import com.jscape.inet.ssh.SshConnectedEvent;
import com.jscape.inet.ssh.SshDataReceivedEvent;
import com.jscape.inet.ssh.SshDisconnectedEvent;
import com.jscape.inet.ssh.SshListener;
import com.jscape.inet.ssh.SshSession;
import com.jscape.inet.ssh.util.SshParameters;
import com.sun.jdi.VirtualMachine;

/**
 * This action shows the alphabetically sorted pull-down menu of all registered views. Selecting the menu item shows and activates the view. The menu item for the currently active view is shown
 * checked and disabled.
 * 
 * @author Sandip V. Chitale
 */
@SuppressWarnings("restriction")
public class testAction implements IWorkbenchWindowPulldownDelegate
	{
		private Menu menu;
		IJavaProject javaProject;
		String javaName="";
		IFile file;
		String BUILD_package="";
		String BUILD_javaname="";
		String BUILD_path="";
		boolean BUILD_status=false;
		IWorkbenchWindow window=null;
		IWorkbenchPage iworkbenchpage=null;
		IWorkbench iworkbench=null;
		IEditorPart ieditorpart=null;
		String MENU_firma="";

		public Menu getMenu(Control parent)
			{
				return createViewsMenu(parent,menu);
			}

		private Menu createViewsMenu(Control parent,Menu menu)
			{
				iworkbench=PlatformUI.getWorkbench();
				window=iworkbench.getActiveWorkbenchWindow();
				iworkbenchpage=window.getActivePage();
				ieditorpart=iworkbenchpage.getActiveEditor();
				//
				if(menu==null)
					{
						try
							{
								BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("osgi.syspath").replace((char)92,'/')+"/AYAR_2.csv"),"ISO-8859-9"));
								String d[]=in.readLine().split(";");
								MENU_firma=d[0]+" / "+d[1];
								in.close();
							}
						catch(Exception e1)
							{
								MESSAGE_BOX("HATA-1:",e1,"");
							}
						menu=new Menu(parent);
						menu.setData("id00");
						//
						MenuItem menuItem1=new MenuItem(menu,SWT.CASCADE);
						menuItem1.setText("Run & Sftp");
						menuItem1.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Forward.gif").createImage());
						menuItem1.setData("id_1");
						menuItem1.setEnabled(true);
						//
						MenuItem menuItem2=new MenuItem(menu,SWT.CASCADE);
						menuItem2.setText("Ayarlar");
						menuItem2.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Wizard.gif").createImage());
						menuItem2.setData("id_2");
						menuItem2.setEnabled(true);
						//
						MenuItem menuItem3=new MenuItem(menu,SWT.CASCADE);
						menuItem3.setText("Ayar CSV Düzenle");
						menuItem3.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Table.gif").createImage());
						menuItem3.setData("id_3");
						menuItem3.setEnabled(true);
						//
						MenuItem menuItem4=new MenuItem(menu,SWT.CASCADE);
						menuItem4.setText("Derle");
						menuItem4.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Disaster.gif").createImage());
						menuItem4.setData("id_4");
						menuItem4.setEnabled(true);
						//
						MenuItem menuItem5=new MenuItem(menu,SWT.CASCADE);
						menuItem5.setText("Metin Arama Yeni");
						menuItem5.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Search.gif").createImage());
						menuItem5.setData("id_5");
						menuItem5.setEnabled(true);
						//
						MenuItem menuItem6=new MenuItem(menu,SWT.CASCADE);
						menuItem6.setText("Özel Karakterler");
						menuItem6.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Redstar.gif").createImage());
						menuItem6.setData("id_6");
						menuItem6.setEnabled(true);
						//
						MenuItem menuItem7=new MenuItem(menu,SWT.CASCADE);
						menuItem7.setText("Compiler Source Version");
						menuItem7.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Repair.gif").createImage());
						menuItem7.setData("id_7");
						menuItem7.setEnabled(true);
						//
						Menu menu2=new Menu(menu);
						menuItem6.setMenu(menu2);
						MenuItem menuItem6_1=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_1.setText("|");
						menuItem6_1.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_1.setData("id_1_1");
						menuItem6_1.setEnabled(true);
						//
						MenuItem menuItem6_2=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_2.setText("<");
						menuItem6_2.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_2.setData("id_1_2");
						menuItem6_2.setEnabled(true);
						//
						MenuItem menuItem6_3=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_3.setText(">");
						menuItem6_3.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_3.setData("id_1_3");
						menuItem6_3.setEnabled(true);
						//
						MenuItem menuItem6_4=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_4.setText("`");
						menuItem6_4.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_4.setData("id_1_4");
						menuItem6_4.setEnabled(true);
						//
						MenuItem menuItem6_5=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_5.setText("'");
						menuItem6_5.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_5.setData("id_1_5");
						menuItem6_5.setEnabled(true);
						//
						MenuItem menuItem6_6=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_6.setText("^");
						menuItem6_6.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_6.setData("id_1_6");
						menuItem6_6.setEnabled(true);
						//
						MenuItem menuItem6_7=new MenuItem(menu2,SWT.POP_UP);
						menuItem6_7.setText("~");
						menuItem6_7.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Add2.gif").createImage());
						menuItem6_7.setData("id_1_7");
						menuItem6_7.setEnabled(true);
						//
						menuItem1.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										try
											{
												Job job=new Job("Run-SFTP")
													{
														protected IStatus run(IProgressMonitor monitor)
															{
																RUN_SFTP();
																return Status.OK_STATUS;
															}
													};
												job.setUser(true);
												job.schedule();
											}
										catch(Exception rt)
											{
												MESSAGE_BOX("HATA-2:",rt,"");
											}
									}
							});
						menuItem2.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										AYAR_FRAMES f=new AYAR_FRAMES();
										f.pack();
										f.setTitle("my-Run&Sftp Ayarlar");
										f.setSize(new Dimension(500,150));
										f.setBounds((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-(f.getBounds().width/2),(int)java.awt.MouseInfo.getPointerInfo().getLocation().getY(),500,150);
										f.setVisible(true);
									}
							});
						menuItem3.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										String filedir="";
										try
											{
												filedir=System.getProperty("osgi.syspath").replace((char)92,'/');
												filedir=filedir+"/AYAR_1.csv";
												filedir=filedir.replace(""+'/',"\\");
												String dizi[];
												dizi=new String[]
													{"rundll32","url.dll","FileProtocolHandler","\""+filedir+"\""
												};
												Process p=Runtime.getRuntime().exec(dizi);
												p.waitFor();
											}
										catch(Exception rt)
											{
												MESSAGE_BOX("HATA-3:",rt,"");
											}
									}
							});
						menuItem4.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										try
											{
												Job job=new Job("DERLE")
													{
														protected IStatus run(IProgressMonitor monitor)
															{
																DERLE();
																return Status.OK_STATUS;
															}
													};
												job.setUser(true);
												job.schedule();
											}
										catch(Exception rt)
											{
												MESSAGE_BOX("HATA-4:",rt,"");
											}
									}
							});
						menuItem5.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										try
											{
												String ara=System.getProperty("osgi.syspath").replace((char)92,'/');
												ara=(new StringBuilder()).append(ara.substring(0,ara.lastIndexOf("/bin"))).append("/lib/ext").toString();
												String ara2=System.getProperty("osgi.instance.area").replace((char)92,'/').replace("/classes","/src").replace(" ","^");
												Runtime.getRuntime().exec(ara+"/myara.exe "+ara2);
											}
										catch(Exception rt)
											{
												MESSAGE_BOX("HATA-5:",rt,"");
											}
									}
							});
						menuItem6_1.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("|");
									}
							});
						menuItem6_2.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("<");
									}
							});
						menuItem6_3.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText(">");
									}
							});
						menuItem6_4.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("`");
									}
							});
						menuItem6_5.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("'");
									}
							});
						menuItem6_6.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("^");
									}
							});
						menuItem6_7.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										insertEditorText("~");
									}
							});
						menuItem7.addSelectionListener(new SelectionAdapter()
							{
								public void widgetSelected(SelectionEvent e)
									{
										String filedir="";
										try
											{
												filedir=System.getProperty("osgi.syspath").replace((char)92,'/');
												filedir=filedir+"/CompilerSourceVersion.txt";
												filedir=filedir.replace(""+'/',"\\");
												String dizi[];
												dizi=new String[]
													{"rundll32","url.dll","FileProtocolHandler","\""+filedir+"\""
												};
												Process p=Runtime.getRuntime().exec(dizi);
												p.waitFor();
											}
										catch(Exception rt)
											{
												MESSAGE_BOX("HATA-6:",rt,"");
											}
									}
							});
					}
				else
					{
						// Delete children
					}
				return menu;
			}

		@SuppressWarnings(
			{"unused","unchecked","rawtypes"
		})
	public void RUN_SFTP()
			{
				String B_package="";
				String B_javaname="";
				String B_path="";
				boolean B_status=false;
				String mypackage="";
				String dizi7mypackage="";
				boolean isansysend=false;
				boolean isanyerror=false;
				Vector xclass=new Vector();
				String xmandants[]=null;
				String clsfile="";
				int i=0,j=0;
				//
				DERLE();
				B_package=BUILD_package;
				B_javaname=BUILD_javaname;
				B_path=BUILD_path;
				B_status=BUILD_status;
				findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("-KOD DERLENDÝ , GÖNDERÝM ÝÞLEMÝ BAÞLADI-");
				if(B_status)
					{
						String dizi[]=null;
						try
							{
								BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("osgi.syspath").replace((char)92,'/')+"/AYAR_2.csv"),"ISO-8859-9"));
								dizi=in.readLine().split(";");
								in.close();
								findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println(B_javaname+".class dosyasý \""+dizi[0]+"\" firmasý için "+dizi[3]+" serverýnda "+dizi[2]+" mandantlarýna gönderilecek...");
								xmandants=dizi[2].replace("&",";").split(";");
								String s1=B_path+"/"+B_package.replace(".","/");
								String s2=B_javaname+"$";
								String yols[]=new File(s1).list();
								xclass.add(""+s1+"/"+B_javaname+".class");
								for(i=0;i<yols.length;i++)
									{
										if(yols[i].startsWith(s2))
											{
												// findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).getInputStream().println("....."+s1+"/"+yols[i]);
												xclass.add(""+s1+"/"+yols[i]);
											}
									}
								//
								findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("?FTP Baðlantýsý Yapýlýyor...");
								if(CONNECTION_CHECK_NEW_CREATE(dizi[0],dizi[3],Integer.parseInt(dizi[8]),dizi[5],dizi[6],dizi[9].equals("0")?false:true,B_package,B_javaname))
									{
										findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("+FTP Baðlantýsý Yapýldý...");
										if(dizi[9].equals("1"))
											{
												findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("?Session Baðlantýsý Yapýlýyor...");
												findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("+Session Baðlantýsý Yapýldý...");
											}
										for(i=0;i<xmandants.length;i++)
											{
												try
													{
														mypackage=mypackage.equals("my/classes")?"win/"+B_package:B_package;
														mypackage=mypackage.replace(".","/");
														dizi7mypackage=mypackage.equals("win/my/classes")?"/"+mypackage:dizi[7]+mypackage;
														c.CONN_ftp.setDir(dizi[4]+"/"+xmandants[i]+dizi7mypackage+"/");
														for(j=0;j<xclass.size();j++)
															{
																clsfile=""+xclass.elementAt(j);
																c.CONN_ftp.upload(new File(clsfile));
															}
														findConsole(iworkbenchpage,"[+]"+B_package+"."+B_javaname,false).println("\u2665+++++ ("+xclass.get(0)+") > "+dizi[4]+"/"+xmandants[i]+dizi7mypackage+" dizinine gönderildi...");
														isansysend=true;
														// #######################################################################
														if(dizi[9].equals("1"))
															{
																c.CONN_session.sendWait("cd "+dizi[4]+"/"+xmandants[i],"$");
																c.CONN_session.sendWait(dizi[10].equals("/")?"jfopserver_cmds.sh -r -a DEFAULT":"jfopserver_cmds.sh -r -a DEFAULT -c "+dizi[10],"$");
																findConsole(iworkbenchpage,"[+]"+B_package+"."+B_javaname,false).println("+"+xmandants[i]+" mandantýnda Jar dosyasý baþarýyla <Deploy> edildi...");
															}
														// #######################################################################
													}
												catch(Exception rt)
													{
														listCONNECTION.remove(c);
														isanyerror=true;
														if(clsfile.equals(""))
															{
																findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2---BAÐLANTI YOK--- ("+dizi[3]+":/"+dizi[4]+"/"+xmandants[i]+dizi7mypackage+")");
															}
														else
															{
																findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2("+clsfile+") > "+dizi[4]+"/"+xmandants[i]+dizi7mypackage+" dizinine gönderilemedi...(Ýþlem Baþarýsýz)");
															}
														findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2"+rt.getMessage());
														findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u255A\u2550\u2550\u2550\u2550\u2550\u21D2");
														findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println(MESSAGE_TEXT(rt));
													}
											}
									}
								else
									{
										findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2---BAÐLANTI YOK--- ("+dizi[3]+":/"+dizi[4]+"/"+xmandants[i]+dizi7mypackage+")");
									}
							}
						catch(Exception rt)
							{
								try
									{
										if(clsfile.equals(""))
											{
												findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2---BAÐLANTI YOK--- ("+dizi[3]+":/"+dizi[4]+"/"+xmandants[i]+dizi7mypackage+")");
											}
										else
											{
												findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2("+clsfile+") > "+dizi[4]+"/"+xmandants[i]+dizi7mypackage+" dizinine gönderilemedi...(Ýþlem Baþarýsýz)");
											}
									}
								catch(Exception r2)
									{
										MESSAGE_BOX("HATA-7:",r2,"");
									}
								findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u2560\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u21D2"+rt.getMessage());
								findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\u255A\u2550\u2550\u2550\u2550\u2550\u21D2");
							}
					}
				else
					{
						findConsole(iworkbenchpage,"[-]"+B_package+"."+B_javaname,false).println("-KOD DERLENEMEDÝ-");
					}
			}

		@SuppressWarnings("rawtypes")
		private ArrayList listCONNECTION=new ArrayList();
		private CONNECTION c;

		public boolean CONNECTION_CHECK_NEW_CREATE(String customer,String ip,int port,String user,String pass,boolean isSession,String B_package,String B_javaname)
			{

				boolean isAdded=false;
				for(int i=0;i<listCONNECTION.size();i++)
					{
						c=(CONNECTION)listCONNECTION.get(i);
						if(c.CONN_customer.equals(customer))
							{
								isAdded=true;
								if(!c.CONN_ip.equals(ip)||c.CONN_port!=port||!c.CONN_user.equals(user)||!c.CONN_pass.equals(pass)||c.CONN_isSession!=isSession)
									{
										isAdded=false;
									}
								break;
							}
					}
				if(!isAdded)
					{
						c=new CONNECTION(customer);
						c.CCNC(ip,port,user,pass,isSession,B_package,B_javaname);
					}
				return true;
			}

		class CONNECTION
			{
				public CONNECTION(String customer)
					{
						CONN_customer=customer;
					}

				public String CONN_customer="";
				public String CONN_ip="";
				public int CONN_port=0;
				public String CONN_user="";
				public String CONN_pass="";
				public boolean CONN_isSession=false;
				public boolean CONN_SessionisConnected=false;
				public SshParameters CONN_sshParams;
				public Sftp CONN_ftp;
				public SshSession CONN_session;
				public String CONN_shellPrompt="/";

				public boolean CC(String customer)
					{
						try
							{
								if(CONN_session!=null)
									{
										CONN_session.sendNoWait("exit");
										CONN_SessionisConnected=false;
										CONN_session.disconnect();
										CONN_session=null;
									}
								if(CONN_ftp!=null)
									{
										CONN_ftp.disconnect();
										CONN_ftp=null;
									}
								for(int i=0;i<listCONNECTION.size();i++)
									{
										if(((CONNECTION)listCONNECTION.get(i)).CONN_customer.equals(customer))
											{
												listCONNECTION.remove(i);
												break;
											}
									}
							}
						catch(Exception rt)
							{
								MESSAGE_BOX("HATA-8:",rt,"");
							}
						finally
							{
								CONN_ftp=null;
								CONN_session=null;
							}
						return true;
					}

				@SuppressWarnings("unchecked")
				public boolean CCNC(String ip,int port,String user,String pass,boolean isSession,String B_package,String B_javaname)
					{

						if(!CONN_ip.equals(ip)||CONN_port!=port||!CONN_user.equals(user)||!CONN_pass.equals(pass)||CONN_isSession!=isSession)
							{
								try
									{
										CC(CONN_customer);
										CONN_ip=ip;
										CONN_port=port;
										CONN_user=user;
										CONN_pass=pass;
										CONN_isSession=isSession;
										// SshParameters
										// params=new
										// SshParameters(ftpHostname,ftpUsername,ftpPassword);
										CONN_sshParams=new SshParameters(ip,port,user,pass);
										CONN_sshParams.setConnectionTimeout(999999999); // 2
										// saat
										CONN_sshParams.setReadingTimeout(999999999);
										listCONNECTION.add(c);
									}
								catch(Exception rt)
									{
										MESSAGE_BOX("HATA-9:",rt,"");
									}
							}
						boolean iserror=false;
						try
							{
								if(CONN_ftp==null)
									{
										CONN_ftp=new Sftp(CONN_sshParams);
									}
								if(!CONN_ftp.isConnected())
									{
										CONN_ftp.connect();
									}
								CONN_ftp.setTimeout(999999999); // 2
								// saat
								if(isSession)
									{
										if(CONN_session==null||!CONN_SessionisConnected)
											{
												CONN_session=null;
												CONN_session=new SshSession(CONN_sshParams);
												CONN_session.setShellPrompt(CONN_shellPrompt);
												// session.setEcho(true);
												CONN_session.addSshListener(new SshListener()
													{
														public void connected(SshConnectedEvent sshConnectedEvent)
															{
																CONN_SessionisConnected=true;
															}

														public void disconnected(SshDisconnectedEvent sshDisconnectedEvent)
															{
																CONN_SessionisConnected=false;
															}

														public void dataReceived(SshDataReceivedEvent sshDataReceivedEvent)
															{
															}
													});
												CONN_session.connect();
												CONN_session.sendWait("su - s3","$");
											}
									}
							}
						catch(Exception rt)
							{
								iserror=true;
								String retstr="";
								StackTraceElement e[]=rt.getStackTrace();
								for(int mm=0;mm<e.length;mm++)
									{
										retstr=retstr+""+e[mm]+"\n";
									}
								retstr=retstr+"\n"+"Host Name: "+ip+"\n"+"Port: "+port+"\n"+"User: "+user+"\n"+"Pass: "+pass;
								findConsole(iworkbenchpage,"[/]"+B_package+"."+B_javaname,false).println("\n"+retstr);
								MESSAGE_BOX("HATA-10:",rt,"");
							}
						return !iserror;
					}
			}

		public void DERLE()
			{
				BUILD_status=false;
				// ISelection
				// isc=ieditorpart.getSite().getSelectionProvider().getSelection();
				// ITextSelection textSelection=(ITextSelection)isc;
				// ITextEditor editor=(ITextEditor)ieditorpart;
				//
				// if(ieditorpart.isDirty())
				// ieditorpart.doSave(new NullProgressMonitor());
				/*
				 * IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); IEditorPart editor=page.getActiveEditor(); page.saveEditor(editor,true);
				 */
				//
				// PlatformUI.getWorkbench().saveAllEditors(true);
				Display.getDefault().syncExec(new Runnable()
					{ // save all editors needs to be called by the ui thread!
							@Override
							public void run()
								{
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(ieditorpart,false);
								}
						});
				if(ieditorpart instanceof JavaEditor)
					{
						BUILD_package=""+System.currentTimeMillis();
						BUILD_javaname="";
						try
							{
								file=(IFile)((ITextEditor)ieditorpart).getEditorInput().getAdapter(org.eclipse.core.resources.IFile.class);
								org.eclipse.jdt.core.ICompilationUnit unit=JavaCore.createCompilationUnitFrom(file);
								javaName=unit.getTypes()[0].getFullyQualifiedName();
								String filePath=file.getLocation().toString();
								javaProject=unit.getJavaProject();
								//
								String outputPath=getJavaOutputPath();
								String classPath=getClasspathInfo(javaProject)+";"+outputPath;
								String filesToCompile=filePath;
								if(filesToCompile.length()!=0)
									{
										//
										BUILD_package=filesToCompile.substring(filesToCompile.indexOf("/src/")+5);
										BUILD_package=BUILD_package.substring(0,BUILD_package.lastIndexOf("/")).replace("/",".");
										BUILD_javaname=filesToCompile.substring(filesToCompile.lastIndexOf("/")+1);
										BUILD_javaname=BUILD_javaname.substring(0,BUILD_javaname.length()-5);
										BUILD_path=outputPath;
										//
										// "[?]"
										// [-]
										// [+]
										findConsole(iworkbenchpage,"[?]"+BUILD_package+"."+BUILD_javaname,true).println("-KOD KAYDEDÝLDÝ , DERLEME ÝÞLEMÝ BAÞLADI-");
										//
										@SuppressWarnings("unused")
										String compParam=compileFile(classPath,filesToCompile,outputPath);
										try
											{
												hotSwap(javaName,getClassToHotswap(),getProjectName());
											}
										catch(Exception e)
											{
												MESSAGE_BOX("HATA-11:",e,"");
											}
										findConsole(iworkbenchpage,"[+]"+BUILD_package+"."+BUILD_javaname,false).println("-DERLEME ÝÞLEMÝ TAMAMLANDI-");
										BUILD_status=true;
										// openSuccessDialog("Compilation/Hotswap successfully finished","Following params used for compilation:\n\n"+compParam);
									}
							}
						catch(Exception e)
							{
								findConsole(iworkbenchpage,"[-]"+BUILD_package+"."+BUILD_javaname,false).println(e.getLocalizedMessage()
										+"\nieditorpart.isDirty()="+ieditorpart.isDirty()
										+"\n-DERLEME ÝÞLEMÝ HATA ÝLE SONUÇLANDI-");
								MESSAGE_BOX("HATA-12:",e,"");
								BUILD_status=false;
							}
					}
			}

		class SampleView extends ViewPart
			{
				/**
				 * The ID of the view as specified by the extension.
				 */
				public static final String ID="mydene3.SampleView";
				public TableViewer viewer;
				private Action action1;
				private Action action2;
				private Action doubleClickAction;

				/*
				 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects as-is. These objects may be sensitive to
				 * the current input of the view, or ignore it and always show the same content (like Task List, for example).
				 */
				class ViewContentProvider implements IStructuredContentProvider
					{
						public void inputChanged(Viewer v,Object oldInput,Object newInput)
							{
							}

						public void dispose()
							{
							}

						public Object[] getElements(Object parent)
							{
								return new String[]
									{"One","Two","Three"
								};
						}
					}

				class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
					{
						public String getColumnText(Object obj,int index)
							{
								return getText(obj);
							}

						public Image getColumnImage(Object obj,int index)
							{
								return getImage(obj);
							}

						public Image getImage(Object obj)
							{
								return iworkbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
							}
					}

				class NameSorter extends ViewerSorter
					{
					}

				/**
				 * The constructor.
				 */
				public SampleView()
					{
					}

				/**
				 * This is a callback that will allow us to create the viewer and initialize it.
				 */
				public void createPartControl(Composite parent)
					{
						viewer=new TableViewer(parent,SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL);
						viewer.setContentProvider(new ViewContentProvider());
						viewer.setLabelProvider(new ViewLabelProvider());
						viewer.setSorter(new NameSorter());
						viewer.setInput(getViewSite());
						// Create the help context id for the viewer's control
						iworkbench.getHelpSystem().setHelp(viewer.getControl(),"d1.viewer");
						makeActions();
						hookContextMenu();
						hookDoubleClickAction();
						contributeToActionBars();
					}

				private void hookContextMenu()
					{
						MenuManager menuMgr=new MenuManager("#PopupMenu");
						menuMgr.setRemoveAllWhenShown(true);
						menuMgr.addMenuListener(new IMenuListener()
							{
								public void menuAboutToShow(IMenuManager manager)
									{
										SampleView.this.fillContextMenu(manager);
									}
							});
						Menu menu=menuMgr.createContextMenu(viewer.getControl());
						viewer.getControl().setMenu(menu);
						getSite().registerContextMenu(menuMgr,viewer);
					}

				private void contributeToActionBars()
					{
						IActionBars bars=getViewSite().getActionBars();
						fillLocalPullDown(bars.getMenuManager());
						fillLocalToolBar(bars.getToolBarManager());
					}

				private void fillLocalPullDown(IMenuManager manager)
					{
						manager.add(action1);
						manager.add(new Separator());
						manager.add(action2);
					}

				private void fillContextMenu(IMenuManager manager)
					{
						manager.add(action1);
						manager.add(action2);
						// Other plug-ins can contribute there actions here
						manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
					}

				private void fillLocalToolBar(IToolBarManager manager)
					{
						manager.add(action1);
						manager.add(action2);
					}

				private void makeActions()
					{
						action1=new Action()
							{
								public void run()
									{
										showMessage("Action 1 executed");
									}
							};
						action1.setText("Action 1");
						action1.setToolTipText("Action 1 tooltip");
						action1.setImageDescriptor(iworkbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
						action2=new Action()
							{
								public void run()
									{
										showMessage("Action 2 executed");
									}
							};
						action2.setText("Action 2");
						action2.setToolTipText("Action 2 tooltip");
						action2.setImageDescriptor(iworkbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
						doubleClickAction=new Action()
							{
								public void run()
									{
										ISelection selection=viewer.getSelection();
										Object obj=((IStructuredSelection)selection).getFirstElement();
										showMessage("Double-click detected on "+obj.toString());
									}
							};
					}

				private void hookDoubleClickAction()
					{
						viewer.addDoubleClickListener(new IDoubleClickListener()
							{
								public void doubleClick(DoubleClickEvent event)
									{
										doubleClickAction.run();
									}
							});
					}

				private void showMessage(String message)
					{
						MessageDialog.openInformation(viewer.getControl().getShell(),"Sample View",message);
					}

				/**
				 * Passing the focus request to the viewer's control.
				 */
				public void setFocus()
					{
						viewer.getControl().setFocus();
					}
			}

		class MyView extends ViewPart
			{
				public static final String ID="mydene3.MyView";
				@SuppressWarnings("unused")
				private Composite parent;
				@SuppressWarnings("unused")
				private Object data;

				public void createPartControl(Composite parent)
					{
						this.parent=parent;
					}

				public void setData(Object data)
					{
						this.data=data;
						/* View layout code. */
					}

				public void setFocus()
					{
					}
			}

		class Perspective implements IPerspectiveFactory
			{
				public static final String ID="mydene3.MyPerspective";

				public void createInitialLayout(IPageLayout layout)
					{
						layout.addStandaloneView(MyView.ID,true,IPageLayout.BOTTOM,1.0f,layout.getEditorArea());
						layout.setEditorAreaVisible(true); // hide the editor in the
																		// perspective
					}
			}

		private MessageConsoleStream findConsole(IWorkbenchPage page,final String name,boolean isClear)
			{
				MessageConsoleStream ms;
				ConsolePlugin plugin=ConsolePlugin.getDefault();
				IConsoleManager conMan=plugin.getConsoleManager();
				IConsole[] existing=conMan.getConsoles();
				IConsole[] existing2=new IConsole[existing.length+1];
				for(int i=0;i<existing.length;i++)
					{
						existing2[i+1]=existing[i];
						if(name.substring(3).equals(existing[i].getName().substring(3)))
							{
								final MessageConsole myConsole=(MessageConsole)existing[i]; //
								ms=myConsole.newMessageStream();
								//
								myConsole.firePropertyChange(this,IBasicPropertyConstants.P_TEXT,myConsole.getName(),name);
								if(isClear)
									{
										myConsole.clearConsole();
									}
								if(name.startsWith("[?]"))
									setConsoleBackFont(myConsole,240,230,180);
								else
									if(name.startsWith("[-]"))
										setConsoleBackFont(myConsole,255,0,0);
									else
										if(name.startsWith("[+]"))
											setConsoleBackFont(myConsole,0,255,0);
										else
											if(name.startsWith("[/]"))
												setConsoleBackFont(myConsole,255,70,0);
								conMan.showConsoleView(myConsole);
								return ms;
							}
					} // no console found, so create a new one final MessageConsole
				MessageConsole myConsole=new MessageConsole(name,ImageDescriptor.createFromImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Disaster.gif").createImage()));
				ms=myConsole.newMessageStream();
				setConsoleBackFont(myConsole,255,0,0);
				if(isClear)
					{
						myConsole.clearConsole();
					}
				// menuItem4.setImage(AbstractUIPlugin.imageDescriptorFromPlugin("mydene3","images/Disaster.gif").createImage());
				conMan.addConsoles(new IConsole[]
					{myConsole
				});
				existing2[0]=myConsole;
				conMan.addConsoles(existing2); // return myConsole;
				return ms;
			}

		public void setConsoleBackFont(final MessageConsole m,final int r,final int g,final int b)
			{
				if(Display.getCurrent()==null)
					{
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
							{
								public void run()
									{
										setConsoleBackFont(m,r,g,b);
									}
							});
					}
				else
					{
						// ITheme theme=PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
						// Font font=theme.getFontRegistry().get("findBugsEclipsePlugin.consoleFont");
						// m.setFont(font);
						m.setFont(new Font(null,"Verdana",8,SWT.NORMAL));
						m.setBackground(new org.eclipse.swt.graphics.Color(null,r,g,b));
						// m.getInputStream().setColor(new org.eclipse.swt.graphics.Color(null,r,g,b));
					}
			}

		public String getProjectName()
			{
				IProject project=file.getProject();
				return project.getName();
			}

		public void hotSwap(String fileName,String fileLocation,String projectName)
			{
				VirtualMachine vm=null;
				try
					{
						vm=getDebuggerVM(projectName);
						if(vm!=null)
							doHotSwap(vm,fileName,fileLocation);
					}
				catch(Exception e)
					{
						MESSAGE_BOX("HATA-13:",e,"");
					}
			}

		@SuppressWarnings(
			{"rawtypes","unchecked"
		})
	public void doHotSwap(VirtualMachine vm,String clsName,String clsLocation) throws IOException
			{
				Map map=new HashMap();
				if(vm.classesByName(clsName).size()>0)
					{
						ReferenceType rf=(ReferenceType)vm.classesByName(clsName).get(0);
						byte array[]=loadClassFile(clsLocation);
						map.put(rf,array);
						vm.redefineClasses(map);
					}
			}

		@SuppressWarnings("resource")
		public byte[] loadClassFile(String clsLocation) throws IOException
			{
				File f=new File(clsLocation);
				InputStream in=new FileInputStream(f);
				byte result[]=new byte[(int)f.length()];
				in.read(result);
				return result;
			}

		public VirtualMachine getDebuggerVM(String projectName)
			{
				VirtualMachine vm=null;
				IDebugTarget targets[]=DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
				JDIDebugTarget jdiTarget=matchTarget(targets,projectName);
				if(jdiTarget!=null)
					vm=jdiTarget.getVM();
				return vm;
			}

		public JDIDebugTarget matchTarget(IDebugTarget targets[],String projectName)
			{
				try
					{
						for(int i=0;i<targets.length;i++)
							{
								String targetProjectName=(String)targets[i].getLaunch().getLaunchConfiguration().getAttributes().get(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME);
								if(targetProjectName.equals(projectName))
									return (JDIDebugTarget)targets[i];
							}
					}
				catch(Exception e)
					{
						MESSAGE_BOX("HATA-14:",e,"");
					}
				return null;
			}

		public void openErrorDialog(String title,String error)
			{
				Status status=new Status(4,"compilePlugin",-1,error,null);
				ErrorDialog.openError(null,title,"",status);
			}

		public void openSuccessDialog(String title,String error)
			{
				Status status=new Status(1,"compilePlugin",0,error,null);
				ErrorDialog.openError(null,title,"",status);
			}

		public String getClassToHotswap() throws Exception
			{
				return getJavaOutputPath()+"\\"+javaName.replace('.','\\')+".class";
			}

		@SuppressWarnings(
			{"deprecation"
		})
	public String compileFile(String classPath,String fileToCompile,String outputPath) throws Exception
			{
				String compiler_source_version="1.6";
				BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("osgi.syspath").replace((char)92,'/')+"/CompilerSourceVersion.txt"),"ISO-8859-9"));
				compiler_source_version=in.readLine();
				in.close();
				//
				StringWriter outWriter=new StringWriter();
				StringWriter errWriter=new StringWriter();
				String param="-g -preserveAllLocals -source "+compiler_source_version+" -classpath "+classPath+" "+fileToCompile+" -d "+outputPath;
				boolean result=Main.compile(param,new PrintWriter(outWriter),new PrintWriter(errWriter));
				if(!result)
					throw new Exception(errWriter.toString());
				else
					return param;
			}

		public void bufferedOutToFile(String metin)
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyMMdd"); // .HH
				SimpleDateFormat sdf2=new SimpleDateFormat("dd.MM.yy HH:mm:ss"); // .HH
				String sTarih=sdf2.format(new Date());
				RandomAccessFile rotf=null;
				try
					{
						rotf=new RandomAccessFile(System.getProperty("osgi.syspath").replace((char)92,'/')+"/logs/XCOPY."+sdf.format(new Date())+".TMP","rw");
						BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rotf.getFD()),"ISO-8859-9"));
						rotf.seek(rotf.length());
						out.write("\n"+sTarih+": "+metin);
						out.flush();
						out.close();
						rotf.close();
					}
				catch(Exception rt)
					{
						try
							{
								rotf.close();
							}
						catch(Exception ty)
							{
							}
					}
			}

		public String getJavaOutputPath() throws Exception
			{
				String result="";
				try
					{
						IPath outputLocation=javaProject.getOutputLocation();
						IProject project=javaProject.getProject();
						outputLocation=outputLocation.removeFirstSegments(1);
						IFolder folder=project.getFolder(outputLocation);
						result=folder.getLocation().toString();
					}
				catch(Exception e)
					{
						MESSAGE_BOX("HATA-15:",e,"");
					}
				return result;
			}

		public String getClasspathInfo(IJavaProject project) throws Exception
			{
				String result="";
				try
					{
						IClasspathEntry entries[]=project.getResolvedClasspath(false);
						for(int i=0;i<entries.length;i++)
							{
								if(entries[i].getContentKind()==2&&entries[i].getEntryKind()==1)
									result=result+convertEntryToSystemPath(project,entries[i]);
								else
									if(entries[i].getEntryKind()==2)
										{
											IJavaProject referredProject=convertEntryToProject(entries[i]);
											result=result+getClasspathInfo(referredProject);
										}
							}
					}
				catch(Exception e)
					{
						MESSAGE_BOX("HATA-16:",e,"");
					}
				return result;
			}

		public String convertEntryToSystemPath(IJavaProject project,IClasspathEntry entry)
			{
				String strPath=null;
				if(entry.getPath().toString().endsWith(".jar")||entry.getPath().toString().endsWith(".zip"))
					{
						strPath=entry.getPath().toString();
						if(strPath.startsWith("/"))
							strPath=(System.getProperty("osgi.instance.area")+strPath.substring(1)).substring(6);
					}
				else
					{
						IProject project2=project.getProject();
						IPath tmpPath=entry.getPath().removeFirstSegments(1);
						IFolder folder=project2.getFolder(tmpPath);
						strPath=folder.getLocation().toString();
					}
				return "\""+strPath+"\""+";";
			}

		public IJavaProject convertEntryToProject(IClasspathEntry entry)
			{
				String name=entry.getPath().toString().substring(1);
				IProject project=ResourcesPlugin.getWorkspace().getRoot().getProject(name);
				IJavaProject javaProject=JavaCore.create(project);
				return javaProject;
			}

		public void insertEditorText(String text)
			{
				ISelection isc=ieditorpart.getSite().getSelectionProvider().getSelection();
				ITextSelection textSelection=(ITextSelection)isc;
				int offset=textSelection.getOffset(); // etc.
				ITextEditor editor=(ITextEditor)ieditorpart;
				IDocumentProvider dp=editor.getDocumentProvider();
				IDocument doc=dp.getDocument(editor.getEditorInput());
				try
					{
						doc.replace(offset,0,text);
						NullProgressMonitor monitor=new NullProgressMonitor();
						if(ieditorpart.isDirty())
							ieditorpart.doSave(monitor);
					}
				catch(Exception e1)
					{
						MESSAGE_BOX("HATA-17:",e1,"");
					}
			}

		public void dispose()
			{
				if(menu!=null)
					{
						// menu.dispose();
					}
			}

		public void init(IWorkbenchWindow window)
			{
				this.window=window;
			}

		public void run(IAction action)
			{
				if(iworkbench==null)
					{
						iworkbench=PlatformUI.getWorkbench();
						window=iworkbench.getActiveWorkbenchWindow();
						iworkbenchpage=window.getActivePage();
						ieditorpart=iworkbenchpage.getActiveEditor();
					}
				Job job=new Job("Run-SFTP")
					{
						protected IStatus run(IProgressMonitor monitor)
							{
								RUN_SFTP();
								return Status.OK_STATUS;
							}
					};
				job.setUser(true);
				job.schedule();
			}

		public void selectionChanged(IAction action,ISelection selection)
			{
				try
					{
						BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("osgi.syspath").replace((char)92,'/')+"/AYAR_2.csv"),"ISO-8859-9"));
						String d[]=in.readLine().split(";");
						MENU_firma=d[0]+" / "+d[1];
						in.close();
					}
				catch(Exception e1)
					{
						MESSAGE_BOX("HATA-1:",e1,"");
					}
				action.setToolTipText(MENU_firma);
			}

		public void MESSAGE_BOX(String header,Exception rt,String m)
			{
				String message="";
				if(rt!=null)
					{
						rt.printStackTrace();
						String retstr=rt.getMessage()+"\n";
						StackTraceElement ee[]=rt.getStackTrace();
						for(int i=0;i<ee.length;i++)
							{
								retstr=retstr+ee[i]+"\n";
							}
						message=m+"\n"+retstr;
					}
				else
					message=m;
				//
				bufferedOutToFile(message);
				/*
				 * JFrame frmOpt=new JFrame();
				 * frmOpt.setVisible(true);
				 * frmOpt.setLocation(100,100);
				 * frmOpt.setAlwaysOnTop(true);
				 * JOptionPane.showMessageDialog(frmOpt,message,header,1);
				 * frmOpt.dispose();
				 */
			}

		public String MESSAGE_TEXT(Exception rt)
			{
				String retstr="";
				if(rt!=null)
					{
						retstr=rt.getMessage()+"\n";
						StackTraceElement ee[]=rt.getStackTrace();
						for(int i=0;i<ee.length;i++)
							{
								retstr=retstr+ee[i]+"\n";
							}
					}
				return retstr;
			}

		class AYAR_FRAMES extends JFrame
			{
				private static final long serialVersionUID=1L;
				BufferedReader in;
				String str;
				String dizi[];
				boolean sil;
				String ara;
				JFrame jf;
				JPanel jPanel1=new JPanel();
				@SuppressWarnings("rawtypes")
				JComboBox sec=new JComboBox();
				@SuppressWarnings("rawtypes")
				JComboBox sec2=new JComboBox();
				JButton button=new JButton();
				JLabel metin=new JLabel();
				public GridLayout gridLayout1=new GridLayout();
				public GridLayout gridLayout2=new GridLayout();
				public VerticalFlowLayout verticalFlowLayout1=new VerticalFlowLayout();
				public VerticalFlowLayout verticalFlowLayout2=new VerticalFlowLayout();
				public JPanel jPanel3=new JPanel();
				public JPanel jPanel2=new JPanel();

				public AYAR_FRAMES()
					{
						try
							{
								jbInit();
							}
						catch(Exception e)
							{
								MESSAGE_BOX("HATA-18:",e,"");
							}
					}

				@SuppressWarnings("unchecked")
				private void jbInit() throws Exception
					{
						getContentPane().setLayout(gridLayout1);
						this.setForeground(new java.awt.Color(151,210,190));
						this.setSize(500,120);
						jPanel1.setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
						jPanel1.setBackground(new java.awt.Color(0,165,165));
						jPanel1.setLayout(gridLayout2);
						metin.setBorder(BorderFactory.createEtchedBorder());
						metin.setForeground(java.awt.Color.white);
						button.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
									{
										button_actionPerformed(e);
									}
							});
						button.setText("Kaydet");
						sec.setMaximumRowCount(10);
						gridLayout2.setColumns(1);
						gridLayout2.setHgap(3);
						gridLayout2.setRows(3);
						gridLayout2.setVgap(3);
						jPanel3.setBackground(new java.awt.Color(0,165,165));
						jPanel3.setLayout(verticalFlowLayout1);
						jPanel2.setBackground(new java.awt.Color(0,165,165));
						jPanel2.setLayout(verticalFlowLayout2);
						this.getContentPane().add(jPanel1,null);
						jPanel1.add(jPanel2);
						jPanel2.add(sec);
						jPanel1.add(jPanel3);
						jPanel3.add(button);
						jPanel1.add(metin,null);
						try
							{
								String kayitli="";
								ara=System.getProperty("osgi.syspath").replace((char)92,'/');
								in=new BufferedReader(new InputStreamReader(new FileInputStream(ara+"/AYAR_2.csv"),"ISO-8859-9"));
								while((str=in.readLine())!=null)
									{
										kayitli=str;
										dizi=str.split(";");
										metin.setText(dizi[0]+"-"+dizi[1]+"-"+dizi[2]);
										break;
									}
								in.close();
								sec.setMaximumRowCount(20);
								//
								in=new BufferedReader(new InputStreamReader(new FileInputStream(ara+"/AYAR_1.csv"),"ISO-8859-9"));
								while((str=in.readLine())!=null)
									{
										dizi=str.split(";");
										sec.addItem(dizi[0]+"-"+dizi[1]+"-"+dizi[2]);
										sec2.addItem(dizi[0]+";"+dizi[1]+";"+dizi[2]+";"+dizi[3]+";"+dizi[4]+";"+dizi[5]+";"+dizi[6]+";"+dizi[7]+";"+dizi[8]+";"+dizi[9]+";"+dizi[10]);
									}
								in.close();
								sec2.setSelectedItem(kayitli);
								sec.setSelectedIndex(sec2.getSelectedIndex());
								jf=this;
							}
						catch(Exception rt)
							{
								MESSAGE_BOX("HATA-19:",rt,"");
							}
					}

				public void button_actionPerformed(ActionEvent e)
					{
						if(sec.getSelectedIndex()!=-1)
							{
								try
									{
										ara=System.getProperty("osgi.syspath").replace((char)92,'/');
										sil=new File(ara+"/AYAR_2.csv").delete();
										new File(ara+"/AYAR_2.csv").createNewFile();
										BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ara+"/AYAR_2.csv"),"ISO-8859-9"));
										str=""+sec2.getItemAt(sec.getSelectedIndex());
										dizi=str.split(";");
										metin.setText(dizi[0]+"-"+dizi[1]+"-"+dizi[2]);
										out.write(dizi[0]+";"+dizi[1]+";"+dizi[2]+";"+dizi[3]+";"+dizi[4]+";"+dizi[5]+";"+dizi[6]+";"+dizi[7]+";"+dizi[8]+";"+dizi[9]+";"+dizi[10]+"\n");
										out.close();
										MENU_firma=dizi[0]+" / "+dizi[1];
									}
								catch(Exception rt)
									{
										MESSAGE_BOX("HATA-20:",rt,"");
									}
							}
						else
							{
								MESSAGE_BOX("Dikkat",null,"Seçim Yapmadýnýz");
							}
						jf.dispose();
					}

				public void MESSAGE_BOX(String header,Throwable rt,String m)
					{
						String message="";
						if(rt!=null)
							{
								rt.printStackTrace();
								String retstr=rt.getMessage()+"\n";
								StackTraceElement ee[]=rt.getStackTrace();
								for(int i=0;i<ee.length;i++)
									{
										retstr=retstr+ee[i]+"\n";
									}
							}
						else
							message=m;
						//
						JFrame frmOpt=new JFrame();
						frmOpt.setVisible(true);
						frmOpt.setLocation(100,100);
						frmOpt.setAlwaysOnTop(true);
						JOptionPane.showMessageDialog(frmOpt,message,header,1);
						frmOpt.dispose();
					}

				class VerticalFlowLayout implements LayoutManager,java.io.Serializable
					{
						private boolean maximizeOtherDimension=false;

						public void setMaximizeOtherDimension(boolean max)
							{
								maximizeOtherDimension=max;
							}

						public boolean isMaximizeOtherDimension()
							{
								return maximizeOtherDimension;
							}

						public static final int TOP=0;
						public static final int CENTER=1;
						public static final int BOTTOM=2;
						public static final int LEADING=3;
						public static final int TRAILING=4;
						int align; // This is for 1.1 serialization
						// compatibility
						int newAlign; // This is the one we actually
						// use
						int hgap;
						int vgap;
						private static final long serialVersionUID=-7262534875583282631L;

						public VerticalFlowLayout()
							{
								this(CENTER,5,5);
							}

						public VerticalFlowLayout(int align)
							{
								this(align,5,5);
							}

						public VerticalFlowLayout(int align,int hgap,int vgap)
							{
								this.hgap=hgap;
								this.vgap=vgap;
								setAlignment(align);
							}

						public int getAlignment()
							{
								return newAlign;
							}

						public void setAlignment(int align)
							{
								this.newAlign=align;
								switch(align)
									{
										case LEADING:
											this.align=TOP;
											break;
										case TRAILING:
											this.align=BOTTOM;
											break;
										default:
											this.align=align;
											break;
									}
							}

						public int getHgap()
							{
								return hgap;
							}

						public void setHgap(int hgap)
							{
								this.hgap=hgap;
							}

						public int getVgap()
							{
								return vgap;
							}

						public void setVgap(int vgap)
							{
								this.vgap=vgap;
							}

						public void addLayoutComponent(String name,Component comp)
							{
							}

						public void removeLayoutComponent(Component comp)
							{
							}

						public Dimension preferredLayoutSize(Container target)
							{
								synchronized(target.getTreeLock())
									{
										Dimension dim=new Dimension(0,0);
										int nmembers=target.getComponentCount();
										boolean firstVisibleComponent=true;
										for(int i=0;i<nmembers;i++)
											{
												Component m=target.getComponent(i);
												if(m.isVisible())
													{
														Dimension d=m.getPreferredSize();
														dim.width=Math.max(dim.width,d.width);
														if(firstVisibleComponent)
															{
																firstVisibleComponent=false;
															}
														else
															{
																dim.height+=vgap;
															}
														dim.height+=d.height;
													}
											}
										Insets insets=target.getInsets();
										dim.width+=insets.left+insets.right+hgap*2;
										dim.height+=insets.top+insets.bottom+vgap*2;
										return dim;
									}
							}

						public Dimension minimumLayoutSize(Container target)
							{
								synchronized(target.getTreeLock())
									{
										Dimension dim=new Dimension(0,0);
										int nmembers=target.getComponentCount();
										for(int i=0;i<nmembers;i++)
											{
												Component m=target.getComponent(i);
												if(m.isVisible())
													{
														Dimension d=m.getMinimumSize();
														dim.width=Math.max(dim.width,d.width);
														if(i>0)
															{
																dim.height+=vgap;
															}
														dim.height+=d.height;
													}
											}
										Insets insets=target.getInsets();
										dim.width+=insets.left+insets.right+hgap*2;
										dim.height+=insets.top+insets.bottom+vgap*2;
										return dim;
									}
							}

						private void moveComponents(Container target,int x,int y,int width,int height,int colStart,int colEnd,boolean ltr)
							{
								synchronized(target.getTreeLock())
									{
										switch(newAlign)
											{
												case TOP:
													y+=ltr?0:height;
													break;
												case CENTER:
													y+=height/2;
													break;
												case BOTTOM:
													y+=ltr?height:0;
													break;
												case LEADING:
													break;
												case TRAILING:
													y+=height;
													break;
											}
										for(int i=colStart;i<colEnd;i++)
											{
												Component m=target.getComponent(i);
												if(m.isVisible())
													{
														if(ltr)
															{
																m.setLocation(x+(width-m.getWidth())/2,y);
															}
														else
															{
																m.setLocation(x+(width-m.getWidth())/2,target.getHeight()-y-m.getHeight());
															}
														y+=m.getHeight()+vgap;
													}
											}
									}
							}

						public void layoutContainer(Container target)
							{
								synchronized(target.getTreeLock())
									{
										Insets insets=target.getInsets();
										int maxwidth=target.getWidth()-(insets.left+insets.right+hgap*2);
										int maxheight=target.getHeight()-(insets.top+insets.bottom+vgap*2);
										int nmembers=target.getComponentCount();
										int x=insets.left+hgap,y=0;
										int colw=0,start=0;
										boolean ltr=target.getComponentOrientation().isLeftToRight();
										for(int i=0;i<nmembers;i++)
											{
												Component m=target.getComponent(i);
												if(m.isVisible())
													{
														Dimension d=m.getPreferredSize();
														if(maximizeOtherDimension)
															{
																d.width=maxwidth;
															}
														m.setSize(d.width,d.height);
														if((y==0)||((y+d.height)<=maxheight))
															{
																if(y>0)
																	{
																		y+=vgap;
																	}
																y+=d.height;
																colw=Math.max(colw,d.width);
															}
														else
															{
																moveComponents(target,insets.left+hgap,y,maxheight-x,colw,start,i,ltr);
																moveComponents(target,x,insets.top+vgap,colw,maxheight-y,start,i,ltr);
																y=d.height;
																x+=hgap+colw;
																colw=d.width;
																start=i;
															}
													}
											}
										moveComponents(target,x,insets.top+vgap,colw,maxheight-y,start,nmembers,ltr);
									}
							}

						private static final int currentSerialVersion=1;
						private int serialVersionOnStream=currentSerialVersion;

						private void readObject(ObjectInputStream stream) throws IOException,ClassNotFoundException
							{
								stream.defaultReadObject();
								if(serialVersionOnStream<1)
									{
										// "newAlign" field
										// wasn't present,
										// so use the old
										// "align"
										// field.
										setAlignment(this.align);
									}
								serialVersionOnStream=currentSerialVersion;
							}

						public String toString()
							{
								String str="";
								switch(align)
									{
										case TOP:
											str=",align=top";
											break;
										case CENTER:
											str=",align=center";
											break;
										case BOTTOM:
											str=",align=bottom";
											break;
										case LEADING:
											str=",align=leading";
											break;
										case TRAILING:
											str=",align=trailing";
											break;
									}
								return getClass().getName()+"[hgap="+hgap+",vgap="+vgap+str+"]";
							}
					}
			}
	}
// try
// {
// myConsole.getInputStream().write(Platform.getInstanceLocation().getURL().getPath()+"\n");//
// /F:/eclipse/samples/
// myConsole.getInputStream().write("eclipse.home.location>"+System.getProperty("eclipse.home.location")+"\n");//
// file:/F:/eclipse/
// myConsole.getInputStream().write("eclipse_launcher"+System.getProperty("eclipse.launcher")+"\n");//
// F:\eclipse\eclipse.exe
// myConsole.getInputStream().write("osgi.instance.area"+System.getProperty("osgi.instance.area")+"\n");//
// file:/F:/eclipse/samples/
// myConsole.getInputStream().write("osgi.syspath"+System.getProperty("osgi.syspath")+"\n");//
// f:\eclipse\plugins
// myConsole.getInputStream().write("osgi.install.area"+System.getProperty("osgi.install.area")+"\n");//
// file:/F:/eclipse/
// myConsole.getInputStream().write("outputPath");//
// F:/eclipse/samples/mydene3/classes
// myConsole.getInputStream().write("filesToCompile");//
// F:/eclipse/samples/mydene3/src/mydene3/actions/testAction.java
// }
// catch(IOException e)
// {
// e.printStackTrace();
// }
