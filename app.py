#!/usr/bin/python

import os, time, tarfile
from flask import Flask, render_template, jsonify, request, send_from_directory

class MyServer(Flask):

    def __init__(self, *args, **kwargs):

        super(MyServer, self).__init__(*args, **kwargs)    

app = MyServer(__name__)
app.config.from_object(__name__)

app.config['PROBE_INSTALLER_PATH'] = 'ProbeInstaller/'
app.config['AGENT_INSTALLER_PATH'] = 'AgentInstaller/'

app.config['PROBE_BAT_INSTALLER'] = 'probeInstall.bat'
app.config['AGENT_BAT_INSTALLER'] = 'agentInstall.bat'

app.config['PROBE_INSTALLER_CONFIG_FILE_PATH'] = 'ProbeInstaller/config.properties'
app.config['AGENT_INSTALLER_CONFIG_FILE_PATH'] = 'AgentInstaller/config.properties'

app.config['CallbackFinishedUrl']='https://silent-installer-generator.herokuapp.com/installFinished'
app.config['CallbackUnFinishedUrl']='https://silent-installer-generator.herokuapp.com/installIncomplete'

@app.route("/")
def index():
    return render_template("wprobe.html")

@app.route("/wprobe")
def wprobe():
    return render_template("wprobe.html")    

@app.route("/wagent")
def wagent():
    return render_template("wagent.html")    

@app.route("/<path:filename>")
def getFile(filename):
    return send_from_directory(app.root_path + '/', filename)

@app.route('/installFinished', methods=['POST'])
def installFinished():

    if request.method == 'POST':  

        fName=request.form['param']+".tar.gz"

        if os.path.isfile(fName):
            os.remove(fName)

        return jsonify({"success":True}) 

    return jsonify({"success":False})    

@app.route('/installIncomplete', methods=['POST'])
def installIncomplete():

    if request.method == 'POST':  

        fName=request.form['param']+".tar.gz"    	

        print "End user who downloaded "+fName+" couldn't get the installation to work"

        return jsonify({"success":True}) 

    return jsonify({"success":False})             

@app.route('/probeUploadInfo', methods=['POST'])
def probeUploadInfo():

    if request.method == 'POST':

        command="cmd /c WindowsProbeSetup.exe /s /v\" /qn "  

        for key, value in request.form.items():
            command+=(key+"="+value+" ")

        command+="\""

        param=str(int(time.time()))
        fName=param+".tar.gz"

        with open(app.config['PROBE_INSTALLER_CONFIG_FILE_PATH'], 'w') as cFile:

            cFile.write("Command="+command+"\n")

            cFile.write("CallbackFinishedUrl="+app.config['CallbackFinishedUrl']+"\n")
            cFile.write("CallbackUnFinishedUrl="+app.config['CallbackUnFinishedUrl']+"\n")

            cFile.write("Param="+param+"\n") 

        with tarfile.open(fName, "w:gz") as tar:     

            tar.add(app.config['PROBE_INSTALLER_PATH'], arcname=None,  recursive=True)                     
            tar.add(app.config['PROBE_BAT_INSTALLER'])

        return jsonify({"fName":"/"+fName})            

    return jsonify({"success":False})    

@app.route('/agentUploadInfo', methods=['POST'])
def agentUploadInfo():

    if request.method == 'POST':

        command="cmd /c WindowsAgentSetup.exe /s /v\" /qn "  

        for key, value in request.form.items():
            command+=(key+"="+value+" ")

        command+="\""

        param=str(int(time.time()))
        fName=param+".tar.gz"

        with open(app.config['AGENT_INSTALLER_CONFIG_FILE_PATH'], 'w') as cFile:

            cFile.write("Command="+command+"\n")

            cFile.write("CallbackFinishedUrl="+app.config['CallbackFinishedUrl']+"\n")
            cFile.write("CallbackUnFinishedUrl="+app.config['CallbackUnFinishedUrl']+"\n")

            cFile.write("Param="+param+"\n") 

        with tarfile.open(fName, "w:gz") as tar:  

            tar.add(app.config['AGENT_INSTALLER_PATH'], arcname=None,  recursive=True)                     
            tar.add(app.config['AGENT_BAT_INSTALLER'])

        return jsonify({"fName":"/"+fName})            

    return jsonify({"success":False})     

if __name__ == "__main__":
	
	port = int(os.environ.get('PORT', 5000)) 
	app.run(host='0.0.0.0', port=port)	
