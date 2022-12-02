; 收容失效终极版 重生版源码
; https://github.com/Jabka666/scpcb-ue-my/blob/no_opt/Source%20Code/Subtitles_Core.bb
Global EnableSubtitles% = GetINIInt(OptionFile, "options", "enable subtitles")
;[Block]
Const ANNOUNCEMENT% = 0
Const FIRST_PERSON% = 1
Const SECOND_PERSON% = 2
Const THIRD_PERSON% = 3
;[End Block]

Type Subtitles
	Field Txt$[THIRD_PERSON + 1]
	Field Timer#[THIRD_PERSON + 1]
End Type

Function UpdateSubtitles%()
	If (Not EnableSubtitles) Then Return
	
	Local sub.Subtitles
	Local ShouldDeleteSubtitles% = True, i%
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					ShouldDeleteSubtitles = False
					sub\Timer[i] = sub\Timer[i] - FPSfactor2
				EndIf
			Next
			If ShouldDeleteSubtitles Then Delete(sub)
		EndIf
	Next
End Function

Function RenderSubtitles%()
	If (Not EnableSubtitles) Then Return
	
	Local sub.Subtitles
	Local i%
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					SetFont Font1
					Color(255,255,255)
					Text((GraphicWidth / 2), (GraphicHeight / 2) + 200 + FontHeight() + 8 + i*FontHeight()*2, sub\Txt[i], True, False)
				EndIf
			Next
		EndIf
	Next
End Function

Const SubtitlesFile$ = "Data\subtitles.ini"

Function ShowSubtitles%(Name$)
	CatchErrors("Uncaught (ShowSubtitles)")
	
	If (Not EnableSubtitles) Then Return
	
	Local sub.Subtitles, CurrSub.Subtitles
	Local Loc% = GetINISectionLocation(SubtitlesFile, Name)
	Local Person% = GetINIString2(SubtitlesFile, Loc, "Person")
	Local LinesAmount% = GetINIInt2(SubtitlesFile, Loc, "LinesAmount")
	Local SubID%, i%
	
	Select Person
		Case 1
			SubID = FIRST_PERSON
		Case 2
			SubID = SECOND_PERSON
		Case 3
			SubID = THIRD_PERSON
		Default
			SubID = ANNOUNCEMENT
	End Select
	
	For sub.Subtitles = Each Subtitles
		If sub\Txt[SubID] = "" Then
			CurrSub.Subtitles = sub.Subtitles
			Exit
		EndIf
	Next
	
	For i = 1 To LinesAmount
		If CurrSub = Null Then
			sub.Subtitles = New Subtitles
		Else
			sub.Subtitles = CurrSub.Subtitles
		EndIf
		sub\Txt[SubID] = GetINIString2(SubtitlesFile, Loc, "Txt" + i)
		sub\Timer[SubID] = 70.0 * GetINIFloat2(SubtitlesFile, Loc, "Timer" + i)
	Next
	
	CatchErrors("ShowSubtitles")
End Function