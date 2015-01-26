package silentinstaller;

import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class SilentInstaller extends javax.swing.JFrame
{
    private String command, callbackUrl, fName;
    private boolean cFinished=false;

    private javax.swing.JButton cButton;
    private javax.swing.JButton fButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JProgressBar progressBar;    

    public SilentInstaller() 
    {
        initComponents();
        readProperties();
        executeCommand();

        updateProgressBar();
    } 

    public void updateProgressBar()
    {
        int progress=0;

        while(!cFinished)
        {
            progressBar.setValue(progress);
            progress=(progress+1)%101;
        }

        finishInstallation();

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
            InputStream inputStream = getClass().getResourceAsStream("/cConfig.properties");

            if(inputStream!=null)
                prop.load(inputStream);              
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
 
        command = prop.getProperty("Command");
        callbackUrl = prop.getProperty("CallbackUrl");
        fName = prop.getProperty("Param");
    }

    public void postData()
    {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(callbackUrl);

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("fName", fName));

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
        progressBar.setValue(100);
        fButton.setEnabled(true);
        postData();
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

    class CommandExecutor implements Runnable{

        @Override
        public void run() 
        {
            Process process = null;

            try 
            {
                process = Runtime.getRuntime().exec(command);
                process.waitFor();

                cFinished=true;
            }
            catch (Exception e) 
            {
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
            java.util.logging.Logger.getLogger(SilentInstaller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new SilentInstaller().setVisible(true);
            }
        });

    }

}
