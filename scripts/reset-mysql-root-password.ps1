$ErrorActionPreference = "Stop"

$mysqlBin = "C:\Program Files\MySQL\MySQL Server 8.0\bin"
$mysqld = Join-Path $mysqlBin "mysqld.exe"
$mysql = Join-Path $mysqlBin "mysql.exe"
$myIni = "C:\ProgramData\MySQL\MySQL Server 8.0\my.ini"
$newPassword = "200511"
$initFile = "C:\ProgramData\MySQL\reset-root-password.sql"

function Invoke-Native {
  param(
    [Parameter(Mandatory = $true)][string]$FilePath,
    [Parameter(ValueFromRemainingArguments = $true)][string[]]$Arguments
  )
  & $FilePath @Arguments
  if ($LASTEXITCODE -ne 0) {
    throw "Command failed with exit code $LASTEXITCODE`: $FilePath $Arguments"
  }
}

if (!(Test-Path $mysqld)) {
  throw "mysqld.exe not found at $mysqld"
}
if (!(Test-Path $mysql)) {
  throw "mysql.exe not found at $mysql"
}
if (!(Test-Path $myIni)) {
  throw "my.ini not found at $myIni"
}

Write-Host "Writing MySQL init file..."
@"
ALTER USER 'root'@'localhost' IDENTIFIED BY '$newPassword';
FLUSH PRIVILEGES;
"@ | Set-Content -Encoding ASCII -Path $initFile

Write-Host "Stopping MySQL80 service..."
$service = Get-Service -Name MySQL80
if ($service.Status -ne "Stopped") {
  Stop-Service -Name MySQL80 -Force
  $service.WaitForStatus("Stopped", "00:00:30")
}

Write-Host "Cleaning leftover mysqld.exe processes..."
taskkill /F /IM mysqld.exe 2>$null
Start-Sleep -Seconds 2

Write-Host "Starting temporary MySQL server with init-file..."
$process = Start-Process `
  -FilePath $mysqld `
  -ArgumentList "--defaults-file=`"$myIni`"", "--init-file=`"$initFile`"" `
  -WindowStyle Hidden `
  -PassThru

Start-Sleep -Seconds 12

Write-Host "Stopping temporary MySQL server..."
if (!$process.HasExited) {
  taskkill /F /PID $process.Id
}
Start-Sleep -Seconds 3

Write-Host "Cleaning temporary MySQL server process..."
taskkill /F /IM mysqld.exe 2>$null
Start-Sleep -Seconds 2

Write-Host "Starting MySQL80 service..."
Start-Service -Name MySQL80
(Get-Service -Name MySQL80).WaitForStatus("Running", "00:00:30")
Start-Sleep -Seconds 3

Write-Host "Verifying new password..."
Invoke-Native $mysql "-uroot" "-p$newPassword" "--execute=SELECT VERSION();"

Remove-Item -LiteralPath $initFile -Force -ErrorAction SilentlyContinue
Write-Host "MySQL root password has been reset to $newPassword"
