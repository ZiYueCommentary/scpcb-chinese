; 我才发现居然还有个这玩意
; 收容失效的源码里虽然有这个文件，但没有BlitzAL插件
; 所以到底有多少东西胎死腹中了？
; ——子悦 2022年5月15日

Include "BlitzAL.bb"

AppTitle "收容失效 音乐播放器"

Graphics 800,600,0,2
SetBuffer BackBuffer()

DisableClose() ;禁用标题栏的关闭按钮
alInitialise()

Global CurrMusicWindow%=0
Global MusicVolume# = 1.0
Global MusicCHN = 0
Global CurrMusic = -1
Global MusicPlaying = -1
Dim Music$(256)
Global MusicAmount%=0
Global MusicAmount2%=0
Global IsMusicPlaying%=0

Local dirPath$ = "SFX\Radio\UserTracks\"
If FileType(dirPath)<>2 Then
	CreateDir(dirPath)
EndIf

Const MusicPath$ = "SFX\Music\"
Const MusicPath2$ = "SFX\Radio\UserTracks\"

Local Dir% = ReadDir(MusicPath)
Repeat
	Local file$=NextFile(Dir)
	If file$="" Then Exit
	If FileType(MusicPath+file$) = 1 Then
		Music(MusicAmount) = file$
		MusicAmount = MusicAmount + 1
	EndIf
Forever
CloseDir Dir

Dir% = ReadDir(MusicPath2)
Repeat
	file$=NextFile(Dir)
	If file$="" Then Exit
	If FileType(MusicPath2+file$) = 1 Then
		Music(MusicAmount2+MusicAmount) = file$
		MusicAmount2 = MusicAmount2 + 1
	EndIf
Forever
CloseDir Dir

Local i

Const ClrR = 50, ClrG = 50, ClrB = 50

Global MouseHit1,MouseDown1

Global OnSliderID=0

Global x#,y#,width#,height#

Global PrevBarTime# = 0.0, CurrBarTime# = 0.0
Global CurrPitch# = 1.0

SetFont LoadFont("GFX\font\Containment Breach.ttf", 14)

