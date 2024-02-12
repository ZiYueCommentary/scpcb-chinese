Function LoadAllSounds()
	For i = 0 To 2
		OpenDoorSFX(0,i) = LoadSound_Strict("SFX\Door\DoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(0,i) = LoadSound_Strict("SFX\Door\DoorClose" + (i + 1) + ".ogg")
		OpenDoorSFX(2,i) = LoadSound_Strict("SFX\Door\Door2Open" + (i + 1) + ".ogg")
		CloseDoorSFX(2,i) = LoadSound_Strict("SFX\Door\Door2Close" + (i + 1) + ".ogg")
		OpenDoorSFX(3,i) = LoadSound_Strict("SFX\Door\ElevatorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(3,i) = LoadSound_Strict("SFX\Door\ElevatorClose" + (i + 1) + ".ogg")
	Next
	For i = 0 To 1
		OpenDoorSFX(1,i) = LoadSound_Strict("SFX\Door\BigDoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(1,i) = LoadSound_Strict("SFX\Door\BigDoorClose" + (i + 1) + ".ogg")
	Next
	
	KeyCardSFX1 = LoadSound_Strict("SFX\Interact\KeyCardUse1.ogg")
	KeyCardSFX2 = LoadSound_Strict("SFX\Interact\KeyCardUse2.ogg")
	ButtonSFX2 = LoadSound_Strict("SFX\Interact\Button2.ogg")
	ScannerSFX1 = LoadSound_Strict("SFX\Interact\ScannerUse1.ogg")
	ScannerSFX2 = LoadSound_Strict("SFX\Interact\ScannerUse2.ogg")
	
	OpenDoorFastSFX=LoadSound_Strict("SFX\Door\DoorOpenFast.ogg")
	CautionSFX% = LoadSound_Strict("SFX\Room\LockroomSiren.ogg")
	
	 ;NuclearSirenSFX%
	
	CameraSFX = LoadSound_Strict("SFX\General\Camera.ogg") 
	
	StoneDragSFX% = LoadSound_Strict("SFX\SCP\173\StoneDrag.ogg")
	
	GunshotSFX% = LoadSound_Strict("SFX\General\Gunshot.ogg")
	Gunshot2SFX% = LoadSound_Strict("SFX\General\Gunshot2.ogg")
	Gunshot3SFX% = LoadSound_Strict("SFX\General\BulletMiss.ogg")
	BullethitSFX% = LoadSound_Strict("SFX\General\BulletHit.ogg")
	
	TeslaIdleSFX = LoadSound_Strict("SFX\Room\Tesla\Idle.ogg")
	TeslaActivateSFX = LoadSound_Strict("SFX\Room\Tesla\WindUp.ogg")
	TeslaPowerUpSFX = LoadSound_Strict("SFX\Room\Tesla\PowerUp.ogg")
	
	MagnetUpSFX% = LoadSound_Strict("SFX\Room\106Chamber\MagnetUp.ogg") 
	MagnetDownSFX = LoadSound_Strict("SFX\Room\106Chamber\MagnetDown.ogg")
	
	For i = 0 To 3
		DecaySFX(i) = LoadSound_Strict("SFX\SCP\106\Decay" + i + ".ogg")
	Next
	
	BurstSFX = LoadSound_Strict("SFX\Room\TunnelBurst.ogg")
	
	For i = 0 To 2
		RustleSFX(i) = LoadSound_Strict("SFX\SCP\372\Rustle" + i + ".ogg")
	Next
	
	Death914SFX% = LoadSound_Strict("SFX\SCP\914\PlayerDeath.ogg") 
	Use914SFX% = LoadSound_Strict("SFX\SCP\914\PlayerUse.ogg")
	
	For i = 0 To 3
		DripSFX(i) = LoadSound_Strict("SFX\Character\D9341\BloodDrip" + i + ".ogg")
	Next
	
	LeverSFX% = LoadSound_Strict("SFX\Interact\LeverFlip.ogg") 
	LightSFX% = LoadSound_Strict("SFX\General\LightSwitch.ogg")
	
	ButtGhostSFX% = LoadSound_Strict("SFX\SCP\Joke\789J.ogg")
	
	RadioSFX(1,0) = LoadSound_Strict("SFX\Radio\RadioAlarm.ogg")
	RadioSFX(1,1) = LoadSound_Strict("SFX\Radio\RadioAlarm2.ogg")
	For i = 0 To 8
		RadioSFX(2,i) = LoadSound_Strict("SFX\Radio\scpradio"+i+".ogg")
	Next
	RadioSquelch = LoadSound_Strict("SFX\Radio\squelch.ogg")
	RadioStatic = LoadSound_Strict("SFX\Radio\static.ogg")
	RadioBuzz = LoadSound_Strict("SFX\Radio\buzz.ogg")
	
	ElevatorBeepSFX = LoadSound_Strict("SFX\General\Elevator\Beep.ogg") 
	ElevatorMoveSFX = LoadSound_Strict("SFX\General\Elevator\Moving.ogg") 
	
	For i = 0 To 3
		PickSFX(i) = LoadSound_Strict("SFX\Interact\PickItem" + i + ".ogg")
	Next
	
	;0 = light containment, 1 = heavy containment, 2 = entrance
	AmbientSFXAmount(0)=11 : AmbientSFXAmount(1)=11 : AmbientSFXAmount(2)=12
	;3 = general, 4 = pre-breach
	AmbientSFXAmount(3)=15 : AmbientSFXAmount(4)=5
	;5 = forest
	AmbientSFXAmount(5)=10
	
	For i = 0 To 2
		OldManSFX(i) = LoadSound_Strict("SFX\SCP\106\Corrosion" + (i + 1) + ".ogg")
	Next
	OldManSFX(3) = LoadSound_Strict("SFX\SCP\106\Laugh.ogg")
	OldManSFX(4) = LoadSound_Strict("SFX\SCP\106\Breathing.ogg")
	OldManSFX(5) = LoadSound_Strict("SFX\Room\PocketDimension\Enter.ogg")
	For i = 0 To 2
		OldManSFX(6+i) = LoadSound_Strict("SFX\SCP\106\WallDecay"+(i+1)+".ogg")
	Next
	
	For i = 0 To 2
		Scp173SFX(i) = LoadSound_Strict("SFX\SCP\173\Rattle" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 11
		HorrorSFX(i) = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	For i = 14 To 15
		HorrorSFX(i) = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	
	For i = 7 To 9
		IntroSFX(i) = LoadSound_Strict("SFX\Room\Intro\Bang" + (i - 6) + ".ogg")
	Next
	For i = 10 To 12
		IntroSFX(i) = LoadSound_Strict("SFX\Room\Intro\Light" + (i - 9) + ".ogg")
	Next
	IntroSFX(15) = LoadSound_Strict("SFX\Room\Intro\173Vent.ogg")
	
	AlarmSFX(0) = LoadSound_Strict("SFX\Alarm\Alarm.ogg")
	AlarmSFX(2) = LoadSound_Strict("SFX\Alarm\Alarm3.ogg")
	
	;room_gw alarms
	AlarmSFX(3) = LoadSound_Strict("SFX\Alarm\Alarm4.ogg")
	AlarmSFX(4) = LoadSound_Strict("SFX\Alarm\Alarm5.ogg")
	
	HeartBeatSFX = LoadSound_Strict("SFX\Character\D9341\Heartbeat.ogg")
	
	For i = 0 To 4
		BreathSFX(0,i)=LoadSound_Strict("SFX\Character\D9341\breath"+i+".ogg")
		BreathSFX(1,i)=LoadSound_Strict("SFX\Character\D9341\breath"+i+"gas.ogg")
	Next
	
	For i = 0 To 2
		NeckSnapSFX(i) =  LoadSound_Strict("SFX\SCP\173\NeckSnap"+(i+1)+".ogg")
	Next
	
	For i = 0 To 8
		DamageSFX(i) = LoadSound_Strict("SFX\Character\D9341\Damage"+(i+1)+".ogg")
	Next
	
	For i = 0 To 2
		CoughSFX(i) = LoadSound_Strict("SFX\Character\D9341\Cough" + (i + 1) + ".ogg")
	Next
	
	 MachineSFX% = LoadSound_Strict("SFX\SCP\914\Refining.ogg")
	
	 ApacheSFX = LoadSound_Strict("SFX\Character\Apache\Propeller.ogg")
	
	For i = 0 To 7
		StepSFX(0, 0, i) = LoadSound_Strict("SFX\Step\Step" + (i + 1) + ".ogg")
		StepSFX(1, 0, i) = LoadSound_Strict("SFX\Step\StepMetal" + (i + 1) + ".ogg")
		StepSFX(0, 1, i)= LoadSound_Strict("SFX\Step\Run" + (i + 1) + ".ogg")
		StepSFX(1, 1, i) = LoadSound_Strict("SFX\Step\RunMetal" + (i + 1) + ".ogg")
		If i < 3
			StepSFX(2, 0, i) = LoadSound_Strict("SFX\Character\MTF\Step" + (i + 1) + ".ogg")
			StepSFX(3, 0, i) = LoadSound_Strict("SFX\SCP\049\Step"+ (i + 1) + ".ogg")
		EndIf
		If i < 4
	        StepSFX(4, 0, i) = LoadSound_Strict("SFX\Step\SCP\StepSCP" + (i + 1) + ".ogg");new one 1.3.9
	    EndIf
	Next
	
	For i = 0 To 2
		Step2SFX(i) = LoadSound_Strict("SFX\Step\StepPD" + (i + 1) + ".ogg")
		Step2SFX(i+3) = LoadSound_Strict("SFX\Step\StepForest" + (i + 1) + ".ogg")
	Next 
End Function