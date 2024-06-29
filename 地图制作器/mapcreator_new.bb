; ����ANSI��ʹ��BlitzPlus����

; ���ڵ�ͼ������ʹ��BlitzPlus��д������ֻ����BlitzPlus����
; ��BlitzPlus��֧�����ģ�����������ʵ��δʹ��BlitzPlus��������Ⱦ������ϵͳ��Ⱦ
; ��ϵͳ�ı�����ANSI�����Ա����ΪANSI������ʾ������Ҫ����BlitzPlus��֧�ֵ�����
; ���ڵ�ͼ�����ƺ���������Ϸ�е���ʾ���⣬ת�����뼴�ɣ���Ϸ����ΪUTF-8��
; �������� 2022��4��29��

Include "IniControler.bb"
IniWriteBuffer("options.ini")
IniWriteBuffer("..\options.ini")
IniWriteBuffer("..\Data\rooms.ini")
IniWriteBuffer("..\Data\events.ini")

Global TraditionalChinese% = IniGetInt("..\options.ini","options","traditional chinese")
If TraditionalChinese Then Notify("��ͼ��������3D�鿴�������֧�ַ������ģ���л������⡣")

Const ResWidth% = 910
Const ResHeight% = 660
Const versionnumber# = 2.1

Loadingwindow=CreateWindow("", GraphicsWidth()/2-160,GraphicsHeight()/2-120,320,260,winhandle,8)
panelloading = CreatePanel(0,0,320,260,Loadingwindow,0)
SetPanelImage(panelloading,"Assets\map_logo.jpg")

; create a window to put the toolbar in
WinHandle=CreateWindow("SCP - ����ʧЧ ��ͼ������ v"+versionnumber,GraphicsWidth()/2-ResWidth/2, GraphicsHeight()/2-ResHeight/2,ResWidth,ResHeight,0, 13) 
Global MainHwnd = api_GetActiveWindow() ;User32.dll
HideGadget WinHandle

Global FileLocation$ = "..\Data\rooms.ini"
LoadRoomTemplates(FileLocation)

Global listbox = CreateListBox(5,60,ResWidth/4,ResHeight/2-20, winhandle)
; ein paar Eintrage hinzufugen
For rt.RoomTemplates = Each RoomTemplates
	If rt\MapGrid = 0
		AddGadgetItem listbox, rt\Name
	EndIf
Next
SetGadgetLayout listbox, 3,3,2,2

InitEvents("..\Data\events.ini")
AddEvents()
Global room_desc = CreateLabel("����������",5,40+ResHeight/2,ResWidth/4,ResHeight/11.8,WinHandle,3)
SetGadgetLayout room_desc , 3,3,2,2

Global grid_room_info = CreateLabel("",5,200+ResHeight/2,ResWidth/4,ResHeight/11.6,WinHandle,3) ;95
SetGadgetLayout grid_room_info , 3,3,2,2
Global ChangeGridGadget% = False
Global GridGadgetText$ = ""

Global event_desc = CreateLabel("",5,117+ResHeight/2,ResWidth/4,ResHeight/12.0,WinHandle,3) ;170	ResHeight/11.8
SetGadgetLayout event_desc , 3,3,2,2

Global event_prob = CreateSlider(6,185+ResHeight/2,ResWidth/4-2,ResHeight/38.0,WinHandle,1)
SetGadgetLayout event_prob , 3,3,2,2
SetSliderRange event_prob,0,100
DisableGadget event_prob

Global event_prob_label = CreateLabel("",5,170+ResHeight/2,ResWidth/4,ResHeight/38.0,WinHandle,3)
SetGadgetLayout event_prob_label , 3,3,2,2

menu=WindowMenu(WinHandle)

Global combobox = CreateComboBox(5, 95+ResHeight/2, ResWidth/4,ResHeight-ResHeight/1.39, winhandle) ;150

SetGadgetLayout combobox , 3,3,2,2
DisableGadget combobox

txtbox=CreateTextField(5,40,150,20,winhandle) ;create ;textfield in that window
SetGadgetText txtbox,"" ;set ;text in that ;textfield for info
ok=CreateButton("����",155,40,50,20,winhandle) ;create button
clean_txt=CreateButton("��",210,40,20,20,winhandle) ;create button

Global ShowGrid% = True
map_2d = CreateCanvas(300,25,551,551,WinHandle)
Dim MapIcons(5,4)
MapIcons(ROOM1, 0)=LoadImage("Assets\room1.png")
MapIcons(ROOM2, 0)=LoadImage("Assets\room2.png")
MapIcons(ROOM2C, 0)=LoadImage("Assets\room2C.png")
MapIcons(ROOM3, 0)=LoadImage("Assets\room3.png")
MapIcons(ROOM4, 0)=LoadImage("Assets\room4.png")
For i = ROOM1 To ROOM4
	MaskImage MapIcons(i,0), 255,255,255
	MidHandle(MapIcons(i,0))
	For n = 1 To 3
		MapIcons(i,n)=CopyImage(MapIcons(i,0))
		MaskImage MapIcons(i,n), 255,255,255
		RotateImage(MapIcons(i,n),90*n)
		MidHandle(MapIcons(i,n))
	Next
Next
Dim ForestIcons(5,4)
ForestIcons(ROOM1, 0)=LoadImage("Assets\forest1.png")
ForestIcons(ROOM2, 0)=LoadImage("Assets\forest2.png")
ForestIcons(ROOM2C, 0)=LoadImage("Assets\forest2C.png")
ForestIcons(ROOM3, 0)=LoadImage("Assets\forest3.png")
ForestIcons(ROOM4, 0)=LoadImage("Assets\forest4.png")
For i = ROOM1 To ROOM4
	MaskImage ForestIcons(i,0), 255,255,255
	MidHandle(ForestIcons(i,0))
	For n = 1 To 3
		ForestIcons(i,n)=CopyImage(ForestIcons(i,0))
		MaskImage ForestIcons(i,n), 255,255,255
		RotateImage(ForestIcons(i,n),90*n)
		MidHandle(ForestIcons(i,n))
	Next
Next
Global Grid_SelectedX#=-1.0, Grid_SelectedY#=-1.0
Global CurrMapGrid% = 0
Dim SpecialIcons(2,4)
SpecialIcons(1, 0) = LoadImage("Assets\forest_exit.png")
SpecialIcons(2, 0) = LoadImage("Assets\room2elev.png")
For i = 1 To 2
	MaskImage SpecialIcons(i,0), 255,255,255
	MidHandle(SpecialIcons(i,0))
	For n = 1 To 3
		SpecialIcons(i,n)=CopyImage(SpecialIcons(i,0))
		MaskImage SpecialIcons(i,n), 255,255,255
		RotateImage(SpecialIcons(i,n),90*n)
		MidHandle(SpecialIcons(i,n))
	Next
Next
;------------------------------------------------------------------------------����ʩ
Const MapWidth = 18, MapHeight = 18
Dim Map.RoomTemplates(MapWidth, MapHeight)
Dim MapAngle%(MapWidth, MapHeight)
Dim MapEvent$(MapWidth, MapHeight)
Dim MapEventProb#(MapWidth, MapHeight)

;------------------------------------------------------------------------------ɭ��
Const ForestGridSize = 9
Dim ForestPlace.RoomTemplates(ForestGridSize, ForestGridSize)
Dim ForestPlaceAngle%(ForestGridSize, ForestGridSize)

;------------------------------------------------------------------------------ά��ͨ��
Const MT_GridSize = 18
Dim MTRoom.RoomTemplates(MT_GridSize, MT_GridSize)
Dim MTRoomAngle%(MT_GridSize, MT_GridSize)

Dim Arrows(4)
Arrows(0) = LoadImage("Assets\arrows.png")
HandleImage Arrows(0),ImageWidth(Arrows(0))/2,ImageHeight(Arrows(0))/2
For i = 1 To 3
	Arrows(i)=CopyImage(Arrows(0))
	HandleImage Arrows(i), ImageWidth(Arrows(i))/2,ImageHeight(Arrows(i))/2
	RotateImage Arrows(i), i*90
Next

Global PlusIcon
PlusIcon = LoadImage("Assets\plus.png")
MaskImage PlusIcon,255,255,255
MidHandle(PlusIcon)

SetGadgetLayout txtbox , 3,3,3,3
SetGadgetLayout ok , 3,3,3,3
SetGadgetLayout clean_txt , 3,3,3,3
tab=CreateTabber(0,5,ResWidth/4+20,ResHeight-60,winhandle)

InsertGadgetItem(tab,0,"2D/��ͼ������")
InsertGadgetItem(tab,1,"3D/��ͼ�鿴��")
SetGadgetLayout tab , 3,3,2,2

tab2=CreateTabber(300,5,ResWidth/4+20,ResHeight-100,winhandle)
InsertGadgetItem(tab2,0,"��ʩ")
InsertGadgetItem(tab2,1,"ɭ��")
InsertGadgetItem(tab2,2,"ά��ͨ��")
SetGadgetLayout tab2 , 3,3,2,2

SetStatusText(Loadingwindow, "��������")
; Now create a whole bunch of menus and sub-items - first of all the FILE menu
file=CreateMenu("�ļ�(&F)",0,menu) ; main menu
CreateMenu "�½�",0,file ; child menu 
CreateMenu "��",1,file ; child menu 
CreateMenu "",1000,file ; Use an empty string to generate separator bars
CreateMenu "����",2,file ; child menu 
CreateMenu "���Ϊ...",3,file ; child menu 
CreateMenu "",1000,file ; Use an empty string to generate separator bars
CreateMenu "�˳�",10001,file ; another child menu

