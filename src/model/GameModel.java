package model;

import game.*;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static model.TextureAtlas.*; // підключаємо всі статичні поля та методи класа TextureAtlas з пакету model

/*
    Оголошуємо клас GameModel у якому буде знаходитись "ігровий" цикл, а також
    поля та методи для роботи з графікою та логікою гри
 */
public class GameModel {
    private JFrame frame; // оголошуємо об'єкт, що буде відповідати за відображення вікна
    private Display display; // оголучшаємо об'єкт, що буде відповідати за відображення зображення на якому,
                            // вже намальований один "ігровий" кадр. Сам об'єкт display повинен знаходитись всередині
                            // об'єкту frame інакше на екрані ми нічого не побачимо
    private boolean running = false; // оголошуємо булеву змінну, що визначає чи виконується "ігровий" цикл
    private final double UPDATE_TIME = 1_000_000_000.0 / 60.0; // оголошуємо константу, число з плаваючою комою подвійної точності,
                                                                // що визначає проміжок часу між оновленням "ігрової" логіки
    private BufferedImage scene; // оголошуємо об'єкт зображення на якому будемо малювати графічні елементи гри (аналогія - полотно)
                                // власне це саме те зображення, що буде відображатись на екрані за допомогою об'єкта display
    private Graphics2D sceneGraphics; // оголошуємо об'єкт за допомогою якого будемо малювати "ігрову" графіку на зображені scene (аналогія - пензлик)
    private Graphics2D displayGraphics; // оголошуємо об'єкт за допомогою якого будемо малювати зображення scene на об'єкті display
    private GameInputListener gameInputListener; // оголошуємо об'єкт для роботи з клавіатурою, а саме для перевірки на нажимання певних клавіш
    private final int WIDTH = 1920, HEIGHT = 1080; // оголошуємо цілочисельні константи, що визначають розміри сцени
    private int alignX, alignY; // оголошення цілочисельних змінних для вирівнюваня положення зображень (танків, блоків і т.д.) під час гри
                                // щоб вони відображалися в рамці
    private Game game; // оголушуємо об'єкт, що відповідає за ігровий процес
    private boolean drawnOnce = false; // оголошуємо і ініціалізуємо булеву змінну, що визначає чи були намальовані графічні елемети,
                                        // які потрібно намалювати лише один раз. Наприклад: рамка та блоки (стіни) оскільки вони не змінюють свого стану

    private GraphicsDevice device; // об'єкт конфігурації дисплею

    // описуємо конструктор класу GameModel, який приймає три аргументи: 1 - заголовок вікна, 2 - чи буде відображатися рамка вікна, 3 - колір фону
    public GameModel(String title, boolean undecorated, int bgColor){
        frame = new JFrame(title); // ініціалізуємо об'єкт frame (аргумент конструктора це рядок, що відображатиметься в заголовку вікна)
        display = new Display(); // ініціалізуємо об'єкт display
        display.setState(DisplayStates.MENU); // задаємо об'єкту display стан MENU (це константа перерахування DisplayStates),
                                                // тобто як тільки ми запустимо гру буде відображатися меню гри
        display.setBackground(new Color(bgColor)); // встановлюємо колір фону
        frame.getContentPane().add(display); // за допомогою метода getContentPane() отримуємо контейнер об'єкта frame
                                            // (об'єкт класа Container) і за допомогою метода add(), який викликаємо
                                            // у цього контейнера (тобто у об'єкта класа Container), додаємо display у вікно frame
        frame.setUndecorated(undecorated); // визначаємо чи буде наше вікно з рамкою (якщо undecorated == true, то вікно буде без рамки, інакше - з рамкою)
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]; // отримуємо конфігурацію дисплею

