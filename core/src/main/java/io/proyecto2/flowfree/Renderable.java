package io.proyecto2.flowfree;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {

    void render(SpriteBatch batch);
    void dispose();
}
