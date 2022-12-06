; 全新更新检查系统
; 事实上，用DownloadFile函数也可以搞自动更新系统
; 但是ModDB的下载链接据我所知会变动，而且也不好做下载量统计
; 所以这玩意怎么用就看各位怎么玩了
; ——子悦 2022/4/28

; DownloadFile函数是Blitz3D TSS（子悦版）的独占内容
; 允许下载网络文件，理论上什么都可以，只要浏览器能下这玩意就能下
; 优点就是比用Blitz3D写下载函数稳定快捷的多，缺点暂时不知道
; 为了测试，该部分使用了现代逻辑运算符

; 捋一下更新检查器的流程：
; 原版： 下载更新日志->检查第一行文本->如果与版本号不同->弹更新检查界面
; 汉化版： 获取域名TXT回答->如果与汉化版本号不同->下载更新日志->弹更新检查界面
; 所以汉化版的更新检查很省性能，把这事当个热知识就行
Global UpdateCheckEnabled% = GetINIInt(OptionFile, "options", "check for updates")
Global UpdaterBG

Type ChangeLogLines
	Field txt$
End Type

Global UpdaterIMG
Global LinesAmount% = 0

Function CheckForUpdates%()
	AppTitle "SCP - 收容失效汉化版 更新检查器"
	
	If !UpdateCheckEnabled Then Return 0
	
	SetBuffer BackBuffer()
	Cls
	Color 255,255,255
	Text 320,240,"正在检查更新...",True,True
	;Text 320,240+20,"（这东西真的有用，相信子悦，别禁用）",True,True ; 我就是担心有人觉得这玩意没用给禁用了，真那样那我写更新检查有歌姬用
	Flip
	
	; 获取域名TXT回答（version.scpcbgame.cn），并用ParseDomainTXT解析其中内容
	; 与原版游戏的更新检查不同，原版游戏的更新检查靠读取官网更新日志中第一行的版本号来确定
	; 因此无论有没有更新，更新日志一定会下载
	; 但获取TXT问答则不同，TXT回答只是字符串，非常节省资源。检测到新版本后再下载更新日志，为玩家列举新版本的更新内容
	; 查看Blitz IDE内关于ParseDomainTXT和GetDomainTXT的帮助即可了解详细内容
	; version.scpcbgame.cn是收失中文网站用于回复最新版本信息的域名，如果你有域名也可以这么搞
	; 至于为什么要在这里大费周章呢？是因为这俩函数是我写的，我骄傲（bushi
	; ——子悦 2022/4/29
	Local txt$ = GetDomainTXT("version.scpcbgame.cn")
	Local version$ = ParseDomainTXT(txt, "version")
	Local date$ = ParseDomainTXT(txt, "date")
	
	; 从第二版开始更新就分为两种——一为补丁更新，二为正式更新
	; 补丁更新一般是那些对于游戏无太大影响的改动，比如补充原版更新日志汉化啥的
	; 正式更新就是改了源码需要重新编译的那种，你总不能指望游戏自己拿新exe替换自己去吧（虽然原版可以）
	; 我寻思可能第二版之后就没啥大更新了，让玩家手动下估计大部分也是懒得下，所以干脆让游戏能自己下
	; 当然这玩意可能也不会用到，也算是给其他模组开发者留个参考啥的
	; 下面这个就是判断新更新是正式更新还是补丁更新，是补丁更新就直接帮玩家下了
	; 我靠我啥时候写那么多了这机制我还一点没做呢
	;
	; 哦对了，在告诉玩家可以自动更新前，还会检查游戏版本是否可以自动更新
	; 如果txt回答中的compatible与游戏版本号不符（不支持当前版本自动更新），则直接当做正式更新
	; ——子悦 2022/7/31
	Local isUpdate% = Int(ParseDomainTXT(txt, "update")) & (ParseDomainTXT(txt, "compatible") = SinicizationNumber) ; 如果 新版本支持自动更新 且 允许当前版本自动更新
	
	DebugLog txt
	If version = "" Then 
		DebugLog "Get TXT failed!"
		Return -1
	EndIf

	If version != SinicizationNumber Then ; 检测到新版本（TXT回答与汉化版本号不符）
		DebugLog "Newer version!"
		DownloadFile("https://scpcbgame.cn/changelog.txt", "changelog_website.txt") ; 下载文件，命名为changelog_website.txt
		;Local ChangeLogFile% = ReadFile(ConvertToANSI("汉化更新日志.txt"))
		Local ChangeLogFile% = ReadFile("changelog_website.txt") ; 读取文件
		
		UpdaterBG = LoadImage_Strict("GFX\menu\updater.jpg")
		UpdaterIMG = CreateImage(452,254)
		
		Local ChangeLogLineAmount% = 0
		Local FirstLine% = True
		If ChangeLogFile != 0 Then ; 如果读取成功
			While Not Eof(ChangeLogFile)
				l$ = ReadLine(ChangeLogFile)
				If l != ""
					chl.ChangeLogLines = New ChangeLogLines
					If FirstLine Then
						chl\txt$ = "新更新： " + l
						FirstLine = False
					Else
						chl\txt$ = l
					EndIf
					ChangeLogLineAmount = ChangeLogLineAmount + 1
				Else
					Exit
				EndIf
			Wend
			CloseFile(ChangeLogFile) ; 关闭读取
			DeleteFile("changelog_website.txt") ; 删除文件
		Else ; 如果读取失败
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "更新日志下载失败。"
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "最新版本： " + version
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "更新时间： " + date
			chl.ChangeLogLines = New ChangeLogLines
			chl\txt$ = "更新日志链接： https://scpcbgame.cn/changelog.txt"
		EndIf
		UpdaterFont = LoadFont_Strict("GFX\font\Containment Breach.ttf",16)
		
		RightTopDisplay$ = "当前版本： " + SinicizationNumber
		If isUpdate Then RightTopDisplay = "该更新支持自动更新！"
		DownloadCompleted% = False
		
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
				LinesAmount% = 0
				SetBuffer(ImageBuffer(UpdaterIMG))
				DrawImage UpdaterBG,-20,-195
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,3) = "新更新" Then Color 200,0,0
					If chl\txt$ = "更新日志下载失败。" Then Color 255,0,0
					RowText2(chl\txt$,2,y#-195,430,254)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				SetBuffer BackBuffer()
				DrawImage UpdaterIMG,20,195
				Color 10,10,10
				Rect 452,195,20,254,True
				ScrollMenuHeight# = LinesAmount-12.3
				ScrollBarY = DrawScrollBar(452,195,20,254,452,195+(254-(254-4*ScrollMenuHeight))*ScrollBarY,20,254-(4*ScrollMenuHeight),ScrollBarY,1)
			Else
				y# = 201
				LinesAmount% = 0
				For chl.ChangeLogLines = Each ChangeLogLines
					Color 1,0,0
					If Left(chl\txt$,3) = "新更新" Then Color 200,0,0
					If chl\txt$ = "更新日志下载失败。" Then Color 255,0,0
					RowText2(chl\txt$,21,y#,431,253)
					y# = y#+(20*GetLineAmount2(chl\txt$,432,254))
					LinesAmount = LinesAmount + (GetLineAmount2(chl\txt$,432,254))
				Next
				ScrollMenuHeight# = LinesAmount
			EndIf
			Color 255,255,255
			Rect 480, 200, 140, 95
			Color 0,0,0
			
			RowText2(RightTopDisplay,482,210,137,90)
			
			If !DownloadCompleted Then
				If isUpdate Then 
					If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 100, 100, 30, "自动更新", False, False, False)
						; 汉化版的更新系统和原版的更新系统有本质上的区别
						; 原版的更新系统想让你不需要自己去网站下载，只需要一键式下载，然后等着更新完毕
						; （我不太确定原版自动更新的稳定程度，因为我现在让它检查更新它查不出来）
						; 而汉化版的更新检查实质上是提示你有新版本，应该去网站上下载
						; 汉化版的自动更新是为了那些非常小的更新而准备的（比如只有几个文件的更新，这些文件类似原版更新日志这种无关紧要的玩意），而原版大小通吃
						; 实际上自动更新系统是万恶之源Box of Horror模组搞出来的，这种没有正式网站也没上ModDB的模组搞这玩意再正常不过
						; （因为原版源码里的Update.bb有一行读取Box of Horror设置的代码...）
						; 而原版直接搬过来就...显得有点鸡肋，但总体来说非常有用
						; 原版要是没有更新系统我哪来的壳做汉化版更新检查呢
						; 其实相比于关心原版那玩意管没管用，我更担心这玩意出bug，这东西的开发难度已经简单到了令人发指的程度
						; ——子悦 2022.7.31
					
						; 解析更新文件表，具体格式可以去这个链接看
						DownloadFile "https://files.ziyuesinicization.site/scpcb/list.txt", "list.txt"
						list% = ReadFile("list.txt")
						While Not Eof(list)
							s$ = ReadLine(list)
							If InStr(s, ",") != 0 Then 
								l$ = Left(s, InStr(s, ",") - 1)
								r$ = Right(s, Len(s)-InStr(s, ",")-2)
						
								; null就是空，代表纯删文件
								DeleteFile(ConvertToANSI(l))
								If r != "null" Then 
									DownloadFile("https://files.ziyuesinicization.site/scpcb/" + r, ConvertToANSI(l))
								EndIf
							EndIf
						Wend
						CloseFile(list)
						DeleteFile "list.txt"
						RightTopDisplay = "更新完毕，建议重新启动游戏。"
						DownloadCompleted = True
					EndIf
					If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 50, 100, 30, "手动下载", False, False, False)
						ExecFile("https://scpcbgame.cn/#download")
						Delay 100
						End
					EndIf
				Else
					If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 100, 100, 30, "重试", False, False, False)
						Delete Each ChangeLogLines
						If UpdaterIMG != 0 Then FreeImage UpdaterIMG
						CheckForUpdates()
						Return 0
					EndIf
					If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 50, 100, 30, "下载", False, False, False)
						ExecFile("https://scpcbgame.cn/#download")
						Delay 100
						End
					EndIf
				EndIf
				If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65, 100, 30, "忽略", False, False, False)
					Delay 100
					Exit
				EndIf
			Else
				If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 100, 100, 30, "网站", False, False, False)
					ExecFile("https://scpcbgame.cn/")
					Delay 100
					End
				EndIf
				If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65 - 50, 100, 30, "继续", False, False, False)
					Delay 100
					Exit
				EndIf
				If DrawButton(LauncherWidth - 30 - 90 - 20, LauncherHeight - 65, 100, 30, "退出", False, False, False)
					Delay 100
					End
				EndIf
			EndIf
			
			Flip
			Delay 8
		Forever
	Else 
		DebugLog "No newer version!"
	EndIf
	Delete Each ChangeLogLines
	If UpdaterIMG != 0 Then FreeImage UpdaterIMG
	Return 0
End Function