$ErrorActionPreference = "Continue"
Write-Host "Node:"
node --version
Write-Host "`nNPM:"
npm.cmd --version
Write-Host "`nJava:"
$jdk = (Resolve-Path "$PSScriptRoot\..\tools\jdk8u462-b08").Path
$env:JAVA_HOME = $jdk
$env:Path = "$jdk\bin;$env:Path"
java -version
Write-Host "`nJavac:"
javac -version
Write-Host "`nMaven:"
& "$PSScriptRoot\..\tools\apache-maven-3.9.9\bin\mvn.cmd" -version
Write-Host "`nMySQL service:"
Get-Service MySQL80
Write-Host "`nMySQL client:"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" --version
