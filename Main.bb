;SCP - 收容失效 汉化计划

;    游戏基于SCP基金会背景(http://scp-wiki-cn.wikidot.com/)

;	 汉化计划基于SCP - Containment Breach TSS制作（https://github.com/ZiYueCommentary/scpcb-tss）

;	 游戏遵循知识共享许可协议BY-SA 3.0（即必须署名、可商用、可二创、可二创商用）
;	 https://creativecommons.org/licenses/by-sa/3.0/deed.zh

;    打开"Credits.txt"文件查看详细制作人员名单

Local InitErrorStr$ = ""
If FileSize("dplayx.dll")=0 Then InitErrorStr=InitErrorStr+ "dplayx.dll"+Chr(13)+Chr(10)
If FileSize("FreeImage.dll")=0 Then InitErrorStr=InitErrorStr+ "FreeImage.dll"+Chr(13)+Chr(10)
If FileSize("fmod.dll")=0 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
If FileSize("opencc.dll")=0 Then InitErrorStr=InitErrorStr+ "opencc.dll"+Chr(13)+Chr(10)

If Len(InitErrorStr)>0 Then
	RuntimeError "游戏文件夹中缺失以下dll文件："+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr
EndIf

Include "StrictLoads.bb"
Include "KeyName.bb"

Global OptionFile$ = "options.ini"
Include "Update.bb"

Include "DevilParticleSystem.bb"

Global UpdaterFont%
Global Font1%, Font2%, Font3%, Font4%, Font5%
Global ConsoleFont%

Global VersionNumber$ = "1.3.11"
Global SinicizationNumber$ = "2023.10-Fifth Revise" ; 汉化版本
Global CompatibleNumber$ = "1.3.11-2022.5" ; 当存档与构建版本不兼容时再更改		——开发者 ENDSHN

Global OpenCC% = CreateOpenCC("Traditional\OpenCC\s2twp.json") ; 繁简转换工具

Global MenuWhite%, MenuBlack%
Global ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")

Global EnableSFXRelease% = GetINIInt(OptionFile, "audio", "sfx release")
Global EnableSFXRelease_Prev% = EnableSFXRelease%

Global CanOpenConsole% = GetINIInt(OptionFile, "console", "enabled")

Dim ArrowIMG(4)

;[Block]

Global LauncherWidth%= Min(GetINIInt(OptionFile, "launcher", "launcher width"), 1024)
Global LauncherHeight% = Min(GetINIInt(OptionFile, "launcher", "launcher height"), 768)
Global LauncherEnabled% = GetINIInt(OptionFile, "launcher", "launcher enabled")

Global GraphicWidth% = GetINIInt(OptionFile, "options", "width")
Global GraphicHeight% = GetINIInt(OptionFile, "options", "height")
Global Depth% = 0, Fullscreen% = GetINIInt(OptionFile, "options", "fullscreen")

Global SelectedGFXMode%
Global SelectedGFXDriver% = Max(GetINIInt(OptionFile, "options", "gfx driver"), 1)

Global fresize_image%, fresize_texture%, fresize_texture2%
Global fresize_cam%

Global ShowFPS = GetINIInt(OptionFile, "options", "show FPS")

Global WireframeState
Global HalloweenTex

Global TotalGFXModes% = CountGfxModes3D(), GFXModes%
Dim GfxModeWidths%(TotalGFXModes), GfxModeHeights%(TotalGFXModes)

Global BorderlessWindowed% = GetINIInt(OptionFile, "options", "borderless windowed")
Global RealGraphicWidth%,RealGraphicHeight%
Global AspectRatioRatio#

Global EnableRoomLights% = GetINIInt(OptionFile, "options", "room lights enabled")

Global TextureDetails% = GetINIInt(OptionFile, "options", "texture details")
Global TextureFloat#
Select TextureDetails%
	Case 0
		TextureFloat# = 0.8
	Case 1
		TextureFloat# = 0.4
	Case 2
		TextureFloat# = 0.0
	Case 3
		TextureFloat# = -0.4
	Case 4
		TextureFloat# = -0.8
End Select
Global ConsoleOpening% = GetINIInt(OptionFile, "console", "auto opening")
Global SFXVolume# = GetINIFloat(OptionFile, "audio", "sound volume")

Global Bit16Mode = GetINIInt(OptionFile, "options", "16bit")

Global TraditionalChinese% = GetINIInt(OptionFile, "options", "traditional chinese")

Include "Subtitles.bb"

If LauncherEnabled Then 
	AspectRatioRatio = 1.0
	UpdateLauncher()
	
	;New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark,
	If BorderlessWindowed
		DebugLog "Using Borderless Windowed Mode"
		Graphics3DExt(DesktopWidth(), DesktopHeight(), 0, 4)
		
		; -- Change the window style to 'WS_POPUP' and then set the window position to force the style to update.
		
		RealGraphicWidth = DesktopWidth()
		RealGraphicHeight = DesktopHeight()
		
		AspectRatioRatio = (Float(GraphicWidth)/Float(GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
		
		Fullscreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If Fullscreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16*Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
Else
	For i% = 1 To TotalGFXModes
		Local samefound% = False
		For  n% = 0 To TotalGFXModes - 1
			If GfxModeWidths(n) = GfxModeWidth(i) And GfxModeHeights(n) = GfxModeHeight(i) Then samefound = True : Exit
		Next
		If samefound = False Then
			If GraphicWidth = GfxModeWidth(i) And GraphicHeight = GfxModeHeight(i) Then SelectedGFXMode = GFXModes
			GfxModeWidths(GFXModes) = GfxModeWidth(i)
			GfxModeHeights(GFXModes) = GfxModeHeight(i)
			GFXModes=GFXModes+1
		End If
	Next
	
	GraphicWidth = GfxModeWidths(SelectedGFXMode)
	GraphicHeight = GfxModeHeights(SelectedGFXMode)
	
	;New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark,
	If BorderlessWindowed
		DebugLog "Using Faked Fullscreen"
		Graphics3DExt(DesktopWidth(), DesktopHeight(), 0, 4)
		
		; -- Change the window style to 'WS_POPUP' and then set the window position to force the style to update.		
		RealGraphicWidth = DesktopWidth()
		RealGraphicHeight = DesktopHeight()
		
		AspectRatioRatio = (Float(GraphicWidth)/Float(GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
		
		Fullscreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If Fullscreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16*Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
EndIf

Global MenuScale# = (GraphicHeight / 1024.0)

SetBuffer(BackBuffer())

Global CurTime%, PrevTime%, LoopDelay%, FPSfactor#, FPSfactor2#, PrevFPSFactor#
Local CheckFPS%, ElapsedLoops%, FPS%, ElapsedTime#

Global Framelimit% = GetINIInt(OptionFile, "options", "framelimit")
Global Vsync% = GetINIInt(OptionFile, "options", "vsync")

Global Opt_AntiAlias = GetINIInt(OptionFile, "options", "antialias")

Global CurrFrameLimit# = (Framelimit%-19)/100.0

Global ScreenGamma# = GetINIFloat(OptionFile, "options", "screengamma")

Const HIT_MAP% = 1, HIT_PLAYER% = 2, HIT_ITEM% = 3, HIT_APACHE% = 4, HIT_178% = 5, HIT_DEAD% = 6
SeedRnd MilliSecs()

;[End block]

Global GameSaved%

Global CanSave% = True

AppTitle "SCP - 收容失效 汉化计划 v" + VersionNumber

PlayStartupVideos()

;---------------------------------------------------------------------------------------------------------------------

;[Block]

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict("Loadingscreens\loadingback.jpg")
InitLoadingScreens("Loadingscreens\loadingscreens.ini")

; 由于玩家可以用输入法，所以输入的文本可能啥都有
; 为了让文字尽可能都显示，游戏最经常用的字体就没砍
; ——子悦 2022/12/22
Font1% = LoadFont_Strict("GFX\font\Containment Breach.ttf", Int(16 * (GraphicHeight / 1024.0)))
Font2% = LoadFont_Strict("GFX\font\Containment Breach.ttf", Int(55 * (GraphicHeight / 1024.0)))
Font3% = LoadFont_Strict("GFX\font\Unifont.ttf", Int(19 * (GraphicHeight / 1024.0)))
Font4% = LoadFont_Strict("GFX\font\Unifont.ttf", Int(57 * (GraphicHeight / 1024.0)))
Font5% = LoadFont_Strict("GFX\font\Journal.ttf", Int(55 * (GraphicHeight / 1024.0)))

Global CreditsFont%, CreditsFont2%, CreditsFont3%

ConsoleFont% = LoadFont_Strict("GFX\font\Containment Breach.ttf", Int(17 * (GraphicHeight / 1024.0)))

SetFont Font2

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")

DrawLoading(0, True)

; - -Viewport.
Global viewport_center_x% = GraphicWidth / 2, viewport_center_y% = GraphicHeight / 2

; -- Mouselook.
Global mouselook_x_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_left_limit% = 250, mouse_right_limit% = GraphicsWidth () - 250
Global mouse_top_limit% = 150, mouse_bottom_limit% = GraphicsHeight () - 150 ; As above.
Global mouse_x_speed_1#, mouse_y_speed_1#

Global KEY_RIGHT = GetINIInt(OptionFile, "binds", "Right key")
Global KEY_LEFT = GetINIInt(OptionFile, "binds", "Left key")
Global KEY_UP = GetINIInt(OptionFile, "binds", "Up key")
Global KEY_DOWN = GetINIInt(OptionFile, "binds", "Down key")

Global KEY_BLINK = GetINIInt(OptionFile, "binds", "Blink key")
Global KEY_SPRINT = GetINIInt(OptionFile, "binds", "Sprint key")
Global KEY_INV = GetINIInt(OptionFile, "binds", "Inventory key")
Global KEY_CROUCH = GetINIInt(OptionFile, "binds", "Crouch key")
Global KEY_SAVE = GetINIInt(OptionFile, "binds", "Save key")
Global KEY_CONSOLE = GetINIInt(OptionFile, "binds", "Console key")

Global MouseSmooth# = GetINIFloat(OptionFile,"options", "mouse smoothing", 1.0)

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

;player stats -------------------------------------------------------------------------------------------------------
Global KillTimer#, KillAnim%, FallTimer#, DeathTimer#
Global Sanity#, ForceMove#, ForceAngle#
Global RestoreSanity%

Global Playable% = True

Global BLINKFREQ#
Global BlinkTimer#, EyeIrritation#, EyeStuck#, BlinkEffect# = 1.0, BlinkEffectTimer#

Global Stamina#, StaminaEffect#=1.0, StaminaEffectTimer#

Global CameraShakeTimer#, Vomit%, VomitTimer#, Regurgitate%

Global SCP1025state#[6]

Global HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#

Global WearingGasMask%, WearingHazmat%, WearingVest%, Wearing714%, WearingNightVision%
Global NVTimer#

Global SuperMan%, SuperManTimer#

Global Injuries#, Bloodloss#, Infect#, HealTimer#

Global RefinedItems%

Include "Achievements.bb"

;player coordinates, angle, speed, movement etc ---------------------------------------------------------------------
Global DropSpeed#, HeadDropSpeed#, CurrSpeed#
Global user_camera_pitch#, side#
Global Crouch%, CrouchState#

Global PlayerZone%, PlayerRoom.Rooms

Global GrabbedEntity%

Global InvertMouse% = GetINIInt(OptionFile, "options", "invert mouse y")
Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%

Global GodMode%, NoClip%, NoClipSpeed# = 2.0

Global CoffinDistance# = 100.0

Global PlayerSoundVolume#

;camera/lighting effects (blur, camera shake, etc)-------------------------------------------------------------------
Global Shake#

Global ExplosionTimer#, ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

;menus, GUI ---------------------------------------------------------------------------------------------------------
Global MainMenuOpen%, MenuOpen%, StopHidingTimer#, InvOpen%
Global OtherOpen.Items = Null

Global SelectedEnding$, EndingScreen%, EndingTimer#

Global MsgTimer#, Msg$, DeathMSG$

Global AccessCode%, KeypadInput$, KeypadTimer#, KeypadMSG$

Global DrawHandIcon%
Dim DrawArrowIcon%(4)

;misc ---------------------------------------------------------------------------------------------------------------

Include "Difficulty.bb"

Global MTFtimer#, MTFrooms.Rooms[10], MTFroomState%[10]

Dim RadioState#(10)
Dim RadioState3%(10)
Dim RadioState4%(9)
Dim RadioCHN%(8)

Dim OldAiPics%(5)

Global PlayTime%
Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global InfiniteStamina% = False
Global NVBlink%
Global IsNVGBlinking% = False

;[End block]


;----------------------------------------------  Console -----------------------------------------------------

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#,ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR% = 255,ConsoleG% = 255,ConsoleB% = 255

Type ConsoleMsg
	Field txt$
	Field isCommand%
	Field r%,g%,b%
End Type

Function CreateConsoleMsg(txt$,r%=-1,g%=-1,b%=-1,isCommand%=False)
	Local c.ConsoleMsg = New ConsoleMsg
	Insert c Before First ConsoleMsg
	
	c\txt = txt
	c\isCommand = isCommand
	
	c\r = r
	c\g = g
	c\b = b
	
	If (c\r<0) Then c\r = ConsoleR
	If (c\g<0) Then c\g = ConsoleG
	If (c\b<0) Then c\b = ConsoleB
End Function

Function UpdateConsole()
	Local e.Events
	
	If CanOpenConsole = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		SetFont ConsoleFont
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		Local x% = 0, y% = GraphicHeight-300*MenuScale, width% = GraphicWidth, height% = 300*MenuScale-30*MenuScale
		Local StrTemp$, temp%,  i%
		Local ev.Events, r.Rooms, it.Items
		
		DrawFrame x,y,width,height+30*MenuScale
		
		Local consoleHeight% = 0
		Local scrollbarHeight% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			consoleHeight = consoleHeight + 15*MenuScale
		Next
		scrollbarHeight = (Float(height)/Float(consoleHeight))*height
		If scrollbarHeight>height Then scrollbarHeight = height
		If consoleHeight<height Then consoleHeight = height
		
		Color 50,50,50
		inBar% = MouseOn(x+width-26*MenuScale,y,26*MenuScale,height)
		If inBar Then Color 70,70,70
		Rect x+width-26*MenuScale,y,26*MenuScale,height,True
		
		Color 120,120,120
		inBox% = MouseOn(x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight)
		If inBox Then Color 200,200,200
		If ConsoleScrollDragging Then Color 255,255,255
		Rect x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight,True
		
		If Not MouseDown(1) Then
			ConsoleScrollDragging=False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll+((ScaledMouseY()-ConsoleMouseMem)*height/scrollbarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If MouseHit1 Then
				If inBox Then
					ConsoleScrollDragging=True
					ConsoleMouseMem = ScaledMouseY()
				ElseIf inBar Then
					ConsoleScroll = ConsoleScroll+((ScaledMouseY()-(y+height))*consoleHeight/height+(height/2))
					ConsoleScroll = ConsoleScroll/2
				EndIf
			EndIf
		EndIf
		
		mouseScroll = MouseZSpeed()
		If mouseScroll=1 Then
			ConsoleScroll = ConsoleScroll - 15*MenuScale
		ElseIf mouseScroll=-1 Then
			ConsoleScroll = ConsoleScroll + 15*MenuScale
		EndIf
		
		Local reissuePos%
		If KeyHit(200) Then
			reissuePos% = 0
			If (ConsoleReissue=Null) Then
				ConsoleReissue=First ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos-15*MenuScale
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				reissuePos = reissuePos-15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=First ConsoleMsg
						reissuePos = 0
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			reissuePos% = -consoleHeight+15*MenuScale
			If (ConsoleReissue=Null) Then
				ConsoleReissue=Last ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos+15*MenuScale
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				reissuePos = reissuePos+15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=Last ConsoleMsg
						reissuePos=-consoleHeight+15*MenuScale
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
			EndIf
		EndIf
		
		If ConsoleScroll<-consoleHeight+height Then ConsoleScroll = -consoleHeight+height
		If ConsoleScroll>0 Then ConsoleScroll = 0
		
		Color 255, 255, 255
		
		SelectedInputBox = 2
		Local oldConsoleInput$ = ConsoleInput
		ConsoleInput = InputBox(x, y + height, width, 30*MenuScale, ConsoleInput, 2)
		If oldConsoleInput<>ConsoleInput Then
			ConsoleReissue = Null
		EndIf
		ConsoleInput = Left(ConsoleInput, 100)
		
		If (KeyHit(28) Or KeyHit(156)) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput,255,255,0,True)
			If Instr(ConsoleInput, " ") > 0 Then
				StrTemp$ = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp$ = Lower(ConsoleInput)
			End If
			
			Select Lower(StrTemp)
				Case "help"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
					
					Select Lower(StrTemp)
						Case "1",""
							CreateConsoleMsg("命令列表 - 1/3页")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- asd")
							CreateConsoleMsg("- status")
							CreateConsoleMsg("- camerapick")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- injure [值]")
							CreateConsoleMsg("- infect [值]")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- teleport [房间ID")
							CreateConsoleMsg("- spawnitem [物品ID]")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- 173speed")
							CreateConsoleMsg("- 106speed")
							CreateConsoleMsg("- 173state")
							CreateConsoleMsg("- 106state")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("使用“help 2”或“help 3”以查看更多命令")
							CreateConsoleMsg("使用“help [命令名]”以获得关于命令的详细信息")
							CreateConsoleMsg("******************************")
						Case "2"
							CreateConsoleMsg("命令列表 - 2/3页")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- spawn [NPC类型] [状态]")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- scp-420-j")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- showfps")
							CreateConsoleMsg("- 096state")
							CreateConsoleMsg("- debughud")
							CreateConsoleMsg("- camerafog [可视距离] [渲染距离]")
							CreateConsoleMsg("- gamma [值]")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("使用“help [命令名]”以获得关于命令的详细信息")
							CreateConsoleMsg("******************************")
						Case "3"
							CreateConsoleMsg("- playmusic [文件名 + .wav/.ogg]")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- unlockexits")
						Case "asd"
							CreateConsoleMsg("帮助 - asd")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("启用无敌模式,无碰撞,渲染线框，")
							CreateConsoleMsg("并且设置可视距离为20,渲染距离为30。")
							CreateConsoleMsg("******************************")
						Case "camerafog"
							CreateConsoleMsg("帮助 - camerafog")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("设置雾的绘制距离，迷雾在“可视距离”开始渲染，")
							CreateConsoleMsg("直到“渲染距离”处变得完全不透明。")
							CreateConsoleMsg("示例：camerafog 20 40")
							CreateConsoleMsg("******************************")
						Case "gamma"
							CreateConsoleMsg("帮助 - gamma")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("设置游戏伽马值。")
							CreateConsoleMsg("建议将伽马值设为0.0到2.0之内的值。")
							CreateConsoleMsg("默认为1.0。")
							CreateConsoleMsg("******************************")
						Case "noclip","fly"
							CreateConsoleMsg("帮助 - noclip")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("切换无碰撞，除非指定是否启用（on/off）。")
							CreateConsoleMsg("允许视角无视任何碰撞移动。")
							CreateConsoleMsg("******************************")
						Case "godmode","god"
							CreateConsoleMsg("帮助 - godmode")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("切换无敌模式，除非指定是否启用（on/off）。")
							CreateConsoleMsg("防止玩家在正常情况下死亡。")
							CreateConsoleMsg("******************************")
						Case "wireframe"
							CreateConsoleMsg("帮助 - wireframe")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("切换渲染线框,除非指定是否启用（on/off）。")
							CreateConsoleMsg("只渲染几何体的边框，使其他部分不可见。")
							CreateConsoleMsg("******************************")
						Case "spawnitem"
							CreateConsoleMsg("帮助 - spawnitem")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("在玩家的位置生成物品（可生成能在物品栏里出现的物品）。")
							CreateConsoleMsg("在汉化计划中，你可以使用物品中文名、物品英文名或物品ID生成物品。")
							CreateConsoleMsg("示例：spawnitem 万能钥匙卡 / spawnitem key card omni / spawnitem key6")
							CreateConsoleMsg("******************************")
						Case "spawn"
							CreateConsoleMsg("帮助 - spawn")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("在玩家的位置生成NPC。")
							CreateConsoleMsg("可用参数：")
							CreateConsoleMsg("008zombie / 049 / 049-2 / 066 / 096 / 106 / 173")
							CreateConsoleMsg("/ 178-1 / 372 / 513-1 / 966 / 1499-1 / class-d")
							CreateConsoleMsg("/ guard / mtf / apache / tentacle")
							CreateConsoleMsg("******************************")
						Case "revive","undead","resurrect"
							CreateConsoleMsg("帮助 - revive")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("在死亡动画触发后重置玩家的死亡计时器。")
							CreateConsoleMsg("不影响受伤、失血和008感染。")
							CreateConsoleMsg("******************************")
						Case "teleport"
							CreateConsoleMsg("帮助 - teleport")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("将玩家传送到指定房间。")
							CreateConsoleMsg("可用的房间ID可在room.ini中找到。")
							CreateConsoleMsg("******************************")
						Case "stopsound", "stfu"
							CreateConsoleMsg("帮助 - stopsound")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("停止播放所有声音。")
							CreateConsoleMsg("******************************")
						Case "camerapick"
							CreateConsoleMsg("帮助 - camerapick")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("打印镜头指向的模型的贴图名称和坐标。")
							CreateConsoleMsg("******************************")
						Case "status"
							CreateConsoleMsg("帮助 - status")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("打印玩家、视角和房间信息。")
							CreateConsoleMsg("******************************")
						Case "weed","scp-420-j","420"
							CreateConsoleMsg("帮助 - 420")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("生成亿堆牛█的SCP-420-J！")
							CreateConsoleMsg("******************************")
						Case "playmusic"
							CreateConsoleMsg("帮助 - playmusic")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("播放“SFX\Music\Custom\”文件夹内的.ogg/.wav格式文件。")
							CreateConsoleMsg("******************************")
						Default
							CreateConsoleMsg("该命令没有可用的帮助",255,150,0)
					End Select
					;[End Block]
				Case "asd"
					;[Block]
					WireFrame 1
					WireframeState=1
					GodMode = 1
					NoClip = 1
					CameraFogNear = 15
					CameraFogFar = 20
					;[End Block]
				Case "status"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					CreateConsoleMsg("******************************")
					CreateConsoleMsg("状态: ")
					CreateConsoleMsg("坐标: ")
					CreateConsoleMsg("    - 碰撞: "+EntityX(Collider)+", "+EntityY(Collider)+", "+EntityZ(Collider))
					CreateConsoleMsg("    - 视角: "+EntityX(Camera)+", "+EntityY(Camera)+", "+EntityZ(Camera))
					
					CreateConsoleMsg("旋转: ")
					CreateConsoleMsg("    - 碰撞: "+EntityPitch(Collider)+", "+EntityYaw(Collider)+", "+EntityRoll(Collider))
					CreateConsoleMsg("    - 视角: "+EntityPitch(Camera)+", "+EntityYaw(Camera)+", "+EntityRoll(Camera))
					
					CreateConsoleMsg("房间: "+PlayerRoom\RoomTemplate\Name)
					For ev.Events = Each Events
						If ev\room = PlayerRoom Then
							CreateConsoleMsg("房间事件: "+ev\EventName)		
							CreateConsoleMsg("-    state: "+ev\EventState)
							CreateConsoleMsg("-    state2: "+ev\EventState2)
							CreateConsoleMsg("-    state3: "+ev\EventState3)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("状态: "+Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5)+", "+ Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5))
					CreateConsoleMsg("耐力: "+Stamina)
					CreateConsoleMsg("死亡计时: "+KillTimer)					
					CreateConsoleMsg("眨眼计时: "+BlinkTimer)
					CreateConsoleMsg("受伤: "+Injuries)
					CreateConsoleMsg("失血: "+Bloodloss)
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "camerapick"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					c = CameraPick(Camera,GraphicWidth/2, GraphicHeight/2)
					If c = 0 Then
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("无实体被捡起")
						CreateConsoleMsg("******************************")
					Else
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("捡起的实体:")
						sf = GetSurface(c,1)
						b = GetSurfaceBrush(sf)
						t = GetBrushTexture(b,0)
						texname$ =  StripPath(TextureName(t))
						CreateConsoleMsg("材质名: "+texname)
						CreateConsoleMsg("坐标: "+EntityX(c)+", "+EntityY(c)+", "+EntityZ(c))
						CreateConsoleMsg("******************************")
					EndIf
					;[End Block]
				Case "hidedistance"
					;[Block]
					HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("隐藏状态设置为"+HideDistance)
					;[End Block]
				Case "ending"
					;[Block]
					SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					KillTimer = -0.1
					;[End Block]
				Case "noclipspeed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					NoClipSpeed = Float(StrTemp)
					;[End Block]
				Case "injure"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Injuries = Float(StrTemp)
					;[End Block]
				Case "infect"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Infect = Float(StrTemp)
					;[End Block]
				Case "heal"
					;[Block]
					Injuries = 0
					Bloodloss = 0
					;[End Block]
				Case "teleport"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "895", "scp-895"
							StrTemp = "coffin"
						Case "scp-914"
							StrTemp = "914"
						Case "offices", "office"
							StrTemp = "room2offices"
					End Select
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							PositionEntity (Collider, EntityX(r\obj), EntityY(r\obj)+0.7, EntityZ(r\obj))
							ResetEntity(Collider)
							UpdateDoors()
							UpdateRooms()
							For it.Items = Each Items
								it\disttimer = 0
							Next
							PlayerRoom = r
							Exit
						EndIf
					Next
					
					If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("房间未找到",255,150,0)
					;[End Block]
				Case "spawnitem"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					temp = False 
					For itt.Itemtemplates = Each ItemTemplates
						If (Lower(itt\name) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\displayName + " 已生成")
							it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							Exit
						Else If (Lower(itt\tempname) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\displayName + " 已生成")
							it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							Exit
						Else If (Lower(itt\displayName) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\displayName + " 已生成")
							it.Items = CreateItem(itt\name, itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
							EntityType(it\collider, HIT_ITEM)
							Exit
						End If
					Next
					
					If temp = False Then CreateConsoleMsg("物品未找到",255,150,0)
					;[End Block]
				Case "wireframe"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							WireframeState = True							
						Case "off", "0", "false"
							WireframeState = False
						Default
							WireframeState = Not WireframeState
					End Select
					
					If WireframeState Then
						CreateConsoleMsg("渲染选框 启用")
					Else
						CreateConsoleMsg("渲染选框 禁用")
					EndIf
					
					WireFrame WireframeState
					;[End Block]
				Case "173speed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr173\Speed = Float(StrTemp)
					CreateConsoleMsg("173速度已设为 " + StrTemp)
					;[End Block]
				Case "106speed"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr106\Speed = Float(StrTemp)
					CreateConsoleMsg("106速度已设为 " + StrTemp)
					;[End Block]
				Case "173state"
					;[Block]
					CreateConsoleMsg("SCP-173")
					CreateConsoleMsg("位置: " + EntityX(Curr173\obj) + ", " + EntityY(Curr173\obj) + ", " + EntityZ(Curr173\obj))
					CreateConsoleMsg("游荡: " + Curr173\Idle)
					CreateConsoleMsg("状态: " + Curr173\State)
					;[End Block]
				Case "106state"
					;[Block]
					CreateConsoleMsg("SCP-106")
					CreateConsoleMsg("位置: " + EntityX(Curr106\obj) + ", " + EntityY(Curr106\obj) + ", " + EntityZ(Curr106\obj))
					CreateConsoleMsg("游荡: " + Curr106\Idle)
					CreateConsoleMsg("状态: " + Curr106\State)
					;[End Block]
				Case "reset096"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							n\State = 0
							StopStream_Strict(n\SoundChn) : n\SoundChn=0
							If n\SoundChn2<>0
								StopStream_Strict(n\SoundChn2) : n\SoundChn2=0
							EndIf
							Exit
						EndIf
					Next
					;[End Block]
				Case "disable173"
					;[Block]
					Curr173\Idle = 3 ;This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of 173 state checks.
					HideEntity Curr173\obj
					HideEntity Curr173\Collider
					;[End Block]
				Case "enable173"
					;[Block]
					Curr173\Idle = False
					ShowEntity Curr173\obj
					ShowEntity Curr173\Collider
					;[End Block]
				Case "disable106"
					;[Block]
					Curr106\Idle = True
					Curr106\State = 200000
					Contained106 = True
					;[End Block]
				Case "enable106"
					;[Block]
					Curr106\Idle = False
					Contained106 = False
					ShowEntity Curr106\Collider
					ShowEntity Curr106\obj
					;[End Block]
				Case "halloween"
					;[Block]
					HalloweenTex = Not HalloweenTex
					If HalloweenTex Then
						Local tex = LoadTexture_Strict("GFX\npcs\173h.jpg", 1)
						EntityTexture Curr173\obj, tex, 0, 0
						FreeTexture tex
						CreateConsoleMsg("南瓜173 启用")
					Else
						Local tex2 = LoadTexture_Strict("GFX\npcs\173texture.jpg", 1)
						EntityTexture Curr173\obj, tex2, 0, 0
						FreeTexture tex2
						CreateConsoleMsg("南瓜173 禁用")
					EndIf
					;[End Block]
				Case "sanic"
					;[Block]
					SuperMan = Not SuperMan
					If SuperMan = True Then
						CreateConsoleMsg("GOTTA GO FAST")
					Else
						CreateConsoleMsg("WHOA SLOW DOWN")
					EndIf
					;[End Block]
				Case "scp-420-j","420","weed"
					;[Block]
					For i = 1 To 20
						If Rand(2)=1 Then
							it.Items = CreateItem("Some SCP-420-J","420", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
						Else
							it.Items = CreateItem("Joint","420s", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
						EndIf
						EntityType (it\collider, HIT_ITEM)
					Next
					PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
					;[End Block]
				Case "godmode", "god"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							GodMode = True						
						Case "off", "0", "false"
							GodMode = False
						Default
							GodMode = Not GodMode
					End Select	
					If GodMode Then
						CreateConsoleMsg("无敌模式 启用")
					Else
						CreateConsoleMsg("无敌模式 禁用")	
					EndIf
					;[End Block]
				Case "revive","undead","resurrect"
					;[Block]
					DropSpeed = -0.1
					HeadDropSpeed = 0.0
					Shake = 0
					CurrSpeed = 0
					
					HeartBeatVolume = 0
					
					CameraShake = 0
					Shake = 0
					LightFlash = 0
					BlurTimer = 0
					
					FallTimer = 0
					MenuOpen = False
					
					GodMode = 0
					NoClip = 0
					
					ShowEntity Collider
					
					KillTimer = 0
					KillAnim = 0
					;[End Block]
				Case "noclip","fly"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							NoClip = True
							Playable = True
						Case "off", "0", "false"
							NoClip = False	
							RotateEntity Collider, 0, EntityYaw(Collider), 0
						Default
							NoClip = Not NoClip
							If NoClip = False Then		
								RotateEntity Collider, 0, EntityYaw(Collider), 0
							Else
								Playable = True
							EndIf
					End Select
					
					If NoClip Then
						CreateConsoleMsg("无碰撞 启用")
					Else
						CreateConsoleMsg("无碰撞 禁用")
					EndIf
					
					DropSpeed = 0
					;[End Block]
				Case "showfps"
					;[Block]
					ShowFPS = Not ShowFPS
					CreateConsoleMsg("显示FPS: "+Str(ShowFPS))
					;[End Block]
				Case "096state"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							CreateConsoleMsg("SCP-096")
							CreateConsoleMsg("位置: " + EntityX(n\obj) + ", " + EntityY(n\obj) + ", " + EntityZ(n\obj))
							CreateConsoleMsg("游荡: " + n\Idle)
							CreateConsoleMsg("状态: " + n\State)
							Exit
						EndIf
					Next
					CreateConsoleMsg("SCP-096尚未生成")
					;[End Block]
				Case "debughud"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Select StrTemp
						Case "on", "1", "true"
							DebugHUD = True
						Case "off", "0", "false"
							DebugHUD = False
						Default
							DebugHUD = Not DebugHUD
					End Select
					
					If DebugHUD Then
						CreateConsoleMsg("调试界面 启用")
					Else
						CreateConsoleMsg("调试界面 禁用")
					EndIf
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\channels[i]<>0 Then
								StopChannel snd\channels[i]
							EndIf
						Next
					Next
					
					For e.Events = Each Events
						If e\EventName = "alarm" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity e\room\Objects[0] : e\room\Objects[0]=0
							FreeEntity e\room\Objects[1] : e\room\Objects[1]=0
							PositionEntity Curr173\Collider, 0,0,0
							ResetEntity Curr173\Collider
							ShowEntity Curr173\obj
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("停止播放所有声音")
					;[End Block]
				Case "camerafog"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CameraFogNear = Float(Left(args, Len(args) - Instr(args, " ")))
					CameraFogFar = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("可视距离已设为：" + CameraFogNear + "，渲染距离已设为：" + CameraFogFar)
					;[End Block]
				Case "gamma"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					ScreenGamma = Int(StrTemp)
					CreateConsoleMsg("屏幕伽马已设为 " + ScreenGamma)
					;[End Block]
				Case "spawn"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$, 1)
					StrTemp2$ = Piece$(args$, 2)
					
					;Hacky fix for when the user doesn't input a second parameter.
					If (StrTemp <> StrTemp2) Then
						Console_SpawnNPC(StrTemp, StrTemp2)
					Else
						Console_SpawnNPC(StrTemp)
					EndIf
					;[End Block]
				;new Console Commands in SCP:CB 1.3 - ENDSHN
				Case "infinitestamina","infstam"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							InfiniteStamina% = True						
						Case "off", "0", "false"
							InfiniteStamina% = False
						Default
							InfiniteStamina% = Not InfiniteStamina%
					End Select
					
					If InfiniteStamina
						CreateConsoleMsg("无限奔跑 启用")
					Else
						CreateConsoleMsg("无限奔跑 禁用")	
					EndIf
					;[End Block]
				Case "asd2"
					;[Block]
					GodMode = 1
					InfiniteStamina = 1
					Curr173\Idle = 3
					Curr106\Idle = True
					Curr106\State = 200000
					Contained106 = True
					;[End Block]
				Case "toggle_warhead_lever"
					;[Block]
					For e.Events = Each Events
						If e\EventName = "room2nuke" Then
							e\EventState = (Not e\EventState)
							Exit
						EndIf
					Next
					;[End Block]
				Case "unlockexits"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "a"
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg("大门A已解锁")	
						Case "b"
							For e.Events = Each Events
								If e\EventName = "exit1" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
									Exit
								EndIf
							Next	
							CreateConsoleMsg("大门B已解锁")	
						Default
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
								ElseIf e\EventName = "exit1" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
								EndIf
							Next
							CreateConsoleMsg("大门A及大门B已解锁")	
					End Select
					
					RemoteDoorOn = True
					;[End Block]
				Case "kill","suicide"
					;[Block]
					KillTimer = -1
					Select Rand(4)
						Case 1
							DeathMSG = "[已编辑]"
						Case 2
							DeathMSG = "对象D-9341的尸体在[已编辑]区找到。"
							DeathMSG = DeathMSG + "对象似乎没有受到任何物理伤害，也没有明显迹象表明是什么杀死了他。"
							DeathMSG = DeathMSG + "尸体已被送去医检。"
						Case 3
							DeathMSG = "EXCP_ACCESS_VIOLATION 意外的访问冲突"
						Case 4
							DeathMSG = "对象D-9341的尸体在[已编辑]区找到。"
							DeathMSG = DeathMSG + "对象似乎在他身边用自己的血液写下了字母“kys”。"
							DeathMSG = DeathMSG + "没有其他身体创伤或挣扎的迹象，尸体被送去尸检。"
					End Select
					;[End Block]
				Case "playmusic"
					;[Block]
					; I think this might be broken since the FMod library streaming was added. -Mark
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If StrTemp$ <> ""
						PlayCustomMusic% = True
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
						CustomMusic = LoadSound_Strict("SFX\Music\Custom\"+StrTemp$)
						If CustomMusic = 0
							PlayCustomMusic% = False
						EndIf
					Else
						PlayCustomMusic% = False
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
					EndIf
					;[End Block]
				Case "tp"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtypeMTF
							If n\MTFLeader = Null
								PositionEntity Collider,EntityX(n\Collider),EntityY(n\Collider)+5,EntityZ(n\Collider)
								ResetEntity Collider
								Exit
							EndIf
						EndIf
					Next
					;[End Block]
				Case "tele"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					PositionEntity Collider,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					PositionEntity Camera,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					ResetEntity Collider
					ResetEntity Camera
					CreateConsoleMsg("传送至坐标 (X|Y|Z): "+EntityX(Collider)+"|"+EntityY(Collider)+"|"+EntityZ(Collider))
					;[End Block]
				Case "notarget"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							NoTarget% = True						
						Case "off", "0", "false"
							NoTarget% = False	
						Default
							NoTarget% = Not NoTarget%
					End Select
					
					If NoTarget% = False Then
						CreateConsoleMsg("无互动 禁用")
					Else
						CreateConsoleMsg("无互动 启用")	
					EndIf
					;[End Block]
				Case "spawnradio"
					;[Block]
					it.Items = CreateItem("Radio Transceiver", "fineradio", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 101
					;[End Block]
				Case "spawnnvg"
					;[Block]
					it.Items = CreateItem("Night Vision Goggles", "nvgoggles", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 1000
					;[End Block]
				Case "spawnpumpkin","pumpkin"
					;[Block]
					CreateConsoleMsg("什么南瓜？")
					;[End Block]
				Case "spawnnav"
					;[Block]
					it.Items = CreateItem("S-NAV Navigator Ultimate", "nav", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 101
					;[End Block]
				Case "teleport173"
					;[Block]
					PositionEntity Curr173\Collider,EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider)
					ResetEntity Curr173\Collider
					;[End Block]
				Case "seteventstate"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					Local pl_room_found% = False
					If StrTemp="" Or StrTemp2="" Or StrTemp3=""
						CreateConsoleMsg("参数过少，该命令需要三个参数。",255,150,0)
					Else
						For e.Events = Each Events
							If e\room = PlayerRoom
								If Lower(StrTemp)<>"keep"
									e\EventState = Float(StrTemp)
								EndIf
								If Lower(StrTemp2)<>"keep"
									e\EventState2 = Float(StrTemp2)
								EndIf
								If Lower(StrTemp3)<>"keep"
									e\EventState3 = Float(StrTemp3)
								EndIf
								CreateConsoleMsg("将玩家当前房间事件状态更改为："+e\EventState+"|"+e\EventState2+"|"+e\EventState3)
								pl_room_found = True
								Exit
							EndIf
						Next
						If (Not pl_room_found)
							CreateConsoleMsg("当前房间未应用任何事件",255,150,0)
						EndIf
					EndIf
					;[End Block]
				Case "spawnparticles"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp) > -1 And Int(StrTemp) <= 1 ;<--- This is the maximum ID of particles by Devil Particle system, will be increased after time - ENDSHN
						SetEmitter(Collider,ParticleEffect[Int(StrTemp)])
						CreateConsoleMsg("在玩家位置生成ID为 "+Int(StrTemp)+" 的粒子效果")
					Else
						CreateConsoleMsg("ID为 "+Int(StrTemp)+" 的粒子效果未找到",255,150,0)
					EndIf
					;[End Block]
				Case "giveachievement"
					;[Block]
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp)>=0 And Int(StrTemp)<MAXACHIEVEMENTS
						Achievements(Int(StrTemp))=True
						CreateConsoleMsg("成就 "+AchievementStrings(Int(StrTemp))+" 已解锁")
					Else
						CreateConsoleMsg("ID为 "+Int(StrTemp)+" 的成就不存在",255,150,0)
					EndIf
					;[End Block]
				Case "427state"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = Float(StrTemp)*70.0
					;[End Block]
				Case "teleport106"
					;[Block]
					Curr106\State = 0
					Curr106\Idle = False
					;[End Block]
				Case "setblinkeffect"
					;[Block]
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					BlinkEffect = Float(Left(args, Len(args) - Instr(args, " ")))
					BlinkEffectTimer = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("设置眨眼效果为: " + BlinkEffect + "，设置眨眼时间为: " + BlinkEffectTimer)
					;[End Block]
				Case "jorge"
					;[Block]	
					CreateConsoleMsg("乔治一直在等你。")
					;[End Block]
				Default
					;[Block]
					CreateConsoleMsg("命令未找到",255,0,0)
					;[End Block]
			End Select
			
			ConsoleInput = ""
		End If
		
		Local TempY% = y + height - 25*MenuScale - ConsoleScroll
		Local count% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			count = count+1
			If count>1000 Then
				Delete cm
			Else
				If TempY >= y And TempY < y + height - 20*MenuScale Then
					If cm=ConsoleReissue Then
						Color cm\r/4,cm\g/4,cm\b/4
						Rect x,TempY-2*MenuScale,width-30*MenuScale,24*MenuScale,True
					EndIf
					Color cm\r,cm\g,cm\b
					If cm\isCommand Then
						Text(x + 20*MenuScale, TempY, "> "+cm\txt)
					Else
						Text(x + 20*MenuScale, TempY, cm\txt)
					EndIf
				EndIf
				TempY = TempY - 15*MenuScale
			EndIf
		Next
		Color 255,255,255
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	End If
	SetFont Font1
End Function

ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
CreateConsoleMsg("Console commands: ")
CreateConsoleMsg("  - teleport [room name]")
CreateConsoleMsg("  - godmode [on/off]")
CreateConsoleMsg("  - noclip [on/off]")
CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
CreateConsoleMsg("  - wireframe [on/off]")
CreateConsoleMsg("  - debughud [on/off]")
CreateConsoleMsg("  - camerafog [near] [far]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - status")
CreateConsoleMsg("  - heal")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - spawnitem [item name]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - 173speed [x] (default = 35)")
CreateConsoleMsg("  - disable173/enable173")
CreateConsoleMsg("  - disable106/enable106")
CreateConsoleMsg("  - 173state/106state/096state")
CreateConsoleMsg("  - spawn [npc type]")

;---------------------------------------------------------------------------------------------------

Global DebugHUD%

Global BlurVolume#, BlurTimer#

Global LightBlink#, LightFlash#

Global BumpEnabled% = GetINIInt("options.ini", "options", "bump mapping enabled")
Global HUDenabled% = GetINIInt("options.ini", "options", "HUD enabled")

Global Camera%, CameraShake#, CurrCameraZoom#

Global Brightness% = GetINIFloat("options.ini", "options", "brightness")
Global CameraFogNear# = GetINIFloat("options.ini", "options", "camera fog near")
Global CameraFogFar# = GetINIFloat("options.ini", "options", "camera fog far")

Global StoredCameraFogFar# = CameraFogFar

Global MouseSens# = GetINIFloat("options.ini", "options", "mouse sensitivity")

Global EnableVRam% = GetINIInt("options.ini", "options", "enable vram")

Include "Dreamfilter.bb"

Dim LightSpriteTex(10)

;----------------------------------------------  Sounds -----------------------------------------------------

;[Block]

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

;The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Dim Music$(40)
Music(0) = "The Dread"
Music(1) = "HeavyContainment"
Music(2) = "EntranceZone"
Music(3) = "PD"
Music(4) = "079"
Music(5) = "GateB1"
Music(6) = "GateB2"
Music(7) = "Room3Storage"
Music(8) = "Room049"
Music(9) = "8601"
Music(10) = "106"
Music(11) = "Menu"
Music(12) = "8601Cancer"
Music(13) = "Intro"
Music(14) = "178"
Music(15) = "PDTrench"
Music(16) = "205"
Music(17) = "GateA"
Music(18) = "1499"
Music(19) = "1499Danger"
Music(20) = "049Chase"
Music(21) = "..\Ending\MenuBreath"
Music(22) = "914"
Music(23) = "Ending"
Music(24) = "Credits"
Music(25) = "SaveMeFrom"

Global MusicVolume# = GetINIFloat(OptionFile, "audio", "music volume")

Global CurrMusicStream, MusicCHN
MusicCHN = StreamSound_Strict("SFX\Music\"+Music(2)+".ogg",MusicVolume,Mode)

Global CurrMusicVolume# = 1.0, NowPlaying%=2, ShouldPlay%=11
Global CurrMusic% = 1

DrawLoading(10, True)

Dim OpenDoorSFX%(3,3), CloseDoorSFX%(3,3)

Global KeyCardSFX1
Global KeyCardSFX2
Global ButtonSFX2
Global ScannerSFX1
Global ScannerSFX2

Global OpenDoorFastSFX
Global CautionSFX% 

Global NuclearSirenSFX%

Global CameraSFX  

Global StoneDragSFX% 

Global GunshotSFX% 
Global Gunshot2SFX% 
Global Gunshot3SFX% 
Global BullethitSFX% 

Global TeslaIdleSFX 
Global TeslaActivateSFX 
Global TeslaPowerUpSFX 

Global MagnetUpSFX%, MagnetDownSFX
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Dim DecaySFX%(5)

Global BurstSFX 

DrawLoading(20, True)

Dim RustleSFX%(3)

Global Use914SFX%
Global Death914SFX% 

Dim DripSFX%(4)

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX(5,10) 

Global RadioSquelch 
Global RadioStatic 
Global RadioBuzz 

Global ElevatorBeepSFX, ElevatorMoveSFX  

Dim PickSFX%(10)

Global AmbientSFXCHN%, CurrAmbientSFX%
Dim AmbientSFXAmount(6)
;0 = light containment, 1 = heavy containment, 2 = entrance
AmbientSFXAmount(0)=8 : AmbientSFXAmount(1)=11 : AmbientSFXAmount(2)=12
;3 = general, 4 = pre-breach
AmbientSFXAmount(3)=15 : AmbientSFXAmount(4)=5
;5 = forest
AmbientSFXAmount(5)=10

Dim AmbientSFX%(6, 15)

Dim OldManSFX%(8)

Dim Scp173SFX%(3)

Dim HorrorSFX%(20)


DrawLoading(25, True)

Dim IntroSFX%(20)

Dim AlarmSFX%(5)

Dim CommotionState%(25)

Global HeartBeatSFX 

Global VomitSFX%

Dim BreathSFX(2,5)
Global BreathCHN%


Dim NeckSnapSFX(3)

Dim DamageSFX%(9)

Dim MTFSFX%(8)

Dim CoughSFX%(3)
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX
Global CurrStepSFX
Dim StepSFX%(5, 2, 8) ;(normal/metal, walk/run, id)

Dim Step2SFX(6)

DrawLoading(30, True)

;[End block]

;New Sounds and Meshes/Other things in SCP:CB 1.3 - ENDSHN
;[Block]
Global PlayCustomMusic% = False, CustomMusic% = 0

Global Monitor2, Monitor3, MonitorTexture2, MonitorTexture3, MonitorTexture4, MonitorTextureOff
Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

;This variable is for when a camera detected the player
	;False: Player is not seen (will be set after every call of the Main Loop
	;True: The Player got detected by a camera
Global PlayerDetected%
Global PrevInjuries#,PrevBloodloss#
Global NoTarget% = False

Global NVGImages = LoadAnimImage("GFX\battery.png",64,64,0,2)
MaskImage NVGImages,255,0,255

Global Wearing1499% = False
Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global EnableUserTracks% = GetINIInt(OptionFile, "audio", "enable user tracks")
Global UserTrackMode% = GetINIInt(OptionFile, "audio", "user track setting")
Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Dim UserTrackName$(256)

Global NTF_1499PrevX#
Global NTF_1499PrevY#
Global NTF_1499PrevZ#
Global NTF_1499PrevRoom.Rooms
Global NTF_1499X#
Global NTF_1499Y#
Global NTF_1499Z#
Global NTF_1499Sky%

Global OptionsMenu% = 0
Global QuitMSG% = 0

Global InFacility% = True

Global PrevMusicVolume# = MusicVolume#
Global PrevSFXVolume# = SFXVolume#
Global DeafPlayer% = False
Global DeafTimer# = 0.0

Global IsZombie% = False

Global room2gw_brokendoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Global Menu_TestIMG
Global menuroomscale# = 8.0 / 2048.0

Global CurrMenu_TestIMG$ = ""

Global ParticleAmount% = GetINIInt(OptionFile,"options","particle amount")

Dim NavImages(5)
For i = 0 To 3
	NavImages(i) = LoadImage_Strict("GFX\navigator\roomborder"+i+".png")
	MaskImage NavImages(i),255,0,255
Next
NavImages(4) = LoadImage_Strict("GFX\navigator\batterymeter.png")

Global NavBG = CreateImage(GraphicWidth,GraphicHeight)

Global LightConeModel

Global ParticleEffect[10]

Const MaxDTextures=8
Global DTextures[MaxDTextures]

Global NPC049OBJ, NPC0492OBJ
Global ClerkOBJ

Global IntercomStreamCHN%

Global ForestNPC,ForestNPCTex,ForestNPCData#[3]
;[End Block]

;-----------------------------------------  Images ----------------------------------------------------------

Global PauseMenuIMG%

Global SprintIcon%
Global BlinkIcon%
Global CrouchIcon%
Global HandIcon%
Global HandIcon2%

Global StaminaMeterIMG%

Global KeypadHUD

Global Panel294, Using294%, Input294$

DrawLoading(35, True)

;----------------------------------------------  Items  -----------------------------------------------------

Include "Items.bb"

;--------------------------------------- Particles ------------------------------------------------------------

Include "Particles.bb"

;-------------------------------------  Doors --------------------------------------------------------------

Global ClosestButton%, ClosestDoor.Doors
Global SelectedDoor.Doors, UpdateDoorsTimer#
Global DoorTempID%
Type Doors
	Field obj%, obj2%, frameobj%, buttons%[2]
	Field locked%, open%, angle%, openstate#, fastopen%
	Field dir%
	Field timer%, timerstate#
	Field KeyCard%
	Field room.Rooms
	
	Field DisableWaypoint%
	
	Field dist#
	
	Field SoundCHN%
	
	Field Code$
	
	Field ID%
	
	Field Level%
	Field LevelDest%
	
	Field AutoClose%
	
	Field LinkedDoor.Doors
	
	Field IsElevatorDoor% = False
	
	Field MTFClose% = True
	Field NPCCalledElevator% = False
	
	Field DoorHitOBJ%
End Type 

Dim BigDoorOBJ(2), HeavyDoorObj(2)
Dim OBJTunnel(7)

Function CreateDoor.Doors(lvl, x#, y#, z#, angle#, room.Rooms, dopen% = False,  big% = False, keycard% = False, code$="", useCollisionMesh% = False)
	Local d.Doors, parent, i%
	If room <> Null Then parent = room\obj
	
	Local d2.Doors
	
	d.Doors = New Doors
	If big=1 Then
		d\obj = CopyEntity(BigDoorOBJ(0))
		ScaleEntity(d\obj, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		d\obj2 = CopyEntity(BigDoorOBJ(1))
		ScaleEntity(d\obj2, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		
		d\frameobj = CopyEntity(DoorColl)	;CopyMesh				
		ScaleEntity(d\frameobj, RoomScale, RoomScale, RoomScale)
		EntityType d\frameobj, HIT_MAP
		EntityAlpha d\frameobj, 0.0
	ElseIf big=2 Then
		d\obj = CopyEntity(HeavyDoorObj(0))
		ScaleEntity(d\obj, RoomScale, RoomScale, RoomScale)
		d\obj2 = CopyEntity(HeavyDoorObj(1))
		ScaleEntity(d\obj2, RoomScale, RoomScale, RoomScale)
		
		d\frameobj = CopyEntity(DoorFrameOBJ)
	ElseIf big=3 Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = 3 Then
				d\obj = CopyEntity(d2\obj)
				d\obj2 = CopyEntity(d2\obj2)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj=0 Then
			d\obj = LoadMesh_Strict("GFX\map\elevatordoor.b3d")
			d\obj2 = CopyEntity(d\obj)
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
			ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(DoorFrameOBJ)
	Else
		d\obj = CopyEntity(DoorOBJ)
		ScaleEntity(d\obj, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
		
		d\frameobj = CopyEntity(DoorFrameOBJ)
		d\obj2 = CopyEntity(DoorOBJ)
		
		ScaleEntity(d\obj2, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
	End If
	
	PositionEntity d\frameobj, x, y, z	
	ScaleEntity(d\frameobj, (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
	EntityPickMode d\frameobj,2
	EntityType d\obj, HIT_MAP
	EntityType d\obj2, HIT_MAP
	
	d\ID = DoorTempID
	DoorTempID=DoorTempID+1
	
	d\KeyCard = keycard
	d\Code = code
	
	d\Level = lvl
	d\LevelDest = 66
	
	For i% = 0 To 1
		If code <> "" Then 
			d\buttons[i]= CopyEntity(ButtonCodeOBJ)
			EntityFX(d\buttons[i], 1)
		Else
			If keycard>0 Then
				d\buttons[i]= CopyEntity(ButtonKeyOBJ)
			ElseIf keycard<0
				d\buttons[i]= CopyEntity(ButtonScannerOBJ)	
			Else
				d\buttons[i] = CopyEntity(ButtonOBJ)
			End If
		EndIf
		
		ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
	Next
	
	If big=1 Then
		PositionEntity d\buttons[0], x - 432.0 * RoomScale, y + 0.7, z + 192.0 * RoomScale
		PositionEntity d\buttons[1], x + 432.0 * RoomScale, y + 0.7, z - 192.0 * RoomScale
		RotateEntity d\buttons[0], 0, 90, 0
		RotateEntity d\buttons[1], 0, 270, 0
	Else
		PositionEntity d\buttons[0], x + 0.6, y + 0.7, z - 0.1
		PositionEntity d\buttons[1], x - 0.6, y + 0.7, z + 0.1
		RotateEntity d\buttons[1], 0, 180, 0		
	End If
	EntityParent(d\buttons[0], d\frameobj)
	EntityParent(d\buttons[1], d\frameobj)
	EntityPickMode(d\buttons[0], 2)
	EntityPickMode(d\buttons[1], 2)
	
	PositionEntity d\obj, x, y, z
	
	RotateEntity d\obj, 0, angle, 0
	RotateEntity d\frameobj, 0, angle, 0
	
	If d\obj2 <> 0 Then
		PositionEntity d\obj2, x, y, z
		If big=1 Then
			RotateEntity(d\obj2, 0, angle, 0)
		Else
			RotateEntity(d\obj2, 0, angle + 180, 0)
		EndIf
		EntityParent(d\obj2, parent)
	EndIf
	
	EntityParent(d\frameobj, parent)
	EntityParent(d\obj, parent)
	
	d\angle = angle
	d\open = dopen		
	
	EntityPickMode(d\obj, 2)
	If d\obj2 <> 0 Then
		EntityPickMode(d\obj2, 2)
	EndIf
	
	EntityPickMode d\frameobj,2
	
	If d\open And big = False And Rand(8) = 1 Then d\AutoClose = True
	d\dir=big
	d\room=room
	
	d\MTFClose = True
	
	If useCollisionMesh Then
		For d2.Doors = Each Doors
			If d2 <> d Then
				If d2\DoorHitOBJ <> 0 Then
					d\DoorHitOBJ = CopyEntity(d2\DoorHitOBJ,d\frameobj)
					EntityAlpha d\DoorHitOBJ,0.0
					EntityFX d\DoorHitOBJ,1
					EntityType d\DoorHitOBJ,HIT_MAP
					EntityColor d\DoorHitOBJ,255,0,0
					HideEntity d\DoorHitOBJ
					Exit
				EndIf
			EndIf
		Next
		If d\DoorHitOBJ=0 Then
			d\DoorHitOBJ = LoadMesh_Strict("GFX\doorhit.b3d",d\frameobj)
			EntityAlpha d\DoorHitOBJ,0.0
			EntityFX d\DoorHitOBJ,1
			EntityType d\DoorHitOBJ,HIT_MAP
			EntityColor d\DoorHitOBJ,255,0,0
			HideEntity d\DoorHitOBJ
		EndIf
	EndIf
	Return d
End Function

Function CreateButton(x#,y#,z#, pitch#,yaw#,roll#=0)
	Local obj = CopyEntity(ButtonOBJ)	
	
	ScaleEntity(obj, 0.03, 0.03, 0.03)
	
	PositionEntity obj, x,y,z
	RotateEntity obj, pitch,yaw,roll
	
	EntityPickMode(obj, 2)	
	
	Return obj
End Function

Function UpdateDoors()
	Local i%, d.Doors, x#, z#, dist#
	If UpdateDoorsTimer =< 0 Then
		For d.Doors = Each Doors
			Local xdist# = Abs(EntityX(Collider)-EntityX(d\obj,True))
			Local zdist# = Abs(EntityZ(Collider)-EntityZ(d\obj,True))
			
			d\dist = xdist+zdist
			
			If d\dist > HideDistance*2 Then
				If d\obj <> 0 Then HideEntity d\obj
				If d\frameobj <> 0 Then HideEntity d\frameobj
				If d\obj2 <> 0 Then HideEntity d\obj2
				If d\buttons[0] <> 0 Then HideEntity d\buttons[0]
				If d\buttons[1] <> 0 Then HideEntity d\buttons[1]				
			Else
				If d\obj <> 0 Then ShowEntity d\obj
				If d\frameobj <> 0 Then ShowEntity d\frameobj
				If d\obj2 <> 0 Then ShowEntity d\obj2
				If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
				If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
			EndIf
			
			If PlayerRoom\RoomTemplate\Name$ = "room2sl"
				If ValidRoom2slCamRoom(d\room)
					If d\obj <> 0 Then ShowEntity d\obj
					If d\frameobj <> 0 Then ShowEntity d\frameobj
					If d\obj2 <> 0 Then ShowEntity d\obj2
					If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
					If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
				EndIf
			EndIf
		Next
		
		UpdateDoorsTimer = 30
	Else
		UpdateDoorsTimer = Max(UpdateDoorsTimer-FPSfactor,0)
	EndIf
	
	ClosestButton = 0
	ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\dist < HideDistance*2 Or d\IsElevatorDoor>0 Then ;Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel - ENDSHN
			
			If (d\openstate >= 180 Or d\openstate <= 0) And GrabbedEntity = 0 Then
				For i% = 0 To 1
					If d\buttons[i] <> 0 Then
						If Abs(EntityX(Collider)-EntityX(d\buttons[i],True)) < 1.0 Then 
							If Abs(EntityZ(Collider)-EntityZ(d\buttons[i],True)) < 1.0 Then 
								dist# = Distance(EntityX(Collider, True), EntityZ(Collider, True), EntityX(d\buttons[i], True), EntityZ(d\buttons[i], True));entityDistance(collider, d\buttons[i])
								If dist < 0.7 Then
									Local temp% = CreatePivot()
									PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
									PointEntity temp,d\buttons[i]
									
									If EntityPick(temp, 0.6) = d\buttons[i] Then
										If ClosestButton = 0 Then
											ClosestButton = d\buttons[i]
											ClosestDoor = d
										Else
											If dist < EntityDistance(Collider, ClosestButton) Then ClosestButton = d\buttons[i] : ClosestDoor = d
										End If							
									End If
									
									FreeEntity temp
									
								EndIf							
							EndIf
						EndIf
						
					EndIf
				Next
			EndIf
			
			If d\open Then
				If d\openstate < 180 Then
					Select d\dir
						Case 0
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen+1) * FPSfactor / 80.0, 0, 0)		
						Case 1
							d\openstate = Min(180, d\openstate + FPSfactor * 0.8)
							MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, -Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
						Case 2
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen+1) * FPSfactor / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 120.0, 0, 0)
						Case 3
							d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
						Case 4 ;Used for 914 only
							d\openstate = Min(180, d\openstate + FPSfactor * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 114.0, 0, 0)
					End Select
				Else
					d\fastopen = 0
					ResetEntity(d\obj)
					If d\obj2 <> 0 Then ResetEntity(d\obj2)
					If d\timerstate > 0 Then
						d\timerstate = Max(0, d\timerstate - FPSfactor)
						If d\timerstate + FPSfactor > 110 And d\timerstate <= 110 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\obj)
						;If d\timerstate = 0 Then d\open = (Not d\open) : PlaySound2(CloseDoorSFX(Min(d\dir,1),Rand(0, 2)), Camera, d\obj)
						Local sound%
						If d\dir = 1 Then sound% = Rand(0, 1) Else sound% = Rand(0, 2)
						If d\timerstate = 0 Then d\open = (Not d\open) : d\SoundCHN = PlaySound2(CloseDoorSFX(d\dir,sound%), Camera, d\obj)
					EndIf
					If d\AutoClose And RemoteDoorOn = True Then
						If EntityDistance(Camera, d\obj) < 2.1 Then
							If (Not Wearing714) Then PlaySound_Strict HorrorSFX(7)
							d\open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\dir,1), Rand(0, 2)), Camera, d\obj) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\openstate > 0 Then
					Select d\dir
						Case 0
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 80.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 80.0, 0, 0)	
						Case 1
							d\openstate = Max(0, d\openstate - FPSfactor*0.8)
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 180.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
							If d\openstate < 15 And d\openstate+FPSfactor => 15
								If ParticleAmount=2
									For i = 0 To Rand(75,99)
										Local pvt% = CreatePivot()
										PositionEntity(pvt, EntityX(d\frameobj,True)+Rnd(-0.2,0.2), EntityY(d\frameobj,True)+Rnd(0.0,1.2), EntityZ(d\frameobj,True)+Rnd(-0.2,0.2))
										RotateEntity(pvt, 0, Rnd(360), 0)
										
										Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
										p\speed = 0.005
										RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
										
										p\SizeChange = -0.00001
										p\size = 0.01
										ScaleSprite p\obj,p\size,p\size
										
										p\Achange = -0.01
										
										EntityOrder p\obj,-1
										
										FreeEntity pvt
									Next
								EndIf
							EndIf
						Case 2
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 85.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 120.0, 0, 0)
						Case 3
							d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 162.0, 0, 0)
							If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 162.0, 0, 0)
						Case 4 ;Used for 914 only
							d\openstate = Min(180, d\openstate - FPSfactor * 1.4)
							MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 114.0, 0, 0)
					End Select
					
					If d\angle = 0 Or d\angle=180 Then
						If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.15 Then
							If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.7*(d\dir*2+1) Then
								z# = CurveValue(EntityZ(d\frameobj,True)+0.15*Sgn(EntityZ(Collider)-EntityZ(d\frameobj, True)), EntityZ(Collider), 5)
								PositionEntity Collider, EntityX(Collider), EntityY(Collider), z
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.15 Then	
							If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.7*(d\dir*2+1) Then
								x# = CurveValue(EntityX(d\frameobj,True)+0.15*Sgn(EntityX(Collider)-EntityX(d\frameobj, True)), EntityX(Collider), 5)
								PositionEntity Collider, x, EntityY(Collider), EntityZ(Collider)
							EndIf
						EndIf
					EndIf
					
					If d\DoorHitOBJ <> 0 Then
						ShowEntity d\DoorHitOBJ
					EndIf
				Else
					d\fastopen = 0
					PositionEntity(d\obj, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 Then PositionEntity(d\obj2, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
					If d\obj2 <> 0 And d\dir = 0 Then
						MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
						MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
					EndIf
					If d\DoorHitOBJ <> 0 Then
						HideEntity d\DoorHitOBJ
					EndIf
				EndIf
			EndIf
			
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\frameobj)
		
		If d\DoorHitOBJ<>0 Then
			If DebugHUD Then
				EntityAlpha d\DoorHitOBJ,0.5
			Else
				EntityAlpha d\DoorHitOBJ,0.0
			EndIf
		EndIf
	Next
End Function

Function UseDoor(d.Doors, showmsg%=True, playsfx%=True)
	Local temp% = 0
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If showmsg = True Then
				If (Instr(Msg,"钥匙卡")=0 And Instr(Msg,"你需要")=0) Or (MsgTimer<70*3) Then
					Msg = "你需要一张钥匙卡来操作这扇门"
					MsgTimer = 70 * 7
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\itemtemplate\tempname
				Case "key1"
					temp = 1
				Case "key2"
					temp = 2
				Case "key3"
					temp = 3
				Case "key4"
					temp = 4
				Case "key5"
					temp = 5
				Case "key6"
					temp = 6
				Default 
					temp = -1
			End Select
			
			If temp =-1 Then 
				If showmsg = True Then
					If (Instr(Msg,"钥匙卡")=0 And Instr(Msg,"你需要")=0) Or (MsgTimer<70*3) Then
					Msg = "你需要一张钥匙卡来操作这扇门"
					MsgTimer = 70 * 7
				EndIf
				EndIf
				Return				
			ElseIf temp >= d\KeyCard 
				SelectedItem = Null
				If showmsg = True Then
					If d\locked Then
						PlaySound_Strict KeyCardSFX2
						Msg = "你将钥匙卡插入了卡槽但无事发生"
						MsgTimer = 70 * 7
						Return
					Else
						PlaySound_Strict KeyCardSFX1
						Msg = "你将钥匙卡插入了卡槽"
						MsgTimer = 70 * 7	
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If showmsg = True Then 
					PlaySound_Strict KeyCardSFX2					
					If d\locked Then
						Msg = "你将钥匙卡插入了卡槽但无事发生"
					Else
						Msg = "你需要"+d\KeyCard+"级或更高级别的钥匙卡来操作这扇门"
					EndIf
					MsgTimer = 70 * 7					
				EndIf
				Return
			End If
		EndIf	
	ElseIf d\KeyCard < 0
		;I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable - risingstar64
		If SelectedItem <> Null Then
			temp = (SelectedItem\itemtemplate\tempname = "hand" And d\KeyCard=-1) Or (SelectedItem\itemtemplate\tempname = "hand2" And d\KeyCard=-2)
		EndIf
		SelectedItem = Null
		If temp <> 0 Then
			PlaySound_Strict ScannerSFX1
			If (Instr(Msg,"你将手掌")=0) Or (MsgTimer < 70*3) Then
				Msg = "你将手掌放在了扫描仪上。扫描仪显示：“DNA验证通过，允许访问。”"
			EndIf
			MsgTimer = 70 * 10
		Else
			If showmsg = True Then 
				PlaySound_Strict ScannerSFX2
				Msg = "你将手掌放在了扫描仪上。扫描仪显示：“未知DNA序列，拒绝访问。”"
				MsgTimer = 70 * 10
			EndIf
			Return			
		EndIf
	Else
		If d\locked Then
			If showmsg = True Then 
				If Not (d\IsElevatorDoor>0) Then
					PlaySound_Strict ButtonSFX2
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
                        If d\open Then
                            Msg = "你按下了按钮但无事发生"
                        Else    
                            Msg = "门看起来被锁上了"
                        EndIf    
                    Else
                        Msg = "电梯似乎坏了"
                    EndIf
					MsgTimer = 70 * 5
				Else
					If d\IsElevatorDoor = 1 Then
						Msg = "你呼叫了电梯"
						MsgTimer = 70 * 5
					ElseIf d\IsElevatorDoor = 3 Then
						Msg = "电梯已经在这层了"
						MsgTimer = 70 * 5
					ElseIf (Msg<>"你呼叫了电梯")
						If (Msg="你已经呼叫了电梯") Or (MsgTimer<70*3)	
							Select Rand(10)
								Case 1
									Msg = "不要再按按钮了"
									MsgTimer = 70 * 7
								Case 2
									Msg = "一直按按钮不会让电梯来得更快"
									MsgTimer = 70 * 7
								Case 3
									Msg = "如果你再按按钮我就让游戏内存访问冲突"
									MsgTimer = 70 * 7
								Default
									Msg = "你已经呼叫了电梯"
									MsgTimer = 70 * 7
							End Select
						EndIf
					Else
						Msg = "你已经呼叫了电梯"
						MsgTimer = 70 * 7
					EndIf
				EndIf
				
			EndIf
			Return
		EndIf	
	EndIf
	
	d\open = (Not d\open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\open = (Not d\LinkedDoor\open)
	
	Local sound = 0
	If d\dir = 1 Then sound=Rand(0, 1) Else sound=Rand(0, 2)
	
	If playsfx=True Then
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
			d\SoundCHN = PlaySound2 (OpenDoorSFX(d\dir, sound), Camera, d\obj)
		Else
			d\SoundCHN = PlaySound2 (CloseDoorSFX(d\dir, sound), Camera, d\obj)
		EndIf
		UpdateSoundOrigin(d\SoundCHN,Camera,d\obj)
	Else
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
		EndIf
	EndIf
	
End Function

Function RemoveDoor(d.Doors)
	If d\buttons[0] <> 0 Then EntityParent d\buttons[0], 0
	If d\buttons[1] <> 0 Then EntityParent d\buttons[1], 0	
	
	If d\obj <> 0 Then FreeEntity d\obj
	If d\obj2 <> 0 Then FreeEntity d\obj2
	If d\frameobj <> 0 Then FreeEntity d\frameobj
	If d\buttons[0] <> 0 Then FreeEntity d\buttons[0]
	If d\buttons[1] <> 0 Then FreeEntity d\buttons[1]
	
	Delete d
End Function

DrawLoading(40,True)

Include "MapSystem.bb"

DrawLoading(80,True)

Include "NPCs.bb"

;-------------------------------------  Events --------------------------------------------------------------

Type Events
	Field EventName$
	Field room.Rooms
	
	Field EventState#, EventState2#, EventState3#
	Field SoundCHN%, SoundCHN2%
	Field Sound, Sound2
	Field SoundCHN_isStream%, SoundCHN2_isStream%
	
	Field EventStr$
	
	Field img%
End Type 

Function CreateEvent.Events(eventname$, roomname$, id%, prob# = 0.0)
	;roomname = the name of the room(s) you want the event to be assigned to
	
	;the id-variable determines which of the rooms the event is assigned to,
	;0 will assign it to the first generated room, 1 to the second, etc
	
	;the prob-variable can be used to randomly assign events into some rooms
	;0.5 means that there's a 50% chance that event is assigned to the rooms
	;1.0 means that the event is assigned to every room
	;the id-variable is ignored if prob <> 0.0
	
	Local i% = 0, temp%, e.Events, e2.Events, r.Rooms
	
	If prob = 0.0 Then
		For r.Rooms = Each Rooms
			If (roomname = "" Or roomname = r\RoomTemplate\Name) Then
				temp = False
				For e2.Events = Each Events
					If e2\room = r Then temp = True : Exit
				Next
				
				i=i+1
				If i >= id And temp = False Then
					e.Events = New Events
					e\EventName = eventname					
					e\room = r
					Return e
				End If
			EndIf
		Next
	Else
		For r.Rooms = Each Rooms
			If (roomname = "" Or roomname = r\RoomTemplate\Name) Then
				temp = False
				For e2.Events = Each Events
					If e2\room = r Then temp = True : Exit
				Next
				
				If Rnd(0.0, 1.0) < prob And temp = False Then
					e.Events = New Events
					e\EventName = eventname					
					e\room = r
				End If
			EndIf
		Next		
	EndIf
	
	Return Null
End Function

Function InitEvents()
	Local e.Events
	
	CreateEvent("173", "173", 0)
	CreateEvent("alarm", "start", 0)
	
	CreateEvent("pocketdimension", "pocketdimension", 0)	
	
	;there's a 7% chance that 106 appears in the rooms named "tunnel"
	CreateEvent("tunnel106", "tunnel", 0, 0.07 + (0.1*SelectedDifficulty\aggressiveNPCs))
	
	;the chance for 173 appearing in the first lockroom is about 66%
	;there's a 30% chance that it appears in the later lockrooms
	If Rand(3)<3 Then CreateEvent("lockroom173", "lockroom", 0)
	CreateEvent("lockroom173", "lockroom", 0, 0.3 + (0.5*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2trick", "room2", 0, 0.15)	
	
	CreateEvent("1048a", "room2", 0, 1.0)	
	
	CreateEvent("room2storage", "room2storage", 0)	
	
	;096 spawns in the first (and last) lockroom2
	CreateEvent("lockroom096", "lockroom2", 0)
	
	CreateEvent("endroom106", "endroom", Rand(0,1))
	
	CreateEvent("room2poffices2", "room2poffices2", 0)
	
	CreateEvent("room2fan", "room2_2", 0, 1.0)
	
	CreateEvent("room2elevator2", "room2elevator", 0)
	CreateEvent("room2elevator", "room2elevator", Rand(1,2))
	
	CreateEvent("room3storage", "room3storage", 0, 0)
	
	CreateEvent("tunnel2smoke", "tunnel2", 0, 0.2)
	CreateEvent("tunnel2", "tunnel2", Rand(0,2), 0)
	CreateEvent("tunnel2", "tunnel2", 0, (0.2*SelectedDifficulty\aggressiveNPCs))
	
	;173 appears in half of the "room2doors" -rooms
	CreateEvent("room2doors173", "room2doors", 0, 0.5 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	;the anomalous duck in room2offices2-rooms
	CreateEvent("room2offices2", "room2offices2", 0, 0.7)
	
	CreateEvent("room2closets", "room2closets", 0)	
	
	CreateEvent("room2cafeteria", "room2cafeteria", 0)	
	
	CreateEvent("room3pitduck", "room3pit", 0)
	CreateEvent("room3pit1048", "room3pit", 1)
	
	;the event that causes the door to open by itself in room2offices3
	CreateEvent("room2offices3", "room2offices3", 0, 1.0)	
	
	CreateEvent("room2servers", "room2servers", 0)	
	
	CreateEvent("room3servers", "room3servers", 0)	
	CreateEvent("room3servers", "room3servers2", 0)
	
	;the dead guard
	CreateEvent("room3tunnel","room3tunnel", 0, 0.08)
	
	CreateEvent("room4","room4", 0)
	
	If Rand(5)<5 Then 
		Select Rand(3)
			Case 1
				CreateEvent("682roar", "tunnel", Rand(0,2), 0)	
			Case 2
				CreateEvent("682roar", "room3pit", Rand(0,2), 0)		
			Case 3
				;CreateEvent("682roar", "room2offices", 0, 0)
				CreateEvent("682roar", "room2z3", 0, 0)
		End Select 
	EndIf 
	
	CreateEvent("testroom173", "room2testroom2", 0, 1.0)	
	
	CreateEvent("room2tesla", "room2tesla", 0, 0.9)
	
	CreateEvent("room2nuke", "room2nuke", 0, 0)
	
	If Rand(5) < 5 Then 
		CreateEvent("coffin106", "coffin", 0, 0)
	Else
		CreateEvent("coffin", "coffin", 0, 0)
	EndIf 
	
	CreateEvent("checkpoint", "checkpoint1", 0, 1.0)
	CreateEvent("checkpoint", "checkpoint2", 0, 1.0)
	
	CreateEvent("room3door", "room3", 0, 0.1)
	CreateEvent("room3door", "room3tunnel", 0, 0.1)	
	
	If Rand(2)=1 Then
		CreateEvent("106victim", "room3", Rand(1,2))
		CreateEvent("106sinkhole", "room3_2", Rand(2,3))
	Else
		CreateEvent("106victim", "room3_2", Rand(1,2))
		CreateEvent("106sinkhole", "room3", Rand(2,3))
	EndIf
	CreateEvent("106sinkhole", "room4", Rand(1,2))
	
	CreateEvent("room079", "room079", 0, 0)	
	
	CreateEvent("room049", "room049", 0, 0)
	
	CreateEvent("room012", "room012", 0, 0)
	
	CreateEvent("room035", "room035", 0, 0)
	
	CreateEvent("008", "008", 0, 0)
	
	CreateEvent("room106", "room106", 0, 0)	
	
	CreateEvent("pj", "roompj", 0, 0)
	
	CreateEvent("914", "914", 0, 0)
	
	CreateEvent("buttghost", "room2toilets", 0, 0)
	CreateEvent("toiletguard", "room2toilets", 1, 0)
	
	CreateEvent("room2pipes106", "room2pipes", Rand(0, 3)) 
	
	CreateEvent("room2pit", "room2pit", 0, 0.4 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("testroom", "testroom", 0)
	
	CreateEvent("room2tunnel", "room2tunnel", 0)
	
	CreateEvent("room2ccont", "room2ccont", 0)
	
	CreateEvent("gateaentrance", "gateaentrance", 0)
	CreateEvent("gatea", "gatea", 0)	
	CreateEvent("exit1", "exit1", 0)
	
	CreateEvent("room205", "room205", 0)
	
	CreateEvent("room860","room860", 0)
	
	CreateEvent("room966","room966", 0)
	
	CreateEvent("room1123", "room1123", 0, 0)
	CreateEvent("room2tesla", "room2tesla_lcz", 0, 0.9)
	CreateEvent("room2tesla", "room2tesla_hcz", 0, 0.9)
	
	;New Events in SCP:CB Version 1.3 - ENDSHN
	CreateEvent("room4tunnels","room4tunnels",0)
	CreateEvent("room_gw","room2gw",0,1.0)
	CreateEvent("dimension1499","dimension1499",0)
	CreateEvent("room1162","room1162",0)
	CreateEvent("room2scps2","room2scps2",0)
	CreateEvent("room_gw","room3gw",0,1.0)
	CreateEvent("room2sl","room2sl",0)
	CreateEvent("medibay","medibay",0)
	CreateEvent("room2shaft","room2shaft",0)
	CreateEvent("room1lifts","room1lifts",0)
	
	CreateEvent("room2gw_b","room2gw_b",Rand(0,1))
	
	CreateEvent("096spawn","room4pit",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room3pit",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room2pipes",0,0.4+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room2pit",0,0.5+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room3tunnel",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room4tunnels",0,0.7+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","tunnel",0,0.6+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","tunnel2",0,0.4+(0.2*SelectedDifficulty\aggressiveNPCs))
	CreateEvent("096spawn","room3z2",0,0.7+(0.2*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2pit","room2_4",0,0.4 + (0.4*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room2offices035","room2offices",0)
	
	CreateEvent("room2pit106", "room2pit", 0, 0.07 + (0.1*SelectedDifficulty\aggressiveNPCs))
	
	CreateEvent("room1archive", "room1archive", 0, 1.0)
	
End Function

Include "UpdateEvents.bb"

Function RemoveEvent(e.Events)
	If e\Sound<>0 Then FreeSound_Strict e\Sound
	If e\Sound2<>0 Then FreeSound_Strict e\Sound2
	If e\img<>0 Then FreeImage e\img
	Delete e
End Function

Collisions HIT_PLAYER, HIT_MAP, 2, 2
Collisions HIT_PLAYER, HIT_PLAYER, 1, 3
Collisions HIT_ITEM, HIT_MAP, 2, 2
Collisions HIT_APACHE, HIT_APACHE, 1, 2
Collisions HIT_178, HIT_MAP, 2, 2
Collisions HIT_178, HIT_178, 1, 3
Collisions HIT_DEAD, HIT_MAP, 2, 2

DrawLoading(90, True)

;----------------------------------- meshes and textures ----------------------------------------------------------------

Global FogTexture%, Fog%
Global GasMaskTexture%, GasMaskOverlay%
Global InfectTexture%, InfectOverlay%
Global DarkTexture%, Dark%
Global Collider%, Head%

Global FogNVTexture%
Global NVTexture%, NVOverlay%

Global TeslaTexture%

Global LightTexture%, Light%
Dim LightSpriteTex%(5)
Global DoorOBJ%, DoorFrameOBJ%

Global LeverOBJ%, LeverBaseOBJ%

Global DoorColl%
Global ButtonOBJ%, ButtonKeyOBJ%, ButtonCodeOBJ%, ButtonScannerOBJ%

Dim DecalTextures%(20)

Global Monitor%, MonitorTexture%
Global CamBaseOBJ%, CamOBJ%

Global LiquidObj%,MTFObj%,GuardObj%,ClassDObj%
Global ApacheObj%,ApacheRotorObj%

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global Save_MSG$ = ""
Global Save_MSG_Timer# = 0.0
Global Save_MSG_Y# = 0.0

Global MTF_CameraCheckTimer# = 0.0
Global MTF_CameraCheckDetected% = False

;---------------------------------------------------------------------------------------------------

Include "menu.bb"
MainMenuOpen = True

;---------------------------------------------------------------------------------------------------

FlushKeys()
FlushMouse()

DrawLoading(100, True)

LoopDelay = MilliSecs()

Global UpdateParticles_Time# = 0.0

Global CurrTrisAmount%

Global Input_ResetTime# = 0

Type SCP427
	Field Using%
	Field Timer#
	Field Sound[1]
	Field SoundCHN[1]
End Type

Global I_427.SCP427 = New SCP427

Type MapZones
	Field Transition%[1]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones = New MapZones


;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------       		MAIN LOOP                 ---------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

Repeat
	Cls
	
	CurTime = MilliSecs()
	ElapsedTime = (CurTime - PrevTime) / 1000.0
	PrevTime = CurTime
	PrevFPSFactor = FPSfactor
	FPSfactor = Max(Min(ElapsedTime * 70, 5.0), 0.2)
	FPSfactor2 = FPSfactor
	
	If MenuOpen Or InvOpen Or OtherOpen<>Null Or ConsoleOpen Or SelectedDoor <> Null Or SelectedScreen <> Null Or Using294 Then FPSfactor = 0
	
	If Framelimit > 0 Then
	    ;Framelimit
		Local WaitingTime% = (1000.0 / Framelimit) - (MilliSecs() - LoopDelay)
		Delay WaitingTime%
		
		LoopDelay = MilliSecs()
	EndIf
	
	;Counting the fps
	If CheckFPS < MilliSecs() Then
		FPS = ElapsedLoops
		ElapsedLoops = 0
		CheckFPS = MilliSecs()+1000
	EndIf
	ElapsedLoops = ElapsedLoops + 1
	
	If Input_ResetTime<=0.0
		DoubleClick = False
		MouseHit1 = MouseHit(1)
		If MouseHit1 Then
			If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
			LastMouseHit1 = MilliSecs()
		EndIf
		
		Local prevmousedown1 = MouseDown1
		MouseDown1 = MouseDown(1)
		If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
		
		MouseHit2 = MouseHit(2)
		
		If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
	Else
		Input_ResetTime = Max(Input_ResetTime-FPSfactor,0.0)
	EndIf
	
	UpdateMusic()
	If EnableSFXRelease Then AutoReleaseSounds()
	
	If MainMenuOpen Then
		If ShouldPlay = 21 Then
			EndBreathSFX = LoadSound("SFX\Ending\MenuBreath.ogg")
			EndBreathCHN = PlaySound(EndBreathSFX)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66
			If (Not ChannelPlaying(EndBreathCHN)) Then
				FreeSound(EndBreathSFX)
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		UpdateMainMenu()
	Else
		UpdateStreamSounds()
		
		ShouldPlay = Min(PlayerZone,2)
		
		DrawHandIcon = False
		
		RestoreSanity = True
		ShouldEntitiesFall = True
		
		If FPSfactor > 0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then UpdateSecurityCams()
		
		If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And PlayerRoom\RoomTemplate\Name <> "exit1" And (Not MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then 
			
			If Rand(1500) = 1 Then
				For i = 0 To 5
					If AmbientSFX(i,CurrAmbientSFX)<>0 Then
						If ChannelPlaying(AmbientSFXCHN)=0 Then FreeSound_Strict AmbientSFX(i,CurrAmbientSFX) : AmbientSFX(i,CurrAmbientSFX) = 0
					EndIf			
				Next
				
				PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
				
				If Rand(3)=1 Then PlayerZone = 3
				
				If PlayerRoom\RoomTemplate\Name = "173" Then 
					PlayerZone = 4
				ElseIf PlayerRoom\RoomTemplate\Name = "room860"
					For e.Events = Each Events
						If e\EventName = "room860"
							If e\EventState = 1.0
								PlayerZone = 5
								PositionEntity (SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
							EndIf
							
							Exit
						EndIf
					Next
				EndIf
				
				CurrAmbientSFX = Rand(0,AmbientSFXAmount(PlayerZone)-1)
				
				Select PlayerZone
					Case 0,1,2
						If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg")
					Case 3
						If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\General\ambient"+(CurrAmbientSFX+1)+".ogg")
					Case 4
						If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Pre-breach\ambient"+(CurrAmbientSFX+1)+".ogg")
					Case 5
						If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Forest\ambient"+(CurrAmbientSFX+1)+".ogg")
				End Select
				
				AmbientSFXCHN = PlaySound2(AmbientSFX(PlayerZone,CurrAmbientSFX), Camera, SoundEmitter)
			EndIf
			UpdateSoundOrigin(AmbientSFXCHN,Camera, SoundEmitter)
			
			If Rand(50000) = 3 Then
				Local RN$ = PlayerRoom\RoomTemplate\Name$
				If RN$ <> "room860" And RN$ <> "room1123" And RN$ <> "173" And RN$ <> "dimension1499" Then
					If FPSfactor > 0 Then LightBlink = Rnd(1.0,2.0)
					PlaySound_Strict  LoadTempSound("SFX\SCP\079\Broadcast"+Rand(1,7)+".ogg")
				EndIf 
			EndIf
		EndIf
		
		UpdateCheckpoint1 = False
		UpdateCheckpoint2 = False
		
		If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And EndingTimer=>0 Then
			LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
			CameraFogRange(Camera, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
			CameraFogColor(Camera, 0,0,0)
			CameraFogMode Camera,1
			CameraRange(Camera, 0.05, Min(CameraFogFar*LightVolume*1.5,28))	
			If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then
				CameraClsColor(Camera, 0,0,0)
			EndIf
			
			AmbientLight Brightness, Brightness, Brightness	
			PlayerSoundVolume = CurveValue(0.0, PlayerSoundVolume, 5.0)
			
			CanSave% = True
			UpdateDeafPlayer()
			UpdateEmitters()
			MouseLook()
			If PlayerRoom\RoomTemplate\Name = "dimension1499" And QuickLoadPercent > 0 And QuickLoadPercent < 100
				ShouldEntitiesFall = False
			EndIf
			MovePlayer()
			InFacility = CheckForPlayerInFacility()
			If PlayerRoom\RoomTemplate\Name = "dimension1499"
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
					UpdateDimension1499()
				EndIf
				UpdateLeave1499()
			ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or (PlayerRoom\RoomTemplate\Name="exit1" And EntityY(Collider)>1040.0*RoomScale)
				UpdateDoors()
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
					UpdateEndings()
				EndIf
				UpdateScreens()
				UpdateRoomLights(Camera)
			Else
				UpdateDoors()
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
					UpdateEvents()
				EndIf
				UpdateScreens()
				TimeCheckpointMonitors()
				Update294()
				UpdateRoomLights(Camera)
			EndIf
			UpdateDecals()
			UpdateMTF()
			UpdateNPCs()
			UpdateItems()
			UpdateParticles()
			Use427()
			UpdateMonitorSaving()
			;Added a simple code for updating the Particles function depending on the FPSFactor (still WIP, might not be the final version of it) - ENDSHN
			UpdateParticles_Time# = Min(1,UpdateParticles_Time#+FPSfactor)
			If UpdateParticles_Time#=1
				UpdateDevilEmitters()
				UpdateParticles_Devil()
				UpdateParticles_Time#=0
			EndIf
		EndIf
		
		If InfiniteStamina% Then Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
		
		If FPSfactor=0
			UpdateWorld(0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
		EndIf
		RenderWorld2()
		
		BlurVolume = Min(CurveValue(0.0, BlurVolume, 20.0),0.95)
		If BlurTimer > 0.0 Then
			BlurVolume = Max(Min(0.95, BlurTimer / 1000.0), BlurVolume)
			BlurTimer = Max(BlurTimer - FPSfactor, 0.0)
		End If
		
		UpdateBlur(BlurVolume)
		
		;[Block]
		
		Local darkA# = 0.0
		If (Not MenuOpen)  Then
			If Sanity < 0 Then
				If RestoreSanity Then Sanity = Min(Sanity + FPSfactor, 0.0)
				If Sanity < (-200) Then 
					darkA = Max(Min((-Sanity - 200) / 700.0, 0.6), darkA)
					If KillTimer => 0 Then 
						HeartBeatVolume = Min(Abs(Sanity+200)/500.0,1.0)
						HeartBeatRate = Max(70 + Abs(Sanity+200)/6.0,HeartBeatRate)
					EndIf
				EndIf
			End If
			
			If EyeStuck > 0 Then 
				BlinkTimer = BLINKFREQ
				EyeStuck = Max(EyeStuck-FPSfactor,0)
				
				If EyeStuck < 9000 Then BlurTimer = Max(BlurTimer, (9000-EyeStuck)*0.5)
				If EyeStuck < 6000 Then darkA = Min(Max(darkA, (6000-EyeStuck)/5000.0),1.0)
				If EyeStuck < 9000 And EyeStuck+FPSfactor =>9000 Then 
					Msg = "眼药水让你的眼睛开始流泪"
					MsgTimer = 70*6
				EndIf
			EndIf
			
			If BlinkTimer < 0 Then
				If BlinkTimer > - 5 Then
					darkA = Max(darkA, Sin(Abs(BlinkTimer * 18.0)))
				ElseIf BlinkTimer > - 15
					darkA = 1.0
				Else
					darkA = Max(darkA, Abs(Sin(BlinkTimer * 18.0)))
				EndIf
				
				If BlinkTimer <= - 20 Then
					;Randomizes the frequency of blinking. Scales with difficulty.
					Select SelectedDifficulty\otherFactors
						Case EASY
							BLINKFREQ = Rnd(490,700)
						Case NORMAL
							BLINKFREQ = Rnd(455,665)
						Case HARD
							BLINKFREQ = Rnd(420,630)
					End Select 
					BlinkTimer = BLINKFREQ
				EndIf
				
				BlinkTimer = BlinkTimer - FPSfactor
			Else
				BlinkTimer = BlinkTimer - FPSfactor * 0.6 * BlinkEffect
				If EyeIrritation > 0 Then BlinkTimer=BlinkTimer-Min(EyeIrritation / 100.0 + 1.0, 4.0) * FPSfactor
				
				darkA = Max(darkA, 0.0)
			End If
			
			EyeIrritation = Max(0, EyeIrritation - FPSfactor)
			
			If BlinkEffectTimer > 0 Then
				BlinkEffectTimer = BlinkEffectTimer - (FPSfactor/70)
			Else
				If BlinkEffect <> 1.0 Then BlinkEffect = 1.0
			EndIf
			
			LightBlink = Max(LightBlink - (FPSfactor / 35.0), 0)
			If LightBlink > 0 Then darkA = Min(Max(darkA, LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If Using294 Then darkA=1.0
			
			If (Not WearingNightVision) Then darkA = Max((1.0-SecondaryLightOn)*0.9, darkA)
			
			If KillTimer < 0 Then
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(KillTimer*5)
				KillTimer=KillTimer-(FPSfactor*0.8)
				If KillTimer < - 360 Then 
					MenuOpen = True 
					If SelectedEnding <> "" Then EndingTimer = Min(KillTimer,-0.1)
				EndIf
				darkA = Max(darkA, Min(Abs(KillTimer / 400.0), 1.0))
			EndIf
			
			If FallTimer < 0 Then
				If SelectedItem <> Null Then
					If Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit") Or Instr(SelectedItem\itemtemplate\tempname,"vest") Then
						If WearingHazmat=0 And WearingVest=0 Then
							DropItem(SelectedItem)
						EndIf
					EndIf
				EndIf
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(FallTimer*10)
				FallTimer = FallTimer-FPSfactor
				darkA = Max(darkA, Min(Abs(FallTimer / 400.0), 1.0))				
			EndIf
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\tempname = "navigator" Or SelectedItem\itemtemplate\tempname = "nav" Then darkA = Max(darkA, 0.5)
			End If
			If SelectedScreen <> Null Then darkA = Max(darkA, 0.5)
			
			EntityAlpha(Dark, darkA)	
		EndIf
		
		If LightFlash > 0 Then
			ShowEntity Light
			EntityAlpha(Light, Max(Min(LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
			LightFlash = Max(LightFlash - (FPSfactor / 70.0), 0)
		Else
			HideEntity Light
			;EntityAlpha(Light, LightFlash)
		EndIf
		
		EntityColor Light,255,255,255
		
		;[End block]
		
		If KeyHit(KEY_INV) And VomitTimer >= 0 Then
			If (Not UnableToMove) And (Not IsZombie) And (Not Using294) Then
				Local W$ = ""
				Local V# = 0
				If SelectedItem<>Null
					W$ = SelectedItem\itemtemplate\tempname
					V# = SelectedItem\state
				EndIf
				If (W<>"vest" And W<>"finevest" And W<>"hazmatsuit" And W<>"hazmatsuit2" And W<>"hazmatsuit3") Or V=0 Or V=100
					If InvOpen Then
						ResumeSounds()
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
					Else
						PauseSounds()
					EndIf
					InvOpen = Not InvOpen
					If OtherOpen<>Null Then OtherOpen=Null
					SelectedItem = Null
				EndIf
			EndIf
		EndIf
		
		If KeyHit(KEY_SAVE) Then
			If SelectedDifficulty\saveType = SAVEANYWHERE Then
				RN$ = PlayerRoom\RoomTemplate\Name$
				If RN$ = "173" Or (RN$ = "exit1" And EntityY(Collider)>1040.0*RoomScale) Or RN$ = "gatea"
					Msg = "你不能在此处保存游戏"
					MsgTimer = 70 * 4
				ElseIf (Not CanSave) Or QuickLoadPercent > -1
					Msg = "你不能在此时保存游戏"
					MsgTimer = 70 * 4
					If QuickLoadPercent > -1
						Msg = Msg + " （游戏正在加载）"
					EndIf
				Else
					SaveGame(SavePath + CurrSave + "\")
				EndIf
			ElseIf SelectedDifficulty\saveType = SAVEONSCREENS
				If SelectedScreen=Null And SelectedMonitor=Null Then
					Msg = "只能在设施中可互动显示器的位置进行保存"
					MsgTimer = 70 * 4
				Else
					RN$ = PlayerRoom\RoomTemplate\Name$
					If RN$ = "173" Or (RN$ = "exit1" And EntityY(Collider)>1040.0*RoomScale) Or RN$ = "gatea"
						Msg = "你不能在此处保存游戏"
						MsgTimer = 70 * 4
					ElseIf (Not CanSave) Or QuickLoadPercent > -1
						Msg = "你不能在此时保存游戏"
						MsgTimer = 70 * 4
						If QuickLoadPercent > -1
							Msg = Msg + " （游戏正在加载）"
						EndIf
					Else
						If SelectedScreen<>Null
							GameSaved = False
							Playable = True
							DropSpeed = 0
						EndIf
						SaveGame(SavePath + CurrSave + "\")
					EndIf
				EndIf
			Else
				Msg = "快速保存已禁用"
				MsgTimer = 70 * 4
			EndIf
		Else If SelectedDifficulty\saveType = SAVEONSCREENS And (SelectedScreen<>Null Or SelectedMonitor<>Null)
			If ((Msg <> "游戏已保存") And ((Msg <> "你无法在此处保存游戏") And (Msg <> "你不能在此时保存游戏"))) Or (MsgTimer <= 0) Then
				Msg = "按 "+KeyName(KEY_SAVE)+" 保存"
				MsgTimer = 70*4
			EndIf
			
			If MouseHit2 Then SelectedMonitor = Null
		EndIf
		
		If KeyHit(KEY_CONSOLE) Then
			If CanOpenConsole
				If ConsoleOpen Then
					UsedConsole = True
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		DrawGUI()
		
		UpdateSubtitles() ;字幕系统
		UpdateConsole()
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "173" Then
				For e.Events = Each Events
					If e\EventName = "173" Then
						If e\EventState3 => 40 And e\EventState3 < 50 Then
							If InvOpen Then
								Msg = "双击文档来阅读"
								MsgTimer=70*7
								e\EventState3 = 50
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		If MsgTimer > 0 Then
			Local temp% = False
			If (Not InvOpen%)
				If SelectedItem <> Null
					If SelectedItem\itemtemplate\tempname = "paper" Or SelectedItem\itemtemplate\tempname = "oldpaper"
						temp% = True
					EndIf
				EndIf
			EndIf
			
			If (Not temp%)
				Color 0,0,0
				Text((GraphicWidth / 2)+1, (GraphicHeight / 2) + 201, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
				Color 255,255,255
				Text((GraphicWidth / 2), (GraphicHeight / 2) + 200, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
			Else
				Color 0,0,0
				Text((GraphicWidth / 2)+1, (GraphicHeight * 0.94) + 1, Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
				Color 255,255,255
				Text((GraphicWidth / 2), (GraphicHeight * 0.94), Msg, True, False, Min(MsgTimer / 2, 255)/255.0)
			EndIf
			MsgTimer=MsgTimer-FPSfactor2 
		End If
		
		Color 255, 255, 255
		If ShowFPS Then SetFont ConsoleFont : Text 20, 20, "FPS: " + FPS : SetFont Font1
		
		If EndingTimer < 0 Then
			If SelectedEnding <> "" Then DrawEnding()
		Else
			DrawMenu()			
		EndIf
		
		RenderSubtitles()
		DrawQuickLoading()
		
		UpdateAchievementMsg()
	End If
	
	If BorderlessWindowed Then
		If (RealGraphicWidth<>GraphicWidth) Or (RealGraphicHeight<>GraphicHeight) Then
			SetBuffer TextureBuffer(fresize_texture)
			ClsColor 0,0,0 : Cls
			CopyRect 0,0,GraphicWidth,GraphicHeight,1024-GraphicWidth/2,1024-GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			SetBuffer BackBuffer()
			ClsColor 0,0,0 : Cls
			ScaleRender(0,0,2050.0 / Float(GraphicWidth) * AspectRatioRatio, 2050.0 / Float(GraphicWidth) * AspectRatioRatio)
			;might want to replace Float(GraphicWidth) with Max(GraphicWidth,GraphicHeight) if portrait sizes cause issues
			;everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	;not by any means a perfect solution
	;Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If ScreenGamma>1.0 Then
		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		EntityBlend fresize_image,1
		ClsColor 0,0,0 : Cls
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		EntityFX fresize_image,1+32
		EntityBlend fresize_image,3
		EntityAlpha fresize_image,ScreenGamma-1.0
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
	ElseIf ScreenGamma<1.0 Then ;todo: maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		EntityBlend fresize_image,1
		ClsColor 0,0,0 : Cls
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		EntityFX fresize_image,1+32
		EntityBlend fresize_image,2
		EntityAlpha fresize_image,1.0
		SetBuffer TextureBuffer(fresize_texture2)
		ClsColor 255*ScreenGamma,255*ScreenGamma,255*ScreenGamma
		Cls
		SetBuffer BackBuffer()
		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		SetBuffer(TextureBuffer(fresize_texture2))
		ClsColor 0,0,0
		Cls
		SetBuffer(BackBuffer())
	EndIf
	EntityFX fresize_image,1
	EntityBlend fresize_image,1
	EntityAlpha fresize_image,1.0
	
	CatchErrors("Main loop / uncaught")
	
	Flip Vsync
Forever

;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

Function QuickLoadEvents()
	CatchErrors("Uncaught (QuickLoadEvents)")
	
	If QuickLoad_CurrEvent = Null Then
		QuickLoadPercent = -1
		Return
	EndIf
	
	Local e.Events = QuickLoad_CurrEvent
	
	Local r.Rooms,sc.SecurityCams,sc2.SecurityCams,scale#,pvt%,n.NPCs,tex%,i%,x#,z#
	
	;might be a good idea to use QuickLoadPercent to determine the "steps" of the loading process 
	;instead of magic values in e\eventState and e\eventStr

	Select e\EventName
		Case "room2sl"
			;[Block]
			If e\EventState = 0 And e\EventStr <> ""
				If e\EventStr <> "" And Left(e\EventStr,4) <> "load"
					QuickLoadPercent = QuickLoadPercent + 5
					If Int(e\EventStr) > 9
						e\EventStr = "load2"
					Else
						e\EventStr = Int(e\EventStr) + 1
					EndIf
				ElseIf e\EventStr = "load2"
					;For SCP-049
					Local skip = False
					If e\room\NPC[0]=Null Then
						For n.NPCs = Each NPCs
							If n\NPCtype = NPCtype049
								skip = True
								Exit
							EndIf
						Next
						
						If (Not skip)
							e\room\NPC[0] = CreateNPC(NPCtype049,EntityX(e\room\Objects[7],True),EntityY(e\room\Objects[7],True)+5,EntityZ(e\room\Objects[7],True))
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity e\room\NPC[0]\Collider,EntityX(e\room\Objects[7],True),EntityY(e\room\Objects[7],True)+5,EntityZ(e\room\Objects[7],True)
							ResetEntity e\room\NPC[0]\Collider
							RotateEntity e\room\NPC[0]\Collider,0,e\room\angle+180,0
							e\room\NPC[0]\State = 0
							e\room\NPC[0]\PrevState = 2
							
							DebugLog(EntityX(e\room\Objects[7],True)+", "+EntityY(e\room\Objects[7],True)+", "+EntityZ(e\room\Objects[7],True))
						Else
							DebugLog "Skipped 049 spawning in room2sl"
						EndIf
					EndIf
					QuickLoadPercent = 80
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					e\EventState = 1
					If e\EventState2 = 0 Then e\EventState2 = -(70*5)
					
					QuickLoadPercent = 100
				EndIf
			EndIf
			;[End Block]
		Case "room2closets"
			;[Block]
			If e\EventState = 0
				If e\EventStr = "load0"
					QuickLoadPercent = 10
					If e\room\NPC[0]=Null Then
						e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[0],True),EntityY(e\room\Objects[0],True),EntityZ(e\room\Objects[0],True))
					EndIf
					
					ChangeNPCTextureID(e\room\NPC[0],4)
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					QuickLoadPercent = 20
					e\room\NPC[0]\Sound=LoadSound_Strict("SFX\Room\Storeroom\Escape1.ogg")
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					QuickLoadPercent = 35
					e\room\NPC[0]\SoundChn = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 12)
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					QuickLoadPercent = 55
					If e\room\NPC[1]=Null Then
						e\room\NPC[1] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[1],True),EntityY(e\room\Objects[1],True),EntityZ(e\room\Objects[1],True))
					EndIf
					
					ChangeNPCTextureID(e\room\NPC[1],2)
					e\EventStr = "load4"
				ElseIf e\EventStr = "load4"
					QuickLoadPercent = 80
					e\room\NPC[1]\Sound=LoadSound_Strict("SFX\Room\Storeroom\Escape2.ogg")
					e\EventStr = "load5"
				ElseIf e\EventStr = "load5"
					QuickLoadPercent = 100
					PointEntity e\room\NPC[0]\Collider, e\room\NPC[1]\Collider
					PointEntity e\room\NPC[1]\Collider, e\room\NPC[0]\Collider
					
					e\EventState=1
				EndIf
			EndIf
			;[End Block]
		Case "room3storage"
			;[Block]
			If e\room\NPC[0]=Null Then
				e\room\NPC[0]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 20
			ElseIf e\room\NPC[1]=Null Then
				e\room\NPC[1]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 50
			ElseIf e\room\NPC[2]=Null Then
				e\room\NPC[2]=CreateNPC(NPCtype939, 0,0,0)
				QuickLoadPercent = 100
			Else
				If QuickLoadPercent > -1 Then QuickLoadPercent = 100
			EndIf
			;[End Block]
		Case "room049"
			;[Block]
			If e\EventState = 0 Then
				If e\EventStr = "load0"
					n.NPCs = CreateNPC(NPCtypeZombie, EntityX(e\room\Objects[4],True),EntityY(e\room\Objects[4],True),EntityZ(e\room\Objects[4],True))
					PointEntity n\Collider, e\room\obj
					TurnEntity n\Collider, 0, 190, 0
					QuickLoadPercent = 20
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					n.NPCs = CreateNPC(NPCtypeZombie, EntityX(e\room\Objects[5],True),EntityY(e\room\Objects[5],True),EntityZ(e\room\Objects[5],True))
					PointEntity n\Collider, e\room\obj
					TurnEntity n\Collider, 0, 20, 0
					QuickLoadPercent = 60
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype049
							e\room\NPC[0]=n
							e\room\NPC[0]\State = 2
							e\room\NPC[0]\Idle = 1
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity e\room\NPC[0]\Collider,EntityX(e\room\Objects[4],True),EntityY(e\room\Objects[4],True)+3,EntityZ(e\room\Objects[4],True)
							ResetEntity e\room\NPC[0]\Collider
							Exit
						EndIf
					Next
					If e\room\NPC[0]=Null
						n.NPCs = CreateNPC(NPCtype049, EntityX(e\room\Objects[4],True), EntityY(e\room\Objects[4],True)+3, EntityZ(e\room\Objects[4],True))
						PointEntity n\Collider, e\room\obj
						n\State = 2
						n\Idle = 1
						n\HideFromNVG = True
						e\room\NPC[0]=n
					EndIf
					QuickLoadPercent = 100
					e\EventState=1
				EndIf
			EndIf
			;[End Block]
		Case "room205"
			;[Block]
			If e\EventState=0 Or e\EventStr <> "loaddone" Then
				If e\EventStr = "load0"
					e\room\Objects[3] = LoadAnimMesh_Strict("GFX\npcs\205_demon1.b3d")
					QuickLoadPercent = 10
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					e\room\Objects[4] = LoadAnimMesh_Strict("GFX\npcs\205_demon2.b3d")
					QuickLoadPercent = 20
					e\EventStr = "load2"
				ElseIf e\EventStr = "load2"
					e\room\Objects[5] = LoadAnimMesh_Strict("GFX\npcs\205_demon3.b3d")
					QuickLoadPercent = 30
					e\EventStr = "load3"
				ElseIf e\EventStr = "load3"
					e\room\Objects[6] = LoadAnimMesh_Strict("GFX\npcs\205_woman.b3d")
					QuickLoadPercent = 40
					e\EventStr = "load4"
				ElseIf e\EventStr = "load4"
					QuickLoadPercent = 50
					e\EventStr = "load5"
				ElseIf e\EventStr = "load5"
					For i = 3 To 6
						PositionEntity e\room\Objects[i], EntityX(e\room\Objects[0],True), EntityY(e\room\Objects[0],True), EntityZ(e\room\Objects[0],True), True
						RotateEntity e\room\Objects[i], -90, EntityYaw(e\room\Objects[0],True), 0, True
						ScaleEntity(e\room\Objects[i], 0.05, 0.05, 0.05, True)
					Next
					QuickLoadPercent = 70
					e\EventStr = "load6"
				ElseIf e\EventStr = "load6"
					HideEntity(e\room\Objects[3])
					HideEntity(e\room\Objects[4])
					HideEntity(e\room\Objects[5])
					QuickLoadPercent = 100
					e\EventStr = "loaddone"
				EndIf
			EndIf
			;[End Block]
		Case "room860"
			;[Block]
			If e\EventStr = "load0"
				QuickLoadPercent = 15
				ForestNPC = CreateSprite()
				ScaleSprite ForestNPC,0.75*(140.0/410.0),0.75
				SpriteViewMode ForestNPC,4
				EntityFX ForestNPC,1+8
				ForestNPCTex = LoadAnimTexture("GFX\npcs\AgentIJ.png",1+2,140,410,0,4)
				ForestNPCData[0] = 0
				EntityTexture ForestNPC,ForestNPCTex,ForestNPCData[0]
				ForestNPCData[1]=0
				ForestNPCData[2]=0
				HideEntity ForestNPC
				e\EventStr = "load1"
			ElseIf e\EventStr = "load1"
				QuickLoadPercent = 40
				e\EventStr = "load2"
			ElseIf e\EventStr = "load2"
				QuickLoadPercent = 100
				If e\room\NPC[0]=Null Then e\room\NPC[0]=CreateNPC(NPCtype860, 0,0,0)
				e\EventStr = "loaddone"
			EndIf
			;[End Block]
		Case "room966"
			;[Block]
			If e\EventState = 1
				e\EventState2 = e\EventState2+FPSfactor
				If e\EventState2>30 Then
					If e\EventStr = ""
						CreateNPC(NPCtype966, EntityX(e\room\Objects[0],True), EntityY(e\room\Objects[0],True), EntityZ(e\room\Objects[0],True))
						QuickLoadPercent = 50
						e\EventStr = "load0"
					ElseIf e\EventStr = "load0"
						CreateNPC(NPCtype966, EntityX(e\room\Objects[2],True), EntityY(e\room\Objects[2],True), EntityZ(e\room\Objects[2],True))
						QuickLoadPercent = 100
						e\EventState=2
					EndIf
				Else
					QuickLoadPercent = Int(e\EventState2)
				EndIf
			EndIf
			;[End Block]
		Case "dimension1499"
			;[Block]
			If e\EventState = 0.0
				If e\EventStr = "load0"
					QuickLoadPercent = 10
					e\room\Objects[0] = LoadMesh_Strict("GFX\map\dimension1499\1499plane.b3d")
					HideEntity e\room\Objects[0]
					e\EventStr = "load1"
				ElseIf e\EventStr = "load1"
					QuickLoadPercent = 30
					NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
					e\EventStr = 1
				Else
					If Int(e\EventStr)<16
						QuickLoadPercent = QuickLoadPercent + 2
						e\room\Objects[Int(e\EventStr)] = LoadMesh_Strict("GFX\map\dimension1499\1499object"+(Int(e\EventStr))+".b3d")
						HideEntity e\room\Objects[Int(e\EventStr)]
						e\EventStr = Int(e\EventStr)+1
					ElseIf Int(e\EventStr)=16
						QuickLoadPercent = 90
						CreateChunkParts(e\room)
						e\EventStr = 17
					ElseIf Int(e\EventStr) = 17
						QuickLoadPercent = 100
						x# = EntityX(e\room\obj)
						z# = EntityZ(e\room\obj)
						Local ch.Chunk
						For i = -2 To 0 Step 2
							ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#,True)
							ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#-40,True)
						Next
						e\EventState = 2.0
						e\EventStr = 18
					EndIf
				EndIf
			EndIf
			;[End Block]
	End Select
	
	CatchErrors("QuickLoadEvents "+e\EventName)
End Function

Function Kill()
	If GodMode Then Return
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If KillTimer >= 0 Then
		KillAnim = Rand(0,1)
		PlaySound_Strict(DamageSFX(0))
		If SelectedDifficulty\permaDeath Then
			DeleteFile(CurrentDir() + SavePath + CurrSave+"\save.txt")
			DeleteDir(SavePath + CurrSave)
			LoadSaveGames()
		End If
		
		KillTimer = Min(-1, KillTimer)
		ShowEntity Head
		PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity (Head)
		RotateEntity(Head, 0, EntityYaw(Camera), 0)		
	EndIf
End Function

Function DrawEnding()
	ShowPointer()
	
	FPSfactor = 0
	If EndingTimer>-2000
		EndingTimer=Max(EndingTimer-FPSfactor2,-1111)
	Else
		EndingTimer=EndingTimer-FPSfactor2
	EndIf
	
	GiveAchievement(Achv055)
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\name = "Keter" Then GiveAchievement(AchvKeter)
	Local x,y,width,height, temp
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(SelectedEnding)
		Case "b2", "a1"
			ClsColor Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0)
		Default
			ClsColor 0,0,0
	End Select
	
	ShouldPlay = 66
	
	Cls
	
	If EndingTimer<-200 Then
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel BreathCHN : Stamina = 100
		EndIf
		
		If EndingScreen = 0 Then
			EndingScreen = LoadImage_Strict("GFX\endingscreen.jpg")
			
			ShouldPlay = 23
			CurrMusicVolume = MusicVolume
			
			CurrMusicVolume = MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\"+Music(23)+".ogg",CurrMusicVolume,0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict LightSFX
		EndIf
		
		If EndingTimer > -700 Then
			If Rand(1,150)<Min((Abs(EndingTimer)-200),155) Then
				DrawImage EndingScreen, GraphicWidth/2-400, GraphicHeight/2-400
			Else
				Color 0,0,0
				Rect 100,100,GraphicWidth-200,GraphicHeight-200
				Color 255,255,255
			EndIf
			
			If EndingTimer+FPSfactor2 > -450 And EndingTimer <= -450 Then
				Select Lower(SelectedEnding)
					Case "a1", "a2"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateA\Ending"+SelectedEnding+".ogg")
					Case "b1", "b2", "b3"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateB\Ending"+SelectedEnding+".ogg")
				End Select
			EndIf
		Else
			DrawImage EndingScreen, GraphicWidth/2-400, GraphicHeight/2-400
			
			If EndingTimer < -1000 And EndingTimer > -2000
				
				width = ImageWidth(PauseMenuIMG)
				height = ImageHeight(PauseMenuIMG)
				x = GraphicWidth / 2 - width / 2
				y = GraphicHeight / 2 - height / 2
				
				DrawImage PauseMenuIMG, x, y
				
				Color(255, 255, 255)
				SetFont Font2
				Text(x + width / 2 + 40*MenuScale, y + 20*MenuScale + 10, "结束", True)
				SetFont Font1
				
				If AchievementsMenu=0 Then 
					x = x+132*MenuScale
					y = y+122*MenuScale
					
					Local roomamount = 0, roomsfound = 0
					For r.Rooms = Each Rooms
						roomamount = roomamount + 1
						roomsfound = roomsfound + r\found
					Next
					
					Local docamount=0, docsfound=0
					For itt.ItemTemplates = Each ItemTemplates
						If itt\tempname = "paper" Then
							docamount=docamount+1
							docsfound=docsfound+itt\found
						EndIf
					Next
					
					Local scpsEncountered=1
					For i = 0 To 24
						scpsEncountered = scpsEncountered+Achievements(i)
					Next
					
					Local achievementsUnlocked =0
					For i = 0 To MAXACHIEVEMENTS-1
						achievementsUnlocked = achievementsUnlocked + Achievements(i)
					Next
					
					Text x, y, "遇到的SCP： " +scpsEncountered
					Text x, y+20*MenuScale, "解锁的成就： " + achievementsUnlocked+"/"+(MAXACHIEVEMENTS)
					Text x, y+40*MenuScale, "找到的房间： " + roomsfound+"/"+roomamount
					Text x, y+60*MenuScale, "发现的文档： " +docsfound+"/"+docamount
					Text x, y+80*MenuScale, "使用SCP-914转换的物品数量： " +RefinedItems			
					
					x = GraphicWidth / 2 - width / 2
					y = GraphicHeight / 2 - height / 2
					x = x+width/2
					y = y+height-100*MenuScale
					
					If DrawButton(x-145*MenuScale,y-200*MenuScale + 5,390*MenuScale,60*MenuScale,"成就", True, False, -2) Then
						AchievementsMenu = 1
					EndIf
					
					If DrawButton(x-145*MenuScale,y-100*MenuScale + 5,390*MenuScale,60*MenuScale,"主菜单", True, False, -2)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i=0 To 9
							If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict("SFX\Music\"+Music(NowPlaying)+".ogg",0.0,Mode)
						SetStreamVolume_Strict(MusicCHN,1.0*MusicVolume)
						FlushKeys()
						EndingTimer=-2000
						InitCredits()
					EndIf
				Else
					ShouldPlay = 23
					DrawMenu()
				EndIf
			;Credits
			ElseIf EndingTimer<=-2000
				ShouldPlay = 24
				DrawCredits()
			EndIf
		EndIf
	EndIf
	
	If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	SetFont Font1
End Function

Type CreditsLine
	Field txt$
	Field id%
	Field stay%
End Type

Global CreditsTimer# = 0.0
Global CreditsScreen%

Function InitCredits()
	Local cl.CreditsLine
	Local file% = OpenFile("Credits.txt")
	Local l$
	
	; 因为整个游戏只有制作人员名单用到了粗体字体，所以粗体就砍到几十个字了
	CreditsFont% = LoadFont_Strict("GFX\font\Containment Breach.ttf", Int(21 * (GraphicHeight / 1024.0)))
	CreditsFont2% = LoadFont_Strict("GFX\font\Containment Breach Bold.ttf", Int(30 * (GraphicHeight / 1024.0)))
	CreditsFont3% = LoadFont_Strict("GFX\font\Containment Breach Bold.ttf", Int(37 * (GraphicHeight / 1024.0)))
	
	If CreditsScreen = 0
		CreditsScreen = LoadImage_Strict("GFX\creditsscreen.jpg")
	EndIf
	
	Repeat
		l = ReadLine(file)
		cl = New CreditsLine
		cl\txt = l
	Until Eof(file)
	
	Delete First CreditsLine
	CreditsTimer = 0
End Function

Function DrawCredits()
    Local credits_Y# = (EndingTimer+2000)/2+(GraphicHeight+10)
    Local cl.CreditsLine
    Local id%
    Local endlinesamount%
	Local LastCreditLine.CreditsLine
	
    Cls
	
	If Rand(1,300)>1
		DrawImage CreditsScreen, GraphicWidth/2-400, GraphicHeight/2-400
	EndIf
	
	id = 0
	endlinesamount = 0
	LastCreditLine = Null
	Color 255,255,255
	For cl = Each CreditsLine
		cl\id = id
		If Left(cl\txt,1)="*"
			SetFont CreditsFont2
			If cl\stay=False
				Text GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
			EndIf
		ElseIf Left(cl\txt,1)="`"
			SetFont CreditsFont3
			If cl\stay=False
				Text GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
			EndIf
		ElseIf Left(cl\txt,1)="/"
			LastCreditLine = Before(cl)
		Else
			SetFont CreditsFont
			If cl\stay=False
				Text GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),cl\txt,True
			EndIf
		EndIf
		If LastCreditLine<>Null
			If cl\id>LastCreditLine\id
				cl\stay = True
			EndIf
		EndIf
		If cl\stay
			endlinesamount=endlinesamount+1
		EndIf
		id=id+1
	Next
	If (credits_Y+(24*LastCreditLine\id*MenuScale))<-StringHeight(LastCreditLine\txt)
		CreditsTimer=CreditsTimer+(0.5*FPSfactor2)
		If CreditsTimer>=0.0 And CreditsTimer<255.0
			Color Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0)
		ElseIf CreditsTimer>=255.0
			Color 255,255,255
			If CreditsTimer>500.0
				CreditsTimer=-255.0
			EndIf
		Else
			Color Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0)
			If CreditsTimer>=-1.0
				CreditsTimer=-1.0
			EndIf
		EndIf
		DebugLog CreditsTimer
	EndIf
	If CreditsTimer<>0.0
		For cl = Each CreditsLine
			If cl\stay
				SetFont CreditsFont
				If Left(cl\txt,1)="/"
					Text GraphicWidth/2,(GraphicHeight/2)+(endlinesamount/2)+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
				Else
					Text GraphicWidth/2,(GraphicHeight/2)+(24*(cl\id-LastCreditLine\id)*MenuScale)-((endlinesamount/2)*24*MenuScale),cl\txt,True
				EndIf
			EndIf
		Next
	EndIf
	
	If GetKey() Then CreditsTimer=-1
	
	If CreditsTimer=-1
		FreeFont CreditsFont
		FreeFont CreditsFont2
		FreeImage CreditsScreen
		CreditsScreen = 0
		FreeImage EndingScreen
		EndingScreen = 0
		Delete Each CreditsLine
        NullGame(False)
        StopStream_Strict(MusicCHN)
        ShouldPlay = 21
        MenuOpen = False
        MainMenuOpen = True
        MainMenuTab = 0
        CurrSave = ""
        FlushKeys()
	EndIf
End Function

;--------------------------------------- player controls -------------------------------------------

Function MovePlayer()
	CatchErrors("Uncaught (MovePlayer)")
	Local Sprint# = 1.0, Speed# = 0.018, i%, angle#
	
	If SuperMan Then
		Speed = Speed * 3
		
		SuperManTimer=SuperManTimer+FPSfactor
		
		CameraShake = Sin(SuperManTimer / 5.0) * (SuperManTimer / 1500.0)
		
		If SuperManTimer > 70 * 50 Then
			DeathMSG = "在[数据编辑]找到一套D级人员连体衣。经过进一步调查，发现该连体衣内装有12.5公斤的蓝灰状物质。"
			DeathMSG = DeathMSG + "对该物质的化学分析仍没有定论，可能与SCP-914有关。"
			Kill()
			ShowEntity Fog
		Else
			BlurTimer = 500		
			HideEntity Fog
		EndIf
	End If
	
	If DeathTimer > 0 Then
		DeathTimer=DeathTimer-FPSfactor
		If DeathTimer < 1 Then DeathTimer = -1.0
	ElseIf DeathTimer < 0 
		Kill()
	EndIf
	
	If CurrSpeed > 0 Then
        Stamina = Min(Stamina + 0.15 * FPSfactor/1.25, 100.0)
    Else
        Stamina = Min(Stamina + 0.15 * FPSfactor*1.25, 100.0)
    EndIf
	
	If StaminaEffectTimer > 0 Then
		StaminaEffectTimer = StaminaEffectTimer - (FPSfactor/70)
	Else
		If StaminaEffect <> 1.0 Then StaminaEffect = 1.0
	EndIf
	
	Local temp#
	
	If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then 
		If KeyDown(KEY_SPRINT) Then
			If Stamina < 5 Then
				temp = 0
				If WearingGasMask>0 Or Wearing1499>0 Then temp=1
				If ChannelPlaying(BreathCHN)=False Then BreathCHN = PlaySound_Strict(BreathSFX((temp), 0))
			ElseIf Stamina < 50
				If BreathCHN=0 Then
					temp = 0
					If WearingGasMask>0 Or Wearing1499>0 Then temp=1
					BreathCHN = PlaySound_Strict(BreathSFX((temp), Rand(1,3)))
					ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*SFXVolume
				Else
					If ChannelPlaying(BreathCHN)=False Then
						temp = 0
						If WearingGasMask>0 Or Wearing1499>0 Then temp=1
						BreathCHN = PlaySound_Strict(BreathSFX((temp), Rand(1,3)))
						ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*SFXVolume			
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount-1
		If Inventory(i)<>Null Then
			If Inventory(i)\itemtemplate\tempname = "finevest" Then Stamina = Min(Stamina, 60) : Exit
		EndIf
	Next
	
	If Wearing714 Then
		Stamina = Min(Stamina, 10)
		Sanity = Max(-850, Sanity)
	EndIf
	
	If IsZombie Then Crouch = False
	
	If Abs(CrouchState-Crouch)<0.001 Then 
		CrouchState = Crouch
	Else
		CrouchState = CurveValue(Crouch, CrouchState, 10.0)
	EndIf
	
	If (Not NoClip) Then 
		If ((KeyDown(KEY_DOWN) Or KeyDown(KEY_UP)) Or (KeyDown(KEY_RIGHT) Or KeyDown(KEY_LEFT)) And Playable) Or ForceMove>0 Then
			
			If Crouch = 0 And (KeyDown(KEY_SPRINT)) And Stamina > 0.0 And (Not IsZombie) Then
				Sprint = 2.5
				Stamina = Stamina - FPSfactor * 0.4 * StaminaEffect
				If Stamina <= 0 Then Stamina = -20.0
			End If
			
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then 
				If EntityY(Collider)<2000*RoomScale Or EntityY(Collider)>2608*RoomScale Then
					Stamina = 0
					Speed = 0.015
					Sprint = 1.0					
				EndIf
			EndIf	
			
			If ForceMove>0 Then Speed=Speed*ForceMove
			
			If SelectedItem<>Null Then
				If SelectedItem\itemtemplate\tempname = "firstaid" Or SelectedItem\itemtemplate\tempname = "finefirstaid" Or SelectedItem\itemtemplate\tempname = "firstaid2" Then 
					Sprint = 0
				EndIf
			EndIf
			
			temp# = (Shake Mod 360)
			Local tempchn%
			If (Not UnableToMove%) Then Shake# = (Shake + FPSfactor * Min(Sprint, 1.5) * 7) Mod 720
			If temp < 180 And (Shake Mod 360) >= 180 And KillTimer>=0 Then
				If CurrStepSFX=0 Then
					temp = GetStepSound(Collider)
					
					If Sprint = 2.5 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(temp, 0, Rand(0, 7)))
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(temp, 1, Rand(0, 7)))
					End If
				ElseIf CurrStepSFX=1
					tempchn% = PlaySound_Strict(Step2SFX(Rand(0, 2)))
				ElseIf CurrStepSFX=2
					tempchn% = PlaySound_Strict(Step2SFX(Rand(3,5)))
				ElseIf CurrStepSFX=3
					If Sprint = 2.5 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 1, Rand(0, 7)))
					End If
				EndIf
				If tempchn <> 0 Then ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
			EndIf	
		EndIf
	Else ;noclip on
		If (KeyDown(KEY_SPRINT)) Then 
			Sprint = 2.5
		ElseIf KeyDown(KEY_CROUCH)
			Sprint = 0.5
		EndIf
	EndIf
	
	If KeyHit(KEY_CROUCH) And Playable Then Crouch = (Not Crouch)
	
	Local temp2# = (Speed * Sprint) / (1.0+CrouchState)
	
	If NoClip Then 
		Shake = 0
		CurrSpeed = 0
		CrouchState = 0
		Crouch = 0
		
		RotateEntity Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0
		
		temp2 = temp2 * NoClipSpeed
		
		If KeyDown(KEY_DOWN) Then MoveEntity Collider, 0, 0, -temp2*FPSfactor
		If KeyDown(KEY_UP) Then MoveEntity Collider, 0, 0, temp2*FPSfactor
		
		If KeyDown(KEY_LEFT) Then MoveEntity Collider, -temp2*FPSfactor, 0, 0
		If KeyDown(KEY_RIGHT) Then MoveEntity Collider, temp2*FPSfactor, 0, 0	
		
		ResetEntity Collider
	Else
		temp2# = temp2 / Max((Injuries+3.0)/3.0,1.0)
		If Injuries > 0.5 Then 
			temp2 = temp2*Min((Sin(Shake/2)+1.2),1.0)
		EndIf
		
		temp = False
		If (Not IsZombie%)
			If KeyDown(KEY_DOWN) And Playable Then
				temp = True 
				angle = 180
				If KeyDown(KEY_LEFT) Then angle = 135 
				If KeyDown(KEY_RIGHT) Then angle = -135 
			ElseIf (KeyDown(KEY_UP) And Playable) Then
				temp = True
				angle = 0
				If KeyDown(KEY_LEFT) Then angle = 45 
				If KeyDown(KEY_RIGHT) Then angle = -45 
			ElseIf ForceMove>0 Then
				temp=True
				angle = ForceAngle
			Else If Playable Then
				If KeyDown(KEY_LEFT) Then angle = 90 : temp = True
				If KeyDown(KEY_RIGHT) Then angle = -90 : temp = True 
			EndIf
		Else
			temp=True
			angle = ForceAngle
		EndIf
		
		angle = WrapAngle(EntityYaw(Collider,True)+angle+90.0)
		
		If temp Then 
			CurrSpeed = CurveValue(temp2, CurrSpeed, 20.0)
		Else
			CurrSpeed = Max(CurveValue(0.0, CurrSpeed-0.1, 1.0),0.0)
		EndIf
		
		If (Not UnableToMove%) Then TranslateEntity Collider, Cos(angle)*CurrSpeed * FPSfactor, 0, Sin(angle)*CurrSpeed * FPSfactor, True
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Collider)
			If CollisionY(Collider, i) < EntityY(Collider) - 0.25 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			If DropSpeed# < - 0.07 Then 
				If CurrStepSFX=0 Then
					PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX=1
					PlaySound_Strict(Step2SFX(Rand(0, 2)))
				ElseIf CurrStepSFX=2
					PlaySound_Strict(Step2SFX(Rand(3, 5)))
				ElseIf CurrStepSFX=3
					PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
				EndIf
				PlayerSoundVolume = Max(3.0,PlayerSoundVolume)
			EndIf
			DropSpeed# = 0
		Else
			If PlayerFallingPickDistance#<>0.0
				Local pick = LinePick(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,-PlayerFallingPickDistance,0)
				If pick
					DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
				Else
					DropSpeed# = 0
				EndIf
			Else
				DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
			EndIf
		EndIf
		PlayerFallingPickDistance# = 10.0
		
		If (Not UnableToMove%) And ShouldEntitiesFall Then TranslateEntity Collider, 0, DropSpeed * FPSfactor, 0
	EndIf
	
	ForceMove = False
	
	If Injuries > 1.0 Then
		temp2 = Bloodloss
		BlurTimer = Max(Max(Sin(MilliSecs()/100.0)*Bloodloss*30.0,Bloodloss*2*(2.0-CrouchState)),BlurTimer)
		If (Not I_427\Using And I_427\Timer < 70*360) Then
			Bloodloss = Min(Bloodloss + (Min(Injuries,3.5)/300.0)*FPSfactor,100)
		EndIf
		
		If temp2 <= 60 And Bloodloss > 60 Then
			Msg = "你因失血过多而感到头晕"
			MsgTimer = 70*4
		EndIf
	EndIf
	
	UpdateInfect()
	
	If Bloodloss > 0 Then
		If Rnd(200)<Min(Injuries,4.0) Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.decals = CreateDecal(Rand(15,16), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\size = Rnd(0.03,0.08)*Min(Injuries,3.0) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
			tempchn% = PlaySound_Strict (DripSFX(Rand(0,2)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			
			FreeEntity pvt
		EndIf
		
		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs())/20.0)+1.0)*Bloodloss*0.2)
		
		If Bloodloss > 60 Then Crouch = True
		If Bloodloss => 100 Then 
			Kill()
			HeartBeatVolume = 0.0
		ElseIf Bloodloss > 80.0
			HeartBeatRate = Max(150-(Bloodloss-80)*5,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, 0.75+(Bloodloss-80.0)*0.0125)	
		ElseIf Bloodloss > 35.0
			HeartBeatRate = Max(70+Bloodloss,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, (Bloodloss-35.0)/60.0)			
		EndIf
	EndIf
	
	If HealTimer > 0 Then
		DebugLog HealTimer
		HealTimer = HealTimer - (FPSfactor / 70)
		Bloodloss = Min(Bloodloss + (2 / 400.0) * FPSfactor, 100)
		Injuries = Max(Injuries - (FPSfactor / 70) / 30, 0.0)
	EndIf
		
	If Playable Then
		If KeyHit(KEY_BLINK) Then BlinkTimer = 0
		If KeyDown(KEY_BLINK) And BlinkTimer < - 10 Then BlinkTimer = -10
	EndIf
	
	
	If HeartBeatVolume > 0 Then
		If HeartBeatTimer <= 0 Then
			tempchn = PlaySound_Strict (HeartBeatSFX)
			ChannelVolume tempchn, HeartBeatVolume*SFXVolume#
			
			HeartBeatTimer = 70.0*(60.0/Max(HeartBeatRate,1.0))
		Else
			HeartBeatTimer = HeartBeatTimer - FPSfactor
		EndIf
		
		HeartBeatVolume = Max(HeartBeatVolume - FPSfactor*0.05, 0)
	EndIf
	
	CatchErrors("MovePlayer")
End Function

Function MouseLook()
	Local i%
	
	CameraShake = Max(CameraShake - (FPSfactor / 10), 0)
	
	CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1))
	CurrCameraZoom = Max(CurrCameraZoom - FPSfactor, 0)
	
	If KillTimer >= 0 And FallTimer >=0 Then
		
		HeadDropSpeed = 0
		
		;fixing the black screen bug with some bubblegum code 
		Local Zero# = 0.0
		Local Nan1# = 0.0 / Zero
		If Int(EntityX(Collider))=Int(Nan1) Then
			PositionEntity Collider, EntityX(Camera, True), EntityY(Camera, True) - 0.5, EntityZ(Camera, True), True
			Msg = "EntityX(碰撞) = NaN， 正在重置位置    -    新位置： "+EntityX(Collider)
			MsgTimer = 300				
		EndIf
		
		Local up# = (Sin(Shake) / (20.0+CrouchState*20.0))*0.6;, side# = Cos(Shake / 2.0) / 35.0		
		Local roll# = Max(Min(Sin(Shake/2)*2.5*Min(Injuries+0.25,3.0),8.0),-8.0)
		
		PositionEntity Camera, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
		RotateEntity Camera, 0, EntityYaw(Collider), roll*0.5
		
		MoveEntity Camera, side, up + 0.6 + CrouchState * -0.3, 0
		
		; -- Update the smoothing que To smooth the movement of the mouse.
		mouse_x_speed_1# = CurveValue(MouseXSpeed() * (MouseSens + 0.6) , mouse_x_speed_1, (6.0 / (MouseSens + 1.0))*MouseSmooth) 
		If Int(mouse_x_speed_1) = Int(Nan1) Then mouse_x_speed_1 = 0
		If PrevFPSFactor>0 Then
            If Abs(FPSfactor/PrevFPSFactor-1.0)>1.0 Then
                ;lag spike detected - stop all camera movement
                mouse_x_speed_1 = 0.0
                mouse_y_speed_1 = 0.0
            EndIf
        EndIf
		If InvertMouse Then
			mouse_y_speed_1# = CurveValue(-MouseYSpeed() * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*MouseSmooth) 
		Else
			mouse_y_speed_1# = CurveValue(MouseYSpeed () * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*MouseSmooth) 
		EndIf
		If Int(mouse_y_speed_1) = Int(Nan1) Then mouse_y_speed_1 = 0
		
		Local the_yaw# = ((mouse_x_speed_1#)) * mouselook_x_inc# / (1.0+WearingVest)
		Local the_pitch# = ((mouse_y_speed_1#)) * mouselook_y_inc# / (1.0+WearingVest)
		
		TurnEntity Collider, 0.0, -the_yaw#, 0.0 ; Turn the user on the Y (yaw) axis.
		user_camera_pitch# = user_camera_pitch# + the_pitch#
		; -- Limit the user;s camera To within 180 degrees of pitch rotation. ;EntityPitch(); returns useless values so we need To use a variable To keep track of the camera pitch.
		If user_camera_pitch# > 70.0 Then user_camera_pitch# = 70.0
		If user_camera_pitch# < - 70.0 Then user_camera_pitch# = -70.0
		
		RotateEntity Camera, WrapAngle(user_camera_pitch + Rnd(-CameraShake, CameraShake)), WrapAngle(EntityYaw(Collider) + Rnd(-CameraShake, CameraShake)), roll ; Pitch the user;s camera up And down.
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
			If EntityY(Collider)<2000*RoomScale Or EntityY(Collider)>2608*RoomScale Then
				RotateEntity Camera, WrapAngle(EntityPitch(Camera)),WrapAngle(EntityYaw(Camera)), roll+WrapAngle(Sin(MilliSecs()/150.0)*30.0) ; Pitch the user;s camera up And down.
			EndIf
		EndIf
		
	Else
		HideEntity Collider
		PositionEntity Camera, EntityX(Head), EntityY(Head), EntityZ(Head)
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Head)
			If CollisionY(Head, i) < EntityY(Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			HeadDropSpeed# = 0
		Else
			
			If KillAnim = 0 Then 
				MoveEntity Head, 0, 0, HeadDropSpeed
				RotateEntity(Head, CurveAngle(-90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity Head, 0, 0, -HeadDropSpeed
				RotateEntity(Head, CurveAngle(90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			HeadDropSpeed# = HeadDropSpeed - 0.002 * FPSfactor
		EndIf
		
		If InvertMouse Then
			TurnEntity (Camera, -MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
		Else
			TurnEntity (Camera, MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
		End If
		
	EndIf
	
	;p??lyhiukkasia
	If ParticleAmount=2
		If Rand(35) = 1 Then
			Local pvt% = CreatePivot()
			PositionEntity(pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(pvt, 0, Rnd(360), 0)
			If Rand(2) = 1 Then
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			End If
			
			Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
			p\speed = 0.001
			RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
			
			p\SizeChange = -0.00001
			
			FreeEntity pvt
		End If
	EndIf
	
	; -- Limit the mouse;s movement. Using this method produces smoother mouselook movement than centering the mouse Each loop.
	If (MouseX() > mouse_right_limit) Or (MouseX() < mouse_left_limit) Or (MouseY() > mouse_bottom_limit) Or (MouseY() < mouse_top_limit)
		MoveMouse viewport_center_x, viewport_center_y
	EndIf
	
	If WearingGasMask Or WearingHazmat Or Wearing1499 Then
		If Wearing714 = False Then
			If WearingGasMask = 2 Or Wearing1499 = 2 Or WearingHazmat = 2 Then
				Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
			EndIf
		EndIf
		If WearingHazmat = 1 Then
			Stamina = Min(60, Stamina)
		EndIf
		
		ShowEntity(GasMaskOverlay)
	Else
		HideEntity(GasMaskOverlay)
	End If
	
	If (Not WearingNightVision=0) Then
		ShowEntity(NVOverlay)
		If WearingNightVision=2 Then
			EntityColor(NVOverlay, 0,100,255)
			AmbientLightRooms(15)
		ElseIf WearingNightVision=3 Then
			EntityColor(NVOverlay, 255,0,0)
			AmbientLightRooms(15)
		Else
			EntityColor(NVOverlay, 0,255,0)
			AmbientLightRooms(15)
		EndIf
		EntityTexture(Fog, FogNVTexture)
	Else
		AmbientLightRooms(0)
		HideEntity(NVOverlay)
		EntityTexture(Fog, FogTexture)
	EndIf
	
	For i = 0 To 5
		If SCP1025state[i]>0 Then
			Select i
				Case 0 ;common cold
					If FPSfactor>0 Then 
						If Rand(1000)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - FPSfactor * 0.3
				Case 1 ;chicken pox
					If Rand(9000)=1 And Msg="" Then
						Msg="你感觉皮肤很痒"
						MsgTimer =70*4
					EndIf
				Case 2 ;cancer of the lungs
					If FPSfactor>0 Then 
						If Rand(800)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - FPSfactor * 0.1
				Case 3 ;appendicitis
					;0.035/sec = 2.1/min
					If (Not I_427\Using And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+FPSfactor*0.0005
					EndIf
					If SCP1025state[i]>20.0 Then
						If SCP1025state[i]-FPSfactor<=20.0 Then 
							Msg="你胃痛得难以忍受"
							MsgTimer = 70*4
						EndIf
						Stamina = Stamina - FPSfactor * 0.3
					ElseIf SCP1025state[i]>10.0
						If SCP1025state[i]-FPSfactor<=10.0 Then 
							Msg="你开始胃痛"
							MsgTimer = 70*4
						EndIf
					EndIf
				Case 4 ;asthma
					If Stamina < 35 Then
						If Rand(Int(140+Stamina*8))=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
						CurrSpeed = CurveValue(0, CurrSpeed, 10+Stamina*15)
					EndIf
				Case 5;cardiac arrest
					If (Not I_427\Using And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+FPSfactor*0.35
					EndIf
					;35/sec
					If SCP1025state[i]>110 Then
						HeartBeatRate=0
						BlurTimer = Max(BlurTimer, 500)
						If SCP1025state[i]>140 Then 
							DeathMSG = "“他在读了SCP-1025后死于心脏骤停，这是肯定的。"
							DeathMSG = DeathMSG + "是否存在‘心神心脏骤停’这种东西，或者SCP-1025是否有一些我们没意识到的异常特性？”"
							Kill()
						EndIf
					Else
						HeartBeatRate=Max(HeartBeatRate, 70+SCP1025state[i])
						HeartBeatVolume = 1.0
					EndIf
			End Select 
		EndIf
	Next
	
	
End Function

;--------------------------------------- GUI, menu etc ------------------------------------------------

Function DrawGUI()
	CatchErrors("Uncaught (DrawGUI)")
	
	Local temp%, x%, y%, z%, i%, yawvalue#, pitchvalue#
	Local x2#,y2#,z2#
	Local n%, xtemp, ytemp, strtemp$
	
	Local e.Events, it.Items
	
	If MenuOpen Or ConsoleOpen Or SelectedDoor <> Null Or InvOpen Or OtherOpen<>Null Or EndingTimer < 0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom Then
				If Float(e\EventStr)<1000.0 Then
					If e\EventState > 600 Then
						If BlinkTimer < -3 And BlinkTimer > -10 Then
							If e\img = 0 Then
								If BlinkTimer > -5 And Rand(30)=1 Then
									PlaySound_Strict DripSFX(0)
									If e\img = 0 Then e\img = LoadImage_Strict("GFX\npcs\106face.jpg")
								EndIf
							Else
								DrawImage e\img, GraphicWidth/2-Rand(390,310), GraphicHeight/2-Rand(290,310)
							EndIf
						Else
							If e\img <> 0 Then FreeImage e\img : e\img = 0
						EndIf
							
						Exit
					EndIf
				Else
					If BlinkTimer < -3 And BlinkTimer > -10 Then
						If e\img = 0 Then
							If BlinkTimer > -5 Then
								If e\img = 0 Then
									e\img = LoadImage_Strict("GFX\kneelmortal.jpg")
									If (ChannelPlaying(e\SoundCHN)) Then
										StopChannel(e\SoundCHN)
									EndIf
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage e\img, GraphicWidth/2-Rand(390,310), GraphicHeight/2-Rand(290,310)
						EndIf
					Else
						If e\img <> 0 Then FreeImage e\img : e\img = 0
						If BlinkTimer < -3 Then
							If (Not ChannelPlaying(e\SoundCHN)) Then
								e\SoundCHN = PlaySound_Strict(e\Sound)
							EndIf
						Else
							If (ChannelPlaying(e\SoundCHN)) Then
								StopChannel(e\SoundCHN)
							EndIf
						EndIf
					EndIf
					
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	
	If ClosestButton <> 0 And SelectedDoor = Null And InvOpen = False And MenuOpen = False And OtherOpen = Null Then
		temp% = CreatePivot()
		PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
		PointEntity temp, ClosestButton
		yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		FreeEntity (temp)
		
		DrawImage(HandIcon, GraphicWidth / 2 + Sin(yawvalue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(pitchvalue) * (GraphicHeight / 3) - 32)
		
		If MouseUp1 Then
			MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf Playable Then
					PlaySound2(ButtonSFX, Camera, ClosestButton)
					UseDoor(ClosestDoor,True)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If ClosestItem <> Null Then
		yawvalue# = -DeltaYaw(Camera, ClosestItem\collider)
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = -DeltaPitch(Camera, ClosestItem\collider)
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		
		DrawImage(HandIcon2, GraphicWidth / 2 + Sin(yawvalue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(pitchvalue) * (GraphicHeight / 3) - 32)
	EndIf
	
	If DrawHandIcon Then DrawImage(HandIcon, GraphicWidth / 2 - 32, GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon(i) Then
			x = GraphicWidth / 2 - 32
			y = GraphicHeight / 2 - 32		
			Select i
				Case 0
					y = y - 64 - 5
				Case 1
					x = x + 64 + 5
				Case 2
					y = y + 64 + 5
				Case 3
					x = x - 5 - 64
			End Select
			DrawImage(HandIcon, x, y)
			Color 0, 0, 0
			Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(ArrowIMG(i), x + 21, y + 21)
			DrawArrowIcon(i) = False
		End If
	Next
	
	If Using294 Then Use294()
	
	If HUDenabled Then 
		
		Local width% = 204, height% = 20
		x% = 80
		y% = GraphicHeight - 95
		
		Color 255, 255, 255	
		Rect (x, y, width, height, False)
		For i = 1 To Int(((width - 2) * (BlinkTimer / (BLINKFREQ))) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		If EyeIrritation > 0 Then
			Color 200, 0, 0
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		End If
		
		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		
		DrawImage BlinkIcon, x - 50, y
		
		y = GraphicHeight - 55
		Color 255, 255, 255
		Rect (x, y, width, height, False)
		For i = 1 To Int(((width - 2) * (Stamina / 100.0)) / 10)
			DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		If Crouch Then
			DrawImage CrouchIcon, x - 50, y
		Else
			DrawImage SprintIcon, x - 50, y
		EndIf
		
		If DebugHUD Then
			Color 255, 255, 255
			SetFont ConsoleFont
			
			Text x - 50, 50, "玩家位置： (" + f2s(EntityX(Collider), 3) + ", " + f2s(EntityY(Collider), 3) + ", " + f2s(EntityZ(Collider), 3) + ")"
			Text x - 50, 70, "视角位置： (" + f2s(EntityX(Camera), 3)+ ", " + f2s(EntityY(Camera), 3) +", " + f2s(EntityZ(Camera), 3) + ")"
			Text x - 50, 100, "玩家方向： (" + f2s(EntityPitch(Collider), 3) + ", " + f2s(EntityYaw(Collider), 3) + ", " + f2s(EntityRoll(Collider), 3) + ")"
			Text x - 50, 120, "镜头方向： (" + f2s(EntityPitch(Camera), 3)+ ", " + f2s(EntityYaw(Camera), 3) +", " + f2s(EntityRoll(Camera), 3) + ")"
			Text x - 50, 150, "房间： " + PlayerRoom\RoomTemplate\Name
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					Text x - 50, 170, "房间事件： " + ev\EventName   
					Text x - 50, 190, "state: " + ev\EventState
					Text x - 50, 210, "state2: " + ev\EventState2   
					Text x - 50, 230, "state3: " + ev\EventState3
					Text x - 50, 250, "str: "+ ev\EventStr
					Exit
				EndIf
			Next
			Text x - 50, 280, "房间坐标： (" + Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5) + ", angle: "+PlayerRoom\angle + ")"
			Text x - 50, 300, "耐力： " + f2s(Stamina, 3)
			Text x - 50, 320, "死亡计时： " + f2s(KillTimer, 3)               
			Text x - 50, 340, "眨眼计时： " + f2s(BlinkTimer, 3)
			Text x - 50, 360, "受伤： " + Injuries
			Text x - 50, 380, "失血： " + Bloodloss
			If Curr173 <> Null
				Text x - 50, 410, "SCP-173位置 (collider)： (" + f2s(EntityX(Curr173\Collider), 3) + ", " + f2s(EntityY(Curr173\Collider), 3) + ", " + f2s(EntityZ(Curr173\Collider), 3) + ")"
				Text x - 50, 430, "SCP-173位置 (obj)： (" + f2s(EntityX(Curr173\obj), 3) + ", " + f2s(EntityY(Curr173\obj), 3) + ", " + f2s(EntityZ(Curr173\obj), 3) + ")"
				Text x - 50, 450, "SCP-173状态： " + Curr173\State
			EndIf
			If Curr106 <> Null
				Text x - 50, 470, "SCP-106位置： (" + f2s(EntityX(Curr106\obj), 3) + ", " + f2s(EntityY(Curr106\obj), 3) + ", " + f2s(EntityZ(Curr106\obj), 3) + ")"
				Text x - 50, 490, "SCP-106游荡： " + Curr106\Idle
				Text x - 50, 510, "SCP-106状态： " + Curr106\State
			EndIf
			offset% = 0
			For npc.NPCs = Each NPCs
				If npc\NPCtype = NPCtype096 Then
					Text x - 50, 530, "SCP-096位置： (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					Text x - 50, 550, "SCP-096游荡： " + npc\Idle
					Text x - 50, 570, "SCP-096状态： " + npc\State
					Text x - 50, 590, "SCP-096速度：" + f2s(npc\currspeed, 5)
				EndIf
				If npc\NPCtype = NPCtypeMTF Then
					Text x - 50, 620 + 60 * offset, "MTF " + offset + " 位置： (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					Text x - 50, 640 + 60 * offset, "MTF " + offset + " 状态： " + npc\State
					Text x - 50, 660 + 60 * offset, "MTF " + offset + " 最后见到： " + npc\lastseen					
					offset = offset + 1
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
				Text x + 350, 50, "当前区块 X/Z： ("+(Int((EntityX(Collider)+20)/40))+", "+(Int((EntityZ(Collider)+20)/40))+")"
				Local CH_Amount% = 0
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				Text x + 350, 70, "当前区块数量： "+CH_Amount
			Else
				Text x + 350, 50, "当前房间位置： ("+PlayerRoom\x+", "+PlayerRoom\y+", "+PlayerRoom\z+")"
			EndIf
			Text x + 350, 90, ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB"
			Text x + 350, 110, "渲染的三角形："+CurrTrisAmount
			Text x + 350, 130, "激活的贴图："+ActiveTextures()
			Text x + 350, 150, "SCP-427状态 （秒）："+Int(I_427\Timer/70.0)
			Text x + 350, 170, "SCP-008感染："+Infect
			For i = 0 To 5
				Text x + 350, 190+(20*i), "SCP-1025状态 "+i+"： "+SCP1025state[i]
			Next
			If SelectedMonitor <> Null Then
				Text x + 350, 310, "当前显示器： "+SelectedMonitor\ScrObj
			Else
				Text x + 350, 310, "当前显示器： 无"
			EndIf
			
			SetFont Font1
		EndIf
		
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage SelectedScreen\img, GraphicWidth/2-ImageWidth(SelectedScreen\img)/2,GraphicHeight/2-ImageHeight(SelectedScreen\img)/2
		
		If MouseUp1 Or MouseHit2 Then
			FreeImage SelectedScreen\img : SelectedScreen\img = 0
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	
	Local shouldDrawHUD%=True
	If SelectedDoor <> Null Then
		SelectedItem = Null
		
		If shouldDrawHUD Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(ClosestButton,True),EntityY(ClosestButton,True),EntityZ(ClosestButton,True)
			RotateEntity pvt, 0, EntityYaw(ClosestButton,True)-180,0
			MoveEntity pvt, 0,0,0.22
			PositionEntity Camera, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
			PointEntity Camera, ClosestButton
			FreeEntity pvt	
			
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)+MeshHeight(ButtonOBJ)*0.015,EntityZ(ClosestButton,True))
			projY# = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)-MeshHeight(ButtonOBJ)*0.015,EntityZ(ClosestButton,True))
			scale# = (ProjectedY()-projy)/462.0
			
			x = GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
			y = GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2		
			
			SetFont Font3
			If KeypadMSG <> "" Then 
				KeypadTimer = KeypadTimer-FPSfactor2
				
				If (KeypadTimer Mod 70) < 35 Then Text GraphicWidth/2, y+124*scale, KeypadMSG, True,True
				If KeypadTimer =<0 Then
					KeypadMSG = ""
					SelectedDoor = Null
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				EndIf
			Else
				Text GraphicWidth/2, y+70*scale, "访问密码：",True,True	
				SetFont Font4
				Text GraphicWidth/2, y+124*scale, KeypadInput,True,True	
			EndIf
			
			x = x+44*scale
			y = y+249*scale
			
			For n = 0 To 3
				For i = 0 To 2
					xtemp = x+Int(58.5*scale*n)
					ytemp = y+(67*scale)*i
					
					temp = False
					If MouseOn(xtemp,ytemp, 54*scale,65*scale) And KeypadMSG = "" Then
						If MouseUp1 Then 
							PlaySound_Strict ButtonSFX
							
							Select (n+1)+(i*4)
								Case 1,2,3
									KeypadInput=KeypadInput + ((n+1)+(i*4))
								Case 4
									KeypadInput=KeypadInput + "0"
								Case 5,6,7
									KeypadInput=KeypadInput + ((n+1)+(i*4)-1)
								Case 8 ;enter
									If KeypadInput = SelectedDoor\Code Then
										PlaySound_Strict ScannerSFX1
										
										If SelectedDoor\Code = Str(AccessCode) Then
											GiveAchievement(AchvMaynard)
										ElseIf SelectedDoor\Code = "7816"
											GiveAchievement(AchvHarp)
										EndIf									
										
										SelectedDoor\locked = 0
										UseDoor(SelectedDoor,True)
										SelectedDoor = Null
										MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
									Else
										PlaySound_Strict ScannerSFX2
										KeypadMSG = "拒绝访问"
										KeypadTimer = 210
										KeypadInput = ""	
									EndIf
								Case 9,10,11
									KeypadInput=KeypadInput + ((n+1)+(i*4)-2)
								Case 12
									KeypadInput = ""
							End Select 
							
							If Len(KeypadInput)> 4 Then KeypadInput = Left(KeypadInput,4)
						EndIf
						
					Else
						temp = False
					EndIf
					
				Next
			Next
			
			If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
			
			If MouseHit2 Then
				SelectedDoor = Null
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		KeypadInput = ""
		KeypadTimer = 0
		KeypadMSG = ""
	EndIf
	
	If KeyHit(1) And EndingTimer=0 And (Not Using294) Then
		If MenuOpen Or InvOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		Else
			PauseSounds()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMSG = 0
		
		SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
		If SelectedItem <> Null Then
			If Instr(SelectedItem\itemtemplate\tempname,"vest") Or Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit") Then
				If (Not WearingVest) And (Not WearingHazmat) Then
					DropItem(SelectedItem)
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local spacing%
	Local PrevOtherOpen.Items
	
	Local OtherSize%,OtherAmount%
	
	Local isEmpty%
	
	Local isMouseOn%
	
	Local closedInv%
	
	If OtherOpen<>Null Then
		;[Block]
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		Else If (PlayerRoom\RoomTemplate\Name = "exit1") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		PrevOtherOpen = OtherOpen
		OtherSize=OtherOpen\invSlots;Int(OtherOpen\state2)
		
		For i%=0 To OtherSize-1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount+1
			EndIf
		Next
		
		InvOpen = False
		SelectedDoor = Null
		Local tempX% = 0
		
		width = 70
		height = 70
		spacing% = 35
		
		x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - (height * OtherSize /5 + height * (OtherSize / 5 - 1)) / 2;height
		
		ItemAmount = 0
		For  n% = 0 To OtherSize - 1
			isMouseOn% = False
			If ScaledMouseX() > x And ScaledMouseX() < x + width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + height Then
					isMouseOn = True
				EndIf
			EndIf
			
			If isMouseOn Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (SelectedItem <> OtherOpen\SecondInv[n] Or isMouseOn) Then DrawImage(OtherOpen\SecondInv[n]\invimg, x + width / 2 - 32, y + height / 2 - 32)
			EndIf
			DebugLog "otheropen: "+(OtherOpen<>Null)
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If isMouseOn Then
					SetFont Font1
					Color 0,0,0
					Text(x + width / 2 + 1, y + height + spacing - 15 + 1, OtherOpen\SecondInv[n]\itemtemplate\name, True)
					Color 255, 255, 255	
					Text(x + width / 2, y + height + spacing - 15, OtherOpen\SecondInv[n]\itemtemplate\displayName, True)				
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							MouseHit1 = False
							
							If DoubleClick Then
								If OtherOpen\SecondInv[n]\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(OtherOpen\SecondInv[n]\itemtemplate\sound))
								OtherOpen = Null
								closedInv=True
								InvOpen = False
								DoubleClick = False
							EndIf
							
						EndIf
					Else
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null : Exit
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
				
			EndIf					
			
			x=x+width + spacing
			tempX=tempX + 1
			If tempX = 5 Then 
				tempX=0
				y = y + height*2 
				x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
					
					ShowEntity(SelectedItem\collider)
					PositionEntity(SelectedItem\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\collider, EntityPitch(Camera), EntityYaw(Camera), 0)
					MoveEntity(SelectedItem\collider, 0, -0.1, 0.1)
					RotateEntity(SelectedItem\collider, 0, Rand(360), 0)
					ResetEntity (SelectedItem\collider)
					
					SelectedItem\DropSpeed = 0.0
					
					SelectedItem\Picked = False
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null : Exit
					Next
					
					isEmpty=True
					If OtherOpen\itemtemplate\tempname = "wallet" Then
						If (Not isEmpty) Then
							For z% = 0 To OtherSize - 1
								If OtherOpen\SecondInv[z]<>Null
									Local name$=OtherOpen\SecondInv[z]\itemtemplate\tempname
									If name$<>"25ct" And name$<>"coin" And name$<>"key" And name$<>"scp860" And name$<>"scp714" Then
										isEmpty=False
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						For z% = 0 To OtherSize - 1
							If OtherOpen\SecondInv[z]<>Null
								isEmpty = False
								Exit
							EndIf
						Next
					EndIf
					
					If isEmpty Then
						Select OtherOpen\itemtemplate\tempname
							Case "clipboard"
								OtherOpen\invimg = OtherOpen\itemtemplate\invimg2
								SetAnimTime OtherOpen\model,17.0
							Case "wallet"
								SetAnimTime OtherOpen\model,0.0
						End Select
					EndIf
					
					SelectedItem = Null
					OtherOpen = Null
					closedInv=True
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z% = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then PrevOtherOpen\SecondInv[z] = Null : Exit
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Default
								Msg = "你不能合并这两项物品"
								MsgTimer = 70 * 5
						End Select					
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If Fullscreen Then DrawImage CursorIMG,ScaledMouseX(),ScaledMouseY()
		If (closedInv) And (Not InvOpen) Then 
			ResumeSounds() 
			OtherOpen=Null
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf
		;[End Block]
		
	Else If InvOpen Then
		
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "exit1") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		SelectedDoor = Null
		
		width% = 70
		height% = 70
		spacing% = 35
		
		x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - (height * MaxItemAmount /5 + height * (MaxItemAmount / 5 - 1)) / 2
		
		ItemAmount = 0
		For  n% = 0 To MaxItemAmount - 1
			isMouseOn% = False
			If ScaledMouseX() > x And ScaledMouseX() < x + width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + height Then
					isMouseOn = True
				End If
			EndIf
			
			If Inventory(n) <> Null Then
				Color 200, 200, 200
				Select Inventory(n)\itemtemplate\tempname 
					Case "gasmask"
						If WearingGasMask=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "supergasmask"
						If WearingGasMask=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "gasmask3"
						If WearingGasMask=3 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit"
						If WearingHazmat=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit2"
						If WearingHazmat=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "hazmatsuit3"
						If WearingHazmat=3 Then Rect(x - 3, y - 3, width + 6, height + 6)	
					Case "vest"
						If WearingVest=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "finevest"
						If WearingVest=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp714"
						If Wearing714=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "nvgoggles"
						If WearingNightVision=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "supernv"
						If WearingNightVision=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp1499"
						If Wearing1499=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "super1499"
						If Wearing1499=2 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "finenvgoggles"
						If WearingNightVision=3 Then Rect(x - 3, y - 3, width + 6, height + 6)
					Case "scp427"
						If I_427\Using=1 Then Rect(x - 3, y - 3, width + 6, height + 6)
				End Select
			EndIf
			
			If isMouseOn Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, width + 2, height + 2)
			EndIf
			
			Color 255, 255, 255
			DrawFrame(x, y, width, height, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null Then
				If (SelectedItem <> Inventory(n) Or isMouseOn) Then 
					DrawImage(Inventory(n)\invimg, x + width / 2 - 32, y + height / 2 - 32)
				EndIf
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				If isMouseOn Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory(n)
							MouseHit1 = False
							
							If DoubleClick Then
								If WearingHazmat > 0 And Instr(SelectedItem\itemtemplate\tempname,"hazmatsuit")=0 Then
									Msg = "你不能在穿着防护服时使用物品"
									MsgTimer = 70*5
									SelectedItem = Null
									Return
								EndIf
								If Inventory(n)\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(Inventory(n)\itemtemplate\sound))
								InvOpen = False
								DoubleClick = False
							EndIf
							
						EndIf
						
						SetFont Font1
						Color 0,0,0
						Text(x + width / 2 + 1, y + height + spacing - 15 + 1, Inventory(n)\displayName, True)							
						Color 255, 255, 255	
						Text(x + width / 2, y + height + spacing - 15, Inventory(n)\displayName, True)	
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn And MouseHit1 Then
					For z% = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem Then Inventory(z) = Null : Exit
					Next
					Inventory(n) = SelectedItem
				End If
				
			EndIf					
			
			x=x+width + spacing
			If n = 4 Then 
				y = y + height*2 
				x = GraphicWidth / 2 - (width * MaxItemAmount /2 + spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					Select SelectedItem\itemtemplate\tempname
						Case "vest","finevest","hazmatsuit","hazmatsuit2","hazmatsuit3"
							Msg = "双击物品以取下"
							MsgTimer = 70*5
						Case "scp1499","super1499"
							If Wearing1499>0 Then
								Msg = "双击物品以取下"
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						Default
							DropItem(SelectedItem)
							SelectedItem = Null
							InvOpen = False
					End Select
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					If Inventory(MouseSlot) = Null Then
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem Then Inventory(z) = Null : Exit
						Next
						Inventory(MouseSlot) = SelectedItem
						SelectedItem = Null
					ElseIf Inventory(MouseSlot) <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Case "paper","key1","key2","key3","key4","key5","key6","misc","oldpaper","badge","ticket","25ct","coin","key","scp860"
								;[Block]
								If Inventory(MouseSlot)\itemtemplate\tempname = "clipboard" Then
									;Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\itemtemplate\tempname
									Local b2$ = SelectedItem\itemtemplate\name
									If (b<>"misc" And b<>"25ct" And b<>"coin" And b<>"key" And b<>"scp860" And b<>"scp714") Or (b2="Playing Card" Or b2="Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													SetAnimTime Inventory(MouseSlot)\model,0.0
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
															Exit
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = "文件夹没有空间放东西了"
										Else
											If added\itemtemplate\tempname = "paper" Or added\itemtemplate\tempname = "oldpaper" Then
												Msg = "你将文档放入了文件夹"
											Else
												Msg = "你将 " + added\itemtemplate\displayName + " 放入了文件夹"
											EndIf
											
										EndIf
										MsgTimer = 70 * 5
									Else
										Msg = "你不能合并这两项物品"
										MsgTimer = 70 * 5
									EndIf
								ElseIf Inventory(MouseSlot)\itemtemplate\tempname = "wallet" Then
									;Add an item to clipboard
									added.Items = Null
									b$ = SelectedItem\itemtemplate\tempname
									b2$ = SelectedItem\itemtemplate\name
									If (b<>"misc" And b<>"paper" And b<>"oldpaper") Or (b2="Playing Card" Or b2="Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													If b<>"25ct" And b<>"coin" And b<>"key" And b<>"scp860"
														SetAnimTime Inventory(MouseSlot)\model,3.0
													EndIf
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
															Exit
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = "钱包已经满了"
										Else
											Msg = "你将 "+added\itemtemplate\displayName+" 放入了钱包"
										EndIf
										
										MsgTimer = 70 * 5
									Else
										Msg = "你不能合并这两项物品"
										MsgTimer = 70 * 5
									EndIf
								Else
									Msg = "你不能合并这两项物品"
									MsgTimer = 70 * 5
								EndIf
								SelectedItem = Null
								
								;[End Block]
							Case "battery", "bat"
								;[Block]
								Select Inventory(MouseSlot)\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										SelectedItem = Null
										Inventory(MouseSlot)\state = 100.0
										Msg = "你更换了导航仪的电池"
										MsgTimer = 70 * 5
									Case "S-NAV Navigator Ultimate"
										Msg = "这个导航仪里看起来没有放电池的地方"
										MsgTimer = 70 * 5
									Case "Radio Transceiver"
										Select Inventory(MouseSlot)\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
												Msg = "这个对讲机里看起来没有放电池的地方"
												MsgTimer = 70 * 5
											Case "18vradio"
												Msg = "这种电池不适合这个对讲机"
												MsgTimer = 70 * 5
											Case "radio"
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\state = 100.0
												Msg = "你更换了对讲机的电池"
												MsgTimer = 70 * 5
										End Select
									Case "Night Vision Goggles"
										Local nvname$ = Inventory(MouseSlot)\itemtemplate\tempname
										If nvname$="nvgoggles" Or nvname$="supernv" Then
											If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
											RemoveItem (SelectedItem)
											SelectedItem = Null
											Inventory(MouseSlot)\state = 1000.0
											Msg = "你更换了夜视仪的电池"
											MsgTimer = 70 * 5
										Else
											Msg = "这个夜视仪里看起来没有放电池的地方"
											MsgTimer = 70 * 5
										EndIf
									Default
										Msg = "你不能合并这两项物品"
										MsgTimer = 70 * 5	
								End Select
								;[End Block]
							Case "18vbat"
								;[Block]
								Select Inventory(MouseSlot)\itemtemplate\name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										Msg = "这种电池不适合这个导航仪"
										MsgTimer = 70 * 5
									Case "S-NAV Navigator Ultimate"
										Msg = "这个导航仪里看起来没有放电池的地方"
										MsgTimer = 70 * 5
									Case "Radio Transceiver"
										Select Inventory(MouseSlot)\itemtemplate\tempname 
											Case "fineradio", "veryfineradio"
												Msg = "这个对讲机里看起来没有放电池的地方"
												MsgTimer = 70 * 5		
											Case "18vradio"
												If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
												RemoveItem (SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\state = 100.0
												Msg = "你更换了对讲机的电池"
												MsgTimer = 70 * 5
										End Select 
									Default
										Msg = "你不能合并这两项物品"
										MsgTimer = 70 * 5	
								End Select
								;[End Block]
							Default
								;[Block]
								Msg = "你不能合并这两项物品"
								MsgTimer = 70 * 5
								;[End Block]
						End Select					
					End If
					
				End If
				SelectedItem = Null
			End If
		End If
		
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
		If InvOpen = False Then 
			ResumeSounds() 
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf
	Else ;invopen = False
		If SelectedItem <> Null Then
			Select SelectedItem\itemtemplate\tempname
				Case "nvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat=0 Then
						If WearingNightVision = 1 Then
							Msg = "你摘下了夜视仪"
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "你戴上了夜视仪"
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision)
					ElseIf Wearing1499 > 0 Then
						Msg = "你需要摘下SCP-1499才能戴上夜视仪"
					Else
						Msg = "你需要脱掉防护服才能戴上夜视仪"
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "supernv"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat=0 Then
						If WearingNightVision = 2 Then
							Msg = "你摘下了夜视仪"
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "你戴上了夜视仪"
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 2
					ElseIf Wearing1499 > 0 Then
						Msg = "你需要摘下SCP-1499才能戴上夜视仪"
					Else
						Msg = "你需要脱掉防护服才能戴上夜视仪"
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "finenvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingNightVision = 3 Then
							Msg = "你摘下了夜视仪"
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "你戴上了夜视仪"
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 3
					ElseIf Wearing1499 > 0 Then
						Msg = "你需要摘下SCP-1499才能戴上夜视仪"
					Else
						Msg = "你需要脱掉防护服才能戴上夜视仪"
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "ring"
					;[Block]
					If Wearing714=2 Then
						Msg = "你摘下了戒指"
						Wearing714 = False
					Else
						Msg = "你戴上了戒指"
						Wearing714 = 2
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null
					;[End Block]
				Case "1123"
					;[Block]
					If Not (Wearing714 = 1) Then
						If PlayerRoom\RoomTemplate\Name <> "room1123" Then
							ShowEntity Light
							LightFlash = 7
							PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
							DeathMSG = "对象D-9341在试图袭击一名九尾狐成员后被击毙。"
							DeathMSG = DeathMSG + "监控录像显示，对象在大约9分钟前在现场徘徊，用中文高喊“消灭四害”。"
							DeathMSG = DeathMSG + "SCP-1123在[已编辑]的附近找到，证明对象与它发生了肢体接触。"
							DeathMSG = DeathMSG + "目前尚未知SCP-1123是如何从收容室离开的。"
							Kill()
							Return
						EndIf
						For e.Events = Each Events
							If e\EventName = "room1123" Then 
								If e\EventState = 0 Then
									ShowEntity Light
									LightFlash = 3
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
								EndIf
								e\EventState = Max(1, e\EventState)
								Exit
							EndIf
						Next
					EndIf
					;[End Block]
				Case "battery"
					;[Block]
					;InvOpen = True
					;[End Block]
				Case "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "25ct"
					;[Block]
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict LoadTempSound("SFX\SCP\513\Bell1.ogg")
					
					If Curr5131 = Null
						Curr5131 = CreateNPC(NPCtype5131, 0,0,0)
					EndIf	
					SelectedItem = Null
					;[End Block]
				Case "scp500"
					;[Block]
					If CanUseItem(False, False, True)
						GiveAchievement(Achv500)
						
						If Infect > 0 Then
							Msg = "你吞下了药丸，你的恶心正在缓解"
						Else
							Msg = "你吞下了药丸"
						EndIf
						MsgTimer = 70*7
						
						DeathTimer = 0
						Infect = 0
						Stamina = 100
						For i = 0 To 5
							SCP1025state[i]=0
						Next
						If StaminaEffect > 1.0 Then
							StaminaEffect = 1.0
							StaminaEffectTimer = 0.0
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If CanUseItem(False, False, True)
						Select Rand(5)
							Case 1
								Injuries = 3.5
								Msg = "你开始大量流血"
								MsgTimer = 70*7
							Case 2
								Injuries = 0
								Bloodloss = 0
								Msg = "你的伤口正在迅速愈合"
								MsgTimer = 70*7
							Case 3
								Injuries = Max(0, Injuries - Rnd(0.5,3.5))
								Bloodloss = Max(0, Bloodloss - Rnd(10,100))
								Msg = "你感觉好多了"
								MsgTimer = 70*7
							Case 4
								BlurTimer = 10000
								Bloodloss = 0
								Msg = "你感到恶心"
								MsgTimer = 70*7
							Case 5
								BlinkTimer = -10
								Local roomname$ = PlayerRoom\RoomTemplate\Name
								If roomname = "dimension1499" Or roomname = "gatea" Or (roomname="exit1" And EntityY(Collider)>1040.0*RoomScale)
									Injuries = 2.5
									Msg = "你开始大量流血"
									MsgTimer = 70*7
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "pocketdimension" Then
											PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))		
											ResetEntity Collider									
											UpdateDoors()
											UpdateRooms()
											PlaySound_Strict(Use914SFX)
											DropSpeed = 0
											Curr106\State = -2500
											Exit
										EndIf
									Next
									Msg = "由于某些奇怪的原因，你发现你身处口袋空间"
									MsgTimer = 70*8
								EndIf
						End Select
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If Bloodloss = 0 And Injuries = 0 Then
						Msg = "你现在不需要使用急救箱"
						MsgTimer = 70*5
						SelectedItem = Null
					Else
						If CanUseItem(False, True, True)
							CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							Crouch = True
							
							DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
							width% = 300
							height% = 20
							x% = GraphicWidth / 2 - width / 2
							y% = GraphicHeight / 2 + 80
							Rect(x, y, width+4, height, False)
							For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
							
							SelectedItem\state = Min(SelectedItem\state+(FPSfactor/5.0),100)			
							
							If SelectedItem\state = 100 Then
								If SelectedItem\itemtemplate\tempname = "finefirstaid" Then
									Bloodloss = 0
									Injuries = Max(0, Injuries - 2.0)
									If Injuries = 0 Then
										Msg = "你包扎了伤口并吃了止痛药，你感觉良好"
									ElseIf Injuries > 1.0
										Msg = "你包扎了伤口并吃了止痛药，但你无法完全止血"
									Else
										Msg = "你包扎了伤口并吃了止痛药，但你仍感到疼痛"
									EndIf
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								Else
									Bloodloss = Max(0, Bloodloss - Rand(10,20))
									If Injuries => 2.5 Then
										Msg = "伤口已严重到无法完全止血了"
										Injuries = Max(2.5, Injuries-Rnd(0.3,0.7))
									ElseIf Injuries > 1.0
										Injuries = Max(0.5, Injuries-Rnd(0.5,1.0))
										If Injuries > 1.0 Then
											Msg = "你用绷带包扎伤口，但仍无法完全止血"
										Else
											Msg = "你设法止了血"
										EndIf
									Else
										If Injuries > 0.5 Then
											Injuries = 0.5
											Msg = "你吃了止痛药，稍微减轻了疼痛"
										Else
											Injuries = 0.5
											Msg = "你吃了止痛药，但走路还是很疼"
										EndIf
									EndIf
									
									If SelectedItem\itemtemplate\tempname = "firstaid2" Then 
										Select Rand(6)
											Case 1
												SuperMan = True
												Msg = "你已经肾上腺素所压倒了啊卧槽芜湖~！"
											Case 2
												InvertMouse = (Not InvertMouse)
												Msg = "你突然发现你很难回头"
											Case 3
												BlurTimer = 5000
												Msg = "你感到恶心"
											Case 4
												BlinkEffect = 0.6
												BlinkEffectTimer = Rand(20,30)
											Case 5
												Bloodloss = 0
												Injuries = 0
												Msg = "你用绷带包扎伤口，血完全止住了。你感觉良好"
											Case 6
												Msg = "你用绷带包扎伤口，血从绷带里大量流出"
												Injuries = 3.5
										End Select
									EndIf
									
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								EndIf							
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops"
					;[Block]
					If CanUseItem(False,False,False)
						If (Not (Wearing714=1)) Then ;wtf is this
							BlinkEffect = 0.6
							BlinkEffectTimer = Rand(20,30)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If CanUseItem(False,False,False)
						If (Not (Wearing714=1)) Then 
							BlinkEffect = 0.4
							BlinkEffectTimer = Rand(30,40)
							Bloodloss = Max(Bloodloss-1.0, 0)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem(False,False,False)
						If (Not (Wearing714 = 1)) Then
							BlinkEffect = 0.0
							BlinkEffectTimer = 60
							EyeStuck = 10000
						EndIf
						BlurTimer = 1000
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						Select SelectedItem\itemtemplate\name
							Case "Burnt Note" 
								SelectedItem\itemtemplate\img = LoadImage_Strict("GFX\items\docAC.jpg")
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 0,0,0
								SetFont Font2
								Text 277, 469, AccessCode, True, True
								SetFont Font1
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Document SCP-372"
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 37,45,137
								SetFont Font5
								temp = ((Int(AccessCode)*3) Mod 10000)
								If temp < 1000 Then temp = temp+1000
								Text 383*MenuScale, 734*MenuScale, temp, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "Movie Ticket"
								;don't resize because it messes up the masking
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								
								If (SelectedItem\state = 0) Then
									Msg = "“嘿，我记得这部电影！”"
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
									SelectedItem\state = 1
								EndIf
							Default 
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						End Select
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025) 
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\state = Rand(0,5)
						SelectedItem\itemtemplate\img=LoadImage_Strict("GFX\items\1025\1025_"+Int(SelectedItem\state)+".jpg")	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If (Not Wearing714) Then SCP1025state[SelectedItem\state]=Max(1,SCP1025state[SelectedItem\state])
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "cup"
					;[Block]
					If CanUseItem(False,False,True)
						SelectedItem\name = Trim(Lower(SelectedItem\name))
						If Left(SelectedItem\name, Min(6,Len(SelectedItem\name))) = "cup of" Then
							SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-7)
						ElseIf Left(SelectedItem\name, Min(8,Len(SelectedItem\name))) = "a cup of" 
							SelectedItem\name = Right(SelectedItem\name, Len(SelectedItem\name)-9)
						EndIf

						;the state of refined items is more than 1.0 (fine setting increases it by 1, very fine doubles it)
						x2 = (SelectedItem\state+1.0)
						
						Local iniStr$ = "Data\SCP-294.ini"
						
						Local loc% = GetINISectionLocation(iniStr, SelectedItem\name)
						
						;Stop
						
						strtemp = GetINIString2(iniStr, loc, "message")
						If strtemp <> "" Then Msg = strtemp : MsgTimer = 70*6
						
						If GetINIInt2(iniStr, loc, "lethal") Or GetINIInt2(iniStr, loc, "deathtimer") Then 
							DeathMSG = GetINIString2(iniStr, loc, "deathmessage")
							If GetINIInt2(iniStr, loc, "lethal") Then Kill()
						EndIf
						BlurTimer = GetINIInt2(iniStr, loc, "blur")*70;*temp
						If VomitTimer = 0 Then VomitTimer = GetINIInt2(iniStr, loc, "vomit")
						CameraShakeTimer = GetINIString2(iniStr, loc, "camerashake")
						Injuries = Max(Injuries + GetINIInt2(iniStr, loc, "damage"),0);*temp
						Bloodloss = Max(Bloodloss + GetINIInt2(iniStr, loc, "blood loss"),0);*temp
						strtemp =  GetINIString2(iniStr, loc, "sound")
						If strtemp<>"" Then
							PlaySound_Strict LoadTempSound(strtemp)
						EndIf
						If GetINIInt2(iniStr, loc, "stomachache") Then SCP1025state[3]=1
						
						DeathTimer=GetINIInt2(iniStr, loc, "deathtimer")*70
						
						BlinkEffect = Float(GetINIString2(iniStr, loc, "blink effect", 1.0))*x2
						BlinkEffectTimer = Float(GetINIString2(iniStr, loc, "blink effect timer", 1.0))*x2

						StaminaEffect = Float(GetINIString2(iniStr, loc, "stamina effect", 1.0))*x2
						StaminaEffectTimer = Float(GetINIString2(iniStr, loc, "stamina effect timer", 1.0))*x2
						
						strtemp = GetINIString2(iniStr, loc, "refusemessage")
						If strtemp <> "" Then
							Msg = strtemp 
							MsgTimer = 70*6		
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0,0,0)
							it\Picked = True
							For i = 0 To MaxItemAmount-1
								If Inventory(i)=SelectedItem Then Inventory(i) = it : Exit
							Next					
							EntityType (it\collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)						
						EndIf
						
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					If CanUseItem(False,True,True)
						HealTimer = 30
						StaminaEffect = 0.5
						StaminaEffectTimer = 20
						
						Msg = "你给自己注射了注射器，你感到轻微的肾上腺素分泌"
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "finesyringe"
					;[Block]
					If CanUseItem(False,True,True)
						HealTimer = Rnd(20, 40)
						StaminaEffect = Rnd(0.5, 0.8)
						StaminaEffectTimer = Rnd(20, 30)
						
						Msg = "你给自己注射了注射器，你感到肾上腺素激增"
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					If CanUseItem(False,True,True)
						Select Rand(3)
							Case 1
								HealTimer = Rnd(40, 60)
								StaminaEffect = 0.1
								StaminaEffectTimer = 30
								Msg = "你给自己注射了注射器，你感到一股巨量肾上腺素冲击着你"
							Case 2
								SuperMan = True
								Msg = "你给自己注射了注射器，你感觉正在分泌着巨量的肾上腺素"
							Case 3
								VomitTimer = 30
								Msg = "你给自己注射了注射器，你感到胃痛"
						End Select
						
						MsgTimer = 70 * 8
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "radio","18vradio","fineradio","veryfineradio"
					;[Block]
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.004)
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If RadioState(5) = 0 Then 
						Msg = "使用数字1-5在频道之间切换"
						MsgTimer = 70 * 5
						RadioState(5) = 1
						RadioState(0) = -1
					EndIf
					
					strtemp$ = ""
					
					x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img) ;+ 120
					y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img) ;- 30
					
					DrawImage(SelectedItem\itemtemplate\img, x, y)
					
					If SelectedItem\state > 0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or CoffinDistance < 4.0 Then
							ResumeChannel(RadioCHN(5))
							If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)	
						Else
							Select Int(SelectedItem\state2)
								Case 0 ;randomkanava
									ResumeChannel(RadioCHN(0))
									strtemp = "        自定频道音频 - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "未启用     "
									ElseIf UserTrackMusicAmount<1
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + "找不到音频文件     "
									Else
										If (Not ChannelPlaying(RadioCHN(0)))
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState(0)<(UserTrackMusicAmount-1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName$(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState(0)
											DebugLog UserTrackName$(RadioState(0))
										Else
											strtemp = strtemp + Upper(UserTrackName$(RadioState(0))) + "          "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict RadioSquelch
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState(0)<(UserTrackMusicAmount-1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName$(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState(0)
											DebugLog UserTrackName$(RadioState(0))
										EndIf
									EndIf
								Case 1 ;hälytyskanava
									DebugLog RadioState(1) 
									
									ResumeChannel(RadioCHN(1))
									strtemp = "        警告 - 收容失效          "
									If ChannelPlaying(RadioCHN(1)) = False Then
										
										If RadioState(1) => 5 Then
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1,1))	
											RadioState(1) = 0
										Else
											RadioState(1)=RadioState(1)+1	
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1,0))	
										EndIf
										
									EndIf
									
								Case 2 ;scp-radio
									ResumeChannel(RadioCHN(2))
									strtemp = "        SCP基金会设施广播          "
									If ChannelPlaying(RadioCHN(2)) = False Then
										RadioState(2)=RadioState(2)+1
										If RadioState(2) = 17 Then RadioState(2) = 1
										If Floor(RadioState(2)/2)=Ceil(RadioState(2)/2) Then ;parillinen, soitetaan normiviesti
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2,Int(RadioState(2)/2)))	
										Else ;pariton, soitetaan musiikkia
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2,0))
										EndIf
									EndIf 
								Case 3
									ResumeChannel(RadioCHN(3))
									strtemp = "             应急频道 - 为收容失效事件而预设的交流频道         "
									If ChannelPlaying(RadioCHN(3)) = False Then RadioCHN(3) = PlaySound_Strict(RadioStatic)
									
									If MTFtimer > 0 Then 
										RadioState(3)=RadioState(3)+Max(Rand(-10,1),0)
										Select RadioState(3)
											Case 40
												If Not RadioState3(0) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState(3) = RadioState(3)+1	
													RadioState3(0) = True	
												EndIf											
											Case 400
												If Not RadioState3(1) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState(3) = RadioState(3)+1	
													RadioState3(1) = True	
												EndIf	
											Case 800
												If Not RadioState3(2) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState(3) = RadioState(3)+1	
													RadioState3(2) = True
												EndIf													
											Case 1200
												If Not RadioState3(3) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState(3) = RadioState(3)+1	
													RadioState3(3) = True
												EndIf
											Case 1600
												If Not RadioState3(4) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState(3) = RadioState(3)+1
													RadioState3(4) = True
												EndIf
											Case 2000
												If Not RadioState3(5) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState(3) = RadioState(3)+1
													RadioState3(5) = True
												EndIf
											Case 2400
												If Not RadioState3(6) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState(3) = RadioState(3)+1
													RadioState3(6) = True
												EndIf
										End Select
									EndIf
								Case 4
									ResumeChannel(RadioCHN(6)) ;taustalle kohinaa
									If ChannelPlaying(RadioCHN(6)) = False Then RadioCHN(6) = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN(4))
									If ChannelPlaying(RadioCHN(4)) = False Then 
										If RemoteDoorOn = False And RadioState(8) = False Then
											RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter3.ogg"))	
											RadioState(8) = True
										Else
											RadioState(4)=RadioState(4)+Max(Rand(-10,1),0)
											
											Select RadioState(4)
												Case 10
													If (Not Contained106)
														If Not RadioState4(0) Then
															RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\OhGod.ogg"))
															RadioState(4) = RadioState(4)+1
															RadioState4(0) = True
														EndIf
													EndIf
												Case 100
													If Not RadioState4(1) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter2.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState4(1) = True
													EndIf		
												Case 158
													If MTFtimer = 0 And (Not RadioState4(2)) Then 
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\franklin1.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState(2) = True
													EndIf
												Case 200
													If Not RadioState4(3) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter4.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState4(3) = True
													EndIf		
												Case 260
													If Not RadioState4(4) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState4(4) = True
													EndIf		
												Case 300
													If Not RadioState4(5) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter1.ogg"))	
														RadioState(4) = RadioState(4)+1	
														RadioState4(5) = True
													EndIf		
												Case 350
													If Not RadioState4(6) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\franklin2.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState4(6) = True
													EndIf		
												Case 400
													If Not RadioState4(7) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState(4) = RadioState(4)+1
														RadioState4(7) = True
													EndIf		
												Case 450
													If Not RadioState4(8) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\franklin3.ogg"))	
														RadioState(4) = RadioState(4)+1		
														RadioState4(8) = True
													EndIf		
												Case 600
													If Not RadioState4(9) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\franklin4.ogg"))	
														RadioState(4) = RadioState(4)+1	
														RadioState4(9) = True
													EndIf		
											End Select
										EndIf
									EndIf
									
									
								Case 5
									ResumeChannel(RadioCHN(5))
									If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)
							End Select 
							
							x=x+66
							y=y+419
							
							Color (30,30,30)
							
							If SelectedItem\state <= 100 Then
								For i = 0 To 4
									Rect(x, y+8*i, 43 - i * 6, 4, Ceil(SelectedItem\state / 20.0) > 4 - i )
								Next
							EndIf	
							
							SetFont Font3
							Text(x+50, y, "频道")						
							
							If SelectedItem\itemtemplate\tempname = "veryfineradio" Then ;"KOODIKANAVA"
								ResumeChannel(RadioCHN(0))
								If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
								
								;radiostate(7)=kuinka mones piippaus menossa
								;radiostate(8)=kuinka mones access coden numero menossa
								RadioState(6)=RadioState(6) + FPSfactor
								temp = Mid(Str(AccessCode),RadioState(8)+1,1)
								If RadioState(6)-FPSfactor =< RadioState(7)*50 And RadioState(6)>RadioState(7)*50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState(7)=RadioState(7)+1
									If RadioState(7)=>temp Then
										RadioState(7)=0
										RadioState(6)=-100
										RadioState(8)=RadioState(8)+1
										If RadioState(8)=4 Then RadioState(8)=0 : RadioState(6)=-200
									EndIf
								EndIf
								
								strtemp = ""
								For i = 0 To Rand(5, 30)
									strtemp = strtemp + Chr(Rand(1,100))
								Next
								
								SetFont Font4
								Text(x+97, y+16, Rand(0,9),True,True)
								
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\state2 <> i-2 Then ;pausetetaan nykyinen radiokanava
											PlaySound_Strict RadioSquelch
											If RadioCHN(Int(SelectedItem\state2)) <> 0 Then PauseChannel(RadioCHN(Int(SelectedItem\state2)))
										EndIf
										SelectedItem\state2 = i-2
										;jos nykyistä kanavaa ollaan soitettu, laitetaan jatketaan toistoa samasta kohdasta
										If RadioCHN(SelectedItem\state2)<>0 Then ResumeChannel(RadioCHN(SelectedItem\state2))
									EndIf
								Next
								
								SetFont Font4
								Text(x+97, y+16, Int(SelectedItem\state2+1),True,True)
							EndIf
							
							SetFont Font3
							If strtemp <> "" Then
								strtemp = Right(Left(strtemp, (Int(MilliSecs()/300) Mod Len(strtemp))),10)
								Text(x+5, y+33, strtemp)
							EndIf
							
							SetFont Font1
							
						EndIf
						
					EndIf
					;[End Block]
				Case "cigarette"
					;[Block]
					If CanUseItem(False,False,True)
						If SelectedItem\state = 0 Then
							Select Rand(6)
								Case 1
									Msg = "“我没有东西来点燃它，嗯...这个怎么样。算了”"
								Case 2
									Msg = "你无法点燃它"
								Case 3
									Msg = "“我已经戒了很长时间了”"
									RemoveItem(SelectedItem)
								Case 4
									Msg = "“即使我想来一根，我也不知道拿什么来点燃它”"
								Case 5
									Msg = "“现在我真的可以来一根了...但愿我有一个打火机”"
								Case 6
									Msg = "“别想着来一根了，尤其是现在这时候”"
									RemoveItem(SelectedItem)
							End Select
							SelectedItem\state = 1 
						Else
							Msg = "你无法点燃它"
						EndIf
						
						MsgTimer = 70 * 5
					EndIf
					;[End Block]
				Case "420"
					;[Block]
					If CanUseItem(False,False,True)
						If Wearing714=1 Then
							Msg = "“我操这玩意根本没用”"
						Else
							Msg = "“伙计这东西真他妈不错”"
							Injuries = Max(Injuries-0.5, 0)
							BlurTimer = 500
							GiveAchievement(Achv420)
							PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
						EndIf
						MsgTimer = 70 * 5
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "420s"
					;[Block]
					If CanUseItem(False,False,True)
						If Wearing714=1 Then
							Msg = "“我操这玩意根本没用”"
						Else
							DeathMSG = "处于昏迷状态的对象D-9341在[数据删除]被发现。对象面带微笑，拿着一根看起来像香烟的东西。"
							DeathMSG = DeathMSG+"尽管香烟中似乎含有高浓度的一种未知化学物质，其分子结构与四氢大麻酚非常相似，但化学分析尚未得出结论。"
							Msg = "“我操我在哪...我在干什么...我需要小睡一会...”"
							KillTimer = -1						
						EndIf
						MsgTimer = 70 * 6
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If Wearing714=1 Then
						Msg = "你摘下了戒指"
						Wearing714 = False
					Else
						GiveAchievement(Achv714)
						Msg = "你戴上了戒指"
						Wearing714 = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If WearingVest = 0 Then
						CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
						
						DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
						
						width% = 300
						height% = 20
						x% = GraphicWidth / 2 - width / 2
						y% = GraphicHeight / 2 + 80
						Rect(x, y, width+4, height, False)
						For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
						
						SelectedItem\state = Min(SelectedItem\state+(FPSfactor/4.0),100)
						
						If SelectedItem\state=100 Then
							If WearingHazmat>0 Then
								Msg = "你脱下了防护服"
								WearingHazmat = False
								DropItem(SelectedItem)
							Else
								If SelectedItem\itemtemplate\tempname="hazmatsuit" Then
									WearingHazmat = 1
								ElseIf SelectedItem\itemtemplate\tempname="hazmatsuit2" Then
									WearingHazmat = 2
								Else
									WearingHazmat = 3
								EndIf
								If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
								Msg = "你穿上了防护服"
								If WearingNightVision Then CameraFogFar = StoredCameraFogFar
								WearingGasMask = 0
								WearingNightVision = 0
							EndIf
							SelectedItem\state=0
							MsgTimer = 70 * 5
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "vest","finevest"
					;[Block]
					CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					width% = 300
					height% = 20
					x% = GraphicWidth / 2 - width / 2
					y% = GraphicHeight / 2 + 80
					Rect(x, y, width+4, height, False)
					For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					
					SelectedItem\state = Min(SelectedItem\state+(FPSfactor/(2.0+(0.5*(SelectedItem\itemtemplate\tempname="finevest")))),100)
					
					If SelectedItem\state=100 Then
						If WearingVest>0 Then
							Msg = "你脱下了防弹衣"
							WearingVest = False
							DropItem(SelectedItem)
						Else
							If SelectedItem\itemtemplate\tempname="vest" Then
								Msg = "你穿上了防弹衣，你感到有些累"
								WearingVest = 1
							Else
								Msg = "你穿上了防弹衣，感到特别重"
								WearingVest = 2
							EndIf
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
						EndIf
						SelectedItem\state=0
						MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingGasMask Then
							Msg = "你摘下了防毒面具"
						Else
							If SelectedItem\itemtemplate\tempname = "supergasmask"
								Msg = "你戴上了防毒面具，你感觉呼吸容易多了"
							Else
								Msg = "你戴上了防毒面具"
							EndIf
							If WearingNightVision Then CameraFogFar = StoredCameraFogFar
							WearingNightVision = 0
							WearingGasMask = 0
						EndIf
						If SelectedItem\itemtemplate\tempname="gasmask3" Then
							If WearingGasMask = 0 Then WearingGasMask = 3 Else WearingGasMask=0
						ElseIf SelectedItem\itemtemplate\tempname="supergasmask"
							If WearingGasMask = 0 Then WearingGasMask = 2 Else WearingGasMask=0
						Else
							WearingGasMask = (Not WearingGasMask)
						EndIf
					ElseIf Wearing1499 > 0 Then
						Msg = "你需要摘下SCP-1499才能戴上防毒面具"
					Else
						Msg = "你需要脱掉防护服才能戴上防毒面具"
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If SelectedItem\state <= 100 Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.005)
					
					x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
					y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
					width = 287
					height = 256
					
					Local PlayerX,PlayerZ
					
					DrawImage(SelectedItem\itemtemplate\img, x - ImageWidth(SelectedItem\itemtemplate\img) / 2, y - ImageHeight(SelectedItem\itemtemplate\img) / 2 + 85)
					
					SetFont Font3
					
					Local NavWorks% = True
					If PlayerRoom\RoomTemplate\Name$ = "pocketdimension" Or PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
						NavWorks% = False
					ElseIf PlayerRoom\RoomTemplate\Name$ = "room860" Then
						For e.Events = Each Events
							If e\EventName = "room860" Then
								If e\EventState = 1.0 Then
									NavWorks% = False
								EndIf
								Exit
							EndIf
						Next
					EndIf
					
					If (Not NavWorks) Then
						If (MilliSecs() Mod 1000) > 300 Then
							Color(200, 0, 0)
							Text(x, y + height / 2 - 80, "错误 06", True)
							Text(x, y + height / 2 - 60, "未知位置", True)						
						EndIf
					Else
						
						If SelectedItem\state > 0 And (Rnd(CoffinDistance + 15.0) > 1.0 Or PlayerRoom\RoomTemplate\Name <> "coffin") Then
							
							PlayerX% = Floor((EntityX(PlayerRoom\obj)+8) / 8.0 + 0.5)
							PlayerZ% = Floor((EntityZ(PlayerRoom\obj)+8) / 8.0 + 0.5)
							
							SetBuffer ImageBuffer(NavBG)
							Local xx = x-ImageWidth(SelectedItem\itemtemplate\img)/2
							Local yy = y-ImageHeight(SelectedItem\itemtemplate\img)/2+85
							DrawImage(SelectedItem\itemtemplate\img, xx, yy)
							
							x = x - 12 + (((EntityX(Collider)-4.0)+8.0) Mod 8.0)*3
							y = y + 12 - (((EntityZ(Collider)-4.0)+8.0) Mod 8.0)*3
							For x2 = Max(0, PlayerX - 6) To Min(MapWidth, PlayerX + 6)
								For z2 = Max(0, PlayerZ - 6) To Min(MapHeight, PlayerZ + 6)
									
									If CoffinDistance > 16.0 Or Rnd(16.0)<CoffinDistance Then 
										If MapTemp(x2, z2)>0 And (MapFound(x2, z2) > 0 Or SelectedItem\itemtemplate\name = "S-NAV 310 Navigator" Or SelectedItem\itemtemplate\name = "S-NAV Navigator Ultimate") Then
											Local drawx% = x + (PlayerX - 1 - x2) * 24 , drawy% = y - (PlayerZ - 1 - z2) * 24
											
											If x2+1<=MapWidth Then
												If MapTemp(x2+1,z2)=False
													DrawImage NavImages(3),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(3),drawx-12,drawy-12
											EndIf
											If x2-1>=0 Then
												If MapTemp(x2-1,z2)=False
													DrawImage NavImages(1),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(1),drawx-12,drawy-12
											EndIf
											If z2-1>=0 Then
												If MapTemp(x2,z2-1)=False
													DrawImage NavImages(0),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(0),drawx-12,drawy-12
											EndIf
											If z2+1<=MapHeight Then
												If MapTemp(x2,z2+1)=False
													DrawImage NavImages(2),drawx-12,drawy-12
												EndIf
											Else
												DrawImage NavImages(2),drawx-12,drawy-12
											EndIf
										EndIf
									EndIf
									
								Next
							Next
							
							SetBuffer BackBuffer()
							DrawImageRect NavBG,xx+80,yy+70,xx+80,yy+70,270,230
							Color 30,30,30
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then Color(100, 0, 0)
							Rect xx+80,yy+70,270,230,False
							
							x = GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
							y = GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
							
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then 
								Color(100, 0, 0)
							Else
								Color (30,30,30)
							EndIf
							If (MilliSecs() Mod 1000) > 300 Then
								If SelectedItem\itemtemplate\name <> "S-NAV 310 Navigator" And SelectedItem\itemtemplate\name <> "S-NAV Navigator Ultimate" Then
									Text(x - width/2 + 10, y - height/2 + 10, "地图数据库离线")
								EndIf
								
								yawvalue = EntityYaw(Collider)-90
								x1 = x+Cos(yawvalue)*6 : y1 = y-Sin(yawvalue)*6
								x2 = x+Cos(yawvalue-140)*5 : y2 = y-Sin(yawvalue-140)*5				
								x3 = x+Cos(yawvalue+140)*5 : y3 = y-Sin(yawvalue+140)*5
								
								Line x1,y1,x2,y2
								Line x1,y1,x3,y3
								Line x2,y2,x3,y3
							EndIf
							
							Local SCPs_found% = 0
							If SelectedItem\itemtemplate\name = "S-NAV Navigator Ultimate" And (MilliSecs() Mod 600) < 400 Then
								If Curr173<>Null Then
									Local dist# = EntityDistance(Camera, Curr173\obj)
									dist = Ceil(dist / 8.0) * 8.0
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 3, y - 7 - dist * 3, dist * 3 * 2, dist * 3 * 2, False)
										Text(x - width / 2 + 10, y - height / 2 + 30, "SCP-173")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr106<>Null Then
									dist# = EntityDistance(Camera, Curr106\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), "SCP-106")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr096<>Null Then 
									dist# = EntityDistance(Camera, Curr096\obj)
									If dist < 8.0 * 4 Then
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), "SCP-096")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								For np.NPCs = Each NPCs
									If np\NPCtype = NPCtype049
										dist# = EntityDistance(Camera, np\obj)
										If dist < 8.0 * 4 Then
											If (Not np\HideFromNVG) Then
												Color 100, 0, 0
												Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
												Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), "SCP-049")
												SCPs_found% = SCPs_found% + 1
											EndIf
										EndIf
										Exit
									EndIf
								Next
								If PlayerRoom\RoomTemplate\Name = "coffin" Then
									If CoffinDistance < 8.0 Then
										dist = Rnd(4.0, 8.0)
										Color 100, 0, 0
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - width / 2 + 10, y - height / 2 + 30 + (20*SCPs_found), "SCP-895")
									EndIf
								EndIf
							End If
							
							Color (30,30,30)
							If SelectedItem\itemtemplate\name = "S-NAV Navigator" Then Color(100, 0, 0)
							If SelectedItem\state <= 100 Then
								xtemp = x - width/2 + 196
								ytemp = y - height/2 + 10
								Rect xtemp,ytemp,80,20,False
								
								For i = 1 To Ceil(SelectedItem\state / 10.0)
									DrawImage NavImages(4),xtemp+i*8-6,ytemp+4
								Next
								
								SetFont Font3
						EndIf
						EndIf
						
					EndIf
					;[End Block]
				;new Items in SCP:CB 1.3
				Case "scp1499","super1499"
					;[Block]
					If WearingHazmat>0
						Msg = "你不能在穿着防护服时戴上SCP-1499"
						MsgTimer = 70 * 5
						SelectedItem=Null
						Return
					EndIf
					
					CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					width% = 300
					height% = 20
					x% = GraphicWidth / 2 - width / 2
					y% = GraphicHeight / 2 + 80
					Rect(x, y, width+4, height, False)
					For  i% = 1 To Int((width - 2) * (SelectedItem\state / 100.0) / 10)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					
					SelectedItem\state = Min(SelectedItem\state+(FPSfactor),100)
					
					If SelectedItem\state=100 Then
						If Wearing1499>0 Then
							;Msg = "1499remove."
							Wearing1499 = False
							;DropItem(SelectedItem)
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
						Else
							If SelectedItem\itemtemplate\tempname="scp1499" Then
								;Msg = "scp1499."
								Wearing1499 = 1
							Else
								;Msg = "super1499."
								Wearing1499 = 2
							EndIf
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
							GiveAchievement(Achv1499)
							If WearingNightVision Then CameraFogFar = StoredCameraFogFar
							WearingGasMask = 0
							WearingNightVision = 0
							For r.Rooms = Each Rooms
								If r\RoomTemplate\Name = "dimension1499" Then
									BlinkTimer = -1
									NTF_1499PrevRoom = PlayerRoom
									NTF_1499PrevX# = EntityX(Collider)
									NTF_1499PrevY# = EntityY(Collider)
									NTF_1499PrevZ# = EntityZ(Collider)
									
									If NTF_1499X# = 0.0 And NTF_1499Y# = 0.0 And NTF_1499Z# = 0.0 Then
										PositionEntity (Collider, r\x+6086.0*RoomScale, r\y+304.0*RoomScale, r\z+2292.5*RoomScale)
										RotateEntity Collider,0,90,0,True
									Else
										PositionEntity (Collider, NTF_1499X#, NTF_1499Y#+0.05, NTF_1499Z#)
									EndIf
									ResetEntity(Collider)
									UpdateDoors()
									UpdateRooms()
									For it.Items = Each Items
										it\disttimer = 0
									Next
									PlayerRoom = r
									PlaySound_Strict (LoadTempSound("SFX\SCP\1499\Enter.ogg"))
									NTF_1499X# = 0.0
									NTF_1499Y# = 0.0
									NTF_1499Z# = 0.0
									If Curr096<>Null Then
										If Curr096\SoundChn<>0 Then
											SetStreamVolume_Strict(Curr096\SoundChn,0.0)
										EndIf
									EndIf
									For e.Events = Each Events
										If e\EventName = "dimension1499" Then
											If EntityDistance(e\room\obj,Collider)>8300.0*RoomScale Then
												If e\EventState2 < 5 Then
													e\EventState2 = e\EventState2 + 1
												EndIf
											EndIf
											Exit
										EndIf
									Next
									Exit
								EndIf
							Next
						EndIf
						SelectedItem\state=0
						;MsgTimer = 70 * 5
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						;SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						Select SelectedItem\itemtemplate\name
							Case "Old Badge"
								Msg = "“嗯？这家伙长得和我一模一样！”"
								MsgTimer = 70*10
						End Select
						
						SelectedItem\state = 1
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						
						Msg = "“这不是那个旧棚屋的钥匙吗？那个...我...不，不可能”"	
						MsgTimer = 70*10						
					EndIf
					
					SelectedItem\state = 1
					SelectedItem = Null
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\itemtemplate\img = 0 Then
						SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					If SelectedItem\state = 0
						Select SelectedItem\itemtemplate\name
							Case "Disciplinary Hearing DH-S-4137-17092"
								BlurTimer = 1000
								
								Msg = "“为什么这东西看起来这么熟悉？”"
								MsgTimer = 70*10
								PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
								SelectedItem\state = 1
						End Select
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\state = 0
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
					EndIf
					
					Msg = ""
					
					SelectedItem\state = 1
					DrawImage(SelectedItem\itemtemplate\invimg, GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using=1 Then
						Msg = "你关上了小盒"
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						Msg = "你打开了小盒"
						I_427\Using = True
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = "你吞下了药丸"
						MsgTimer = 70*7
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "scp500death"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = "你吞下了药丸"
						MsgTimer = 70*7
						
						If I_427\Timer < 70*360 Then
							I_427\Timer = 70*360
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Default
					;[Block]
					;check if the item is an inventory-type object
					DoubleClick = 0
					MouseHit1 = 0
					MouseDown1 = 0
					LastMouseHit1 = 0
					If SelectedItem\invSlots>0 Then OtherOpen = SelectedItem
					SelectedItem = Null					
					;[End Block]
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\img <> 0
					Local IN$ = SelectedItem\itemtemplate\tempname
					If IN$ = "paper" Or IN$ = "badge" Or IN$ = "oldpaper" Or IN$ = "ticket" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\itemtemplate\tempname
								If IN2$ = "paper" Or IN2$ = "badge" Or IN2$ = "oldpaper" Or IN2$ = "ticket" Then
									If a_it\itemtemplate\img<>0
										If a_it\itemtemplate\img <> SelectedItem\itemtemplate\img
											FreeImage(a_it\itemtemplate\img)
											a_it\itemtemplate\img = 0
											Exit
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf			
			EndIf
			
			If MouseHit2 Then
				EntityAlpha Dark, 0.0
				
				IN$ = SelectedItem\itemtemplate\tempname
				If IN$ = "scp1025" Then
					If SelectedItem\itemtemplate\img<>0 Then FreeImage(SelectedItem\itemtemplate\img)
					SelectedItem\itemtemplate\img=0
				ElseIf IN$ = "firstaid" Or IN$="finefirstaid" Or IN$="firstaid2" Then
					SelectedItem\state = 0
				ElseIf IN$ = "vest" Or IN$="finevest"
					SelectedItem\state = 0
					If (Not WearingVest)
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN$="hazmatsuit" Or IN$="hazmatsuit2" Or IN$="hazmatsuit3"
					SelectedItem\state = 0
					If (Not WearingHazmat)
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN$="scp1499" Or IN$="super1499"
					SelectedItem\state = 0
					;If (Not Wearing1499)
					;	DropItem(SelectedItem,False)
					;EndIf
				EndIf
				
				If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
				SelectedItem = Null
			EndIf
		End If		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 6
			If RadioCHN(i) <> 0 Then 
				If ChannelPlaying(RadioCHN(i)) Then PauseChannel(RadioCHN(i))
			EndIf
		Next
	EndIf
	
	For it.Items = Each Items
		If it<>SelectedItem
			Select it\itemtemplate\tempname
				Case "firstaid","finefirstaid","firstaid2","vest","finevest","hazmatsuit","hazmatsuit2","hazmatsuit3","scp1499","super1499"
					it\state = 0
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse viewport_center_x, viewport_center_y
	
	CatchErrors("DrawGUI")
End Function

Function DrawMenu()
	CatchErrors("Uncaught (DrawMenu)")
	
	Local x%, y%, width%, height%
	If InFocus() = 0 Then ;Game is out of focus -> pause the game
		If (Not Using294) Then
			MenuOpen = True
			PauseSounds()
		EndIf
        Delay 1000 ;Reduce the CPU take while game is not in focus
    EndIf
	If MenuOpen Then
		
		;DebugLog AchievementsMenu+"|"+OptionsMenu+"|"+QuitMSG
		
		If PlayerRoom\RoomTemplate\Name$ <> "exit1" And PlayerRoom\RoomTemplate\Name$ <> "gatea"
			If StopHidingTimer = 0 Then
				If EntityDistance(Curr173\Collider, Collider)<4.0 Or EntityDistance(Curr106\Collider, Collider)<4.0 Then 
					StopHidingTimer = 1
				EndIf	
			ElseIf StopHidingTimer < 40
				If KillTimer >= 0 Then 
					StopHidingTimer = StopHidingTimer+FPSfactor
					
					If StopHidingTimer => 40 Then
						PlaySound_Strict(HorrorSFX(15))
						Msg = "别再躲了。"
						MsgTimer = 6*70
						MenuOpen = False
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		
		width = ImageWidth(PauseMenuIMG)
		height = ImageHeight(PauseMenuIMG)
		x = GraphicWidth / 2 - width / 2
		y = GraphicHeight / 2 - height / 2
		
		DrawImage PauseMenuIMG, x, y
		
		Color(255, 255, 255)
		
		x = x+132*MenuScale
		y = y+122*MenuScale	
		
		If (Not MouseDown1)
			OnSliderID = 0
		EndIf
		
		If AchievementsMenu > 0 Then
			SetFont Font2
			Text(x, y-(122-45)*MenuScale+3, "成就",False,True)
			SetFont Font1
		ElseIf OptionsMenu > 0 Then
			SetFont Font2
			Text(x, y-(122-45)*MenuScale+3, "设置",False,True)
			SetFont Font1
		ElseIf QuitMSG > 0 Then
			SetFont Font2
			Text(x, y-(122-45)*MenuScale+3, "退出？",False,True)
			SetFont Font1
		ElseIf KillTimer >= 0 Then
			SetFont Font2
			Text(x, y-(122-45)*MenuScale+3, "已暂停",False,True)
			SetFont Font1
		Else
			SetFont Font2
			Text(x, y-(122-45)*MenuScale+3, "你死了",False,True)
			SetFont Font1
		End If		
		
		Local AchvXIMG% = (x + (22*MenuScale))
		Local scale# = GraphicHeight/768.0
		Local SeparationConst% = 76*scale
		Local imgsize% = 64
		
		If AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG <= 0
			SetFont Font1
			Text x, y, "难度： "+SelectedDifficulty\name
			Text x, y+20*MenuScale, "存档名： "+ConvertToUTF8(CurrSave)
			Text x, y+40*MenuScale, "地图种子： "+RandomSeed
		ElseIf AchievementsMenu <= 0 And OptionsMenu > 0 And QuitMSG <= 0 And KillTimer >= 0
			If DrawButton(x + 101 * MenuScale, y + 390 * MenuScale, 230 * MenuScale, 60 * MenuScale, "返回", True, False, -2) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias Opt_AntiAlias
				TextureLodBias TextureFloat#
			EndIf
			
			Color 0,255,0
			If OptionsMenu = 1
				Rect(x-10*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 2
				Rect(x+100*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 3
				Rect(x+210*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 4
				Rect(x+320*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			EndIf
			
			If DrawButton(x-5*MenuScale,y,100*MenuScale,30*MenuScale,"画面",False) Then OptionsMenu = 1
			If DrawButton(x+105*MenuScale,y,100*MenuScale,30*MenuScale,"音频",False) Then OptionsMenu = 2
			If DrawButton(x+215*MenuScale,y,100*MenuScale,30*MenuScale,"控制",False) Then OptionsMenu = 3
			If DrawButton(x+325*MenuScale,y,100*MenuScale,30*MenuScale,"高级",False) Then OptionsMenu = 4
			
			Local tx# = (GraphicWidth/2)+(width/2)
			Local ty# = y
			Local tw# = 400*MenuScale
			Local th# = 150*MenuScale
			
			Color 255,255,255
			Select OptionsMenu
				Case 1 ;Graphics
					SetFont Font1
					;[Block]
					y=y+50*MenuScale
					
					Color 100,100,100
					Text(x, y + 4, "启用凹凸贴图：")	
					BumpEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, BumpEnabled, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"bump")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y + 2, "垂直同步：")
					Vsync% = DrawTick(x + 270 * MenuScale, y + MenuScale, Vsync%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vsync")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y + 2, "抗锯齿：")
					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"antialias")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y + 2, "启用房间光亮：")
					EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
					EndIf
					
					y=y+30*MenuScale
					
					ScreenGamma = (SlideBar(x + 270*MenuScale, y+6*MenuScale, 100*MenuScale, ScreenGamma*50.0)/50.0)
					Color 255,255,255
					Text(x, y + 7, "屏幕伽马值：")
					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
					EndIf
					
					;y = y + 50*MenuScale
					
					y=y+50*MenuScale
					
					Color 255,255,255
					Text(x, y, "粒子数量：")
					ParticleAmount = Slider3(x+270*MenuScale,y+6*MenuScale,100*MenuScale,ParticleAmount,2,"最少","适中","最多")
					If (MouseOn(x + 270 * MenuScale, y-6*MenuScale, 100*MenuScale+14, 20) And OnSliderID=0) Or OnSliderID=2
						DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					Text(x, y, "纹理细节层次：")
					TextureDetails = Slider5(x+270*MenuScale,y+6*MenuScale,100*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
					Select TextureDetails%
						Case 0
							TextureFloat# = 0.8
						Case 1
							TextureFloat# = 0.4
						Case 2
							TextureFloat# = 0.0
						Case 3
							TextureFloat# = -0.4
						Case 4
							TextureFloat# = -0.8
					End Select
					TextureLodBias TextureFloat
					If (MouseOn(x+270*MenuScale,y-6*MenuScale,100*MenuScale+14,20) And OnSliderID=0) Or OnSliderID=3
						DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
					EndIf
					
					y=y+50*MenuScale
					Color 100,100,100
					Text(x, y + 4, "在VRAM中存储贴图：")	
					EnableVRam = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableVRam, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vram")
					EndIf
					
					;[End Block]
				Case 2 ;Audio
					SetFont Font1
					;[Block]
					y = y + 50*MenuScale
					
					MusicVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, MusicVolume*100.0)/100.0)
					Color 255,255,255
					Text(x, y + 2, "音乐大小：")
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					PrevSFXVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, SFXVolume*100.0)/100.0)
					If (Not DeafPlayer) Then SFXVolume# = PrevSFXVolume#
					Color 255,255,255
					Text(x, y + 2, "音效大小：")
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					Text x, y + 3, "自动释放音频："
					EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableSFXRelease,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					Text x, y + 3, "启用自定频道："
					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
					EndIf
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						Color 255,255,255
						Text x, y + 3, "自定频道模式："
						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							Text x, y + 20 * MenuScale + 3, "循环"
						Else
							Text x, y + 20 * MenuScale + 3, "随机"
						EndIf
						If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
						EndIf
					EndIf
					;[End Block]
				Case 3 ;Controls
					SetFont Font1
					;[Block]
					y = y + 50*MenuScale
					
					MouseSens = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
					Color(255, 255, 255)
					Text(x, y + 2, "鼠标灵敏度：")
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSens)
					EndIf
					
					y = y + 30*MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4, "反转鼠标Y轴：")
					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
					EndIf
					
					y = y + 40*MenuScale
					
					MouseSmooth = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSmooth)*50.0)/50.0)
					Color(255, 255, 255)
					Text(x, y + 2, "鼠标平滑度：")
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing",MouseSmooth)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30*MenuScale
					Text(x, y + 2, "键位设置：")
					y = y + 10*MenuScale
					
					Text(x, y + 20 * MenuScale, "向前")
					InputBox(x + 200 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_UP,210)),5)		
					Text(x, y + 40 * MenuScale, "向左")
					InputBox(x + 200 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_LEFT,210)),3)	
					Text(x, y + 60 * MenuScale, "后退")
					InputBox(x + 200 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_DOWN,210)),6)				
					Text(x, y + 80 * MenuScale, "向右")
					InputBox(x + 200 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_RIGHT,210)),4)
					
					Text(x, y + 100 * MenuScale, "手动眨眼")
					InputBox(x + 200 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_BLINK,210)),7)				
					Text(x, y + 120 * MenuScale, "奔跑")
					InputBox(x + 200 * MenuScale, y + 120 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SPRINT,210)),8)
					Text(x, y + 140 * MenuScale, "物品栏")
					InputBox(x + 200 * MenuScale, y + 140 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_INV,210)),9)
					Text(x, y + 160 * MenuScale, "蹲下")
					InputBox(x + 200 * MenuScale, y + 160 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CROUCH,210)),10)
					Text(x, y + 180 * MenuScale, "快速保存")
					InputBox(x + 200 * MenuScale, y + 180 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SAVE,210)),11)	
					Text(x, y + 200 * MenuScale, "控制台")
					InputBox(x + 200 * MenuScale, y + 200 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CONSOLE,210)),12)
					
					If MouseOn(x,y,300*MenuScale,220*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"controls")
					EndIf
					
					For i = 0 To 227
						If KeyHit(i) Then key = i : Exit
					Next
					If key <> 0 Then
						Select SelectedInputBox
							Case 3
								KEY_LEFT = key
							Case 4
								KEY_RIGHT = key
							Case 5
								KEY_UP = key
							Case 6
								KEY_DOWN = key
							Case 7
								KEY_BLINK = key
							Case 8
								KEY_SPRINT = key
							Case 9
								KEY_INV = key
							Case 10
								KEY_CROUCH = key
							Case 11
								KEY_SAVE = key
							Case 12
								KEY_CONSOLE = key
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				Case 4 ;Advanced
					SetFont Font1
					;[Block]
					y = y + 50*MenuScale
					
					Color 255,255,255				
					Text(x, y + 3, "显示HUD：")	
					HUDenabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"hud")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y + 3, "启用控制台：")
					CanOpenConsole = DrawTick(x +270 * MenuScale, y + MenuScale, CanOpenConsole)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y + 3, "出现错误时打开控制台：")
					ConsoleOpening = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleOpening)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					Text(x, y + 3, "成就提示：")
					AchvMSGenabled% = DrawTick(x + 270 * MenuScale, y, AchvMSGenabled%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					Text(x, y + 3, "显示FPS：")
					ShowFPS% = DrawTick(x + 270 * MenuScale, y, ShowFPS%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"showfps")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y + 3, "帧数限制：")
					
					Color 255,255,255
					If DrawTick(x + 270 * MenuScale, y, CurrFrameLimit > 0.0) Then
						CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*99.0)/99.0)
						CurrFrameLimit# = Max(CurrFrameLimit, 0.01)
						Framelimit% = 19+(CurrFrameLimit*100.0)
						Color 255,255,0
						Text(x + 5 * MenuScale, y + 40 * MenuScale, Framelimit%+" FPS")
						If MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
						EndIf
					Else
						CurrFrameLimit# = 0.0
						Framelimit = 0
					EndIf
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
					EndIf
					
					Color 255,255,255
					y = y + 70*MenuScale
					
					Text(x, y + 3, "显示字幕：")
					EnableSubtitles% = DrawTick(x + 270 * MenuScale, y, EnableSubtitles%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"subtitle")
					EndIf
					
					Color 255,255,255
					y = y + 30*MenuScale
					
					Text(x, y + 3, "繁简转换：")
					TraditionalChinese% = DrawTick(x + 270 * MenuScale, y, TraditionalChinese%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"traditional")
					EndIf
					;[End Block]
			End Select
		ElseIf AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG > 0 And KillTimer >= 0
			Local QuitButton% = 60 
			If SelectedDifficulty\saveType = SAVEONQUIT Or SelectedDifficulty\saveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name$
				Local AbleToSave% = True
				If RN$ = "173" Or RN$ = "exit1" Or RN$ = "gatea" Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave
					QuitButton = 140
					If DrawButton(x, y + 60*MenuScale, 390*MenuScale, 60*MenuScale, "保存并退出", True, False, -2) Then
						DropSpeed = 0
						SaveGame(SavePath + CurrSave + "\")
						NullGame()
						MenuOpen = False
						MainMenuOpen = True
						MainMenuTab = 0
						CurrSave = ""
						FlushKeys()
					EndIf
				EndIf
			EndIf
			
			If DrawButton(x, y + QuitButton*MenuScale, 390*MenuScale, 60*MenuScale, "退出", True, False, -2) Then
				NullGame()
				MenuOpen = False
				MainMenuOpen = True
				MainMenuTab = 0
				CurrSave = ""
				FlushKeys()
			EndIf
			
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, "返回", True, False, -2) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
		Else
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, "返回", True, False, -2) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
			
			If AchievementsMenu>0 Then
				;DebugLog AchievementsMenu
				If AchievementsMenu <= Floor(Float(MAXACHIEVEMENTS-1)/12.0) Then 
					If DrawButton(x+341*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, ">") Then
						AchievementsMenu = AchievementsMenu+1
					EndIf
				EndIf
				If AchievementsMenu > 1 Then
					If DrawButton(x+41*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "<") Then
						AchievementsMenu = AchievementsMenu-1
					EndIf
				EndIf
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG,y+((i/4)*120*MenuScale),i+((AchievementsMenu-1)*12))
					Else
						Exit
					EndIf
				Next
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG+((i Mod 4)*SeparationConst),y+((i/4)*120*MenuScale),64*scale,64*scale) Then
							AchievementTooltip(i+((AchievementsMenu-1)*12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
				
			EndIf
		EndIf
		
		y = y+10
		
		If AchievementsMenu<=0 And OptionsMenu<=0 And QuitMSG<=0 Then
			If KillTimer >= 0 Then	
				y = y+ 72*MenuScale
				
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "继续", True, False, -2) Then
					MenuOpen = False
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				EndIf
				
				y = y + 75*MenuScale
				If (Not SelectedDifficulty\permaDeath) Then
					If GameSaved Then
						If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "加载游戏", True, False) Then
							DrawLoading(0)
							
							MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse viewport_center_x,viewport_center_y
							SetFont Font1
							HidePointer ()
							
							FlushKeys()
							FlushMouse()
							Playable=True
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(Collider) - EntityX(r\obj))
								z = Abs(EntityZ(Collider) - EntityZ(r\obj))
								
								If x < 12.0 And z < 12.0 Then
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
										MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
									EndIf
								End If
							Next
							
							DrawLoading(100)
							
							DropSpeed=0
							
							UpdateWorld 0.0
							
							PrevTime = MilliSecs()
							FPSfactor = 0
							
							ResetInput()
						EndIf
					Else
						DrawFrame(x,y,390*MenuScale, 60*MenuScale)
						Color (100, 100, 100)
						SetFont Font2
						Text(x + (390*MenuScale) / 2, y + (60*MenuScale) / 2 + 2, "加载游戏", True, True)
					EndIf
					y = y + 75*MenuScale
			EndIf
				
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "成就", True, False, -2) Then AchievementsMenu = 1
				y = y + 75*MenuScale
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "设置", True, False, -2) Then OptionsMenu = 1
				y = y + 75*MenuScale
			Else
				y = y+104*MenuScale
				If GameSaved And (Not SelectedDifficulty\permaDeath) Then
					If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "加载游戏", True, False, -2) Then
						DrawLoading(0)
						
						MenuOpen = False
						LoadGameQuick(SavePath + CurrSave + "\")
						
						MoveMouse viewport_center_x,viewport_center_y
						SetFont Font1
						HidePointer ()
						
						FlushKeys()
						FlushMouse()
						Playable=True
						
						UpdateRooms()
						
						For r.Rooms = Each Rooms
							x = Abs(EntityX(Collider) - EntityX(r\obj))
							z = Abs(EntityZ(Collider) - EntityZ(r\obj))
							
							If x < 12.0 And z < 12.0 Then
								MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
								If x < 4.0 And z < 4.0 Then
									If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
								EndIf
							End If
						Next
						
						DrawLoading(100)
						
						DropSpeed=0
						
						UpdateWorld 0.0
						
						PrevTime = MilliSecs()
						FPSfactor = 0
						
						ResetInput()
					EndIf
				Else
					DrawButton(x, y, 390*MenuScale, 60*MenuScale, "")
					Color 50,50,50
					Text(x + 185*MenuScale, y + 30*MenuScale, "加载游戏", True, True)
				EndIf
				If DrawButton(x, y + 80*MenuScale, 390*MenuScale, 60*MenuScale, "返回主菜单", True, False, -2) Then
					NullGame()
					MenuOpen = False
					MainMenuOpen = True
					MainMenuTab = 0
					CurrSave = ""
					FlushKeys()
				EndIf
				y= y + 80*MenuScale
			EndIf
			
			If KillTimer >= 0 And (Not MainMenuOpen)
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, "退出", True, False, -2) Then
					QuitMSG = 1
				EndIf
			EndIf
			
			SetFont Font1
			If KillTimer < 0 Then RowText(DeathMSG$, x, y + 80*MenuScale, 390*MenuScale, 600*MenuScale)
		EndIf
		
		If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
	End If
	
	SetFont Font1
	
	CatchErrors("DrawMenu")
End Function

Function MouseOn%(x%, y%, width%, height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + height Then
			Return True
		End If
	End If
	Return False
End Function

;----------------------------------------------------------------------------------------------

Include "LoadAllSounds.bb"
Function LoadEntities()
	CatchErrors("Uncaught (LoadEntities)")
	DrawLoading(0)
	
	Local i%
	
	For i=0 To 9
		TempSounds[i]=0
	Next
	
	PauseMenuIMG% = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	MaskImage PauseMenuIMG, 255,255,0
	ScaleImage PauseMenuIMG,MenuScale,MenuScale
	
	SprintIcon% = LoadImage_Strict("GFX\sprinticon.png")
	BlinkIcon% = LoadImage_Strict("GFX\blinkicon.png")
	CrouchIcon% = LoadImage_Strict("GFX\sneakicon.png")
	HandIcon% = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2% = LoadImage_Strict("GFX\handsymbol2.png")

	StaminaMeterIMG% = LoadImage_Strict("GFX\staminameter.jpg")

	KeypadHUD =  LoadImage_Strict("GFX\keypadhud.jpg")
	MaskImage(KeypadHUD, 255,0,255)

	Panel294 = LoadImage_Strict("GFX\294panel.jpg")
	MaskImage(Panel294, 255,0,255)
	
	
	Brightness% = GetINIFloat("options.ini", "options", "brightness")
	CameraFogNear# = GetINIFloat("options.ini", "options", "camera fog near")
	CameraFogFar# = GetINIFloat("options.ini", "options", "camera fog far")
	StoredCameraFogFar# = CameraFogFar
	
	;TextureLodBias
	
	AmbientLightRoomTex% = CreateTexture(2,2,257)
	TextureBlend AmbientLightRoomTex,5
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport Camera,0,0,GraphicWidth,GraphicHeight
	CameraRange(Camera, 0.05, CameraFogFar)
	CameraFogMode (Camera, 1)
	CameraFogRange (Camera, CameraFogNear, CameraFogFar)
	CameraFogColor (Camera, GetINIInt("options.ini", "options", "fog r"), GetINIInt("options.ini", "options", "fog g"), GetINIInt("options.ini", "options", "fog b"))
	AmbientLight Brightness, Brightness, Brightness
	
	ScreenTexs[0] = CreateTexture(512, 512, 1+256)
	ScreenTexs[1] = CreateTexture(512, 512, 1+256)
	
	CreateBlurImage()
	CameraProjMode ark_blur_cam,0
	;Listener = CreateListener(Camera)
	
	FogTexture = LoadTexture_Strict("GFX\fog.jpg", 1)
	
	Fog = CreateSprite(ark_blur_cam)
	ScaleSprite(Fog, Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(Fog, FogTexture)
	EntityBlend (Fog, 2)
	EntityOrder Fog, -1000
	MoveEntity(Fog, 0, 0, 1.0)
	
	GasMaskTexture = LoadTexture_Strict("GFX\GasmaskOverlay.jpg", 1)
	GasMaskOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(GasMaskOverlay, Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(GasMaskOverlay, GasMaskTexture)
	EntityBlend (GasMaskOverlay, 2)
	EntityFX(GasMaskOverlay, 1)
	EntityOrder GasMaskOverlay, -1003
	MoveEntity(GasMaskOverlay, 0, 0, 1.0)
	HideEntity(GasMaskOverlay)
	
	InfectTexture = LoadTexture_Strict("GFX\InfectOverlay.jpg", 1)
	InfectOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(InfectOverlay, Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(InfectOverlay, InfectTexture)
	EntityBlend (InfectOverlay, 3)
	EntityFX(InfectOverlay, 1)
	EntityOrder InfectOverlay, -1003
	MoveEntity(InfectOverlay, 0, 0, 1.0)
	;EntityAlpha (InfectOverlay, 255.0)
	HideEntity(InfectOverlay)
	
	NVTexture = LoadTexture_Strict("GFX\NightVisionOverlay.jpg", 1)
	NVOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(NVOverlay, Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityTexture(NVOverlay, NVTexture)
	EntityBlend (NVOverlay, 2)
	EntityFX(NVOverlay, 1)
	EntityOrder NVOverlay, -1003
	MoveEntity(NVOverlay, 0, 0, 1.0)
	HideEntity(NVOverlay)
	NVBlink = CreateSprite(ark_blur_cam)
	ScaleSprite(NVBlink, Max(GraphicWidth / 1024.0, 1.0), Max(GraphicHeight / 1024.0 * 0.8, 0.8))
	EntityColor(NVBlink,0,0,0)
	EntityFX(NVBlink, 1)
	EntityOrder NVBlink, -1005
	MoveEntity(NVBlink, 0, 0, 1.0)
	HideEntity(NVBlink)
	
	FogNVTexture = LoadTexture_Strict("GFX\fogNV.jpg", 1)
	
	DrawLoading(5)
	
	DarkTexture = CreateTexture(1024, 1024, 1 + 2)
	SetBuffer TextureBuffer(DarkTexture)
	Cls
	SetBuffer BackBuffer()
	
	Dark = CreateSprite(Camera)
	ScaleSprite(Dark, Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(Dark, DarkTexture)
	EntityBlend (Dark, 1)
	EntityOrder Dark, -1002
	MoveEntity(Dark, 0, 0, 1.0)
	EntityAlpha Dark, 0.0
	
	LightTexture = CreateTexture(1024, 1024, 1 + 2+256)
	SetBuffer TextureBuffer(LightTexture)
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	
	TeslaTexture = LoadTexture_Strict("GFX\map\tesla.jpg", 1+2)
	
	Light = CreateSprite(Camera)
	ScaleSprite(Light, Max(GraphicWidth / 1240.0, 1.0), Max(GraphicHeight / 960.0 * 0.8, 0.8))
	EntityTexture(Light, LightTexture)
	EntityBlend (Light, 1)
	EntityOrder Light, -1002
	MoveEntity(Light, 0, 0, 1.0)
	HideEntity Light
	
	Collider = CreatePivot()
	EntityRadius Collider, 0.15, 0.30
	EntityPickMode(Collider, 1)
	EntityType Collider, HIT_PLAYER
	
	Head = CreatePivot()
	EntityRadius Head, 0.15
	EntityType Head, HIT_PLAYER
	
	
	LiquidObj = LoadMesh_Strict("GFX\items\cupliquid.x") ;optimized the cups dispensed by 294
	HideEntity LiquidObj
	
	MTFObj = LoadAnimMesh_Strict("GFX\npcs\MTF2.b3d") ;optimized MTFs
	GuardObj = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ;optimized Guards
	
	ClassDObj = LoadAnimMesh_Strict("GFX\npcs\classd.b3d") ;optimized Class-D's and scientists/researchers
	ApacheObj = LoadAnimMesh_Strict("GFX\apache.b3d") ;optimized Apaches (helicopters)
	ApacheRotorObj = LoadAnimMesh_Strict("GFX\apacherotor.b3d") ;optimized the Apaches even more
	
	HideEntity MTFObj
	HideEntity GuardObj
	HideEntity ClassDObj
	HideEntity ApacheObj
	HideEntity ApacheRotorObj
	
	;Other NPCs pre-loaded
	;[Block]
	NPC049OBJ = LoadAnimMesh_Strict("GFX\npcs\scp-049.b3d")
	HideEntity NPC049OBJ
	NPC0492OBJ = LoadAnimMesh_Strict("GFX\npcs\zombie1.b3d")
	HideEntity NPC0492OBJ
	ClerkOBJ = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d")
	HideEntity ClerkOBJ	
	;[End Block]
	
	LightSpriteTex(0) = LoadTexture_Strict("GFX\light1.jpg", 1)
	LightSpriteTex(1) = LoadTexture_Strict("GFX\light2.jpg", 1)
	LightSpriteTex(2) = LoadTexture_Strict("GFX\lightsprite.jpg",1)
	
	DrawLoading(10)
	
	DoorOBJ = LoadMesh_Strict("GFX\map\door01.x")
	HideEntity DoorOBJ
	DoorFrameOBJ = LoadMesh_Strict("GFX\map\doorframe.x")
	HideEntity DoorFrameOBJ
	
	HeavyDoorObj(0) = LoadMesh_Strict("GFX\map\heavydoor1.x")
	HideEntity HeavyDoorObj(0)
	HeavyDoorObj(1) = LoadMesh_Strict("GFX\map\heavydoor2.x")
	HideEntity HeavyDoorObj(1)
	
	DoorColl = LoadMesh_Strict("GFX\map\doorcoll.x")
	HideEntity DoorColl
	
	ButtonOBJ = LoadMesh_Strict("GFX\map\Button.x")
	HideEntity ButtonOBJ
	ButtonKeyOBJ = LoadMesh_Strict("GFX\map\ButtonKeycard.x")
	HideEntity ButtonKeyOBJ
	ButtonCodeOBJ = LoadMesh_Strict("GFX\map\ButtonCode.x")
	HideEntity ButtonCodeOBJ	
	ButtonScannerOBJ = LoadMesh_Strict("GFX\map\ButtonScanner.x")
	HideEntity ButtonScannerOBJ	
	
	BigDoorOBJ(0) = LoadMesh_Strict("GFX\map\ContDoorLeft.x")
	HideEntity BigDoorOBJ(0)
	BigDoorOBJ(1) = LoadMesh_Strict("GFX\map\ContDoorRight.x")
	HideEntity BigDoorOBJ(1)
	
	LeverBaseOBJ = LoadMesh_Strict("GFX\map\leverbase.x")
	HideEntity LeverBaseOBJ
	LeverOBJ = LoadMesh_Strict("GFX\map\leverhandle.x")
	HideEntity LeverOBJ
	
	DrawLoading(15)
	
	For i = 0 To 5
		GorePics(i) = LoadTexture_Strict("GFX\895pics\pic" + (i + 1) + ".jpg")
	Next
	
	OldAiPics(0) = LoadTexture_Strict("GFX\AIface.jpg")
	OldAiPics(1) = LoadTexture_Strict("GFX\AIface2.jpg")	
	
	DrawLoading(20)
	
	For i = 0 To 6
		DecalTextures(i) = LoadTexture_Strict("GFX\decal" + (i + 1) + ".png", 1 + 2)
	Next
	DecalTextures(7) = LoadTexture_Strict("GFX\items\INVpaperstrips.jpg", 1 + 2)
	For i = 8 To 12
		DecalTextures(i) = LoadTexture_Strict("GFX\decalpd"+(i-7)+".jpg", 1 + 2)	
	Next
	For i = 13 To 14
		DecalTextures(i) = LoadTexture_Strict("GFX\bullethole"+(i-12)+".jpg", 1 + 2)	
	Next	
	For i = 15 To 16
		DecalTextures(i) = LoadTexture_Strict("GFX\blooddrop"+(i-14)+".png", 1 + 2)	
	Next
	DecalTextures(17) = LoadTexture_Strict("GFX\decal8.png", 1 + 2)	
	DecalTextures(18) = LoadTexture_Strict("GFX\decalpd6.png", 1 + 2)	
	DecalTextures(19) = LoadTexture_Strict("GFX\decal19.png", 1 + 2)
	DecalTextures(20) = LoadTexture_Strict("GFX\decal427.png", 1 + 2)
	
	DrawLoading(25)
	
	Monitor = LoadMesh_Strict("GFX\map\monitor.b3d")
	HideEntity Monitor
	MonitorTexture = LoadTexture_Strict("GFX\monitortexture.jpg")
	
	CamBaseOBJ = LoadMesh_Strict("GFX\map\cambase.x")
	HideEntity(CamBaseOBJ)
	CamOBJ = LoadMesh_Strict("GFX\map\CamHead.b3d")
	HideEntity(CamOBJ)
	
	Monitor2 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity Monitor2
	Monitor3 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity Monitor3
	MonitorTexture2 = LoadTexture_Strict("GFX\map\LockdownScreen2.jpg")
	MonitorTexture3 = LoadTexture_Strict("GFX\map\LockdownScreen.jpg")
	MonitorTexture4 = LoadTexture_Strict("GFX\map\LockdownScreen3.jpg")
	MonitorTextureOff = CreateTexture(1,1)
	SetBuffer TextureBuffer(MonitorTextureOff)
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	LightConeModel = LoadMesh_Strict("GFX\lightcone.b3d")
	HideEntity LightConeModel
	
	For i = 2 To CountSurfaces(Monitor2)
		sf = GetSurface(Monitor2,i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitortexture.jpg"
					BrushTexture b, MonitorTextureOff, 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	For i = 2 To CountSurfaces(Monitor3)
		sf = GetSurface(Monitor3,i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitortexture.jpg"
					BrushTexture b, MonitorTextureOff, 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	
	UserTrackMusicAmount% = 0
	If EnableUserTracks Then
		Local dirPath$ = "SFX\Radio\UserTracks\"
		If FileType(dirPath)<>2 Then
			CreateDir(dirPath)
		EndIf
		
		Local Dir% = ReadDir("SFX\Radio\UserTracks\")
		Repeat
			file$=NextFile(Dir)
			If file$="" Then Exit
			If FileType("SFX\Radio\UserTracks\"+file$) = 1 Then
				test = LoadSound("SFX\Radio\UserTracks\"+file$)
				If test<>0
					UserTrackName$(UserTrackMusicAmount%) = file$
					UserTrackMusicAmount% = UserTrackMusicAmount% + 1
				EndIf
				FreeSound test
			EndIf
		Forever
		CloseDir Dir
	EndIf
	If EnableUserTracks Then DebugLog "User Tracks found: "+UserTrackMusicAmount
	
	InitItemTemplates()
	
	ParticleTextures(0) = LoadTexture_Strict("GFX\smoke.png", 1 + 2)
	ParticleTextures(1) = LoadTexture_Strict("GFX\flash.jpg", 1 + 2)
	ParticleTextures(2) = LoadTexture_Strict("GFX\dust.jpg", 1 + 2)
	ParticleTextures(3) = LoadTexture_Strict("GFX\npcs\hg.jpg", 1 + 2)
	ParticleTextures(4) = LoadTexture_Strict("GFX\map\sun.jpg", 1 + 2)
	ParticleTextures(5) = LoadTexture_Strict("GFX\bloodsprite.png", 1 + 2)
	ParticleTextures(6) = LoadTexture_Strict("GFX\smoke2.png", 1 + 2)
	ParticleTextures(7) = LoadTexture_Strict("GFX\spark.jpg", 1 + 2)
	ParticleTextures(8) = LoadTexture_Strict("GFX\particle.png", 1 + 2)
	
	SetChunkDataValues()
	
	;NPCtypeD - different models with different textures (loaded using "CopyEntity") - ENDSHN
	;[Block]
	For i=1 To MaxDTextures
		DTextures[i] = CopyEntity(ClassDObj)
		HideEntity DTextures[i]
	Next
	;Gonzales
	tex = LoadTexture_Strict("GFX\npcs\gonzales.jpg")
	EntityTexture DTextures[1],tex
	FreeTexture tex
	;SCP-970 corpse
	tex = LoadTexture_Strict("GFX\npcs\corpse.jpg")
	EntityTexture DTextures[2],tex
	FreeTexture tex
	;scientist 1
	tex = LoadTexture_Strict("GFX\npcs\scientist.jpg")
	EntityTexture DTextures[3],tex
	FreeTexture tex
	;scientist 2
	tex = LoadTexture_Strict("GFX\npcs\scientist2.jpg")
	EntityTexture DTextures[4],tex
	FreeTexture tex
	;janitor
	tex = LoadTexture_Strict("GFX\npcs\janitor.jpg")
	EntityTexture DTextures[5],tex
	FreeTexture tex
	;106 Victim
	tex = LoadTexture_Strict("GFX\npcs\106victim.jpg")
	EntityTexture DTextures[6],tex
	FreeTexture tex
	;2nd ClassD
	tex = LoadTexture_Strict("GFX\npcs\classd2.jpg")
	EntityTexture DTextures[7],tex
	FreeTexture tex
	;035 victim
	tex = LoadTexture_Strict("GFX\npcs\035victim.jpg")
	EntityTexture DTextures[8],tex
	FreeTexture tex
	
	;[End Block]
	
	LoadMaterials("DATA\materials.ini")
	
	OBJTunnel(0)=LoadRMesh("GFX\map\mt1.rmesh",Null)	
	HideEntity OBJTunnel(0)				
	OBJTunnel(1)=LoadRMesh("GFX\map\mt2.rmesh",Null)	
	HideEntity OBJTunnel(1)
	OBJTunnel(2)=LoadRMesh("GFX\map\mt2c.rmesh",Null)	
	HideEntity OBJTunnel(2)				
	OBJTunnel(3)=LoadRMesh("GFX\map\mt3.rmesh",Null)	
	HideEntity OBJTunnel(3)	
	OBJTunnel(4)=LoadRMesh("GFX\map\mt4.rmesh",Null)	
	HideEntity OBJTunnel(4)				
	OBJTunnel(5)=LoadRMesh("GFX\map\mt_elevator.rmesh",Null)
	HideEntity OBJTunnel(5)
	OBJTunnel(6)=LoadRMesh("GFX\map\mt_generator.rmesh",Null)
	HideEntity OBJTunnel(6)
	
	TextureLodBias TextureFloat#
	;Devil Particle System
	;ParticleEffect[] numbers:
	;	0 - electric spark
	;	1 - smoke effect
	
	Local t0
	
	InitParticles(Camera)
	
	;Spark Effect (short)
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 3)
	SetTemplateInterval(ParticleEffect[0], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[0], 6)
	SetTemplateEmitterLifeTime(ParticleEffect[0], 6)
	SetTemplateParticleLifeTime(ParticleEffect[0], 20, 30)
	SetTemplateTexture(ParticleEffect[0], "GFX\Spark.png", 2, 3)
	SetTemplateOffset(ParticleEffect[0], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[0], -0.0375, 0.0375, -0.0375, 0.0375, -0.0375, 0.0375)
	SetTemplateAlignToFall(ParticleEffect[0], True, 45)
	SetTemplateGravity(ParticleEffect[0], 0.001)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	SetTemplateSize(ParticleEffect[0], 0.03125, 0.0625, 0.7, 1)
	SetTemplateColors(ParticleEffect[0], $0000FF, $6565FF)
	SetTemplateFloor(ParticleEffect[0], 0.0, 0.5)
	
	;Smoke effect (for some vents)
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateInterval(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], 3)
	SetTemplateParticleLifeTime(ParticleEffect[1], 30, 45)
	SetTemplateTexture(ParticleEffect[1], "GFX\smoke2.png", 2, 1)
	SetTemplateOffset(ParticleEffect[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[1], 0.0, 0.0, 0.02, 0.025, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	SetTemplateSize(ParticleEffect[1], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], .01, 1.01)
	
	;Smoke effect (for decontamination gas)
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateInterval(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 3)
	SetTemplateParticleLifeTime(ParticleEffect[2], 30, 45)
	SetTemplateTexture(ParticleEffect[2], "GFX\smoke.png", 2, 1)
	SetTemplateOffset(ParticleEffect[2], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[2], -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[2], True)
	SetTemplateSize(ParticleEffect[2], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[2], .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, 3)
	SetTemplateParticleLifeTime(t0, 30, 45)
	SetTemplateTexture(t0, "GFX\smoke2.png", 2, 1)
	SetTemplateOffset(t0, -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(t0, -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(t0, .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	SetTemplateSubTemplate(ParticleEffect[2], t0)
	
	Room2slCam = CreateCamera()
	CameraViewport(Room2slCam, 0, 0, 128, 128)
	CameraRange Room2slCam, 0.05, 6.0
	CameraZoom(Room2slCam, 0.8)
	HideEntity(Room2slCam)
	
	DrawLoading(30)
	CatchErrors("LoadEntities")
End Function

Function InitNewGame()
	CatchErrors("Uncaught (InitNewGame)")
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events
	
	DrawLoading(45)
	
	HideDistance# = 15.0
	
	HeartBeatRate = 70
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1,9)*(10^i)
	Next	
	
	If SelectedMap = "" Then
		CreateMap()
	Else
		LoadMap(ConvertToANSI("地图制作器\Maps\")+SelectedMap)
	EndIf
	InitWayPoints()
	
	DrawLoading(79)
	
	Curr173 = CreateNPC(NPCtype173, 0, -30.0, 0)
	Curr106 = CreateNPC(NPCtypeOldMan, 0, -30.0, 0)
	Curr106\State = 70 * 60 * Rand(12,17)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
		If d\obj2 <> 0 And d\dir = 0 Then
			MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
			MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
		EndIf	
	Next
	
	For it.Items = Each Items
		EntityType (it\collider, HIT_ITEM)
		EntityParent(it\collider, 0)
	Next
	
	DrawLoading(80)
	For sc.SecurityCams= Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights
			If r\Lights[i]<>0 Then 
				EntityParent(r\Lights[i],0)
			Else
				Exit
			EndIf
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(Rand(2, 3), EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.1, 0.4) : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.85, 0.95))
			EndIf
			
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(0, EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.5, 0.7) : EntityAlpha(de\obj, 0.7) : de\ID = 1 : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.7, 0.85))
			EndIf
		EndIf
		
		If (r\RoomTemplate\Name = "start" And IntroEnabled = False) Then 
			PositionEntity (Collider, EntityX(r\obj)+3584*RoomScale, 704*RoomScale, EntityZ(r\obj)+1024*RoomScale)
			PlayerRoom = r
			it = CreateItem("Class D Orientation Leaflet", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\itemtemplate\found=True
			Inventory(0) = it
			HideEntity(it\collider)
			EntityType (it\collider, HIT_ITEM)
			EntityParent(it\collider, 0)
			ItemAmount = ItemAmount + 1
			it = CreateItem("Document SCP-173", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\itemtemplate\found=True
			Inventory(1) = it
			HideEntity(it\collider)
			EntityType (it\collider, HIT_ITEM)
			EntityParent(it\collider, 0)
			ItemAmount = ItemAmount + 1
		ElseIf (r\RoomTemplate\Name = "173" And IntroEnabled) Then
			PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
			PlayerRoom = r
		EndIf
		
	Next
	
	Local rt.RoomTemplates
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity (rt\obj)
	Next	
	
	Local tw.TempWayPoints
	For tw.TempWayPoints = Each TempWayPoints
		Delete tw
	Next

	Local ts.TempScreens
	For ts.TempScreens = Each TempScreens
		Delete ts
	Next
	
	TurnEntity(Collider, 0, Rand(160, 200), 0)
	
	ResetEntity Collider
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2nuke"
			e\EventState = 1
			DebugLog "room2nuke"
		EndIf
		If e\EventName = "room106"
			e\EventState2 = 1
			DebugLog "room106"
		EndIf	
		If e\EventName = "room2sl"
			e\EventState3 = 1
			DebugLog "room2sl"
		EndIf
	Next
	
	MoveMouse viewport_center_x,viewport_center_y;320, 240
	
	SetFont Font1
	
	HidePointer()
	
	BlinkTimer = -10
	BlurTimer = 100
	Stamina = 100
	
	For i% = 0 To 70
		FPSfactor = 1.0
		FlushKeys()
		MovePlayer()
		UpdateDoors()
		UpdateNPCs()
		UpdateWorld()
		;Cls
		If (Int(Float(i)*0.27)<>Int(Float(i-1)*0.27)) Then
			DrawLoading(80+Int(Float(i)*0.27))
		EndIf
	Next
	
	FreeTextureCache
	DrawLoading(100)
	
	FlushKeys
	FlushMouse
	
	DropSpeed = 0
	
	PrevTime = MilliSecs()
	CatchErrors("InitNewGame")
End Function

Function InitLoadGame()
	CatchErrors("Uncaught (InitLoadGame)")
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	
	DrawLoading(80)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next
	
	ResetEntity Collider
	
	DrawLoading(90)
	
	MoveMouse viewport_center_x,viewport_center_y
	
	SetFont Font1
	
	HidePointer ()
	
	BlinkTimer = BLINKFREQ
	Stamina = 100
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\obj <> 0 Then FreeEntity(rt\obj) : rt\obj = 0
	Next

	Local tw.TempWayPoints
	For tw.TempWayPoints = Each TempWayPoints
		Delete tw
	Next

	Local ts.TempScreens
	For ts.TempScreens = Each TempScreens
		Delete ts
	Next
	
	DropSpeed = 0.0
	
	For e.Events = Each Events
		;Loading the necessary stuff for dimension1499, but this will only be done if the player is in this dimension already
		If e\EventName = "dimension1499"
			If e\EventState = 2
				;[Block]
				DrawLoading(91)
				e\room\Objects[0] = LoadMesh_Strict("GFX\map\dimension1499\1499plane.b3d")
				HideEntity(e\room\Objects[0])
				DrawLoading(92)
				NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
				DrawLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict("GFX\map\dimension1499\1499object"+i+".b3d")
					HideEntity e\room\Objects[i]
				Next
				DrawLoading(96)
				CreateChunkParts(e\room)
				DrawLoading(97)
				x# = EntityX(e\room\obj)
				z# = EntityZ(e\room\obj)
				Local ch.Chunk
				For i = -2 To 2 Step 2
					ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#,True)
					ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#-40,True)
				Next
				DrawLoading(98)
				UpdateChunks(e\room,15,False)
				
				DebugLog "Loaded dimension1499 successful"
				
				Exit
				;[End Block]
			EndIf
		EndIf
	Next
	
	FreeTextureCache
	
	CatchErrors("InitLoadGame")
	DrawLoading(100)
	
	PrevTime = MilliSecs()
	FPSfactor = 0
	ResetInput()
	
End Function

Function NullGame(playbuttonsfx%=True)
	CatchErrors("Uncaught (NullGame)")
	Local i%, x%, y%, lvl
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items
	
	KillSounds()
	If playbuttonsfx Then PlaySound_Strict ButtonSFX
	
	FreeParticles()
	
	ClearTextureCache
	
	DebugHUD = False
	
	UnableToMove% = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer# = 0
	QuickLoad_CurrEvent = Null
	
	DeathMSG$=""
	
	SelectedMap = ""
	
	UsedConsole = False
	
	DoorTempID = 0
	RoomTempID = 0
	
	GameSaved = 0
	
	HideDistance# = 15.0
	
	For lvl = 0 To 0
		For x = 0 To MapWidth+1
			For y = 0 To MapHeight+1
				MapTemp(x, y) = 0
				MapFound(x, y) = 0
			Next
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = False
	Next
	
	DropSpeed = 0
	Shake = 0
	CurrSpeed = 0
	
	DeathTimer=0
	
	HeartBeatVolume = 0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0
	BlinkEffect = 1.0
	BlinkEffectTimer = 0
	
	Bloodloss = 0
	Injuries = 0
	Infect = 0
	
	For i = 0 To 5
		SCP1025state[i] = 0
	Next
	
	SelectedEnding = ""
	EndingTimer = 0
	ExplosionTimer = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	
	GodMode = 0
	NoClip = 0
	WireframeState = 0
	WireFrame 0
	WearingGasMask = 0
	WearingHazmat = 0
	WearingVest = 0
	Wearing714 = 0
	If WearingNightVision Then
		CameraFogFar = StoredCameraFogFar
		WearingNightVision = 0
	EndIf
	I_427\Using = 0
	I_427\Timer = 0.0
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
	CoffinDistance = 100
	
	Contained106 = False
	If Curr173 <> Null Then Curr173\Idle = False
	
	MTFtimer = 0
	For i = 0 To 9
		MTFrooms[i]=Null
		MTFroomState[i]=0
	Next
	
	For s.Screens = Each Screens
		If s\img <> 0 Then FreeImage s\img : s\img = 0
		Delete s
	Next
	
	For i = 0 To MAXACHIEVEMENTS-1
		Achievements(i)=0
	Next
	RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	EyeIrritation = 0
	EyeStuck = 0
	
	ShouldPlay = 0
	
	KillTimer = 0
	FallTimer = 0
	Stamina = 100
	BlurTimer = 0
	SuperMan = False
	SuperManTimer = 0
	Sanity = 0
	RestoreSanity = True
	Crouch = False
	CrouchState = 0.0
	LightVolume = 0.0
	Vomit = False
	VomitTimer = 0.0
	SecondaryLightOn# = True
	PrevSecondaryLightOn# = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	InfiniteStamina% = False
	
	Msg = ""
	MsgTimer = 0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory(i) = Null
	Next
	SelectedItem = Null
	
	ClosestButton = 0
	
	For d.Doors = Each Doors
		Delete d
	Next
	
	;ClearWorld
	
	For lt.LightTemplates = Each LightTemplates
		Delete lt
	Next 
	
	For m.Materials = Each Materials
		Delete m
	Next
	
	For wp.WayPoints = Each WayPoints
		Delete wp
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete twp
	Next	
	
	For r.Rooms = Each Rooms
		Delete r
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		Delete itt
	Next 
	
	For it.Items = Each Items
		Delete it
	Next
	
	For pr.Props = Each Props
		Delete pr
	Next
	
	For de.decals = Each Decals
		Delete de
	Next
	
	For n.NPCS = Each NPCs
		Delete n
	Next
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	For i = 0 To 6
		MTFrooms[i]=Null
	Next
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound<>0 Then FreeSound_Strict e\Sound
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2
		Delete e
	Next
	
	For sc.securitycams = Each SecurityCams
		Delete sc
	Next
	
	For em.emitters = Each Emitters
		Delete em
	Next	
	
	For p.particles = Each Particles
		Delete p
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = 0
	Next
	
	For i = 0 To 5
		If ChannelPlaying(RadioCHN(i)) Then StopChannel(RadioCHN(i))
	Next
	
	NTF_1499PrevX# = 0.0
	NTF_1499PrevY# = 0.0
	NTF_1499PrevZ# = 0.0
	NTF_1499PrevRoom = Null
	NTF_1499X# = 0.0
	NTF_1499Y# = 0.0
	NTF_1499Z# = 0.0
	Wearing1499% = False
	DeleteChunks()
	
	DeleteElevatorObjects()
	
	DeleteDevilEmitters()
	
	NoTarget% = False
	
	OptionsMenu% = -1
	QuitMSG% = -1
	AchievementsMenu% = -1
	
	MusicVolume# = PrevMusicVolume
	SFXVolume# = PrevSFXVolume
	DeafPlayer% = False
	DeafTimer# = 0.0
	
	IsZombie% = False
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	ClearWorld
	Camera = 0
	ark_blur_cam = 0
	Collider = 0
	Sky = 0
	InitFastResize()
	
	CatchErrors("NullGame")
End Function

Include "save.bb"

;--------------------------------------- music & sounds ----------------------------------------------

Function PlaySound2%(SoundHandle%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range, 1.0)
	Local soundchn% = 0
	
	If volume > 0 Then 
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			soundchn% = PlaySound_Strict (SoundHandle)
			
			ChannelVolume(soundchn, volume# * (1 - dist#)*SFXVolume#)
			ChannelPan(soundchn, panvalue)			
		EndIf
	EndIf
	
	Return soundchn
End Function

Function LoopSound2%(SoundHandle%, Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		Local panvalue# = Sin(-DeltaYaw(cam,entity))
		
		If Chn = 0 Then
			Chn% = PlaySound_Strict (SoundHandle)
		Else
			If (Not ChannelPlaying(Chn)) Then Chn% = PlaySound_Strict (SoundHandle)
		EndIf
		
		ChannelVolume(Chn, volume# * (1 - dist#)*SFXVolume#)
		ChannelPan(Chn, panvalue)
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
	
	Return Chn
End Function

Function LoadTempSound(file$)
	If TempSounds[TempSoundIndex]<>0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(file)
	TempSounds[TempSoundIndex] = TempSound
	
	TempSoundIndex=(TempSoundIndex+1) Mod 10
	
	Return TempSound
End Function

Function LoadEventSound(e.Events,file$,num%=0)
	If num=0 Then
		If e\Sound<>0 Then FreeSound_Strict e\Sound : e\Sound=0
		e\Sound=LoadSound_Strict(file)
		Return e\Sound
	Else If num=1 Then
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2 : e\Sound2=0
		e\Sound2=LoadSound_Strict(file)
		Return e\Sound2
	EndIf
End Function

Function UpdateMusic()
	If ConsoleFlush Then
		If Not ChannelPlaying(ConsoleMusPlay) Then ConsoleMusPlay = PlaySound(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay ; playing the wrong clip, fade out
			CurrMusicVolume# = Max(CurrMusicVolume - (FPSfactor / 250.0), 0)
			If CurrMusicVolume = 0
				If NowPlaying<66
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic=0
			EndIf
		Else ; playing the right clip
			CurrMusicVolume = CurrMusicVolume + (MusicVolume - CurrMusicVolume) * (0.1*FPSfactor)
		EndIf
		
		If NowPlaying < 66
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict("SFX\Music\"+Music(NowPlaying)+".ogg",0.0,Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN,CurrMusicVolume)
		EndIf
	Else
		If FPSfactor > 0 Or OptionsMenu = 2 Then
			;CurrMusicVolume = 1.0
			If (Not ChannelPlaying(MusicCHN)) Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume MusicCHN,1.0*MusicVolume
		EndIf
	EndIf
End Function

Function PauseSounds()
	For e.events = Each Events
		If e\soundchn <> 0 Then
			If (Not e\soundchn_isstream)
				If ChannelPlaying(e\soundchn) Then PauseChannel(e\soundchn)
			Else
				SetStreamPaused_Strict(e\soundchn,True)
			EndIf
		EndIf
		If e\soundchn2 <> 0 Then
			If (Not e\soundchn2_isstream)
				If ChannelPlaying(e\soundchn2) Then PauseChannel(e\soundchn2)
			Else
				SetStreamPaused_Strict(e\soundchn2,True)
			EndIf
		EndIf		
	Next
	
	For n.npcs = Each NPCs
		If n\soundchn <> 0 Then
			If (Not n\soundchn_isstream)
				If ChannelPlaying(n\soundchn) Then PauseChannel(n\soundchn)
			Else
				If n\soundchn_isstream=True
					SetStreamPaused_Strict(n\soundchn,True)
				EndIf
			EndIf
		EndIf
		If n\soundchn2 <> 0 Then
			If (Not n\soundchn2_isstream)
				If ChannelPlaying(n\soundchn2) Then PauseChannel(n\soundchn2)
			Else
				If n\soundchn2_isstream=True
					SetStreamPaused_Strict(n\soundchn2,True)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.doors = Each Doors
		If d\soundchn <> 0 Then
			If ChannelPlaying(d\soundchn) Then PauseChannel(d\soundchn)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\soundchn <> 0 Then
			If ChannelPlaying(dem\soundchn) Then PauseChannel(dem\soundchn)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then PauseChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN,True)
	EndIf
End Function

Function ResumeSounds()
	For e.events = Each Events
		If e\soundchn <> 0 Then
			If (Not e\soundchn_isstream)
				If ChannelPlaying(e\soundchn) Then ResumeChannel(e\soundchn)
			Else
				SetStreamPaused_Strict(e\soundchn,False)
			EndIf
		EndIf
		If e\soundchn2 <> 0 Then
			If (Not e\soundchn2_isstream)
				If ChannelPlaying(e\soundchn2) Then ResumeChannel(e\soundchn2)
			Else
				SetStreamPaused_Strict(e\soundchn2,False)
			EndIf
		EndIf	
	Next
	
	For n.npcs = Each NPCs
		If n\soundchn <> 0 Then
			If (Not n\soundchn_isstream)
				If ChannelPlaying(n\soundchn) Then ResumeChannel(n\soundchn)
			Else
				If n\soundchn_isstream=True
					SetStreamPaused_Strict(n\soundchn,False)
				EndIf
			EndIf
		EndIf
		If n\soundchn2 <> 0 Then
			If (Not n\soundchn2_isstream)
				If ChannelPlaying(n\soundchn2) Then ResumeChannel(n\soundchn2)
			Else
				If n\soundchn2_isstream=True
					SetStreamPaused_Strict(n\soundchn2,False)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.doors = Each Doors
		If d\soundchn <> 0 Then
			If ChannelPlaying(d\soundchn) Then ResumeChannel(d\soundchn)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\soundchn <> 0 Then
			If ChannelPlaying(dem\soundchn) Then ResumeChannel(dem\soundchn)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then ResumeChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN,False)
	EndIf
End Function

Function KillSounds()
	Local i%,e.Events,n.NPCs,d.Doors,dem.DevilEmitters,snd.Sound
	
	For i=0 To 9
		If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
	Next
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_isStream)
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_isStream)
				If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf		
	Next
	For n.NPCs = Each NPCs
		If n\SoundChn <> 0 Then
			If (Not n\SoundChn_IsStream)
				If ChannelPlaying(n\SoundChn) Then StopChannel(n\SoundChn)
			Else
				StopStream_Strict(n\SoundChn)
			EndIf
		EndIf
		If n\SoundChn2 <> 0 Then
			If (Not n\SoundChn2_IsStream)
				If ChannelPlaying(n\SoundChn2) Then StopChannel(n\SoundChn2)
			Else
				StopStream_Strict(n\SoundChn2)
			EndIf
		EndIf
	Next	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then StopChannel(d\SoundCHN)
		EndIf
	Next
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) Then StopChannel(dem\SoundCHN)
		EndIf
	Next
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then StopChannel(AmbientSFXCHN)
	EndIf
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	If IntercomStreamCHN <> 0
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	If EnableSFXRelease
		For snd.Sound = Each Sound
			If snd\internalHandle <> 0 Then
				FreeSound snd\internalHandle
				snd\internalHandle = 0
				snd\releaseTime = 0
			EndIf
		Next
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			If snd\channels[i]<>0 Then
				StopChannel snd\channels[i]
			EndIf
		Next
	Next
	
	DebugLog "Terminated all sounds"
End Function

Function GetStepSound(entity%)
    Local picker%,brush%,texture%,name$
    Local mat.Materials
    
    picker = LinePick(EntityX(entity),EntityY(entity),EntityZ(entity),0,-1,0)
    If picker <> 0 Then
        If GetEntityType(picker) <> HIT_MAP Then Return 0
        brush = GetSurfaceBrush(GetSurface(picker,CountSurfaces(picker)))
        If brush <> 0 Then
            texture = GetBrushTexture(brush,3)
            If texture <> 0 Then
                name = StripPath(TextureName(texture))
                If (name <> "") Then FreeTexture(texture)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							FreeBrush(brush)
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			texture = GetBrushTexture(brush,2)
			If texture <> 0 Then
				name = StripPath(TextureName(texture))
				If (name <> "") Then FreeTexture(texture)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							FreeBrush(brush)
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			texture = GetBrushTexture(brush,1)
			If texture <> 0 Then
				name = StripPath(TextureName(texture))
				If (name <> "") Then FreeTexture(texture)
				FreeBrush(brush)
				For mat.Materials = Each Materials
					If mat\name = name Then
						If mat\StepSound > 0 Then
							Return mat\StepSound-1
						EndIf
						Exit
					EndIf
				Next                
			EndIf
		EndIf
	EndIf
    
    Return 0
End Function

Function UpdateSoundOrigin2(Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			ChannelVolume(Chn, volume# * (1 - dist#))
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
End Function

Function UpdateSoundOrigin(Chn%, cam%, entity%, range# = 10, volume# = 1.0)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			ChannelVolume(Chn, volume# * (1 - dist#)*SFXVolume#)
			ChannelPan(Chn, panvalue)
		EndIf
	Else
		If Chn <> 0 Then
			ChannelVolume (Chn, 0)
		EndIf 
	EndIf
End Function
;--------------------------------------- random -------------------------------------------------------

Function f2s$(n#, count%)
	Return Left(n, Len(Int(Str(n)))+count+1)
End Function

Function AnimateNPC(n.NPCs, start#, quit#, speed#, loop=True)
	Local newTime#
	
	If speed > 0.0 Then 
		newTime = Max(Min(n\Frame + speed * FPSfactor,quit),start)
		
		If loop And newTime => quit Then
			newTime = start
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = n\Frame + speed * FPSfactor
			
			If newTime < quit Then 
				newTime = start
			Else If newTime > start 
				newTime = quit
			EndIf
		Else
			newTime = Max(Min(n\Frame + speed * FPSfactor,start),quit)
		EndIf
	EndIf
	SetNPCFrame(n, newTime)
End Function

Function SetNPCFrame(n.NPCs, frame#)
	If (Abs(n\Frame-frame)<0.001) Then Return
	
	SetAnimTime n\obj, frame
	
	n\Frame = frame
End Function

Function Animate2#(entity%, curr#, start%, quit%, speed#, loop=True)
	Local newTime#
	
	If speed > 0.0 Then 
		newTime = Max(Min(curr + speed * FPSfactor,quit),start)
		
		If loop Then
			If newTime => quit Then 
				newTime = start
			Else
			EndIf
		Else
		EndIf
	Else
		If start < quit Then
			temp% = start
			start = quit
			quit = temp
		EndIf
		
		If loop Then
			newTime = curr + speed * FPSfactor
			
			If newTime < quit Then newTime = start
			If newTime > start Then newTime = quit
		Else
			newTime = Max(Min(curr + speed * FPSfactor,start),quit)
		EndIf
	EndIf
	
	SetAnimTime entity, newTime
	Return newTime
End Function 

Function Use914(item.Items, setting$, x#, y#, z#)
	RefinedItems = RefinedItems+1
	
	Local it2.Items
	Select item\itemtemplate\name
		Case "Gas Mask", "Heavy Gas Mask"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case "1:1"
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case "fine", "very fine"
					it2 = CreateItem("Gas Mask", "supergasmask", x, y, z)
					RemoveItem(item)
			End Select
		Case "SCP-1499"
				Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case "1:1"
					it2 = CreateItem("Gas Mask", "gasmask", x, y, z)
					RemoveItem(item)
				Case "fine"
					it2 = CreateItem("SCP-1499", "super1499", x, y, z)
					RemoveItem(item)
				Case "very fine"
					n.NPCs = CreateNPC(NPCtype1499,x,y,z)
					n\State = 1
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
					n\State3 = 1
					RemoveItem(item)
			End Select
		Case "Ballistic Vest"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case "1:1"
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case "fine"
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					RemoveItem(item)
				Case "very fine"
					it2 = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					RemoveItem(item)
			End Select
		Case "Clipboard"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					For i% = 0 To 19
						If item\SecondInv[i]<>Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i]=Null
					Next
					RemoveItem(item)
				Case "1:1"
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case "fine"
					item\invSlots = Max(item\state2,15)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case "very fine"
					item\invSlots = Max(item\state2,20)
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
			End Select
		Case "Cowbell"
			Select setting
				Case "rough","coarse"
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case "1:1","fine","very fine"
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
			End Select
		Case "Night Vision Goggles"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					RemoveItem(item)
				Case "1:1"
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)
				Case "fine"
					it2 = CreateItem("Night Vision Goggles", "finenvgoggles", x, y, z)
					RemoveItem(item)
				Case "very fine"
					it2 = CreateItem("Night Vision Goggles", "supernv", x, y, z)
					it2\state = 1000
					RemoveItem(item)
			End Select
		Case "Metal Panel", "SCP-148 Ingot"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					RemoveItem(item)
				Case "1:1", "fine", "very fine"
					it2 = Null
					For it.Items = Each Items
						If it<>item And it\collider <> 0 And it\Picked = False Then
							If Distance(EntityX(it\collider,True), EntityZ(it\collider,True), EntityX(item\collider, True), EntityZ(item\collider, True)) < (180.0 * RoomScale) Then
								it2 = it
								Exit
							ElseIf Distance(EntityX(it\collider,True), EntityZ(it\collider,True), x,z) < (180.0 * RoomScale)
								it2 = it
								Exit
							End If
						End If
					Next
					
					If it2<>Null Then
						Select it2\itemtemplate\tempname
							Case "gasmask", "supergasmask"
								RemoveItem (it2)
								RemoveItem (item)
								
								it2 = CreateItem("Heavy Gas Mask", "gasmask3", x, y, z)
							Case "vest"
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
							Case "hazmatsuit","hazmatsuit2"
								RemoveItem (it2)
								RemoveItem(item)
								it2 = CreateItem("Heavy Hazmat Suit", "hazmatsuit3", x, y, z)
						End Select
					Else 
						If item\itemtemplate\name="SCP-148 Ingot" Then
							it2 = CreateItem("Metal Panel", "scp148", x, y, z)
							RemoveItem(item)
						Else
							PositionEntity(item\collider, x, y, z)
							ResetEntity(item\collider)							
						EndIf
					EndIf					
			End Select
		Case "Severed Hand", "Black Severed Hand"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(3, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					If (item\itemtemplate\name = "Severed Hand")
						it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
					Else
						it2 = CreateItem("Severed Hand", "hand", x, y, z)
					EndIf
			End Select
			RemoveItem(item)
		Case "First Aid Kit", "Blue First Aid Kit"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
				If Rand(2)=1 Then
					it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
				Else
				    it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
				EndIf
				Case "fine"
					it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
				Case "very fine"
					it2 = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
			End Select
			RemoveItem(item)
		Case "Level 1 Key Card", "Level 2 Key Card", "Level 3 Key Card", "Level 4 Key Card", "Level 5 Key Card", "Key Card"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("Playing Card", "misc", x, y, z)
				Case "fine"
					Select item\itemtemplate\name
						Case "Level 1 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
								Case NORMAL
									If Rand(5)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
								Case HARD
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
							End Select
						Case "Level 2 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
							End Select
						Case "Level 3 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									If Rand(10)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
								Case NORMAL
									If Rand(15)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
								Case HARD
									If Rand(20)=1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
							End Select
						Case "Level 4 Key Card"
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
							End Select
						Case "Level 5 Key Card"	
							Local CurrAchvAmount%=0
							For i = 0 To MAXACHIEVEMENTS-1
								If Achievements(i)=True
									CurrAchvAmount=CurrAchvAmount+1
								EndIf
							Next
							
							DebugLog CurrAchvAmount
							
							Select SelectedDifficulty\otherFactors
								Case EASY
									If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
								Case NORMAL
									If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
								Case HARD
									If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
							End Select		
					End Select
				Case "very fine"
					CurrAchvAmount%=0
					For i = 0 To MAXACHIEVEMENTS-1
						If Achievements(i)=True
							CurrAchvAmount=CurrAchvAmount+1
						EndIf
					Next
					
					DebugLog CurrAchvAmount
					
					Select SelectedDifficulty\otherFactors
						Case EASY
							If Rand(0,((MAXACHIEVEMENTS-1)*3)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
						Case NORMAL
							If Rand(0,((MAXACHIEVEMENTS-1)*4)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
						Case HARD
							If Rand(0,((MAXACHIEVEMENTS-1)*5)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
					End Select
			End Select
			
			RemoveItem(item)
		Case "Key Card Omni"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If Rand(2)=1 Then
						it2 = CreateItem("Mastercard", "misc", x, y, z)
					Else
						it2 = CreateItem("Playing Card", "misc", x, y, z)			
					EndIf	
				Case "fine", "very fine"
					it2 = CreateItem("Key Card Omni", "key6", x, y, z)
			End Select			
			
			RemoveItem(item)
		Case "Playing Card", "Coin", "Quarter"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)	
			    Case "fine", "very fine"
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
			End Select
			RemoveItem(item)
		Case "Mastercard"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("Quarter", "25ct", x, y, z)
					Local it3.Items,it4.Items,it5.Items
					it3 = CreateItem("Quarter", "25ct", x, y, z)
					it4 = CreateItem("Quarter", "25ct", x, y, z)
					it5 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType (it3\collider, HIT_ITEM)
					EntityType (it4\collider, HIT_ITEM)
					EntityType (it5\collider, HIT_ITEM)
				Case "1:1"
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)	
			    Case "fine", "very fine"
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
			End Select
			RemoveItem(item)
		Case "S-NAV 300 Navigator", "S-NAV 310 Navigator", "S-NAV Navigator", "S-NAV Navigator Ultimate"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("Electronical components", "misc", x, y, z)
				Case "1:1"
					it2 = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\state = 100
				Case "fine"
					it2 = CreateItem("S-NAV 310 Navigator", "nav", x, y, z)
					it2\state = 100
				Case "very fine"
					it2 = CreateItem("S-NAV Navigator Ultimate", "nav", x, y, z)
					it2\state = 101
			End Select
			
			RemoveItem(item)
		Case "Radio Transceiver"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("Electronical components", "misc", x, y, z)
				Case "1:1"
					it2 = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\state = 100
				Case "fine"
					it2 = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					it2\state = 101
				Case "very fine"
					it2 = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
					it2\state = 101
			End Select
			
			RemoveItem(item)
		Case "SCP-513"
			Select setting
				Case "rough", "coarse"
					PlaySound_Strict LoadTempSound("SFX\SCP\513\914Refine.ogg")
					For n.npcs = Each NPCs
						If n\npctype = NPCtype5131 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					it2 = CreateItem("SCP-513", "scp513", x, y, z)
					
			End Select
			
			RemoveItem(item)
		Case "Some SCP-420-J", "Cigarette"
			Select setting
				Case "rough", "coarse"			
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
				Case "fine"
					it2 = CreateItem("Joint", "420s", x + 1.5, y + 0.5, z + 1.0)
				Case "very fine"
					it2 = CreateItem("Smelly Joint", "420s", x + 1.5, y + 0.5, z + 1.0)
			End Select
			
			RemoveItem(item)
		Case "9V Battery", "18V Battery", "Strange Battery"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("18V Battery", "18vbat", x, y, z)
				Case "fine"
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
				Case "very fine"
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
			End Select
			
			RemoveItem(item)
		Case "ReVision Eyedrops", "RedVision Eyedrops", "Eyedrops"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("RedVision Eyedrops", "eyedrops", x,y,z)
				Case "fine"
					it2 = CreateItem("Eyedrops", "fineeyedrops", x,y,z)
				Case "very fine"
					it2 = CreateItem("Eyedrops", "supereyedrops", x,y,z)
			End Select
			
			RemoveItem(item)		
		Case "Hazmat Suit"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("Hazmat Suit", "hazmatsuit", x,y,z)
				Case "fine"
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
				Case "very fine"
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x,y,z)
			End Select
			
			RemoveItem(item)
			
		Case "Syringe"
			Select item\itemtemplate\tempname
				Case "syringe"
					Select setting
						Case "rough", "coarse"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1"
							it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)	
						Case "fine"
							it2 = CreateItem("Syringe", "finesyringe", x, y, z)
						Case "very fine"
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					End Select
					
				Case "finesyringe"
					Select setting
						Case "rough"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "coarse"
							it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
						Case "1:1"
							it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)	
						Case "fine", "very fine"
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					End Select
					
				Case "veryfinesyringe"
					Select setting
						Case "rough", "coarse", "1:1", "fine"
							it2 = CreateItem("Electronical components", "misc", x, y, z)	
						Case "very fine"
							n.NPCs = CreateNPC(NPCtype008,x,y,z)
							n\State = 2
					End Select
			End Select
			
			RemoveItem(item)
			
		Case "SCP-500-01", "Upgraded pill", "Pill"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("Pill", "pill", x, y, z)
					RemoveItem(item)
				Case "fine"
					Local no427Spawn% = False
					For it3.Items = Each Items
						If it3\itemtemplate\tempname = "scp427" Then
							no427Spawn = True
							Exit
						EndIf
					Next
					If (Not no427Spawn) Then
						it2 = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					EndIf
					RemoveItem(item)
				Case "very fine"
					it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					RemoveItem(item)
			End Select
		Default
			Select item\itemtemplate\tempname
				Case "cup"
					Select setting
						Case "rough", "coarse"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
							d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1"
							it2 = CreateItem("cup", "cup", x,y,z, 255-item\r,255-item\g,255-item\b,item\a)
							it2\name = item\name
							it2\state = item\state
						Case "fine"
							it2 = CreateItem("cup", "cup", x,y,z, Min(item\r*Rnd(0.9,1.1),255),Min(item\g*Rnd(0.9,1.1),255),Min(item\b*Rnd(0.9,1.1),255),item\a)
							it2\name = item\name
							it2\state = item\state+1.0
						Case "very fine"
							it2 = CreateItem("cup", "cup", x,y,z, Min(item\r*Rnd(0.5,1.5),255),Min(item\g*Rnd(0.5,1.5),255),Min(item\b*Rnd(0.5,1.5),255),item\a)
							it2\name = item\name
							it2\state = item\state*2
							If Rand(5)=1 Then
								ExplosionTimer = 135
							EndIf
					End Select	
					
					RemoveItem(item)
				Case "paper"
					Select setting
						Case "rough", "coarse"
							d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1"
							Select Rand(6)
								Case 1
									it2 = CreateItem("Document SCP-106", "paper", x, y, z)
								Case 2
									it2 = CreateItem("Document SCP-079", "paper", x, y, z)
								Case 3
									it2 = CreateItem("Document SCP-173", "paper", x, y, z)
								Case 4
									it2 = CreateItem("Document SCP-895", "paper", x, y, z)
								Case 5
									it2 = CreateItem("Document SCP-682", "paper", x, y, z)
								Case 6
									it2 = CreateItem("Document SCP-860", "paper", x, y, z)
							End Select
						Case "fine", "very fine"
							it2 = CreateItem("Origami", "misc", x, y, z)
					End Select
					
					RemoveItem(item)
				Default
					PositionEntity(item\collider, x, y, z)
					ResetEntity(item\collider)	
			End Select
			
	End Select
	
	If it2 <> Null Then EntityType (it2\collider, HIT_ITEM)
End Function

Function Use294()
	Local x#,y#, xtemp%,ytemp%, strtemp$, temp%
	
	ShowPointer()
	
	x = GraphicWidth/2 - (ImageWidth(Panel294)/2)
	y = GraphicHeight/2 - (ImageHeight(Panel294)/2)
	DrawImage Panel294, x, y
	If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	temp = True
	If PlayerRoom\SoundCHN<>0 Then temp = False
	
	Text x+907, y+185, Input294, True,True
	
	If temp Then
		If MouseHit1 Then
			xtemp = Floor((ScaledMouseX()-x-228) / 35.5)
			ytemp = Floor((ScaledMouseY()-y-342) / 36.5)
			
			If ytemp => 0 And ytemp < 5 Then
				If xtemp => 0 And xtemp < 10 Then PlaySound_Strict ButtonSFX
			EndIf
			
			strtemp = ""
			
			temp = False
			
			Select ytemp
				Case 0
					strtemp = (xtemp + 1) Mod 10
				Case 1
					Select xtemp
						Case 0
							strtemp = "Q"
						Case 1
							strtemp = "W"
						Case 2
							strtemp = "E"
						Case 3
							strtemp = "R"
						Case 4
							strtemp = "T"
						Case 5
							strtemp = "Y"
						Case 6
							strtemp = "U"
						Case 7
							strtemp = "I"
						Case 8
							strtemp = "O"
						Case 9
							strtemp = "P"
					End Select
				Case 2
					Select xtemp
						Case 0
							strtemp = "A"
						Case 1
							strtemp = "S"
						Case 2
							strtemp = "D"
						Case 3
							strtemp = "F"
						Case 4
							strtemp = "G"
						Case 5
							strtemp = "H"
						Case 6
							strtemp = "J"
						Case 7
							strtemp = "K"
						Case 8
							strtemp = "L"
						Case 9 ;dispense
							temp = True
					End Select
				Case 3
					Select xtemp
						Case 0
							strtemp = "Z"
						Case 1
							strtemp = "X"
						Case 2
							strtemp = "C"
						Case 3
							strtemp = "V"
						Case 4
							strtemp = "B"
						Case 5
							strtemp = "N"
						Case 6
							strtemp = "M"
						Case 7
							strtemp = "-"
						Case 8
							strtemp = " "
						Case 9
							Input294 = Left(Input294, Max(Len(Input294)-1,0))
					End Select
				Case 4
					strtemp = " "
			End Select
			
			Input294 = Input294 + strtemp
			
			Input294 = Left(Input294, Min(Len(Input294),15))
			
			If temp And Input294<>"" Then ;dispense
				Input294 = Trim(Lower(Input294))
				If Left(Input294, Min(7,Len(Input294))) = "cup of " Then
					Input294 = Right(Input294, Len(Input294)-7)
				ElseIf Left(Input294, Min(9,Len(Input294))) = "a cup of " 
					Input294 = Right(Input294, Len(Input294)-9)
				EndIf
				
				If Input294<>""
					Local loc% = GetINISectionLocation("DATA\SCP-294.ini",Input294)
				EndIf
				
				If loc > 0 Then
					strtemp$ = GetINIString2("DATA\SCP-294.ini", loc, "dispensesound")
					If strtemp="" Then
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(strtemp))
					EndIf
					
					If GetINIInt2("DATA\SCP-294.ini", loc, "explosion")=True Then 
						ExplosionTimer = 135
						DeathMSG = GetINIString2("DATA\SCP-294.ini", loc, "deathmessage")
					EndIf
					
					strtemp$ = GetINIString2("DATA\SCP-294.ini", loc, "color")
					
					sep1 = Instr(strtemp, ",", 1)
					sep2 = Instr(strtemp, ",", sep1+1)
					r% = Trim(Left(strtemp, sep1-1))
					g% = Trim(Mid(strtemp, sep1+1, sep2-sep1-1))
					b% = Trim(Right(strtemp, Len(strtemp)-sep2))
					
					alpha# = Float(GetINIString2("DATA\SCP-294.ini", loc, "alpha",1.0))
					glow = GetINIInt2("DATA\SCP-294.ini", loc, "glow")
					If glow Then alpha = -alpha
					
					it.items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1],True),EntityY(PlayerRoom\Objects[1],True),EntityZ(PlayerRoom\Objects[1],True), r,g,b,alpha)
					it\displayName = "一杯"+Input294
					it\name = "Cup of "+Input294
					EntityType (it\collider, HIT_ITEM)
					
				Else
					;out of range
					Input294 = "超出范围。"
					PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\outofrange.ogg"))
				EndIf
				
			EndIf
			
		EndIf ;if mousehit1
		
		If MouseHit2 Or (Not Using294) Then 
			HidePointer()
			Using294 = False
			Input294 = ""
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf
		
	Else ;playing a dispensing sound
		If Input294 <> "超出范围。" Then Input294 = "制作中..."
		
		If Not ChannelPlaying(PlayerRoom\SoundCHN) Then
			If Input294 <> "超出范围。" Then
				HidePointer()
				Using294 = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				Local e.Events
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0
						Exit
					EndIf
				Next
			EndIf
			Input294=""
			PlayerRoom\SoundCHN=0
		EndIf
	EndIf
End Function

Function Use427()
	Local i%,pvt%,de.Decals,tempchn%
	Local prevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70*360
		If I_427\Using=True Then
			I_427\Timer = I_427\Timer + FPSfactor
			If Injuries > 0.0 Then
				Injuries = Max(Injuries - 0.0005 * FPSfactor,0.0)
			EndIf
			If Bloodloss > 0.0 And Injuries <= 1.0 Then
				Bloodloss = Max(Bloodloss - 0.001 * FPSfactor,0.0)
			EndIf
			If Infect > 0.0 Then
				Infect = Max(Infect - 0.001 * FPSfactor,0.0)
			EndIf
			For i = 0 To 5
				If SCP1025state[i]>0.0 Then
					SCP1025state[i] = Max(SCP1025state[i] - 0.001 * FPSfactor,0.0)
				EndIf
			Next
			If I_427\Sound[0]=0 Then
				I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			EndIf
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer => 70*180 Then
				If I_427\Sound[1]=0 Then
					I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				EndIf
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then
					I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
				EndIf
			EndIf
			If prevI427Timer < 70*60 And I_427\Timer => 70*60 Then
				Msg = "你感到精力充沛"
				MsgTimer = 70*5
			ElseIf prevI427Timer < 70*180 And I_427\Timer => 70*180 Then
				Msg = "你感觉全身肌肉轻微痉挛"
				MsgTimer = 70*5
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i]<>0 Then
					If ChannelPlaying(I_427\SoundCHN[i]) Then
						StopChannel(I_427\SoundCHN[i])
					EndIf
				EndIf
			Next
		EndIf
	Else
		If prevI427Timer-FPSfactor < 70*360 And I_427\Timer => 70*360 Then
			Msg = "你的肌肉在肿胀，你感觉比以往更强大"
			MsgTimer = 70*5
		ElseIf prevI427Timer-FPSfactor < 70*390 And I_427\Timer => 70*390 Then
			Msg = "你感觉不到你的腿，但你也不需要腿了"
			MsgTimer = 70*5
		EndIf
		I_427\Timer = I_427\Timer + FPSfactor
		If I_427\Sound[0]=0 Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If I_427\Sound[1]=0 Then
			I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		EndIf
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then
				I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
			EndIf
		Next
		If Rnd(200)<2.0 Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.Decals = CreateDecal(20, PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\Size = Rnd(0.03,0.08)*2.0 : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
			tempchn% = PlaySound_Strict (DripSFX(Rand(0,2)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			FreeEntity pvt
			BlurTimer = 800
		EndIf
		If I_427\Timer >= 70*420 Then
			Kill()
			DeathMSG = "“请求MTF Nu-7提供支持，我们需要更多的火力来打败这东西。”"
		ElseIf I_427\Timer >= 70*390 Then
			Crouch = True
		EndIf
	EndIf
End Function

Function UpdateMTF%()
	If PlayerRoom\RoomTemplate\Name = "gateaentrance" Then Return
	
	Local r.Rooms, n.NPCs
	Local dist#, i%
	
	;mtf ei vielä spawnannut, spawnataan jos pelaaja menee tarpeeksi lähelle gate b:tä
	If MTFtimer = 0 Then
		If Rand(30)=1 And PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
			
			Local entrance.Rooms = Null
			For r.Rooms = Each Rooms
				If Lower(r\RoomTemplate\Name) = "gateaentrance" Then entrance = r : Exit
			Next
			
			If entrance <> Null Then 
				If Abs(EntityZ(entrance\obj)-EntityZ(Collider))<30.0 Then
					If PlayerInReachableRoom()
						PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
					EndIf
					
					MTFtimer = FPSfactor
					Local leader.NPCs
					For i = 0 To 2
						n.NPCs = CreateNPC(NPCtypeMTF, EntityX(entrance\obj)+0.3*(i-1), 1.0,EntityZ(entrance\obj)+8.0)
						
						If i = 0 Then 
							leader = n
						Else
							n\MTFLeader = leader
						EndIf
						
						n\PrevX = i
					Next
				EndIf
			EndIf
		EndIf
	Else
		If MTFtimer <= 70*120 ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 70*120 And MTFtimer < 10000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			EndIf
			MTFtimer = 10000
		ElseIf MTFtimer >= 10000 And MTFtimer <= 10000+(70*120) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 10000+(70*120) And MTFtimer < 20000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			EndIf
			MTFtimer = 20000
		ElseIf MTFtimer >= 20000 And MTFtimer <= 20000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 20000+(70*60) And MTFtimer < 25000
			If PlayerInReachableRoom()
				;If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount-1
					If Inventory(i) <> Null Then
						If (Left(Inventory(i)\itemtemplate\name, 4) = "SCP-") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-035") And (Left(Inventory(i)\itemtemplate\name, 7) <> "SCP-093")
							PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncPossession.ogg")
							MTFtimer = 25000
							Return
							Exit
						EndIf
					EndIf
				Next
				
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnounc"+Rand(1,3)+".ogg")
			EndIf
			MTFtimer = 25000
			
		ElseIf MTFtimer >= 25000 And MTFtimer <= 25000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 25000+(70*60) And MTFtimer < 30000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			EndIf
			MTFtimer = 30000
			
		EndIf
	EndIf
End Function

Function UpdateInfect()
	Local temp#, i%, r.Rooms
	
	Local teleportForInfect% = True
	
	If PlayerRoom\RoomTemplate\Name = "room860"
		For e.Events = Each Events
			If e\EventName = "room860"
				If e\EventState = 1.0
					teleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "gatea"
		teleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "exit1" And EntityY(Collider)>1040.0*RoomScale
		teleportForInfect = False
	EndIf
	
	If Infect>0 Then
		ShowEntity InfectOverlay
		
		If Infect < 93.0 Then
			temp=Infect
			If (Not I_427\Using And I_427\Timer < 70*360) Then
				Infect = Min(Infect+FPSfactor*0.002,100)
			EndIf
			
			BlurTimer = Max(Infect*3*(2.0-CrouchState),BlurTimer)
			
			HeartBeatRate = Max(HeartBeatRate, 100)
			HeartBeatVolume = Max(HeartBeatVolume, Infect/120.0)
			
			EntityAlpha InfectOverlay, Min(((Infect*0.2)^2)/1000.0,0.5) * (Sin(MilliSecs()/8.0)+2.0)
			
			For i = 0 To 6
				If Infect>i*15+10 And temp =< i*15+10 Then
					PlaySound_Strict LoadTempSound("SFX\SCP\008\Voices"+i+".ogg")
				EndIf
			Next
			
			If Infect > 20 And temp =< 20.0 Then
				Msg = "你感觉有点发烧"
				MsgTimer = 70*6
			ElseIf Infect > 40 And temp =< 40.0
				Msg = "你感觉恶心"
				MsgTimer = 70*6
			ElseIf Infect > 60 And temp =< 60.0
				Msg = "恶心越来越严重了"
				MsgTimer = 70*6
			ElseIf Infect > 80 And temp =< 80.0
				Msg = "你感觉很虚弱"
				MsgTimer = 70*6
			ElseIf Infect =>91.5
				BlinkTimer = Max(Min(-10*(Infect-91.5),BlinkTimer),-10)
				IsZombie = True
				UnableToMove = True
				If Infect >= 92.7 And temp < 92.7 Then
					If teleportForInfect
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name="008" Then
								PositionEntity Collider, EntityX(r\Objects[7],True),EntityY(r\Objects[7],True),EntityZ(r\Objects[7],True),True
								ResetEntity Collider
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6],True),EntityY(r\Objects[6],True)+0.2,EntityZ(r\Objects[6],True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundChn = PlaySound_Strict(r\NPC[0]\Sound)
								tex = LoadTexture_Strict("GFX\npcs\scientist2.jpg")
								EntityTexture r\NPC[0]\obj, tex
								FreeTexture tex
								r\NPC[0]\State=6
								PlayerRoom = r
								UnableToMove = False
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			
			temp=Infect
			Infect = Min(Infect+FPSfactor*0.004,100)
			
			If teleportForInfect
				If Infect < 94.7 Then
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/8.0)+2.0)
					BlurTimer = 900
					
					If Infect > 94.5 Then BlinkTimer = Max(Min(-50*(Infect-94.5),BlinkTimer),-10)
					PointEntity Collider, PlayerRoom\NPC[0]\Collider
					PointEntity PlayerRoom\NPC[0]\Collider, Collider
					PointEntity Camera, PlayerRoom\NPC[0]\Collider,EntityRoll(Camera)
					ForceMove = 0.75
					Injuries = 2.5
					Bloodloss = 0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 357, 381, 0.3)
				ElseIf Infect < 98.5
					
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/5.0)+2.0)
					BlurTimer = 950
					
					ForceMove = 0.0
					UnableToMove = True
					PointEntity Camera, PlayerRoom\NPC[0]\Collider
					
					If temp < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundChn = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						DeathMSG = "对象D-9341在[已编辑]区进食[已编辑]博士，对象立即被九尾狐击毙并送去尸检。"
						DeathMSG = DeathMSG + "已确认为SCP-008感染，尸体已被火化。"
						
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544*RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider),90,Rnd(360),0)
						de\Size = 0.8
						ScaleSprite(de\obj, de\Size,de\Size)
					ElseIf Infect > 96
						BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
					Else
						KillTimer = Max(-350, KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2=0 Then
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 13, 19, 0.3,False)
						If AnimTime(PlayerRoom\NPC[0]\obj) => 19 Then PlayerRoom\NPC[0]\State2=1
					Else
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 19, 13, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\obj) =< 13 Then PlayerRoom\NPC[0]\State2=0
					EndIf
					
					If ParticleAmount>0
						If Rand(50)=1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider),EntityY(PlayerRoom\NPC[0]\Collider),EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05,0.1), 0.15, 200)
							p\speed = 0.01
							p\SizeChange = 0.01
							p\A = 0.5
							p\Achange = -0.01
							RotateEntity p\pvt, Rnd(360),Rnd(360),0
						EndIf
					EndIf
					
					PositionEntity Head, EntityX(PlayerRoom\NPC[0]\Collider,True), EntityY(PlayerRoom\NPC[0]\Collider,True)+0.65,EntityZ(PlayerRoom\NPC[0]\Collider,True),True
					RotateEntity Head, (1.0+Sin(MilliSecs()/5.0))*15, PlayerRoom\angle-180, 0, True
					MoveEntity Head, 0,0,-0.4
					TurnEntity Head, 80+(Sin(MilliSecs()/5.0))*30,(Sin(MilliSecs()/5.0))*40,0
				EndIf
			Else
				Kill()
				BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
				If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
					DeathMSG = "SCP-1499的下落仍旧不明，一个侦察小组已被派去调查俄罗斯[已编辑]镇一座教堂遭受暴力袭击的事件。"
				ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or PlayerRoom\RoomTemplate\Name = "exit1" Then
					DeathMSG = "对象D-9341被发现在"
					If PlayerRoom\RoomTemplate\Name = "gatea" Then
						DeathMSG = DeathMSG + "A"
					Else
						DeathMSG = DeathMSG + "B"
					EndIf
					DeathMSG = DeathMSG + "门徘徊，对象立即被九尾狐击毙并送去尸检。"
					DeathMSG = DeathMSG + "已确认为SCP-008感染，尸体已被火化。"
				Else
					DeathMSG = ""
				EndIf
			EndIf
		EndIf		
	Else
		HideEntity InfectOverlay
	EndIf
End Function

;--------------------------------------- math -------------------------------------------------------

Function GenerateSeedNumber(seed$)
 	Local temp% = 0
 	Local shift% = 0
 	For i = 1 To Len(seed)
 		temp = temp Xor (Asc(Mid(seed,i,1)) Shl shift)
 		shift=(shift+1) Mod 24
	Next
 	Return temp
End Function

Function Distance#(x1#, y1#, x2#, y2#)
	Local x# = x2 - x1, y# = y2 - y1
	Return(Sqr(x*x + y*y))
End Function

Function CurveValue#(number#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
	If number < old Then
		Return Max(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	Else
		Return Min(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	EndIf
End Function

Function CurveAngle#(val#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
   Local diff# = WrapAngle(val) - WrapAngle(old)
   If diff > 180 Then diff = diff - 360
   If diff < - 180 Then diff = diff + 360
   Return WrapAngle(old + diff * (1.0 / smooth * FPSfactor))
End Function

Function WrapAngle#(angle#)
	If angle = INFINITY Then Return 0.0
	While angle < 0
		angle = angle + 360
	Wend 
	While angle >= 360
		angle = angle - 360
	Wend
	Return angle
End Function

Function GetAngle#(x1#, y1#, x2#, y2#)
	Return ATan2( y2 - y1, x2 - x1 )
End Function

Function CircleToLineSegIsect% (cx#, cy#, r#, l1x#, l1y#, l2x#, l2y#)
	;Palauttaa:
	;  True (1) kun:
	;      Ympyrä [keskipiste = (cx, cy): säde = r]
	;      leikkaa janan, joka kulkee pisteiden (l1x, l1y) & (l2x, l2y) kaitta
	;  False (0) muulloin
	
	;Ympyrän keskipisteen ja (ainakin toisen) janan päätepisteen etäisyys < r
	;-> leikkaus
	If Distance(cx, cy, l1x, l1y) <= r Then
		Return True
	EndIf
	
	If Distance(cx, cy, l2x, l2y) <= r Then
		Return True
	EndIf	
	
	;Ympyrän keskipisteen ja (ainakin toisen) janan päätepisteen etäisyys < r
	Local SegVecX# = l2x - l1x
	Local SegVecY# = l2y - l1y
	
	Local PntVec1X# = cx - l1x
	Local PntVec1Y# = cy - l1y
	
	Local PntVec2X# = cx - l2x
	Local PntVec2Y# = cy - l2y
	
	;Em. vektorien pistetulot
	Local dp1# = SegVecX * PntVec1X + SegVecY * PntVec1Y
	Local dp2# = -SegVecX * PntVec2X - SegVecY * PntVec2Y
	
	;Tarkistaa onko toisen pistetulon arvo 0
	;tai molempien merkki sama
	If dp1 = 0 Or dp2 = 0 Then
	ElseIf (dp1 > 0 And dp2 > 0) Or (dp1 < 0 And dp2 < 0) Then
	Else
		;Ei kumpikaan -> ei leikkausta
		Return False
	EndIf
	
	;Janan päätepisteiden kautta kulkevan suoran ;yhtälö; (ax + by + c = 0)
	Local a# = (l2y - l1y) / (l2x - l1x)
	Local b# = -1
	Local c# = -(l2y - l1y) / (l2x - l1x) * l1x + l1y
	
	;Ympyrän keskipisteen etäisyys suorasta
	Local d# = Abs(a * cx + b * cy + c) / Sqr(a * a + b * b)
	
	;Ympyrä on liian kaukana
	;-> ei leikkausta
	If d > r Then Return False
	
	;Jos päästään tänne saakka, ympyrä ja jana leikkaavat (tai ovat sisäkkäin)
	Return True
End Function

Function point_direction#(x1#,z1#,x2#,z2#)
	Local dx#, dz#
	dx = x1 - x2
	dz = z1 - z2
	Return ATan2(dz,dx)
End Function

Function point_distance#(x1#,z1#,x2#,z2#)
	Local dx#,dy#
	dx = x1 - x2
	dy = z1 - z2
	Return Sqr((dx*dx)+(dy*dy)) 
End Function

Function angleDist#(a0#,a1#)
	Local b# = a0-a1
	Local bb#
	If b<-180.0 Then
		bb = b+360.0
	Else If b>180.0 Then
		bb = b-360.0
	Else
		bb = b
	EndIf
	Return bb
End Function

Function Inverse#(number#)
	Return Float(1.0-number#)
End Function

Function Rnd_Array#(numb1#,numb2#,Array1#,Array2#)
	Local whatarray% = Rand(1,2)
	
	If whatarray% = 1
		Return Rnd(Array1#,numb1#)
	Else
		Return Rnd(numb2#,Array2#)
	EndIf
End Function

;--------------------------------------- decals -------------------------------------------------------

Type Decals
	Field obj%
	Field SizeChange#, Size#, MaxSize#
	Field AlphaChange#, Alpha#
	Field blendmode%
	Field fx%
	Field ID%
	Field Timer#
	
	Field lifetime#
	
	Field x#, y#, z#
	Field pitch#, yaw#, roll#
End Type

Function CreateDecal.Decals(id%, x#, y#, z#, pitch#, yaw#, roll#)
	Local d.Decals = New Decals
	
	d\x = x
	d\y = y
	d\z = z
	d\pitch = pitch
	d\yaw = yaw
	d\roll = roll
	
	d\MaxSize = 1.0
	
	d\Alpha = 1.0
	d\Size = 1.0
	d\obj = CreateSprite()
	d\blendmode = 1
	
	EntityTexture(d\obj, DecalTextures(id))
	EntityFX(d\obj, 0)
	SpriteViewMode(d\obj, 2)
	PositionEntity(d\obj, x, y, z)
	RotateEntity(d\obj, pitch, yaw, roll)
	
	d\ID = id
	
	If DecalTextures(id) = 0 Or d\obj = 0 Then Return Null
	
	Return d
End Function

Function UpdateDecals()
	Local d.Decals
	For d.Decals = Each Decals
		If d\SizeChange <> 0 Then
			d\Size=d\Size + d\SizeChange * FPSfactor
			ScaleSprite(d\obj, d\Size, d\Size)
			
			Select d\ID
				Case 0
					If d\Timer <= 0 Then
						Local angle# = Rand(360)
						Local temp# = Rnd(d\Size)
						Local d2.Decals = CreateDecal(1, EntityX(d\obj) + Cos(angle) * temp, EntityY(d\obj) - 0.0005, EntityZ(d\obj) + Sin(angle) * temp, EntityPitch(d\obj), Rnd(360), EntityRoll(d\obj))
						d2\Size = Rnd(0.1, 0.5) : ScaleSprite(d2\obj, d2\Size, d2\Size)
						PlaySound2(DecaySFX(Rand(1, 3)), Camera, d2\obj, 10.0, Rnd(0.1, 0.5))
						;d\Timer = d\Timer + Rand(50,150)
						d\Timer = Rand(50, 100)
					Else
						d\Timer= d\Timer-FPSfactor
					End If
				;Case 6
				;	EntityBlend d\obj, 2
			End Select
			
			If d\Size >= d\MaxSize Then d\SizeChange = 0 : d\Size = d\MaxSize
		End If
		
		If d\AlphaChange <> 0 Then
			d\Alpha = Min(d\Alpha + FPSfactor * d\AlphaChange, 1.0)
			EntityAlpha(d\obj, d\Alpha)
		End If
		
		If d\lifetime > 0 Then
			d\lifetime=Max(d\lifetime-FPSfactor,5)
		EndIf
		
		If d\Size <= 0 Or d\Alpha <= 0 Or d\lifetime=5.0  Then
			FreeEntity(d\obj)
			Delete d
		End If
	Next
End Function


;--------------------------------------- INI-functions -------------------------------------------------------

Type INIFile
	Field name$
	Field bank%
	Field bankOffset% = 0
	Field size%
End Type

Function ReadINILine$(file.INIFile)
	Local rdbyte%
	Local firstbyte% = True
	Local offset% = file\bankOffset
	Local bank% = file\bank
	Local retStr$ = ""
	rdbyte = PeekByte(bank,offset)
	While ((firstbyte) Or ((rdbyte<>13) And (rdbyte<>10))) And (offset<file\size)
		rdbyte = PeekByte(bank,offset)
		If ((rdbyte<>13) And (rdbyte<>10)) Then
			firstbyte = False
			retStr=retStr+Chr(rdbyte)
		EndIf
		offset=offset+1
	Wend
	file\bankOffset = offset
	Return retStr
End Function

Function UpdateINIFile$(filename$)
	Local file.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(filename) Then
			file = k
			Exit
		EndIf
	Next
	
	If file=Null Then Return
	
	If file\bank<>0 Then FreeBank file\bank
	Local f% = ReadFile(file\name)
	Local fleSize% = 1
	While fleSize<FileSize(file\name)
		fleSize=fleSize*2
	Wend
	file\bank = CreateBank(fleSize)
	file\size = 0
	While Not Eof(f)
		PokeByte(file\bank,file\size,ReadByte(f))
		file\size=file\size+1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	
	Local lfile.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
			Exit
		EndIf
	Next
	
	If lfile = Null Then
		DebugLog "CREATE BANK FOR "+file
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
					EndIf
				Until (Left(TemporaryString, 1) = "[") Or (lfile\bankOffset>=lfile\size)
				
				Return defaultvalue
			EndIf
		EndIf
	Wend
	
	Return defaultvalue
End Function

Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
End Function

Function GetINIFloat2#(File$, Section$, Parameter$, DefaultValue# = 0.0)
	Return(Float(GetINIString2(File, Section, Parameter, DefaultValue)))
End Function

Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString, 1) = "[" Or Eof(f)
			CloseFile f
			Return defaultvalue
		EndIf
	Wend
	
	CloseFile f	
	
	Return defaultvalue
End Function

Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINISectionLocation%(file$, section$)
	Local Temp%
	Local f% = ReadFile(file)
	
	section = Lower(section)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			Temp = Instr(strtemp, section)
			If Temp>0 Then
				If Mid(strtemp, Temp-1, 1)="[" Or Mid(strtemp, Temp-1, 1)="|" Then
					CloseFile f
					Return n
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile f
End Function


Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
	
	; Returns: True (Success) Or False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	Local INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	Local INI_sFilename$ = file$
	
	; Retrieve the INI Data (If it exists)
	
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	
		; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
	
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
					; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine INI_lFileHandle, INI_sTemp
				Else
						; KEY=VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine INI_lFileHandle, INI_sTemp
						End If
					End If
				EndIf
				
			End If
			
		End If
		
			; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
		; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	End If
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function

Function INI_FileToString$(INI_sFilename$)
	Local INI_sString$ = ""
	Local INI_lFileHandle%= ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	End If
	Return INI_sString
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
	Return True
End Function

;Save options to .ini.
Function SaveOptionsINI()
	
	PutINIValue(OptionFile, "options", "mouse sensitivity", MouseSens)
	PutINIValue(OptionFile, "options", "invert mouse y", InvertMouse)
	PutINIValue(OptionFile, "options", "bump mapping enabled", BumpEnabled)			
	PutINIValue(OptionFile, "options", "HUD enabled", HUDenabled)
	PutINIValue(OptionFile, "options", "screengamma", ScreenGamma)
	PutINIValue(OptionFile, "options", "antialias", Opt_AntiAlias)
	PutINIValue(OptionFile, "options", "vsync", Vsync)
	PutINIValue(OptionFile, "options", "show FPS", ShowFPS)
	PutINIValue(OptionFile, "options", "framelimit", Framelimit%)
	PutINIValue(OptionFile, "options", "achievement popup enabled", AchvMSGenabled%)
	PutINIValue(OptionFile, "options", "room lights enabled", EnableRoomLights%)
	PutINIValue(OptionFile, "options", "texture details", TextureDetails%)
	PutINIValue(OptionFile, "options", "particle amount", ParticleAmount)
	PutINIValue(OptionFile, "options", "enable vram", EnableVRam)
	PutINIValue(OptionFile, "options", "mouse smoothing", MouseSmooth)
	PutINIValue(OptionFile, "options", "enable subtitles", EnableSubtitles)
	PutINIValue(OptionFile, "options", "traditional chinese", TraditionalChinese)
	
	PutINIValue(OptionFile, "console", "enabled", CanOpenConsole%)
	PutINIValue(OptionFile, "console", "auto opening", ConsoleOpening%)
	
	PutINIValue(OptionFile, "audio", "music volume", MusicVolume)
	PutINIValue(OptionFile, "audio", "sound volume", PrevSFXVolume)
	PutINIValue(OptionFile, "audio", "sfx release", EnableSFXRelease)
	PutINIValue(OptionFile, "audio", "enable user tracks", EnableUserTracks%)
	PutINIValue(OptionFile, "audio", "user track setting", UserTrackMode%)
	
	PutINIValue(OptionFile, "binds", "Right key", KEY_RIGHT)
	PutINIValue(OptionFile, "binds", "Left key", KEY_LEFT)
	PutINIValue(OptionFile, "binds", "Up key", KEY_UP)
	PutINIValue(OptionFile, "binds", "Down key", KEY_DOWN)
	PutINIValue(OptionFile, "binds", "Blink key", KEY_BLINK)
	PutINIValue(OptionFile, "binds", "Sprint key", KEY_SPRINT)
	PutINIValue(OptionFile, "binds", "Inventory key", KEY_INV)
	PutINIValue(OptionFile, "binds", "Crouch key", KEY_CROUCH)
	PutINIValue(OptionFile, "binds", "Save key", KEY_SAVE)
	PutINIValue(OptionFile, "binds", "Console key", KEY_CONSOLE)
End Function

;--------------------------------------- MakeCollBox -functions -------------------------------------------------------


; Create a collision box For a mesh entity taking into account entity scale
; (will not work in non-uniform scaled space)
Function MakeCollBox(mesh%)
	Local sx# = EntityScaleX(mesh, 1)
	Local sy# = Max(EntityScaleY(mesh, 1), 0.001)
	Local sz# = EntityScaleZ(mesh, 1)
	GetMeshExtents(mesh)
	EntityBox mesh, Mesh_MinX * sx, Mesh_MinY * sy, Mesh_MinZ * sz, Mesh_MagX * sx, Mesh_MagY * sy, Mesh_MagZ * sz
End Function

; Find mesh extents
Function GetMeshExtents(Mesh%)
	Local s%, surf%, surfs%, v%, verts%, x#, y#, z#
	Local minx# = INFINITY
	Local miny# = INFINITY
	Local minz# = INFINITY
	Local maxx# = -INFINITY
	Local maxy# = -INFINITY
	Local maxz# = -INFINITY
	
	surfs = CountSurfaces(Mesh)
	
	For s = 1 To surfs
		surf = GetSurface(Mesh, s)
		verts = CountVertices(surf)
		
		For v = 0 To verts - 1
			x = VertexX(surf, v)
			y = VertexY(surf, v)
			z = VertexZ(surf, v)
			
			If (x < minx) Then minx = x
			If (x > maxx) Then maxx = x
			If (y < miny) Then miny = y
			If (y > maxy) Then maxy = y
			If (z < minz) Then minz = z
			If (z > maxz) Then maxz = z
		Next
	Next
	
	Mesh_MinX = minx
	Mesh_MinY = miny
	Mesh_MinZ = minz
	Mesh_MaxX = maxx
	Mesh_MaxY = maxy
	Mesh_MaxZ = maxz
	Mesh_MagX = maxx-minx
	Mesh_MagY = maxy-miny
	Mesh_MagZ = maxz-minz
End Function

Function Graphics3DExt%(width%,height%,depth%=32,mode%=2)
	Graphics3D width,height,depth,mode
	InitFastResize()
	AntiAlias GetINIInt(OptionFile,"options","antialias")
End Function

Function ResizeImage2(image%,width%,height%)
    img% = CreateImage(width,height)
	
	oldWidth% = ImageWidth(image)
	oldHeight% = ImageHeight(image)
	CopyRect 0,0,oldWidth,oldHeight,1024-oldWidth/2,1024-oldHeight/2,ImageBuffer(image),TextureBuffer(fresize_texture)
	SetBuffer BackBuffer()
	ScaleRender(0,0,2048.0 / Float(RealGraphicWidth) * Float(width) / Float(oldWidth), 2048.0 / Float(RealGraphicWidth) * Float(height) / Float(oldHeight))
	;might want to replace Float(GraphicWidth) with Max(GraphicWidth,GraphicHeight) if portrait sizes cause issues
	;everyone uses landscape so it's probably a non-issue
	CopyRect RealGraphicWidth/2-width/2,RealGraphicHeight/2-height/2,width,height,0,0,BackBuffer(),ImageBuffer(img)
	
    FreeImage image
    Return img
End Function

Function RenderWorld2()
	CameraProjMode ark_blur_cam,0
	CameraProjMode Camera,1
	
	If WearingNightVision>0 And WearingNightVision<3 Then
		AmbientLight Min(Brightness*2,255), Min(Brightness*2,255), Min(Brightness*2,255)
	ElseIf WearingNightVision=3
		AmbientLight 255,255,255
	ElseIf PlayerRoom<>Null
		If (PlayerRoom\RoomTemplate\Name<>"173") And (PlayerRoom\RoomTemplate\Name<>"exit1") And (PlayerRoom\RoomTemplate\Name<>"gatea") Then
			AmbientLight Brightness, Brightness, Brightness
		EndIf
	EndIf
	
	IsNVGBlinking% = False
	HideEntity NVBlink
	
	CameraViewport Camera,0,0,GraphicWidth,GraphicHeight
	
	Local hasBattery% = 2
	Local power% = 0
	If (WearingNightVision=1) Or (WearingNightVision=2)
		For i% = 0 To MaxItemAmount - 1
			If (Inventory(i)<>Null) Then
				If (WearingNightVision = 1 And Inventory(i)\itemtemplate\tempname = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\itemtemplate\tempname = "supernv") Then
					Inventory(i)\state = Inventory(i)\state - (FPSfactor * (0.02 * WearingNightVision))
					power%=Int(Inventory(i)\state)
					If Inventory(i)\state<=0.0 Then ;this nvg can't be used
						hasBattery = 0
						Msg = "夜视仪里的电池已经没电了"
						BlinkTimer = -1.0
						MsgTimer = 350
						Exit
					ElseIf Inventory(i)\state<=100.0 Then
						hasBattery = 1
					EndIf
				EndIf
			EndIf
		Next
		
		If (hasBattery) Then
			RenderWorld()
		EndIf
	Else
		RenderWorld()
	EndIf
	
	CurrTrisAmount = TrisRendered()

	If hasBattery=0 And WearingNightVision<>3
		IsNVGBlinking% = True
		ShowEntity NVBlink%
	EndIf
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6
		If WearingNightVision=2 And hasBattery<>0 Then ;show a HUD
			NVTimer=NVTimer-FPSfactor
			
			If NVTimer<=0.0 Then
				For np.NPCs = Each NPCs
					np\NVX = EntityX(np\Collider,True)
					np\NVY = EntityY(np\Collider,True)
					np\NVZ = EntityZ(np\Collider,True)
				Next
				IsNVGBlinking% = True
				ShowEntity NVBlink%
				If NVTimer<=-10
				NVTimer = 600.0
			EndIf
			EndIf
			
			Color 255,255,255
			
			SetFont Font3
			
			Local plusY% = 0
			If hasBattery=1 Then plusY% = 40
			
			Text GraphicWidth/2,(45+plusY)*MenuScale,Max(f2s(NVTimer/60.0,1),0.0),True,False
			Text GraphicWidth/2,(70+plusY)*MenuScale,"秒后刷新数据",True,False
			
			temp% = CreatePivot() : temp2% = CreatePivot()
			PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
			
			Color 255,255,255;
			
			For np.NPCs = Each NPCs
				If np\NVName<>"" And (Not np\HideFromNVG) Then ;don't waste your time if the string is empty
					PositionEntity temp2,np\NVX,np\NVY,np\NVZ
					dist# = EntityDistance(temp2,Collider)
					If dist<23.5 Then ;don't draw text if the NPC is too far away
						PointEntity temp, temp2
						yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
						xvalue# = 0.0
						If yawvalue > 90 And yawvalue <= 180 Then
							xvalue# = Sin(90)/90*yawvalue
						Else If yawvalue > 180 And yawvalue < 270 Then
							xvalue# = Sin(270)/yawvalue*270
						Else
							xvalue = Sin(yawvalue)
						EndIf
						pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
						yvalue# = 0.0
						If pitchvalue > 90 And pitchvalue <= 180 Then
							yvalue# = Sin(90)/90*pitchvalue
						Else If pitchvalue > 180 And pitchvalue < 270 Then
							yvalue# = Sin(270)/pitchvalue*270
						Else
							yvalue# = Sin(pitchvalue)
						EndIf
						
						If (Not IsNVGBlinking%)
						Text GraphicWidth / 2 + xvalue * (GraphicWidth / 2),GraphicHeight / 2 - yvalue * (GraphicHeight / 2),np\NVName,True,True
						Text GraphicWidth / 2 + xvalue * (GraphicWidth / 2),GraphicHeight / 2 - yvalue * (GraphicHeight / 2) + 30.0 * MenuScale,f2s(dist,1)+" m",True,True
					EndIf
				EndIf
				EndIf
			Next
			
			FreeEntity (temp) : FreeEntity (temp2)
			
			Color 0,0,55
			For k=0 To 10
				Rect 45,GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,0,255
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,GraphicHeight*0.5+30,1
			
			Color 255,255,255
		ElseIf WearingNightVision=1 And hasBattery<>0
			Color 0,55,0
			For k=0 To 10
				Rect 45,GraphicHeight*0.5-(k*20),54,10,True
			Next
			Color 0,255,0
			For l=0 To Floor((power%+50)*0.01)
				Rect 45,GraphicHeight*0.5-(l*20),54,10,True
			Next
			DrawImage NVGImages,40,GraphicHeight*0.5+30,0
		EndIf
	EndIf
	
	;render sprites
	CameraProjMode ark_blur_cam,2
	CameraProjMode Camera,0
	RenderWorld()
	CameraProjMode ark_blur_cam,0
	
	If BlinkTimer < - 16 Or BlinkTimer > - 6
		If (WearingNightVision=1 Or WearingNightVision=2) And (hasBattery=1) And ((MilliSecs() Mod 800) < 400) Then
			Color 255,0,0
			SetFont Font3
			
			Text GraphicWidth/2,20*MenuScale,"警告：电量低",True,False
			Color 255,255,255
		EndIf
	EndIf
End Function

Function ScaleRender(x#,y#,hscale#=1.0,vscale#=1.0)
	If Camera<>0 Then HideEntity Camera
	WireFrame 0
	ShowEntity fresize_image
	ScaleEntity fresize_image,hscale,vscale,1.0
	PositionEntity fresize_image, x, y, 1.0001
	ShowEntity fresize_cam
	RenderWorld()
	HideEntity fresize_cam
	HideEntity fresize_image
	WireFrame WireframeState
	If Camera<>0 Then ShowEntity Camera
End Function

Function InitFastResize()
    ;Create Camera
	Local cam% = CreateCamera()
	CameraProjMode cam, 2
	CameraZoom cam, 0.1
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, -10000
	
	fresize_cam = cam
	
    ;Create sprite
	Local spr% = CreateMesh(cam)
	Local sf% = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100001
	EntityBlend spr, 1
	fresize_image = spr
	
    ;Create texture
	fresize_texture = CreateTexture(2048, 2048, 1+256)
	fresize_texture2 = CreateTexture(2048, 2048, 1+256)
	TextureBlend fresize_texture2,3
	SetBuffer(TextureBuffer(fresize_texture2))
	ClsColor 0,0,0
	Cls
	SetBuffer(BackBuffer())
	EntityTexture spr, fresize_texture,0,0
	EntityTexture spr, fresize_texture2,0,1
	
	HideEntity fresize_cam
End Function

;--------------------------------------- Some new 1.3 -functions -------------------------------------------------------

Function UpdateLeave1499()
	Local r.Rooms, it.Items,r2.Rooms,i%
	Local r1499.Rooms
	
	If (Not Wearing1499) And PlayerRoom\RoomTemplate\Name$ = "dimension1499"
		For r.Rooms = Each Rooms
			If r = NTF_1499PrevRoom
				BlinkTimer = -1
				NTF_1499X# = EntityX(Collider)
				NTF_1499Y# = EntityY(Collider)
				NTF_1499Z# = EntityZ(Collider)
				PositionEntity (Collider, NTF_1499PrevX#, NTF_1499PrevY#+0.05, NTF_1499PrevZ#)
				ResetEntity(Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(Collider)<-4600*RoomScale
						For i = 0 To 2
							PlayerRoom\NPC[i]\State = 2
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True),EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True)+0.2,EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True))
							ResetEntity PlayerRoom\NPC[i]\Collider
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState-3)
						Next
					EndIf
				ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
					CameraFogColor Camera, 0,0,0
					CameraClsColor Camera, 0,0,0
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499"
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\disttimer = 0
					If it\itemtemplate\tempname = "scp1499" Or it\itemtemplate\tempname = "super1499"
						If EntityY(it\collider) >= EntityY(r1499\obj)-5
							PositionEntity it\collider,NTF_1499PrevX#,NTF_1499PrevY#+(EntityY(it\collider)-EntityY(r1499\obj)),NTF_1499PrevZ#
							ResetEntity it\collider
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict (LoadTempSound("SFX\SCP\1499\Exit.ogg"))
				NTF_1499PrevX# = 0.0
				NTF_1499PrevY# = 0.0
				NTF_1499PrevZ# = 0.0
				NTF_1499PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
End Function

Function CheckForPlayerInFacility()
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(Collider)>100.0
		Return False
	EndIf
	If EntityY(Collider)< -10.0
		Return 2
	EndIf
	If EntityY(Collider)> 7.0 And EntityY(Collider)<=100.0
		Return 2
	EndIf
	
	Return True
End Function

Function IsItemGoodFor1162(itt.ItemTemplates)
	Local IN$ = itt\tempname$
	
	Select itt\tempname
		Case "key1", "key2", "key3"
			Return True
		Case "misc", "420", "cigarette"
			Return True
		Case "vest", "finevest","gasmask"
			Return True
		Case "radio","18vradio"
			Return True
		Case "clipboard","eyedrops","nvgoggles"
			Return True
		Case "drawing"
			If itt\img<>0 Then FreeImage itt\img	
			itt\img = LoadImage_Strict("GFX\items\1048\1048_"+Rand(1,20)+".jpg") ;Gives a random drawing.
			Return True
		Default
			If itt\tempname <> "paper" Then
				Return False
			Else If Instr(itt\name, "Leaflet")
				Return False
			Else
				;if the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				;(because those are items created recently, which D-9341 has most likely never seen)
				Return ((Not Instr(itt\name, "Note")) And (Not Instr(itt\name, "Log")))
			EndIf
	End Select
End Function

Function ControlSoundVolume()
	Local snd.Sound,i
	
	For snd.Sound = Each Sound
		For i=0 To 31
			ChannelVolume snd\channels[i],SFXVolume#
		Next
	Next
End Function

Function UpdateDeafPlayer()
	If DeafTimer > 0
		DeafTimer = DeafTimer-FPSfactor
		SFXVolume# = 0.0
		If SFXVolume# > 0.0
			ControlSoundVolume()
		EndIf
		DebugLog DeafTimer
	Else
		DeafTimer = 0
		SFXVolume# = PrevSFXVolume#
		If DeafPlayer Then ControlSoundVolume()
		DeafPlayer = False
	EndIf
End Function

Function CheckTriggers$()
	Local i%,sx#,sy#,sz#
	Local inside% = -1
	
	If PlayerRoom\TriggerboxAmount = 0
		Return ""
	Else
		For i = 0 To PlayerRoom\TriggerboxAmount-1
			EntityAlpha PlayerRoom\Triggerbox[i],1.0
			sx# = EntityScaleX(PlayerRoom\Triggerbox[i], 1)
			sy# = Max(EntityScaleY(PlayerRoom\Triggerbox[i], 1), 0.001)
			sz# = EntityScaleZ(PlayerRoom\Triggerbox[i], 1)
			GetMeshExtents(PlayerRoom\Triggerbox[i])
			If DebugHUD
				EntityColor PlayerRoom\Triggerbox[i],255,255,0
				EntityAlpha PlayerRoom\Triggerbox[i],0.2
			Else
				EntityColor PlayerRoom\Triggerbox[i],255,255,255
				EntityAlpha PlayerRoom\Triggerbox[i],0.0
 			EndIf
			If EntityX(Collider)>((sx#*Mesh_MinX)+PlayerRoom\x) And EntityX(Collider)<((sx#*Mesh_MaxX)+PlayerRoom\x)
				If EntityY(Collider)>((sy#*Mesh_MinY)+PlayerRoom\y) And EntityY(Collider)<((sy#*Mesh_MaxY)+PlayerRoom\y)
					If EntityZ(Collider)>((sz#*Mesh_MinZ)+PlayerRoom\z) And EntityZ(Collider)<((sz#*Mesh_MaxZ)+PlayerRoom\z)
						inside% = i%
						Exit
					EndIf
				EndIf
			EndIf
		Next
		
		If inside% > -1 Then Return PlayerRoom\TriggerboxName[inside%]
	EndIf
End Function

Function ScaledMouseX%()
	Return Float(MouseX()-(RealGraphicWidth*0.5*(1.0-AspectRatioRatio)))*Float(GraphicWidth)/Float(RealGraphicWidth*AspectRatioRatio)
End Function

Function ScaledMouseY%()
	Return Float(MouseY())*Float(GraphicHeight)/Float(RealGraphicHeight)
End Function

Function CatchErrors(location$)
	InitErrorMsgs(10, True) ; 声明报错内有宏存在
	SetErrorMsg(0, "SCP - 收容失效 汉化计划 v" + VersionNumber + " 出现错误！")
	SetErrorMsg(1, "汉化计划版本号：" + SinicizationNumber)
	SetErrorMsg(2, "地图种子：" + RandomSeed)
	SetErrorMsg(3, "日期和时间：" + CurrentDate() + "，" + CurrentTime())
	SetErrorMsg(4, "系统：" + SystemProperty("os") + " " + (32 + (GetEnv("ProgramFiles(X86)") <> 0) * 32) + " bit (Build: " + SystemProperty("osbuild") + ")" + Chr(10))
	SetErrorMsg(5, "显示内存：" + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB")
	SetErrorMsg(6, "全局内存状态：" + ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB")
	SetErrorMsg(7, "出错位置：" + location)
	SetErrorMsg(8, "抛出异常：_CaughtError_" + Chr(10)) ; _CaughtError_是一个宏，在显示时会被替换为引擎最后抛出的异常
	SetErrorMsg(9, "请带着游戏截图联系我们！")
End Function

Function Create3DIcon(width%,height%,modelpath$,modelX#=0,modelY#=0,modelZ#=0,modelPitch#=0,modelYaw#=0,modelRoll#=0,modelscaleX#=1,modelscaleY#=1,modelscaleZ#=1,withfog%=False)
	Local img% = CreateImage(width,height)
	Local cam% = CreateCamera()
	Local model%
	
	CameraRange cam,0.01,16
	CameraViewport cam,0,0,width,height
	If withfog
		CameraFogMode cam,1
		CameraFogRange cam,CameraFogNear,CameraFogFar
	EndIf
	
	If Right(Lower(modelpath$),6)=".rmesh"
		model = LoadRMesh(modelpath$,Null)
	Else
		model = LoadMesh(modelpath$)
	EndIf
	ScaleEntity model,modelscaleX,modelscaleY,modelscaleZ
	PositionEntity model,modelX#,modelY#,modelZ#
	RotateEntity model,modelPitch#,modelYaw#,modelRoll#
	
	;Cls
	RenderWorld
	CopyRect 0,0,width,height,0,0,BackBuffer(),ImageBuffer(img)
	
	FreeEntity model
	FreeEntity cam
	Return img%
End Function

Function PlayAnnouncement(file$) ;This function streams the announcement currently playing
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = PlaySound_Strict(LoadTempSound(file))
End Function

Function UpdateStreamSounds()
	Local e.Events
	
	If FPSfactor > 0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN,SFXVolume)
		EndIf
		For e = Each Events
			If e\SoundCHN<>0 Then
				If e\SoundCHN_isStream
					SetStreamVolume_Strict(e\SoundCHN,SFXVolume)
				EndIf
			EndIf
			If e\SoundCHN2<>0 Then
				If e\SoundCHN2_isStream
					SetStreamVolume_Strict(e\SoundCHN2,SFXVolume)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
			If PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
				For e = Each Events
					If e\SoundCHN<>0 And e\SoundCHN_isStream Then
						StopStream_Strict(e\SoundCHN)
						e\SoundCHN = 0
						e\SoundCHN_isStream = 0
					EndIf
					If e\SoundCHN2<>0 And e\SoundCHN2_isStream Then
						StopStream_Strict(e\SoundCHN2)
						e\SoundCHN2 = 0
						e\SoundCHN2_isStream = 0
					EndIf
				Next
			EndIf
		EndIf
	EndIf
End Function

Function TeleportEntity(entity%,x#,y#,z#,customradius#=0.3,isglobal%=False,pickrange#=2.0,dir%=0)
	Local pvt,pick
	;dir = 0 - towards the floor (default)
	;dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	pvt = CreatePivot()
	PositionEntity(pvt, x,y+0.05,z,isglobal)
	If dir%=0
		RotateEntity pvt,90,0,0
	Else
		RotateEntity pvt,-90,0,0
	EndIf
	pick = EntityPick(pvt,pickrange)
	If pick<>0
		If dir%=0
			PositionEntity(entity, x,PickedY()+customradius#+0.02,z,isglobal)
		Else
			PositionEntity(entity, x,PickedY()+customradius#-0.02,z,isglobal)
		EndIf
		DebugLog "Entity teleported successfully"
	Else
		PositionEntity(entity,x,y,z,isglobal)
		DebugLog "Warning: no ground found when teleporting an entity"
	EndIf
	FreeEntity pvt
	ResetEntity entity
	DebugLog "Teleported entity to: "+EntityX(entity)+"/"+EntityY(entity)+"/"+EntityZ(entity)
End Function

Function PlayStartupVideos()
	If GetINIInt("options.ini","options","play startup video")=0 Then Return
	
	HidePointer()
	Local SplashScreenAudio%
	Local Movie%
	Local ScaledGraphicHeight%
	Local Ratio# = Float(RealGraphicWidth) / Float(RealGraphicHeight)
	
	If Ratio > 1.76 And Ratio < 1.78 Then
		ScaledGraphicHeight = RealGraphicHeight
	Else
		ScaledGraphicHeight = Float(RealGraphicWidth) / (16.0 / 9.0)
	EndIf
	
	Local MovieFile$, i%
	
	For i = 0 To 1
		Select i
			Case 0
				;[Block]
				MovieFile = "GFX\menu\startup_Undertow"
				;[End Block]
			Case 1
				;[Block]
				MovieFile = "GFX\menu\startup_TSS"
				;[End Block]
		End Select
		
		Movie% = OpenMovie(MovieFile + ".avi")
		SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", SFXVolume, 0)
		
		Repeat
			Cls()
			DrawMovie(Movie, 0, (RealGraphicHeight / 2 - ScaledGraphicHeight / 2), RealGraphicWidth, ScaledGraphicHeight)
			Flip()
		Until (GetKey() Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		StopStream_Strict(SplashScreenAudio)
		CloseMovie(Movie)
		
		Cls()
		Flip()
	Next
	ShowPointer()
End Function

Function CanUseItem(canUseWithHazmat%, canUseWithGasMask%, canUseWithEyewear%)
	If (canUseWithHazmat = False And WearingHazmat) Then
		Msg = "你不能在穿着防护服时使用物品"
		MsgTimer = 70*5
		Return False
	Else If (canUseWithGasMask = False And (WearingGasMask Or Wearing1499))
		Msg = "你不能在戴着防毒面具时使用物品"
		MsgTimer = 70*5
		Return False
	Else If (canUseWithEyewear = False And (WearingNightVision))
		Msg = "你不能在戴着夜视仪时使用物品"
		MsgTimer = 70*5
		Return False
	EndIf
	
	Return True
End Function

Function ResetInput()
	FlushKeys()
	FlushMouse()
	MouseHit1 = 0
	MouseHit2 = 0
	MouseDown1 = 0
	MouseUp1 = 0
	MouseHit(1)
	MouseHit(2)
	MouseDown(1)
	GrabbedEntity = 0
	Input_ResetTime# = 10.0
End Function

Function Update096ElevatorEvent#(e.Events,EventState#,d.Doors,elevatorobj%)
	Local prevEventState# = EventState#
	
	If EventState < 0 Then
		EventState = 0
		prevEventState = 0
	EndIf
	
	If d\openstate = 0 And d\open = False Then
		If Abs(EntityX(Collider)-EntityX(elevatorobj%,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
			If Abs(EntityZ(Collider)-EntityZ(elevatorobj%,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
				If Abs(EntityY(Collider)-EntityY(elevatorobj%,True))<=280.0*RoomScale+(0.015*FPSfactor) Then
					d\locked = True
					If EventState = 0 Then
						TeleportEntity(Curr096\Collider,EntityX(d\frameobj),EntityY(d\frameobj)+1.0,EntityZ(d\frameobj),Curr096\CollRadius)
						PointEntity Curr096\Collider,elevatorobj
						RotateEntity Curr096\Collider,0,EntityYaw(Curr096\Collider),0
						MoveEntity Curr096\Collider,0,0,-0.5
						ResetEntity Curr096\Collider
						Curr096\State = 6
						SetNPCFrame(Curr096,0)
						e\Sound = LoadSound_Strict("SFX\SCP\096\ElevatorSlam.ogg")
						EventState = EventState + FPSfactor * 1.4
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	If EventState > 0 Then
		If prevEventState = 0 Then
			e\SoundCHN = PlaySound_Strict(e\Sound)
		EndIf
		
		If EventState > 70*1.9 And EventState < 70*2+FPSfactor
			CameraShake = 7
		ElseIf EventState > 70*4.2 And EventState < 70*4.25+FPSfactor
			CameraShake = 1
		ElseIf EventState > 70*5.9 And EventState < 70*5.95+FPSfactor
			CameraShake = 1
		ElseIf EventState > 70*7.25 And EventState < 70*7.3+FPSfactor
			CameraShake = 1
			d\fastopen = True
			d\open = True
			Curr096\State = 4
			Curr096\LastSeen = 1
		ElseIf EventState > 70*8.1 And EventState < 70*8.15+FPSfactor
			CameraShake = 1
		EndIf
		
		If EventState <= 70*8.1 Then
			d\openstate = Min(d\openstate,20)
		EndIf
		EventState = EventState + FPSfactor * 1.4
	EndIf
	Return EventState
End Function

Function RotateEntity90DegreeAngles(entity%)
	Local angle = WrapAngle(entity)
	
	If angle < 45.0 Then
		Return 0
	ElseIf angle >= 45.0 And angle < 135 Then
		Return 90
	ElseIf angle >= 135 And angle < 225 Then
		Return 180
	Else
		Return 270
	EndIf
End Function

Function Text(x%, y%, txt$, xPos% = 0, yPos% = 0, a# = 1.0)
	Local oldr% = ColorRed() : Local oldg% = ColorGreen() : Local oldb% = ColorBlue()
	Color oldr*a, oldg*a, oldb*a
	If TraditionalChinese Then 
		Blitz_Text x, y+1, OpenCConvert(OpenCC, txt), xPos, yPos ; 中文比英文高一点，所以高度要低一点
	Else
		Blitz_Text x, y+1, txt, xPos, yPos
	EndIf
	Color oldr, oldg, oldb
	Return
End Function