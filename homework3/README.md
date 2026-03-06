cd C:\Users\mrrom\IdeaProjects\homework1\homework3

New-Item -ItemType Directory -Force .\logs | Out-Null
@'
93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 200 100 "-" "UA"
93.180.71.3 - - [17/May/2015:08:05:33 +0000] "GET /downloads/product_2 HTTP/1.1" 404 300 "-" "UA"
broken line
'@ | Set-Content -Encoding utf8 .\logs\access.log

Remove-Item .\report.json,.\report.md -ErrorAction SilentlyContinue

& "C:\Program Files\JetBrains\PyCharm Community Edition 2023.2.1\jbr\bin\java.exe" -jar .\target\log-analyzer-1.0.0.jar --path .\logs\*.log --format json --output .\report.json
Get-Content .\report.json

& "C:\Program Files\JetBrains\PyCharm Community Edition 2023.2.1\jbr\bin\java.exe" -jar .\target\log-analyzer-1.0.0.jar --path .\logs\*.log --format markdown --output .\report.md
Get-Content .\report.md

& "C:\Program Files\JetBrains\PyCharm Community Edition 2023.2.1\jbr\bin\java.exe" -jar .\target\log-analyzer-1.0.0.jar --path .\logs\access.log --format json --output .\bad.md; $LASTEXITCODE
