package org.ken22.obstacles;

import org.ken22.utils.MathUtils;

public record Wall(double[] startPoint, double[] endPoint, double thickness) {
    public boolean isPointInWall(double x, double y) {
        // Find the thickness offset
        double wid = this.thickness();

        // Calculate unit normal to the wall
        double[] unitNormal = MathUtils.unitNormal2D(this.startPoint()[0], this.startPoint()[1], this.endPoint()[0], this.endPoint()[1]);
        double xn = unitNormal[0];
        double yn = unitNormal[1];
        double xnw = xn*wid;
        double ynw = yn*wid;

        // Calculate actual corners of the wall using the normal
        double smx = this.startPoint()[0] - xnw, smy = this.startPoint()[1] - ynw;
        double spx = this.startPoint()[0] + xnw, spy = this.startPoint()[1] + ynw;
        double epx = this.endPoint()[0] + xnw, epy = this.endPoint()[1] + ynw;
        double emx = this.endPoint()[0] - xnw, emy = this.endPoint()[1] - ynw;

        // Calculate the bounding box for quick test
        double xMax = Math.max(smx, Math.max(spx, Math.max(epx, emx)));
        double xMin = Math.min(smx, Math.min(spx, Math.min(epx, emx)));
        double yMax = Math.max(smy, Math.max(spy, Math.max(epy, emy)));
        double yMin = Math.min(smy, Math.min(spy, Math.min(epy, emy)));

        return x <= xMax && x >= xMin && y <= yMax && y >= yMin && // Quick test
            MathUtils.pointInQuadrilateral(x, y, smx, smy, spx, spy, epx, epy, emx, emy);
    }
}
