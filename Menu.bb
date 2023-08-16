Global MenuBack% = LoadImage_Strict("GFX\menu\back.jpg")
Global MenuText% = LoadImage_Strict("GFX\menu\scptext.jpg")
Global Menu173% = LoadImage_Strict("GFX\menu\173back.jpg")
MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")
MaskImage MenuBlack, 255,255,0
Global QuickLoadIcon% = LoadImage_Strict("GFX\menu\QuickLoading.png")

ResizeImage(MenuBack, ImageWidth(MenuBack) * MenuScale, ImageHeight(MenuBack) * MenuScale)
ResizeImage(MenuText, ImageWidth(MenuText) * MenuScale, ImageHeight(MenuText) * MenuScale)
ResizeImage(Menu173, ImageWidth(Menu173) * MenuScale, ImageHeight(Menu173) * MenuScale)
ResizeImage(QuickLoadIcon, ImageWidth(QuickLoadIcon) * MenuScale, ImageHeight(QuickLoadIcon) * MenuScale)

For i = 0 To 3
	ArrowIMG(i) = LoadImage_Strict("GFX\menu\arrow.png")
	RotateImage(ArrowIMG(i), 90 * i)
	HandleImage(ArrowIMG(i), 0, 0)
Next

Global RandomSeed$

Dim MenuBlinkTimer%(2), MenuBlinkDuration%(2)
MenuBlinkTimer%(0) = 1
MenuBlinkTimer%(1) = 1

Global MenuStr$, MenuStrX%, MenuStrY%

Global MainMenuTab%

Global IntroEnabled% = GetINIInt(OptionFile, "options", "intro enabled")

Global SelectedInputBox%

Global SavePath$ = "Saves\"
Global SaveMSG$

Global CurrSave$

Global SaveGameAmount%
Dim SaveGames$(SaveGameAmount+1) 
Dim SaveGameTime$(SaveGameAmount + 1)
Dim SaveGameDate$(SaveGameAmount + 1)
Dim SaveGameVersion$(SaveGameAmount + 1)

Global SavedMapsAmount% = 0
Dim SavedMaps$(SavedMapsAmount+1)
Dim SavedMapsAuthor$(SavedMapsAmount+1)

Global SelectedMap$

LoadSaveGames()

Global CurrLoadGamePage% = 0

