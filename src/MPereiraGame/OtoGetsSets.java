package MPereiraGame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.scene.Node;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author 090117
 */
public class OtoGetsSets {
    
    private Node oto;
    private AnimChannel channel;
    private AnimControl control;
    
    public Node getOto(){
        return oto;
    }
    
    public void setOto(Node oto){
        this.oto = oto;
    }
    
    public AnimChannel getChannel(){
        return channel;
    }
    
    public void setChannel(AnimChannel channel){
        this.channel = channel;
    }
    
    public AnimControl getControl(){
        return control;
    }
    
    public void setControl(AnimControl control){
        this.control = control;
    }
}
