package game

import scala.collection.mutable.ArrayBuffer

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Texture, GL20, PerspectiveCamera, OrthographicCamera}
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
import com.badlogic.gdx.math.Matrix4 
import com.badlogic.gdx.assets.AssetManager 
import com.badlogic.gdx.graphics.g3d.Model 
import com.badlogic.gdx.utils.Timer.Task
import com.badlogic.gdx.utils.{Timer, TimeUtils}

class GameScreen (game: GameMain) extends Screen {
  // config
  val ColorStretchLength = 100.0f
  val ItemXDistance = 600.0f
  val PointXDistance = 100.0f
  var Speed = 1.0f
  val DebugCamera = false
  val MovementAmount = 10.0f
  val CollisionAllowanceZ = 2.0f
  val CollisionAllowanceX = 5.0f
  val CollisionAllowanceFastZ = 3.0f
  val CollisionAllowanceFastX = 50.0f
  val StartDistance = 500.0f
  val PointsPerSphere = 100
  val TotalTime = 31.0f
  //val TotalTime = 1.0f
  val FOV = 90.0f

  // game state
  var points = 0
  var playerZ = 0.0f
  var warp9 = false
  var timeLeft = TotalTime

  lazy val camera = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
  val w = Gdx.graphics.getWidth
  val h = Gdx.graphics.getHeight
  println(s"width : $w, height is $h")

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

  lazy val groundModels = ArrayBuffer[ModelInstance]()
  lazy val blueGroundModel = modelBuilder.createBox(ColorStretchLength, 0.1f, 50f, 
      new Material(ColorAttribute.createDiffuse(Color.BLUE)),
      Usage.Position | Usage.Normal)

  lazy val redGroundModel = modelBuilder.createBox(ColorStretchLength, 0.1f, 50f, 
      new Material(ColorAttribute.createDiffuse(Color.RED)),
      Usage.Position | Usage.Normal)
  for (i <- 0 to 200 if i % 2 == 0) {
    lazy val blueGroundModelInstance = new ModelInstance(blueGroundModel, new Vector3(-ColorStretchLength * i, -2.6f, 0))
    lazy val redGroundModelInstance = new ModelInstance(redGroundModel, new Vector3(-ColorStretchLength * (i+1), -2.6f, 0))
    groundModels += blueGroundModelInstance
    groundModels += redGroundModelInstance
  }
  var itemModelIns = ArrayBuffer[ModelInstance]()
  var pointModelIns = ArrayBuffer[ModelInstance]()

  lazy val modelBatch = new ModelBatch()
  lazy val environment = new Environment()
  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
  environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

  val assets = new AssetManager()
  assets.load("item.g3db", classOf[Model])
  var loading = true
  var itemInstance : ModelInstance = null

