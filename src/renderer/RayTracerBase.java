package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

class  RayTracerBase {
    protected  final Scene _scene;
    final abstract Color traceRay(Ray ray);
     RayTracerBase(Scene scene) {
         this._scene = scene;
     }

}
