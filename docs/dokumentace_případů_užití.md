## Dokumentace případů užití
Aplikace obsahuje následující funkce a případy užití

### Offline-first synchronizace dat
- Aplikace používá Google Firebase backend a GitLive Firebase SDK pro autentizaci uživatele a synchronizaci dat
- Většina funkcí aplikace je na mobilních zařízeních proveditelná bez připojení k internetu
- Na desktopových zařízeních GitLive Firebase SDK nepoporuje kešování dat a připojení k internetu je potřeba alespoň pro přihlášení
- Uživatel musí být připojený k internetu během mazání uživatelského účtu
- Bez připojení k internetu může uživatel provádět následující akce:
- spustit a zaznamenat neplánovaný trénink
- vytvořit vlastní cvik
- vyvořit vlastní tréninkový plán
- zaznamenat měření těla
- spustit a dokončit tréninkový plán s progresivním přetížením
- odhlásit se
- procházet záznamy měření, tréninků a cviků v offline databázi
- mazat záznamy měření, tréninků a cviků v offline databázi. Při obnovení připojení se data synchronizují s online databází

# Autentizace
##### Registrace uživatele
- Před použitím aplikace se uživatel musí registrovat pomocí emailu a hesla
- Uživatel může, ale nemusí ověřit svoji emailovou adresu. Na email mu bude odeslán ověřovací odkaz

##### Přihlášení uživatele
- uživatel se může přihlásit pomocí emailu a hesla
- Pokud uživatel zapomene heslo, může požádat o jeho obnovení. Na email mu bude odeslán odkaz na resetování hesla

##### Smazání účtu uživatele
- Uživatel může smazat svůj účet v záložce Nastavení
- Během smazání účtu jsou smazána všechna data uživatele (záznamy tréninku, plány, měření atd.)
- Uživatel může požádat a smazání účtu mimo aplikaci (https://jakubherr.github.io/)


# Plánování tréninků

#### Tvorba tréninkových plánů
- Uživatel může vytvářet vlastní tréninkové plány
- Plán se skládá z tréninkových dní
- Tréninkový den se skládá z bloků, které obsahují cvik a série
- Uživatel může přejmenovat tréninkový den
- Uživatel může spustit záznam tréninku, který je kopií tréninkového dne z plánu
- Uživatel může smazat tréninkový den z plánu nebo celý plán

#### Základní progresivní přetížení
- Uživatel může definovat nastavení, které bude postupem času zvyšovat náročnost tréninkového plánu
- Progresivní přetížení lze nastavit zvlášť pro každý blok s cvikem
-  Nastavení přetížení je založeno na zvyšované hodnotě, prahu pro zvýšení a inkrementu
- zvyšovaná hodnota je buď váha nebo počet opakování
- práh pro zvýšení je minimální váha a počet opakování, které je potřeba dosáhnout v každé sérii cviku
- inkrement je hodnota, o kterou se zvýší váha nebo počet opakování
- nastavení progresivního přetížení je volitelnou součástí tvorby tréninkového plánu
- Aby progresivní přetížení fungovalo, musí uživatel spustit záznam tréninku z tréninkového plánu
- Pokud uživatel během záznamu tréninku splní podmínky progrese, tréninkový plán je automaticky upraven na základě nastavení

### Přizpůsobení
- uživatel může vytvářet vlastní cviky
- Vlastní cvik se skládá z názvu a primárních a sekundárních svalů

### Záznam tréninku
- Uživatel může spustit záznam neplánovaného tréninku, nebo spustit záznam tréninkového dne z plánu
- Uživatel může do záznamu přidat vlastní nebo předdefinovaný cvik
- Uživatel může odstranit cvik ze záznamu
- Uživatel může k cviku přidat série a zaznamenat váhu a počet opakování
- Uživatel může odstranit poslední sérii cviku
- Uživatel může zahodit probíhající záznam cvičení
- Uživatel může uložit dokončený záznam do databáze

### Měření těla
- Uživatel může do aplikace zaznamenávat měření těla
- historický vývoj měření je znázorněn na grafu
- uživatel může procházet všechny záznamy měření
- uživatel může smazat záznamy měření

### Vizualizace dat pomocí grafů
- Uživatel může sledovat historický vývoj měření a zaznamenaných cviků
- grafy cviků se nachází v detailu cviku, který je přístupný ze záložky Historie
- graf měření se nachází v záložce Měření

# Validace vstupů
- Všechna data zadaná uživatelem musí být zvalidována
- Textový vstup je obecně limitován na 20 znaků, s výjimkou textových polí pro zadání emailu a hesla

#### Tréninkový plán
- Název tréninku nesmí být prázdný
- Tréninkový plán musí obsahovat alespoň jeden tréninkový den
- Název tréninkového dne nesmí být prázdný
- Každý tréninkový den musí mít alespoň jeden blok s cvikem
- každý blok musí obsahovat alespoň jednu neprázdnou sérii
- každá série musí obsahovat platné hodnoty pro váhu a počet opakování
- platná váha je nezáporné celé nebo desetinné číslo, s maximální přesností na setiny
- platný počet opakování je nezáporné celé číslo (0 je platný vstup pro případ, kdy chce uživatel zaznamenat nepodařený cvik)

### Meření
- V současnosti jsou všechna měření v centimetrech a kilogramech
- platné měření je nezáporné celé nebo desetinné číslo, s maximální přesností na setiny
- V současnosti je potřeba pro uložení záznamu měření vyplnit všechny hodnoty

### Navigace
- pokud při spuštení aplikace uživatel není přihlášený, zobrazí se přihlašovací obrazovka
- (pouze na mobilu) Pokud uživatel je přihlášený, zobrazí se obrazovka "Přehled"
- uživatel může navigovat mezi hlavními destinacemi aplikace pomocí navigačních tlačítek
- když se uživatel snaží odejít z obrazovky s neuloženými daty (např. tvorba tréninkového plánu), musí tuto akci potvrdit