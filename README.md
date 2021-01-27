# Neon-Tanks
Проект старый и создавался *для изучения* принципов ООП. Написан на языке Java (с использованием старой библиотеки Swing).</br>
Все функции отслеживания коллизий и реагирования врагов на игрока написаны *с нуля*, без использования различных библиотек.

## Содержание
- [Цель игры](#goal)
- [Краткое описание игрового процеса](#gameproc)
- [Танк игрока](#player)
- [Вражеский танк](#enemy)
- [Типы бонусов](#bonustypes)
  - [Двойной урон](#doubledmg)
  - [Силовой щит](#shield)
  - [Ускорение](#haste)
  - [Невидимость](#invisibility)
  - [Энергия](#energy)
- [Меню паузы](#pause)
- [Таблица счёта](#scoreboard)
- [Генерация карты](#mapgen)

<a name="goal"></a>
## Цель игры:
  Как можно дольше продержаться против вражеских танков, которые будут искать вас на карте. Со временем, этих танков будет становится всё больше (но не больше 10), поэтому лучше не только уворачиватся от выстрелов, а и самому атаковать.

<a name="gameproc"></a>
## Краткое описание игрового процесса:
  Во время игры, у вашего танка будет постепенно снижаться уровень энергии. Кроме этого, энергия будет снижаться (значительно) при попадании вражеским снарядом по вашему танку. Если энергия закончиться - вы проиграли. Время игры и набранные очки за уничтожение вражеских танков записываються в таблицу рекордов. Все рекорды сохраняются в отдельный файл под именем "data" который находится в директории "...\res\Scores". Поэтому, при следующем сеансе игры, прошлые результаты будут сохранены.
Меню игры выглядит следующим образом:
![Меню игры](/ReadMe_Assets/menu.gif)

Превью игрового процесса:

![Превью часть 1](/ReadMe_Assets/gameplay_part1.gif)
![Превью часть 2](/ReadMe_Assets/gameplay_part2.gif)


<a name="player"></a>
## Танк игрока
  Имеет 600 ед. энергии и урон в 50 ед. За один игровой тик (1/60 секунды) энергия игрока уменьшаеться на 0.05 ед. (3 ед. в секунду). Начальная скорость передвижения равна 2 пикселям/тик. Выглядит танк игрока так:
  
![Танк игрока в простое](/ReadMe_Assets/player_tank.jpg)
![Танк игрока в движении](/ReadMe_Assets/player_tank_moving.jpg)


Полученный бонус, время его действия и запасы энергии танка отображаються на специальной панели:

![Панель игрока](/ReadMe_Assets/player_panel.jpg)
![Панель игрока с бонусом](/ReadMe_Assets/player_panel_bonus.jpg)

Также при уничтожении вражеского танка игрок получает 10 очков, за обычный, и 20 очков за усиленный.
Все набранные очки отображаються на следующей панели:

![Панель счёта](/ReadMe_Assets/score_panel.jpg)

<a name="enemy"></a>
## Вражеский танк
  Появлються эти танки каждые 5 секунд (не больше 10 на карте одновременно) в верхней части карты. Первые 10 танков будут иметь 100 ед. энергии и 20 ед. урона, но дальше каждый новый танк будет иметь всё больше энергии и урона. Вычисляються они следующим образом:

уровень_энергии = 100 * (1 + id_танка / 10); </br>
урон = 20 * (1 + id_танка / 10);

Кроме этого каждый 5-ый танк будет в 2 раза быстрее (вместо 2 пикселей/тик будет 4).

<a name="bonustypes"></a>
## Типы бонусов:
  Всего в игре присутствуют 5 типов бонусов, частота появления которых равна 15 секундам, а время действия 10 секундам.
  
<a name="doubledmg"></a>
###  Двойной урон
  Название говорит само за себя. Подобрав этот бонус, танк игрока будет наносить вдвое больше урона.
Выглядит этот бонус так: </br>

![Двойной урон](/ReadMe_Assets/doubledmg.jpg) </br>

<a name="shield"></a>
### Силовой щит
  Этот бонус даст игроку неуязвимость к урону. Выглядит он следующим образом:</br>
  
![Щит](/ReadMe_Assets/shield.jpg) </br>

Подобрав этот бонус, танк игрока будет покрыт щитом:

![Танк игрока с щитом](/ReadMe_Assets/shield_tile.jpg) </br>

<a name="haste"></a>
### Ускорение
  Этот бонус ускоряет танк игрока в 2 раза (4 пикселя/тик). Выглядит он так:

![Ускорение](/ReadMe_Assets/haste.jpg)

<a name="invisibility"></a>
### Невидимость
  Даный бонус делает танк игрока невидимым для вражеских танков, что позволяет игроку без проблем уничтожать их. Выглядит следующим образом:

![Невидимость](/ReadMe_Assets/invisibility.jpg)

<a name="energy"></a>
### Энергия
  Этот бонус достаточно быстро восстанавливает энергию танка игрока (0.25 ед. за тик или 15 ед. в секунду). Выглядит бонус так:

![Энергия](/ReadMe_Assets/energy.jpg)


<a name="pause"></a>
## Меню паузы:
  Даное меню появляется при нажатии клавиши "ESCAPE" или при сворачивании окна с игрой. В этом меню можно начать игру сначала, регулировать громкость звука (к слову, эта настройка сохраняется в отдельном файле "volume" путь которого ".../res/Game/"), выйти из игры или вернуться в игру. Выглядит меню следующим образом:
  
![Меню паузы](/ReadMe_Assets/pause.gif)


<a name="scoreboard"></a>
## Таблица счёта:
  Как уже говорилось ранее, после проигрыша результаты игры сохраняются в отдельном файле и отображаются в таблице счёта. 
  
Окно проигрыша выглядит следующим образом: </br>
![Окно проигрыша](/ReadMe_Assets/gameover.jpg)

Записи в таблице счёта выглядят так: </br> 
![Таблица счёта](/ReadMe_Assets/scoreboard.jpg)


<a name="mapgen"></a>
## Генерация карты:
  Карта хранится в отдельном файле (файл "map", путь ".../res/Game/"), в виде символов. При запуске игры этот файл считывается и, начав сражение, карта отображается на экране уже блоками. Содержание файла "map":
```
XXXXXXXXXXXXXXXXX
X===============X
X=X==X=XXX=X====X
X=X=XX=X=X=XX=XXX
X=X=============X
X=X====X=X====X=X
X=X=X=======X=X=X
X=X=X=XX=XX=X=X=X
X===============X
X===============X
X=X=X=XX=XX=X=X=X
X=X=========X=X=X
X=X=XX=XXX====X=X
X=X=X==X=X=XXXX=X
X===X===========X
XXXXXXXXXXXXXXXXX
```

