MAS2025_AjlaiAlejna
Repozitorij za predmet Razvoj mobilnih aplikacija i servisa - RMAS 2025

### Commiti - Alejna 

### Commiti - Ajla 
* Initial commit - kreiran inicijalni `ClientHomeScreen.kt`,  `FakeLeaveRepository.kt`, `LeaveRequestViewModel.kt`  koje je kolegica Alejna doradila i rasporedila u  fajlove
* Dodan `DropDownMenu` u `ClientHomeScreen.kt` i implementacija filtriranja u `LeaveRequestViewModel.kt` kako bi se omogucila funkcionalnost filtera requesta
* dodan `NewRequestScreen.kt`
* fixan `NewRequestScreen.kt` , dodana nova polja u `LeaveRequest.kt` tj u `data class LeaveRequest`. Dodane nove funkcionalnosti u `LeaveRequestViewModel.kt` koje handluju promjenu polja za opis , datume i tip requesta. U `LeaveUIState` dodan `currentRequest : LeaveRequest` kao atribut kojem se cuva stanje u state-u tj. viewModelu 
* U `NewRequestScreen.kt` dodane Composable funkcije za date picking i attaching files. 
*Dodan `LoginScreen.kt` `LoginUIState.kt` 
* dodan Repository `LeaveRepository.kt` , update-an `InboxRequestViewModel.kt` u skladu sa tim i dodana veza na Firebase Firestore 
