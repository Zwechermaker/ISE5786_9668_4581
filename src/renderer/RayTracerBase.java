package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

abstract class RayTracerBase {
    protected final Scene _scene;
    abstract Color traceRay(Ray ray);
    RayTracerBase(Scene scene) {
         this._scene = scene;
     }

}
