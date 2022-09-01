.lib "opencc.dll"

CreateOpenCC%(configFileName$):"_CreateOpenCC@4"
OpenCConvert$(converter%, input$):"_OpenCConvert@8"
FreeOpenCC(converter%):"_FreeOpenCC@4"