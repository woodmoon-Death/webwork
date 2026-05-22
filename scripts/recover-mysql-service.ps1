$ErrorActionPreference = "Continue"

Write-Host "Killing leftover mysqld.exe processes..."
taskkill /F /IM mysqld.exe

Write-Host "Removing reset init file if it exists..."
Remove-Item -LiteralPath "C:\ProgramData\MySQL\reset-root-password.sql" -Force -ErrorAction SilentlyContinue

Write-Host "Starting MySQL80 service..."
Start-Service -Name MySQL80
Start-Sleep -Seconds 5

Write-Host "Service status:"
Get-Service MySQL80

Write-Host "Recent MySQL errors, if readable:"
Get-Content "C:\ProgramData\MySQL\MySQL Server 8.0\Data\LAPTOP-NQG811MG.err" -Tail 40 -ErrorAction SilentlyContinue
