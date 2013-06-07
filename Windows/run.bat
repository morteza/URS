@echo off
cd ..
call "windows\play\play.bat" deps
call "windows\play\play.bat" run
start http://localhost:8080