Repeat
	Cls
	ClsColor 200,200,200
	
	MouseHit1 = MouseHit(1)
	MouseDown1 = MouseDown(1)
	alUpdate()
	
	If (Not MouseDown1)
		OnSliderID = 0
	EndIf
	
	x = 0
	y = 150
	width = 200
	height = 25
	If CurrMusicWindow=0
		For i = 0 To MusicAmount-1
			If Right(Music(i),4)=".ogg"
				If Button(x,y,width,height,ShortLine(Music(i),25),(i=CurrMusic))
					CurrMusic = i
					DebugLog "Playing Music: "+Music(i)
					CurrBarTime# = 0.0
					PrevBarTime# = 0.0
					AppTitle "收容失效 音乐播放器 - 正在播放："+Chr(34)+Music(i)+Chr(34)
				EndIf
			Else
				Button(x,y,width,height,ShortLine(Music(i),25),True)
				Color 210,50,55
				Rect x+1,y+1,width-2,height-2,False
				Text (x+width/2),(y+height/2-1),ShortLine(Music(i),25),True,True
			EndIf
			If i=CurrMusic
				Color 50,210,55
				Rect x+1,y+1,width-2,height-2,False
				Text (x+width/2),(y+height/2-1),ShortLine(Music(i),25),True,True
			EndIf
			y=y+height
			If y > 600
				y=150
				x=x+width
			EndIf
		Next
	Else
		For i = 0 To MusicAmount2-1
			If Right(Music(i+MusicAmount),4)=".ogg" Or Right(Music(i+MusicAmount),4)=".wav"
				If Button(x,y,width,height,ShortLine(Music(i+MusicAmount),25),(i+MusicAmount=CurrMusic))
					CurrMusic = i+MusicAmount
					DebugLog "Playing Music: "+Music(i+MusicAmount)
					CurrBarTime# = 0.0
					PrevBarTime# = 0.0
					AppTitle "收容失效 音乐播放器 - 正在播放："+Chr(34)+Music(i+MusicAmount)+Chr(34)
				EndIf
			Else
				Button(x,y,width,height,ShortLine(Music(i+MusicAmount),25),True)
				Color 210,50,55
				Rect x+1,y+1,width-2,height-2,False
				Text (x+width/2),(y+height/2-1),ShortLine(Music(i+MusicAmount),25),True,True
			EndIf
			If i+MusicAmount=CurrMusic
				Color 50,210,55
				Rect x+1,y+1,width-2,height-2,False
				Text (x+width/2),(y+height/2-1),ShortLine(Music(i+MusicAmount),25),True,True
			EndIf
			y=y+height
			If y > 600
				y=150
				x=x+width
			EndIf
		Next
	EndIf
	x = 0
	y = 150
	width = 255
	height = 25
	Color 40,80,50
	Rect 0,0,800,y-20,True
	Color 40,50,80
	Rect 0,y-20,800,20,True
	If CurrMusicWindow = 0
		If Button(750,y-20,50,20,"下一页")
			CurrMusicWindow = 1
		EndIf
		Button(0,y-20,50,20,"上一页",True)
		Color 140,140,140
		Text 800-13,90,"兼容的音频类型： OGG",2,1
	Else
		If Button(0,y-20,50,20,"上一页")
			CurrMusicWindow = 0
		EndIf
		Button(750,y-20,50,20,"下一页",True)
		Color 140,140,140
		Text 800-13,90,"兼容的音频类型： OGG,WAV",2,1
	EndIf
	Color 130,140,150
	If CurrMusicWindow=0
		Text GraphicsWidth()/2,y-11,"音乐列表 （“"+MusicPath+"”文件夹）：",1,1
	Else
		Text GraphicsWidth()/2,y-11,"音乐列表 （“"+MusicPath2+"”文件夹）：",1,1
	EndIf
	If IsMusicPlaying=1
		Color 100,100,100
		If alSourceIsPlaying(MusicCHN)
			PrevBarTime# = (alSourceGetAudioTime(MusicCHN,0)-1)/(alSourceGetLenght(MusicCHN,0)-1)
			CurrBarTime# = CurveValue(PrevBarTime#,CurrBarTime#,400.0/alSourceGetLenght(MusicCHN,0))
		EndIf
		Rect 10,10,780*CurrBarTime#,20,1
		Color 140,140,140
		Text GraphicsWidth()/2,40,"正在播放： “"+ShortLine$(Music(MusicPlaying),38)+"”",1,1
		Text GraphicsWidth()/2,60,"音乐时长： "+GetTime(Int(alSourceGetAudioTime(MusicCHN,0)-1))+"/"+GetTime(Int(alSourceGetLenght(MusicCHN,0)-1)),1,1
		If alSourceIsPlaying(MusicCHN)
			If Button(60,40,50,25,"暂停")
				alSourcePause(MusicCHN)
			EndIf
		Else
			If Button(60,40,50,25,"播放")
				alSourceResume(MusicCHN)
			EndIf
		EndIf
		If Button(110,40,60,25,"重播")
			alSourceStop(MusicCHN)
			alSourceSeek(MusicCHN,0,0)
			alSourcePlay_(MusicCHN,True)
			CurrBarTime# = 0.0
			PrevBarTime# = 0.0
		EndIf
		If alSourceGetAudioTime(MusicCHN,0)<1
			CurrBarTime# = 0.0
			PrevBarTime# = 0.0
		EndIf
	Else
		Color 140,140,140
		Text GraphicsWidth()/2,40,"正在播放： 无",1,1
		Text GraphicsWidth()/2,60,"音乐时长： 00:00/00:00",1,1
		Button(60,40,50,25,"暂停",True)
		Button(110,40,60,25,"重启",True)
	EndIf
	If Button(10,40,50,25,"停止",(Not IsMusicPlaying))
		CurrMusic = -1
		IsMusicPlaying = 0
		alSourceStop(MusicCHN)
		alFreeSource(MusicCHN)
		MusicPlaying = -1
		CurrBarTime# = 0.0
		PrevBarTime# = 0.0
		AppTitle "收容失效 音乐播放器"
	EndIf
	Color 5,5,5
	Rect 10,10,780,20,0
	MusicVolume = (SlideBar(30, 80, 100, MusicVolume*100.0)/100.0)
	Color 140,140,140
	Text 90,110,"音乐音量： "+Int(MusicVolume*100)+"%",1,1
	CurrPitch = (SlideBar(220, 80, 100, CurrPitch*50.0,0,200)/50.0)
	Color 140,140,140
	Text 280,110,"音乐音阶： "+Int(CurrPitch*100)+"%",1,1
	If Button(675,40,115,25,"关闭程序")
		alDestroy()
		End
	EndIf
	
	UpdateMusic()
	Flip 0
Until KeyHit(1)
alDestroy()
End

