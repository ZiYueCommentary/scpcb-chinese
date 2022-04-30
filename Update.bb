; 全新更新检查系统
; 事实上，用DownloadFile函数也可以搞自动更新系统
; 但是ModDB的下载链接据我所知会变动，而且也不好做下载量统计
; 所以这玩意怎么用就看各位怎么玩了
; ——子悦 2022/4/28

; DownloadFile函数是Blitz3D TSS（子悦版）的独占内容
; 允许下载网络文件，理论上什么都可以，只要浏览器能下这玩意就能下
; 优点就是比用Blitz3D写下载函数稳定快捷的多，缺点暂时不知道
Global UpdateCheckEnabled% = GetINIInt(OptionFile, "options", "check for updates")
Global UpdaterBG

Type ChangeLogLines
	Field txt$
End Type

Global UpdaterIMG
Global LinesAmount% = 0

Function CheckForUpdates()
	AppTitle "SCP - 收容失效汉化版 更新检查器"
	
	If Not UpdateCheckEnabled Then Return
	
	SetBuffer BackBuffer()
	Cls
	Color 255,255,255
	Text 320,240,"正在检查更新...",True,True
	Text 320,240+20,"（这东西真的有用，相信子悦，别禁用）",True,True ;我就是担心有人觉得这玩意没用给禁用了，真那样那我写更新检查有歌姬用
	Flip
	
	; 获取域名TXT回答（version.scpcbgame.cn），并用ParseDomainTXT解析其中内容
	; 与原版游戏的更新检查不同，原版游戏的更新检查靠读取官网更新日志中第一行的版本号来确定
	; 因此无论有没有更新，更新日志一定会下载
	; 但获取TXT问答则不同，TXT回答只是字符串，非常节省资源。检测到新版本后再下载更新日志，为玩家列举新版本的更新内容
	; 查看Blitz IDE内关于ParseDomainTXT和GetDomainTXT的帮助即可了解详细内容
	; version.scpcbgame.cn是收失中文网站用于回复最新版本信息的域名，如果你有域名也可以这么搞
	; 至于为什么要在这里大费周章呢？是因为这俩函数是我写的，我骄傲（bushi
	; ——子悦 2022/4/29
	Local domainTXT$ = ParseDomainTXT(GetDomainTXT("version.scpcbgame.cn"), "version")

	If domainTXT <> SinicizationNumber And domainTXT<>"" Then ;检测到新版本（TXT回答与汉化版本号不符）
		DebugLog "Newer version!"
		DownloadFile("https://scpcbgame.cn/changelog.txt", "changelog_website.txt") ;下载文件，命名为changelog_website.txt
		;Local ChangeLogFile% = ReadFile(ConvertToANSI("汉化更新日志.txt"))
		Local ChangeLogFile% = ReadFile("changelog_website.txt") ;读取文件
		
		UpdaterBG = LoadImage_Strict("GFX\menu\updater.jpg")
		UpdaterIMG = CreateImage(452,254)
		
		Local ChangeLogLineAmount% = 0
		Local firstLine% = 1
		While Not Eof(ChangeLogFile)
			l$ = ReadLine(ChangeLogFile)
			If l <> ""
				chl.ChangeLogLines = New ChangeLogLines
				If firstLine
					chl\txt$ = "新更新："+l
					firstLine = 0
				Else
					chl\txt$ = l
				EndIf
				ChangeLogLineAmount = ChangeLogLineAmount + 1
			Else
				Exit
			EndIf
		Wend
		CloseFile(ChangeLogFile) ;关闭读取
		DeleteFile("changelog_website.txt") ;删除文件
		
		UpdaterFont = LoadFont("GFX\font\Containment Breach.ttf",16)
		
		Repeat
			SetBuffer BackBuffer()
			Cls
			Color 255,255,255
			MouseHit1 = MouseHit(1)
			MouseDown1 = MouseDown(1)
			DrawImage UpdaterBG,0,0
			
			SetFont UpdaterFont
			If LinesAmount > 13
				y# = 200-(20*ScrollMenuHeight*ScrollBarY)
				LinesAmount%=0
				SetBuffer(ImageBuffer(UpdaterIMG))
				DrawImage UpdaterBG,-20,-195
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,3) = "新更新" Then Color 200,0,0
					RowText2(chl\txt$,2,y#-195,430,254)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				SetBuffer BackBuffer()
				DrawImage UpdaterIMG,20,195
				Color 10,10,10
				Rect 452,195,20,254,True
				ScrollMenuHeight# = LinesAmount-13
				ScrollBarY = DrawScrollBar(452,195,20,254,452,195+(254-(254-4*ScrollMenuHeight))*ScrollBarY,20,254-(4*ScrollMenuHeight),ScrollBarY,1)
			Else
				y# = 201
				LinesAmount%=0
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,3) = "新更新" Then Color 200,0,0
					RowText2(chl\txt$,21,y#,431,253)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				ScrollMenuHeight# = LinesAmount
			EndIf
			Color 255,255,255
			Rect(480, 200, 140, 95)
			Color 0,0,0
			RowText2("当前版本："+SinicizationNumber,482,210,137,90)
			
			SetFont Font1
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 100, 100, 30, "重试", False, False, False)
				Delete Each ChangeLogLines
				If UpdaterIMG <> 0 Then FreeImage UpdaterIMG
				Return True
			EndIf
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 50, 100, 30, "下载", False, False, False)
				ExecFile("https://scpcbgame.cn/#download")
				Delay 100
				End
			EndIf
			If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65, 100, 30, "忽略", False, False, False)
				Delay 100
				Exit
			EndIf
			
			Flip
			Delay 8
		Forever
	Else 
		DebugLog "No newer version!"
	EndIf
	Delete Each ChangeLogLines
	If UpdaterIMG <> 0 Then FreeImage UpdaterIMG
	Return False
End Function