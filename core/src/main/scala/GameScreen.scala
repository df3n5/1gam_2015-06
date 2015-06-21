package game

import scala.collection.mutable.ArrayBuffer

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Texture, GL20, PerspectiveCamera}
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController
import com.badlogic.gdx.math.Vector3 

class GameScreen (game: GameMain) extends Screen {
  //Config
  val ColorStretchLength = 10.0f
  val Speed = 1.0f
  val DebugCamera = false

  lazy val camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  camera.position.set(10f, 5f, 0f)
  camera.lookAt(0, 0, 0)
  camera.near = 1f
  camera.far = 300f
  camera.update()
  lazy val playerPos = new Vector3(0, 0, 0)
  //lazy val grannyNiceImage = new Texture(Gdx.files.internal("granny_happy.png"))
  lazy val modelBuilder = new ModelBuilder()

  lazy val playerModel = modelBuilder.createBox(5f, 5f, 5f, 
      new Material(ColorAttribute.createDiffuse(Color.GREEN)),
      Usage.Position | Usage.Normal)
  lazy val playerModelInstance = new ModelInstance(playerModel)

  lazy val groundModels = ArrayBuffer[ModelInstance]();
  lazy val blueGroundModel = modelBuilder.createBox(ColorStretchLength, 0.1f, 50f, 
      new Material(ColorAttribute.createDiffuse(Color.BLUE)),
      Usage.Position | Usage.Normal)

  lazy val redGroundModel = modelBuilder.createBox(ColorStretchLength, 0.1f, 50f, 
      new Material(ColorAttribute.createDiffuse(Color.RED)),
      Usage.Position | Usage.Normal)
  for (i <- 0 to 200 if i % 2 == 0) {
    //println(s"i is $i ${-ColorStretchLength * i}")
    lazy val blueGroundModelInstance = new ModelInstance(blueGroundModel, new Vector3(-ColorStretchLength * i, -2.6f, 0))
    lazy val redGroundModelInstance = new ModelInstance(redGroundModel, new Vector3(-ColorStretchLength * (i+1), -2.6f, 0))
    //blueGroundModelInstance.transform.translate(-ColorStretchLength * i, 0f, 0f);
    //redGroundModelInstance.transform.translate(-ColorStretchLength * (i+1), 0f, 0f);
    groundModels += blueGroundModelInstance;
    groundModels += redGroundModelInstance;
  }
  //lazy val groundModel = modelBuilder.createLineGrid(10, 10, 1000.0f, 1000.0f, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal)

  lazy val modelBatch = new ModelBatch()
  lazy val environment = new Environment()
  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
  environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

  //lazy val camController = new CameraInputController(camera)
  //lazy val camController = new FirstPersonCameraController(camera)
  lazy val camController = new FirstPersonCameraController(camera)
  if(DebugCamera) {
    Gdx.input.setInputProcessor(camController)
  }

  override def render(delta: Float): Unit = {
    //camera.position.set(playerPosition)

    playerModelInstance.transform.translate(-Speed, 0, 0)
    
    if(DebugCamera) {
      camController.update();
    } else {
      camera.translate(-Speed, 0, 0)
      camera.update()
    }

    //camera.update()
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    modelBatch.begin(camera);
    modelBatch.render(playerModelInstance, environment);
    groundModels.foreach { case model =>
      modelBatch.render(model, environment);
    }
    modelBatch.end();
  }

  def dispose(): Unit = {
    modelBatch.dispose();
    playerModel.dispose();
  }
  def hide(): Unit = {}
  def pause(): Unit = {}
  def resize(x1: Int,x2: Int): Unit = {}
  def resume(): Unit = {}
  def show(): Unit = {}
}
