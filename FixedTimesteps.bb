Global ft.FixedTimesteps = New FixedTimesteps

Type FixedTimesteps
	Field tickDuration#
	Field accumulator#
	Field prevTime%
	Field currTime%
	Field fps%
	Field tempfps%
	Field fpsgoal%
	Field DeltaTime%
End Type

Function SetTickrate(tickrate%)
	ft\tickDuration = 70.0/Float(tickrate)
End Function

Function AddToTimingAccumulator(milliseconds%)
	If (milliseconds<1 Lor milliseconds>500) Then
		Return
	EndIf
	ft\accumulator = ft\accumulator+Max(0,Float(milliseconds)*70.0/1000.0)
End Function

Function ResetTimingAccumulator()
	ft\accumulator = 0.0
End Function

Function SetCurrTime(time%)
	CurTime = time%
End Function

Function SetPrevTime(time%)
	PrevTime = time%
End Function

Function GetCurrTime%()
	Return CurTime
End Function

Function GetPrevTime%()
	Return PrevTime
End Function

Function GetTickDuration#()
	Return ft\tickDuration
End Function

SetTickrate(60)