options=CreateMenu("����(&O)",0,menu)
event_default = CreateMenu("Ĭ�����÷����¼�",15,options)
Global adjdoor_place = CreateMenu("��3DԤ���з�����",16,options)
CreateMenu "",1000,options
zone_trans = CreateMenu("��ͼ����",18,options)
author_descr = CreateMenu("�޸����ߺ�����",19,options)
CreateMenu "",1000,options
CreateMenu "�޸ľ�ͷ����",17,options

Local option_event = IniGetInt("options.ini","general","events_default")
If (Not option_event)
	UncheckMenu event_default
Else
	CheckMenu event_default
EndIf
Local option_adjdoors = IniGetInt("options.ini","3d scene","adjdoors_place")
If (Not option_adjdoors)
	UncheckMenu adjdoor_place
Else
	CheckMenu adjdoor_place
EndIf

; Now the Edit menu
edit=CreateMenu("����(&H)",0,menu) ; Main menu with Alt Shortcut - Use & to specify the shortcut key
CreateMenu "�ֲ�"+Chr$(8)+"F1",6,edit ; Another Child menu with Alt Shortcut
CreateMenu "����"+Chr$(8)+"F12",40,edit ; Child menu with Alt Shortcut

HotKeyEvent 59,0,$1001,6

HotKeyEvent 88,0,$1001,40

; Finally, once all menus are set up / updated, we call UpdateWindowMenu to tell the OS about the menu
UpdateWindowMenu WinHandle

SetStatusText(Loadingwindow, "��������2D����...")
Optionwin=CreateWindow("�޸ľ�ͷ����", GraphicsWidth()/2-160,GraphicsHeight()/2-120,300,280,winhandle,1)
HideGadget optionwin
LabelColor = CreateLabel("",5,5,285,60, optionwin,1)
LabelColor2 = CreateLabel("",5,70,285,60,optionwin,1)
LabelRange = CreateLabel("",5,135,285,60, optionwin,1) ;70
color_button = CreateButton("�޸�������ɫ", 25,20,150,30,optionwin)
color_button2 = CreateButton("�޸������ɫ", 25,85,150,30,optionwin)

labelfogR=CreateLabel("R "+IniGetInt("options.ini","3d scene","bg color R"),225,15,40,15, optionwin)
labelfogG=CreateLabel("G "+IniGetInt("options.ini","3d scene","bg color G"),225,30,40,15, optionwin)
labelfogB=CreateLabel("B "+IniGetInt("options.ini","3d scene","bg color B"),225,45,40,15, optionwin)

labelcursorR=CreateLabel("R "+IniGetInt("options.ini","3d scene","cursor color R"),225,80,40,15, optionwin)
labelcursorG=CreateLabel("G "+IniGetInt("options.ini","3d scene","cursor color G"),225,95,40,15, optionwin)
labelcursorB=CreateLabel("B "+IniGetInt("options.ini","3d scene","cursor color B"),225,110 ,40,15, optionwin)

Global redfog = IniGetInt("options.ini","3d scene","bg color R")
Global greenfog = IniGetInt("options.ini","3d scene","bg color G")
Global bluefog = IniGetInt("options.ini","3d scene","bg color B")

Global redcursor = IniGetInt("options.ini","3d scene","cursor color R")
Global greencursor = IniGetInt("options.ini","3d scene","cursor color G")
Global bluecursor = IniGetInt("options.ini","3d scene","cursor color B")

labelrange=CreateLabel("��Ⱦ����",20,170,80,20, optionwin)
Global camerarange = CreateTextField(25, 150, 40, 20, optionwin)
SetGadgetText CameraRange, IniGetInt("options.ini","3d scene","camera range")

Global vsync = CreateButton("��ֱͬ��", 123, 145, 70, 30, optionwin, 2)
SetButtonState vsync, IniGetInt("options.ini","3d scene","vsync")

Global showfps = CreateButton("��ʾFPS", 210, 145, 70, 30, optionwin, 2)
SetButtonState showfps, IniGetInt("options.ini","3d scene","show fps")

cancelopt_button=CreateButton("ȡ��",10,210,100,30,optionwin)
saveopt_button=CreateButton("����",185,210,100,30,optionwin) ;create button

map_settings=CreateWindow("��ͼ����", GraphicsWidth()/2-120,GraphicsHeight()/2-80,240,160,winhandle,1)
HideGadget map_settings

zonetext = CreateLabel("����������ã�",10,10,200,20,map_settings)
labelzonetrans1 = CreateLabel("�������������ݹ���",20,60,80,25,map_settings)
Global zonetrans1 = CreateTextField(20,40,80,20,map_settings)
SetGadgetText zonetrans1,5
labelzonetrans2 = CreateLabel("���������칫������",130,60,80,25,map_settings)
Global zonetrans2 = CreateTextField(130,40,80,20,map_settings)
SetGadgetText zonetrans2,11

Global zonetransvalue1 = 13, zonetransvalue2 = 7

resetzonetrans = CreateButton("����",10,90,100,30,map_settings)
applyzonetrans = CreateButton("Ӧ��",120,90,100,30,map_settings)

authordescr_settings=CreateWindow("�޸����ߺ�����", GraphicsWidth()/2-200,GraphicsHeight()/2-80,400,200,winhandle,1)
HideGadget authordescr_settings

Global MapAuthor$ = "", MapDescription$ = ""
Global map_author_text = CreateTextField(120,30,140,20,authordescr_settings)
map_author_label = CreateLabel("���ߣ�",140,10,160,20,authordescr_settings)
Global descr_text = CreateTextArea(20,80,350,80,authordescr_settings,1)
descr_label = CreateLabel("������",140,60,160,20,authordescr_settings)

SetStatusText(Loadingwindow, "��������3D�鿴��...")
ExecFile("window3d.exe")

Repeat
	vwprt% = api_FindWindow("Blitz Runtime Class" , "MapCreator 3d view") ;User32.dll
	ShowGadget Loadingwindow
Until vwprt <> 0
SetStatusText(Loadingwindow, "���ڴ���3D����...")

api_SetParent(vwprt,MainHwnd);User32.dll				
api_SetWindowPos( vwprt , 0 , 5 , 30 , 895 , 560 , 1) ;User32.dll
api_ShowWindow% (vwprt ,0) ;User32.dll

HideGadget Loadingwindow
ShowGadget WinHandle

SetBuffer CanvasBuffer(map_2d)

Global MouseHit1,MouseHit2,MouseDown1

