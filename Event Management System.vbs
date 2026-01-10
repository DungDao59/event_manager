Set objShell = CreateObject("WScript.Shell")
Set objFSO = CreateObject("Scripting.FileSystemObject")

' Get the directory where this script is located
scriptDir = objFSO.GetParentFolderName(WScript.ScriptFullName)

' Change to the script directory
objShell.CurrentDirectory = scriptDir

' Build the java command
javaCmd = "java --module-path ""target\lib"" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar ""assignment2-1.0-SNAPSHOT.jar"""

' Run the command (0 = hide window, True = wait for completion)
objShell.Run javaCmd, 1, False

Set objShell = Nothing
Set objFSO = Nothing