  lazy val camController = new FirstPersonCameraController(camera)
  if(DebugCamera) {
    Gdx.input.setInputProcessor(camController)
  }
  Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1)

  lazy val batch = new SpriteBatch()
  lazy val font = new BitmapFont(Gdx.files.internal("test2.fnt"))
  //lazy val generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myfont.ttf"))
  //lazy val font = generator.generateFont(12); // font size 12 pixels
  //BitmapFont font25 = generator.generateFont(25); // font size 25 pixels
  //generator.dispose(); // don't forget to dispose to avoid memory leaks!

  lazy val camera2D = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  camera2D.update()
  println("Init")

  lazy val bgMusic = Gdx.audio.newMusic(Gdx.files.internal("mainloop.ogg"))

  def doneLoading() : Unit = {
    println("Finished loading")
    bgMusic.setLooping(true)
    bgMusic.play
    val model = assets.get("item.g3db", classOf[Model])
    //itemInstance = new ModelInstance(model, "item")
    for (i <- 0 to 100) {
      itemInstance = new ModelInstance(model, "item")
      if((i % 2) == 0 ) {
        itemInstance.transform.setToTranslation((-ItemXDistance * i) + StartDistance, 0, 9.0f)
      }
      if((i % 2) == 1) {
        itemInstance.transform.setToTranslation((-ItemXDistance * i) + StartDistance, 0, -11.0f)
      }
      //itemInstance.transform.setToTranslation(-ItemXDistance * i, 0, -1.0f)
      //itemInstance.transform.setToTranslation(-ItemXDistance * i, 0, -11.0f)
      itemModelIns += itemInstance
    }

    for (i <- 0 to 1000) {
      itemInstance = new ModelInstance(model, "item")
      //itemInstance.transform.setToTranslation(-ItemXDistance * i, 0, -1.0f)
      //itemInstance.transform.setToTranslation(-ItemXDistance * i, 0, -11.0f)
      itemInstance.transform.setToTranslation((-PointXDistance * i) + StartDistance, 0, -1.0f)
      itemInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN))
      pointModelIns += itemInstance
    }
    loading = false
  }

  override def render(delta: Float): Unit = {
    if (loading) { 
      if(assets.update()) doneLoading()
    } else {
      timeLeft -= delta
      if(timeLeft < 0.0f) {
          bgMusic.stop
          game.setScreen(new GameOverScreen(game, points))
      }

      if(Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A)) {
        playerZ  = -MovementAmount
      } else if(Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
        playerZ  = MovementAmount
      } else if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
        playerZ  = 0.0f
      }

      playerModelInstance.transform.translate(-Speed, 0, 0)
      
      if(DebugCamera) {
        camController.update()
      } else {
        camera.translate(-Speed, 0, 0)
        camera.position.z = playerZ
        playerModelInstance.transform.`val`(Matrix4.M23) = playerZ
        camera.update()
      }
      collisionDetectAndResolve

      //camera.update()
      Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

      // Model Rendering
      modelBatch.begin(camera)
      itemModelIns.foreach { case model =>
        modelBatch.render(model, environment)
      }
      pointModelIns.foreach { case model =>
        modelBatch.render(model, environment)
      }

      modelBatch.render(playerModelInstance, environment)
      groundModels.foreach { case model =>
        modelBatch.render(model, environment)
      }
      modelBatch.end()

      batch.setProjectionMatrix(camera2D.combined);
      batch.begin()
      val x1 = -Gdx.graphics.getWidth()*0.05f
      val x2 = -Gdx.graphics.getWidth()*0.086f
      font.draw(batch, s"Score : $points", x1, Gdx.graphics.getHeight()*0.45f)
      val intTime :Int = timeLeft.toInt
      font.draw(batch, s"Time left : ${intTime}", x2, Gdx.graphics.getHeight()*0.35f)
      batch.end()
    }
  }

  def collisionDetectAndResolve(): Unit = {
      val newItemModelIns = ArrayBuffer[ModelInstance]()

      itemModelIns.foreach { case model =>
        //println(s"model.transform ${model.transform}")
        val playerZ = playerModelInstance.transform.`val`(Matrix4.M23)
        val itemModelZ = model.transform.`val`(Matrix4.M23)
        val zDelta = Math.abs(playerZ - itemModelZ)

        val playerX = playerModelInstance.transform.`val`(Matrix4.M03)
        val itemModelX = model.transform.`val`(Matrix4.M03)
        val xDelta = Math.abs(playerX - itemModelX)
        val collisionAllowanceZ = warp9 match {
          case true => CollisionAllowanceFastZ
          case false => CollisionAllowanceZ
        }
        val collisionAllowanceX = warp9 match {
          case true => CollisionAllowanceFastX
          case false => CollisionAllowanceX
        }
        if((zDelta < collisionAllowanceZ) && (xDelta < collisionAllowanceX)) {
          println(s"Collision ${xDelta} ${zDelta}")
          warpNineEngage
          Timer.instance.clear
          Timer.schedule(new Task {
            override def run(): Unit = { 
              warpSixEngage
            }
          }, 2.0f)

        } else {
          newItemModelIns += model
        }
      }
      itemModelIns = newItemModelIns

      // Resolve points now
      val newPointModelIns = ArrayBuffer[ModelInstance]()
      pointModelIns.foreach { case model =>
        //println(s"model.transform ${model.transform}")
        val playerZ = playerModelInstance.transform.`val`(Matrix4.M23)
        val pointModelZ = model.transform.`val`(Matrix4.M23)
        val zDelta = Math.abs(playerZ - pointModelZ)

        val playerX = playerModelInstance.transform.`val`(Matrix4.M03)
        val pointModelX = model.transform.`val`(Matrix4.M03)
        val xDelta = Math.abs(playerX - pointModelX)
        val collisionAllowanceZ = warp9 match {
          case true => CollisionAllowanceFastZ
          case false => CollisionAllowanceZ
        }
        val collisionAllowanceX = warp9 match {
          case true => CollisionAllowanceFastX
          case false => CollisionAllowanceX
        }
        if((zDelta < collisionAllowanceZ) && (xDelta < collisionAllowanceX)) {
          points += PointsPerSphere
          println(s"Collision with points , now have ${points}")
        } else {
          newPointModelIns += model
        }
      }
      pointModelIns = newPointModelIns
  }

  def warpSixEngage(): Unit = {
    Speed = 1.0f
    warp9 = false
    playerModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN))
  }

  def warpNineEngage(): Unit = {
    Speed = 10.0f
    warp9 = true
    playerModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.ORANGE))
  }

  def dispose(): Unit = {
    bgMusic.stop
    bgMusic.dispose
    modelBatch.dispose
    playerModel.dispose
  }
  def hide(): Unit = {}
  def pause(): Unit = {}
  def resize(x1: Int,x2: Int): Unit = {}
  def resume(): Unit = {}
  def show(): Unit = {}
}