Repeat
	MouseHit1 = MouseHit(1)
	MouseHit2 = MouseHit(2)
	MouseDown1 = MouseDown(1)
	MouseDown2 = MouseDown(2)
	MouseHit3 = MouseHit(3)
	
	SetGadgetText(map_author_text,(Left(TextFieldText(map_author_text),30)))
	SetGadgetText(map_author_label,("��ͼ���ߣ�"+(Len(TextFieldText(map_author_text)))+"/30����"))
	
	If Len(TextAreaText(descr_text))>400 Then
		SetGadgetText(descr_text,(Left(TextAreaText(descr_text),400)))
	EndIf
	SetGadgetText(descr_label,("������"+(Len(TextAreaText(descr_text)))+"/400����"))
	
	If FileType("CONFIG_TO2D.SI")=1
		f = ReadFile("CONFIG_TO2D.SI")
		
		Grid_SelectedX=ReadInt(f)
		Grid_SelectedY=ReadInt(f)
		
		ChangeGridGadget = True
		GridGadgetText = ""
		SelectGadgetItem listbox,-1
		HideGadget listbox
		ShowGadget listbox
		ClearGadgetItems combobox
		
		If CurrMapGrid%=0
			Local hasEvent% = False
			Local currEventDescr$ = ""
			For rt.RoomTemplates = Each RoomTemplates
				If rt = Map(Grid_SelectedX,Grid_SelectedY)
					For i = 0 To 5
						If rt\Events[i]<>""
							InsertGadgetItem combobox, 0, "[��]"
							hasEvent=True
							Exit
						EndIf
					Next
					For i = 0 To 5
						If rt\events[i]<>""
							InsertGadgetItem combobox, i+1, rt\events[i]
						EndIf
					Next
					SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
					Exit
				EndIf
			Next 
			
			If (Not hasEvent)
				DisableGadget combobox
				SetGadgetText event_desc, ""
				SetGadgetText event_prob_label, ""
				SetSliderValue event_prob,99
				DisableGadget event_prob
				GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"
			Else
				EnableGadget combobox
				If MapEvent(Grid_SelectedX,Grid_SelectedY)<>"" And MapEvent(Grid_SelectedX,Grid_SelectedY)<>"[��]"
					For ev.event = Each Event
						If ev\name = MapEvent(Grid_SelectedX,Grid_SelectedY)
							SetGadgetText event_desc, "�¼�������"+Chr(13)+ev\description
							Exit
						EndIf
					Next
				Else
					SetGadgetText event_desc, ""
				EndIf
				If MapEvent(Grid_SelectedX,Grid_SelectedY)<>"" And MapEvent(Grid_SelectedX,Grid_SelectedY)<>"[��]"
					SetGadgetText event_prob_label, "�¼����ʣ�"+Int(MapEventProb(Grid_SelectedX,Grid_SelectedY)*100)+"%"
					SetSliderValue event_prob,Int(MapEventProb(Grid_SelectedX,Grid_SelectedY)*100)-1
					EnableGadget event_prob
					GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"+Chr(13)+"�¼�����"+MapEvent(Grid_SelectedX,Grid_SelectedY)+Chr(13)+"�¼����ʣ�"+Int(MapEventProb(Grid_SelectedX,Grid_SelectedY)*100)+"%"
				Else
					SetGadgetText event_prob_label, ""
					SetSliderValue event_prob,99
					DisableGadget event_prob
					GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"
				EndIf
			EndIf
			
			c = CountGadgetItems( combobox )
			If c >= 0 Then
				For e=0 To c-1
					If GadgetItemText(combobox,e)=MapEvent(Grid_SelectedX,Grid_SelectedY)
						SelectGadgetItem combobox,e
					EndIf
				Next
			EndIf
		ElseIf CurrMapGrid%=1
			For rt.RoomTemplates = Each RoomTemplates
				If rt = ForestPlace(Grid_SelectedX,Grid_SelectedY)
					SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
					Exit
				EndIf
			Next 
			
			DisableGadget combobox
			SetGadgetText event_desc, ""
			SetGadgetText event_prob_label, ""
			SetSliderValue event_prob,99
			DisableGadget event_prob
			GridGadgetText="��������"+ForestPlace(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)+"��"
		Else
			For rt.RoomTemplates = Each RoomTemplates
				If rt = MTRoom(Grid_SelectedX,Grid_SelectedY)
					SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
					Exit
				EndIf
			Next 
			
			DisableGadget combobox
			SetGadgetText event_desc, ""
			SetGadgetText event_prob_label, ""
			SetSliderValue event_prob,99
			DisableGadget event_prob
			GridGadgetText="��������"+MTRoom(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MTRoomAngle(Grid_SelectedX,Grid_SelectedY)+"��"
		EndIf
		
		CloseFile f
		DeleteFile("CONFIG_TO2D.SI")
	EndIf
	
	If ShowGrid
		Cls
		Local width# = GadgetWidth(map_2d)
		Local height# = GadgetHeight(map_2d)
		;Facility grid
		If CurrMapGrid%=0
			For x = 0 To MapWidth
				For y = 0 To MapHeight
					If y<zonetransvalue2 Then
						Color 255,255,125
					ElseIf y=zonetransvalue2 Then
						Color 255,200,125
					ElseIf y>zonetransvalue2 And y<zonetransvalue1 Then
						Color 255,125,125
					ElseIf y=zonetransvalue1 Then
						Color 255,200,200
					Else
						Color 255,255,255
					EndIf
					
					Rect Float(width)/Float(MapWidth+1)*x,Float(height)/Float(MapHeight+1)*y,(Float(width)/Float(MapWidth+1)),(Float(height)/Float(MapHeight+1)),True
					
					Local PrevSelectedX=Grid_SelectedX, PrevSelectedY=Grid_SelectedY
						If (MouseX()-GadgetX(map_2d))>(Float(width)/Float(MapWidth+1)*x+GadgetX(WinHandle)) And (MouseX()-GadgetX(map_2d))<((Float(width)/Float(MapWidth+1)*x)+(Float(width)/Float(MapWidth+1))+GadgetX(WinHandle))
							Local offset% = 45
							If (MouseY()-GadgetY(map_2d))>(Float(height)/Float(MapHeight+1)*y+GadgetY(WinHandle)+offset) And (MouseY()-GadgetY(map_2d))<((Float(height)/Float(MapHeight+1)*y)+(Float(height)/Float(MapHeight+1))+GadgetY(WinHandle)+offset)
								Color 200,200,200
								Rect Float(width)/Float(MapWidth+1)*x,Float(height)/Float(MapHeight+1)*y,(Float(width)/Float(MapWidth+1)),(Float(height)/Float(MapHeight+1)),True
								If Map(x,y)=Null And SelectedGadgetItem(listbox)>-1
									x2 = Float(width)/Float(MapWidth+1)
									y2 = Float(height)/Float(MapHeight+1)
									DrawImage PlusIcon,(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
								EndIf
								If MouseHit1
									If Grid_SelectedX=x And Grid_SelectedY=y
									Else
										item = SelectedGadgetItem( listbox )
										If Map(x,y)<>Null
											Grid_SelectedX=x
											Grid_SelectedY=y
											ChangeGridGadget = True
											GridGadgetText = ""
											SelectGadgetItem listbox,-1
											HideGadget listbox
											ShowGadget listbox
											
											ClearGadgetItems combobox
											
											hasEvent% = False
											currEventDescr$ = ""
											For rt.RoomTemplates = Each RoomTemplates
												If rt = Map(x,y)
													For i = 0 To 5
														If rt\Events[i]<>""
															InsertGadgetItem combobox, 0, "[��]"
															hasEvent=True
															Exit
														EndIf
													Next
													For i = 0 To 5
														If rt\events[i]<>""
															InsertGadgetItem combobox, i+1, rt\events[i]
														EndIf
													Next
													SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
													Exit
												EndIf
											Next 
											
											If (Not hasEvent)
												DisableGadget combobox
												SetGadgetText event_desc, ""
												SetGadgetText event_prob_label, ""
												SetSliderValue event_prob,99
												DisableGadget event_prob
											Else
												EnableGadget combobox
												If MapEvent(x,y)<>"" And MapEvent(x,y)<>"[��]"
													For ev.event = Each Event
														If ev\name = MapEvent(x,y)
															SetGadgetText event_desc, "�¼�������"+Chr(13)+ev\description
															Exit
														EndIf
													Next
												Else
													SetGadgetText event_desc, ""
												EndIf
												If MapEvent(x,y)<>"" And MapEvent(x,y)<>"[��]"
													SetGadgetText event_prob_label, "�¼����ʣ�100%"
													SetSliderValue event_prob,99
													EnableGadget event_prob
												Else
													SetGadgetText event_prob_label, ""
													SetSliderValue event_prob,99
													DisableGadget event_prob
												EndIf
											EndIf
											
											c = CountGadgetItems( combobox )
											If c >= 0 Then
												For e=0 To c-1
													If GadgetItemText(combobox,e)=MapEvent(x,y)
														SelectGadgetItem combobox,e
													EndIf
												Next
											EndIf
										EndIf
										If item>=0
											If Map(x,y)=Null
												Local room_name$ = GadgetItemText$(listbox, item)
												For rt.RoomTemplates = Each RoomTemplates
													If rt\Name = room_name
														Map(x,y)=rt
														Exit
													EndIf
												Next
												If Map(x,y)\Name = "start" Or Map(x,y)\Name = "checkpoint1" Or Map(x,y)\Name = "checkpoint2"
													MapAngle(x,y)=180
												EndIf
												item2 = SelectedGadgetItem(combobox)
												If item2>=0
													Local event_name$ = GadgetItemText$(combobox, item2)
													If event_name$<>"" And event_name$<>"[��]"
														MapEvent(x,y)=event_name
														MapEventProb(x,y)=Float((SliderValue(event_prob)+1)/100.0)
													EndIf
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
								If MouseDown2
									Grid_SelectedX=-1
									Grid_SelectedY=-1
									ChangeGridGadget = True
									GridGadgetText = ""
									SetSliderValue(event_prob,99)
									SetGadgetText event_prob_label,""
									DisableGadget event_prob
									SetGadgetText event_desc,""
									DisableGadget combobox
									ClearGadgetItems combobox
									If Map(x,y)<>Null
										Map(x,y)=Null
										MapAngle(x,y)=0
										MapEvent(x,y)=""
										MapEventProb(x,y)=0.0
									EndIf
								EndIf
								If MouseHit3
									Grid_SelectedX=-1
									Grid_SelectedY=-1
									ChangeGridGadget = True
									GridGadgetText = ""
									SetSliderValue(event_prob,99)
									SetGadgetText event_prob_label,""
									DisableGadget event_prob
									SetGadgetText event_desc,""
									DisableGadget combobox
									ClearGadgetItems combobox
								EndIf
							EndIf
						EndIf
					
					If Grid_SelectedX=x And Grid_SelectedY=y
						Color 150,150,150
						Rect Float(width)/Float(MapWidth+1)*x,Float(height)/Float(MapHeight+1)*y,(Float(width)/Float(MapWidth+1)),(Float(height)/Float(MapHeight+1)),True
					EndIf
					
					If Map(x,y) = Null
						Color 90,90,90
						Rect Float(width)/Float(MapWidth+1)*x+1,Float(height)/Float(MapHeight+1)*y+1,(Float(width)/Float(MapWidth+1))-1,(Float(height)/Float(MapHeight+1))-1,False
					Else
						x2 = Float(width)/Float(MapWidth+1)
						y2 = Float(height)/Float(MapHeight+1)
						DrawImage MapIcons(Map(x,y)\Shape,Floor(MapAngle(x,y)/90.0)),(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
						
						If Grid_SelectedX=x And Grid_SelectedY=y
							If PrevSelectedX<>Grid_SelectedX Or PrevSelectedY<>Grid_SelectedY
								ChangeGridGadget = True
								If MapEvent(x,y)<>"" And MapEvent(x,y)<>"[��]"
									GridGadgetText = "��������"+Map(x,y)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(x,y)+"��"+Chr(13)+"�¼�����"+MapEvent(x,y)+Chr(13)+"�¼����ʣ�"+Int(MapEventProb(x,y)*100)+"%"
									SetSliderValue(event_prob,Int(MapEventProb(x,y)*100)-1)
								Else
									GridGadgetText = "��������"+Map(x,y)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(x,y)+"��"
								EndIf
								If GadgetText(event_prob_label)<>""
									SetGadgetText event_prob_label,"�¼����ʣ�"+(SliderValue(event_prob)+1)+"%"
								EndIf
							EndIf
						EndIf
					EndIf
				Next
			Next
			If MouseDown1
				If Grid_SelectedX>-1 And Grid_SelectedY>-1
					If MouseX()>(GadgetX(map_2d)+GadgetX(WinHandle)) And MouseX()<((width)+GadgetX(map_2d)+GadgetX(WinHandle))
						offset% = 45
						If MouseY()>(GadgetY(map_2d)+GadgetY(WinHandle)+offset) And MouseY()<((height)+GadgetY(map_2d)+GadgetY(WinHandle)+offset)
							If Map(Grid_SelectedX,Grid_SelectedY)\Name<>"start"
								Local prevAngle = MapAngle(Grid_SelectedX,Grid_SelectedY)
								;Left
								If (MouseX()-GadgetX(map_2d))<(Float(width)/Float(MapWidth+1)*Grid_SelectedX+GadgetX(WinHandle))
									MapAngle(Grid_SelectedX,Grid_SelectedY)=90
								EndIf
								;Right
								If (MouseX()-GadgetX(map_2d))>((Float(width)/Float(MapWidth+1)*Grid_SelectedX)+(Float(width)/Float(MapWidth+1))+GadgetX(WinHandle))
									MapAngle(Grid_SelectedX,Grid_SelectedY)=270
								EndIf
								;Up
								offset% = 45
								If (MouseY()-GadgetY(map_2d))<(Float(height)/Float(MapHeight+1)*Grid_SelectedY+GadgetY(WinHandle)+offset)
									MapAngle(Grid_SelectedX,Grid_SelectedY)=180
								EndIf
								;Down
								If (MouseY()-GadgetY(map_2d))>((Float(height)/Float(MapHeight+1)*Grid_SelectedY)+(Float(height)/Float(MapHeight+1))+GadgetY(WinHandle)+offset)
									MapAngle(Grid_SelectedX,Grid_SelectedY)=0
								EndIf
								Local width2 = Float(width)/Float(MapWidth+1)/2.0
								Local height2 = Float(height)/Float(MapHeight+1)/2.0
								DrawImage Arrows(Floor(MapAngle(Grid_SelectedX,Grid_SelectedY)/90)),Float(width)/Float(MapWidth+1)*Grid_SelectedX+width2,Float(height)/Float(MapHeight+1)*Grid_SelectedY+height2
								If prevAngle<>MapAngle(Grid_SelectedX,Grid_SelectedY)
									ChangeGridGadget = True
									If MapEvent(Grid_SelectedX,Grid_SelectedY)<>"" And MapEvent(Grid_SelectedX,Grid_SelectedY)<>"[��]"
										GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"+Chr(13)+"�¼�����"+MapEvent(Grid_SelectedX,Grid_SelectedY)+Chr(13)+"�¼����ʣ�"+Int(MapEventProb(Grid_SelectedX,Grid_SelectedY)*100)+"%"
									Else
										GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		;Forest grid
		ElseIf CurrMapGrid%=1
			For x = 0 To ForestGridSize
				For y = 0 To ForestGridSize
					Color 125,255,255
					If x=ForestGridSize Or y=ForestGridSize
						Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSize+1))+1,(Float(height-1)/Float(ForestGridSize+1))+1,True
					Else
						Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSize+1)),(Float(height-1)/Float(ForestGridSize+1)),True
					EndIf
					
					If ForestPlace(x,y) = Null
						Color 90,90,90
						Rect Float(width-1)/Float(ForestGridSize+1)*x+1,Float(height-1)/Float(ForestGridSize+1)*y+1,(Float(width-1)/Float(ForestGridSize+1))-1,(Float(height-1)/Float(ForestGridSize+1))-1,False
					EndIf
				Next
			Next
			
			For x = 0 To ForestGridSize
				For y = 0 To ForestGridSize
					Color 255,255,255
					If x=ForestGridSize Or y=ForestGridSize
						Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSize+1))+1,(Float(height-1)/Float(ForestGridSize+1))+1,True
					Else
						Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSize+1)),(Float(height-1)/Float(ForestGridSize+1)),True
					EndIf
					
					PrevSelectedX=Grid_SelectedX
					PrevSelectedY=Grid_SelectedY
					If (MouseX()-GadgetX(map_2d))>(Float(width-1)/Float(ForestGridSize+1)*x+GadgetX(WinHandle)) And (MouseX()-GadgetX(map_2d))<((Float(width-1)/Float(ForestGridSize+1)*x)+(Float(width-1)/Float(ForestGridSize+1))+GadgetX(WinHandle))
						offset% = 45
						If (MouseY()-GadgetY(map_2d))>(Float(height-1)/Float(ForestGridSize+1)*y+GadgetY(WinHandle)+offset) And (MouseY()-GadgetY(map_2d))<((Float(height-1)/Float(ForestGridSize+1)*y)+(Float(height-1)/Float(ForestGridSize+1))+GadgetY(WinHandle)+offset)
							Color 200,200,200
							Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSizee+1)),(Float(height-1)/Float(ForestGridSize+1)),True
							If ForestPlace(x,y)=Null And SelectedGadgetItem(listbox)>-1
								x2 = Float(width)/Float(ForestGridSize+1)
								y2 = Float(height)/Float(ForestGridSize+1)
								DrawImage PlusIcon,(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
							EndIf
							If MouseHit1
								If Grid_SelectedX=x And Grid_SelectedY=y
									
								Else
									item = SelectedGadgetItem( listbox )
									If ForestPlace(x,y)<>Null
										Grid_SelectedX=x
										Grid_SelectedY=y
										ChangeGridGadget = True
										GridGadgetText = ""
										SelectGadgetItem listbox,-1
										HideGadget listbox
										ShowGadget listbox
										
										ClearGadgetItems combobox
										
										For rt.RoomTemplates = Each RoomTemplates
											If rt = ForestPlace(x,y)
												SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
												Exit
											EndIf
										Next 
										
										DisableGadget combobox
										SetGadgetText event_desc, ""
										SetGadgetText event_prob_label, ""
										SetSliderValue event_prob,99
										DisableGadget event_prob
									EndIf
									If item>=0
										If ForestPlace(x,y)=Null
											room_name$ = GadgetItemText$(listbox, item)
											For rt.RoomTemplates = Each RoomTemplates
												If rt\Name = room_name
													ForestPlace(x,y)=rt
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							If MouseDown2
								Grid_SelectedX=-1
								Grid_SelectedY=-1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(event_prob,99)
								SetGadgetText event_prob_label,""
								DisableGadget event_prob
								SetGadgetText event_desc,""
								DisableGadget combobox
								ClearGadgetItems combobox
								If ForestPlace(x,y)<>Null
									ForestPlace(x,y)=Null
									ForestPlaceAngle(x,y)=0
								EndIf
							EndIf
							If MouseHit3
								Grid_SelectedX=-1
								Grid_SelectedY=-1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(event_prob,99)
								SetGadgetText event_prob_label,""
								DisableGadget event_prob
								SetGadgetText event_desc,""
								DisableGadget combobox
								ClearGadgetItems combobox
							EndIf
						EndIf
					EndIf
					
					If Grid_SelectedX=x And Grid_SelectedY=y
						Color 150,150,150
						Rect Float(width-1)/Float(ForestGridSize+1)*x,Float(height-1)/Float(ForestGridSize+1)*y,(Float(width-1)/Float(ForestGridSize+1)),(Float(height-1)/Float(ForestGridSize+1)),True
					EndIf
					
					If ForestPlace(x,y) = Null
						Color 90,90,90
						Rect Float(width-1)/Float(ForestGridSize+1)*x+1,Float(height-1)/Float(ForestGridSize+1)*y+1,(Float(width-1)/Float(ForestGridSize+1))-1,(Float(height-1)/Float(ForestGridSize+1))-1,False
					Else
						x2 = Float(width-1)/Float(ForestGridSize+1)
						y2 = Float(height-1)/Float(ForestGridSize+1)
						If ForestPlace(x,y)\Name = "SCP-860-1 door" Then
							DrawImage SpecialIcons(1,Floor(ForestPlaceAngle(x,y)/90.0)),(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
						Else
							DrawImage ForestIcons(ForestPlace(x,y)\Shape,Floor(ForestPlaceAngle(x,y)/90.0)),(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
						EndIf
						
						If Grid_SelectedX=x And Grid_SelectedY=y
							If PrevSelectedX<>Grid_SelectedX Or PrevSelectedY<>Grid_SelectedY
								ChangeGridGadget = True
								GridGadgetText = "��������"+ForestPlace(x,y)\Name+Chr(13)+"��ת�Ƕȣ�"+ForestPlaceAngle(x,y)+"��"
							EndIf
						EndIf
					EndIf
				Next
			Next
			If MouseDown1
				If Grid_SelectedX>-1 And Grid_SelectedY>-1
					If MouseX()>(GadgetX(map_2d)+GadgetX(WinHandle)) And MouseX()<((width)+GadgetX(map_2d)+GadgetX(WinHandle))
						offset% = 45
						If MouseY()>(GadgetY(map_2d)+GadgetY(WinHandle)+offset) And MouseY()<((height)+GadgetY(map_2d)+GadgetY(WinHandle)+offset)
							prevAngle = ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)
							;Left
							If (MouseX()-GadgetX(map_2d))<(Float(width-1)/Float(ForestGridSize+1)*Grid_SelectedX+GadgetX(WinHandle))
								ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)=90
							EndIf
							;Right
							If (MouseX()-GadgetX(map_2d))>((Float(width-1)/Float(ForestGridSize+1)*Grid_SelectedX)+(Float(width-1)/Float(ForestGridSize+1))+GadgetX(WinHandle))
								ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)=270
							EndIf
							;Up
							offset% = 45
							If (MouseY()-GadgetY(map_2d))<(Float(height-1)/Float(ForestGridSize+1)*Grid_SelectedY+GadgetY(WinHandle)+offset)
								ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)=180
							EndIf
							;Down
							If (MouseY()-GadgetY(map_2d))>((Float(height-1)/Float(ForestGridSize+1)*Grid_SelectedY)+(Float(height-1)/Float(ForestGridSize+1))+GadgetY(WinHandle)+offset)
								ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)=0
							EndIf
							width2 = Float(width-1)/Float(ForestGridSize+1)/2.0
							height2 = Float(height-1)/Float(ForestGridSize+1)/2.0
							DrawImage Arrows(Floor(ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)/90)),Float(width-1)/Float(ForestGridSize+1)*Grid_SelectedX+width2,Float(height-1)/Float(ForestGridSize+1)*Grid_SelectedY+height2
							If prevAngle<>ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)
								ChangeGridGadget = True
								GridGadgetText="��������"+ForestPlace(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+ForestPlaceAngle(Grid_SelectedX,Grid_SelectedY)+"��"
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		;Maintenance tunnel grid
		Else ;20*28
			For x = 0 To MT_GridSize
				For y = 0 To MT_GridSize
					Color 255,255,255
					Rect Float(width)/Float(MT_GridSize+1)*x,Float(height)/Float(MT_GridSize+1)*y,(Float(width)/Float(MT_GridSize+1)),(Float(height)/Float(MT_GridSize+1)),True
					
					PrevSelectedX=Grid_SelectedX
					PrevSelectedY=Grid_SelectedY
					If (MouseX()-GadgetX(map_2d))>(Float(width)/Float(MT_GridSize+1)*x+GadgetX(WinHandle)) And (MouseX()-GadgetX(map_2d))<((Float(width)/Float(MT_GridSize+1)*x)+(Float(width)/Float(MT_GridSize+1))+GadgetX(WinHandle))
						offset% = 45
						If (MouseY()-GadgetY(map_2d))>(Float(height)/Float(MT_GridSize+1)*y+GadgetY(WinHandle)+offset) And (MouseY()-GadgetY(map_2d))<((Float(height)/Float(MT_GridSize+1)*y)+(Float(height)/Float(MT_GridSize+1))+GadgetY(WinHandle)+offset)
							Color 200,200,200
							Rect Float(width)/Float(MT_GridSize+1)*x,Float(height)/Float(MT_GridSize+1)*y,(Float(width)/Float(MT_GridSize+1)),(Float(height)/Float(MT_GridSize+1)),True
							If MTRoom(x,y)=Null And SelectedGadgetItem(listbox)>-1
								x2 = Float(width)/Float(MT_GridSize+1)
								y2 = Float(height)/Float(MT_GridSize+1)
								DrawImage PlusIcon,(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
							EndIf
							If MouseHit1
								If Grid_SelectedX=x And Grid_SelectedY=y
									
								Else
									item = SelectedGadgetItem( listbox )
									If MTRoom(x,y)<>Null
										Grid_SelectedX=x
										Grid_SelectedY=y
										ChangeGridGadget = True
										GridGadgetText = ""
										SelectGadgetItem listbox,-1
										HideGadget listbox
										ShowGadget listbox
										
										ClearGadgetItems combobox
										
										For rt.RoomTemplates = Each RoomTemplates
											If rt = MTRoom(x,y)
												SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
												Exit
											EndIf
										Next 
										
										DisableGadget combobox
										SetGadgetText event_desc, ""
										SetGadgetText event_prob_label, ""
										SetSliderValue event_prob,99
										DisableGadget event_prob
									EndIf
									If item>=0
										If MTRoom(x,y)=Null
											room_name$ = GadgetItemText$(listbox, item)
											For rt.RoomTemplates = Each RoomTemplates
												If rt\Name = room_name
													MTRoom(x,y)=rt
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							If MouseDown2
								Grid_SelectedX=-1
								Grid_SelectedY=-1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(event_prob,99)
								SetGadgetText event_prob_label,""
								DisableGadget event_prob
								SetGadgetText event_desc,""
								DisableGadget combobox
								ClearGadgetItems combobox
								If MTRoom(x,y)<>Null
									MTRoom(x,y)=Null
									MTRoomAngle(x,y)=0
								EndIf
							EndIf
							If MouseHit3
								Grid_SelectedX=-1
								Grid_SelectedY=-1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(event_prob,99)
								SetGadgetText event_prob_label,""
								DisableGadget event_prob
								SetGadgetText event_desc,""
								DisableGadget combobox
								ClearGadgetItems combobox
							EndIf
						EndIf
					EndIf
					
					If Grid_SelectedX=x And Grid_SelectedY=y
						Color 150,150,150
						Rect Float(width)/Float(MT_GridSize+1)*x,Float(height)/Float(MT_GridSize+1)*y,(Float(width)/Float(MT_GridSize+1)),(Float(height)/Float(MT_GridSize+1)),True
					EndIf
					
					If MTRoom(x,y) = Null
						Color 90,90,90
						Rect Float(width)/Float(MT_GridSize+1)*x+1,Float(height)/Float(MT_GridSize+1)*y+1,(Float(width)/Float(MT_GridSize+1))-1,(Float(height)/Float(MT_GridSize+1))-1,False
					Else
						x2 = Float(width)/Float(MT_GridSize+1)
						y2 = Float(height)/Float(MT_GridSize+1)
						If MTRoom(x,y)\Name = "Maintenance tunnel elevator"
							DrawImage SpecialIcons(2,Floor(MTRoomAngle(x,y)/90.0)),(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
						Else
							DrawImage MapIcons(MTRoom(x,y)\Shape,Floor(MTRoomAngle(x,y)/90.0)),(x2*x)+(x2/2.0)+0.5,(y2*y)+(y2/2.0)+0.5
						EndIf
						
						If Grid_SelectedX=x And Grid_SelectedY=y
							If PrevSelectedX<>Grid_SelectedX Or PrevSelectedY<>Grid_SelectedY
								ChangeGridGadget = True
								GridGadgetText = "��������"+MTRoom(x,y)\Name+Chr(13)+"��ת�Ƕȣ�"+MTRoomAngle(x,y)+"��"
							EndIf
						EndIf
					EndIf
				Next
			Next
			If MouseDown1
				If Grid_SelectedX>-1 And Grid_SelectedY>-1
					If MouseX()>(GadgetX(map_2d)+GadgetX(WinHandle)) And MouseX()<((width)+GadgetX(map_2d)+GadgetX(WinHandle))
						offset% = 45
						If MouseY()>(GadgetY(map_2d)+GadgetY(WinHandle)+offset) And MouseY()<((height)+GadgetY(map_2d)+GadgetY(WinHandle)+offset)
							prevAngle = MTRoomAngle(Grid_SelectedX,Grid_SelectedY)
							;Left
							If (MouseX()-GadgetX(map_2d))<(Float(width)/Float(MT_GridSize+1)*Grid_SelectedX+GadgetX(WinHandle))
								MTRoomAngle(Grid_SelectedX,Grid_SelectedY)=90
							EndIf
							;Right
							If (MouseX()-GadgetX(map_2d))>((Float(width)/Float(MT_GridSize+1)*Grid_SelectedX)+(Float(width)/Float(MT_GridSize+1))+GadgetX(WinHandle))
								MTRoomAngle(Grid_SelectedX,Grid_SelectedY)=270
							EndIf
							;Up
							offset% = 45
							If (MouseY()-GadgetY(map_2d))<(Float(height)/Float(MT_GridSize+1)*Grid_SelectedY+GadgetY(WinHandle)+offset)
								MTRoomAngle(Grid_SelectedX,Grid_SelectedY)=180
							EndIf
							;Down
							If (MouseY()-GadgetY(map_2d))>((Float(height)/Float(MT_GridSize+1)*Grid_SelectedY)+(Float(height)/Float(MT_GridSize+1))+GadgetY(WinHandle)+offset)
								MTRoomAngle(Grid_SelectedX,Grid_SelectedY)=0
							EndIf
							width2 = Float(width)/Float(MT_GridSize+1)/2.0
							height2 = Float(height)/Float(MT_GridSize+1)/2.0
							DrawImage Arrows(Floor(MTRoomAngle(Grid_SelectedX,Grid_SelectedY)/90)),Float(width)/Float(MT_GridSize+1)*Grid_SelectedX+width2,Float(height)/Float(MT_GridSize+1)*Grid_SelectedY+height2
							If prevAngle<>MTRoomAngle(Grid_SelectedX,Grid_SelectedY)
								ChangeGridGadget = True
								GridGadgetText="��������"+MTRoom(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MTRoomAngle(Grid_SelectedX,Grid_SelectedY)+"��"
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		FlipCanvas map_2d
	EndIf
	If Grid_SelectedX<>-1 And Grid_SelectedY<>-1
		Local prevEvent = MapEvent(Grid_SelectedX,Grid_SelectedY)
		item2 = SelectedGadgetItem(combobox)
		If item2>=0
			event_name$ = GadgetItemText$(combobox, item2)
			If event_name<>prevEvent
				If event_name$<>"" And event_name$<>"[��]"
					MapEvent(Grid_SelectedX,Grid_SelectedY)=event_name
					MapEventProb(Grid_SelectedX,Grid_SelectedY)=Float((SliderValue(event_prob)+1)/100.0)
					GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"+Chr(13)+"�¼�����"+MapEvent(Grid_SelectedX,Grid_SelectedY)+Chr(13)+"�¼����ʣ�"+Int(MapEventProb(Grid_SelectedX,Grid_SelectedY)*100)+"%"
					ChangeGridGadget=True
				Else
					MapEvent(Grid_SelectedX,Grid_SelectedY)=event_name
					MapEventProb(Grid_SelectedX,Grid_SelectedY)=0.0
					GridGadgetText="��������"+Map(Grid_SelectedX,Grid_SelectedY)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(Grid_SelectedX,Grid_SelectedY)+"��"
					ChangeGridGadget=True
				EndIf
			EndIf
		EndIf
	EndIf
	If ChangeGridGadget
		SetGadgetText grid_room_info, GridGadgetText
		ChangeGridGadget=False
	EndIf
	id=WaitEvent()
	If ID=$803 And EventSource()= winhandle Then Exit ; Handle the close gadget on the window being hit
	If ID=$803 And  EventSource()= optionwin Then HideGadget optionwin
	If ID=$803 And EventSource()= map_settings Then HideGadget map_settings
	If ID=$803 And EventSource()= authordescr_settings Then HideGadget authordescr_settings
	If ID=$1001 Then ; Handle any menu item hit events
	; extract the EventData as this will contain our unique id for the menu item
	EID=EventData() 
	    If EID=0 Then 
			
			result = Proceed("���浱ǰ��ͼ��",True) 
			If result=1 Then
				SetStatusText(winhandle, "�������ŵ�ͼ�󴴽��µ�ͼ")
				If FileType(filename$) <>1
  			   		filename$ = RequestFile("�򿪵�ͼ","cbmap",True,"")
				EndIf
				If filename<>""
					SaveMap(filename$)
				EndIf
				EraseMap()
				If ShowGrid=False
					SaveMap("CONFIG_MAPINIT.SI",True)
				EndIf
				filename$ = ""
			ElseIf result=0 Then 
				SetStatusText(winhandle, "�������ŵ�ͼ�󴴽��µ�ͼ")
				EraseMap()
				If ShowGrid=False
					SaveMap("CONFIG_MAPINIT.SI",True)
				EndIf
				filename$ = ""
			ElseIf result=-1 Then
				SetStatusText(winhandle, "�µ�ͼ�����ѱ�ȡ��")
			EndIf
		EndIf
		If EID=1 Then
			filename$ = RequestFile("�򿪵�ͼ","*cbmap2;*cbmap,*cbmap2,*cbmap",False,"") 
			If filename<>""
				LoadMap(filename$)
			Else
				;Maybe a message or something here, dunno...
			EndIf
		EndIf
		If EID=2 Then
			If FileType(filename) <> 1
  			   filename$ = RequestFile("�����ͼ","cbmap2,cbmap",True,"")
			EndIf
			If filename<>""
				If Right(filename,5)="cbmap" Then
					value = Confirm("cbmap���ѹ�ʱ���ļ���ʽ�������ʹ�ô˸�ʽ�����ͼ��һЩ���ݽ��ᶪʧ��"+Chr(13)+"ȷ��Ҫ������",0)
					If value=1 Then
						SaveMap(filename$,False,1)
					EndIf
				Else
					SaveMap(filename$)
				EndIf
			Else
				;Maybe a message or something here, dunno...
			EndIf
		EndIf	
		If EID=3 Then
			.back
			filename$ = RequestFile("�����ͼ","cbmap2,cbmap",True,"")
			If filename<>""
				If Right(filename,5)="cbmap" Then
						value = Confirm("cbmap���ѹ�ʱ���ļ���ʽ�������ʹ�ô˸�ʽ�����ͼ��һЩ���ݽ��ᶪʧ��"+Chr(13)+"ȷ��Ҫ������",0)					
						If value=0 Then
						Goto back
					EndIf
					SaveMap(filename$,False,1)
				Else
					SaveMap(filename$)
				EndIf
			Else
				;Maybe a message or something here, dunno...
			EndIf
		EndIf
		If EID=6 Then 
			If TraditionalChinese Then 
				ExecFile "https://manual.scpcbgame.cn/general/map-creator/zh_hant"
			Else 
				ExecFile "Manual.pdf"
			EndIf
		EndIf
		If EID=40  Then Notify "SCP - ����ʧЧ ��ͼ������ v"+versionnumber+Chr$(13)+"��Vane Brain��ENDSHN���������ú����麺��"+Chr$(13)+"https://scpcbgame.cn/"
		If EID=17 Then 
			ShowGadget optionwin
		EndIf
		If EID=15
			value=MenuChecked(event_default)
			If value=0 Then CheckMenu(event_default)
			If value=1 Then UncheckMenu(event_default)
			UpdateWindowMenu winhandle
			IniWriteString("options.ini","general","events_default",Not value)
		EndIf
		If EID=16
			value=MenuChecked(adjdoor_place)
			If value=0 Then CheckMenu(adjdoor_place)
			If value=1 Then UncheckMenu(adjdoor_place)
			UpdateWindowMenu winhandle
			IniWriteString("options.ini","3d scene","adjdoors_place",Not value)
			WriteOptions()
		EndIf
		If EID=18
			ShowGadget map_settings
		EndIf
		If EID=19
			ShowGadget authordescr_settings
		EndIf
		If EID=10001 Then End
	EndIf
	
	DebugLog EventData()
	If ID=$401 Then ; Button action event.  EventData contains the toolbar button hit.
		If EventSource()=tab Then
            ;in EventData steht das neue Item
            ;also in Abhangigkeit des Gadgets zeigen und verstecken
			Select EventData()
				Case 0
                 	api_ShowWindow% (vwprt ,0)
                  	ShowGadget listbox 
					ShowGadget event_desc 
					ShowGadget txtbox 
					ShowGadget ok 
					ShowGadget clean_txt
					ShowGadget combobox
					ShowGadget map_2d
					ShowGadget room_desc
					ShowGadget event_prob
					ShowGadget event_prob_label
					ShowGadget grid_room_info
					ShowGadget tab2
					SetGadgetShape(tab, 0,5,ResWidth/4+20,ResHeight-60)
					ShowGrid = True
				Case 1
					api_ShowWindow% (vwprt ,1) ;User32.dll
              		HideGadget listbox 
					HideGadget event_desc 
					HideGadget txtbox 
					HideGadget ok 
					HideGadget clean_txt
					HideGadget combobox
					HideGadget map_2d
					HideGadget room_desc
					HideGadget event_prob
					HideGadget event_prob_label
					HideGadget grid_room_info
					HideGadget tab2
					SetGadgetShape(tab, 0,5,ResWidth,ResHeight-60)
					ShowGrid = False
					SaveMap("CONFIG_MAPINIT.SI",True)
			End Select
		EndIf
		
		If EventSource()=tab2 Then
			CurrMapGrid% = EventData()
			ClearGadgetItems listbox
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					AddGadgetItem listbox, rt\Name
				EndIf
			Next
			ClearGadgetItems combobox
			DisableGadget combobox
			SetGadgetText event_desc, ""
			SetGadgetText event_prob_label, ""
			SetSliderValue event_prob,99
			DisableGadget event_prob
			SetGadgetText room_desc,"����������"
			Grid_SelectedX=-1
			Grid_SelectedY=-1
		EndIf
		
		If EventSource()=color_button Then 
			If RequestColor(IniGetInt("options.ini","3d scene","bg color R"),IniGetInt("options.ini","3d scene","bg color G"),IniGetInt("options.ini","3d scene","bg color B"))=1 Then
				redfog=RequestedRed()
				greenfog=RequestedGreen()
				bluefog=RequestedBlue()
				SetGadgetText labelfogR, "R "+redfog
				SetGadgetText labelfogG, "G "+greenfog
				SetGadgetText labelfogB, "B "+bluefog
			EndIf	
		EndIf
		If EventSource()=color_button2 Then
			If RequestColor(IniGetInt("options.ini","3d scene","cursor color R"),IniGetInt("options.ini","3d scene","cursor color G"),IniGetInt("options.ini","3d scene","cursor color B"))=1 Then
				redcursor=RequestedRed()
				greencursor=RequestedGreen()
				bluecursor=RequestedBlue()
				SetGadgetText labelcursorR, "R "+redcursor
				SetGadgetText labelcursorG, "G "+greencursor
				SetGadgetText labelcursorB, "B "+bluecursor
			EndIf
		EndIf
		If EventSource()=cancelopt_button Then
			SetGadgetText labelfogR,"R "+IniGetInt("options.ini","3d scene","bg color R")
			SetGadgetText labelfogG,"G "+IniGetInt("options.ini","3d scene","bg color G")
			SetGadgetText labelfogB,"B "+IniGetInt("options.ini","3d scene","bg color B")
			SetGadgetText labelcursorR,"R "+IniGetInt("options.ini","3d scene","cursor color R")
			SetGadgetText labelcursorG,"G "+IniGetInt("options.ini","3d scene","cursor color G")
			SetGadgetText labelcursorB,"B "+IniGetInt("options.ini","3d scene","cursor color B")
			SetGadgetText CameraRange, IniGetInt("options.ini","3d scene","camera range")
			SetButtonState vsync, IniGetInt("options.ini","3d scene","vsync")
			SetButtonState showfps, IniGetInt("options.ini","3d scene","show fps")
			HideGadget optionwin
		EndIf	
		If EventSource()=saveopt_button Then
			HideGadget optionwin
			SetStatusText(winhandle, "�������ѱ���")
			IniWriteString("options.ini","3d scene","bg color R",redfog)
			IniWriteString("options.ini","3d scene","bg color G",greenfog)
			IniWriteString("options.ini","3d scene","bg color B",bluefog)
			IniWriteString("options.ini","3d scene","cursor color R",redcursor)
			IniWriteString("options.ini","3d scene","cursor color G",greencursor)
			IniWriteString("options.ini","3d scene","cursor color B",bluecursor)
			IniWriteString("options.ini","3d scene","camera range",TextFieldText$(CameraRange))
			IniWriteString("options.ini","3d scene","vsync",ButtonState(vsync))
			IniWriteString("options.ini","3d scene","show fps",ButtonState(showfps))
			WriteOptions()
		EndIf
		If EventSource()=resetzonetrans Then
			SetGadgetText zonetrans1,5
			SetGadgetText zonetrans2,11
			zonetransvalue1 = (MapHeight)-Int(TextFieldText$(zonetrans1))
			zonetransvalue2 = (MapHeight)-Int(TextFieldText$(zonetrans2))
		EndIf
		If EventSource()=zonetrans1
			SetGadgetText zonetrans1,Int(TextFieldText$(zonetrans1))
		EndIf
		If EventSource()=zonetrans2
			SetGadgetText zonetrans2,Int(TextFieldText$(zonetrans2))
		EndIf
		If EventSource()=applyzonetrans
			SetGadgetText zonetrans2,Int(Min(Max(Int(TextFieldText$(zonetrans2)),Int(TextFieldText$(zonetrans1))+2),MapHeight-1))
			SetGadgetText zonetrans1,Int(Min(Max(Int(TextFieldText$(zonetrans1)),1),Int(TextFieldText$(zonetrans2))-2))
			zonetransvalue1 = (MapHeight)-Int(TextFieldText$(zonetrans1))
			zonetransvalue2 = (MapHeight)-Int(TextFieldText$(zonetrans2))
		EndIf
		If EventSource()=ok Then ; when ok is pressed
			ClearGadgetItems listbox
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					If Instr(rt\Name,TextFieldText(txtbox))
						AddGadgetItem listbox, rt\Name
					EndIf
				EndIf
			Next
		EndIf
		If EventSource()=clean_txt Then
			SetGadgetText txtbox, ""
			ClearGadgetItems listbox
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					AddGadgetItem listbox, rt\Name
				EndIf
			Next
		EndIf
		If EventSource() = combobox Then
			item = SelectedGadgetItem( combobox )
			
			If item > -1 Then
				
				name$ = GadgetItemText$(combobox,item)
				
				If item > 0
					For ev.event = Each Event
						If ev\name = name
							SetGadgetText event_desc, "�¼�������"+Chr(13)+ev\description
							Exit
						EndIf
					Next
					SetGadgetText event_prob_label,"�¼����ʣ�"+(SliderValue(event_prob)+1)+"%"
					EnableGadget event_prob
					SetSliderValue event_prob,99
				Else
					SetGadgetText event_desc, ""
					SetGadgetText event_prob_label, ""
					SetSliderValue event_prob,99
					DisableGadget event_prob
				EndIf
			EndIf
		EndIf
		If EventSource() = listbox Then 
		    ;In Abhangigkeit des Tabs den selektierten Eintrag herausfinden
            item = SelectedGadgetItem( listbox )
            
			Grid_SelectedX=-1
			Grid_SelectedY=-1
			ChangeGridGadget = True
			GridGadgetText = ""
            
            ;Wenn ein Eintrag ausgewahlt wurde
            If item > - 1 Then
				
               ;Bezeichnung des Eintrags herausfinden
               name$ = GadgetItemText$(listbox, item)
				
				ClearGadgetItems combobox
				
				hasEvent% = False
				Local currRT.RoomTemplates = Null
				For rt.RoomTemplates = Each RoomTemplates
					If rt\Name = name
						For i = 0 To 5
							If rt\Events[i]<>""
								InsertGadgetItem combobox, 0, "[��]"
								hasEvent=True
								Exit
							EndIf
						Next
						For i = 0 To 5
							If rt\events[i]<>""
								InsertGadgetItem combobox, i+1, rt\events[i]
							EndIf
						Next
						SetGadgetText room_desc,"����������"+Chr(13)+rt\Description
						currRT = rt
						Exit
					EndIf
				Next 
				
				If CountGadgetItems( combobox ) > 0 Then
					If MenuChecked(event_default) Then
						SelectGadgetItem combobox, 1
					Else
						SelectGadgetItem combobox, 0
					EndIf 
				EndIf
				
				If (Not hasEvent)
					DisableGadget combobox
					SetGadgetText event_desc, ""
					SetGadgetText event_prob_label, ""
					SetSliderValue event_prob,99
					DisableGadget event_prob
				Else
					EnableGadget combobox
					If SelectedGadgetItem(combobox)<>0
						For ev.event = Each event
							If ev\name = currRT\events[0]
								SetGadgetText event_desc, "�¼�������"+Chr(13)+ev\description
								Exit
							EndIf
						Next
						SetGadgetText event_prob_label, "�¼����ʣ�100%"
						SetSliderValue event_prob,99
						EnableGadget event_prob
					Else
						SetGadgetText event_prob_label, ""
						SetSliderValue event_prob,99
						DisableGadget event_prob
					EndIf
				EndIf
				Grid_SelectedX=-1
				Grid_SelectedY=-1
				ChangeGridGadget = True
				GridGadgetText = ""
			EndIf
		EndIf
		If EventSource()=event_prob
			SetGadgetText event_prob_label,"�¼����ʣ�"+(SliderValue(event_prob)+1)+"%"
			If Grid_SelectedX<>-1 And Grid_SelectedY<>-1
				x=Grid_SelectedX
				y=Grid_SelectedY
				MapEventProb(x,y)=Float((SliderValue(event_prob)+1)/100.0)
				If MapEvent(x,y)<>""
					GridGadgetText = "��������"+Map(x,y)\Name+Chr(13)+"��ת�Ƕȣ�"+MapAngle(x,y)+"��"+Chr(13)+"�¼�����"+MapEvent(x,y)+Chr(13)+"�¼����ʣ�"+Int(MapEventProb(x,y)*100)+"%"
				EndIf
				SetGadgetText grid_room_info, GridGadgetText
			EndIf
		EndIf
	EndIf
	
Forever
End

Function StripPath$(file$) 
	Local name$=""
	If Len(file$)>0 
		For i=Len(file$) To 1 Step -1 
			
			mi$=Mid$(file$,i,1) 
			If mi$="\" Or mi$="/" Then Return name$
			
			name$=mi$+name$ 
		Next 
		
	EndIf 
	
	Return name$ 
End Function

Function Piece$(s$,entry,char$=" ")
	While Instr(s,char+char)
		s=Replace(s,char+char,char)
	Wend
	For n=1 To entry-1
		p=Instr(s,char)
		s=Right(s,Len(s)-p)
	Next
	p=Instr(s,char)
	If p<1
		a$=s
	Else
		a=Left(s,p-1)
	EndIf
	Return a
End Function

Function Min#(a#,b#)
	If a < b Then Return a Else Return b
End Function

Function Max#(a#,b#)
	If a > b Then Return a Else Return b
End Function

Const ROOM1% = 1, ROOM2% = 2, ROOM2C% = 3, ROOM3% = 4, ROOM4% = 5

Const ZONEAMOUNT% = 3

Global RoomTempID%
Type RoomTemplates
	Field Shape%, Name$
	Field Description$
	Field Large%
	Field id
	
	Field events$[5]
	
	Field MapGrid% = 0
End Type 

Function CreateRoomTemplate.RoomTemplates()
	Local rt.RoomTemplates = New RoomTemplates
	
	rt\id = RoomTempID
	RoomTempID=RoomTempID+1
	
	Return rt
End Function

Function LoadRoomTemplates(file$)
	Local TemporaryString$
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	
	Local f = OpenFile(file)
	
	;Facility rooms
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			Local AddRoom% = True
			Select TemporaryString
				Case "room ambience","173","pocketdimension","dimension1499","gatea"
					AddRoom = False
			End Select
			If AddRoom
				rt = CreateRoomTemplate()
				rt\Name = TemporaryString
				
				StrTemp = Lower(IniGetString(file, TemporaryString, "shape"))
				Select StrTemp
					Case "room1", "1"
						rt\Shape = ROOM1
					Case "room2", "2"
						rt\Shape = ROOM2
					Case "room2c", "2c"
						rt\Shape = ROOM2C
					Case "room3", "3"
						rt\Shape = ROOM3
					Case "room4", "4"
						rt\Shape = ROOM4
					Default
				End Select
				
				rt\Description = IniGetString(file, TemporaryString, "descr")
				rt\Large = IniGetInt(file, TemporaryString, "large")
				
				rt\MapGrid = 0
			EndIf
			
		EndIf
	Wend
	
	;Forest pieces
	Local fr_prefix$ = "SCP-860-1 "
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"door"
	rt\Shape = ROOM1
	rt\Description = "FRDOOR"
	rt\MapGrid = 1
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"endroom"
	rt\Shape = ROOM1
	rt\Description = "FRENDROOM"
	rt\MapGrid = 1
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"path"
	rt\Shape = ROOM2
	rt\Description = "FRPATH"
	rt\MapGrid = 1
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"corner"
	rt\Shape = ROOM2C
	rt\Description = "FRCORNER"
	rt\MapGrid = 1
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"t-shaped path"
	rt\Shape = ROOM3
	rt\Description = "FRTSHAPE"
	rt\MapGrid = 1
	rt = CreateRoomTemplate()
	rt\Name = fr_Prefix$+"4-way path"
	rt\Shape = ROOM4
	rt\Description = "FR4WAY"
	rt\MapGrid = 1
	
	;Maintenance tunnel rooms
	Local mt_prefix$ = "Maintenance tunnel "
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"endroom"
	rt\shape = ROOM1
	rt\Description = "MTENDROOM"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"corridor"
	rt\shape = ROOM2
	rt\Description = "MTCORRIDOR"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"corner"
	rt\shape = ROOM2C
	rt\Description = "MTCORNER"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"t-shaped room"
	rt\shape = ROOM3
	rt\Description = "MTTSHAPE"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"4-way room"
	rt\shape = ROOM4
	rt\Description = "MT4WAY"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"elevator"
	rt\shape = ROOM2
	rt\Description = "MTELEVATOR"
	rt\MapGrid = 2
	rt = CreateRoomTemplate()
	rt\Name = mt_prefix$+"generator room"
	rt\shape = ROOM1
	rt\Description = "MTGENERATOR"
	rt\MapGrid = 2
	
	CloseFile f
End Function

Const MaxEvents = 9

Type Event
	Field Name$
	Field Description$
	Field Room$[MaxEvents]
End Type

Function InitEvents(file$)
	Local TemporaryString$
	Local e.Event = Null
	Local StrTemp$ = ""
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "["
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			e = New Event
			e\Name = TemporaryString
			
			e\Description = IniGetString(file, TemporaryString, "descr")
			
			For i = 1 To MaxEvents
				e\Room[i] = IniGetString(file, TemporaryString, "room"+i)
			Next
			
		EndIf
	Wend
	
	CloseFile f
End Function

Function AddEvents()
	Local rt.RoomTemplates,e.Event
	
	For rt.RoomTemplates = Each RoomTemplates
		For e = Each Event
			For i = 1 To MaxEvents
				If rt\Name = e\Room[i]
					AssignEventToRoomTemplate(rt,e)
				EndIf
			Next
		Next
	Next
End Function

Function AssignEventToRoomTemplate(rt.RoomTemplates,e.Event)
	For i = 0 To 5
		If rt\events[i]=""
			rt\events[i]=e\Name
			Exit
		EndIf
	Next
End Function

Function GetZone(y%)
	Return Min(Floor((Float(MapWidth-y)/MapWidth*ZONEAMOUNT)),ZONEAMOUNT-1)
End Function

Function EraseMap()
	Grid_SelectedX=-1
	Grid_SelectedY=-1
	ChangeGridGadget = True
	GridGadgetText = ""
	
	Local hasEvent% = False
	item = SelectedGadgetItem( listbox )
    If item > - 1 Then
    	name$ = GadgetItemText$(listbox, item)
		For rt.RoomTemplates = Each RoomTemplates
			If rt\Name = name
				For i = 0 To 5
					If rt\events[i]<>""
						hasEvent = True
					EndIf
				Next
				Exit
			EndIf
		Next 
	EndIf
	
	If (Not hasEvent)
		DisableGadget combobox
		SetGadgetText event_desc, ""
		SetGadgetText event_prob_label, ""
		SetSliderValue event_prob,99
		DisableGadget event_prob
	Else
		SetSliderValue event_prob,99
		SetGadgetText event_prob_label,"�¼����ʣ�"+(SliderValue(event_prob)+1)+"%"
	EndIf
	
	For x = 0 To MapWidth
		For y = 0 To MapHeight
			Map(x,y)=Null
			MapAngle(x,y)=0
			MapEvent(x,y)=""
			MapEventProb(x,y)=0.0
		Next
	Next
	
	For x = 0 To ForestGridSize
		For y = 0 To ForestGridSize
			ForestPlace(x,y)=Null
			ForestPlaceAngle(x,y)=0
		Next
	Next
	
	For x = 0 To MT_GridSize
		For y = 0 To MT_GridSize
			MTRoom(x,y)=Null
			MTRoomAngle(x,y)=0
		Next
	Next
	
	zonetransvalue1 = 13
	zonetransvalue2 = 7
	SetGadgetText zonetrans1,5
	SetGadgetText zonetrans2,11
	MapAuthor$ = ""
	MapDescription$ = ""
	SetGadgetText map_author_text,""
End Function

Function LoadMap(file$)
	EraseMap()
	
	f% = ReadFile(file)
	DebugLog file
	
	If Right(file$,6)="cbmap2"
		MapAuthor$ = ReadLine(f)
		MapDescription$ = ReadLine(f)
		If MapAuthor$ = "[δ֪]" Then MapAuthor$ = ""
		If MapDescription$ = "[������]" Then MapDescription$ = ""
		SetGadgetText map_author_text,MapAuthor$
		SetGadgetText descr_text,MapDescription$
		zonetransvalue1 = ReadByte(f)
		zonetransvalue2 = ReadByte(f)
		SetGadgetText zonetrans1,(MapHeight)-zonetransvalue1
		SetGadgetText zonetrans2,(MapHeight)-zonetransvalue2
		Local roomamount = ReadInt(f) ;Amount of rooms
		Local forestamount = ReadInt(f) ;Amount of forest pieces
		Local mtroomamount = ReadInt(f) ;Amount of maintenance tunnel rooms
		
		;Facility rooms
		For i = 0 To roomamount-1
			x = ReadByte(f)
			y = ReadByte(f)
			name$ = ReadString(f)
			DebugLog x+", "+y+": "+name
			For rt.roomtemplates=Each RoomTemplates
				If Lower(rt\name) = name Then
					DebugLog rt\name
					Map(x,y)=rt
					Exit
				EndIf
			Next
			MapAngle(x,y)=ReadByte(f)*90
			MapEvent(x,y) = ReadString(f)
			If MapEvent(x,y)=""
				MapEvent(x,y)="[��]"
			EndIf
			MapEventProb(x,y) = ReadFloat(f)
			If MapEventProb(x,y)=0.0
				MapEventProb(x,y)=1.0
			EndIf
		Next
		;Forest pieces
		For i = 0 To forestamount-1
			x = ReadByte(f)
			y = ReadByte(f)
			name$ = ReadString(f)
			DebugLog x+", "+y+": "+name
			For rt.roomtemplates=Each RoomTemplates
				If Lower(rt\name) = name Then
					DebugLog rt\name
					ForestPlace(x,y)=rt
					Exit
				EndIf
			Next
			ForestPlaceAngle(x,y)=ReadByte(f)*90
		Next
		;Maintenance tunnel pieces
		For i = 0 To mtroomamount-1
			x = ReadByte(f)
			y = ReadByte(f)
			name$ = ReadString(f)
			DebugLog x+", "+y+": "+name
			For rt.roomtemplates=Each RoomTemplates
				If Lower(rt\name) = name Then
					DebugLog rt\name
					MTRoom(x,y)=rt
					Exit
				EndIf
			Next
			MTRoomAngle(x,y)=ReadByte(f)*90
		Next
	Else
		While Not Eof(f)
			x = ReadByte(f)
			y = ReadByte(f)
			name$ = ReadString(f)
			DebugLog x+", "+y+": "+name
			For rt.roomtemplates=Each RoomTemplates
				If Lower(rt\name) = name Then
					DebugLog rt\name
					Map(x,y)=rt
					Exit
				EndIf
			Next
			MapAngle(x,y)=ReadByte(f)*90
			MapEvent(x,y) = ReadString(f)
			If MapEvent(x,y)=""
				MapEvent(x,y)="[��]"
			EndIf
			MapEventProb(x,y) = ReadFloat(f)
			If MapEventProb(x,y)=0.0
				MapEventProb(x,y)=1.0
			EndIf
		Wend
	EndIf
	
	If ShowGrid=False
		SaveMap("CONFIG_MAPINIT.SI",True)
	EndIf
	
	CloseFile f
End Function

Function SaveMap(file$,streamtoprgm%=False,old%=0)
	f% = WriteFile(file)
	
	If old%=0 Then
		MapAuthor$ = TextFieldText(map_author_text)
		If Trim(MapAuthor$)=""
			WriteLine f,"[δ֪]"
		Else
			WriteLine f,MapAuthor$
		EndIf
		MapDescription$ = TextAreaText(descr_text)
		If Trim(MapDescription$)=""
			WriteLine f,"[������]"
		Else
			WriteLine f,MapDescription$
		EndIf
		WriteByte f,zonetransvalue1
		WriteByte f,zonetransvalue2
		;Facility room amount
		temp=0
		For x=0 To MapWidth
			For y=0 To MapHeight
				If Map(x,y)<>Null
					temp=temp+1
				EndIf
			Next
		Next
		WriteInt f,temp
		;Forest room amount
		temp=0
		For x=0 To ForestGridSize
			For y=0 To ForestGridSize
				If ForestPlace(x,y)<>Null
					temp=temp+1
				EndIf
			Next
		Next
		WriteInt f,temp
		;Maintenance tunnel room amount
		temp=0
		For x=0 To MT_GridSize
			For y=0 To MT_GridSize
				If MTRoom(x,y)<>Null
					temp=temp+1
				EndIf
			Next
		Next
		WriteInt f,temp
	EndIf
	
	If streamtoprgm
		WriteInt f,CurrMapGrid
	EndIf
	
	For x = 0 To MapWidth
		For y = 0 To MapHeight
			If Map(x,y)<>Null
				WriteByte f, x
				WriteByte f, y
				WriteString f, Lower(Map(x,y)\Name)
				WriteByte f, Floor(MapAngle(x,y)/90.0)
				If MapEvent(x,y)<>"[��]"
					WriteString f, MapEvent(x,y)
				Else
					WriteString f, ""
				EndIf
				WriteFloat f, MapEventProb(x,y)
				
				If streamtoprgm
					If Grid_SelectedX=x And Grid_SelectedY=y
						WriteByte f,1
					Else
						WriteByte f,0
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	If old%=0 Then
		For x = 0 To ForestGridSize
			For y = 0 To ForestGridSize
				If ForestPlace(x,y)<>Null
					WriteByte f, x
					WriteByte f, y
					WriteString f, Lower(ForestPlace(x,y)\Name)
					WriteByte f, Floor(ForestPlaceAngle(x,y)/90.0)
					
					If streamtoprgm
						If Grid_SelectedX=x And Grid_SelectedY=y
							WriteByte f,1
						Else
							WriteByte f,0
						EndIf
					EndIf
				EndIf
			Next
		Next
		
		For x = 0 To MT_GridSize
			For y = 0 To MT_GridSize
				If MTRoom(x,y)<>Null
					WriteByte f, x
					WriteByte f, y
					WriteString f, Lower(MTRoom(x,y)\Name)
					WriteByte f, Floor(MTRoomAngle(x,y)/90.0)
					
					If streamtoprgm
						If Grid_SelectedX=x And Grid_SelectedY=y
							WriteByte f,1
						Else
							WriteByte f,0
						EndIf
					EndIf
				EndIf
			Next
		Next
	EndIf
	
	CloseFile f
End Function

Function MilliSecs2()
	Local retVal% = MilliSecs()
	If retVal < 0 Then retVal = retVal + 2147483648
	Return retVal
End Function

Function WriteOptions()
	f = WriteFile("CONFIG_OPTINIT.SI")
	WriteInt f,redfog
	WriteInt f,greenfog
	WriteInt f,bluefog
	WriteInt f,redcursor
	WriteInt f,greencursor
	WriteInt f,bluecursor
	WriteInt f,TextFieldText$(CameraRange)
	WriteByte f,ButtonState(vsync)
	WriteByte f,ButtonState(showfps)
	WriteByte f,MenuChecked(adjdoor_place)
	CloseFile f
End Function
