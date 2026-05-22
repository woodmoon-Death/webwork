$ErrorActionPreference = "Stop"
$maven = "$PSScriptRoot\..\tools\apache-maven-3.9.9\bin\mvn.cmd"
$jdk = (Resolve-Path "$PSScriptRoot\..\tools\jdk8u462-b08").Path
if (!(Test-Path $maven)) {
  throw "Maven not found at $maven"
}
$env:JAVA_HOME = $jdk
$env:Path = "$jdk\bin;$env:Path"
Set-Location "$PSScriptRoot\..\backend"
& $maven spring-boot:run
