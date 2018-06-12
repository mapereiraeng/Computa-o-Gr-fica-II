package MPereiraGame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author 090117
 */
public class OtoCamNode extends Node implements AnimEventListener {
    
    private final BetterCharacterControl physicsCharacter;
    private AnimChannel animationChannel;
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    
    public OtoCamNode (String name, AssetManager assetManager, 
                BulletAppState bulletAppState, float x, float y, float z){
        
        super(name);
        
        Node oto = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        oto.setLocalTranslation(0, 5, 0);
        setLocalTranslation(x, y, z);        
        attachChild(oto);
        
        physicsCharacter = new BetterCharacterControl(2.5f, 10f, 10f);
        addControl(physicsCharacter);
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
    }
    
    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }
    
    public void atualizaInimigos(float tpf){
        
        Vector3f camDir  = getWorldRotation().mult(Vector3f.UNIT_Z);
        viewDirection.set(camDir);
        physicsCharacter.setViewDirection(viewDirection);
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, 
                                                            String animName) { }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, 
                                                            String animName) { }
    
    public AnimChannel getAnimationChannel(){
        return animationChannel;
    }
    
    public void setAnimationChannel(AnimChannel animationChannel){
        this.animationChannel = animationChannel;
    }
}
