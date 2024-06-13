package org.ken22.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public enum ViewportType {
    FIT {

        @Override
        public Viewport getViewport() {
            throw new UnsupportedOperationException("Fit viewport not working from this enum yet");
        }
    },
    STRETCH {

        @Override
        public Viewport getViewport() {
            throw new UnsupportedOperationException("Stretch viewport not working from this enum yet");
        }
    },
    SCREEN {

        @Override
        public Viewport getViewport() {
            var viewport = new ScreenViewport();
            // just a value that was found to look good enough
            viewport.setUnitsPerPixel(0.5f/ Gdx.graphics.getDensity());

            return viewport;
        }
    };

    public abstract Viewport getViewport();
}
