@echo off
cd ..
"windows\play\play.bat" deps
"windows\play\play.bat" test
start http://localhost:8080