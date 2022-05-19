package com.example.projetmobile.Model.Mouvement;

import com.example.projetmobile.Model.GameObject;

//TO perform specila action on displacement
public interface Action<T extends GameObject> {

    //static ComposedDrawing graphicElement  = new ComposedDrawing();

    enum ActionState{
        VALID,INVALID,STILLGOOD
    }

    /**Indicate wathever if an action is valid or not
     * @param t : a gameobject, util for perform validation of an action**/
    ActionState isValidated(T t);

}
