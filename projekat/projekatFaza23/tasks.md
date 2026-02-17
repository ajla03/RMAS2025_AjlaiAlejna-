## Evidencija funkcionalnosti projekta

---
#### Ajla Bulić:

> **Rad na ekranima za Zaposlenika (Employee)**
> * Napravila sam početne ekrane (`ClientHomeScreen`) i `NewRequestScreen`.
> * Dodala sam filtriranje zahtjeva pomoću padajućeg menija (`DropDownMenu`).
> * Napravila funkcionalnost za biranje datuma i dodavanje fajlova uz zahtjev.
> * Ubacila animacije za greške (npr. ako se izabere pogrešan datum) i ograničenja za tipove odsustva.
> * Sredila izgled Login ekrana i dodala popup za profil i odjavu.

> **Baza podataka**
> * Kreirala `LeaveRepository` i povezala aplikaciju sa **Firebase Firestore** bazom.
> * Napravila `RequestType` (enum) da se tačno zna koji su tipovi odsustva (plaćeno/neplaćeno) i koliko dana je dozvoljeno.
> * Sredila `ViewModel`-e (`LeaveRequestViewModel`, `InboxRequestViewModel`) da pravilno upravljaju podacima.
> * Povezala **Firebase Storage** tako da se dokumenti koje korisnik pošalje stvarno čuvaju na serveru i mogu se otvoriti putem linka.

> **Dio za Dekana i Sekretara**
> * **Dekan:**
>   * Napravila ekrane za Dekana (`DeanHomeScreen`, `DeanDirectoryScreen`, `ApproveRequestScreen`).
>   * Dodala navigaciju (Bottom Bar) i filtriranje zahtjeva po imenu i datumu.
>   * Napravila da Dekan može vidjeti dokumente, odobriti ili odbiti zahtjev.
>   * **PDF Export:** Napravila funkcionalnost da Dekan može spasiti listu zaposlenih u PDF formatu.
> * **Sekretar:**
>   * Napravila ekrane za Sekretara gdje on prvi pregleda zahtjeve.
>   * Dodala navigaciju (Bottom Bar) i filtriranje zahtjeva po imenu.
>   * Dodala opciju da Sekretar može promijeniti/korigovati datume odsustva prije nego što ih pošalje Dekanu.

> **Dodatne funkcionalnosti**
> * **Historija zahtjeva:** Napravila `HistoryScreen` gdje Dekan i Sekretar mogu vidjeti stare, završene zahtjeve.
> * **Notifikacije:** Implementirala `WorkManager` koji šalje notifikacije (podsjetnike) korisniku pred kraj odmora.
> * **Tok statusa:** Sredila logiku kako se status mijenja: prvo je `Pending`, pa kad sekretar pregleda ide u `Reviewed`, i na kraju Dekan stavlja `Approved`.

### Alejna Hodzic:
> 1. organizacija projekta:
>   - paketa
>   - fajlova - razdvajanje UI Statea od ViewModela
>   - odredjenih funkcija

> 2. navigacija projekta
>   - dodan sealed interface Screen
>   - implementirana AppNavigation funkcija (wrappan NavHost)
>   - onemogucen povratak na login screen nakon prijave
>   - izmjena potpisa funkcija za prikazivanje ekrana i prosljiedjena lambda za navigaciju

> 3. projekt dodan na Google CLoud \ Firebase
>   - dodan config file u projekt za google servis

> 4. implementacija Google autentikacije

> 5. dodan UserManager i UserProfile

> 6. ispravljena potpuna funkcionalnost LoginScreen-a
>   - ViewModelu dodana funkcija za `GoogleSingIn` 
>   - prepravljene Compose funkcije

> 7. dodan logo i izmjenjen homescreen izgled

> 8. bugfixes

> 9. prepravljena data klasa `LeaveRepository` i fileovi koji su ju koristili

> 10. pripremila Retfofit, okHttpClient, interface i
> repozitorij za poziv ka apiju - turns out nije nam 
> ni trebalo :/

> 11. dodana offline database support koristeci 
> firebase 
