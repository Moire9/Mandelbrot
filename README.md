# A program to generate high-resolution images of the mandelbrot set

Currently does not support zooming. ETA for zoom: whenever I actually figure out how this code actually works.

## Building

`gradlew build` 

Output binary is `build/libs/Mandelbrot-<VERSION>-all.jar`

## Running

`java -jar Mandelbrot-<VERSION>-all.jar <width> <height> <iterations> [optimized mode=false] [filename=image.png]` 

Large image sizes may take several hours to render. I recommend enabling optimized mode, as long as your real coordinate=0, as it can halve the rendering time, but may increase memory usage by 25%.