Function Button%(x,y,width,height,txt$, disabled%=False)
	Local Pushed = False
	
	Color ClrR, ClrG, ClrB
	If Not disabled Then 
		If MouseX() > x And MouseX() < (x+width) Then
			If MouseY() > y And MouseY() < (y+height) Then
				If MouseDown1 Then
					Pushed = True
					Color ClrR*0.6, ClrG*0.6, ClrB*0.6
				Else
					Color Min(ClrR*1.2,255),Min(ClrR*1.2,255),Min(ClrR*1.2,255)
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect x,y,width,height
		Color 133,130,125
		Rect (x+1),(y+1),(width-1),(height-1),False	
		Color 10,10,10
		Rect x,y,width,height,False
		Color 250,250,250
		Line x,(y+height-1),(x+width-1),(y+height-1)
		Line (x+width-1),y,(x+width-1),(y+height-1)
	Else
		Rect x,y,width,height
		Color 133,130,125
		Rect x,y,(width-1),(height-1),False	
		Color 250,250,250
		Rect x,y,width,height,False
		Color 10,10,10
		Line x,(y+height-1),(x+width-1),(y+height-1)
		Line (x+width-1),y,(x+width-1),(y+height-1)		
	EndIf
	
	Color 255,255,255
	If disabled Then Color 70,70,70
	Text (x+width/2),(y+height/2-1), txt, True, True
	
	Color 0,0,0
	
	If Pushed And MouseHit1 Then Return True
End Function

Function f2s$(n#, count%)
	Return Left(n, Len(Int(n))+count+1)
End Function

Function CurveValue#(number#, old#, smooth#)
	If number < old Then
		Return Max(old + (number - old) * (1.0 / smooth), number)
	Else
		Return Min(old + (number - old) * (1.0 / smooth), number)
	EndIf
End Function

Function SlideBar#(x%, y%, width%, value#, min_p%=0, max_p%=100)
	If MouseDown1 And OnSliderID=0
		If MouseX() >= x And MouseX() <= x + width + 14 And MouseY() >= y And MouseY() <= y + 20 Then
			value = Min(Max((MouseX() - x) * 100 / width, 0), 100)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	Color 200,200,240
	Rect x+width*value/100.0+3,y+3,8,14,True
	
	Color 140,140,140 
	Text (x - 23, y + 4, min_p+"%")				
	Text (x + width + 20, y+4, max_p+"%")	
	
	Return value
End Function

Function GetTime$(numb%)
	Local secs%,mins%,secstring$,minstring$
	
	If numb<60
		If Len(Int(Max(numb,0)))<2
			secstring$ = "0"+Int(Max(numb,0))
		Else
			secstring$ = Int(Max(numb,0))
		EndIf
		Return "00:"+secstring$
	Else
		mins% = Int(numb%/60)
		secs = numb-(mins%*60)
		If Len(Int(Max(mins%,0)))<2
			minstring$ = "0"+Int(Max(mins%,0))
		Else
			minstring$ = Int(Max(mins%,0))
		EndIf
		If Len(Int(Max(secs%,0)))<2
			secstring$ = "0"+Int(Max(secs%,0))
		Else
			secstring$ = Int(Max(secs%,0))
		EndIf
		Return minstring$+":"+secstring$
	EndIf
	
End Function

Function ShortLine$(txt$,amount%)
	
	If Len(txt$)>(amount%)
		Return Left(txt$,(amount%-3))+"..."
	EndIf
	
	Return txt$
	
End Function

Function UpdateMusic()
	
	If CurrMusic > -1
		If CurrMusic <> MusicPlaying Then
			If IsMusicPlaying=1
				alSourceStop(MusicCHN)
				alFreeSource(MusicCHN)
			EndIf
			MusicPlaying = CurrMusic
			MusicCHN = 0
			IsMusicPlaying=0
		EndIf
		
		If IsMusicPlaying=0
			If MusicPlaying < MusicAmount
				MusicCHN% = alCreateSource_(MusicPath+Music(MusicPlaying),True,False)
			Else
				MusicCHN% = alCreateSource_(MusicPath2+Music(MusicPlaying),True,False)
			EndIf
			alSourcePlay2D_(MusicCHN,True)
			alSourceSetLoop(MusicCHN,True)
			IsMusicPlaying=1
		EndIf
		If alSourceIsPlaying(MusicCHN)
			alSourceSetVolume(MusicCHN, MusicVolume)
			alSourceSetPitch(MusicCHN, CurrPitch)
		EndIf
	EndIf
End Function

Function Text(x%, y%, txt$, center% = 0, middle% = 0, encoding% = 0)
	Blitz_Text(x, y+1, txt, center, middle, encoding)
End Function