        frame.setVisible(true); // робимо вікно видимим, тобто відображаємо його на екрані
        device.setFullScreenWindow(frame); // встановлюємо вікно frame в повноекранний режим
        //............ приховуємо курсор.............................................
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), "blank cursor");
        //............................................................................
        frame.getContentPane().setCursor(blankCursor);
        frame.setResizable(false);
        display.setSize(frame.getWidth(), frame.getHeight()); // встановлюємо розміри дисплею
        frame.setLocationRelativeTo(null); // встановлюэємо вікно по центру екрана
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // встановлюємо стандартну операцію для події натискання на "хрестик"
        scene = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB); // ініціалізуємо зображення на якому будемо малювати
        sceneGraphics = (Graphics2D) scene.getGraphics(); // ініціалізуємо об'єкт графіки за допомогою якого ми будемо малювати на зображені scene

        displayGraphics = (Graphics2D) display.getGraphics(); // ініціалізуємо об'єкт графіки за допомогою якого будемо малювати на дисплеї вже готовий "ігровий" кадр
        addInputListener(); // додаємо об'єкт для роботи з клавіатурою у вікно

        alignX = 417; // встановлюємо значеня вирівнювання по осі Х
        alignY = 31; // встановлюємо значеняв ирівнювання по осі У
        Menu.init(); // ініціалізуємо меню (точніше завантажуємо усі зображення, що будуть використовуватись для відображення меню)
        Scores.init(); // ініціалізуємо ігрові рекорди (точніше завантажуємо з файлу всі попередні рекорди)
        Game.initVolume(); // ініціалізуємо значення гучності звуків (точніше завантажуємо з файлу збережене значення гучності з попереднього разу)

        displayGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); // встановлюємо високу якість відображення
        displayGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // вмикаємо згладжування
    }

    // метод в якому знаходиться "ігровий" цикл
    public void run(){
        if(running) return; // якщо "ігровий" цикл вже винконується, то виходимо з методу

        running = true; // якщо ж цикл не виконується запущений, то вказуємо,  що тепер він виконується
        long lastTime = System.nanoTime(); // отримуємо системний час
        long elapsedTime = 0; // тут зберігатиметься пройдений час між оновленями "ігрової" логіки
        long now; // тут будемо отримувати теперішній систмений час тобто після кожної ітерації циклу

        while(running){ // доки булева змінна running == true, то "ігровий" цикл буде виконуватись
            now = System.nanoTime(); // отримуємо системний час
            elapsedTime += now - lastTime; // вираховуємо пройдений проміжок часу після останьої ітераціїї і додаємо його до пройденого часу, тобто
                                            // віднімаємо час який був збережений на минулій ітерації від теперішнього часу і додаємо до змінної elapsedTime
            lastTime = now; // зберігаємо теперішній час для використання на наступній ітерації

            if(elapsedTime >= UPDATE_TIME){ // якщо пройдений час elapsedTime більше або рівний часу між оновленями "ігрової" логіки,
                                            // то виконуємо оновленя "ігрової" логіки
                update(); // даний метод оновлює "ігрову" логіку, тобто виконує  зміни координат, перевіряє зіткнення і т.д.
                elapsedTime -= UPDATE_TIME; // віднімаємо від пройденого часу час одного оновлення, оскільки саме стільки повинно було пройти часу,
                                            // щоб зробити одне оновлення, якщо ж пройшло більше часу, то враховуємо це вже як початок нового проміжку часу
                render(); // малюємо "ігрову" графіку (танки, кнопки, рахунок, прямкутники, лінії і т.д.)
            }

        }
    }

    // метод в якому знаходиться "ігрова" логіка (перевірки натискання клавіш, перевірки зіткнень об'єктів і т.д.)
    private void update(){
        switch (display.getState()) { // перевіряємо, який стан у дисплея (може бути MENU, SCORES, PAUSE, GAME, GAME_OVER)
            case MENU: // якщо стан дисплею MENU, то виконуємо код нижче
                menuChoice(); // викликаємо метод для вибору одного з пунктів меню

                break; // виходимо з блоку switch

            case RECORDS: // якщо стан дисплею RECORDS, то виконуємо код нижче
                if (gameInputListener.getKey(KeyEvent.VK_ESCAPE)) { // якщо натиснули клавішу ESC на клавіатурі, то
                    gameInputListener.setKeyReleased(KeyEvent.VK_ESCAPE); // встановюємо кнопку ESC як відпущену (для того щоб не було зациклення натискання)
                    display.setState(DisplayStates.MENU); // встановлюємо стан дисплею в меню (тобто повертаємось в меню)
                }

                break; // виходимо з блоку switch

            case GAME: // якщо стан дисплею GAME, то виконуємо код нижче
                Player player = game.player(); // отримуємо об'єкт класу Player, що знаходиться в об'єкті game
                                                // (іншими словами зберігаємо об'єкт "гравець" для подальшого використання)

                // перевіряємо чи натиснуті клавіші клавіатури (одночасно) вверх та вліво (тобто стрілки вверх та вліво)
                if (gameInputListener.getKey(KeyEvent.VK_UP) && gameInputListener.getKey(KeyEvent.VK_LEFT)){
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вверх
                    if (game.isMovingObjectCollision(player, Direction.UP, player.getSpeed()) == -2){
                        // isMovingObjectCollision() повертає -2 якщо зіткненя немає
                        player.move(Direction.UP); // якщо зіткнення немає, то рухаємо гравця вверх (тобто танк гравця)
                    } // якщо ж зіткненя в напрямку вверх є, то перевіряємо чи немає його в напрямку вліво
                    else if (game.isMovingObjectCollision(player, Direction.LEFT, player.getSpeed()) == -2){
                        player.move(Direction.LEFT); // якщо зіткнення немає, то рухаємо гравця вліво (тобто танк гравця)
                    }
                } // якщо клавіші вверх та вліво не натиснуті (одночасно), то перевіряємо чи натиснуті стрілки вверх та вправо
                else if (gameInputListener.getKey(KeyEvent.VK_UP) && gameInputListener.getKey(KeyEvent.VK_RIGHT)){
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вверх
                    if (game.isMovingObjectCollision(player, Direction.UP, player.getSpeed()) == -2){
                        player.move(Direction.UP); // якщо зіткнення немає, то рухаємо гравця вверх (тобто танк гравця)
                    } // якщо ж зіткненя в напрямку вверх є, то перевіряємо чи немає його в напрямку вправо
                    else if (game.isMovingObjectCollision(player, Direction.RIGHT, player.getSpeed()) == -2){
                        player.move(Direction.RIGHT); // якщо зіткнення немає, то рухаємо гравця вправо (тобто танк гравця)
                    }
                } // якщо клавіші вверх та враво не натиснуті (одночасно), то перевіряємо чи натиснуті стрілки вниз та вліво
                else if (gameInputListener.getKey(KeyEvent.VK_DOWN) && gameInputListener.getKey(KeyEvent.VK_LEFT)){
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вверх
                    if (game.isMovingObjectCollision(player, Direction.DOWN, player.getSpeed()) == -2){
                        player.move(Direction.DOWN); // якщо зіткнення немає, то рухаємо гравця вниз (тобто танк гравця)
                    } // якщо ж зіткненя в напрямку вниз є, то перевіряємо чи немає його в напрямку вліво
                    else if (game.isMovingObjectCollision(player, Direction.LEFT, player.getSpeed()) == -2){
                        player.move(Direction.LEFT); // якщо зіткнення немає, то рухаємо гравця вліво (тобто танк гравця)
                    }
                } // якщо клавіші вниз та вліво не натиснуті (одночасно), то перевіряємо чи натиснуті стрілки вниз та вправо
                else if (gameInputListener.getKey(KeyEvent.VK_DOWN) && gameInputListener.getKey(KeyEvent.VK_RIGHT)){
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вниз
                    if (game.isMovingObjectCollision(player, Direction.DOWN, player.getSpeed()) == -2){
                        player.move(Direction.DOWN); // якщо зіткнення немає, то рухаємо гравця вниз (тобто танк гравця)
                    } // якщо ж зіткненя в напрямку вниз є, то перевіряємо чи немає його в напрямку вправо
                    else if (game.isMovingObjectCollision(player, Direction.RIGHT, player.getSpeed()) == -2){
                        player.move(Direction.RIGHT); // якщо зіткнення немає, то рухаємо гравця вправо (тобто танк гравця)
                    }
                } // якщо клавіші вниз та вправо не натиснуті (одночасно), то перевіряємо чи натиснута клавіша вверх
                else if (gameInputListener.getKey(KeyEvent.VK_UP)) {
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вверх
                    if (game.isMovingObjectCollision(player, Direction.UP, player.getSpeed()) == -2)
                        player.move(Direction.UP); // якщо зіткненя немає рухаємо гравця вверх
                    player.setDirection(Direction.UP);

                } // якщо клавіша вверх не натиснута, то перевіряємо чи натиснута клавіша вниз
                else if (gameInputListener.getKey(KeyEvent.VK_DOWN)) {
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вниз
                    if (game.isMovingObjectCollision(player, Direction.DOWN, player.getSpeed()) == -2)
                        player.move(Direction.DOWN); // якщо зіткненя немає рухаємо гравця вниз
                    player.setDirection(Direction.DOWN);

                } // якщо клавіша вниз не натиснута, то перевіряємо чи натиснута клавіша вправо
                else if (gameInputListener.getKey(KeyEvent.VK_RIGHT)) {
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вправо
                    if (game.isMovingObjectCollision(player, Direction.RIGHT, player.getSpeed()) == -2)
                        player.move(Direction.RIGHT); // якщо зіткненя немає рухаємо гравця вправо
                    player.setDirection(Direction.RIGHT);

                } // якщо клавіша вправо не натиснута, то перевіряємо чи натиснута клавіша вліво
                else if (gameInputListener.getKey(KeyEvent.VK_LEFT)) {
                    // якщо умова вище виконується, то перевіряємо чи не має зіткненя гравця з іншими об'ктами в напрямку вліво
                    if (game.isMovingObjectCollision(player, Direction.LEFT, player.getSpeed()) == -2)
                        player.move(Direction.LEFT); // якщо зіткненя немає рухаємо гравця вліво
                    player.setDirection(Direction.LEFT);

                } // якщо клавіша вліво не натиснута, то зупиняємо гравця
                else {
                    player.stop(); // встановлюємо булевій змінній isMoving (це одне з полів об'єкта player) значення false
                }

                // перевіряємо чи натиснута клавіша SPACE
                if (gameInputListener.getKey(KeyEvent.VK_SPACE)) {
                    // якщо умова вище виконується, то гравець робить постріл, якщо це можливо (див. реалізацію метода shootIfPossible)
                    player.shoot();;
                }
                // перевіряємо чи натиснута клавіша ESC
                if (gameInputListener.getKey(KeyEvent.VK_ESCAPE)) {
                    // якщо умова вище виконується, то зупиняємо гру
                    gameInputListener.setKeyReleased(KeyEvent.VK_ESCAPE); // встановюємо кнопку ESC як відпущену (для того щоб не було зациклення натискання)
                    game.setSelectedButton(GameButton.RESUME); // встановлюємо кнопку RESUME, як обрану
                    game.pause(); // зупиняємо гру (зупиняються всі таймери та оброблення ігрової "логіки": зіткненя, рух і т.д.)
                    display.setState(DisplayStates.PAUSE); // змінюємо стан дисплею на PAUSE (тобто буде відображатись меню паузи)
                }


                Tank enemy; // оголошуємо об'єкт типу Tank (для того щоб на кожній ітерації циклу зберігати там ворожий танк, тобто об'єкт класу Tank)
                Laser laser; // оголошуємо об'єкт типу Laser (для того щоб на кожній ітерації циклу зберігати там ворожий лазер, тобто об'єкт класу Laser)
                for (int i = 0; i < game.getEnemiesCount(); i++) { // цикл в якому будемо робити перевірку ворожих танків на зіткненя з іншими об'єктами, а також виявленя танку гравця
                    enemy = game.getEnemyAt(i); // зберігаємо ворожий танк
                    for (int j = 0; j < enemy.getLasersCount(); j++) { // цикл в якому будемо робити перевірку ворожих лазерів на зіткненя з іншими об'єктами
                        laser = enemy.getLaser(j); // зберігаємо лазер
                        int id; // змінна в якому будемо записувати ідентифікаційний номер об'єкта з яким відбувається зіткнення (якщо id == -2 то зіткнення немає)
                        if ((id = game.isMovingObjectCollision(laser, laser.getDirection(), laser.getSpeed())) == -2) { // перевіряємо чи немає зіткненя ворожого лазеру в тому напрямку в якому він рухається
                            laser.move(laser.getDirection()); // якщо умова вище виконалась, рухаємо лазер, в тому напрямку в якому знаходився ворожий танк про пострілі
                        } // якщо було зіткненя, то:
                        else {
                            // якщо лазер зіштовхнувся з танком (якщо id == 0, то це танк гравця, якщо id > 0, то це ворожий танк)
                            if (id >= 0) game.damageTank(id, enemy.getDamage()); // завдаємо втрати енергії тому танку у який влучив лазер (чим більший id танку, тим більший розмір витрат які він може завдати, але
                                                                                // не рахуючи перші 10 ворожих танків, тому що вони мають однаковий розмір втрат, які вони можуть завдати)
                            enemy.removeLaser(j); // видаляємо лазер, що влучив в об'єкт
                        }
                    }

                    int id; // змінна в якій будемо зберігати ідентифікаційний номер об'єкта з яким зіштовнувся ворожий танк
                    if ((id = game.isMovingObjectCollision(enemy, enemy.getDirection(), enemy.getSpeed())) == -2) { // перевіряємо чи немає зіткненя ворожого танку в тому напрямку в якому він рухається
                        enemy.move(enemy.getDirection()); // якщо зіткненя немає, то ворожий танк продовжує рух в тому ж напрямку
                    } // якщо зіткненя є, перевіряємо чи це не танк гравця
                    else if (id != 0) enemy.setDirection(game.randDirection()); // якщо ворожий танк зіштовхнувся не з танком гравця, то змінюємо напрямок руху на інший випадковий
                    else if (id == 0) enemy.shoot(); // якщо ворожий танк зіштовхнувся з гравцем, то ворожий танк робить постріл якщо це можливо (див. реалізацію метода shootIfPossible)

                    if (player.getUpgrade().getType() != Upgrade.INVISIBILITY) { // перевіряємо чи гравець має покращення невидимості
                        if (enemyOnTankTileX(enemy, player)) { // якщо гравець невидимий, перевіряємо чи знаходяться ворожий танк та танк гравця в одному секторі по осі Х (сектор це квадрат 64*64 пікселя)
                            detectPlayer(directionToPlayerY(enemy, player), enemy, player); // якщо вони в одному секторі по осі Х, то ворожий танк спробує виявити танк гравця та вистрілити в нього,
                                                                                            // якщо на шляху до танку гравця немає перешкод (інших ворожих танків або блоків)
                        } else if (enemyOnTankTileY(enemy, player)) {// якщо умова вище не спрацювла, перевіряємо чи знаходяться ворожий танк та танк гравця в одному секторі по осі У
                            detectPlayer(directionToPlayerX(enemy, player), enemy, player); // якщо вони в одному секторі по осі Н, то ворожий танк спробує виявити танк гравця та вистрілити в нього,
                                                                                            // якщо на шляху до танку гравця немає перешкод (інших ворожих танків або блоків)
                        }
                    }
                }

                for (int i = 0; i < player.getLasersCount(); i++) { //
                    laser = player.getLaser(i);
                    int id;
                    if ((id = game.isMovingObjectCollision(laser, laser.getDirection(), laser.getSpeed())) == -2) {
                        laser.move(laser.getDirection());
                    } else {
                        if (id > 0) game.damageTank(id, player.getDamage());
                        player.removeLaser(i);
                    }
                }
                game.spawnEnemy();
                game.spawnUpgrade();
                game.playerTakesUpgrade();
                game.playerUpgradeEffect();
                player.charge();
                if (game.isGameOver()) {
                    game.pause();
                    Scores.addScore(game.getScoreInfo());
                    game.setSelectedButton(GameButton.RESTART);
                    display.setState(DisplayStates.GAME_OVER);
                }
                pauseOnHide();

                break;


            case PAUSE: // якщо стан дисплею PAUSE, то виконуємо код нижче
                if (gameInputListener.getKey(KeyEvent.VK_UP)) {
                    gameInputListener.setKeyReleased(KeyEvent.VK_UP);
                    game.switchUpPauseButton();
                } else if (gameInputListener.getKey(KeyEvent.VK_DOWN)) {
                    gameInputListener.setKeyReleased(KeyEvent.VK_DOWN);
                    game.switchDownPauseButton();
                } else if (gameInputListener.getKey(KeyEvent.VK_ENTER)) {
                    gameInputListener.setKeyReleased(KeyEvent.VK_ENTER);
                    switch (game.getSelectedButton()) {
                        case RESTART:
                            game = new Game();
                            display.setState(DisplayStates.GAME);
                            break;

                        case RESUME:
                            display.setState(DisplayStates.GAME);
                            drawnOnce = false;
                            game.resume();
                            break;

                        case EXIT:
                            close();
                            break;
                    }
                }
                if (game.getSelectedButton() == GameButton.SOUND) {
                    if (gameInputListener.getKey(KeyEvent.VK_LEFT)) {
                        gameInputListener.setKeyReleased(KeyEvent.VK_LEFT);
                        game.volumeDown();
                    } else if (gameInputListener.getKey(KeyEvent.VK_RIGHT)) {
                        gameInputListener.setKeyReleased(KeyEvent.VK_RIGHT);
                        game.volumeUp();
                    }
                }
                break;

            case GAME_OVER: // якщо стан дисплею GAME_OVER, то виконуємо код нижче
                if (gameInputListener.getKey(KeyEvent.VK_UP) || gameInputListener.getKey(KeyEvent.VK_DOWN)) {
                    gameInputListener.setKeyReleased(KeyEvent.VK_UP);
                    gameInputListener.setKeyReleased(KeyEvent.VK_DOWN);
                    game.switchGameOverButton();
                }
                else if (gameInputListener.getKey(KeyEvent.VK_ENTER)){
                    drawnOnce = false;
                    gameInputListener.setKeyReleased(KeyEvent.VK_ENTER);
                    switch (game.getSelectedButton()){
                        case RESTART:
                            game = new Game();
                            display.setState(DisplayStates.GAME);
                            break;

                        case BACK_TO_MENU:
                            display.setState(DisplayStates.MENU);
                            break;
                    }
                }

                break;

        }
    }

    public void render() {
        switch (display.getState()){
            case MENU:
                drawMenuBackground();
                drawPlayButton();
                drawScoresButton();
                drawExitButton();
                break;

            case GAME:
                Player player = game.player();
                drawFreeTiles();
                drawUpgrade();
                drawEnemiesAndTheirLasers();
                drawPlayer();
                drawPlayerLasers();
                if(!drawnOnce){
                    drawBorder();
                }
                drawBlocks();
                drawScore();
                drawPlayerInfo();
                if (player.getUpgrade().getType() == Upgrade.SHIELD)
                    drawShield();


                break;
            case PAUSE:
                sceneGraphics.setColor(new Color(0x290a1f));
                sceneGraphics.fillRect(6*64 + alignX,4*64 + alignY, 64*5, 80);
                sceneGraphics.setColor(new Color(0x7b1e5e));
                sceneGraphics.setFont(new Font("Impact", Font.BOLD, 50));
                sceneGraphics.drawString("Pause", 6*64 + 505, 4*64 + 81);
                sceneGraphics.setColor(new Color(0x002129));
                sceneGraphics.fillRect(6*64 + alignX,5*64 + alignY, 64*5, 400);
                sceneGraphics.setColor(new Color(0xffffff));
                sceneGraphics.drawRect(6*64 + alignX,4*64 + alignY, 64*5, 464);
                drawPauseButtons();
                break;

            case RECORDS:
                drawScoresBackground();
                drawScoresInfo();
                break;

            case GAME_OVER:

                sceneGraphics.setColor(new Color(0x0A1629)); // встановлюємо поточний колір, яким будемо малювати
                sceneGraphics.fillRect(0,0, WIDTH, HEIGHT);
                sceneGraphics.setColor(new Color(0x163256));
                sceneGraphics.setFont(new Font("Impact", Font.BOLD, 100));
                sceneGraphics.drawString("GAME OVER", WIDTH / 2 - 250, HEIGHT/3);
                sceneGraphics.setFont(new Font("Consolas", Font.BOLD, 50));
                sceneGraphics.drawString(game.getScoreInfo(), WIDTH / 2 - 250, HEIGHT/2);

                if (game.getSelectedButton() == GameButton.RESTART)
                    sceneGraphics.setColor(new Color(0x215E98));
                sceneGraphics.drawString("Restart", WIDTH / 2 - 250, HEIGHT - 370);
                sceneGraphics.setColor(new Color(0x163256));
                if (game.getSelectedButton() == GameButton.BACK_TO_MENU)
                    sceneGraphics.setColor(new Color(0x215E98));
                sceneGraphics.drawString("Back to menu", WIDTH / 2 - 250, HEIGHT - 300);
                sceneGraphics.setColor(new Color(0x163256));
                break;
        }

        // відображаємо ігрову сцену(зображення scene) на екрані (при цьому зображення маштабується відповідно до розмірів екрану)
        displayGraphics.drawImage(scene, 0,0, frame.getWidth(), frame.getHeight(),null);
    }

    private void addInputListener(){
        gameInputListener = new GameInputListener();
        frame.add(gameInputListener);
    }

    private void menuChoice(){
        if (gameInputListener.getKey(KeyEvent.VK_UP)){
            gameInputListener.setKeyReleased(KeyEvent.VK_UP);
            Menu.switchSelectedButtonUp();
        }
        else if (gameInputListener.getKey(KeyEvent.VK_DOWN)){
            gameInputListener.setKeyReleased(KeyEvent.VK_DOWN);
            Menu.switchSelectedButtonDown();
        }
        else if (gameInputListener.getKey(KeyEvent.VK_ENTER)){
            gameInputListener.setKeyReleased(KeyEvent.VK_ENTER);
            switch (Menu.getSelectedButton()){
                case PLAY:
                    game = new Game();
                    display.setState(DisplayStates.GAME);

                    break;
                case SCORES:
                    display.setState(DisplayStates.RECORDS);
                    break;
                case EXIT:
                    close();
                    break;
            }
        }
    }

    private void drawMenuBackground(){
        sceneGraphics.drawImage(Menu.getBackgroundImage(), 0,0, null);
    }

    private void drawPlayButton(){
        sceneGraphics.drawImage(Menu.getPlayButtonImage(), -20, 650, null);
    }

    private void drawScoresButton(){
        sceneGraphics.drawImage(Menu.getScoresButtonImage(), -63, 750, null);
    }

    private void drawExitButton(){
        sceneGraphics.drawImage(Menu.getExitButtonImage(), -58, 850, null);
    }

    private void drawFreeTiles(){
        Map map = game.map();
        sceneGraphics.setColor(new Color(0x00000000));
        for (int i = 0; i < Map.HEIGHT; i++){
            for (int j = 0; j < Map.WIDTH; j++){
                if(!map.tileAt(i, j).isBlock())
                    sceneGraphics.fillRect(j * SPRITE_SIZE + alignX, i * SPRITE_SIZE + alignY,
                            SPRITE_SIZE, SPRITE_SIZE);
            }
        }
    }

    private void drawEnemiesAndTheirLasers(){
        Laser laser;
        Tank enemy;
        for (int i = 0; i < game.getEnemiesCount(); i++){
            enemy = game.getEnemyAt(i);
            for (int j = 0; j < enemy.getLasersCount(); j++){
                laser = enemy.getLaser(j);
                sceneGraphics.drawImage(game.getLaserSprite(laser.getId(), laser.getDirection()),
                        laser.left() + alignX, laser.top() + alignY, null);
            }
            sceneGraphics.drawImage(game.getEnemySprite(enemy.getDirection(), enemy.isMoving()),
                    enemy.left() + alignX, enemy.top() + alignY, null);
        }
    }
    private void drawPlayer(){
        Player player = game.player();

        if (player.getUpgrade().getType() == Upgrade.INVISIBILITY){
            AlphaComposite transparency;
            BufferedImage playerSprite = new BufferedImage(64,64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = playerSprite.createGraphics();
            transparency = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2d.setComposite(transparency);
            g2d.drawImage(game.getPlayerSprite(), 0,0, null);
            sceneGraphics.drawImage(playerSprite,
                player.left() + alignX, player.top() + alignY, null);
        }
        else {
            sceneGraphics.drawImage(game.getPlayerSprite(),
                    player.left() + alignX, player.top() + alignY, null);
        }

    }

    private void drawPlayerLasers(){
        Player player = game.player();
        Laser laser;
        for (int i = 0; i < player.getLasersCount(); i++){
            laser = player.getLaser(i);
            sceneGraphics.drawImage(game.getLaserSprite(laser.getId(), laser.getDirection()),
                    laser.left() + alignX, laser.top() + alignY, null);
        }
    }

    private void drawBorder(){
        sceneGraphics.setColor(new Color(0x00000000));
        sceneGraphics.fillRect(alignX, alignY, 1088, 1024);
        sceneGraphics.drawImage(game.getBorderImage(), 0,0, null);
        drawnOnce = true;
    }

    private void drawBlocks(){
        Tile tile;
        Map map = game.map();
        Image img = game.getBlockSprite();
        for (int i = 0; i < map.getBlocksCount(); i++){
            tile = map.getBlock(i);
            sceneGraphics.drawImage(img,tile.left() + alignX, tile.top() + alignY, null);
        }
    }

    private void drawScoresBackground(){
        sceneGraphics.drawImage(Scores.getBackgroundImage(), 0,0, null);
    }

    private boolean enemyOnTankTileY(Tank enemy, Tank tank){
        return  enemy.getTileY() == tank.getTileY();
    }

    private boolean enemyOnTankTileX(Tank enemy, Tank tank){
        return enemy.getTileX() == tank.getTileX();
    }

    private Direction directionToPlayerX(Tank enemy, Player player){
        if (enemy.left() > player.left()){
            return Direction.LEFT;
        }

        return Direction.RIGHT;
    }

    private Direction directionToPlayerY(Tank enemy, Player player){
        if (enemy.top() > player.top()){
            return Direction.UP;
        }

        return Direction.DOWN;
    }

    private boolean noBlocksAtEnemyRIGHT(Tank enemy, Player player){
        Map map = game.map();
        for (int j = enemy.getTileX(); j < player.getTileX(); j++){
            if (map.tileAt(player.getTileY(), j).isBlock()) {
                return false;
            }
        }

        return true;
    }

    private boolean noBlocksAtEnemyLEFT(Tank enemy, Player player){
        Map map = game.map();
        for (int j = enemy.getTileX(); j > player.getTileX(); j--){
            if (map.tileAt(player.getTileY(), j).isBlock()) {
                return false;
            }
        }

        return true;
    }

    private boolean noBlocksAtEnemyUP(Tank enemy, Player player){
        Map map = game.map();
        for (int j = enemy.getTileY(); j > player.getTileY(); j--){
            if (map.tileAt(j, player.getTileX()).isBlock()) {
                return false;
            }
        }

        return true;
    }

    private boolean noBlocksAtEnemyDOWN(Tank enemy, Player player){
        Map map = game.map();
        for (int j = enemy.getTileY(); j < player.getTileY(); j++){
            if (map.tileAt(j, player.getTileX()).isBlock()) {
                return false;
            }
        }

        return true;
    }

    private boolean noEnemiesAtEnemyUP(Tank enemy){
        Tank anotherEnemy;
        Player player = game.player();
        for (int i = 0; i < game.getEnemiesCount(); i++){
            anotherEnemy = game.getEnemyAt(i);
            if (enemy.top() > anotherEnemy.top() && enemyOnTankTileX(enemy, anotherEnemy) &&
                    anotherEnemy.top() > player.top()){
                return false;
            }
        }

        return true;
    }

    private boolean noEnemiesAtEnemyDOWN(Tank enemy){
        Tank anotherEnemy;
        Player player = game.player();
        for (int i = 0; i < game.getEnemiesCount(); i++){
            anotherEnemy = game.getEnemyAt(i);
            if (enemy.top() < anotherEnemy.top() && enemyOnTankTileX(enemy, anotherEnemy) &&
                    anotherEnemy.top() < player.top()){
                return false;
            }
        }

        return true;
    }

    private boolean noEnemiesAtEnemyRIGHT(Tank enemy){
        Tank anotherEnemy;
        Player player = game.player();
        for (int i = 0; i < game.getEnemiesCount(); i++){
            anotherEnemy = game.getEnemyAt(i);
            if (enemy.left() < anotherEnemy.left() && enemyOnTankTileY(enemy, anotherEnemy) &&
                    anotherEnemy.left() < player.left()){
                return false;
            }
        }

        return true;
    }

    private boolean noEnemiesAtEnemyLEFT(Tank enemy){
        Tank anotherEnemy;
        Player player = game.player();
        for (int i = 0; i < game.getEnemiesCount(); i++){
            anotherEnemy = game.getEnemyAt(i);
            if (enemy.left() > anotherEnemy.left() && enemyOnTankTileY(enemy, anotherEnemy) &&
                    anotherEnemy.left() > player.left()){
                return false;
            }
        }

        return true;
    }

    private void detectPlayer(Direction direction, Tank enemy, Player player){
        switch (direction){
            case UP:
                if(noBlocksAtEnemyUP(enemy, player) && noEnemiesAtEnemyUP(enemy)){
                    enemy.setDirection(direction);
                    enemy.shoot();
                }
                break;
            case DOWN:
                if(noBlocksAtEnemyDOWN(enemy, player) && noEnemiesAtEnemyDOWN(enemy)){
                    enemy.setDirection(direction);
                    enemy.shoot();
                }
                break;
            case RIGHT:
                if(noBlocksAtEnemyRIGHT(enemy, player) && noEnemiesAtEnemyRIGHT(enemy)){
                    enemy.setDirection(direction);
                    enemy.shoot();
                }
                break;
            case LEFT:
                if(noBlocksAtEnemyLEFT(enemy, player) && noEnemiesAtEnemyLEFT(enemy)){
                    enemy.setDirection(direction);
                    enemy.shoot();
                }
                break;
        }

    }



    private void drawPlayerInfo(){
        Player player = game.player();
        sceneGraphics.setColor(new Color(0x002129));
        sceneGraphics.fillRect(30, 563, 340, 409);
        sceneGraphics.drawImage(game.getPlayerImage(), 97, 590, null);
        sceneGraphics.setColor(new Color(0x008189));
        sceneGraphics.setFont(new Font("System", Font.BOLD,32));
        sceneGraphics.drawString("Upgrade:", 35, 880);
        sceneGraphics.drawOval(182, 820, 75, 75);
        sceneGraphics.drawImage(game.getPlayerUpgradeImage(), 182, 820, null);
        sceneGraphics.drawRect(265, 840, 100, 40);
        sceneGraphics.drawString(String.format("%.2f", game.getEffectTime()), 285, 870);
        sceneGraphics.drawString("Energy:", 35, 935);
        sceneGraphics.drawRect(155, 916, player.getEnergyCapacity()/3 + 1, 21);
        sceneGraphics.fillRect(156, 917, player.getEnergyLevel()/3, 20);
    }

    private void drawUpgrade(){
        Upgrade u = game.getUpgrade();
        sceneGraphics.drawImage(game.getUpgradeSprite(), u.left()+417, u.top()+31, null);
    }

    private void drawPauseButtons(){
        sceneGraphics.setFont(new Font("Consolas", Font.BOLD, 50));
        sceneGraphics.setColor(new Color(0x008189));

        GameButton selected = game.getSelectedButton();
        if (selected == GameButton.RESTART)
            sceneGraphics.setColor(new Color(0x00F1F9));
        sceneGraphics.drawString("Restart", 8*64 + 350, 6*64 +31);
        sceneGraphics.setColor(new Color(0x008189));

        if (selected == GameButton.RESUME)
            sceneGraphics.setColor(new Color(0x00F1F9));
        sceneGraphics.drawString("Resume", 8*64 + 350, 7*64 +31);
        sceneGraphics.setColor(new Color(0x008189));

        if (selected == GameButton.SOUND)
            sceneGraphics.setColor(new Color(0x00F1F9));
        sceneGraphics.drawString("Sound", 8*64 + 350, 8*64 +31);
        sceneGraphics.fillRect(8*64 + 350, 9*64, Math.round(game.getVolume() * 205.0f), 30);
        sceneGraphics.drawRect(8*64 + 350, 9*64, 205, 30);
        sceneGraphics.setColor(new Color(0x008189));

        if (selected == GameButton.EXIT)
            sceneGraphics.setColor(new Color(0x00F1F9));
        sceneGraphics.drawString("Exit", 8*64 + 350, 10*64 +31);
        sceneGraphics.setColor(new Color(0x008189));

    }

    private void drawScoresInfo(){
        sceneGraphics.setColor(new Color(0x290a1f));
        sceneGraphics.fillRect(WIDTH / 5,HEIGHT / 5 - 200, WIDTH - (WIDTH / 5)*2, 140);
        sceneGraphics.setColor(new Color(0x7b1e5e));
        sceneGraphics.setFont(new Font("Impact", Font.BOLD, 100));
        sceneGraphics.drawString("Scores", WIDTH / 2 - 150, HEIGHT / 5 - 100);

        sceneGraphics.setColor(new Color(0x002129));
        int x = WIDTH / 5;
        int y = HEIGHT / 5 - 60;
        int w = WIDTH - x * 2;
        int h = 70*12;

        sceneGraphics.fillRect(x, y, w, h);
        sceneGraphics.setColor(new Color(0x008189));
        sceneGraphics.setFont(new Font("System", Font.BOLD, 30));
        x = WIDTH / 5 + 35;
        y = HEIGHT / 5 - 20;
        int size = Scores.getScoresCount();
        if (size > 54) size = 54;
        for (int i = 0; i < size; i++){
            if (i > 26){
                x = WIDTH / 5 + 615;
                y = HEIGHT / 5 - 20 - 27*30;
            }
            sceneGraphics.drawString((i+1)+ ". " + Scores.getScoreAt(i), x, y + i * 30);
        }
        x = WIDTH / 5 ;
        y = HEIGHT / 5 - 60;
        sceneGraphics.setColor(new Color(0xab1e5e));
        sceneGraphics.drawRect(WIDTH / 5,HEIGHT / 5 - 200, WIDTH - (WIDTH / 5)*2, 140);
        sceneGraphics.setColor(new Color(0x00F1F9));
        sceneGraphics.drawRect(x, y, w, h);
        sceneGraphics.drawLine(x + w/2, y, x + w/2, y + h);
    }

    private void drawScore(){
        sceneGraphics.setColor(new Color(0x002129));
        sceneGraphics.fillRect(30, 212, 340, 71);
        sceneGraphics.setColor(new Color(0x008189));
        sceneGraphics.setFont(new Font("System", Font.BOLD,61));
        sceneGraphics.drawString("" + game.getScore(), 40, 270);
    }

    private void drawShield(){
        Player player = game.player();
        sceneGraphics.drawImage(game.getShieldSprite(), player.left()+417, player.top()+31, null);
    }

    private void pauseOnHide(){
        if (!frame.isFocused()){
            game.pause();
            game.setSelectedButton(GameButton.RESTART);
            display.setState(DisplayStates.PAUSE);
        }
    }

    private void close(){
        Game.saveVolume();
        Scores.saveScores();
        frame.dispose();
        running = false;
    }
}
