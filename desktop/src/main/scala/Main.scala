package game

import com.badlogic.gdx.backends.lwjgl._

object Main extends App {
    val cfg = new LwjglApplicationConfiguration
    cfg.title = "1GAM 2015-06"
    cfg.width = 0
    cfg.height = 0 
    cfg.forceExit = false
    new LwjglApplication(new GameMain, cfg)
}
