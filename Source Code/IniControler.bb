; IniControler - BlitzToolBox的一部分
; Blitz3D的ini文件读写工具。收容失效汉化计划的定制unordered map版本。
; v1.06 2022.11.12
; v1.06.2 2023.9.10
; https://github.com/ZiYueCommentary/BlitzToolbox/tree/ziyue

Function IniWriteBuffer(path$, clearPrevious% = 1)
	IniWriteBuffer_(path$, clearPrevious%)
End Function

Function IniWriteString(path$, section$, key$, value$, updateBuffer% = 1)
	IniWriteString_(path$, section$, key$, value$, updateBuffer%)
End Function

Function IniWriteInt(path$, section$, key$, value%, updateBuffer% = 1)
	IniWriteInt_(path$, section$, key$, value%, updateBuffer%)
End Function

Function IniWriteFloat(path$, section$, key$, value#, updateBuffer% = 1)
	IniWriteFloat_(path$, section$, key$, value#, updateBuffer%)
End Function

Function IniGetString$(path$, section$, key$, defaultValue$ = "", allowBuffer% = 1)
	Return IniGetBufferString_(path$, section$, key$, defaultValue$)
End Function

Function IniGetInt%(path$, section$, key$, defaultValue% = 0, allowBuffer% = 1)
	Local Value$ = IniGetString_(path$, section$, key$, defaultValue%, allowBuffer%)
	Select Value
		Case "True", "true", "1"
			Return True
		Case "False", "false", "0"
			Return False
		Default
			Return Int(Value)
	End Select
End Function

Function IniGetFloat#(path$, section$, key$, defaultValue# = 0.0, allowBuffer% = 1)
	Return IniGetFloat_(path$, section$, key$, defaultValue#, allowBuffer%)
End Function

Function IniGetBufferString$(path$, section$, key$, defaultValue$ = "")
	Return IniGetBufferString_(path$, section$, key$, defaultValue$)
End Function

Function IniGetBufferInt%(path$, section$, key$, defaultValue% = 0)
	Return IniGetBufferInt_(path$, section$, key$, defaultValue%)
End Function

Function IniGetBufferFloat#(path$, section$, key$, defaultValue# = 0.0)
	Return IniGetBufferFloat_(path$, section$, key$, defaultValue#)
End Function

Function IniSectionExist%(path$, section$, allowBuffer% = 1)
	Return IniSectionExist_(path$, section$, allowBuffer%)
End Function

Function IniKeyExist%(path$, section$, key$, allowBuffer% = 1)
	Return IniKeyExist_%(path$, section$, key$, allowBuffer%)
End Function

Function IniRemoveKey(path$, section$, key$, updateBuffer% = 1)
	IniRemoveKey_(path$, section$, key$, updateBuffer%)
End Function

Function IniRemoveSection(path$, section$, updateBuffer% = 1)
	IniRemoveSection_(path$, section$, updateBuffer%)
End Function

Function FindSCP294Drink$(Drink$)
	Local StrTemp$ = FindSCP294Drink_("Data\SCP-294.ini", Drink)
	If StrTemp = "Null" Then Return(StrTemp)
	Return Left(StrTemp, Instr(StrTemp, ",") - 1)
End Function