Function UpdateMainMenu()	
	Local x%, y%, width%, height%, temp%
	
	Color 0,0,0
	Rect 0,0,GraphicWidth,GraphicHeight,True
	
	ShowPointer()
	
	DrawImage(MenuBack, 0, 0)
	
	If (MilliSecs() Mod MenuBlinkTimer(0)) >= Rand(MenuBlinkDuration(0)) Then
		DrawImage(Menu173, GraphicWidth - ImageWidth(Menu173), GraphicHeight - ImageHeight(Menu173))
	EndIf
	
	If Rand(300) = 1 Then
		MenuBlinkTimer(0) = Rand(4000, 8000)
		MenuBlinkDuration(0) = Rand(200, 500)
	End If
	
	SetFont Font1
	
	MenuBlinkTimer(1)=MenuBlinkTimer(1)-FPSfactor
	If MenuBlinkTimer(1) < MenuBlinkDuration(1) Then
		Color(50, 50, 50)
		Text(MenuStrX + Rand(-5, 5), MenuStrY + Rand(-5, 5), MenuStr, True)
		If MenuBlinkTimer(1) < 0 Then
			MenuBlinkTimer(1) = Rand(700, 800)
			MenuBlinkDuration(1) = Rand(10, 35)
			MenuStrX = Rand(700, 1000) * MenuScale
			MenuStrY = Rand(100, 600) * MenuScale
			
			Select Rand(0, 22)
				Case 0, 2, 3
					MenuStr = "不要眨眼。"
				Case 4, 5
					MenuStr = "控制 收容 保护"
				Case 6, 7, 8
					MenuStr = "你想要个好结局？滚你妈。"
				Case 9, 10, 11
					MenuStr = "有时我们不得不尖叫"
				Case 12, 19
					MenuStr = "无。"
				Case 13
					MenuStr = "不。"
				Case 14
					MenuStr = "黑白黑白黑白灰"
				Case 15
					MenuStr = "雕像不在乎"
				Case 16
					MenuStr = "9341"
				Case 17
					MenuStr = "它控制着门"
				Case 18
					MenuStr = "e8m106]af173o+079m895w914"
				Case 20
					MenuStr = "它已接管了一切"
				Case 21
					MenuStr = "螺旋式增长"
				Case 22
					MenuStr = "“由于巨大的现实伤害而产生的某种格式塔效应。”"
			End Select
		EndIf
	EndIf
	
	SetFont Font2
	
	DrawImage(MenuText, GraphicWidth / 2 - ImageWidth(MenuText) / 2, GraphicHeight - 20 * MenuScale - ImageHeight(MenuText))
	
	If GraphicWidth > 1240 * MenuScale Then
		DrawTiledImageRect(MenuWhite, 0, 5, 512, 7 * MenuScale, 985.0 * MenuScale, 407.0 * MenuScale, (GraphicWidth - 1240 * MenuScale) + 300, 7 * MenuScale)
	EndIf
	
	If (Not MouseDown1) Then OnSliderID = 0
	
	If MainMenuTab = 0 Then
		For i% = 0 To 3
			temp = False
			x = 159 * MenuScale
			y = (286 + 100 * i) * MenuScale
			
			width = 400 * MenuScale
			height = 70 * MenuScale
			
			temp = (MouseHit1 And MouseOn(x, y, width, height))
			
			Local txt$
			Select i
				Case 0
					txt = "新游戏"
					RandomSeed = ""
					If temp Then 
						If Rand(15)=1 Then 
							Select Rand(13)
								Case 1 
									RandomSeed = "NIL"
								Case 2
									RandomSeed = "NO"
								Case 3
									RandomSeed = "d9341"
								Case 4
									RandomSeed = "5CP_I73"
								Case 5
									RandomSeed = "DONTBLINK"
								Case 6
									RandomSeed = "CRUNCH"
								Case 7
									RandomSeed = "die"
								Case 8
									RandomSeed = "HTAED"
								Case 9
									RandomSeed = "rustledjim"
								Case 10
									RandomSeed = "larry"
								Case 11
									RandomSeed = "JORGE"
								Case 12
									RandomSeed = "dirtymetal"
								Case 13
									RandomSeed = "whatpumpkin"
							End Select
						Else
							n = Rand(4,8)
							For i = 1 To n
								If Rand(3)=1 Then
									RandomSeed = RandomSeed + Rand(0,9)
								Else
									RandomSeed = RandomSeed + Chr(Rand(97,122))
								EndIf
							Next							
						EndIf
						
						MainMenuTab = 1
					EndIf
				Case 1
					txt = "加载游戏"
					If temp Then
						LoadSaveGames()
						MainMenuTab = 2
					EndIf
				Case 2
					txt = "设置"
					If temp Then MainMenuTab = 3
				Case 3
					txt = "退出"
					If temp Then
						StopChannel(CurrMusicStream)
						End
					EndIf
			End Select
			
			DrawButton(x, y, width, height, txt, True, False, -3)
		Next
	Else
		x = 159 * MenuScale
		y = 286 * MenuScale
		
		width = 400 * MenuScale
		height = 70 * MenuScale
		
		DrawFrame(x, y, width, height)
		
		If DrawButton(x + width + 20 * MenuScale, y, 580 * MenuScale - width - 20 * MenuScale, height, "返回", False) Then 
			Select MainMenuTab
				Case 1
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					MainMenuTab = 0
				Case 2
					CurrLoadGamePage = 0
					MainMenuTab = 0
				Case 3,5,6,7 ;save the options
					SaveOptionsINI()
					
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
					
					AntiAlias Opt_AntiAlias
					MainMenuTab = 0
				Case 4 ;move back to the "new game" tab
					MainMenuTab = 1
					CurrLoadGamePage = 0
					MouseHit1 = False
				Default
					MainMenuTab = 0
			End Select
		EndIf
		
		Select MainMenuTab
			Case 1 ; New game
				;[Block]
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont Font2
				Text(x + width / 2, y + height / 2 + 2, "新游戏", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 330 * MenuScale
				
				DrawFrame(x, y, width, height)				
				
				SetFont Font1
				
				Text (x + 20 * MenuScale, y + 20 * MenuScale + 3, "名称：")
				CurrSave = InputBox(x + 150 * MenuScale, y + 15 * MenuScale, 200 * MenuScale, 30 * MenuScale, CurrSave, 1)
				CurrSave = Left(CurrSave, 15)
				CurrSave = Replace(CurrSave,":","")
				CurrSave = Replace(CurrSave,".","")
				CurrSave = Replace(CurrSave,"/","")
				CurrSave = Replace(CurrSave,"\","")
				CurrSave = Replace(CurrSave,"<","")
				CurrSave = Replace(CurrSave,">","")
				CurrSave = Replace(CurrSave,"|","")
				CurrSave = Replace(CurrSave,"?","")
				CurrSave = Replace(CurrSave,Chr(34),"")
				CurrSave = Replace(CurrSave,"*","")
				
				Color 255,255,255
				If SelectedMap = "" Then
					Text (x + 20 * MenuScale, y + 60 * MenuScale + 3, "地图种子：")
					RandomSeed = Left(InputBox(x+150*MenuScale, y+55*MenuScale, 200*MenuScale, 30*MenuScale, RandomSeed, 3),15)	
				Else
					Text (x + 20 * MenuScale, y + 60 * MenuScale + 3, "已选地图：")
					Color (255, 255, 255)
					Rect(x+150*MenuScale, y+55*MenuScale, 200*MenuScale, 30*MenuScale)
					Color (0, 0, 0)
					Rect(x+150*MenuScale+2, y+55*MenuScale+2, 200*MenuScale-4, 30*MenuScale-4)
					
					Color (255, 0,0)
					If Len(ConvertToUTF8(SelectedMap))>15 Then
						Text(x+150*MenuScale + 100*MenuScale, y+55*MenuScale + 15*MenuScale, Left(ConvertToUTF8(SelectedMap),14)+"...", True, True)
					Else
						Text(x+150*MenuScale + 100*MenuScale, y+55*MenuScale + 15*MenuScale, ConvertToUTF8(SelectedMap), True, True)
					EndIf
					
					If DrawButton(x+370*MenuScale, y+55*MenuScale, 120*MenuScale, 30*MenuScale, "取消选择", False) Then
						SelectedMap=""
					EndIf
				EndIf	
				
				Text(x + 20 * MenuScale, y + 110 * MenuScale + 3, "启用序章：")
				IntroEnabled = DrawTick(x + 280 * MenuScale, y + 110 * MenuScale, IntroEnabled)	
				
				Text (x + 20 * MenuScale, y + 150 * MenuScale + 3, "难度：")				
				For i = SAFE To CUSTOM
					If DrawTick(x + 20 * MenuScale, y + (180+30*i) * MenuScale, (SelectedDifficulty = difficulties(i))) Then SelectedDifficulty = difficulties(i)
					Color(difficulties(i)\r,difficulties(i)\g,difficulties(i)\b)
					Text(x + 60 * MenuScale, y + (180+30*i) * MenuScale + 3, difficulties(i)\name)
				Next
				
				Color(255, 255, 255)
				DrawFrame(x + 150 * MenuScale,y + 155 * MenuScale, 410*MenuScale, 150*MenuScale)
				
				If SelectedDifficulty\customizable Then
					SelectedDifficulty\permaDeath =  DrawTick(x + 160 * MenuScale, y + 165 * MenuScale, (SelectedDifficulty\permaDeath))
					Text(x + 200 * MenuScale, y + 165 * MenuScale + 3, "永久死亡")
					
					If DrawTick(x + 160 * MenuScale, y + 195 * MenuScale, SelectedDifficulty\saveType = SAVEANYWHERE And (Not SelectedDifficulty\permaDeath), SelectedDifficulty\permaDeath) Then 
						SelectedDifficulty\saveType = SAVEANYWHERE
					Else
						SelectedDifficulty\saveType = SAVEONSCREENS
					EndIf
					
					Text(x + 200 * MenuScale, y + 195 * MenuScale + 3, "随时保存")	
					
					SelectedDifficulty\aggressiveNPCs =  DrawTick(x + 160 * MenuScale, y + 225 * MenuScale, SelectedDifficulty\aggressiveNPCs)
					Text(x + 200 * MenuScale, y + 225 * MenuScale + 3, "更危险的NPC")
					
					;Other factor's difficulty
					Color 255,255,255
					DrawImage ArrowIMG(1),x + 155 * MenuScale, y+251*MenuScale
					If MouseHit1
						If ImageRectOverlap(ArrowIMG(1),x + 155 * MenuScale, y+251*MenuScale, ScaledMouseX(),ScaledMouseY(),0,0)
							If SelectedDifficulty\otherFactors < HARD
								SelectedDifficulty\otherFactors = SelectedDifficulty\otherFactors + 1
							Else
								SelectedDifficulty\otherFactors = EASY
							EndIf
							PlaySound_Strict(ButtonSFX)
						EndIf
					EndIf
					Color 255,255,255
					Select SelectedDifficulty\otherFactors
						Case EASY
							Text(x + 200 * MenuScale, y + 255 * MenuScale + 3, "其他难度预设: 简单")
						Case NORMAL
							Text(x + 200 * MenuScale, y + 255 * MenuScale + 3, "其他难度预设: 普通")
						Case HARD
							Text(x + 200 * MenuScale, y + 255 * MenuScale + 3, "其他难度预设: 困难")
					End Select
				Else
					RowText(SelectedDifficulty\description, x+160*MenuScale - 2, y+160*MenuScale + 2, (410-20)*MenuScale, 200)					
				EndIf
				
				If DrawButton(x, y + height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "加载地图", False) Then
					MainMenuTab = 4
					LoadSavedMaps()
				EndIf
				
				SetFont Font2
				
				If DrawButton(x + 420 * MenuScale, y + height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "开始", False) Then
					CurrSave = ConvertToANSI(CurrSave)
					If CurrSave = "" Then CurrSave = ConvertToANSI("未命名")
					
					If RandomSeed = "" Then
						RandomSeed = Abs(MilliSecs())
					EndIf
					
					SeedRnd GenerateSeedNumber(RandomSeed)
					
					Local SameFound% = False
					
					For  i% = 1 To SaveGameAmount
						If SaveGames(i - 1) = CurrSave Then SameFound = SameFound + 1
					Next
						
					If SameFound > 0 Then CurrSave = CurrSave + " (" + (SameFound + 1) + ")"
					
					LoadEntities()
					LoadAllSounds()
					InitNewGame()
					MainMenuOpen = False
					FlushKeys()
					FlushMouse()
					
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					
				EndIf
				
				;[End Block]
			Case 2 ;load game
				;[Block]
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 510 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont Font2
				Text(x + width / 2, y + height / 2 + 2, "加载游戏", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 296 * MenuScale
				
				SetFont Font2
				
				If CurrLoadGamePage < Ceil(Float(SaveGameAmount)/6.0)-1 And SaveMSG = "" Then 
					If DrawButton(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage+1
					EndIf
				Else
					DrawFrame(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+555*MenuScale, y + 537.5*MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 And SaveMSG = "" Then
					If DrawButton(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage-1
					EndIf
				Else
					DrawFrame(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+25*MenuScale, y + 537.5*MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x+50*MenuScale,y+510*MenuScale,width-100*MenuScale,55*MenuScale)
				
				Text(x+(width/2.0),y+536*MenuScale,"第 "+Int(Max((CurrLoadGamePage+1),1))+"/"+Int(Max((Int(Ceil(Float(SaveGameAmount)/6.0))),1)) + " 页",True,True)
				
				SetFont Font1
				
				If CurrLoadGamePage > Ceil(Float(SaveGameAmount)/6.0)-1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				If SaveGameAmount = 0 Then
					Text (x + 20 * MenuScale, y + 20 * MenuScale, "没有保存的存档。")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					
					For i% = (1+(6*CurrLoadGamePage)) To 6+(6*CurrLoadGamePage)
						If i <= SaveGameAmount Then
							DrawFrame(x,y,540* MenuScale, 70* MenuScale)
							
							If Left(SaveGameVersion(i - 1),6) <> "1.3.11" Then Color 255,0,0 Else Color 255,255,255
							
							Text(x + 20 * MenuScale, y + 10 * MenuScale, ConvertToUTF8(SaveGames(i - 1)))
							Text(x + 20 * MenuScale, y + (10+18) * MenuScale, SaveGameTime(i - 1))
							Text(x + 120 * MenuScale, y + (10+18) * MenuScale, SaveGameDate(i - 1))
							Text(x + 20 * MenuScale, y + (10+36) * MenuScale, SaveGameVersion(i - 1))
							
							If SaveMSG = "" Then
								If Left(SaveGameVersion(i - 1),6) <> "1.3.11" Then
									DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
									Color(255, 0, 0)
									Text(x + 330 * MenuScale, y + 34 * MenuScale, "加载", True, True)
								Else
									If DrawButton(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "加载", False) Then
										LoadEntities()
										LoadAllSounds()
										LoadGame(SavePath + SaveGames(i - 1) + "\")
										CurrSave = SaveGames(i - 1)
										InitLoadGame()
										MainMenuOpen = False
									EndIf
								EndIf
								
								If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "删除", False) Then
									SaveMSG = SaveGames(i - 1)
									DebugLog SaveMSG
									Exit
								EndIf
							Else
								DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								If Left(SaveGameVersion(i - 1),6) <> "1.3.11" Then Color 255, 0, 0 Else Color 100, 100, 100
								Text(x + 330 * MenuScale, y + 34 * MenuScale, "加载", True, True)
								
								DrawFrame(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								Color(100, 100, 100)
								Text(x + 450 * MenuScale, y + 34 * MenuScale, "删除", True, True)
							EndIf
							
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
					
					If SaveMSG <> ""
						x = 740 * MenuScale
						y = 376 * MenuScale
						DrawFrame(x, y, 420 * MenuScale, 200 * MenuScale)
						RowText("你确定要删除这个存档吗？", x + 20 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
						If DrawButton(x + 50 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "是", False) Then
							DeleteFile(CurrentDir() + SavePath + SaveMSG + "\save.txt")
							DeleteDir(CurrentDir() + SavePath + SaveMSG)
							SaveMSG = ""
							LoadSaveGames()
						EndIf
						If DrawButton(x + 250 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "否", False) Then
							SaveMSG = ""
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case 3,5,6,7 ;options
				;[Block]
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont Font2
				Text(x + width / 2, y + height / 2 + 2, "设置", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 60 * MenuScale
				DrawFrame(x, y, width, height)
				
				Color 0,255,0
				If MainMenuTab = 3
					Rect(x+15*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 5
					Rect(x+155*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 6
					Rect(x+295*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 7
					Rect(x+435*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				EndIf
				
				Color 255,255,255
				If DrawButton(x+20*MenuScale,y+15*MenuScale,width/5,height/2, "画面", False) Then MainMenuTab = 3
				If DrawButton(x+160*MenuScale,y+15*MenuScale,width/5,height/2, "音频", False) Then MainMenuTab = 5
				If DrawButton(x+300*MenuScale,y+15*MenuScale,width/5,height/2, "控制", False) Then MainMenuTab = 6
				If DrawButton(x+440*MenuScale,y+15*MenuScale,width/5,height/2, "高级", False) Then MainMenuTab = 7
				
				SetFont Font1
				y = y + 70 * MenuScale
				
				If MainMenuTab <> 5
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
				EndIf
				
				Local tx# = x+width
				Local ty# = y
				Local tw# = 400*MenuScale
				Local th# = 150*MenuScale
				
				Select MainMenuTab
					Case 3: ;Graphics
						;[Block]
						height = 330 * MenuScale
						DrawFrame(x, y, width, height)

						y=y+20*MenuScale

						Color 255,255,255				
						Text(x + 20 * MenuScale, y + 4, "启用凹凸贴图：")	
						BumpEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, BumpEnabled)
						If MouseOn(x + 310 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"bump")
						EndIf

						y=y+30*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 4, "垂直同步：")
						Vsync% = DrawTick(x + 310 * MenuScale, y + MenuScale, Vsync%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"vsync")
						EndIf

						y=y+30*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 4, "抗锯齿：")
						Opt_AntiAlias = DrawTick(x + 310 * MenuScale, y + MenuScale, Opt_AntiAlias%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"antialias")
						EndIf

						y=y+30*MenuScale ;40

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 5, "启用房间光亮：")
						EnableRoomLights = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableRoomLights)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
						EndIf

						y=y+30*MenuScale

						ScreenGamma = (SlideBar(x + 310*MenuScale, y+6*MenuScale, 150*MenuScale, ScreenGamma*50.0)/50.0)
						Color 255,255,255
						Text(x + 20 * MenuScale, y + 7, "屏幕伽马值：")
						If MouseOn(x+310*MenuScale,y+6*MenuScale,150*MenuScale+14,20) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
						EndIf

						y=y+50*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 4, "粒子数量：")
						ParticleAmount = Slider3(x+310*MenuScale,y+6*MenuScale,150*MenuScale,ParticleAmount,2,"最少","适中","最多")
						If (MouseOn(x + 310 * MenuScale, y-6*MenuScale, 150*MenuScale+14, 20) And OnSliderID=0) Or OnSliderID=2
							DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
						EndIf

						y=y+50*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 3, "纹理细节层次：")
						TextureDetails = Slider5(x+310*MenuScale,y+6*MenuScale,150*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
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
						If (MouseOn(x+310*MenuScale,y-6*MenuScale,150*MenuScale+14,20) And OnSliderID=0) Or OnSliderID=3
							DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
						EndIf

						y=y+50*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 5, "在VRAM中存储贴图：")
						EnableVRam = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableVRam)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"vram")
						EndIf

						;[End Block]
					Case 5: ;Audio
						;[Block]
						height = 220 * MenuScale
						DrawFrame(x, y, width, height)	

						y = y + 20*MenuScale

						MusicVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, MusicVolume*100.0)/100.0)
						Color 255,255,255
						Text(x + 20 * MenuScale, y + 1, "音乐大小：")
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
						EndIf

						y = y + 40*MenuScale

						PrevSFXVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, SFXVolume*100.0)/100.0)
						SFXVolume = PrevSFXVolume
						Color 255,255,255
						Text(x + 20 * MenuScale, y + 1, "音效大小：")
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
						EndIf

						y = y + 30*MenuScale

						Color 255,255,255
						Text x + 20 * MenuScale, y + 2, "自动释放音频："
						EnableSFXRelease = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableSFXRelease)
						If EnableSFXRelease_Prev% <> EnableSFXRelease
							If EnableSFXRelease%
								For snd.Sound = Each Sound
									For i=0 To 31
										If snd\channels[i]<>0 Then
											If ChannelPlaying(snd\channels[i]) Then
												StopChannel(snd\channels[i])
											EndIf
										EndIf
									Next
									If snd\internalHandle<>0 Then
										FreeSound snd\internalHandle
										snd\internalHandle = 0
									EndIf
									snd\releaseTime = 0
								Next
							Else
								For snd.Sound = Each Sound
									If snd\internalHandle = 0 Then snd\internalHandle = LoadSound(snd\name)
								Next
							EndIf
							EnableSFXRelease_Prev% = EnableSFXRelease
						EndIf
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
						EndIf
						y = y + 30*MenuScale

						Color 255,255,255
						Text x + 20 * MenuScale, y + 2, "启用自定频道："
						EnableUserTracks = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableUserTracks)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
						EndIf

						If EnableUserTracks
							y = y + 30 * MenuScale
							Color 255,255,255
							Text x + 20 * MenuScale, y + 3, "自定频道模式："
							UserTrackMode = DrawTick(x + 310 * MenuScale, y + MenuScale, UserTrackMode)
							If UserTrackMode
								Text x + 350 * MenuScale, y + MenuScale + 3, "循环"
							Else
								Text x + 350 * MenuScale, y + MenuScale + 3, "随机"
							EndIf
							If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
								DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
							EndIf
							If DrawButton(x + 20 * MenuScale, y + 30 * MenuScale + 1, 190 * MenuScale, 25 * MenuScale, "扫描自定频道",False)
								DebugLog "User Tracks Check Started"

								UserTrackCheck% = 0
								UserTrackCheck2% = 0

								Dir=ReadDir("SFX\Radio\UserTracks\")
								Repeat
									file$=NextFile(Dir)
									If file$="" Then Exit
									If FileType("SFX\Radio\UserTracks\"+file$) = 1 Then
										UserTrackCheck = UserTrackCheck + 1
										test = LoadSound("SFX\Radio\UserTracks\"+file$)
										If test<>0
											UserTrackCheck2 = UserTrackCheck2 + 1
										EndIf
										FreeSound test
									EndIf
								Forever
								CloseDir Dir

								DebugLog "User Tracks Check Ended"
							EndIf
							If MouseOn(x+20*MenuScale,y+30*MenuScale,190*MenuScale,25*MenuScale)
								DrawOptionsTooltip(tx,ty,tw,th,"usertrackscan")
							EndIf
							If UserTrackCheck%>0
								Text x + 20 * MenuScale, y + 100 * MenuScale, "自定频道已创建 （"+UserTrackCheck2+"/"+UserTrackCheck+" 个文件已加载）"
							EndIf
						Else
							UserTrackCheck%=0
						EndIf
						;[End Block]
					Case 6: ;Controls
						;[Block]
						height = 270 * MenuScale
						DrawFrame(x, y, width, height)	

						y = y + 20*MenuScale

						MouseSens = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y + 2, "鼠标灵敏度：")
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSens)
						EndIf

						y = y + 40*MenuScale

						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y + 5, "反转鼠标Y轴：")
						InvertMouse = DrawTick(x + 310 * MenuScale, y + MenuScale, InvertMouse)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
						EndIf

						y = y + 40*MenuScale

						MouseSmooth = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSmooth)*50.0)/50.0)
						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y + 1, "鼠标平滑度：")
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing",MouseSmooth)
						EndIf

						Color(255, 255, 255)

						y = y + 30*MenuScale
						Text(x + 20 * MenuScale, y, "键位设置：")
						y = y + 10*MenuScale

						Text(x + 20 * MenuScale, y + 20 * MenuScale + 2, "向前")
						InputBox(x + 160 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_UP,210)),5)		
						Text(x + 20 * MenuScale, y + 40 * MenuScale + 2, "向左")
						InputBox(x + 160 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_LEFT,210)),3)	
						Text(x + 20 * MenuScale, y + 60 * MenuScale + 2, "后退")
						InputBox(x + 160 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_DOWN,210)),6)				
						Text(x + 20 * MenuScale, y + 80 * MenuScale + 2, "向右")
						InputBox(x + 160 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_RIGHT,210)),4)	
						Text(x + 20 * MenuScale, y + 100 * MenuScale + 2, "快速保存")
						InputBox(x + 160 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SAVE,210)),11)

						Text(x + 280 * MenuScale, y + 20 * MenuScale + 2, "手动眨眼")
						InputBox(x + 470 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_BLINK,210)),7)				
						Text(x + 280 * MenuScale, y + 40 * MenuScale + 2, "奔跑")
						InputBox(x + 470 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SPRINT,210)),8)
						Text(x + 280 * MenuScale, y + 60 * MenuScale + 2, "物品栏")
						InputBox(x + 470 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_INV,210)),9)
						Text(x + 280 * MenuScale, y + 80 * MenuScale + 2, "蹲下")
						InputBox(x + 470 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CROUCH,210)),10)	
						Text(x + 280 * MenuScale, y + 100 * MenuScale + 2, "控制台")
						InputBox(x + 470 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CONSOLE,210)),12)

						If MouseOn(x+20*MenuScale,y,width-40*MenuScale,120*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"controls")
						EndIf

						For i = 0 To 227
							If KeyHit(i) Then key = i : Exit
						Next
						If key<>0 Then
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
					Case 7: ;Advanced
						;[Block]
						height = 340 * MenuScale
						DrawFrame(x, y, width, height)	

						y = y + 20*MenuScale

						Color 255,255,255				
						Text(x + 20 * MenuScale, y + 5, "显示HUD：")	
						HUDenabled = DrawTick(x + 310 * MenuScale, y + MenuScale, HUDenabled)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"hud")
						EndIf

						y=y+30*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 3, "启用控制台：")
						CanOpenConsole = DrawTick(x + 310 * MenuScale, y + MenuScale, CanOpenConsole)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
						EndIf

						y = y + 30*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 3, "出现错误时打开控制台：")
						ConsoleOpening = DrawTick(x + 310 * MenuScale, y + MenuScale, ConsoleOpening)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
						EndIf

						y = y + 50*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 3, "成就提示：")
						AchvMSGenabled% = DrawTick(x + 310 * MenuScale, y + MenuScale, AchvMSGenabled%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
						EndIf

						y = y + 50*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 2, "显示FPS：")
						ShowFPS% = DrawTick(x + 310 * MenuScale, y + MenuScale, ShowFPS%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"showfps")
						EndIf

						y = y + 30*MenuScale

						Color 255,255,255
						Text(x + 20 * MenuScale, y + 2, "帧数限制：")
						Color 255,255,255
						If DrawTick(x + 310 * MenuScale, y, CurrFrameLimit > 0.0) Then
							CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*99.0)/99.0)
							CurrFrameLimit# = Max(CurrFrameLimit, 0.01)
							Framelimit% = 19+(CurrFrameLimit*100.0)
							Color 255,255,0
							Text(x + 25 * MenuScale, y + 25 * MenuScale + 7, Framelimit%+" FPS")
						Else
							CurrFrameLimit# = 0.0
							Framelimit = 0
						EndIf
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
						EndIf
						If MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
						EndIf

						Color 255,255,255
						y = y + 70*MenuScale

						Text(x + 20 * MenuScale, y + 2, "启用字幕：")
						EnableSubtitles% = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableSubtitles%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"subtitle")
						EndIf

						Color 255,255,255
						y = y + 30*MenuScale

						Text(x + 20 * MenuScale, y + 2, "繁简转换：")
						TraditionalChinese% = DrawTick(x + 310 * MenuScale, y, TraditionalChinese%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"traditional")
						EndIf
						;[End Block]
				End Select
				;[End Block]
			Case 4 ; load map
				;[Block]
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 510 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont Font2
				Text(x + width / 2, y + height / 2 + 1, "加载地图", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 350 * MenuScale
				
				SetFont Font2
				
				tx# = x+width
				ty# = y
				tw# = 400*MenuScale
				th# = 150*MenuScale
				
				If CurrLoadGamePage < Ceil(Float(SavedMapsAmount)/6.0)-1 Then 
					If DrawButton(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage+1
					EndIf
				Else
					DrawFrame(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+555*MenuScale, y + 537.5*MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 Then
					If DrawButton(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage-1
					EndIf
				Else
					DrawFrame(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+25*MenuScale, y + 537.5*MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x+50*MenuScale,y+510*MenuScale,width-100*MenuScale,55*MenuScale)
				
				Text(x+(width/2.0),y+536*MenuScale,"第 "+Int(Max((CurrLoadGamePage+1),1))+"/"+Int(Max((Int(Ceil(Float(SavedMapsAmount)/6.0))),1)) + " 页",True,True)
				
				SetFont Font1
				
				If CurrLoadGamePage > Ceil(Float(SavedMapsAmount)/6.0)-1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				SetFont Font1
				
				If SavedMaps(0)="" Then 
					Text (x + 20 * MenuScale, y + 20 * MenuScale, "没有保存的地图。使用地图制作器创建地图。")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					For i = (1+(6*CurrLoadGamePage)) To 6+(6*CurrLoadGamePage)
						If i <= SavedMapsAmount Then
							DrawFrame(x,y,540* MenuScale, 70* MenuScale)
							
							Text(x + 20 * MenuScale, y + 10 * MenuScale, ConvertToUTF8(SavedMaps(i - 1)))
							Text(x + 20 * MenuScale, y + (10+27) * MenuScale, SavedMapsAuthor(i - 1))
							
							If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "加载", False) Then
								SelectedMap=SavedMaps(i - 1)
								MainMenuTab = 1
							EndIf
							If MouseOn(x + 400 * MenuScale, y + 20 * MenuScale, 100*MenuScale,30*MenuScale)
								DrawMapCreatorTooltip(tx,ty,tw,th,SavedMaps(i-1))
							EndIf
							
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
				EndIf
				;[End Block]
		End Select
		
	End If
	
	Color 255,255,255
	SetFont ConsoleFont
	Text 20,GraphicHeight-45,"v"+VersionNumber
	Text 20,GraphicHeight-30,SinicizationNumber
	
	SetFont Font1
	Color 255,255,0
	Text GraphicWidth-20,10,"汉化制作：子悦汉化组",2
	Text GraphicWidth-20,(10+FontHeight())+FontHeight()+1,"对外交流群：1006841985",2
	Text GraphicWidth-20,(10+FontHeight()*2)+FontHeight()+FontHeight(),"https://scpcbgame.cn/",2
	
	If Fullscreen Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	Color 255,255,255
End Function

Function UpdateLauncher()
	MenuScale = 1
	
	Graphics3DExt(LauncherWidth, LauncherHeight, 0, 2)
	
	SetBuffer BackBuffer()
	
	RealGraphicWidth = GraphicWidth
	RealGraphicHeight = GraphicHeight
	
	Font1 = LoadFont_Strict("GFX\font\Containment Breach.ttf", 16)
	SetFont Font1
	MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
	MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")	
	MaskImage MenuBlack, 255,255,0
	LauncherIMG = LoadImage_Strict("GFX\menu\launcher.jpg")
	ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
	Local i%	
	
	For i = 0 To 3
		ArrowIMG(i) = LoadImage_Strict("GFX\menu\arrow.png")
		RotateImage(ArrowIMG(i), 90 * i)
		HandleImage(ArrowIMG(i), 0, 0)
	Next
	
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
	
	If GetINIInt(OptionFile, "options", "first launch") Then ; 第一次启动
		PutINIValue(OptionFile, "options", "first launch", 0)
		If GetUserLanguage() <> "zh-CN" Then
			Color 255, 255, 255
			Repeat
				MouseHit1 = MouseHit(1)
				Text 320, 200, "遊戲檢測到您的系統語言不為簡體中文", 1, 0
				Text 320, 220, "是否需要開啟繁簡轉換？", 1, 0
				If DrawButton(200, 250, 100, 30, "是", False, False, False)
					Delay 100
					TraditionalChinese = True
					Exit
				EndIf
				If DrawButton(LauncherWidth - 300, 250, 100, 30, "否", False, False, False)
					Delay 100
					TraditionalChinese = False
					Exit
				EndIf
				Delay 8
				Flip
				Cls
			Forever
		EndIf
	EndIf
	
	BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")
	Local UpdaterStatus% = 0
	
	AppTitle "SCP - 收容失效 汉化计划 启动器"
	
	Repeat
		Color 0,0,0
		Rect 0,0,LauncherWidth,LauncherHeight,True
		
		MouseHit1 = MouseHit(1)
		
		DrawImage(LauncherIMG, 0, 0)
		
		Color 255,255,0
		Select UpdaterStatus
			Case 1
				Text(0,5,"检查更新完毕，无新更新")
			Case -1
				Text(0,5,"警告： 检查更新失败（连接超时）")
		End Select
		Color 255, 255, 255
		
		Text(20, 240 - 65 + 3, "分辨率：")
		
		Local x% = 40
		Local y% = 270 - 65
		For i = 0 To (GFXModes - 1)
			Color 0, 0, 0
			If SelectedGFXMode = i Then Rect(x - 1, y - 1, 100, 20, False)
			
			Text(x, y + 3, (GfxModeWidths(i) + "x" + GfxModeHeights(i)))
			If MouseOn(x - 1, y - 1, 100, 20) Then
				Color 100, 100, 100
				Rect(x - 1, y - 1, 100, 20, False)
				If MouseHit1 Then SelectedGFXMode = i
			EndIf
			
			y=y+20
			If y >= 250 - 65 + (LauncherHeight - 80 - 260) Then y = 270 - 65 : x=x+100
		Next
		
		;-----------------------------------------------------------------
		Color 255, 255, 255
		x = 30
		y = 369
		Rect(x - 10, y, 340, 95)
		Text(x - 10, y - 25 + 3, "显示设备：")
		
		y=y+10
		For i = 1 To CountGfxDrivers()
			Color 0, 0, 0
			If SelectedGFXDriver = i Then Rect(x - 1, y - 1, 290, 20, False)
			LimitText(ConvertToUTF8(GfxDriverName(i)), x, y + 4, 290, False)
			If MouseOn(x - 1, y - 1, 290, 20) Then
				Color 100, 100, 100
				Rect(x - 1, y - 1, 290, 20, False)
				If MouseHit1 Then SelectedGFXDriver = i
			EndIf
			
			y=y+20
		Next
		
		Fullscreen = DrawTick(40 + 430 - 15, 260 - 55 + 5 - 8, Fullscreen, BorderlessWindowed)
		BorderlessWindowed = DrawTick(40 + 430 - 15, 260 - 55 + 35, BorderlessWindowed)
		lock% = False

		If BorderlessWindowed Or (Not Fullscreen) Then lock% = True
		Bit16Mode = DrawTick(40 + 430 - 15, 260 - 55 + 65 + 8, Bit16Mode,lock%)
		LauncherEnabled = DrawTick(40 + 430 - 15, 260 - 55 + 95 + 8, LauncherEnabled)

		If BorderlessWindowed
 		   Color 255, 0, 0
 		   Fullscreen = False
		Else
  		  Color 255, 255, 255
		EndIf

		Text(40 + 430 + 15, 262 - 55 + 5 - 8 + 2, "全屏")
		Color 255, 255, 255
		Text(40 + 430 + 15, 262 - 55 + 43 - 5, "无边窗口",False,False)

		If BorderlessWindowed Or (Not Fullscreen)
 		   Color 255, 0, 0
 		   Bit16Mode = False
		Else
		    Color 255, 255, 255
		EndIf

		Text(40 + 430 + 15, 262 - 55 + 65 + 8 + 3, "16位色")
		Color 255, 255, 255
		Text(40 + 430 + 15, 262 - 55 + 95 + 8 + 2, "使用启动器")
		
		If (Not BorderlessWindowed)
			If Fullscreen
				Text(40+ 260 + 65, 262 - 55 + 140, "当前分辨率： "+(GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + "," + (16+(16*(Not Bit16Mode)))))
			Else
				Text(40+ 260 + 65, 262 - 55 + 140, "当前分辨率： "+(GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32"))
			EndIf
		Else
			Text(40+ 260 + 65, 262 - 55 + 140, "当前分辨率： "+GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32")
			If GfxModeWidths(SelectedGFXMode) < DesktopWidth() Then
				Text(40+ 260 + 65+10, 262 - 55 + 167 + 1, "（分辨率应升至")
				Text(40+ 260 + 65+10, 262 - 55 + 187 + 1, DesktopWidth() + "x" + DesktopHeight() + ",32)")
			ElseIf GfxModeWidths(SelectedGFXMode) > DesktopWidth() Then
				Text(40+ 260 + 65+10, 262 - 55 + 167 + 1, "（分辨率应降至")
				Text(40+ 260 + 65+10, 262 - 55 + 187 + 1, DesktopWidth() + "x" + DesktopHeight() + ",32)")
			EndIf
		EndIf
		
		If DrawButton(LauncherWidth - 275 + 17, LauncherHeight - 50, 100, 30, "检查更新", False, False, -1) Then
			UpdaterStatus = CheckForUpdates()
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 90, LauncherHeight - 50 - 55, 100, 30, "启动", False, False, -1) Then
			GraphicWidth = GfxModeWidths(SelectedGFXMode)
			GraphicHeight = GfxModeHeights(SelectedGFXMode)
			RealGraphicWidth = GraphicWidth
			RealGraphicHeight = GraphicHeight
			SetGfxDriver SelectedGFXDriver
			Exit
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 90, LauncherHeight - 50, 100, 30, "退出", False, False, -1) Then End
		Flip
	Forever
	
	PutINIValue(OptionFile, "options", "width", GfxModeWidths(SelectedGFXMode))
	PutINIValue(OptionFile, "options", "height", GfxModeHeights(SelectedGFXMode))
	If Fullscreen Then
		PutINIValue(OptionFile, "options", "fullscreen", "true")
	Else
		PutINIValue(OptionFile, "options", "fullscreen", "false")
	EndIf
	If LauncherEnabled Then
		PutINIValue(OptionFile, "launcher", "launcher enabled", "true")
	Else
		PutINIValue(OptionFile, "launcher", "launcher enabled", "false")
	EndIf
	If BorderlessWindowed Then
		PutINIValue(OptionFile, "options", "borderless windowed", "true")
	Else
		PutINIValue(OptionFile, "options", "borderless windowed", "false")
	EndIf
	If Bit16Mode Then
		PutINIValue(OptionFile, "options", "16bit", "true")
	Else
		PutINIValue(OptionFile, "options", "16bit", "false")
	EndIf
	PutINIValue(OptionFile, "options", "gfx driver", SelectedGFXDriver)
	
End Function

Function DrawTiledImageRect(img%, srcX%, srcY%, srcwidth#, srcheight#, x%, y%, width%, height%)
	Local x2% = x
	While x2 < x+width
		Local y2% = y
		While y2 < y+height
			If x2 + srcwidth > x + width Then srcwidth = srcwidth - Max((x2 + srcwidth) - (x + width), 1)
			If y2 + srcheight > y + height Then srcheight = srcheight - Max((y2 + srcheight) - (y + height), 1)
			DrawImageRect(img, x2, y2, srcX, srcY, srcwidth, srcheight)
			y2 = y2 + srcheight
		Wend
		x2 = x2 + srcwidth
	Wend
End Function


Type LoadingScreens
	Field imgpath$
	Field img%
	Field ID%
	Field title$
	Field alignx%, aligny%
	Field disablebackground%
	Field txt$[5], txtamount%
End Type

Function InitLoadingScreens(file$)
	Local TemporaryString$, i%
	Local ls.LoadingScreens
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			ls.LoadingScreens = New LoadingScreens
			LoadingScreenAmount=LoadingScreenAmount+1
			ls\ID = LoadingScreenAmount
			
			ls\title = TemporaryString
			ls\imgpath = GetINIString(file, TemporaryString, "image path")
			
			For i = 0 To 4
				ls\txt[i] = GetINIString(file, TemporaryString, "text"+(i+1))
				If ls\txt[i]<> "" Then ls\txtamount=ls\txtamount+1
			Next
			
			ls\disablebackground = GetINIInt(file, TemporaryString, "disablebackground")
			
			Select Lower(GetINIString(file, TemporaryString, "align x"))
				Case "left"
					ls\alignx = -1
				Case "middle", "center"
					ls\alignx = 0
				Case "right" 
					ls\alignx = 1
			End Select 
			
			Select Lower(GetINIString(file, TemporaryString, "align y"))
				Case "top", "up"
					ls\aligny = -1
				Case "middle", "center"
					ls\aligny = 0
				Case "bottom", "down"
					ls\aligny = 1
			End Select 			
			
		EndIf
	Wend
	
	CloseFile f
End Function

Function DrawLoading(percent%, shortloading=False)
	Local x%, y%
	
	If percent = 0 Then
		LoadingScreenText=0
		
		temp = Rand(1,LoadingScreenAmount)
		For ls.loadingscreens = Each LoadingScreens
			If ls\id = temp Then
				If ls\img=0 Then ls\img = LoadImage_Strict("Loadingscreens\"+ls\imgpath)
				SelectedLoadingScreen = ls 
				Exit
			EndIf
		Next
	EndIf	
	
	firstloop = True
	Repeat 
		ClsColor 0,0,0
		Cls
		
		If percent > 20 Then
			UpdateMusic()
		EndIf
		
		If shortloading = False Then
			If percent > (100.0 / SelectedLoadingScreen\txtamount)*(LoadingScreenText+1) Then
				LoadingScreenText=LoadingScreenText+1
			EndIf
		EndIf
		
		If (Not SelectedLoadingScreen\disablebackground) Then
			DrawImage LoadingBack, GraphicWidth/2 - ImageWidth(LoadingBack)/2, GraphicHeight/2 - ImageHeight(LoadingBack)/2
		EndIf	
		
		If SelectedLoadingScreen\alignx = 0 Then
			x = GraphicWidth/2 - ImageWidth(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\alignx = 1
			x = GraphicWidth - ImageWidth(SelectedLoadingScreen\img)
		Else
			x = 0
		EndIf
		
		If SelectedLoadingScreen\aligny = 0 Then
			y = GraphicHeight/2 - ImageHeight(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\aligny = 1
			y = GraphicHeight - ImageHeight(SelectedLoadingScreen\img)
		Else
			y = 0
		EndIf	
		
		DrawImage SelectedLoadingScreen\img, x, y
		
		Local width% = 300, height% = 20
		x% = GraphicWidth / 2 - width / 2
		y% = GraphicHeight / 2 + 30 - 100
		
		Rect(x, y, width+4, height, False)
		For  i% = 1 To Int((width - 2) * (percent / 100.0) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next
		
		If SelectedLoadingScreen\title = "CWM" Then
			
			If Not shortloading Then 
				If firstloop Then 
					If percent = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm1.cwm")
					ElseIf percent = 100
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm2.cwm")
					EndIf
				EndIf
			EndIf
			
			SetFont Font2
			strtemp$ = ""
			temp = Rand(2,9)
			For i = 0 To temp
				strtemp$ = STRTEMP + Chr(Rand(48,122))
			Next
			Text(GraphicWidth / 2, GraphicHeight / 2 + 80, strtemp, True, True)
			
			If percent = 0 Then 
				If Rand(5)=1 Then
					Select Rand(2)
						Case 1
							SelectedLoadingScreen\txt[0] = "它将会在 " + CurrentDate() + "发生"
						Case 2
							SelectedLoadingScreen\txt[0] = CurrentTime()
					End Select
				Else
					Select Rand(13)
						Case 1
							SelectedLoadingScreen\txt[0] = "一个超精的对讲机可能会有用"
						Case 2
							SelectedLoadingScreen\txt[0] = "這  个  方 地   將  燃   會  烧"
						Case 3
							SelectedLoadingScreen\txt[0] = "你无法控制它"
						Case 4
							SelectedLoadingScreen\txt[0] = "eof9nsd3jue4iwe1fgj"
						Case 5
							SelectedLoadingScreen\txt[0] = "你需要相信它。"
						Case 6 
							SelectedLoadingScreen\txt[0] = "当你叫我的朋友时看着他的眼睛，这不是十分绅士的举止吗?"
						Case 7
							SelectedLoadingScreen\txt[0] = "???____??_???__????n?"
						Case 8, 9
							SelectedLoadingScreen\txt[0] = "乔治一直在等你。"
						Case 10
							SelectedLoadingScreen\txt[0] = "???????????"
						Case 11
							SelectedLoadingScreen\txt[0] = "让她成为午夜剧组的一员"
						Case 12
							SelectedLoadingScreen\txt[0] = "我断定来这里是个错误，我们必须回去。"
						Case 13
							SelectedLoadingScreen\txt[0] = "他的合金包含了我生命的精华。"
					End Select
				EndIf
			EndIf
			
			strtemp$ = SelectedLoadingScreen\txt[0]
			temp = Int(Len(SelectedLoadingScreen\txt[0])-Rand(5))
			For i = 0 To Rand(10,15);temp
				strtemp$ = Replace(SelectedLoadingScreen\txt[0],Mid(SelectedLoadingScreen\txt[0],Rand(1,Len(strtemp)-1),1),Chr(Rand(130,250)))
			Next		
			SetFont Font1
			RowText(strtemp, GraphicWidth / 2-200, GraphicHeight / 2 +120,400,300,True)		
		Else
			
			Color 0,0,0
			SetFont Font2
			Text(GraphicWidth / 2 + 1, GraphicHeight / 2 + 80 + 1, SelectedLoadingScreen\title, True, True)
			SetFont Font1
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], GraphicWidth / 2-200+1, GraphicHeight / 2 +120+1,400,300,True)
			
			Color 255,255,255
			SetFont Font2
			Text(GraphicWidth / 2, GraphicHeight / 2 +80, SelectedLoadingScreen\title, True, True)
			SetFont Font1
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], GraphicWidth / 2-200, GraphicHeight / 2 +120,400,300,True)
			
		EndIf
		
		Color 0,0,0
		Text(GraphicWidth / 2 + 1, GraphicHeight / 2 - 100 + 1, "加载中 - " + percent + " %", True, True)
		Color 255,255,255
		Text(GraphicWidth / 2, GraphicHeight / 2 - 100, "加载中 - " + percent + " %", True, True)
		
		If percent = 100 Then 
			If firstloop And SelectedLoadingScreen\title <> "CWM" Then PlaySound_Strict LoadTempSound(("SFX\Horror\Horror8.ogg"))
			Text(GraphicWidth / 2, GraphicHeight - 50, "按任意键继续", True, True)
		Else
			FlushKeys()
			FlushMouse()
		EndIf
		
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
		
		Flip False
		
		firstloop = False
		If percent <> 100 Then Exit
		
	Until (GetKey()<>0 Or MouseHit(1))
End Function

Function InputBox$(x%, y%, width%, height%, Txt$, ID% = 0)
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	Color (0, 0, 0)
	
	Local MouseOnBox% = False
	If MouseOn(x, y, width, height) Then
		Color(50, 50, 50)
		MouseOnBox = True
		If MouseHit1 Then SelectedInputBox = ID : FlushKeys
	EndIf
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	Color (255, 255, 255)	
	
	If (Not MouseOnBox) And MouseHit1 And SelectedInputBox = ID Then SelectedInputBox = 0
	
	If SelectedInputBox = ID Then
		Txt = TextInput(Txt)
		If (MilliSecs() Mod 800) < 400 Then Rect (x + width / 2 + StringWidth(Txt) / 2 + 2, y + height / 2 - 5, 2, 12)
	EndIf	
	
	Text(x + width / 2, y + height / 2, Txt, True, True)
	
	Return Txt
End Function

Function DrawFrame(x%, y%, width%, height%, xoffset%=0, yoffset%=0)
	Color 255, 255, 255
	DrawTiledImageRect(MenuWhite, xoffset, (y Mod 256), 512, 512, x, y, width, height)
	
	DrawTiledImageRect(MenuBlack, yoffset, (y Mod 256), 512, 512, x+3*MenuScale, y+3*MenuScale, width-6*MenuScale, height-6*MenuScale)	
End Function

Function DrawButton%(x%, y%, width%, height%, txt$, bigfont% = True, waitForMouseUp%=False, fontOffset% = 0)
	Local clicked% = False
	
	DrawFrame (x, y, width, height)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If (MouseHit1 And (Not waitForMouseUp)) Or (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x + width / 2, y + height / 2 - 1 - fontOffset, txt, True, True)
	
	Return clicked
End Function

Function DrawButton2%(x%, y%, width%, height%, txt$, bigfont% = True)
	Local clicked% = False
	
	DrawFrame (x, y, width, height)
	Local hit% = MouseHit(1)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If hit Then clicked = True : PlaySound_Strict(ButtonSFX)
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	Text(x + width / 2, y + height / 2, txt, True, True)
	
	Return clicked
End Function

Function DrawTick%(x%, y%, selected%, locked% = False)
	Local width% = 20 * MenuScale, height% = 20 * MenuScale
	
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	;Rect(x, y, width, height)
	
	Local Highlight% = MouseOn(x, y, width, height) And (Not locked)
	
	If Highlight Then
		Color(50, 50, 50)
		If MouseHit1 Then selected = (Not selected) : PlaySound_Strict (ButtonSFX)
	Else
		Color(0, 0, 0)		
	End If
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	
	If selected Then
		If Highlight Then
			Color 255,255,255
		Else
			Color 200,200,200
		EndIf
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, width - 8, height - 8)
		;Rect(x + 4, y + 4, width - 8, height - 8)
	EndIf
	
	Color 255, 255, 255
	
	Return selected
End Function

Function SlideBar#(x%, y%, width%, value#)
	If MouseDown1 And OnSliderID=0 Then
		If ScaledMouseX() >= x And ScaledMouseX() <= x + width + 14 And ScaledMouseY() >= y And ScaledMouseY() <= y + 20 Then
			value = Min(Max((ScaledMouseX() - x) * 100 / width, 0), 100)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	DrawImage(BlinkMeterIMG, x + width * value / 100.0 +3, y+3)
	
	Color 170,170,170 
	Text (x - 50 * MenuScale, (y + 4*MenuScale)+2, "低")					
	Text (x + width + 38 * MenuScale, (y+4*MenuScale)+2, "高")	
	
	Return value
End Function

Function RowText(A$, X, Y, W, H, align% = 0, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, "")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			If align Then
				Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b)
			Else
				Text(X, LinesShown * Height + Y, b)
			EndIf			
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	If (b$ <> "") And((LinesShown + 1) <= H) Then
		If align Then
			Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		Else
			Text(X, LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		EndIf
	EndIf
End Function

Function RowText2(A$, X, Y, W, H, align% = 0, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, "")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			If align Then
				Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b)
			Else
				Text(X, LinesShown * Height + Y, b)
			EndIf
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	If (b$ <> "") And((LinesShown + 1) <= H) Then
		If align Then
			Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		Else
			Text(X, LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		EndIf
	EndIf
End Function

Function GetLineAmount(A$, W, H, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, "")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	Return LinesShown+1
End Function

Function GetLineAmount2(A$, W, H, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, "")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	Return LinesShown+1
End Function

Function LimitText%(txt$, x%, y%, width%, usingAA%=True)
	Local TextLength%
	Local UnFitting%
	Local LetterWidth%
	If usingAA Then
		If txt = "" Or width = 0 Then Return 0
		TextLength = StringWidth(txt)
		UnFitting = TextLength - width
		If UnFitting <= 0 Then ;mahtuu
			Text(x, y, txt, 0, 0)
		Else ;ei mahdu
			LetterWidth = TextLength / Len(txt)
			Text(x, y, Left(txt, Max(Len(txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
		End If
	Else
		If txt = "" Or width = 0 Then Return 0
		TextLength = StringWidth(txt)
		UnFitting = TextLength - width
		If UnFitting <= 0 Then ;mahtuu
			Text(x, y, txt, 0, 0)
		Else ;ei mahdu
			LetterWidth = TextLength / Len(txt)
			
			Text(x, y, Left(txt, Max(Len(txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
		End If
	EndIf
End Function

Function DrawTooltip(message$)
	Local scale# = GraphicHeight/768.0
	
	Local width = (StringWidth(message$))+20*MenuScale
	
	Color 25,25,25
	Rect(ScaledMouseX()+20,ScaledMouseY(),width,19*scale,True)
	Color 150,150,150
	Rect(ScaledMouseX()+20,ScaledMouseY(),width,19*scale,False)
	SetFont Font1
	Text(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(12*MenuScale), message$, True, True)
End Function

Global QuickLoadPercent% = -1
Global QuickLoadPercent_DisplayTimer# = 0
Global QuickLoad_CurrEvent.Events

Function DrawQuickLoading()
	If QuickLoadPercent > -1
		MidHandle QuickLoadIcon
		DrawImage QuickLoadIcon,GraphicWidth-90,GraphicHeight-150
		Color 255,255,255
		SetFont Font1
		Text GraphicWidth-100,GraphicHeight-90,"加载中： "+QuickLoadPercent+"%",1
		If QuickLoadPercent > 99
			If QuickLoadPercent_DisplayTimer < 70
				QuickLoadPercent_DisplayTimer# = Min(QuickLoadPercent_DisplayTimer+FPSfactor,70)
			Else
				QuickLoadPercent = -1
			EndIf
		EndIf
		QuickLoadEvents()
	Else
		QuickLoadPercent = -1
		QuickLoadPercent_DisplayTimer# = 0
		QuickLoad_CurrEvent = Null
	EndIf
End Function

Function DrawOptionsTooltip(x%,y%,width%,height%,option$,value#=0,ingame%=False)
	Local fx# = x+6*MenuScale
	Local fy# = y+6*MenuScale
	Local fw# = width-12*MenuScale
	Local fh# = height-12*MenuScale
	Local lines% = 0, lines2% = 0
	Local txt$ = ""
	Local txt2$ = "", R% = 0, G% = 0, B% = 0
	Local usetestimg% = False, extraspace% = 0
	
	SetFont Font1
	Color 255,255,255
	Select Lower(option$)
		;画面设置
			;[Block]
		Case "bump"
			txt = "“凹凸贴图”意为通过高度图为贴图模拟凹凸效果。"
			txt2 = "该选项无法在游戏中修改。"
			R = 255
		Case "vsync"
			txt = "“垂直同步”意为在计算下一帧前，等待显示器完成当前刷新周期，防止出现画面撕裂等问题。"
			txt = txt + "这会强制将游戏的FPS限制在显示器刷新率之内，可能会导致画面延迟。"
		Case "antialias"
			txt = "“抗锯齿”意为在显示之前平滑渲染图像,以减少模型边缘周围的锯齿。"
			txt2 = "该选项只适用于全屏模式中。"
			R = 255
		Case "roomlights"
			txt = "在特定光源上生成人造的光斑效果。"
		Case "gamma"
			txt = "“伽马值矫正”用于在游戏显示过暗或过亮时矫正至适合的亮度，以便与显示器伽马值平衡。"
			txt = txt + "将伽马值设置的过高或过低可能会丢失画面细节。"
			R = 255
			G = 255
			B = 255
			txt2 = "当前值: "+Int(value*100)+"% （默认100%）"
		Case "texquality"
			txt = "“纹理细节层次”会影响纹理的细节程度。如果纹理闪烁或过于模糊，请更改此选项。"
		Case "particleamount"
			txt = "设置每刻度允许渲染的粒子数量。"
			Select value
				Case 0
					R = 255
					txt2 = "只渲染烟雾粒子"
				Case 1
					R = 255
					G = 255
					txt2 = "每刻度仅渲染少数粒子"
				Case 2
					G = 255
					txt2 = "渲染所有粒子"
			End Select
		Case "vram"
			txt = "将纹理储存在Video-RAM（影像随机接达记忆器）中会让纹理的加载速度更快，但也会对纹理质量造成负面影响。"
			txt2 = "该选项无法在游戏中修改。"
			R = 255
			;[End Block]
		;音频设置
			;[Block]
		Case "musicvol"
			txt = "调整背景音乐的音量，将控制杆完全划向左侧禁用所有背景音乐。"
			R = 255
			G = 255
			B = 255
			txt2 = "当前值： "+Int(value*100)+"% （默认50%）"
		Case "soundvol"
			txt = "调整音效音量，将控制杆完全划向左侧禁用所有声音。"
			R = 255
			G = 255
			B = 255
			txt2 = "当前值： "+Int(value*100)+"% （默认100%）"
		Case "sfxautorelease"
			txt = "“自动释放音频”将会自动从内存中释放五秒后未使用的声音，以防止内存分配问题。"
			R = 255
			txt2 = "该选项无法在游戏中修改。"
		Case "usertrack"
			txt = "启用在对讲机一频道播放自定曲目的功能，这些曲目从“SFX\Radio\UserTracks\”目录中加载。"
			txt = txt + "使用对讲机时按1以播放自定曲目。"
			R = 255
			txt2 = "该选项无法在游戏中修改。"
		Case "usertrackmode"
			txt = "设置自定频道中曲目的播放模式。“重复”将会按文件名首字母顺序播放，“随机”将会随机选择下一首曲目。"
			R = 255
			G = 255
			txt2 = "请注意，随机模式不会阻止播放已播放过的曲目。"
		Case "usertrackscan"
			txt = "重新检查路径内是否有删除或添加的音乐文件。"
			;[End Block]
		;控制设置
			;[Block]
		Case "mousesensitivity"
			txt = "设置鼠标指针的速度。"
			R = 255
			G = 255
			B = 255
			txt2 = "当前值： "+Int((0.5+value)*100)+"% （默认50%）"
		Case "mouseinvert"
			txt = "反转鼠标Y轴，无需更多解释。"
		Case "mousesmoothing"
			txt = "调整鼠标指针移动的平滑度。"
			R = 255
			G = 255
			B = 255
			txt2 = "当前值： "+Int(value*100)+"% （默认100%）"
		Case "controls"
			txt = "设置游戏中的键位。"
			;[End Block]
		;高级设置
			;[Block]
		Case "hud"
			txt = "显示眨眼条和耐力条。"
		Case "consoleenable"
			txt = "允许打开控制台，启用后按 " + KeyName(KEY_CONSOLE) + " 显示控制台。"
		Case "consoleerror"
			txt = "在出现错误时弹出控制台并显示错误。"
		Case "achpopup"
			txt = "在获得成就时弹出气泡提示。"
		Case "showfps"
			txt = "在左上角显示每秒的帧数。"
		Case "framelimit"
			txt = "将游戏帧数最高限制为所设的值。"
			If value > 0 And value < 60
				R = 255
				G = 255
				txt2 = "通常推荐60FPS及更高。如果你注意到设置后出现过度卡顿，请尝试降低数值使帧速率更加一致。"
			EndIf
		Case "subtitle"
			txt = "在游戏中显示人物说话的内容，字幕的位置与游戏原本的提示的位置不同。"
		Case "traditional"
			txt = "将游戏显示文本转换为繁体，同时转换为台湾地区用语。启用后会自动读取繁体版贴图。"
			R = 255
			G = 255
			txt2 = "该选项为实验功能，基于OpenCC制作。"
			;[End Block]
	End Select
	
	lines% = GetLineAmount(txt,fw,fh)
	If usetestimg
		extraspace = 210*MenuScale
	EndIf
	If txt2$ = ""
		DrawFrame(x,y,width,((StringHeight(txt)*lines)+(10+lines)*MenuScale)+extraspace)
	Else
		lines2% = GetLineAmount(txt2,fw,fh)
		DrawFrame(x,y,width,(((StringHeight(txt)*lines)+(10+lines)*MenuScale)+(StringHeight(txt2)*lines2)+(10+lines2)*MenuScale)+extraspace)
	EndIf
	RowText(txt,fx,fy,fw,fh)
	If txt2$ <> ""
		Color R,G,B
		RowText(txt2,fx,(fy+(StringHeight(txt)*lines)+(5+lines)*MenuScale),fw,fh)
	EndIf
	If usetestimg
		MidHandle Menu_TestIMG
		If txt2$ = ""
			DrawImage Menu_TestIMG,x+(width/2),y+100*MenuScale+((StringHeight(txt)*lines)+(10+lines)*MenuScale)
		Else
			DrawImage Menu_TestIMG,x+(width/2),y+100*MenuScale+(((StringHeight(txt)*lines)+(10+lines)*MenuScale)+(StringHeight(txt2)*lines2)+(10+lines2)*MenuScale)
		EndIf
	EndIf
End Function

Function DrawMapCreatorTooltip(x%,y%,width%,height%,mapname$)
	Local fx# = x+6*MenuScale
	Local fy# = y+6*MenuScale
	Local fw# = width-12*MenuScale
	Local fh# = height-12*MenuScale
	Local lines% = 0
	
	SetFont Font1
	Color 255,255,255
	
	Local txt$[5]
	If Right(mapname,6)="cbmap2" Then
		txt[0] = Left(ConvertToUTF8(mapname$),Len(ConvertToUTF8(mapname$))-7)
		Local f% = OpenFile("Map Creator\Maps\"+mapname$)
		
		Local author$ = ConvertToUTF8(ReadLine(f))
		Local descr$ = ConvertToUTF8(ReadLine(f))
		ReadByte(f)
		ReadByte(f)
		Local ramount% = ReadInt(f)
		If ReadInt(f) > 0 Then
			Local hasForest% = True
		Else
			hasForest% = False
		EndIf
		If ReadInt(f) > 0 Then
			Local hasMT% = True
		Else
			hasMT% = False
		EndIf
		
		CloseFile f%
	Else
		txt[0] = Left(mapname$,Len(mapname$)-6)
		author$ = "[未知]"
		descr$ = "[无描述]"
		ramount% = 0
		hasForest% = False
		hasMT% = False
	EndIf
	txt[1] = "作者： "+author
	txt[2] = "描述： "+descr
	If ramount > 0 Then
		txt[3] = "房间数量： "+ramount
	Else
		txt[3] = "房间数量： [未知]"
	EndIf
	If hasForest Then
		txt[4] = "自定森林： 是"
	Else
		txt[4] = "自定森林： 否"
	EndIf
	If hasMT Then
		txt[5] = "自定维修通道： 是"
	Else
		txt[5] = "自定维修通道： 否"
	EndIf
	
	lines% = GetLineAmount(txt[2],fw,fh)
	DrawFrame(x,y,width,(StringHeight(txt[0])*6)+StringHeight(txt[2])*lines+5*MenuScale)
	
	Color 255,255,255
	Text(fx,fy,txt[0])
	Text(fx,fy+StringHeight(txt[0]),txt[1])
	RowText(txt[2],fx,fy+(StringHeight(txt[0])*2),fw,fh)
	Text(fx,fy+((StringHeight(txt[0])*2)+StringHeight(txt[2])*lines+5*MenuScale),txt[3])
	Text(fx,fy+((StringHeight(txt[0])*3)+StringHeight(txt[2])*lines+5*MenuScale),txt[4])
	Text(fx,fy+((StringHeight(txt[0])*4)+StringHeight(txt[2])*lines+5*MenuScale),txt[5])
End Function

Function ChangeMenu_TestIMG(change$)
	If Menu_TestIMG <> 0 Then FreeImage Menu_TestIMG
	AmbientLightRoomTex% = CreateTexture(2,2,257)
	TextureBlend AmbientLightRoomTex,5
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	Menu_TestIMG = Create3DIcon(200,200,"GFX\map\room3z3_opt.rmesh",0,-0.75,1,0,0,0,menuroomscale#,menuroomscale#,menuroomscale#,True)
	ScaleImage Menu_TestIMG,MenuScale,MenuScale
	MaskImage Menu_TestIMG,255,0,255
	FreeTexture AmbientLightRoomTex : AmbientLightRoomTex = 0
	
	CurrMenu_TestIMG = change$
End Function

Global OnSliderID% = 0

Function Slider3(x%,y%,width%,value%,ID%,val1$,val2$,val3$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True)
	Rect(x+(width/2)+5,y-8,4,14,True)
	Rect(x+width+10,y-8,4,14,True)
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width)
			value = 2
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale + 5,val1,True)
	ElseIf value = 1
		Text(x+(width/2)+7,y+10+MenuScale + 5,val2,True)
	Else
		Text(x+width+12,y+10+MenuScale + 5,val3,True)
	EndIf
	
	Return value
End Function

Function Slider4(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width*(1.0/3.0))+(10.0/3.0),y-8,4,14,True) ;2
	Rect(x+(width*(2.0/3.0))+(20.0/3.0),y-8,4,14,True) ;3
	Rect(x+width+10,y-8,4,14,True) ;4
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width*(1.0/3.0)) And (ScaledMouseX() <= x+width*(1.0/3.0)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width*(2.0/3.0)) And (ScaledMouseX() <= x+width*(2.0/3.0)+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+width)
			value = 3
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+width*(1.0/3.0)+2,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+width*(2.0/3.0)+4,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		Text(x+width*(1.0/3.0)+2+(10.0/3.0),y+10+MenuScale,val2,True)
	ElseIf value = 2
		Text(x+width*(2.0/3.0)+2+((10.0/3.0)*2),y+10+MenuScale,val3,True)
	Else
		Text(x+width+12,y+10+MenuScale,val4,True)
	EndIf
	
	Return value
End Function

Function Slider5(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$,val5$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width/4)+2.5,y-8,4,14,True) ;2
	Rect(x+(width/2)+5,y-8,4,14,True) ;3
	Rect(x+(width*0.75)+7.5,y-8,4,14,True) ;4
	Rect(x+width+10,y-8,4,14,True) ;5
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/4) And (ScaledMouseX() <= x+(width/4)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+width*0.75) And (ScaledMouseX() <= x+(width*0.75)+8)
			value = 3
		ElseIf (ScaledMouseX() >= x+width)
			value = 4
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/4)+1.5,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	ElseIf value = 3
		DrawImage(BlinkMeterIMG,x+(width*0.75)+4.5,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale + 5,val1,True)
	ElseIf value = 1
		Text(x+(width/4)+4.5,y+10+MenuScale + 5,val2,True)
	ElseIf value = 2
		Text(x+(width/2)+7,y+10+MenuScale + 5,val3,True)
	ElseIf value = 3
		Text(x+(width*0.75)+9.5,y+10+MenuScale + 5,val4,True)
	Else
		Text(x+width+12,y+10+MenuScale + 5,val5,True)
	EndIf
	
	Return value
End Function

Function Slider7(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$,val5$,val6$,val7$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width*(1.0/6.0))+(10.0/6.0),y-8,4,14,True) ;2
	Rect(x+(width*(2.0/6.0))+(20.0/6.0),y-8,4,14,True) ;3
	Rect(x+(width*(3.0/6.0))+(30.0/6.0),y-8,4,14,True) ;4
	Rect(x+(width*(4.0/6.0))+(40.0/6.0),y-8,4,14,True) ;5
	Rect(x+(width*(5.0/6.0))+(50.0/6.0),y-8,4,14,True) ;6
	Rect(x+width+10,y-8,4,14,True) ;7
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+(width*(1.0/6.0))) And (ScaledMouseX() <= x+(width*(1.0/6.0))+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+(width*(2.0/6.0))) And (ScaledMouseX() <= x+(width*(2.0/6.0))+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+(width*(3.0/6.0))) And (ScaledMouseX() <= x+(width*(3.0/6.0))+8)
			value = 3
		ElseIf (ScaledMouseX() >= x+(width*(4.0/6.0))) And (ScaledMouseX() <= x+(width*(4.0/6.0))+8)
			value = 4
		ElseIf (ScaledMouseX() >= x+(width*(5.0/6.0))) And (ScaledMouseX() <= x+(width*(5.0/6.0))+8)
			value = 5
		ElseIf (ScaledMouseX() >= x+width)
			value = 6
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width*(1.0/6.0))+1,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+(width*(2.0/6.0))+2,y-8)
	ElseIf value = 3
		DrawImage(BlinkMeterIMG,x+(width*(3.0/6.0))+3,y-8)
	ElseIf value = 4
		DrawImage(BlinkMeterIMG,x+(width*(4.0/6.0))+4,y-8)
	ElseIf value = 5
		DrawImage(BlinkMeterIMG,x+(width*(5.0/6.0))+5,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		Text(x+(width*(1.0/6.0))+2+(10.0/6.0),y+10+MenuScale,val2,True)
	ElseIf value = 2
		Text(x+(width*(2.0/6.0))+2+((10.0/6.0)*2),y+10+MenuScale,val3,True)
	ElseIf value = 3
		Text(x+(width*(3.0/6.0))+2+((10.0/6.0)*3),y+10+MenuScale,val4,True)
	ElseIf value = 4
		Text(x+(width*(4.0/6.0))+2+((10.0/6.0)*4),y+10+MenuScale,val5,True)
	ElseIf value = 5
		Text(x+(width*(5.0/6.0))+2+((10.0/6.0)*5),y+10+MenuScale,val6,True)
	Else
		Text(x+width+12,y+10+MenuScale,val7,True)
	EndIf
	
	Return value
End Function

Global OnBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function DrawScrollBar#(x, y, width, height, barx, bary, barwidth, barheight, bar#, dir = 0)
	;0 = vaakasuuntainen, 1 = pystysuuntainen
	Local MouseSpeedX = MouseXSpeed()
	Local MouseSpeedY = MouseYSpeed()
	
	Color(0, 0, 0)
	Button(barx, bary, barwidth, barheight, "")
	
	If dir = 0 Then ;vaakasuunnassa
		If height > 10 Then
			Color 250,250,250
			Rect(barx + barwidth / 2, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 - 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 + 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
		EndIf
	Else ;pystysuunnassa
		If width > 10 Then
			Color 250,250,250
			Rect(barx + 4*MenuScale, bary + barheight / 2, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 - 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 + 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
		EndIf
	EndIf
	
	If MouseX()>barx And MouseX()<barx+barwidth
		If MouseY()>bary And MouseY()<bary+barheight
			OnBar = True
		Else
			If (Not MouseDown1)
				OnBar = False
			EndIf
		EndIf
	Else
		If (Not MouseDown1)
			OnBar = False
		EndIf
	EndIf
	
	If MouseDown1
		If OnBar
			If dir = 0
				Return Min(Max(bar + MouseSpeedX / Float(width - barwidth), 0), 1)
			Else
				Return Min(Max(bar + MouseSpeedY / Float(height - barheight), 0), 1)
			EndIf
		EndIf
	EndIf
	
	Local MouseSpeedZ = MouseZSpeed()
			
	If MouseSpeedZ<>0 Then ;Only for vertical scroll bars
		Return Min(Max(bar - (MouseSpeedZ*3) / Float(height - barheight), 0), 1)
    EndIf
	
	Return bar
End Function

Function Button%(x,y,width,height,txt$, disabled%=False)
	Local Pushed = False
	
	Color 50, 50, 50
	If Not disabled Then 
		If MouseX() > x And MouseX() < x+width Then
			If MouseY() > y And MouseY() < y+height Then
				If MouseDown1 Then
					Pushed = True
					Color 50*0.6, 50*0.6, 50*0.6
				Else
					Color Min(50*1.2,255),Min(50*1.2,255),Min(50*1.2,255)
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect x,y,width,height
		Color 133,130,125
		Rect x+1*MenuScale,y+1*MenuScale,width-1*MenuScale,height-1*MenuScale,False	
		Color 10,10,10
		Rect x,y,width,height,False
		Color 250,250,250
		Line x,y+height-1*MenuScale,x+width-1*MenuScale,y+height-1*MenuScale
		Line x+width-1*MenuScale,y,x+width-1*MenuScale,y+height-1*MenuScale
	Else
		Rect x,y,width,height
		Color 133,130,125
		Rect x,y,width-1*MenuScale,height-1*MenuScale,False	
		Color 250,250,250
		Rect x,y,width,height,False
		Color 10,10,10
		Line x,y+height-1,x+width-1,y+height-1
		Line x+width-1,y,x+width-1,y+height-1		
	EndIf
	
	Color 255,255,255
	If disabled Then Color 70,70,70
	Text x+width/2, y+height/2-1*MenuScale, txt, True, True
	
	Color 0,0,0
	
	If Pushed And MouseHit1 Then PlaySound_Strict ButtonSFX : Return True
End Function