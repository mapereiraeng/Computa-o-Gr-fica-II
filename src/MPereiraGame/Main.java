package MPereiraGame;

import com.jme3.animation.AnimChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author 090117
 */
public class Main extends SimpleApplication 
        implements ActionListener, 
        PhysicsCollisionListener{

    private static Main app = null;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.showSettings = false;
        app.start();
    }
    
    private BulletAppState bulletAppState;
    private JogadorCamNode jogador;
    private boolean pCima = false, esquerda = false, direita = false, ataque = false;
    private Material boxMatColosion;
    private List<OtoGetsSets> inimigos;
    private List<OtoCamNode> oto;
    private long startTime;
    Date afterAddingTenMins;
    private BitmapText infoPontos;
    private BitmapText infoTempo;
    private BitmapText infoCriador;
    private BitmapText fimDeJogo;
    private int pontos = 0;
    private long tempo;
    private boolean pause;
    private final long ts = 10 * 2000;
    private AnimChannel channel;

    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        inimigos = new ArrayList();
        oto = new ArrayList();
        
        startTime = System.currentTimeMillis();
        afterAddingTenMins = new Date(startTime + ts);

        criaLuz();
        criaCidade();
        
        boxMatColosion = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMatColosion.setBoolean("UseMaterialColors", true);
        boxMatColosion.setColor("Ambient", ColorRGBA.Red);
        boxMatColosion.setColor("Diffuse", ColorRGBA.Red);         
        
        criaJogador();
        criaTeclas();        
        criaInimigos();        
        Infos();

        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        for(Spatial r : rootNode.getChildren()){
            System.out.println(r.getName());
        }
    }
    
    private void criaLuz() {

        DirectionalLight l1 = new DirectionalLight();
        l1.setDirection(new Vector3f(1, -0.7f, 0));
        rootNode.addLight(l1);

        DirectionalLight l2 = new DirectionalLight();
        l2.setDirection(new Vector3f(-1, 0, 0));
        rootNode.addLight(l2);

        DirectionalLight l3 = new DirectionalLight();
        l3.setDirection(new Vector3f(0, 0, -1.0f));
        rootNode.addLight(l3);

        DirectionalLight l4 = new DirectionalLight();
        l4.setDirection(new Vector3f(0, 0, 1.0f));
        rootNode.addLight(l4);
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    }
    
    private void criaCidade() {
        
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");
        scene.setLocalTranslation(0, -5.2f, 0);
        scene.setName("city");
        rootNode.attachChild(scene);
        System.out.println("Cidade: "+scene.getName());

        Box boxMesh = new Box(100f,0.5f,100f); 
        Geometry boxGeo = new Geometry("Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);       
        boxGeo.setMaterial(boxMat); 
        boxGeo.setLocalTranslation(0, -5.2f, 0);
        
        RigidBodyControl boxPhysicsNode = new RigidBodyControl
        (CollisionShapeFactory.createMeshShape(boxGeo), 0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);
        
        RigidBodyControl cityPhysicsNode = new RigidBodyControl
        (CollisionShapeFactory.createMeshShape(scene), 0);
        scene.addControl(cityPhysicsNode);
        bulletAppState.getPhysicsSpace().add(cityPhysicsNode);
    }
    
    private void criaJogador() {

        jogador = new JogadorCamNode("Jogador", assetManager, bulletAppState, cam);
        rootNode.attachChild(jogador);
        flyCam.setEnabled(false);
    }

    private void criaTeclas() {
        
        inputManager.addMapping("Esquerda", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Direita", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("PraFrente", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Ataque", new KeyTrigger(KeyInput.KEY_SPACE));        
        inputManager.addMapping("Parar", new KeyTrigger(KeyInput.KEY_P));

        inputManager.addListener(this, "Esquerda", "Direita");
        inputManager.addListener(this, "PraFrente", "Ataque");
        inputManager.addListener(this, "Parar");
    }
    
    public void criaInimigos(){
        Random r = new Random();
        for(int i=0; i < 10; i++){
            criaInimigos(r.nextInt(32), 3, r.nextInt(32));
        }
    }
     
    @Override
    public void simpleUpdate(float tpf) {
        
        jogador.upDateKeys(tpf, pCima, esquerda, direita, ataque);
        
        for(OtoCamNode o : oto){
            o.atualizaInimigos(tpf);
        }
        
        infoCriador.setText("Marcela Pereira - RA: 090117");
        infoCriador.setColor(ColorRGBA.Green);
        
        infoPontos.setText("Pontos: " + pontos);    
        infoPontos.setColor(ColorRGBA.Green);
        
        infoTempo.setColor(ColorRGBA.Green);
        
        if (tempo()) {
            
            fimDeJogo.setText("Parabéns. "
                    + "Você matou "+ pontos +" Otos Monster!");
            
            fimDeJogo.setColor(ColorRGBA.White);
            guiNode.attachChild(fimDeJogo);
            pause = true;
            tempo = 0;
            bulletAppState.setEnabled(false);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {}
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
        if("Esquerda".equals(name)) {
            if (isPressed) {
                esquerda = true;
            } else {
                esquerda = false;
            }
        }
        if("Direita".equals(name)){
            if (isPressed) {
                direita = true;
            } else {
                direita = false;
            }
        }
        if("PraFrente".equals(name)){
            if (isPressed) {
                pCima = true;
            } else {
                pCima = false;
            }
        }
        if("Ataque".equals(name)){
            if (isPressed) {
                ataque = true;
            } else {
                ataque = false;
            }
        }
        
        if (name.equals("Parar") && isPressed) {
            pause = !pause;
            if(pause)
                bulletAppState.setEnabled(false);
            else if(!pause)
                bulletAppState.setEnabled(true);
        }
    }
       
    private void criaInimigos(float x, float y, float z) {
        
        OtoGetsSets nObj = new OtoGetsSets();
        OtoCamNode otoInimigo = new OtoCamNode("oto", assetManager, bulletAppState, x*4, y, z);
        nObj.setOto(otoInimigo);
        nObj.setChannel(otoInimigo.getAnimationChannel());
        inimigos.add(nObj);
        oto.add(otoInimigo);
        rootNode.attachChild(otoInimigo);
    }
    
    private void Infos() {

        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        infoCriador = new BitmapText(guiFont, false);
        infoCriador.setSize(guiFont.getCharSet().getRenderedSize());
        infoCriador.setLocalTranslation(0, settings.getHeight() - 10, 0);
        guiNode.attachChild(infoCriador);
        
        infoPontos = new BitmapText(guiFont, false);
        infoPontos.setSize(guiFont.getCharSet().getRenderedSize());
        infoPontos.setLocalTranslation(0, settings.getHeight() - 40, 0);
        guiNode.attachChild(infoPontos);

        infoTempo = new BitmapText(guiFont, false);
        infoTempo.setSize(guiFont.getCharSet().getRenderedSize());
        infoTempo.setLocalTranslation(0, settings.getHeight() - 60, 0);
        guiNode.attachChild(infoTempo);
        
        fimDeJogo = new BitmapText(guiFont, false);
        fimDeJogo.setSize(guiFont.getCharSet().getRenderedSize());

        fimDeJogo.setLocalTranslation((settings.getWidth() / 2) - 
                                      (guiFont.getCharSet().getRenderedSize()
                                    * (fimDeJogo.getText().length() / 3)),
                                       settings.getHeight() / 2 + 
                                       fimDeJogo.getLineHeight() / 2 - 100, 0);

    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {

        if(event.getNodeA().getName().equals("Jogador") || 
                event.getNodeB().getName().equals("Jogador")){
        
            if(event.getNodeA().getName().equals("oto")){
                Spatial s = event.getNodeA();             
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().removeAll(s);
                oto.remove(s);
                pontos++;
            }
            else if(event.getNodeB().getName().equals("oto")){
                Spatial s = event.getNodeB();
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().removeAll(s);
                oto.remove(s);
                pontos++;
            }           
        }        
    }
        
    public boolean tempo() {
        
        tempo = System.currentTimeMillis();
        
        long differenceTime = afterAddingTenMins.getTime() - tempo;
        
        if (differenceTime >= 0) {
            infoTempo.setText("Tempo restante: " + 
                    TimeUnit.MILLISECONDS.toSeconds(differenceTime) + " s");
        }

        if (TimeUnit.MILLISECONDS.toSeconds(differenceTime) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
