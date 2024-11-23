package dev.undefinedteam.gensh1n.render;

public class ShaderMesh extends Mesh {
    private final Shader shader;

    public ShaderMesh(Shader shader, DrawMode drawMode, Attrib... attributes) {
        super(drawMode, attributes);
        this.shader = shader;
    }

    @Override
    protected void beforeRender() {
        shader.bind();
        shader.setDefaults();
    }

    @Override
    public void endRender() {
        super.endRender();
        shader.unbind();
    }
}
