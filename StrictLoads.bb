; ID: 2975
; Author: RifRaf, further modified by MonocleBios
; Date: 2012-09-11 11:44:22
; Title: Safe Loads (b3d) ;strict loads sounds more appropriate IMO
; Description: Get the missing filename reported

;safe loads for mav trapping media issues

;basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
;more informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
;likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
;added zero checks since blitz load functions return zero sometimes even if the filetype exists
Function LoadImage_Strict(file$)
	If TraditionalChinese Then
		If FileType("Traditional\"+file) = 1 Then Return LoadImage("Traditional\"+file)
	EndIf
	If FileType(file$)<>1 Then RuntimeError "找不到图片：" + file$
	tmp = LoadImage(file$)
	Return tmp
	;attempt to load the image again
	If tmp = 0 Then tmp2 = LoadImage(file)
	DebugLog "Attempting to load again: "+file
	Return tmp2
End Function

Type Sound
	Field internalHandle%
	Field name$
	Field HasSubtitles%
	Field channels%[32]
	Field releaseTime%
End Type

Function AutoReleaseSounds()
	Local snd.Sound
	For snd.Sound = Each Sound
		Local tryRelease% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If ChannelPlaying(snd\channels[i]) Then
					tryRelease = False
					snd\releaseTime = MilliSecs()+5000
					Exit
				EndIf
			EndIf
		Next
		If tryRelease Then
			If snd\releaseTime < MilliSecs() Then
				If snd\internalHandle <> 0 Then
					FreeSound snd\internalHandle
					snd\internalHandle = 0
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		Local shouldPlay% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If Not ChannelPlaying(snd\channels[i]) Then
					If snd\internalHandle = 0 Then
						If FileType(snd\name) <> 1 Then
							CreateConsoleMsg("声音未找到：" + snd\name)
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						Else
							If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
						EndIf
						If snd\internalHandle = 0 Then
							CreateConsoleMsg("声音加载失败：" + snd\name)
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\channels[i] = PlaySound(snd\internalHandle)
					EndIf
					If EnableSubtitles Then
						If snd\HasSubtitles Then ShowSubtitles(snd\Name)
					EndIf
					ChannelVolume snd\channels[i],SFXVolume#
					snd\releaseTime = MilliSecs()+5000 ;release after 5 seconds
					Return snd\channels[i]
				EndIf
			Else
				If snd\internalHandle = 0 Then
					If FileType(snd\name) <> 1 Then
						CreateConsoleMsg("声音未找到：" + snd\name)
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					Else
						If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
					EndIf
						
					If snd\internalHandle = 0 Then
						CreateConsoleMsg("声音加载失败：" + snd\name)
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\channels[i] = PlaySound(snd\internalHandle)
				EndIf
				If EnableSubtitles Then
					If snd\HasSubtitles Then ShowSubtitles(snd\Name)
				EndIf
				ChannelVolume snd\channels[i],SFXVolume#
				snd\releaseTime = MilliSecs()+5000 ;release after 5 seconds
				Return snd\channels[i]
			EndIf
		Next
	EndIf
	
	Return 0
End Function

Function LoadSound_Strict(file$)
	Local snd.Sound = New Sound
	snd\name = file
	snd\internalHandle = 0
	snd\releaseTime = 0
	If EnableSubtitles Then
		; ~ Check if the sound has subtitles
		If GetINISectionLocation(SubtitlesFile, File) <> 0 Then
			snd\HasSubtitles = True
		EndIf
	EndIf
	If (Not EnableSFXRelease) Then
		If snd\internalHandle = 0 Then 
			snd\internalHandle = LoadSound(snd\name)
		EndIf
	EndIf
	
	Return Handle(snd)
End Function

Function FreeSound_Strict(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		If snd\internalHandle <> 0 Then
			FreeSound snd\internalHandle
			snd\internalHandle = 0
		EndIf
		Delete snd
	EndIf
End Function

Type Stream
	Field sfx%
	Field chn%
	Field HasSubtitles%
	Field name%
End Type

Const Mode% = 2
Const TwoD% = 8192

Function StreamSound_Strict(file$,volume#=1.0,custommode=Mode)
	If FileType(file$)<>1
		CreateConsoleMsg("声音未找到：" + file$)
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return 0
	EndIf
	
	Local st.Stream = New Stream
	st\name = file
	If EnableSubtitles Then
		If GetINISectionLocation(SubtitlesFile, File) <> 0 Then
			st\HasSubtitles = True
		EndIf
	EndIf
	
	st\chn = PlayMusic(File, CustomMode + TwoD)
	
	If st\chn = -1
		CreateConsoleMsg("声音流加载失败（返回值-1）：" + file$)
		If ConsoleOpening Then ConsoleOpen = True
		Return -1
	EndIf
	
	If EnableSubtitles Then
		If st\HasSubtitles Then ShowSubtitles(st\Name)
	EndIf
	
	ChannelVolume(st\chn, Volume * 1.0)
	
	Return Handle(st)
End Function

Function StopStream_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null Then Return

	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("声音流停止失败：返回值"+st\chn)
		Return
	EndIf
	
	StopChannel(st\CHN)

	Delete st
End Function

Function SetStreamVolume_Strict(streamHandle%,volume#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null Then Return
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("声音流音量设置失败：返回值"+st\chn)
		Return
	EndIf
	
	ChannelVolume(st\CHN, Volume * 1.0)
End Function

Function SetStreamPaused_Strict(streamHandle%,paused%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null Then Return
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("声音流暂停/恢复失败：返回值"+st\chn)
		Return
	EndIf
	
	If Paused Then
		PauseChannel(st\CHN)
	Else
		ResumeChannel(st\CHN)
	EndIf
End Function

Function IsStreamPlaying_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null Then Return
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("声音流寻找失败：返回值"+st\chn)
		Return
	EndIf
	
	Return(ChannelPlaying(st\CHN))	
End Function

Function SetStreamPan_Strict(streamHandle%,pan#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null Then Return
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("声音流寻找失败：返回值"+st\chn)
		Return
	EndIf
	
	ChannelPan(st\CHN, Pan)
End Function

Function UpdateStreamSoundOrigin(streamHandle%,cam%,entity%,range#=10,volume#=1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			SetStreamVolume_Strict(streamHandle,volume#*(1-dist#)*SFXVolume#)
			SetStreamPan_Strict(streamHandle,panvalue)
		Else
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf
	Else
		If streamHandle <> 0 Then
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf 
	EndIf
End Function

Function LoadMesh_Strict(File$,parent=0)
	If TraditionalChinese Then
		If FileType("Traditional\"+file) = 1 Then Return LoadMesh("Traditional\"+file, flags+(256*(EnableVRam=True)))
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "3D Mesh未找到：" + File$
	tmp = LoadMesh(File$, parent)
	If tmp = 0 Then RuntimeError "3D Mesh加载失败：" + File$ 
	Return tmp  
End Function   

Function LoadAnimMesh_Strict(File$,parent=0)
	DebugLog File
	If TraditionalChinese Then
		If FileType("Traditional\" + file) = 1 Then Return LoadAnimMesh("Traditional\" + file, parent)
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "3D Animated Mesh未找到：" + File$
	tmp = LoadAnimMesh(File$, parent)
	If tmp = 0 Then RuntimeError "3D Animated Mesh加载失败：" + File$ 
	Return tmp
End Function   

;don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$,flags=1)
	If TraditionalChinese Then
		If FileType("Traditional\" + file) = 1 Then Return LoadTexture("Traditional\" + file, flags+(256*(EnableVRam=True)))
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "贴图未找到：" + File$
	tmp = LoadTexture(File$, flags+(256*(EnableVRam=True)))
	If tmp = 0 Then RuntimeError "贴图加载失败：" + File$ 
	Return tmp 
End Function   

Function LoadTexture1(File$,flags=1)
	If TraditionalChinese Then
		If FileType("Traditional\" + file) = 1 Then Return LoadTexture("Traditional\" + file, flags+(256*(EnableVRam=True)))
	EndIf
	tmp = LoadTexture(File$, flags+(256*(EnableVRam=True)))
	Return tmp 
End Function

Function LoadBrush_Strict(file$,flags,u#=1.0,v#=1.0)
	If FileType(file$) <> 1 Then RuntimeError "Brush贴图未找到：" + file$
	tmp = LoadBrush(file$, flags, u, v)
	If tmp = 0 Then RuntimeError "Brush加载失败：" + file$ 
	Return tmp 
End Function 

Function LoadFont_Strict(file$, height=13)
	If TraditionalChinese Then
		If FileType("Traditional\" + file) = 1 Then Return LoadFont("Traditional\" + file, height)
	EndIf
	If FileType(file$) <> 1 Then RuntimeError "字体未找到：" + file$
	tmp = LoadFont(file, height)  
	If tmp = 0 Then RuntimeError "字体加载失败：" + file$ 
	Return tmp
End Function