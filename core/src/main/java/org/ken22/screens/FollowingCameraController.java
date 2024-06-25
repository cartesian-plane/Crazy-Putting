package org.ken22.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class FollowingCameraController implements InputProcessor {

    private Camera camera;
    private float previousX, previousY;
    private float rotationSpeed = 0.5f;

    public FollowingCameraController(Camera camera) {
        this.camera = camera;
    }

    // ... other methods ...

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.previousX = screenX;
        this.previousY = screenY;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = previousX - screenX;
        float deltaY = previousY - screenY;

        Vector3 direction = camera.direction.cpy();
        Vector3 up = camera.up.cpy();

        camera.direction.rotate(up, deltaX * rotationSpeed);
        camera.direction.rotate(direction.crs(up), deltaY * rotationSpeed);

        previousX = screenX;
        previousY = screenY;

        camera.update();

        return true;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
