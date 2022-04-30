Graphics 640,480,0,2
SetBuffer BackBuffer()

SetFont LoadFont("GFX\font\Containment Breach.ttf", 15)

Cls

Color 255,255,255
Text 0,5,"这个程序用来显示你的CPU信息（它一开始在调试界面显示）。"
Color 255,0,0
Text 0,25,"警告：该程序不能在所有计算机上正常显示，"
Text 0,45,"因此该程序从游戏本体中删除。"
Color 255,255,255
Text 0,85,"按任意键继续"

Flip

WaitKey()

Global kCPUid$, kCPUfamily%, kCPUsteppingId%, kCPUbrand$, kCPUextendedId$, kCPUfeatures$

kCPUid$         = CPUid$()
kCPUfamily%     = CPUfamily%()
kCPUsteppingId% = CPUsteppingId%()
kCPUbrand$      = CPUbrand$()
kCPUextendedId$ = CPUextendedId$()
kCPUfeatures$   = CPUfeatures$()

Repeat
	Cls
	Color 255,255,255
	Text 0,0,LSet("CPU ID: ",15)+kCPUid
	Text 0,20,LSet("CPU 系列: ",18)+kCPUfamily
	Text 0,40,LSet("CPU 步进ID: ",18)+kCPUsteppingId
	Text 0,60,LSet("CPU 品牌: ",18)+kCPUbrand
	Text 0,80,LSet("CPU 名称: ",18)+kCPUextendedId
	Text 0,100,LSet("CPU 特征: ",18)+kCPUfeatures
	Text 0,140,"按任意键关闭程序"
	Flip
	Delay 8
Until WaitKey()
End
;~IDEal Editor Parameters:
;~C#Blitz3D