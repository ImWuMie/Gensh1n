package dev.undefinedteam.gensh1n.render;

public class GLState {
    public boolean depthSaved, blendSaved, cullSaved, scissorSaved;
    public GLState child;
    public GLState parent;
}
