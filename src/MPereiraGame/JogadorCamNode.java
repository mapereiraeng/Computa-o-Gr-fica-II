package MPereiraGame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author 090117
 */
public class JogadorCamNode extends Node{
    
    private final BetterCharacterControl physicsCharacter;
    private final AnimControl animationControl;
    private final AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    private float airTime;

    public JogadorCamNode(String name,AssetManager assetManager, 
                                    BulletAppState bulletAppState, Camera cam) {
        
        super(name);
        
        Node ninja=(Node)assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.03f, 0.03f, 0.03f);
        ninja.rotate(0.0f, -3.2f, 0.0f); 
        setLocalTranslation(0, 1, 0);
        attachChild(ninja);
       
        physicsCharacter = new BetterCharacterControl(2.3f, 4.6f, 10f);
        addControl(physicsCharacter);
                
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        animationControl = ninja.getControl(AnimControl.class);
        animationChannel = animationControl.createChannel();  
        
        CameraNode camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(1, 12,-30));
        camNode.lookAt(this.getLocalTranslation(), Vector3f.UNIT_Y);
        
        this.attachChild(camNode);
    }
    
    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }
    
    void upDateAnimationPlayer() {
   
        if (walkDirection.length() == 0) {
            if (!"stand".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("stand", 1f);
            }
        } else {
            if (airTime > .3f) {
                if (!"stand".equals(animationChannel.getAnimationName())) {
                    animationChannel.setAnim("stand");
                }
            } else if (!"Walk".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Walk", 0.7f);
            }
        }
    }

    void upDateKeys(float tpf, boolean up, boolean left, boolean right, 
                                                                boolean space) {
        
        Vector3f camDir  = getWorldRotation().mult(Vector3f.UNIT_Z);
       
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
            
        walkDirection.addLocal(camDir.mult(3));
        
        if (up) {
            walkDirection.addLocal(camDir.mult(6));
        } 
        if (left) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * 
                                                          tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        }        
        if (right) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * 
                                                          tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        if(space){
            animationChannel.setAnim("Attack3");
            animationChannel.setLoopMode(LoopMode.DontLoop); 
        }
        
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
 
        upDateAnimationPlayer();
    }
}
