package game

import com.badlogic.gdx.{Gdx, Game}

class GameMain extends Game {
  val width = 20;
  val height = 20;

  override def create() {
    //setScreen(new MainMenuScreen(this))
    setScreen(new GameScreen(this))
    //setScreen(new CreditsScreen(this))
    //setScreen(new StoryScreen(this))
  }

  override def render() {
    super.render()
  }

  override def dispose() {
  }
}
