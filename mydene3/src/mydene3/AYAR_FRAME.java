package mydene3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AYAR_FRAME extends JFrame
	{
		BufferedReader in;
		String str;
		String dizi[];
		boolean sil;
		String ara;
		JFrame jf;
		JPanel jPanel1=new JPanel();
		JComboBox sec=new JComboBox();
		JComboBox sec2=new JComboBox();
		JButton button=new JButton();
		JLabel metin=new JLabel();
		public GridLayout gridLayout1=new GridLayout();
		public GridLayout gridLayout2=new GridLayout();
		public VerticalFlowLayout verticalFlowLayout1=new VerticalFlowLayout();
		public VerticalFlowLayout verticalFlowLayout2=new VerticalFlowLayout();
		public JPanel jPanel3=new JPanel();
		public JPanel jPanel2=new JPanel();

		public AYAR_FRAME()
			{
				try
					{
						jbInit();
					}
				catch(Exception exception)
					{
						exception.printStackTrace();
					}
			}

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
						MESSAGE_BOX("Hata:13",rt,"");
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
							}
						catch(Exception rt)
							{
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
				String[] options=
					{"delete","hide","break"
				};
				JOptionPane.showMessageDialog(frmOpt,message,header,1);
				frmOpt.dispose();

			}
	}
