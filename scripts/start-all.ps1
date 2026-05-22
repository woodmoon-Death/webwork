$ErrorActionPreference = "Stop"

$root = (Resolve-Path "$PSScriptRoot\..").Path
$logDir = Join-Path $root "logs"
$frontendUrl = "http://127.0.0.1:5173/"
$backendHealthUrl = "http://127.0.0.1:8080/api/posts?filter=all"
$nodePath = "C:\Program Files\nodejs"

New-Item -ItemType Directory -Force -Path $logDir | Out-Null

function Write-Step($message) {
  Write-Host "==> $message" -ForegroundColor Cyan
}

function Stop-ProjectProcesses {
  Write-Step "Stopping old project services"
  try {
    $processes = Get-CimInstance Win32_Process |
      Where-Object {
        $_.Name -in @("java.exe", "node.exe", "powershell.exe") -and
        $_.CommandLine -like "*webwork*" -and
        ($_.CommandLine -like "*spring-boot:run*" -or $_.CommandLine -like "*vite*" -or $_.CommandLine -like "*start-backend.ps1*" -or $_.CommandLine -like "*start-frontend.ps1*")
      }
  } catch {
    Write-Host "Cannot inspect old process command lines. Existing healthy services will be reused." -ForegroundColor Yellow
    return
  }

  foreach ($process in $processes) {
    try {
      Stop-Process -Id $process.ProcessId -Force -ErrorAction Stop
      Write-Host "Stopped PID $($process.ProcessId)"
    } catch {
      Write-Host "Skip PID $($process.ProcessId): $($_.Exception.Message)" -ForegroundColor Yellow
    }
  }
}

function Test-Port($port) {
  return [bool](Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
}

function Test-BackendApi {
  try {
    $response = Invoke-WebRequest -UseBasicParsing -Uri $backendHealthUrl -TimeoutSec 3
    return $response.StatusCode -eq 200 -and $response.Content -like '*"code":0*'
  } catch {
    return $false
  }
}

function Wait-ForPort($port, $name, $timeoutSeconds) {
  Write-Step "Waiting for $name on port $port"
  $deadline = (Get-Date).AddSeconds($timeoutSeconds)
  while ((Get-Date) -lt $deadline) {
    if (Test-Port $port) {
      Write-Host "$name is listening on port $port" -ForegroundColor Green
      return
    }
    Start-Sleep -Seconds 1
  }
  throw "$name did not start on port $port within $timeoutSeconds seconds"
}

function Wait-ForBackendApi($timeoutSeconds) {
  Write-Step "Checking backend API"
  $deadline = (Get-Date).AddSeconds($timeoutSeconds)
  while ((Get-Date) -lt $deadline) {
    if (Test-BackendApi) {
      Write-Host "Backend API is ready" -ForegroundColor Green
      return
    }
    Start-Sleep -Seconds 1
  }
  throw "Backend API is not ready. Check logs\backend.err.log and logs\backend.out.log"
}

function Start-Backend {
  Write-Step "Starting backend"
  $backendScript = Join-Path $root "scripts\start-backend.ps1"
  Start-Process -FilePath "powershell.exe" `
    -ArgumentList @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $backendScript) `
    -RedirectStandardOutput (Join-Path $logDir "backend.out.log") `
    -RedirectStandardError (Join-Path $logDir "backend.err.log") `
    -WindowStyle Hidden
}

function Start-Frontend {
  Write-Step "Starting frontend"
  $frontendDir = Join-Path $root "frontend"
  $npm = Join-Path $nodePath "npm.cmd"
  if (!(Test-Path $npm)) {
    throw "npm.cmd not found at $npm"
  }

  $env:Path = "$nodePath;$frontendDir\node_modules\.bin;" + [Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [Environment]::GetEnvironmentVariable("Path", "User")
  Start-Process -FilePath $npm `
    -ArgumentList @("run", "dev", "--", "--host", "127.0.0.1") `
    -WorkingDirectory $frontendDir `
    -RedirectStandardOutput (Join-Path $logDir "frontend.out.log") `
    -RedirectStandardError (Join-Path $logDir "frontend.err.log") `
    -WindowStyle Hidden
}

Write-Host ""
Write-Host "Starting Xinyu Stack..." -ForegroundColor Magenta
Write-Host "Project: $root"
Write-Host ""

[Environment]::SetEnvironmentVariable("PATH", $null, "Process")

if (Test-BackendApi) {
  Write-Host "Backend is already running. Reusing it." -ForegroundColor Green
} else {
  Stop-ProjectProcesses
  Start-Backend
}

if (Test-Port 5173) {
  Write-Host "Frontend is already running. Reusing it." -ForegroundColor Green
} else {
  Start-Frontend
}

Wait-ForPort 8080 "Backend" 45
Wait-ForBackendApi 45
Wait-ForPort 5173 "Frontend" 30

Write-Step "Opening browser"
Start-Process $frontendUrl

Write-Host ""
Write-Host "Xinyu Stack is ready: $frontendUrl" -ForegroundColor Green
Write-Host "Logs: $logDir"
Write-Host ""
