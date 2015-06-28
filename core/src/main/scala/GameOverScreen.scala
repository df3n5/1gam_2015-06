package game

import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.{Gdx, Screen}

class GameOverScreen (game: GameMain, score:Int) extends Screen {
  lazy val camera2D = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
  camera2D.update()
  lazy val batch = new SpriteBatch()
  lazy val font = new BitmapFont(Gdx.files.internal("test2.fnt"))
  //lazy val font = new BitmapFont()

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1)
    //Gdx.gl.glClearColor(1, 1, 1, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    batch.setProjectionMatrix(camera2D.combined)

    batch.begin()
    var x = -Gdx.graphics.getWidth()*0.2f
    var y = Gdx.graphics.getHeight()*0.1f
    font.draw(batch, s"Game over, you're score is $score", x, y)
    y -= 100
    font.draw(batch, "You probably could have used some instructions:", x, y)
    y -= 60
    font.draw(batch, "A to go left, D to go right.", x, y)
    y -= 200
    font.draw(batch, "Press space to retry", x, y)
    batch.end()
    if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
      game.setScreen(new GameScreen(game))
      dispose()
    }
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    font.dispose()
  }

  override def pause(): Unit = {}

  override def show(): Unit = {}

  override def resume(): Unit = {}
}
