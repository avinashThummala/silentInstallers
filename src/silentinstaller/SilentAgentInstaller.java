package silentinstaller;

import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class SilentAgentInstaller extends javax.swing.JFrame
{
    private String command, callbackFUrl, callbackUFUrl, param;
    private boolean cFinished=false;
    private int exitValue=-1;

    private javax.swing.JButton cButton;
    private javax.swing.JButton fButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JProgressBar progressBar;    

    public SilentAgentInstaller() 
    {  	
        initComponents();
        readProperties();
        executeCommand();
        
        setVisible(true);        

        updateProgressBar();        
    } 

    public void updateProgressBar()
    {
    	ProgressUpdater task = new ProgressUpdater();

        Thread executorThread = new Thread(task);
        executorThread.start(); 
    }

    public void executeCommand()
    {
        CommandExecutor task = new CommandExecutor();

        Thread executorThread = new Thread(task);
        executorThread.start();        
    }

    public void readProperties()
    {
        Properties prop = new Properties();

        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/config.properties");

            if(inputStream!=null)
                prop.load(inputStream);              
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
 
        command = prop.getProperty("Command");
        
        callbackFUrl = prop.getProperty("CallbackFinishedUrl");
        callbackUFUrl = prop.getProperty("CallbackUnFinishedUrl");
        
        param = prop.getProperty("Param");
    }
    
    public void postData(String url)
    {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("param", param));

        try
        {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpclient.execute(httppost);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }       

    }

    public void finishInstallation()
    {
    	if(exitValue==0)
    	{
	        progressBar.setValue(100);
	        fButton.setEnabled(true);
	        
	        postData(callbackFUrl);
    	}
    	else
    	{
	        progressBar.setValue(0);
	        javax.swing.JOptionPane.showMessageDialog(this, "The Installation can't be completed.\nError code: "+exitValue);
	        
	        postData(callbackUFUrl);	        
    	}
    }

    private void initComponents() 
    {

        logoLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        fButton = new javax.swing.JButton();
        cButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Probe Installer");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Watchpoint_DATA_Logo.png")));

        progressBar.setFont(new java.awt.Font("Tahoma", 1, 12));
        progressBar.setToolTipText("");
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        fButton.setEnabled(false);
        fButton.setText("Finish");

        fButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fButtonActionPerformed(evt);
            }
        });

        cButton.setText("Cancel");
        
        cButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fButton)
                    .addComponent(cButton))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        pack();
    }

    private void fButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {
        this.dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING));     
    }

    private void cButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {
        this.dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING));          
    }
    
    class ProgressUpdater implements Runnable{

        @Override
        public void run() 
        {
            int progress=0;

            try
            {
	            while(!cFinished)
	            {
	                progressBar.setValue(progress);
	                progress=(progress+1)%101;
	                
	                Thread.sleep(400);
	            }
	
	            finishInstallation();
            }
            catch(Exception ex)
            {
            	ex.printStackTrace();
            }
            
        }

    }    

    class CommandExecutor implements Runnable{

        @Override
        public void run() 
        {
            Process process = null;

            try 
            {            	
                process = Runtime.getRuntime().exec(command);
                process.waitFor();
                
                exitValue=process.exitValue();
                cFinished=true;
            }
            catch (Exception e) 
            {
                exitValue=-1;
                cFinished=true;
                
                e.printStackTrace();
            }

        }

    }

    public static void main(String args[]) 
    {

        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) 
            {
                if ("Nimbus".equals(info.getName())) 
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }

            }

        } 
        catch (Exception ex)
        {
        	ex.printStackTrace();
            java.util.logging.Logger.getLogger(SilentAgentInstaller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new SilentAgentInstaller();
            }
        });

    }

}
