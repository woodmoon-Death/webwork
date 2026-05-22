$ErrorActionPreference = "Stop"
$mysql = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if (!(Test-Path $mysql)) {
  throw "MySQL client not found at $mysql"
}
$script = (Resolve-Path "$PSScriptRoot\..\backend\src\main\resources\schema.sql").Path
$user = Read-Host "MySQL username" 
& $mysql "-u$user" "-p" "--execute=source $script"
