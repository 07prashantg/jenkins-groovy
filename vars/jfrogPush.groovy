#!/groovy

import java.net.InetAddress

// Get the current EC2 instance IP address
InetAddress inetAddress = InetAddress.getLocalHost();
String ec2Ip = inetAddress.getHostAddress();

// Set the username and repository URL
String username = "admin"
String repositoryUrl = "http://${ec2Ip}:8082/artifactory/example-repo-local/"

// Set the jar file path
String jarFilePath = "/path/to/kubernetes-configmap-reload-0.0.1-SNAPSHOT.jar"

// Upload the jar file to Jfrog
def file = new File(jarFilePath)
def url = new URL(repositoryUrl)
def connection = url.openConnection()
connection.setRequestMethod("PUT")
connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString("${username}:".bytes));
connection.doOutput = true
def outputStream = connection.getOutputStream()
def fileInputStream = new FileInputStream(file)
def buffer = new byte[1024]
int bytesRead
while ((bytesRead = fileInputStream.read(buffer)) != -1) {
    outputStream.write(buffer, 0, bytesRead)
}
outputStream.close()
fileInputStream.close()

// Check the response status code
if (connection.responseCode == 200) {
    println("Jar file uploaded successfully")
} else {
    println("Error uploading jar file: ${connection.responseCode}")
}
